package com.bustedelbow.kivo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** A role, its expected value, and the actual value the scheme carries. */
private data class RoleExpectation(
    val role: String,
    val expected: Color,
    val actual: Color,
)

/** The lighter text pair from the spec's contrast-list; asserted by [ColorSchemeTest]. */
private data class ContrastPair(
    val description: String,
    val foreground: Color,
    val background: Color,
    val minimumRatio: Double,
)

/**
 * Pins Kivo's hand-authored brand scheme (ADR-0007): every standard role holds the value the
 * redesign spec specifies, and the spec's required contrast pairs stay legible.
 */
class ColorSchemeTest {
    @Test
    fun lightSchemeImplementsEveryRole() {
        assertRoles(lightExpectations(KivoLightColorScheme))
    }

    @Test
    fun darkSchemeImplementsEveryRole() {
        assertRoles(darkExpectations(KivoDarkColorScheme))
    }

    @Test
    fun lightSchemeMeetsContrastForListedPairs() {
        assertContrast(contrastPairs(KivoLightColorScheme))
    }

    @Test
    fun darkSchemeMeetsContrastForListedPairs() {
        assertContrast(contrastPairs(KivoDarkColorScheme))
    }

    private fun assertRoles(expectations: List<RoleExpectation>) {
        expectations.forEach { (role, expected, actual) ->
            assertEquals("role $role", expected, actual)
        }
    }

    private fun assertContrast(pairs: List<ContrastPair>) {
        pairs.forEach { pair ->
            val ratio = contrastRatio(pair.foreground, pair.background)
            assertTrue(
                "${pair.description} is $ratio:1, needs ${pair.minimumRatio}:1",
                ratio >= pair.minimumRatio,
            )
        }
    }

    private fun lightExpectations(scheme: ColorScheme) =
        listOf(
            RoleExpectation("primary", Color(0xFF3B4B9E), scheme.primary),
            RoleExpectation("onPrimary", Color(0xFFFFFFFF), scheme.onPrimary),
            RoleExpectation("primaryContainer", Color(0xFFDFE0FF), scheme.primaryContainer),
            RoleExpectation("onPrimaryContainer", Color(0xFF00105C), scheme.onPrimaryContainer),
            RoleExpectation("inversePrimary", Color(0xFFBAC3FF), scheme.inversePrimary),
            RoleExpectation("secondary", Color(0xFF5A5D72), scheme.secondary),
            RoleExpectation("onSecondary", Color(0xFFFFFFFF), scheme.onSecondary),
            RoleExpectation("secondaryContainer", Color(0xFFDFE1F9), scheme.secondaryContainer),
            RoleExpectation("onSecondaryContainer", Color(0xFF171B2C), scheme.onSecondaryContainer),
            RoleExpectation("tertiary", Color(0xFF006B54), scheme.tertiary),
            RoleExpectation("onTertiary", Color(0xFFFFFFFF), scheme.onTertiary),
            RoleExpectation("tertiaryContainer", Color(0xFF7BF8D0), scheme.tertiaryContainer),
            RoleExpectation("onTertiaryContainer", Color(0xFF002016), scheme.onTertiaryContainer),
            RoleExpectation("background", Color(0xFFFBF8FF), scheme.background),
            RoleExpectation("onBackground", Color(0xFF1B1B21), scheme.onBackground),
            RoleExpectation("surface", Color(0xFFFBF8FF), scheme.surface),
            RoleExpectation("onSurface", Color(0xFF1B1B21), scheme.onSurface),
            RoleExpectation("surfaceVariant", Color(0xFFE3E1EC), scheme.surfaceVariant),
            RoleExpectation("onSurfaceVariant", Color(0xFF46464F), scheme.onSurfaceVariant),
            RoleExpectation("surfaceTint", Color(0xFF3B4B9E), scheme.surfaceTint),
            RoleExpectation("inverseSurface", Color(0xFF303036), scheme.inverseSurface),
            RoleExpectation("inverseOnSurface", Color(0xFFF3EFF7), scheme.inverseOnSurface),
            RoleExpectation("error", Color(0xFFBA1A1A), scheme.error),
            RoleExpectation("onError", Color(0xFFFFFFFF), scheme.onError),
            RoleExpectation("errorContainer", Color(0xFFFFDAD6), scheme.errorContainer),
            RoleExpectation("onErrorContainer", Color(0xFF410002), scheme.onErrorContainer),
            RoleExpectation("outline", Color(0xFF777680), scheme.outline),
            RoleExpectation("outlineVariant", Color(0xFFC7C5D0), scheme.outlineVariant),
            RoleExpectation("scrim", Color(0xFF000000), scheme.scrim),
            RoleExpectation("surfaceBright", Color(0xFFFBF8FF), scheme.surfaceBright),
            RoleExpectation("surfaceDim", Color(0xFFDBD9E0), scheme.surfaceDim),
            RoleExpectation("surfaceContainer", Color(0xFFEFEDF4), scheme.surfaceContainer),
            RoleExpectation("surfaceContainerHigh", Color(0xFFE9E7EF), scheme.surfaceContainerHigh),
            RoleExpectation("surfaceContainerHighest", Color(0xFFE4E1E9), scheme.surfaceContainerHighest),
            RoleExpectation("surfaceContainerLow", Color(0xFFF5F2FA), scheme.surfaceContainerLow),
            RoleExpectation("surfaceContainerLowest", Color(0xFFFFFFFF), scheme.surfaceContainerLowest),
        )

    private fun darkExpectations(scheme: ColorScheme) =
        listOf(
            RoleExpectation("primary", Color(0xFFBAC3FF), scheme.primary),
            RoleExpectation("onPrimary", Color(0xFF08218A), scheme.onPrimary),
            RoleExpectation("primaryContainer", Color(0xFF22308B), scheme.primaryContainer),
            RoleExpectation("onPrimaryContainer", Color(0xFFDFE0FF), scheme.onPrimaryContainer),
            RoleExpectation("inversePrimary", Color(0xFF3B4B9E), scheme.inversePrimary),
            RoleExpectation("secondary", Color(0xFFC3C5DD), scheme.secondary),
            RoleExpectation("onSecondary", Color(0xFF2C2F42), scheme.onSecondary),
            RoleExpectation("secondaryContainer", Color(0xFF424659), scheme.secondaryContainer),
            RoleExpectation("onSecondaryContainer", Color(0xFFDFE1F9), scheme.onSecondaryContainer),
            RoleExpectation("tertiary", Color(0xFF5EDBB4), scheme.tertiary),
            RoleExpectation("onTertiary", Color(0xFF003828), scheme.onTertiary),
            RoleExpectation("tertiaryContainer", Color(0xFF00513E), scheme.tertiaryContainer),
            RoleExpectation("onTertiaryContainer", Color(0xFF7BF8D0), scheme.onTertiaryContainer),
            RoleExpectation("background", Color(0xFF131318), scheme.background),
            RoleExpectation("onBackground", Color(0xFFE4E1E9), scheme.onBackground),
            RoleExpectation("surface", Color(0xFF131318), scheme.surface),
            RoleExpectation("onSurface", Color(0xFFE4E1E9), scheme.onSurface),
            RoleExpectation("surfaceVariant", Color(0xFF46464F), scheme.surfaceVariant),
            RoleExpectation("onSurfaceVariant", Color(0xFFC7C5D0), scheme.onSurfaceVariant),
            RoleExpectation("surfaceTint", Color(0xFFBAC3FF), scheme.surfaceTint),
            RoleExpectation("inverseSurface", Color(0xFFE4E1E9), scheme.inverseSurface),
            RoleExpectation("inverseOnSurface", Color(0xFF303036), scheme.inverseOnSurface),
            RoleExpectation("error", Color(0xFFFFB4AB), scheme.error),
            RoleExpectation("onError", Color(0xFF690005), scheme.onError),
            RoleExpectation("errorContainer", Color(0xFF93000A), scheme.errorContainer),
            RoleExpectation("onErrorContainer", Color(0xFFFFDAD6), scheme.onErrorContainer),
            RoleExpectation("outline", Color(0xFF918F9A), scheme.outline),
            RoleExpectation("outlineVariant", Color(0xFF46464F), scheme.outlineVariant),
            RoleExpectation("scrim", Color(0xFF000000), scheme.scrim),
            RoleExpectation("surfaceBright", Color(0xFF39383F), scheme.surfaceBright),
            RoleExpectation("surfaceDim", Color(0xFF131318), scheme.surfaceDim),
            RoleExpectation("surfaceContainer", Color(0xFF1F1F25), scheme.surfaceContainer),
            RoleExpectation("surfaceContainerHigh", Color(0xFF2A2930), scheme.surfaceContainerHigh),
            RoleExpectation("surfaceContainerHighest", Color(0xFF35343B), scheme.surfaceContainerHighest),
            RoleExpectation("surfaceContainerLow", Color(0xFF1B1B21), scheme.surfaceContainerLow),
            RoleExpectation("surfaceContainerLowest", Color(0xFF0E0E13), scheme.surfaceContainerLowest),
        )

    private fun contrastPairs(scheme: ColorScheme) =
        listOf(
            ContrastPair("onSurface on surface", scheme.onSurface, scheme.surface, 4.5),
            ContrastPair("onSurfaceVariant on surface", scheme.onSurfaceVariant, scheme.surface, 4.5),
            ContrastPair("onPrimary on primary", scheme.onPrimary, scheme.primary, 4.5),
            ContrastPair(
                "onPrimaryContainer on primaryContainer",
                scheme.onPrimaryContainer,
                scheme.primaryContainer,
                4.5,
            ),
            ContrastPair("tertiary on surface", scheme.tertiary, scheme.surface, 4.5),
            ContrastPair("outline on surface", scheme.outline, scheme.surface, 3.0),
        )

    /** WCAG 2.1 relative luminance of an sRGB [color]. */
    private fun relativeLuminance(color: Color): Double {
        val red = linearized(color.red)
        val green = linearized(color.green)
        val blue = linearized(color.blue)
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue
    }

    private fun linearized(channel: Float): Double {
        val value = channel.toDouble()
        return if (value <= 0.03928) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
    }

    /** WCAG 2.1 contrast ratio between two opaque colours. */
    private fun contrastRatio(
        first: Color,
        second: Color,
    ): Double {
        val firstLuminance = relativeLuminance(first)
        val secondLuminance = relativeLuminance(second)
        val lighter = max(firstLuminance, secondLuminance)
        val darker = min(firstLuminance, secondLuminance)
        return (lighter + 0.05) / (darker + 0.05)
    }
}
