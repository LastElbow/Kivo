package com.bustedelbow.kivo.ui.home

import com.bustedelbow.kivo.data.FakeAccountDao
import com.bustedelbow.kivo.data.FakeEntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Home shows the derived total across active Accounts and the active Account list (issue #3). */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `total sums the active accounts and excludes archived ones`() =
        runTest {
            val accounts =
                listOf(
                    AccountEntity(
                        id = 1,
                        name = "Bank",
                        type = "BANK",
                        openingBalanceMinorUnits = 100_000,
                    ),
                    AccountEntity(
                        id = 2,
                        name = "Retired",
                        type = "CASH",
                        openingBalanceMinorUnits = 50_000,
                        archived = true,
                    ),
                )
            val viewModel = HomeViewModel(AccountRepository(FakeAccountDao(accounts), FakeEntryDao()))

            val state = viewModel.uiState.first { !it.isLoading }

            assertEquals(100_000L, state.totalBalanceMinorUnits)
            assertEquals(listOf(1L), state.accounts.map { it.account.id })
        }
}
