package com.bustedelbow.kivo.ui.accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.bustedelbow.kivo.ui.components.withGutters

/**
 * Accounts: the list with derived Balances and Account creation (name, type, Opening balance).
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
        onAddAccount = viewModel::showCreateDialog,
        onDismissCreateAccount = viewModel::dismissCreateDialog,
        onCreateAccount = viewModel::createAccount,
        modifier = modifier,
    )
}

@Composable
private fun AccountsContent(
    uiState: AccountsUiState,
    contentPadding: PaddingValues,
    onAddAccount: () -> Unit,
    onDismissCreateAccount: () -> Unit,
    onCreateAccount: (NewAccount) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LoadingState()

            uiState.accounts.isEmpty() ->
                EmptyAccountsState(
                    title = stringResource(R.string.accounts_empty_title),
                    body = stringResource(R.string.accounts_empty_body),
                    action = stringResource(R.string.accounts_empty_action),
                    onAction = onAddAccount,
                    modifier = Modifier.padding(contentPadding),
                )

            else ->
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .consumeWindowInsets(contentPadding),
                    contentPadding = contentPadding.withGutters(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(uiState.accounts, key = { it.account.id }) { AccountRow(it) }
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

    if (uiState.isCreateDialogVisible) {
        CreateAccountDialog(
            onDismiss = onDismissCreateAccount,
            onConfirm = onCreateAccount,
        )
    }
}
