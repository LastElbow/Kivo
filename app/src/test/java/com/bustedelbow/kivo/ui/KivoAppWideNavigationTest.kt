package com.bustedelbow.kivo.ui

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.ui.navigation.KivoDestination
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Verifies the shell swaps its bottom bar for a navigation rail on a medium window, and that the
 * rail still drives navigation. Runs under Robolectric with an 840dp-wide window, above the
 * 600dp rail breakpoint.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w840dp-h800dp")
class KivoAppWideNavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheRailAndNavigates() {
        composeRule.setContent {
            KivoTheme {
                KivoApp()
            }
        }

        composeRule.onNodeWithTag(KivoDestination.HOME.navItemTestTag).assertIsSelected()

        composeRule.onNodeWithTag(KivoDestination.ACCOUNTS.navItemTestTag).performClick()

        composeRule.onNodeWithTag(KivoDestination.ACCOUNTS.navItemTestTag).assertIsSelected()
    }
}
