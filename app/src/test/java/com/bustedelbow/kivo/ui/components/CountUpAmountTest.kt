package com.bustedelbow.kivo.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Home hero's count-up is money maths, so it is pure Kotlin and tested directly rather than
 * only through the rendered string (docs/testing.md). Its contract is that the amount it draws
 * starts on the amount being counted from and rests on the target, to the centavo, whatever the
 * magnitudes — which is exactly what driving the count as a `Float` or `Double` could not
 * promise (ADR-0003, issue #9).
 */
class CountUpAmountTest {
    @Test
    fun `a zero fraction is the amount the count starts from`() {
        assertEquals(100_000L, countUpAmount(fromMinorUnits = 100_000L, toMinorUnits = 250_000L, fraction = 0f))
    }

    @Test
    fun `a whole fraction rests on the target exactly`() {
        // 3,141,592,653,589 centavos is past the 2^24 of them a Float holds to the centavo.
        val target = 3_141_592_653_589L

        assertEquals(target, countUpAmount(fromMinorUnits = 0L, toMinorUnits = target, fraction = 1f))
        assertEquals(target, countUpAmount(fromMinorUnits = 999_999L, toMinorUnits = target, fraction = 1f))
    }

    @Test
    fun `half way is half the distance`() {
        assertEquals(150_000L, countUpAmount(fromMinorUnits = 100_000L, toMinorUnits = 200_000L, fraction = 0.5f))
    }

    @Test
    fun `a falling count walks down from the amount it starts on`() {
        assertEquals(50_000L, countUpAmount(fromMinorUnits = 100_000L, toMinorUnits = 0L, fraction = 0.5f))
    }

    @Test
    fun `a fraction past one walks past the target and back`() {
        // The spatial default spring is underdamped, so the count overshoots before it rests.
        assertTrue(countUpAmount(fromMinorUnits = 0L, toMinorUnits = 100_000L, fraction = 1.05f) > 100_000L)
    }
}
