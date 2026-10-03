package com.gncal.app.ui.haptic

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.staticCompositionLocalOf

/** 振动事件类型。baseDurationMs 为基准时长，实际时长与振幅再按强度缩放。 */
enum class HapticEvent(val baseDurationMs: Long) {
    /** 档位跳动：光圈 / ISO / 功率 / 补偿每跳一档 */
    TICK(12L),

    /** 选择：切换求解模式或单位 */
    SELECT(24L),

    /** 确认：重置、保存等较重的操作 */
    CONFIRM(40L)
}

/**
 * 触感反馈控制器。
 *
 * 直接使用 Vibrator 而非 Compose 的 LocalHapticFeedback，因为后者不提供强度调节，
 * 且多数 ROM 会忽略其请求。强度 0–100 同时影响振幅与时长：
 * - 支持振幅控制的设备（API 26+ hasAmplitudeControl）按强度映射振幅 60–255；
 * - 不支持的设备退化为默认振幅，靠时长变化体现强度差异。
 *
 * context 为 null 时退化为空实现（用于 CompositionLocal 默认值）。
 */
class Haptics(private val context: Context?) {

    private var enabled: Boolean = true
    private var intensity: Int = DEFAULT_INTENSITY
    private var lastVibrationAtMs: Long = 0L

    fun configure(enabled: Boolean, intensity: Int) {
        this.enabled = enabled
        this.intensity = intensity.coerceIn(0, 100)
    }

    fun perform(event: HapticEvent) {
        val vibrator = context?.let(::resolveVibrator) ?: return
        if (!enabled || !vibrator.hasVibrator()) return

        // 节流：连点按钮或快速拖动滑杆时不让振动器被高频调用，避免主线程抖动
        val now = SystemClock.elapsedRealtime()
        if (now - lastVibrationAtMs < MIN_INTERVAL_MS) return
        lastVibrationAtMs = now

        val scale = intensity / 100f
        val amplitude = if (vibrator.hasAmplitudeControl()) {
            (60 + scale * 195).toInt().coerceIn(1, 255)
        } else {
            VibrationEffect.DEFAULT_AMPLITUDE
        }
        val duration = (event.baseDurationMs * (0.6f + scale * 0.9f))
            .toLong()
            .coerceIn(8L, 150L)

        runCatching { vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude)) }
    }

    private fun resolveVibrator(context: Context): Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }.getOrNull()

    companion object {
        const val DEFAULT_INTENSITY = 55
        private const val MIN_INTERVAL_MS = 25L
    }
}

/** 未提供实例时为空实现，界面代码可直接调用而不必判空。 */
val LocalHaptics = staticCompositionLocalOf { Haptics(null) }
