package com.bustedelbow.kivo.domain.model

/**
 * The details needed to record an Entry (GLOSSARY: Entry): its kind, amount, Account, Category,
 * date and optional note. Passed as one value from the Add Entry form down to the repository so the
 * fields cannot drift apart, mirroring `NewAccount`.
 *
 * Only Expense and Income Entries are recorded in this slice; Transfers and Adjustments arrive
 * later. [amountMinorUnits] is a positive magnitude in minor units (ADR-0003).
 */
data class NewEntry(
    val type: EntryType,
    val amountMinorUnits: Long,
    val accountId: Long,
    val categoryId: Long,
    val occurredOnEpochDay: Long,
    val note: String? = null,
)
