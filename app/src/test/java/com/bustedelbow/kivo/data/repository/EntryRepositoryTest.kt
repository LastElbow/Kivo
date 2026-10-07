package com.bustedelbow.kivo.data.repository

import com.bustedelbow.kivo.data.FakeAccountDao
import com.bustedelbow.kivo.data.FakeCategoryDao
import com.bustedelbow.kivo.data.FakeEntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.CategoryEntity
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import com.bustedelbow.kivo.domain.model.EntryType
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.domain.model.IncomeEntry
import com.bustedelbow.kivo.domain.model.NewEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [EntryRepository] records a [NewEntry] and resolves the Account and Category names recent history
 * displays, including for Archived items (issue #4, ADR-0004).
 */
class EntryRepositoryTest {
    private val bank =
        AccountEntity(
            id = 1,
            name = "Bank",
            type = "BANK",
            openingBalanceMinorUnits = 100_000,
        )
    private val retired =
        AccountEntity(
            id = 2,
            name = "Retired",
            type = "CASH",
            openingBalanceMinorUnits = 0,
            archived = true,
        )
    private val food = CategoryEntity(id = 10, name = "Food", type = "EXPENSE")
    private val salary = CategoryEntity(id = 11, name = "Salary", type = "INCOME", archived = true)

    @Test
    fun `createEntry stores the domain entry and returns its id`() =
        runTest {
            val entryDao = FakeEntryDao()
            val repository = EntryRepository(entryDao, FakeAccountDao(), FakeCategoryDao())

            val id =
                repository.createEntry(
                    NewEntry(
                        type = EntryType.EXPENSE,
                        amountMinorUnits = 12_500,
                        accountId = 1,
                        categoryId = 10,
                        occurredOnEpochDay = 20_000,
                        note = "Lunch",
                    ),
                )

            assertEquals(1L, id)
            val stored = entryDao.inserted.single()
            assertEquals("EXPENSE", stored.type)
            assertEquals(1L, stored.accountId)
            assertEquals(10L, stored.categoryId)
            assertEquals(12_500L, stored.amountMinorUnits)
            assertEquals(20_000L, stored.occurredOnEpochDay)
            assertEquals("Lunch", stored.note)
        }

    @Test
    fun `observeEntries maps rows to domain entries`() =
        runTest {
            val repository =
                EntryRepository(
                    FakeEntryDao(
                        listOf(
                            entry(id = 1, type = "EXPENSE", accountId = 1, categoryId = 10),
                            entry(id = 2, type = "INCOME", accountId = 1, categoryId = 11),
                        ),
                    ),
                    FakeAccountDao(listOf(bank)),
                    FakeCategoryDao(listOf(food, salary)),
                )

            val entries = repository.observeEntries().first()

            assertEquals(true, entries.any { it is ExpenseEntry })
            assertEquals(true, entries.any { it is IncomeEntry })
        }

    @Test
    fun `observeRecentEntries resolves names, newest first, including archived items`() =
        runTest {
            val repository =
                EntryRepository(
                    FakeEntryDao(
                        listOf(
                            entry(id = 1, type = "EXPENSE", accountId = 1, categoryId = 10, occurredOnEpochDay = 100),
                            entry(id = 2, type = "INCOME", accountId = 2, categoryId = 11, occurredOnEpochDay = 200),
                        ),
                    ),
                    FakeAccountDao(listOf(bank, retired)),
                    FakeCategoryDao(listOf(food, salary)),
                )

            val summaries = repository.observeRecentEntries().first()

            assertEquals(listOf(2L, 1L), summaries.map { it.entry.id })
            assertEquals("Retired", summaries.first().accountName)
            assertEquals("Salary", summaries.first().categoryName)
            assertEquals("Bank", summaries.last().accountName)
            assertEquals("Food", summaries.last().categoryName)
        }

    @Test
    fun `observeRecentEntries returns only the most recent entries`() =
        runTest {
            val repository =
                EntryRepository(
                    FakeEntryDao(
                        listOf(
                            entry(id = 1, type = "EXPENSE", accountId = 1, categoryId = 10, occurredOnEpochDay = 100),
                            entry(id = 2, type = "EXPENSE", accountId = 1, categoryId = 10, occurredOnEpochDay = 200),
                            entry(id = 3, type = "EXPENSE", accountId = 1, categoryId = 10, occurredOnEpochDay = 300),
                        ),
                    ),
                    FakeAccountDao(listOf(bank)),
                    FakeCategoryDao(listOf(food)),
                )

            val summaries = repository.observeRecentEntries(limit = 2).first()

            assertEquals(listOf(3L, 2L), summaries.map { it.entry.id })
        }

    private fun entry(
        id: Long,
        type: String,
        accountId: Long,
        categoryId: Long?,
        occurredOnEpochDay: Long = 20_000,
    ) = EntryEntity(
        id = id,
        type = type,
        accountId = accountId,
        categoryId = categoryId,
        amountMinorUnits = 1_000,
        occurredOnEpochDay = occurredOnEpochDay,
    )
}
