package com.bustedelbow.kivo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bustedelbow.kivo.ui.theme.KivoType

/** Temporary content for a destination whose real UI lands in a later slice. */
@Composable
fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = KivoType.emphasized.headlineMedium,
        )
    }
}
