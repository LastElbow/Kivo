package com.bustedelbow.kivo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** The side of the loading state's tonal container. */
private val ContainerSize = 72.dp

/** The diameter of the spinner inside the container. */
private val IndicatorSize = 32.dp

/**
 * Shown while the first database emission is loading: a spinner centred on a plain rounded tonal
 * container, matching the shape motif the redesigned empty state uses.
 */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        TonalShape(
            size = ContainerSize,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(IndicatorSize),
                strokeWidth = 3.dp,
            )
        }
    }
}
