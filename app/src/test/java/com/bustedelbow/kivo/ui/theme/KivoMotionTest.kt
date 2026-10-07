package com.bustedelbow.kivo.ui.theme

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

/** Pins Kivo's hand-rolled motion spring tokens (ADR-0007) to Material's published values. */
class KivoMotionTest {
    @Test
    fun spatialDefaultIsTheExpressiveWorkhorse() {
        val spring = KivoMotion.spatialDefault<Float>()
        assertEquals(0.8f, spring.dampingRatio, 0f)
        assertEquals(380f, spring.stiffness, 0f)
    }

    @Test
    fun spatialFastSuitsSmallElements() {
        val spring = KivoMotion.spatialFast<Offset>()
        assertEquals(0.6f, spring.dampingRatio, 0f)
        assertEquals(800f, spring.stiffness, 0f)
    }

    @Test
    fun effectsNeverOvershoots() {
        val spring = KivoMotion.effects<Float>()
        assertEquals(1.0f, spring.dampingRatio, 0f)
        assertEquals(1600f, spring.stiffness, 0f)
    }
}
