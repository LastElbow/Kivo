package com.bustedelbow.kivo.ui.accounts

import android.content.Context
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.NewAccount
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The redesigned Account creation form (issue #11): a segmented type selector and a focal Opening
 * balance field feed a NewAccount, the old dialog's whole-centavo validation is preserved, and the
 * sheet offers a close affordance.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CreateAccountSheetTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `the form reports the name, type and opening balance`() {
        val created = mutableListOf<NewAccount>()
        renderForm(onConfirm = { created += it })

        composeRule.onNodeWithTag(CREATE_ACCOUNT_NAME_TEST_TAG).performTextInput("Pocket money")
        composeRule.onNodeWithText("Cash").performClick()
        composeRule.onNodeWithTag(CREATE_ACCOUNT_OPENING_BALANCE_TEST_TAG).performTextInput("1,250.75")
        composeRule.onNodeWithText("Create").performClick()

        assertEquals(
            listOf(
                NewAccount(name = "Pocket money", type = AccountType.CASH, openingBalanceMinorUnits = 125_075),
            ),
            created,
        )
    }

    @Test
    fun `the type selector offers every Account type`() {
        renderForm()

        listOf("Bank", "E-Wallet", "Cash", "Other").forEach { type ->
            composeRule.onNodeWithText(type).assertExists()
        }
    }

    @Test
    fun `create requires a name and a whole-centavo opening balance`() {
        renderForm()

        composeRule.onNodeWithText("Create").assertIsNotEnabled()

        composeRule.onNodeWithTag(CREATE_ACCOUNT_NAME_TEST_TAG).performTextInput("Bank")
        composeRule.onNodeWithText("Create").assertIsEnabled()

        composeRule.onNodeWithTag(CREATE_ACCOUNT_OPENING_BALANCE_TEST_TAG).performTextInput("10.999")
        composeRule.onNodeWithText("Create").assertIsNotEnabled()

        composeRule.onNodeWithTag(CREATE_ACCOUNT_OPENING_BALANCE_TEST_TAG).performTextClearance()
        composeRule.onNodeWithTag(CREATE_ACCOUNT_OPENING_BALANCE_TEST_TAG).performTextInput("10.99")
        composeRule.onNodeWithText("Create").assertIsEnabled()
    }

    @Test
    fun `the sheet hides before its close affordance reports the dismissal`() {
        var dismissed = false
        composeRule.setContent {
            KivoTheme {
                CreateAccountSheet(onDismiss = { dismissed = true }, onConfirm = {})
            }
        }

        composeRule.onNodeWithContentDescription("Close").performClick()
        composeRule.waitForIdle()

        assertTrue(dismissed)
    }

    @Test
    fun `the close affordance dismisses the form`() {
        var dismissed = false
        renderForm(onDismiss = { dismissed = true })

        composeRule.onNodeWithContentDescription("Close").performClick()

        assertTrue(dismissed)
    }

    @Test
    @Config(qualifiers = "w360dp-h800dp")
    fun `the create action is on screen when the sheet opens`() {
        composeRule.setContent {
            KivoTheme {
                CreateAccountSheet(onDismiss = {}, onConfirm = {})
            }
        }
        composeRule.waitForIdle()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val screenHeight = context.resources.configuration.screenHeightDp
        val button = composeRule.onNodeWithText("Create").getUnclippedBoundsInRoot()

        // The form fits the half-window the sheet opens at, so Create is on screen without a drag.
        assertTrue(button.bottom.value <= screenHeight)
    }

    private fun renderForm(
        onDismiss: () -> Unit = {},
        onConfirm: (NewAccount) -> Unit = {},
    ) {
        composeRule.setContent {
            KivoTheme {
                CreateAccountForm(onDismiss = onDismiss, onConfirm = onConfirm)
            }
        }
    }
}
