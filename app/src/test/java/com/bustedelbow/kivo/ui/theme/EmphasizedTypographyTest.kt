package com.bustedelbow.kivo.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Test

/** One emphasized style beside the baseline it must pair with, plus its expected weight/tracking. */
private data class TypeExpectation(
    val name: String,
    val emphasized: TextStyle,
    val baseline: TextStyle,
    val weight: FontWeight,
    val trackingEm: Double,
)

/**
 * Pins the hand-rolled emphasized type set (ADR-0007): same size and line height as Material 3's
 * baseline, only weight and tracking change, and money can carry tabular figures.
 */
class EmphasizedTypographyTest {
    @Test
    fun emphasizedStylesKeepBaselineSizeAndLineHeight() {
        expectations().forEach { expectation ->
            assertEquals(
                "${expectation.name} font size",
                expectation.baseline.fontSize,
                expectation.emphasized.fontSize,
            )
            assertEquals(
                "${expectation.name} line height",
                expectation.baseline.lineHeight,
                expectation.emphasized.lineHeight,
            )
        }
    }

    @Test
    fun emphasizedStylesUseThePublishedWeightAndTracking() {
        expectations().forEach { expectation ->
            assertEquals("${expectation.name} weight", expectation.weight, expectation.emphasized.fontWeight)
            assertEquals(
                "${expectation.name} tracking",
                expectation.trackingEm.toFloat(),
                expectation.emphasized.letterSpacing.value,
                0.0001f,
            )
        }
    }

    @Test
    fun withTabularFiguresRequestsTheTnumFeature() {
        val style = TextStyle(fontFeatureSettings = "liga").withTabularFigures()
        assertEquals(TABULAR_FIGURES, style.fontFeatureSettings)
    }

    private fun expectations() =
        listOf(
            TypeExpectation("displayLarge", KivoEmphasizedTypography.displayLarge, KivoTypography.displayLarge, FontWeight.Medium, 0.0),
            TypeExpectation("displayMedium", KivoEmphasizedTypography.displayMedium, KivoTypography.displayMedium, FontWeight.Medium, 0.0),
            TypeExpectation("displaySmall", KivoEmphasizedTypography.displaySmall, KivoTypography.displaySmall, FontWeight.Medium, 0.0),
            TypeExpectation("headlineLarge", KivoEmphasizedTypography.headlineLarge, KivoTypography.headlineLarge, FontWeight.Medium, 0.0),
            TypeExpectation(
                "headlineMedium",
                KivoEmphasizedTypography.headlineMedium,
                KivoTypography.headlineMedium,
                FontWeight.Medium,
                0.0,
            ),
            TypeExpectation("headlineSmall", KivoEmphasizedTypography.headlineSmall, KivoTypography.headlineSmall, FontWeight.Medium, 0.0),
            TypeExpectation("titleLarge", KivoEmphasizedTypography.titleLarge, KivoTypography.titleLarge, FontWeight.Medium, 0.0),
            TypeExpectation("titleMedium", KivoEmphasizedTypography.titleMedium, KivoTypography.titleMedium, FontWeight.Bold, 0.15),
            TypeExpectation("titleSmall", KivoEmphasizedTypography.titleSmall, KivoTypography.titleSmall, FontWeight.Bold, 0.10),
            TypeExpectation("bodyLarge", KivoEmphasizedTypography.bodyLarge, KivoTypography.bodyLarge, FontWeight.Medium, 0.15),
            TypeExpectation("bodyMedium", KivoEmphasizedTypography.bodyMedium, KivoTypography.bodyMedium, FontWeight.Medium, 0.25),
            TypeExpectation("bodySmall", KivoEmphasizedTypography.bodySmall, KivoTypography.bodySmall, FontWeight.Medium, 0.40),
            TypeExpectation("labelLarge", KivoEmphasizedTypography.labelLarge, KivoTypography.labelLarge, FontWeight.Bold, 0.10),
            TypeExpectation("labelMedium", KivoEmphasizedTypography.labelMedium, KivoTypography.labelMedium, FontWeight.Bold, 0.50),
            TypeExpectation("labelSmall", KivoEmphasizedTypography.labelSmall, KivoTypography.labelSmall, FontWeight.Bold, 0.50),
        )
}
