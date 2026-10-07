package com.bustedelbow.kivo.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.ui.LocalAppContainer
import com.bustedelbow.kivo.ui.components.AccountRow
import com.bustedelbow.kivo.ui.components.EmptyAccountsState
import com.bustedelbow.kivo.ui.components.EntryRow
import com.bustedelbow.kivo.ui.components.LoadingState
import com.bustedelbow.kivo.ui.components.withGutters
import com.bustedelbow.kivo.ui.format.formatPhp

/**
 * Home: the total across active Accounts, this Week's Spend and Income, the Account list, and
 * recent history, or an empty state that guides a fresh install to create its first Account
 * (issues #3, #4).
 */
@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onCreateAccount: () -> Unit,
    onAddEntry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val viewModel: HomeViewModel =
        viewModel(factory = HomeViewModel.factory(container.accountRepository, container.entryRepository))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onCreateAccount = onCreateAccount,
        onAddEntry = onAddEntry,
        modifier = modifier,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    contentPadding: PaddingValues,
    onCreateAccount: () -> Unit,
    onAddEntry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> LoadingState(modifier)

        uiState.accounts.isEmpty() ->
            EmptyAccountsState(
                title = stringResource(R.string.home_empty_title),
                body = stringResource(R.string.home_empty_body),
                action = stringResource(R.string.home_empty_action),
                onAction = onCreateAccount,
                modifier = modifier.padding(contentPadding),
            )

        else ->
            Box(modifier = modifier.fillMaxSize()) {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .consumeWindowInsets(contentPadding),
                    contentPadding = contentPadding.withGutters(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    item(key = "total") { TotalBalanceCard(uiState.totalBalanceMinorUnits) }
                    item(key = "week") {
                        WeekSummaryCard(
                            spendMinorUnits = uiState.spendMinorUnits,
                            incomeMinorUnits = uiState.incomeMinorUnits,
                        )
                    }
                    item(key = "accounts-title") { SectionTitle(stringResource(R.string.home_accounts_title)) }
                    items(uiState.accounts, key = { it.account.id }) { AccountRow(it) }
                    item(key = "recent-title") { SectionTitle(stringResource(R.string.home_recent_title)) }
                    recentHistory(uiState.recentEntries)
                }

                FloatingActionButton(
                    onClick = onAddEntry,
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(contentPadding)
                            .padding(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.home_add_entry_fab),
                    )
                }
            }
    }
}

/** The recent Entries, or a prompt when there are none yet. */
private fun LazyListScope.recentHistory(entries: List<EntrySummary>) {
    if (entries.isEmpty()) {
        item(key = "recent-empty") {
            Text(
                text = stringResource(R.string.home_recent_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    } else {
        items(entries, key = { it.entry.id }) { EntryRow(it) }
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun TotalBalanceCard(
    totalMinorUnits: Long,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.home_total_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatPhp(totalMinorUnits),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Composable
private fun WeekSummaryCard(
    spendMinorUnits: Long,
    incomeMinorUnits: Long,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(20.dp)) {
            AmountColumn(
                label = stringResource(R.string.home_spend_label),
                amountMinorUnits = spendMinorUnits,
                modifier = Modifier.weight(1f),
            )
            AmountColumn(
                label = stringResource(R.string.home_income_label),
                amountMinorUnits = incomeMinorUnits,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AmountColumn(
    label: String,
    amountMinorUnits: Long,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatPhp(amountMinorUnits),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
