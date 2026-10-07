package com.bustedelbow.kivo.ui.accounts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.ui.components.ACCOUNT_ACTIONS_TEST_TAG
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

/**
 * Accounts offers archiving per row: the row's action opens a menu whose Archive item reports the
 * Account's id, so the ViewModel can retire it (issue #5).
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

        composeRule.setContent {
            KivoTheme {
                AccountsContent(
                    uiState = AccountsUiState(accounts = accounts, isLoading = false),
                    contentPadding = PaddingValues(0.dp),
                    onAddAccount = {},
                    onDismissCreateAccount = {},
                    onCreateAccount = {},
                    onArchive = { archived += it },
                )
            }
        }

        composeRule.onAllNodesWithTag(ACCOUNT_ACTIONS_TEST_TAG)[1].performClick()
        composeRule.onNodeWithText("Archive").performClick()

        assertEquals(listOf(2L), archived)
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
