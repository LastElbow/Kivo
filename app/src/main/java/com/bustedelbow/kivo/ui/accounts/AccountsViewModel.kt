package com.bustedelbow.kivo.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.NewAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Drives Accounts: the list with derived Balances (ADR-0002) and Account creation (name, type,
 * Opening balance).
 */
class AccountsViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val isCreateDialogVisible = MutableStateFlow(false)

    val uiState: StateFlow<AccountsUiState> =
        combine(
            accountRepository.observeAccountBalances(),
            isCreateDialogVisible,
        ) { balances, dialogVisible ->
            AccountsUiState(
                accounts = balances,
                isLoading = false,
                isCreateDialogVisible = dialogVisible,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = AccountsUiState(),
        )

    fun showCreateDialog() {
        isCreateDialogVisible.value = true
    }

    fun dismissCreateDialog() {
        isCreateDialogVisible.value = false
    }

    fun createAccount(account: NewAccount) {
        viewModelScope.launch {
            accountRepository.createAccount(account)
            isCreateDialogVisible.value = false
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        fun factory(repository: AccountRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { AccountsViewModel(repository) }
            }
    }
}

/** The state Accounts renders: its Account list and whether the creation dialog is open. */
data class AccountsUiState(
    val accounts: List<AccountBalance> = emptyList(),
    val isLoading: Boolean = true,
    val isCreateDialogVisible: Boolean = false,
)
