package com.bustedelbow.kivo.domain.ledger

import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.AdjustmentEntry
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.domain.model.IncomeEntry
import com.bustedelbow.kivo.domain.model.Period
import com.bustedelbow.kivo.domain.model.TransferEntry
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The pure balance maths (ADR-0002), specified in the domain's vocabulary: derived Balance,
 * Transfer neutrality, Adjustment delta, Archived exclusion and period Spend.
 */
class LedgerTest {
    private val bank =
        Account(
            id = 1,
            name = "Bank",
            type = AccountType.BANK,
            openingBalanceMinorUnits = 100_000,
        )
    private val cash =
        Account(
            id = 2,
            name = "Cash",
            type = AccountType.CASH,
            openingBalanceMinorUnits = 0,
        )

    @Test
    fun `balance derives from the opening balance and the account's entries`() {
        val entries =
            listOf(
                IncomeEntry(
                    id = 1,
                    accountId = 1,
                    categoryId = 1,
                    amountMinorUnits = 50_000,
                    occurredOnEpochDay = 20_000,
                ),
                ExpenseEntry(
                    id = 2,
                    accountId = 1,
                    categoryId = 2,
                    amountMinorUnits = 20_000,
                    occurredOnEpochDay = 20_001,
                ),
                // Belongs to another Account and must not affect the Bank's Balance.
                ExpenseEntry(
                    id = 3,
                    accountId = 2,
                    categoryId = 2,
                    amountMinorUnits = 5_000,
                    occurredOnEpochDay = 20_001,
                ),
            )

        assertEquals(130_000L, Ledger.balanceOf(bank, entries))
    }

    @Test
    fun `a transfer moves money without changing any account total`() {
        val entries =
            listOf(
                TransferEntry(
                    id = 1,
                    fromAccountId = 1,
                    toAccountId = 2,
                    amountMinorUnits = 25_000,
                    occurredOnEpochDay = 20_000,
                ),
            )

        assertEquals(75_000L, Ledger.balanceOf(bank, entries))
        assertEquals(25_000L, Ledger.balanceOf(cash, entries))
        assertEquals(100_000L, Ledger.totalOf(Ledger.balancesOf(listOf(bank, cash), entries)))
    }

    @Test
    fun `an adjustment applies its signed delta and is never spend`() {
        val entries =
            listOf(
                AdjustmentEntry(
                    id = 1,
                    accountId = 1,
                    deltaMinorUnits = -1_500,
                    occurredOnEpochDay = 20_000,
                ),
                AdjustmentEntry(
                    id = 2,
                    accountId = 2,
                    deltaMinorUnits = 2_500,
                    occurredOnEpochDay = 20_000,
                ),
            )

        assertEquals(98_500L, Ledger.balanceOf(bank, entries))
        assertEquals(2_500L, Ledger.balanceOf(cash, entries))
        assertEquals(0L, Ledger.spendOf(entries, Period(19_000, 21_000)))
    }

    @Test
    fun `archived accounts are excluded from the total but keep their balance`() {
        val archivedCash = cash.copy(archived = true)
        val entries =
            listOf(
                IncomeEntry(
                    id = 1,
                    accountId = 2,
                    categoryId = 1,
                    amountMinorUnits = 40_000,
                    occurredOnEpochDay = 20_000,
                ),
            )

        assertEquals(100_000L, Ledger.totalOf(Ledger.balancesOf(listOf(bank, archivedCash), entries)))
        assertEquals(40_000L, Ledger.balanceOf(archivedCash, entries))
    }

    @Test
    fun `spend sums only expense entries inside the period`() {
        val period = Period(startEpochDay = 20_000, endEpochDay = 20_006)
        val entries =
            listOf(
                ExpenseEntry( // inside
                    id = 1,
                    accountId = 1,
                    categoryId = 2,
                    amountMinorUnits = 12_000,
                    occurredOnEpochDay = 20_000,
                ),
                ExpenseEntry( // inside, last day
                    id = 2,
                    accountId = 1,
                    categoryId = 2,
                    amountMinorUnits = 3_500,
                    occurredOnEpochDay = 20_006,
                ),
                ExpenseEntry( // outside
                    id = 3,
                    accountId = 1,
                    categoryId = 2,
                    amountMinorUnits = 9_999,
                    occurredOnEpochDay = 20_007,
                ),
                IncomeEntry( // income, not spend
                    id = 4,
                    accountId = 1,
                    categoryId = 1,
                    amountMinorUnits = 40_000,
                    occurredOnEpochDay = 20_001,
                ),
                TransferEntry( // transfer, not spend
                    id = 5,
                    fromAccountId = 1,
                    toAccountId = 2,
                    amountMinorUnits = 5_000,
                    occurredOnEpochDay = 20_002,
                ),
                AdjustmentEntry( // adjustment, not spend
                    id = 6,
                    accountId = 1,
                    deltaMinorUnits = -2_000,
                    occurredOnEpochDay = 20_003,
                ),
            )

        assertEquals(15_500L, Ledger.spendOf(entries, period))
    }

    @Test
    fun `income sums only income entries inside the period`() {
        val period = Period(startEpochDay = 20_000, endEpochDay = 20_006)
        val entries =
            listOf(
                IncomeEntry( // inside
                    id = 1,
                    accountId = 1,
                    categoryId = 1,
                    amountMinorUnits = 40_000,
                    occurredOnEpochDay = 20_000,
                ),
                IncomeEntry( // outside
                    id = 2,
                    accountId = 1,
                    categoryId = 1,
                    amountMinorUnits = 7_000,
                    occurredOnEpochDay = 20_007,
                ),
                ExpenseEntry( // expense, not income
                    id = 3,
                    accountId = 1,
                    categoryId = 2,
                    amountMinorUnits = 12_000,
                    occurredOnEpochDay = 20_001,
                ),
            )

        assertEquals(40_000L, Ledger.incomeOf(entries, period))
    }

    @Test
    fun `signed amount is negative for an expense and positive for an income`() {
        assertEquals(
            -20_000L,
            Ledger.signedAmountOf(
                ExpenseEntry(
                    id = 1,
                    accountId = 1,
                    categoryId = 2,
                    amountMinorUnits = 20_000,
                    occurredOnEpochDay = 20_000,
                ),
            ),
        )
        assertEquals(
            50_000L,
            Ledger.signedAmountOf(
                IncomeEntry(
                    id = 2,
                    accountId = 1,
                    categoryId = 1,
                    amountMinorUnits = 50_000,
                    occurredOnEpochDay = 20_000,
                ),
            ),
        )
    }
}
