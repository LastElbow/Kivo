package com.bustedelbow.kivo.ui.home

import com.bustedelbow.kivo.data.FakeAccountDao
import com.bustedelbow.kivo.data.FakeCategoryDao
import com.bustedelbow.kivo.data.FakeEntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.CategoryEntity
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.data.repository.EntryRepository
import com.bustedelbow.kivo.domain.model.EntryType
import com.bustedelbow.kivo.domain.model.NewEntry
import com.bustedelbow.kivo.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Home derives its total from active Accounts and its this-Week Spend and Income from Entries, and
 * resolves recent history's names (issue #4).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(Instant.parse("2026-10-07T09:00:00Z"), ZoneOffset.UTC)
    private val today = epochDay(2026, 10, 7)
    private val lastWeek = epochDay(2026, 9, 30)

    private val bank =
        AccountEntity(
            id = 1,
            name = "Bank",
            type = "BANK",
            openingBalanceMinorUnits = 100_000,
        )
    private val food = CategoryEntity(id = 10, name = "Food", type = "EXPENSE")
    private val salary = CategoryEntity(id = 11, name = "Salary", type = "INCOME")

    @Test
    fun `total sums the active accounts and excludes archived ones`() =
        runTest {
            val accounts =
                listOf(
                    bank,
                    AccountEntity(
                        id = 2,
                        name = "Retired",
                        type = "CASH",
                        openingBalanceMinorUnits = 50_000,
                        archived = true,
                    ),
                )
            val viewModel = viewModel(accounts)

            val state = viewModel.uiState.first { !it.isLoading }

            assertEquals(100_000L, state.totalBalanceMinorUnits)
            assertEquals(listOf(1L), state.accounts.map { it.account.id })
        }

    @Test
    fun `this-week spend and income count only entries inside the week`() =
        runTest {
            val entries =
                listOf(
                    entry(id = 1, type = "EXPENSE", categoryId = 10, amountMinorUnits = 12_000, occurredOnEpochDay = today),
                    entry(id = 2, type = "EXPENSE", categoryId = 10, amountMinorUnits = 9_999, occurredOnEpochDay = lastWeek),
                    entry(id = 3, type = "INCOME", categoryId = 11, amountMinorUnits = 40_000, occurredOnEpochDay = today),
                )
            val viewModel = viewModel(listOf(bank), entries)

            val state = viewModel.uiState.first { !it.isLoading }

            assertEquals(12_000L, state.spendMinorUnits)
            assertEquals(40_000L, state.incomeMinorUnits)
        }

    @Test
    fun `recent history resolves category and account names`() =
        runTest {
            val entries =
                listOf(
                    entry(id = 1, type = "EXPENSE", categoryId = 10, amountMinorUnits = 12_000, occurredOnEpochDay = today),
                )
            val viewModel = viewModel(listOf(bank), entries)

            val state = viewModel.uiState.first { !it.isLoading }

            val summary = state.recentEntries.single()
            assertEquals("Food", summary.categoryName)
            assertEquals("Bank", summary.accountName)
        }

    @Test
    fun `recording an expense lowers the balance and raises this-week spend`() =
        runTest {
            val accountDao = FakeAccountDao(listOf(bank))
            val entryDao = FakeEntryDao()
            val accountRepository = AccountRepository(accountDao, entryDao)
            val entryRepository =
                EntryRepository(entryDao, accountDao, FakeCategoryDao(listOf(food, salary)))
            val viewModel = HomeViewModel(accountRepository, entryRepository, clock)
            viewModel.uiState.first { !it.isLoading }

            entryRepository.createEntry(
                NewEntry(
                    type = EntryType.EXPENSE,
                    amountMinorUnits = 12_000,
                    accountId = 1,
                    categoryId = 10,
                    occurredOnEpochDay = today,
                ),
            )

            val state = viewModel.uiState.first { it.spendMinorUnits == 12_000L }
            assertEquals(88_000L, state.totalBalanceMinorUnits)
            assertEquals("Food", state.recentEntries.single().categoryName)
            assertEquals("Bank", state.recentEntries.single().accountName)
        }

    private fun viewModel(
        accounts: List<AccountEntity>,
        entries: List<EntryEntity> = emptyList(),
    ): HomeViewModel {
        val accountDao = FakeAccountDao(accounts)
        val entryDao = FakeEntryDao(entries)
        val accountRepository = AccountRepository(accountDao, entryDao)
        val entryRepository =
            EntryRepository(entryDao, accountDao, FakeCategoryDao(listOf(food, salary)))
        return HomeViewModel(accountRepository, entryRepository, clock)
    }

    private fun entry(
        id: Long,
        type: String,
        categoryId: Long,
        amountMinorUnits: Long,
        occurredOnEpochDay: Long,
    ) = EntryEntity(
        id = id,
        type = type,
        accountId = 1,
        categoryId = categoryId,
        amountMinorUnits = amountMinorUnits,
        occurredOnEpochDay = occurredOnEpochDay,
    )

    private fun epochDay(
        year: Int,
        month: Int,
        day: Int,
    ): Long = LocalDate.of(year, month, day).toEpochDay()
}
