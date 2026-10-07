package com.bustedelbow.kivo.domain.model

/** A reason a draft Entry cannot be recorded, surfaced to the user as it blocks saving. */
enum class EntryValidationError {
    AMOUNT_NOT_POSITIVE,
    ACCOUNT_REQUIRED,
    CATEGORY_REQUIRED,
    CATEGORY_KIND_MISMATCH,
    DATE_IN_FUTURE,
}

/**
 * The invariants an Entry must satisfy before it is recorded: a positive amount, a chosen Account,
 * a Category of the same kind as the Entry, and a date that is not in the future. Pure and
 * Android-free so the rules are unit-tested directly.
 */
object EntryValidation {
    /** Every reason [type], [amountMinorUnits], [accountId], [category] and [occurredOnEpochDay] cannot yet be recorded, given [todayEpochDay]. */
    fun errorsFor(
        type: EntryType,
        amountMinorUnits: Long?,
        accountId: Long?,
        category: Category?,
        occurredOnEpochDay: Long,
        todayEpochDay: Long,
    ): Set<EntryValidationError> =
        buildSet {
            if (amountMinorUnits == null || amountMinorUnits <= 0) add(EntryValidationError.AMOUNT_NOT_POSITIVE)
            if (accountId == null) add(EntryValidationError.ACCOUNT_REQUIRED)
            when {
                category == null -> add(EntryValidationError.CATEGORY_REQUIRED)
                !category.matches(type) -> add(EntryValidationError.CATEGORY_KIND_MISMATCH)
            }
            if (occurredOnEpochDay > todayEpochDay) add(EntryValidationError.DATE_IN_FUTURE)
        }
}
