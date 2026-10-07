package com.bustedelbow.kivo.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.ui.components.PlaceholderScreen

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = stringResource(R.string.destination_home),
        modifier = modifier,
    )
}
