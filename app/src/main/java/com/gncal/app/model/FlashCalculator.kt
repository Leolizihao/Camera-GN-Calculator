package com.gncal.app.model

import java.util.Locale
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 闪光灯曝光计算核心。
 *
 * 基本公式（ISO 100 基准，距离单位与 GN 单位一致）：
 *   GN = 光圈 × 距离
 *   GN(ISO) = GN₁₀₀ × √(ISO / 100)
 * 功率与闪光补偿按「光量」折算：GN × √(2^EV)，因为 1 EV = 2 倍光量。
 */

enum class SolveMode { APERTURE, DISTANCE, GUIDE_NUMBER, ISO }

enum class DistanceUnit(val metersPerUnit: Double, val symbol: String) {
    METER(1.0, "m"),
    FEET(0.3048, "ft");

    fun fromMeters(value: Double): Double = value / metersPerUnit
    fun toMeters(value: Double): Double = value * metersPerUnit
}

object PhotoScales {
    /** 1/3 档标准光圈序列 */
    val APERTURES: List<Double> = listOf(
        1.0, 1.1, 1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.5, 2.8, 3.2, 3.5,
        4.0, 4.5, 5.0, 5.6, 6.3, 7.1, 8.0, 9.0, 10.0, 11.0, 13.0, 14.0,
        16.0, 18.0, 20.0, 22.0, 25.0, 29.0, 32.0
    )

    /** 1/3 档标准感光度序列 */
    val ISOS: List<Int> = listOf(
        25, 32, 40, 50, 64, 80, 100, 125, 160, 200, 250, 320, 400, 500, 640,
        800, 1000, 1250, 1600, 2000, 2500, 3200, 4000, 5000, 6400, 8000,
        10000, 12800, 16000, 20000, 25600, 32000, 40000, 51200, 64000,
        80000, 102400
    )

    /** 闪光输出功率的分母：1/1、1/2、1/4 … 1/128 */
    val POWER_DENOMINATORS: List<Int> = listOf(1, 2, 4, 8, 16, 32, 64, 128)

    /** 闪光曝光补偿：-3EV … +3EV，步长 1/3 EV */
    val COMPENSATION_EV: List<Double> = (-9..9).map { it / 3.0 }

    val DEFAULT_APERTURE_INDEX: Int = APERTURES.indexOf(5.6)
    val DEFAULT_ISO_INDEX: Int = ISOS.indexOf(400)
    val DEFAULT_COMP_INDEX: Int = COMPENSATION_EV.indexOf(0.0)

    fun nearestAperture(value: Double): Double =
        APERTURES.minByOrNull { abs(2.0 * log2(value / it)) } ?: value

    fun nearestIsoAtLeast(value: Double): Int {
        val exact = ISOS.minByOrNull { abs(log2(it.toDouble() / value)) } ?: 100
        // 感光度低于需求会欠曝，因此取不小于需求值的档位
        return if (exact.toDouble() >= value) exact else (ISOS.firstOrNull { it >= value } ?: ISOS.last())
    }

    fun nearestIso(value: Double): Int =
        ISOS.minByOrNull { abs(log2(it.toDouble() / value)) } ?: 100
}

/** 计算所需的全部输入（滑杆统一使用序列下标，保证只能取到标准档位） */
data class FlashInputs(
    val mode: SolveMode = SolveMode.APERTURE,
    val gnIso100: Double = 36.0,
    val apertureIndex: Int = PhotoScales.DEFAULT_APERTURE_INDEX,
    val distance: Double = 3.0,
    val isoIndex: Int = PhotoScales.DEFAULT_ISO_INDEX,
    val powerIndex: Int = 0,
    val compensationIndex: Int = PhotoScales.DEFAULT_COMP_INDEX,
    val unit: DistanceUnit = DistanceUnit.METER
) {
    val aperture: Double get() = PhotoScales.APERTURES[apertureIndex.coerceIn(PhotoScales.APERTURES.indices)]
    val iso: Int get() = PhotoScales.ISOS[isoIndex.coerceIn(PhotoScales.ISOS.indices)]

    /** 输出功率折算成 EV（1/2 功率 = -1 EV） */
    val powerEv: Double
        get() = -log2(PhotoScales.POWER_DENOMINATORS[powerIndex.coerceIn(PhotoScales.POWER_DENOMINATORS.indices)].toDouble())

    val compensationEv: Double
        get() = PhotoScales.COMPENSATION_EV[compensationIndex.coerceIn(PhotoScales.COMPENSATION_EV.indices)]
}

sealed interface Advice {
    data object InvalidInput : Advice
    data object SyncSpeedReminder : Advice
    data class NearestAperture(val standard: Double, val deltaEv: Double) : Advice
    data class DistanceTooNear(val threshold: Double) : Advice
    data class DistanceTooFar(val threshold: Double) : Advice
    data object ApertureOutOfRange : Advice
    data class IsoTooHigh(val iso: Int) : Advice
    data class IsoTooLow(val iso: Int) : Advice
    data class GnTooLarge(val threshold: Int) : Advice
    data class GnTooSmall(val threshold: Int) : Advice
    data class EffectiveGn(val gn: Double) : Advice
}

data class ReferenceRow(val aperture: Double, val distance: Double)

data class CalcResult(
    val mode: SolveMode,
    val isValid: Boolean,
    /** 主结果，已格式化（如 "f/8"、"4.5"、"36"、"800"） */
    val primary: String,
    val unitSuffix: String,
    /** 补充说明，如理论值与推荐档位的差异 */
    val detail: String?,
    val effectiveGn: Double?,
    val advices: List<Advice>,
    val rows: List<ReferenceRow>
) {
    companion object {
        fun invalid(mode: SolveMode) = CalcResult(
            mode = mode,
            isValid = false,
            primary = "—",
            unitSuffix = "",
            detail = null,
            effectiveGn = null,
            advices = listOf(Advice.InvalidInput),
            rows = emptyList()
        )
    }
}

object FlashCalculator {

    private const val MIN_DISTANCE_METERS = 0.5
    private const val FASTEST_USABLE_APERTURE = 1.4
    private const val ISO_HIGH_THRESHOLD = 6400
    private const val ISO_LOW_THRESHOLD = 50
    private const val GN_LARGE_THRESHOLD = 60
    private const val GN_SMALL_THRESHOLD = 12

    /** 有效 GN：已计入 ISO、输出功率与闪光补偿，单位与输入距离一致 */
    fun effectiveGn(inputs: FlashInputs): Double {
        val isoScale = sqrt(inputs.iso / 100.0)
        val powerScale = 2.0.pow(inputs.powerEv / 2.0)
        val compensationScale = 2.0.pow(inputs.compensationEv / 2.0)
        return inputs.gnIso100 * isoScale * powerScale * compensationScale
    }

    /** ISO / 功率 / 补偿 的综合折算系数，用于反解 GN 与 ISO */
    private fun gainFactor(inputs: FlashInputs): Double =
        sqrt(inputs.iso / 100.0) * 2.0.pow(inputs.powerEv / 2.0) * 2.0.pow(inputs.compensationEv / 2.0)

    fun solve(inputs: FlashInputs): CalcResult {
        val advices = mutableListOf<Advice>()
        val unit = inputs.unit

        val distanceValid = inputs.distance > 0.0
        val gnValid = inputs.gnIso100 > 0.0

        val effGn: Double? = when (inputs.mode) {
            SolveMode.APERTURE, SolveMode.DISTANCE -> if (gnValid) effectiveGn(inputs) else null
            SolveMode.GUIDE_NUMBER, SolveMode.ISO ->
                if (distanceValid) inputs.aperture * inputs.distance else null
        }

        if ((inputs.mode == SolveMode.APERTURE && !distanceValid) ||
            (inputs.mode == SolveMode.DISTANCE && !gnValid) ||
            (inputs.mode == SolveMode.GUIDE_NUMBER && !distanceValid) ||
            (inputs.mode == SolveMode.ISO && (!distanceValid || !gnValid))
        ) {
            return CalcResult.invalid(inputs.mode)
        }

        var primary = "—"
        var unitSuffix = ""
        var detail: String? = null

        when (inputs.mode) {
            SolveMode.APERTURE -> {
                val f = (effGn ?: 0.0) / inputs.distance
                primary = "f/${formatAperture(f)}"
                val standard = PhotoScales.nearestAperture(f)
                val deltaEv = 2.0 * log2(f / standard)
                advices += Advice.NearestAperture(standard, deltaEv)
                if (f < 1.0 || f > 32.0) advices += Advice.ApertureOutOfRange
                checkDistance(advices, inputs.distance, effGn, unit)
            }

            SolveMode.DISTANCE -> {
                val d = (effGn ?: 0.0) / inputs.aperture
                primary = formatDistance(d)
                unitSuffix = unit.symbol
                checkDistance(advices, d, effGn, unit)
            }

            SolveMode.GUIDE_NUMBER -> {
                val gn = inputs.aperture * inputs.distance / gainFactor(inputs)
                primary = formatGuideNumber(gn)
                detail = "ISO 100 · ${unit.symbol}"
                if (gn > GN_LARGE_THRESHOLD) advices += Advice.GnTooLarge(GN_LARGE_THRESHOLD)
                if (gn < GN_SMALL_THRESHOLD) advices += Advice.GnTooSmall(GN_SMALL_THRESHOLD)
                checkDistance(advices, inputs.distance, effGn, unit)
            }

            SolveMode.ISO -> {
                val needed = 100.0 * (inputs.aperture * inputs.distance /
                    (inputs.gnIso100 * 2.0.pow(inputs.powerEv / 2.0) *
                        2.0.pow(inputs.compensationEv / 2.0))).pow(2.0)
                val standard = PhotoScales.nearestIsoAtLeast(needed)
                primary = standard.toString()
                detail = formatIsoDetail(needed)
                if (standard >= ISO_HIGH_THRESHOLD) advices += Advice.IsoTooHigh(standard)
                if (standard <= ISO_LOW_THRESHOLD) advices += Advice.IsoTooLow(standard)
                checkDistance(advices, inputs.distance, effGn, unit)
            }
        }

        effGn?.let { advices += Advice.EffectiveGn(it) }
        advices += Advice.SyncSpeedReminder

        val rows = effGn?.let { gn ->
            PhotoScales.APERTURES
                .filter { it >= FASTEST_USABLE_APERTURE && it <= 22.0 }
                .map { f -> ReferenceRow(f, gn / f) }
        } ?: emptyList()

        return CalcResult(
            mode = inputs.mode,
            isValid = true,
            primary = primary,
            unitSuffix = unitSuffix,
            detail = detail,
            effectiveGn = effGn,
            advices = advices.distinct(),
            rows = rows
        )
    }

    private fun checkDistance(
        advices: MutableList<Advice>,
        distance: Double,
        effGn: Double?,
        unit: DistanceUnit
    ) {
        val nearThreshold = unit.fromMeters(MIN_DISTANCE_METERS)
        if (distance < nearThreshold) {
            advices += Advice.DistanceTooNear(nearThreshold)
        }
        effGn?.let {
            val farThreshold = it / FASTEST_USABLE_APERTURE
            if (distance > farThreshold) advices += Advice.DistanceTooFar(farThreshold)
        }
    }

    fun formatAperture(value: Double): String =
        if (value < 10.0) String.format(Locale.US, "%.1f", value)
        else String.format(Locale.US, "%.0f", value)

    fun formatDistance(value: Double): String =
        when {
            value < 10.0 -> String.format(Locale.US, "%.1f", value)
            value < 100.0 -> String.format(Locale.US, "%.1f", value)
            else -> String.format(Locale.US, "%.0f", value)
        }

    fun formatGuideNumber(value: Double): String =
        if (value < 10.0) String.format(Locale.US, "%.1f", value)
        else String.format(Locale.US, "%.0f", value)

    private fun formatIsoDetail(value: Double): String =
        "≈ ${String.format(Locale.US, "%.0f", value)}"
}
