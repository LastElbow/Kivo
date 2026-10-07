package com.bustedelbow.kivo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.data.repository.EntryRepository
import com.bustedelbow.kivo.domain.ledger.Ledger
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.domain.model.Period
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate

/**
 * Drives Home: the total across active Accounts, this Week's Spend and Income, and recent history,
 * all read from derived Balances and Entries (ADR-0002).
 */
class HomeViewModel(
    accountRepository: AccountRepository,
    entryRepository: EntryRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        combine(
            accountRepository.observeAccountBalances(),
            entryRepository.observeEntries(),
            entryRepository.observeRecentEntries(),
        ) { balances, entries, recentEntries ->
            val week = Period.weekContaining(todayEpochDay())
            HomeUiState(
                accounts = balances,
                totalBalanceMinorUnits = Ledger.totalOf(balances),
                spendMinorUnits = Ledger.spendOf(entries, week),
                incomeMinorUnits = Ledger.incomeOf(entries, week),
                recentEntries = recentEntries,
                isLoading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState(),
        )

    private fun todayEpochDay(): Long = LocalDate.now(clock).toEpochDay()

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        fun factory(
            accountRepository: AccountRepository,
            entryRepository: EntryRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { HomeViewModel(accountRepository, entryRepository) }
            }
    }
}

/** The state Home renders: its Account list, derived total, this Week's Spend and Income, and recent history. */
data class HomeUiState(
    val accounts: List<AccountBalance> = emptyList(),
    val totalBalanceMinorUnits: Long = 0,
    val spendMinorUnits: Long = 0,
    val incomeMinorUnits: Long = 0,
    val recentEntries: List<EntrySummary> = emptyList(),
    val isLoading: Boolean = true,
)
