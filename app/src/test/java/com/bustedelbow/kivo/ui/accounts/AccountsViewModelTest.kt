package com.bustedelbow.kivo.ui.accounts

import com.bustedelbow.kivo.data.FakeAccountDao
import com.bustedelbow.kivo.data.FakeEntryDao
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.NewAccount
import com.bustedelbow.kivo.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Accounts lists derived Balances and drives Account creation (issue #3). */
@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `showing and dismissing the create dialog updates state`() =
        runTest {
            val viewModel = AccountsViewModel(AccountRepository(FakeAccountDao(), FakeEntryDao()))

            assertFalse(viewModel.uiState.first { !it.isLoading }.isCreateDialogVisible)

            viewModel.showCreateDialog()
            assertTrue(viewModel.uiState.first { it.isCreateDialogVisible }.isCreateDialogVisible)

            viewModel.dismissCreateDialog()
            assertFalse(viewModel.uiState.first { !it.isCreateDialogVisible }.isCreateDialogVisible)
        }

    @Test
    fun `createAccount stores the account and closes the dialog`() =
        runTest {
            val accountDao = FakeAccountDao()
            val viewModel = AccountsViewModel(AccountRepository(accountDao, FakeEntryDao()))
            viewModel.showCreateDialog()

            viewModel.createAccount(
                NewAccount(name = "Cash", type = AccountType.CASH, openingBalanceMinorUnits = 5_000),
            )

            val state = viewModel.uiState.first { it.accounts.isNotEmpty() }
            assertFalse(state.isCreateDialogVisible)
            assertEquals(
                "Cash",
                state.accounts
                    .single()
                    .account.name,
            )
        }
}
