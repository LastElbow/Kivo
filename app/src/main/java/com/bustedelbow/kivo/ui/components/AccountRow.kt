package com.bustedelbow.kivo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.ui.theme.KivoType

/** Stable tag for the per-Account actions button, shared with UI tests. */
internal const val ACCOUNT_ACTIONS_TEST_TAG = "account_actions"

/**
 * One Account with its type shape, name, type and derived Balance, as listed on Home and Accounts.
 * The Balance renders emphasized in tabular figures. When [onArchive] is given the row also offers
 * Archiving; Accounts passes it, Home leaves it off.
 */
@Composable
fun AccountRow(
    accountBalance: AccountBalance,
    modifier: Modifier = Modifier,
    onArchive: (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier,
        leadingContent = { LeadingTypeShape(iconRes = accountBalance.account.type.iconRes()) },
        headlineContent = { Text(text = accountBalance.account.name) },
        supportingContent = { Text(text = stringResource(accountBalance.account.type.labelRes())) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AmountText(
                    amountMinorUnits = accountBalance.balanceMinorUnits,
                    kind = AmountKind.NEUTRAL,
                    style = KivoType.emphasized.titleMedium,
                )
                if (onArchive != null) {
                    AccountActionsMenu(onArchive = onArchive)
                }
            }
        },
    )
}

/** The per-Account overflow menu; for now it holds Archiving alone. */
@Composable
private fun AccountActionsMenu(onArchive: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.testTag(ACCOUNT_ACTIONS_TEST_TAG),
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.accounts_actions),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.accounts_archive_action)) },
                onClick = {
                    expanded = false
                    onArchive()
                },
            )
        }
    }
}
