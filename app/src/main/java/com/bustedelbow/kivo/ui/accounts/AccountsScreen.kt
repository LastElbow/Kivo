package com.bustedelbow.kivo.ui.accounts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.ui.components.PlaceholderScreen

@Composable
fun AccountsScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = stringResource(R.string.destination_accounts),
        modifier = modifier,
    )
}
