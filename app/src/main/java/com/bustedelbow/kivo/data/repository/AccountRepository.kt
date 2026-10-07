package com.bustedelbow.kivo.data.repository

import com.bustedelbow.kivo.data.local.dao.AccountDao
import com.bustedelbow.kivo.data.local.dao.EntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.mapper.toDomain
import com.bustedelbow.kivo.domain.ledger.Ledger
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.NewAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Reads and writes Accounts, and derives each one's Balance from its Entries (ADR-0002). Balances
 * are never persisted.
 */
class AccountRepository(
    private val accountDao: AccountDao,
    private val entryDao: EntryDao,
) {

    /** The active Accounts, each paired with its derived Balance, ordered by name. */
    fun observeAccountBalances(): Flow<List<AccountBalance>> =
        combine(accountDao.observeActive(), entryDao.observeAll()) { accounts, entries ->
            val domainEntries = entries.map { it.toDomain() }
            Ledger.balancesOf(accounts.map { it.toDomain() }, domainEntries)
        }

    /** Creates an Account and returns its generated id. */
    suspend fun createAccount(account: NewAccount): Long = accountDao.insert(
        AccountEntity(
            name = account.name,
            type = account.type.name,
            openingBalanceMinorUnits = account.openingBalanceMinorUnits,
        ),
    )
}
