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
 * Opening balance) in a modal bottom sheet.
 */
class AccountsViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val isCreateSheetVisible = MutableStateFlow(false)

    val uiState: StateFlow<AccountsUiState> =
        combine(
            accountRepository.observeAccountBalances(),
            isCreateSheetVisible,
        ) { balances, sheetVisible ->
            AccountsUiState(
                accounts = balances,
                isLoading = false,
                isCreateSheetVisible = sheetVisible,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = AccountsUiState(),
        )

    fun showCreateSheet() {
        isCreateSheetVisible.value = true
    }

    fun dismissCreateSheet() {
        isCreateSheetVisible.value = false
    }

    fun createAccount(account: NewAccount) {
        viewModelScope.launch {
            accountRepository.createAccount(account)
            isCreateSheetVisible.value = false
        }
    }

    /** Archives the Account with [id], retiring it from the list while its Entries stay readable (ADR-0004). */
    fun archiveAccount(id: Long) {
        viewModelScope.launch {
            accountRepository.archiveAccount(id)
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

/** The state Accounts renders: its Account list and whether the creation sheet is open. */
data class AccountsUiState(
    val accounts: List<AccountBalance> = emptyList(),
    val isLoading: Boolean = true,
    val isCreateSheetVisible: Boolean = false,
)
