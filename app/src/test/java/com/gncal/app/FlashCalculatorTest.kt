package com.gncal.app

import com.gncal.app.model.DistanceUnit
import com.gncal.app.model.FlashCalculator
import com.gncal.app.model.FlashInputs
import com.gncal.app.model.PhotoScales
import com.gncal.app.model.SolveMode
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
        isoIndex = PhotoScales.ISOS.indexOf(iso),
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
}
