package com.bustedelbow.kivo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.domain.ledger.Ledger
import com.bustedelbow.kivo.domain.model.AccountBalance
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Drives Home: the total across active Accounts and the Account list, both read from derived
 * Balances (ADR-0002).
 */
class HomeViewModel(
    accountRepository: AccountRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        accountRepository
            .observeAccountBalances()
            .map { balances ->
                HomeUiState(
                    accounts = balances,
                    totalBalanceMinorUnits = Ledger.totalOf(balances),
                    isLoading = false,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = HomeUiState(),
            )

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        fun factory(repository: AccountRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { HomeViewModel(repository) }
            }
    }
}

/** The state Home renders: its Account list and the derived total across them. */
data class HomeUiState(
    val accounts: List<AccountBalance> = emptyList(),
    val totalBalanceMinorUnits: Long = 0,
    val isLoading: Boolean = true,
)
