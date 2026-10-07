package com.bustedelbow.kivo.data

import com.bustedelbow.kivo.data.local.dao.AccountDao
import com.bustedelbow.kivo.data.local.dao.CategoryDao
import com.bustedelbow.kivo.data.local.dao.EntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.CategoryEntity
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory DAO doubles for unit-testing the repositories and ViewModels without Room. They keep
 * the same observable behaviour as the generated DAOs that matters here: active Accounts exclude
 * Archived rows, inserts are observable, and reads return the order their queries declare.
 */
class FakeAccountDao(
    initial: List<AccountEntity> = emptyList(),
) : AccountDao {
    private val rows = MutableStateFlow(initial)

    /** Every Account passed to [insert], in order, so tests can assert the mapping. */
    val inserted = mutableListOf<AccountEntity>()

    override fun observeActive(): Flow<List<AccountEntity>> = rows.map { accounts -> accounts.filterNot { it.archived } }

    override fun observeAll(): Flow<List<AccountEntity>> = rows

    override suspend fun insert(account: AccountEntity): Long {
        val id = (rows.value.maxOfOrNull { it.id } ?: 0L) + 1
        val stored = account.copy(id = id)
        inserted += stored
        rows.value = rows.value + stored
        return id
    }
}

/** An in-memory [EntryDao] double that mirrors the generated DAO's ordering and inserts. */
class FakeEntryDao(
    initial: List<EntryEntity> = emptyList(),
) : EntryDao {
    private val rows = MutableStateFlow(initial)

    /** Every Entry passed to [insert], in order, so tests can assert the mapping. */
    val inserted = mutableListOf<EntryEntity>()

    override fun observeAll(): Flow<List<EntryEntity>> = rows.map { newestFirst(it) }

    override fun observeRecent(limit: Int): Flow<List<EntryEntity>> = rows.map { newestFirst(it).take(limit) }

    override suspend fun insert(entry: EntryEntity): Long {
        val id = (rows.value.maxOfOrNull { it.id } ?: 0L) + 1
        val stored = entry.copy(id = id)
        inserted += stored
        rows.value = rows.value + stored
        return id
    }

    private fun newestFirst(entries: List<EntryEntity>): List<EntryEntity> =
        entries.sortedWith(compareByDescending<EntryEntity> { it.occurredOnEpochDay }.thenByDescending { it.id })
}

/** An in-memory [CategoryDao] double; seeding is exercised against real Room elsewhere. */
class FakeCategoryDao(
    initial: List<CategoryEntity> = emptyList(),
) : CategoryDao {
    private val rows = MutableStateFlow(initial)

    override fun observeActive(): Flow<List<CategoryEntity>> =
        rows.map { categories -> categories.filterNot { it.archived }.sortedByName() }

    override fun observeAll(): Flow<List<CategoryEntity>> = rows.map { it.sortedByName() }

    private fun List<CategoryEntity>.sortedByName(): List<CategoryEntity> = sortedBy { it.name.lowercase() }
}
