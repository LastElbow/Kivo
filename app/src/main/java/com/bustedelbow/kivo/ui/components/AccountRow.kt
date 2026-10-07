package com.bustedelbow.kivo.ui.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.ui.format.formatPhp

/** One Account with its name, type and derived Balance, as listed on Home and Accounts. */
@Composable
fun AccountRow(accountBalance: AccountBalance, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = accountBalance.account.name) },
        supportingContent = { Text(text = stringResource(accountBalance.account.type.labelRes())) },
        trailingContent = {
            Text(
                text = formatPhp(accountBalance.balanceMinorUnits),
                style = MaterialTheme.typography.titleMedium,
            )
        },
    )
}
