package com.bustedelbow.kivo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/** The side of the leading shape, sized to sit inside an M3 list item without crowding it. */
private val ShapeSize = 40.dp

/** The glyph inside the leading shape. */
private val GlyphSize = 24.dp

/**
 * The plain rounded shape that leads an Account or Entry row, holding a type or category [iconRes]
 * (the redesign spec's "leading shape").
 *
 * The icon is decorative: the row's text names the type, and the amount's sign carries the money's
 * direction, so it takes no content description of its own.
 */
@Composable
fun LeadingTypeShape(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
) {
    TonalShape(
        size = ShapeSize,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(GlyphSize),
        )
    }
}
