package com.bustedelbow.kivo.data

import com.bustedelbow.kivo.data.local.dao.AccountDao
import com.bustedelbow.kivo.data.local.dao.EntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory DAO doubles for unit-testing the repositories and ViewModels without Room. They keep
 * the same observable behaviour as the generated DAOs that matters here: active Accounts exclude
 * Archived rows, and inserts are observable.
 */
class FakeAccountDao(
    initial: List<AccountEntity> = emptyList(),
) : AccountDao {
    private val rows = MutableStateFlow(initial)

    /** Every Account passed to [insert], in order, so tests can assert the mapping. */
    val inserted = mutableListOf<AccountEntity>()

    override fun observeActive(): Flow<List<AccountEntity>> = rows.map { accounts -> accounts.filterNot { it.archived } }

    override suspend fun insert(account: AccountEntity): Long {
        val id = (rows.value.maxOfOrNull { it.id } ?: 0L) + 1
        val stored = account.copy(id = id)
        inserted += stored
        rows.value = rows.value + stored
        return id
    }
}

/** An in-memory [EntryDao] double; entries are read-only in this slice. */
class FakeEntryDao(
    initial: List<EntryEntity> = emptyList(),
) : EntryDao {
    private val rows = MutableStateFlow(initial)

    override fun observeAll(): Flow<List<EntryEntity>> = rows
}
