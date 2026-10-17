package com.gncal.app

import com.gncal.app.data.IsoMode
import com.gncal.app.model.Advice
import com.gncal.app.model.DistanceUnit
import com.gncal.app.model.FlashCalculator
import com.gncal.app.model.FlashInputs
import com.gncal.app.model.PhotoScales
import com.gncal.app.model.SolveMode
import com.gncal.app.ui.calc.CalculatorState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlashCalculatorTest {

    private fun inputs(
        mode: SolveMode,
        gn: Double = 36.0,
        aperture: Double = 5.6,
        distance: Double = 3.0,
        iso: Int = 100,
        powerIndex: Int = 0,
        compensationIndex: Int = PhotoScales.DEFAULT_COMP_INDEX,
        unit: DistanceUnit = DistanceUnit.METER
    ) = FlashInputs(
        mode = mode,
        gnIso100 = gn,
        apertureIndex = PhotoScales.APERTURES.indexOf(aperture),
        distance = distance,
        iso = iso,
        powerIndex = powerIndex,
        compensationIndex = compensationIndex,
        unit = unit
    )

    @Test
    fun effectiveGn_doublesWhenIsoQuadruples() {
        val base = inputs(SolveMode.DISTANCE, iso = 100)
        val higher = inputs(SolveMode.DISTANCE, iso = 400)
        assertEquals(36.0, FlashCalculator.effectiveGn(base), 0.001)
        assertEquals(72.0, FlashCalculator.effectiveGn(higher), 0.001)
    }

    @Test
    fun solveAperture_gn36_iso100_distance4_5_isF8() {
        val result = FlashCalculator.solve(
            inputs(SolveMode.APERTURE, distance = 4.5, iso = 100)
        )
        assertEquals("f/8.0", result.primary)
        assertTrue(result.isValid)
    }

    @Test
    fun solveDistance_gn36_iso400_f5_6_isAbout12_9() {
        val result = FlashCalculator.solve(
            inputs(SolveMode.DISTANCE, aperture = 5.6, iso = 400)
        )
        assertEquals("12.9", result.primary)
        assertEquals("m", result.unitSuffix)
    }

    @Test
    fun solveGuideNumber_f8_distance4_5_iso100_is36() {
        val result = FlashCalculator.solve(
            inputs(SolveMode.GUIDE_NUMBER, aperture = 8.0, distance = 4.5, iso = 100)
        )
        assertEquals("36", result.primary)
    }

    @Test
    fun solveIso_gn36_f8_distance4_5_is100() {
        val result = FlashCalculator.solve(
            inputs(SolveMode.ISO, aperture = 8.0, distance = 4.5)
        )
        assertEquals("100", result.primary)
    }

    @Test
    fun quarterPower_reducesEffectiveGnByHalf() {
        val result = FlashCalculator.solve(
            inputs(SolveMode.APERTURE, distance = 2.0, powerIndex = 2)
        )
        // 1/4 功率下有效 GN = 36 × 0.5 = 18，距离 2 m 时光圈 = 9
        assertEquals("f/9.0", result.primary)
    }

    @Test
    fun feetUnit_usesSameFormulaInFeet() {
        val result = FlashCalculator.solve(
            inputs(SolveMode.APERTURE, gn = 36.0, distance = 10.0, unit = DistanceUnit.FEET)
        )
        assertEquals("f/3.6", result.primary)
    }

    @Test
    fun invalidDistance_reportsInvalid() {
        val result = FlashCalculator.solve(inputs(SolveMode.APERTURE, distance = 0.0))
        assertTrue(!result.isValid)
        assertEquals("—", result.primary)
    }

    // ---------- 单位换算 ----------

    @Test
    fun distanceUnit_convertsLinearlyBetweenMetersAndFeet() {
        assertEquals(9.843, DistanceUnit.METER.convert(3.0, DistanceUnit.FEET), 0.001)
        assertEquals(118.11, DistanceUnit.METER.convert(36.0, DistanceUnit.FEET), 0.01)
        assertEquals(3.0, DistanceUnit.FEET.convert(9.843, DistanceUnit.METER), 0.001)
        assertEquals(36.0, DistanceUnit.METER.convert(36.0, DistanceUnit.METER), 0.0)
    }

    @Test
    fun solveAperture_samePhysicalSetup_givesSameResultInBothUnits() {
        val meters = FlashCalculator.solve(inputs(SolveMode.APERTURE, gn = 36.0, distance = 3.0))
        val feet = FlashCalculator.solve(
            inputs(
                SolveMode.APERTURE,
                gn = DistanceUnit.METER.convert(36.0, DistanceUnit.FEET),
                distance = DistanceUnit.METER.convert(3.0, DistanceUnit.FEET),
                unit = DistanceUnit.FEET
            )
        )
        assertEquals("f/12", meters.primary)
        assertEquals(meters.primary, feet.primary)

        // 求距离模式的结果带单位后缀：数值不同但物理量必须一致
        val metersDistance = FlashCalculator.solve(
            inputs(SolveMode.DISTANCE, gn = 36.0, aperture = 5.6, iso = 400)
        )
        val feetDistance = FlashCalculator.solve(
            inputs(
                SolveMode.DISTANCE,
                gn = DistanceUnit.METER.convert(36.0, DistanceUnit.FEET),
                aperture = 5.6,
                iso = 400,
                unit = DistanceUnit.FEET
            )
        )
        assertEquals("m", metersDistance.unitSuffix)
        assertEquals("ft", feetDistance.unitSuffix)
        assertEquals(
            DistanceUnit.METER.convert(metersDistance.primary.toDouble(), DistanceUnit.FEET),
            feetDistance.primary.toDouble(),
            // 两侧结果都只保留一位小数，米制 0.05 m 的舍入在英制下约 0.16 ft
            0.2
        )
    }

    @Test
    fun gnThreshold_isConvertedToCurrentUnit() {
        // 8 × 14.76 ft ≈ 118 ft，等效于 36 m，不应被误报为「GN 过大」
        val feet = FlashCalculator.solve(
            inputs(SolveMode.GUIDE_NUMBER, aperture = 8.0, distance = 14.76, unit = DistanceUnit.FEET)
        )
        assertTrue(feet.advices.none { it is Advice.GnTooLarge })

        // 同一场景用米制表达：8 × 4.5 m = 36 m，同样不报警
        val meters = FlashCalculator.solve(
            inputs(SolveMode.GUIDE_NUMBER, aperture = 8.0, distance = 4.5)
        )
        assertTrue(meters.advices.none { it is Advice.GnTooLarge })
    }

    @Test
    fun gnThreshold_warnsConsistentlyForPhysicallyIdenticalSetups() {
        val meters = FlashCalculator.solve(
            inputs(SolveMode.GUIDE_NUMBER, aperture = 8.0, distance = 12.5)
        )
        val feet = FlashCalculator.solve(
            inputs(
                SolveMode.GUIDE_NUMBER,
                aperture = 8.0,
                distance = DistanceUnit.METER.convert(12.5, DistanceUnit.FEET),
                unit = DistanceUnit.FEET
            )
        )
        assertTrue(meters.advices.any { it is Advice.GnTooLarge })
        assertTrue(feet.advices.any { it is Advice.GnTooLarge })
    }

    @Test
    fun distanceTooNear_thresholdIsConvertedToFeet() {
        // 0.5 m ≈ 1.64 ft
        val feet = FlashCalculator.solve(
            inputs(SolveMode.APERTURE, gn = 36.0, distance = 1.0, unit = DistanceUnit.FEET)
        )
        val meters = FlashCalculator.solve(inputs(SolveMode.APERTURE, gn = 36.0, distance = 1.0))
        assertTrue(feet.advices.any { it is Advice.DistanceTooNear })
        assertTrue(meters.advices.none { it is Advice.DistanceTooNear })
    }

    @Test
    fun calculatorState_convertsInputValuesWhenUnitChanges() {
        val state = CalculatorState()
        state.gnText = "36"
        state.distanceText = "3"

        state.convertUnit(DistanceUnit.METER, DistanceUnit.FEET)
        assertEquals("118", state.gnText)
        assertEquals("9.8", state.distanceText)

        state.convertUnit(DistanceUnit.FEET, DistanceUnit.METER)
        assertEquals("36", state.gnText)
        assertEquals("3.0", state.distanceText)
    }

    @Test
    fun calculatorState_ignoresNonNumericInputOnUnitChange() {
        val state = CalculatorState()
        state.gnText = ""
        state.distanceText = "abc"
        state.convertUnit(DistanceUnit.METER, DistanceUnit.FEET)
        assertEquals("", state.gnText)
        assertEquals("abc", state.distanceText)
    }

    // ---------- 胶片 ISO 模式 ----------

    @Test
    fun snapFilmIso_singleDigitsStepByOne() {
        assertEquals(6, PhotoScales.snapFilmIso(6f))
        assertEquals(7, PhotoScales.snapFilmIso(6.6f))
        assertEquals(8, PhotoScales.snapFilmIso(8.4f))
        assertEquals(9, PhotoScales.snapFilmIso(9f))
    }

    @Test
    fun snapFilmIso_snapsToStandardValuesAboveNine() {
        assertEquals(10, PhotoScales.snapFilmIso(9.6f))
        assertEquals(10, PhotoScales.snapFilmIso(11f))
        assertEquals(25, PhotoScales.snapFilmIso(24f))
        assertEquals(100, PhotoScales.snapFilmIso(96f))
        assertEquals(400, PhotoScales.snapFilmIso(390f))
        assertEquals(1600, PhotoScales.snapFilmIso(1500f))
    }

    @Test
    fun snapFilmIso_clampsToFilmRange() {
        assertEquals(PhotoScales.FILM_MIN_ISO, PhotoScales.snapFilmIso(1f))
        assertEquals(PhotoScales.FILM_MAX_ISO, PhotoScales.snapFilmIso(102400f))
    }

    @Test
    fun applyIsoMode_clampsValueIntoNewModeRange() {
        val state = CalculatorState()

        state.isoValue = 6400
        state.applyIsoMode(IsoMode.FILM)
        assertEquals(1600, state.isoValue)

        state.isoValue = 6
        state.applyIsoMode(IsoMode.EXTENDED)
        assertEquals(25, state.isoValue)

        state.isoValue = 400
        state.applyIsoMode(IsoMode.FILM)
        assertEquals(400, state.isoValue)
        state.applyIsoMode(IsoMode.EXTENDED)
        assertEquals(400, state.isoValue)
    }

    @Test
    fun filmIsoPosition_isLogarithmicAndRoundTrips() {
        assertEquals(0f, PhotoScales.filmIsoPosition(6f), 0.01f)
        assertEquals(100f, PhotoScales.filmIsoPosition(1600f), 0.01f)
        // ISO 100 应落在轨道中段附近，而不是线性刻度下的最左端
        assertTrue(PhotoScales.filmIsoPosition(100f) > 45f)
        assertTrue(PhotoScales.filmIsoPosition(100f) < 55f)

        PhotoScales.FILM_ISO_STOPS.forEach { stop ->
            assertEquals(
                stop.toFloat(),
                PhotoScales.snapFilmIso(
                    PhotoScales.filmIsoFromPosition(PhotoScales.filmIsoPosition(stop.toFloat()))
                ).toFloat(),
                0.01f
            )
        }
    }

    @Test
    fun filmIsoValues_stillProduceCorrectExposure() {
        // 胶片模式下可取标准值之间的任意整数（如 ISO 320），曝光计算必须成立
        val film = FlashCalculator.solve(inputs(SolveMode.DISTANCE, gn = 36.0, aperture = 5.6, iso = 320))
        // 36 × √(320/100) ÷ 5.6 ≈ 11.5
        assertEquals(11.5, film.primary.toDouble(), 0.1)

        val low = FlashCalculator.solve(inputs(SolveMode.DISTANCE, gn = 36.0, aperture = 5.6, iso = 6))
        // 36 × √(6/100) ÷ 5.6 ≈ 1.57
        assertEquals(1.6, low.primary.toDouble(), 0.1)
    }
}
