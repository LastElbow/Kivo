package com.bustedelbow.kivo.domain.model

/**
 * An [Entry] with the names of the Account and Category it references resolved, so recent history
 * can be read without another lookup. A Category is absent for Entries that carry none, such as an
 * Adjustment.
 */
data class EntrySummary(
    val entry: Entry,
    val accountName: String,
    val categoryName: String?,
)
