package com.bustedelbow.kivo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * A plain rounded tonal shape with [content] centred inside — the motif the redesign uses for the
 * leading row glyph and for the empty and loading states (the M3 shape library is unavailable on
 * stable `material3`).
 */
@Composable
fun TonalShape(
    size: Dp,
    containerColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .size(size)
                .clip(shape)
                .background(containerColor),
        contentAlignment = Alignment.Center,
        content = content,
    )
}
