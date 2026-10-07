package com.bustedelbow.kivo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

/** Pins Kivo's refined Material 3 Expressive shape scale: 8 / 12 / 16 / 20 / 28dp. */
class KivoShapesTest {
    @Test
    fun shapeScaleMatchesTheSpec() {
        assertEquals(RoundedCornerShape(8.dp), KivoShapes.extraSmall)
        assertEquals(RoundedCornerShape(12.dp), KivoShapes.small)
        assertEquals(RoundedCornerShape(16.dp), KivoShapes.medium)
        assertEquals(RoundedCornerShape(20.dp), KivoShapes.large)
        assertEquals(RoundedCornerShape(28.dp), KivoShapes.extraLarge)
    }
}
