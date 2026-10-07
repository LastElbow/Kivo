package com.bustedelbow.kivo.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * The time window a summary covers (GLOSSARY: Period), identified by ISO epoch days. Both ends
 * are inclusive.
 */
data class Period(
    val startEpochDay: Long,
    val endEpochDay: Long,
) {
    init {
        require(endEpochDay >= startEpochDay) {
            "Period end ($endEpochDay) must not precede start ($startEpochDay)"
        }
    }

    /** Whether [epochDay] falls inside this Period. */
    fun contains(epochDay: Long): Boolean = epochDay in startEpochDay..endEpochDay

    companion object {
        /**
         * The Monday-to-Sunday Week (GLOSSARY: Period) containing [epochDay]. Weeks follow the
         * ISO-8601 convention that a Week starts on Monday.
         */
        fun weekContaining(epochDay: Long): Period {
            val monday =
                LocalDate
                    .ofEpochDay(epochDay)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            return Period(
                startEpochDay = monday.toEpochDay(),
                endEpochDay = monday.plusDays(DAYS_IN_WEEK - 1).toEpochDay(),
            )
        }

        private const val DAYS_IN_WEEK = 7L
    }
}
