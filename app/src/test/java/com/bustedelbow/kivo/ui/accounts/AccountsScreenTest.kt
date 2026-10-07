package com.bustedelbow.kivo.ui.accounts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.NewAccount
import com.bustedelbow.kivo.ui.components.ACCOUNT_ACTIONS_TEST_TAG
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

/**
 * Accounts names itself with an app bar, lists contained Account rows, and opens creation in a sheet
 * while the per-row Archive menu stays unchanged (issues #5 and #11).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AccountsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `archiving an account reports its id`() {
        val archived = mutableListOf<Long>()
        val accounts =
            listOf(
                accountBalance(id = 1, name = "Bank"),
                accountBalance(id = 2, name = "Cash"),
            )

        render(AccountsUiState(accounts = accounts, isLoading = false), onArchive = { archived += it })

        composeRule.onAllNodesWithTag(ACCOUNT_ACTIONS_TEST_TAG)[1].performClick()
        composeRule.onNodeWithText("Archive").performClick()

        assertEquals(listOf(2L), archived)
    }

    @Test
    fun `the app bar names the screen and the FAB requests creation`() {
        var requested = false

        render(
            AccountsUiState(accounts = listOf(accountBalance(id = 1, name = "Bank")), isLoading = false),
            onAddAccount = { requested = true },
        )

        composeRule.onNodeWithText("Accounts").assertExists()
        composeRule.onNodeWithContentDescription("Add account").performClick()

        assertTrue(requested)
    }

    @Test
    fun `the create sheet opens when creation is requested`() {
        render(
            AccountsUiState(
                accounts = listOf(accountBalance(id = 1, name = "Bank")),
                isLoading = false,
                isCreateSheetVisible = true,
            ),
        )

        composeRule.onNodeWithText("New account").assertExists()
    }

    private fun render(
        uiState: AccountsUiState,
        onAddAccount: () -> Unit = {},
        onDismissCreateAccount: () -> Unit = {},
        onCreateAccount: (NewAccount) -> Unit = {},
        onArchive: (Long) -> Unit = {},
    ) {
        composeRule.setContent {
            KivoTheme {
                AccountsContent(
                    uiState = uiState,
                    contentPadding = PaddingValues(0.dp),
                    onAddAccount = onAddAccount,
                    onDismissCreateAccount = onDismissCreateAccount,
                    onCreateAccount = onCreateAccount,
                    onArchive = onArchive,
                )
            }
        }
    }

    private fun accountBalance(
        id: Long,
        name: String,
    ): AccountBalance =
        AccountBalance(
            account =
                Account(
                    id = id,
                    name = name,
                    type = AccountType.BANK,
                    openingBalanceMinorUnits = 100_000,
                ),
            balanceMinorUnits = 100_000,
        )
}
