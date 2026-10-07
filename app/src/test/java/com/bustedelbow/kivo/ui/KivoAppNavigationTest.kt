package com.bustedelbow.kivo.ui

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.ui.navigation.KivoDestination
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

/**
 * Verifies the bottom-navigation shell on the JVM: it starts on Home, and each tab switches
 * destination. Runs under Robolectric so navigation is covered without a device.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class KivoAppNavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun launchApp() {
        composeRule.setContent {
            KivoTheme {
                KivoApp()
            }
        }
    }

    @Test
    fun startsOnHome() {
        launchApp()

        composeRule.onNodeWithTag(KivoDestination.HOME.navItemTestTag).assertIsSelected()
        composeRule.onNodeWithContentDescription("Add account").assertDoesNotExist()
    }

    @Test
    fun switchesToAccounts() {
        launchApp()

        composeRule.onNodeWithTag(KivoDestination.ACCOUNTS.navItemTestTag).performClick()

        composeRule.onNodeWithTag(KivoDestination.ACCOUNTS.navItemTestTag).assertIsSelected()
        composeRule.onNodeWithContentDescription("Add account").assertExists()
    }

    @Test
    fun switchesToSettings() {
        launchApp()

        composeRule.onNodeWithTag(KivoDestination.SETTINGS.navItemTestTag).performClick()

        composeRule.onNodeWithTag(KivoDestination.SETTINGS.navItemTestTag).assertIsSelected()
    }
}
