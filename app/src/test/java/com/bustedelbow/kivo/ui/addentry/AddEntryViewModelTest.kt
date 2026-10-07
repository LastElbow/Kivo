package com.bustedelbow.kivo.ui.addentry

import com.bustedelbow.kivo.data.FakeAccountDao
import com.bustedelbow.kivo.data.FakeCategoryDao
import com.bustedelbow.kivo.data.FakeEntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.CategoryEntity
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.data.repository.CategoryRepository
import com.bustedelbow.kivo.data.repository.EntryRepository
import com.bustedelbow.kivo.domain.model.EntryType
import com.bustedelbow.kivo.domain.model.EntryValidationError
import com.bustedelbow.kivo.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * The Add Entry flow records an Expense or Income when valid and blocks saving otherwise
 * (issue #4).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddEntryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(Instant.parse("2026-10-07T09:00:00Z"), ZoneOffset.UTC)
    private val today = LocalDate.of(2026, 10, 7).toEpochDay()

    private val accounts =
        listOf(AccountEntity(id = 1, name = "Bank", type = "BANK", openingBalanceMinorUnits = 100_000))
    private val categories =
        listOf(
            CategoryEntity(id = 10, name = "Food", type = "EXPENSE"),
            CategoryEntity(id = 11, name = "Salary", type = "INCOME"),
        )

    @Test
    fun `a valid expense is recorded`() =
        runTest {
            val entryDao = FakeEntryDao()
            val viewModel = viewModel(entryDao)

            viewModel.setAmount("125.00")
            viewModel.selectAccount(1)
            viewModel.selectCategory(10)
            viewModel.uiState.first { it.canSave }

            viewModel.save()

            val stored = entryDao.inserted.single()
            assertEquals("EXPENSE", stored.type)
            assertEquals(12_500L, stored.amountMinorUnits)
            assertEquals(1L, stored.accountId)
            assertEquals(10L, stored.categoryId)
            assertEquals(today, stored.occurredOnEpochDay)
        }

    @Test
    fun `a valid income is recorded`() =
        runTest {
            val entryDao = FakeEntryDao()
            val viewModel = viewModel(entryDao)

            viewModel.setType(EntryType.INCOME)
            viewModel.setAmount("2,000")
            viewModel.selectAccount(1)
            viewModel.selectCategory(11)
            viewModel.uiState.first { it.canSave }

            viewModel.save()

            val stored = entryDao.inserted.single()
            assertEquals("INCOME", stored.type)
            assertEquals(200_000L, stored.amountMinorUnits)
            assertEquals(11L, stored.categoryId)
        }

    @Test
    fun `saving is blocked until amount, account and category are valid`() =
        runTest {
            val entryDao = FakeEntryDao()
            val viewModel = viewModel(entryDao)

            assertFalse(viewModel.uiState.first { !it.isLoading }.canSave)

            viewModel.setAmount("125.00")
            assertFalse(viewModel.uiState.first { it.amount == "125.00" }.canSave)

            viewModel.selectAccount(1)
            assertFalse(viewModel.uiState.first { it.selectedAccountId == 1L }.canSave)

            viewModel.selectCategory(10)
            assertTrue(viewModel.uiState.first { it.selectedCategoryId == 10L }.canSave)

            viewModel.save()
            assertEquals(1, entryDao.inserted.size)
        }

    @Test
    fun `save does nothing while a required field is missing or the amount is not positive`() =
        runTest {
            val entryDao = FakeEntryDao()
            val viewModel = viewModel(entryDao)

            viewModel.uiState.first { !it.isLoading }
            viewModel.save()

            viewModel.setAmount("0")
            viewModel.uiState.first { it.amount == "0" }
            viewModel.save()

            viewModel.setAmount("125.00")
            viewModel.uiState.first { it.amount == "125.00" }
            viewModel.save()

            assertTrue(entryDao.inserted.isEmpty())
        }

    @Test
    fun `a future date blocks saving`() =
        runTest {
            val entryDao = FakeEntryDao()
            val viewModel = viewModel(entryDao)

            viewModel.setAmount("125.00")
            viewModel.selectAccount(1)
            viewModel.selectCategory(10)
            viewModel.setDate(today + 1)

            val state = viewModel.uiState.first { it.occurredOnEpochDay == today + 1 }
            assertTrue(state.errors.contains(EntryValidationError.DATE_IN_FUTURE))
            assertFalse(state.canSave)

            viewModel.save()
            assertTrue(entryDao.inserted.isEmpty())
        }

    @Test
    fun `switching kind filters to its categories and clears the selection`() =
        runTest {
            val viewModel = viewModel()

            viewModel.setType(EntryType.INCOME)

            val state = viewModel.uiState.first { it.type == EntryType.INCOME && it.categories.isNotEmpty() }
            assertEquals(listOf("Salary"), state.categories.map { it.name })
            assertNull(state.selectedCategoryId)
        }

    private fun viewModel(entryDao: FakeEntryDao = FakeEntryDao()): AddEntryViewModel {
        val accountRepository = AccountRepository(FakeAccountDao(accounts), FakeEntryDao())
        val categoryRepository = CategoryRepository(FakeCategoryDao(categories))
        val entryRepository = EntryRepository(entryDao, FakeAccountDao(accounts), FakeCategoryDao(categories))
        return AddEntryViewModel(entryRepository, accountRepository, categoryRepository, clock)
    }
}
