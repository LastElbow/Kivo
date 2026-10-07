package com.bustedelbow.kivo.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * The [Period.weekContaining] maths: a Week runs from Monday to Sunday (ISO-8601), independent of
 * which day inside it is asked about.
 */
class PeriodTest {
    private fun epochDay(
        year: Int,
        month: Int,
        day: Int,
    ): Long = LocalDate.of(year, month, day).toEpochDay()

    @Test
    fun `the week runs from the Monday before to the Sunday after`() {
        // 2026-10-07 is a Wednesday; its week is 2026-10-05 (Mon) to 2026-10-11 (Sun).
        val period = Period.weekContaining(epochDay(2026, 10, 7))

        assertEquals(epochDay(2026, 10, 5), period.startEpochDay)
        assertEquals(epochDay(2026, 10, 11), period.endEpochDay)
    }

    @Test
    fun `a Sunday belongs to the week that began the previous Monday`() {
        val period = Period.weekContaining(epochDay(2026, 10, 11))

        assertEquals(epochDay(2026, 10, 5), period.startEpochDay)
        assertEquals(epochDay(2026, 10, 11), period.endEpochDay)
    }

    @Test
    fun `a Monday starts its own week`() {
        val period = Period.weekContaining(epochDay(2026, 10, 5))

        assertEquals(epochDay(2026, 10, 5), period.startEpochDay)
        assertEquals(epochDay(2026, 10, 11), period.endEpochDay)
    }
}
