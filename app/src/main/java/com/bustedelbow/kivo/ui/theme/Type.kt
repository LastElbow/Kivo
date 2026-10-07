package com.bustedelbow.kivo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Kivo's baseline type scale. Material 3's own `Typography()` is the baseline set; Kivo adds a
 * hand-rolled [EmphasizedTypography] beside it rather than replacing it (ADR-0007).
 */
internal val KivoTypography: Typography = Typography()

/**
 * The emphasized counterparts to [KivoTypography]'s baseline styles, hand-authored because stable
 * `material3` keeps its own emphasized getters and its 30-argument `Typography` constructor
 * `internal` (ADR-0005, ADR-0007).
 *
 * Each style keeps its baseline size and line height and changes **weight and tracking only**:
 * display/headline/title-large/body go Regular → Medium; title-medium/title-small and every label
 * go Medium → Bold. Baseline and emphasized are used together — emphasized marks the exception
 * (headlines, primary actions, selection), never the whole scale.
 */
@Immutable
class EmphasizedTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
)

internal val KivoEmphasizedTypography =
    EmphasizedTypography(
        displayLarge = TextStyle(fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        displayMedium = TextStyle(fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        displaySmall = TextStyle(fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        headlineSmall = TextStyle(fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium),
        titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp, fontWeight = FontWeight.Bold),
        titleSmall = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp, fontWeight = FontWeight.Bold),
        bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp, fontWeight = FontWeight.Medium),
        bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp, fontWeight = FontWeight.Medium),
        bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp, fontWeight = FontWeight.Medium),
        labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp, fontWeight = FontWeight.Bold),
        labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Bold),
        labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Bold),
    )

/** Holds the active [EmphasizedTypography]; [KivoTheme] provides it. */
internal val LocalEmphasizedTypography = staticCompositionLocalOf { KivoEmphasizedTypography }

/**
 * App-local type access. [emphasized] reads the styles [KivoTheme] provides, alongside
 * `MaterialTheme.typography`'s baseline set.
 */
object KivoType {
    /** The emphasized text styles, or [KivoEmphasizedTypography] outside a [KivoTheme]. */
    val emphasized: EmphasizedTypography
        @Composable @ReadOnlyComposable
        get() = LocalEmphasizedTypography.current
}

/** The OpenType feature that gives digits an equal width. */
const val TABULAR_FIGURES = "tnum"

/**
 * Returns this style with tabular figures, so money rendered through it keeps its digits aligned
 * and does not shift as the value changes.
 */
fun TextStyle.withTabularFigures(): TextStyle = copy(fontFeatureSettings = TABULAR_FIGURES)
