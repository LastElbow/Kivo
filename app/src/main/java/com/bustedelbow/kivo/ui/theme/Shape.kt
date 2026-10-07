package com.bustedelbow.kivo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Kivo's Material 3 Expressive shape scale.
 *
 * Stable `material3` exposes only five customisable [Shapes] slots — the increased and
 * extra-extra-large tokens are internal — so the expressive character is carried by
 * generous radii across those slots, mapping the spec's `8/12/16/20/28dp` onto them.
 */
val KivoShapes =
    Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp),
    )
