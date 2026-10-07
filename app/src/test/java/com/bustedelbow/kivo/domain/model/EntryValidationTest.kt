package com.bustedelbow.kivo.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The invariants an Entry must satisfy before it is recorded: a positive amount, a chosen Account,
 * a Category of the same kind, and a date that is not in the future (issue #4).
 */
class EntryValidationTest {
    private val food = Category(id = 1, name = "Food", type = CategoryType.EXPENSE)
    private val salary = Category(id = 2, name = "Salary", type = CategoryType.INCOME)
    private val today = 20_000L

    private fun errors(
        type: EntryType = EntryType.EXPENSE,
        amountMinorUnits: Long? = 1_000,
        accountId: Long? = 1,
        category: Category? = food,
        occurredOnEpochDay: Long = today,
    ) = EntryValidation.errorsFor(
        type = type,
        amountMinorUnits = amountMinorUnits,
        accountId = accountId,
        category = category,
        occurredOnEpochDay = occurredOnEpochDay,
        todayEpochDay = today,
    )

    @Test
    fun `a complete draft has no errors`() {
        assertEquals(emptySet<EntryValidationError>(), errors())
    }

    @Test
    fun `a missing or non-positive amount is rejected`() {
        assertEquals(setOf(EntryValidationError.AMOUNT_NOT_POSITIVE), errors(amountMinorUnits = null))
        assertEquals(setOf(EntryValidationError.AMOUNT_NOT_POSITIVE), errors(amountMinorUnits = 0))
        assertEquals(setOf(EntryValidationError.AMOUNT_NOT_POSITIVE), errors(amountMinorUnits = -5))
    }

    @Test
    fun `a missing account is rejected`() {
        assertEquals(setOf(EntryValidationError.ACCOUNT_REQUIRED), errors(accountId = null))
    }

    @Test
    fun `a missing category is rejected`() {
        assertEquals(setOf(EntryValidationError.CATEGORY_REQUIRED), errors(category = null))
    }

    @Test
    fun `a category of the wrong kind is rejected`() {
        assertEquals(
            setOf(EntryValidationError.CATEGORY_KIND_MISMATCH),
            errors(type = EntryType.EXPENSE, category = salary),
        )
    }

    @Test
    fun `a future date is rejected`() {
        assertEquals(setOf(EntryValidationError.DATE_IN_FUTURE), errors(occurredOnEpochDay = today + 1))
    }

    @Test
    fun `an income accepts an income category`() {
        assertEquals(emptySet<EntryValidationError>(), errors(type = EntryType.INCOME, category = salary))
    }

    @Test
    fun `every broken rule is reported together`() {
        assertEquals(
            setOf(
                EntryValidationError.AMOUNT_NOT_POSITIVE,
                EntryValidationError.ACCOUNT_REQUIRED,
                EntryValidationError.CATEGORY_REQUIRED,
                EntryValidationError.DATE_IN_FUTURE,
            ),
            errors(
                amountMinorUnits = 0,
                accountId = null,
                category = null,
                occurredOnEpochDay = today + 1,
            ),
        )
    }
}
