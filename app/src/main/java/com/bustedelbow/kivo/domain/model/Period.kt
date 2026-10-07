package com.bustedelbow.kivo.domain.model

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
}
