package com.bustedelbow.kivo.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.ui.LocalAppContainer
import com.bustedelbow.kivo.ui.components.AccountRow
import com.bustedelbow.kivo.ui.components.EmptyAccountsState
import com.bustedelbow.kivo.ui.components.LoadingState
import com.bustedelbow.kivo.ui.format.formatPhp

/**
 * Home: the total across active Accounts and the Account list, or an empty state that guides a
 * fresh install to create its first Account (issue #3).
 */
@Composable
fun HomeScreen(onCreateAccount: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel =
        viewModel(factory = HomeViewModel.factory(LocalAppContainer.current.accountRepository))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(uiState = uiState, onCreateAccount = onCreateAccount, modifier = modifier)
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> LoadingState(modifier)

        uiState.accounts.isEmpty() -> EmptyAccountsState(
            title = stringResource(R.string.home_empty_title),
            body = stringResource(R.string.home_empty_body),
            action = stringResource(R.string.home_empty_action),
            onAction = onCreateAccount,
            modifier = modifier,
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item(key = "total") { TotalBalanceCard(uiState.totalBalanceMinorUnits) }
            item(key = "accounts-title") {
                Text(
                    text = stringResource(R.string.home_accounts_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                )
            }
            items(uiState.accounts, key = { it.account.id }) { AccountRow(it) }
        }
    }
}

@Composable
private fun TotalBalanceCard(totalMinorUnits: Long, modifier: Modifier = Modifier) {
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
