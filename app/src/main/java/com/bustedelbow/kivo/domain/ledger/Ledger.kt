package com.bustedelbow.kivo.domain.ledger

import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.AdjustmentEntry
import com.bustedelbow.kivo.domain.model.Entry
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.domain.model.IncomeEntry
import com.bustedelbow.kivo.domain.model.Period
import com.bustedelbow.kivo.domain.model.TransferEntry

/**
 * The pure balance maths (ADR-0002): it derives Balances and Spend from Accounts and Entries and
 * stores nothing. It touches no Android or database types, so it is unit-testable on the JVM.
 */
object Ledger {
    /** The signed change [entry] applies to [accountId], or 0 when it does not touch it. */
    fun deltaFor(
        entry: Entry,
        accountId: Long,
    ): Long =
        when (entry) {
            is ExpenseEntry -> if (entry.accountId == accountId) -entry.amountMinorUnits else 0
            is IncomeEntry -> if (entry.accountId == accountId) entry.amountMinorUnits else 0
            is AdjustmentEntry -> if (entry.accountId == accountId) entry.deltaMinorUnits else 0
            is TransferEntry ->
                when (accountId) {
                    entry.fromAccountId -> -entry.amountMinorUnits
                    entry.toAccountId -> entry.amountMinorUnits
                    else -> 0
                }
        }

    /** The derived Balance of [account] given every Entry it may be affected by. */
    fun balanceOf(
        account: Account,
        entries: List<Entry>,
    ): Long = account.openingBalanceMinorUnits + entries.sumOf { deltaFor(it, account.id) }

    /**
     * The signed amount [entry] shows in history, relative to the Account the user recorded it
     * against: negative for money leaving, positive for money arriving.
     */
    fun signedAmountOf(entry: Entry): Long =
        when (entry) {
            is ExpenseEntry -> deltaFor(entry, entry.accountId)
            is IncomeEntry -> deltaFor(entry, entry.accountId)
            is AdjustmentEntry -> deltaFor(entry, entry.accountId)
            is TransferEntry -> deltaFor(entry, entry.fromAccountId)
        }

    /** Every Account paired with its derived Balance, in the order given. */
    fun balancesOf(
        accounts: List<Account>,
        entries: List<Entry>,
    ): List<AccountBalance> = accounts.map { AccountBalance(account = it, balanceMinorUnits = balanceOf(it, entries)) }

    /** The total Balance across active Accounts; Archived Accounts are excluded (ADR-0004). */
    fun totalOf(balances: List<AccountBalance>): Long = balances.filterNot { it.account.archived }.sumOf { it.balanceMinorUnits }

    /**
     * The Spend in [period]: the sum of Expense Entries inside it. Transfers and Adjustments are
     * never Spend (GLOSSARY: Spend).
     */
    fun spendOf(
        entries: List<Entry>,
        period: Period,
    ): Long =
        entries
            .filterIsInstance<ExpenseEntry>()
            .filter { period.contains(it.occurredOnEpochDay) }
            .sumOf { it.amountMinorUnits }

    /** The Income in [period]: the sum of Income Entries inside it. */
    fun incomeOf(
        entries: List<Entry>,
        period: Period,
    ): Long =
        entries
            .filterIsInstance<IncomeEntry>()
            .filter { period.contains(it.occurredOnEpochDay) }
            .sumOf { it.amountMinorUnits }
}
