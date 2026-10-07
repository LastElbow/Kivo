package com.bustedelbow.kivo.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.data.preferences.AppearanceMode
import com.bustedelbow.kivo.data.preferences.AppearanceSettings
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

/** Settings shows its Appearance and About sections and reports each choice (issue #12). */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun show(
        uiState: AppearanceSettings = AppearanceSettings(),
        onModeSelected: (AppearanceMode) -> Unit = {},
        onDynamicColorChange: (Boolean) -> Unit = {},
    ) {
        composeRule.setContent {
            KivoTheme {
                SettingsContent(
                    uiState = uiState,
                    contentPadding = PaddingValues(0.dp),
                    onModeSelected = onModeSelected,
                    onDynamicColorChange = onDynamicColorChange,
                )
            }
        }
    }

    @Test
    fun `shows the Appearance and About sections`() {
        show()

        composeRule.onNodeWithText("Appearance").assertExists()
        composeRule.onNodeWithText("Light").assertExists()
        composeRule.onNodeWithText("Dark").assertExists()
        composeRule.onNodeWithText("System").assertExists()
        composeRule.onNodeWithText("Dynamic colour").assertExists()
        composeRule.onNodeWithText("About").assertExists()
        composeRule.onNodeWithText("Kivo").assertExists()
    }

    @Test
    fun `selecting a mode reports it`() {
        val selected = mutableListOf<AppearanceMode>()
        show(onModeSelected = { selected += it })

        composeRule.onNodeWithText("Dark").performClick()

        assertEquals(listOf(AppearanceMode.DARK), selected)
    }

    @Test
    fun `toggling dynamic colour reports the new value`() {
        val changes = mutableListOf<Boolean>()
        show(
            uiState = AppearanceSettings(dynamicColor = true),
            onDynamicColorChange = { changes += it },
        )

        composeRule.onNodeWithTag(SETTINGS_DYNAMIC_COLOUR_ROW_TEST_TAG).performClick()

        assertEquals(listOf(false), changes)
    }
}
