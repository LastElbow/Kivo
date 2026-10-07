package com.bustedelbow.kivo.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

/**
 * Home renders Accounts and recent Entries in one scrolling list. They live in separate tables, so
 * their ids overlap (both start at 1) and must not collide as lazy-list keys (issue #4 regression:
 * the app crashed on scroll once a first Entry shared the first Account's id).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `scrolls from the account list to a recent entry whose id matches an account's`() {
        val accounts =
            listOf(
                AccountBalance(
                    account =
                        Account(
                            id = 1,
                            name = "Bank",
                            type = AccountType.BANK,
                            openingBalanceMinorUnits = 100_000,
                        ),
                    balanceMinorUnits = 100_000,
                ),
            )
        val recentEntry =
            EntrySummary(
                entry =
                    ExpenseEntry(
                        id = 1,
                        accountId = 1,
                        categoryId = 1,
                        amountMinorUnits = 12_000,
                        occurredOnEpochDay = 20_000,
                    ),
                accountName = "Bank",
                categoryName = "Food",
            )

        composeRule.setContent {
            KivoTheme {
                HomeContent(
                    uiState = HomeUiState(accounts = accounts, recentEntries = listOf(recentEntry), isLoading = false),
                    contentPadding = PaddingValues(0.dp),
                    onCreateAccount = {},
                    onAddEntry = {},
                )
            }
        }

        composeRule.onNodeWithTag(HOME_LIST_TEST_TAG).performScrollToNode(hasText("Food"))

        composeRule.onNodeWithText("Food").assertExists()
    }
}
