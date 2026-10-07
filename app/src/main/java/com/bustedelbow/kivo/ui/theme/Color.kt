package com.bustedelbow.kivo.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Kivo's brand colour scheme, hand-authored onto Material 3's standard role → tone slots (ADR-0007).
 *
 * Stable `material3` keeps its own `expressiveLightColorScheme`/`expressiveDarkColorScheme`
 * `internal`, so the palette is written out here. Every role is set explicitly; dynamic colour is
 * off unless [KivoTheme]'s opt-in is enabled, and dynamic colour never sees these values.
 *
 * Light follows tone 40 for `primary`, tone 90 for its container and tone 98 for `surface`; dark
 * follows tone 80, tone 30 and tone 10. `error` is static. The values and the role → tone mapping
 * are recorded in the redesign spec; the contrast pairs are asserted in `ColorSchemeTest`.
 */
internal val KivoLightColorScheme =
    lightColorScheme(
        primary = Color(0xFF3B4B9E),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFDFE0FF),
        onPrimaryContainer = Color(0xFF00105C),
        inversePrimary = Color(0xFFBAC3FF),
        secondary = Color(0xFF5A5D72),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFDFE1F9),
        onSecondaryContainer = Color(0xFF171B2C),
        tertiary = Color(0xFF006B54),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF7BF8D0),
        onTertiaryContainer = Color(0xFF002016),
        background = Color(0xFFFBF8FF),
        onBackground = Color(0xFF1B1B21),
        surface = Color(0xFFFBF8FF),
        onSurface = Color(0xFF1B1B21),
        surfaceVariant = Color(0xFFE3E1EC),
        onSurfaceVariant = Color(0xFF46464F),
        surfaceTint = Color(0xFF3B4B9E),
        inverseSurface = Color(0xFF303036),
        inverseOnSurface = Color(0xFFF3EFF7),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        outline = Color(0xFF777680),
        outlineVariant = Color(0xFFC7C5D0),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFFFBF8FF),
        surfaceDim = Color(0xFFDBD9E0),
        surfaceContainer = Color(0xFFEFEDF4),
        surfaceContainerHigh = Color(0xFFE9E7EF),
        surfaceContainerHighest = Color(0xFFE4E1E9),
        surfaceContainerLow = Color(0xFFF5F2FA),
        surfaceContainerLowest = Color(0xFFFFFFFF),
    )

internal val KivoDarkColorScheme =
    darkColorScheme(
        primary = Color(0xFFBAC3FF),
        onPrimary = Color(0xFF08218A),
        primaryContainer = Color(0xFF22308B),
        onPrimaryContainer = Color(0xFFDFE0FF),
        inversePrimary = Color(0xFF3B4B9E),
        secondary = Color(0xFFC3C5DD),
        onSecondary = Color(0xFF2C2F42),
        secondaryContainer = Color(0xFF424659),
        onSecondaryContainer = Color(0xFFDFE1F9),
        tertiary = Color(0xFF5EDBB4),
        onTertiary = Color(0xFF003828),
        tertiaryContainer = Color(0xFF00513E),
        onTertiaryContainer = Color(0xFF7BF8D0),
        background = Color(0xFF131318),
        onBackground = Color(0xFFE4E1E9),
        surface = Color(0xFF131318),
        onSurface = Color(0xFFE4E1E9),
        surfaceVariant = Color(0xFF46464F),
        onSurfaceVariant = Color(0xFFC7C5D0),
        surfaceTint = Color(0xFFBAC3FF),
        inverseSurface = Color(0xFFE4E1E9),
        inverseOnSurface = Color(0xFF303036),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF918F9A),
        outlineVariant = Color(0xFF46464F),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF39383F),
        surfaceDim = Color(0xFF131318),
        surfaceContainer = Color(0xFF1F1F25),
        surfaceContainerHigh = Color(0xFF2A2930),
        surfaceContainerHighest = Color(0xFF35343B),
        surfaceContainerLow = Color(0xFF1B1B21),
        surfaceContainerLowest = Color(0xFF0E0E13),
    )
