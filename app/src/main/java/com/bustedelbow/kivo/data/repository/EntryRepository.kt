package com.bustedelbow.kivo.data.repository

import com.bustedelbow.kivo.data.local.dao.AccountDao
import com.bustedelbow.kivo.data.local.dao.CategoryDao
import com.bustedelbow.kivo.data.local.dao.EntryDao
import com.bustedelbow.kivo.data.mapper.toDomain
import com.bustedelbow.kivo.data.mapper.toEntity
import com.bustedelbow.kivo.domain.model.Entry
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.domain.model.NewEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Reads and writes Entries, the source of truth for derived Balances (ADR-0002), and resolves the
 * Account and Category names recent history displays.
 */
class EntryRepository(
    private val entryDao: EntryDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
) {
    /** Every Entry as a domain type, newest first. */
    fun observeEntries(): Flow<List<Entry>> = entryDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    /**
     * The [limit] most recent Entries, newest first, each with its Account and Category names
     * resolved. Archived Accounts and Categories still resolve, so history stays readable
     * (ADR-0004).
     */
    fun observeRecentEntries(limit: Int = RECENT_ENTRY_LIMIT): Flow<List<EntrySummary>> =
        combine(
            entryDao.observeRecent(limit),
            accountDao.observeAll(),
            categoryDao.observeAll(),
        ) { entries, accounts, categories ->
            val accountNames = accounts.associate { it.id to it.name }
            val categoryNames = categories.associate { it.id to it.name }
            entries.map { entry ->
                EntrySummary(
                    entry = entry.toDomain(),
                    accountName = accountNames[entry.accountId].orEmpty(),
                    categoryName = entry.categoryId?.let(categoryNames::get),
                )
            }
        }

    /** Records an Entry and returns its generated id. */
    suspend fun createEntry(entry: NewEntry): Long = entryDao.insert(entry.toEntity())

    companion object {
        /** How many Entries Home's recent history shows. */
        const val RECENT_ENTRY_LIMIT = 10
    }
}
