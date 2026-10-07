package com.bustedelbow.kivo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

/**
 * The one shared amount component draws every amount through the money formatter, so the sign is
 * always explicit for Income (`+`) and Expense (`−`) while a Balance shows a sign only when it is
 * overdrawn (issue #8). Colour never has to carry the meaning alone.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AmountTextTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `draws an explicit sign for income and expense`() {
        composeRule.setContent {
            KivoTheme {
                Column {
                    AmountText(amountMinorUnits = 100_000, kind = AmountKind.INCOME)
                    AmountText(amountMinorUnits = -12_000, kind = AmountKind.EXPENSE)
                }
            }
        }

        composeRule.onNodeWithText("+₱1,000.00").assertExists()
        composeRule.onNodeWithText("−₱120.00").assertExists()
    }

    @Test
    fun `signs a balance only when it is negative`() {
        composeRule.setContent {
            KivoTheme {
                Column {
                    AmountText(amountMinorUnits = 50_000, kind = AmountKind.NEUTRAL)
                    AmountText(amountMinorUnits = -2_500, kind = AmountKind.NEUTRAL)
                    AmountText(amountMinorUnits = 0, kind = AmountKind.NEUTRAL)
                }
            }
        }

        composeRule.onNodeWithText("₱500.00").assertExists()
        composeRule.onNodeWithText("−₱25.00").assertExists()
        composeRule.onNodeWithText("₱0.00").assertExists()
    }
}
