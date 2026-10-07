package com.bustedelbow.kivo.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.NewAccount
import com.bustedelbow.kivo.ui.LocalAppContainer
import com.bustedelbow.kivo.ui.components.AccountRow
import com.bustedelbow.kivo.ui.components.EmptyAccountsState
import com.bustedelbow.kivo.ui.components.LoadingState
import com.bustedelbow.kivo.ui.theme.KivoType

/** The gap between the contained Account rows. */
private val AccountRowGap = 8.dp

/** The screen gutter the contained Account rows keep. */
private val AccountsGutter = 16.dp

/**
 * Accounts: the app bar, the contained list of Accounts with their derived Balances, and Account
 * creation in a modal bottom sheet.
 */
@Composable
fun AccountsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val viewModel: AccountsViewModel =
        viewModel(factory = AccountsViewModel.factory(LocalAppContainer.current.accountRepository))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AccountsContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onAddAccount = viewModel::showCreateSheet,
        onDismissCreateAccount = viewModel::dismissCreateSheet,
        onCreateAccount = viewModel::createAccount,
        onArchive = viewModel::archiveAccount,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountsContent(
    uiState: AccountsUiState,
    contentPadding: PaddingValues,
    onAddAccount: () -> Unit,
    onDismissCreateAccount: () -> Unit,
    onCreateAccount: (NewAccount) -> Unit,
    onArchive: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(contentPadding)
                    .padding(contentPadding),
        ) {
            // The Scaffold already applied the system-bar insets, so the bar itself adds none.
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.destination_accounts),
                        style = KivoType.emphasized.titleLarge,
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> LoadingState()

                    uiState.accounts.isEmpty() ->
                        EmptyAccountsState(
                            title = stringResource(R.string.accounts_empty_title),
                            body = stringResource(R.string.accounts_empty_body),
                            action = stringResource(R.string.accounts_empty_action),
                            onAction = onAddAccount,
                        )

                    else ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = AccountsGutter, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(AccountRowGap),
                        ) {
                            items(uiState.accounts, key = { it.account.id }) { accountBalance ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    AccountRow(
                                        accountBalance = accountBalance,
                                        onArchive = { onArchive(accountBalance.account.id) },
                                        // The Card is the segment's container; the row draws
                                        // no background of its own so that container shows.
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                    )
                                }
                            }
                        }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddAccount,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(contentPadding)
                    .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.accounts_add_fab),
            )
        }
    }

    if (uiState.isCreateSheetVisible) {
        CreateAccountSheet(
            onDismiss = onDismissCreateAccount,
            onConfirm = onCreateAccount,
        )
    }
}
