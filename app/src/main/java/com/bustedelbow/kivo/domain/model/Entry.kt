package com.bustedelbow.kivo.domain.model

/**
 * A single recorded movement of money (GLOSSARY: Entry). Entries are the source of truth; every
 * Account Balance is derived from them (ADR-0002).
 *
 * Amounts are `Long` minor units (ADR-0003): [ExpenseEntry], [IncomeEntry] and [TransferEntry]
 * carry a positive magnitude, while [AdjustmentEntry] carries a signed delta.
 */
sealed interface Entry {
    val id: Long
    /** The calendar day the movement happened, as an ISO epoch day. */
    val occurredOnEpochDay: Long
    val note: String?
}

/** An Entry that reduces the money in one Account (GLOSSARY: Expense). */
data class ExpenseEntry(
    override val id: Long,
    val accountId: Long,
    val categoryId: Long,
    val amountMinorUnits: Long,
    override val occurredOnEpochDay: Long,
    override val note: String? = null,
) : Entry

/** An Entry that increases the money in one Account (GLOSSARY: Income). */
data class IncomeEntry(
    override val id: Long,
    val accountId: Long,
    val categoryId: Long,
    val amountMinorUnits: Long,
    override val occurredOnEpochDay: Long,
    override val note: String? = null,
) : Entry

/**
 * An Entry that moves money between two Accounts (GLOSSARY: Transfer): it leaves
 * [fromAccountId] and arrives in [toAccountId]. A Transfer changes no Account's total and is
 * never counted as Spend.
 */
data class TransferEntry(
    override val id: Long,
    val fromAccountId: Long,
    val toAccountId: Long,
    val amountMinorUnits: Long,
    override val occurredOnEpochDay: Long,
    override val note: String? = null,
) : Entry

/**
 * An Entry that corrects an Account's Balance to a real-world value the user observes
 * (GLOSSARY: Adjustment). [deltaMinorUnits] is signed: positive raises the Balance, negative
 * lowers it. An Adjustment is not Spend and belongs to no Category.
 */
data class AdjustmentEntry(
    override val id: Long,
    val accountId: Long,
    val deltaMinorUnits: Long,
    override val occurredOnEpochDay: Long,
    override val note: String? = null,
) : Entry

/** The persisted discriminator for the [Entry] subtypes. */
enum class EntryType {
    EXPENSE,
    INCOME,
    TRANSFER,
    ADJUSTMENT,
}
