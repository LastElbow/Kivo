package com.bustedelbow.kivo.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountBalance
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Home renders Accounts and recent Entries in one scrolling list. They live in separate tables, so
 * their ids overlap (both start at 1) and must not collide as lazy-list keys (issue #4 regression:
 * the app crashed on scroll once a first Entry shared the first Account's id).
 *
 * The redesign (issue #9) puts a large app bar over a Balance hero that counts up, a tonal week
 * summary and two segmented sections; these tests cover each of those.
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
        composeRule.onNodeWithText("−₱120.00").assertExists()
    }

    @Test
    fun `shows the large app bar over the hero balance and the week summary`() {
        render(homeState())
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Kivo").assertExists()
        composeRule.onNodeWithText("Total balance").assertExists()
        composeRule.onNodeWithText("₱2,500.00").assertExists()
        composeRule.onNodeWithText("Spent this week").assertExists()
        composeRule.onNodeWithText("−₱120.00").assertExists()
        composeRule.onNodeWithText("Income this week").assertExists()
        composeRule.onNodeWithText("+₱500.00").assertExists()
    }

    @Test
    fun `segments Accounts and Recent into separate sections`() {
        render(homeState())
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Accounts").assertExists()
        composeRule.onNodeWithText("₱1,000.00").assertExists()

        composeRule.onNodeWithTag(HOME_LIST_TEST_TAG).performScrollToNode(hasText("Recent"))

        composeRule.onNodeWithText("Recent").assertExists()
        composeRule.onNodeWithText("Food").assertExists()
        composeRule.onNodeWithText("−₱70.00").assertExists()
    }

    @Test
    @Config(qualifiers = "w411dp-h891dp")
    fun `holds the segments of a section apart with a gap`() {
        render(
            homeState().copy(
                accounts =
                    listOf(
                        accountBalance(id = 1, name = "Bank", balanceMinorUnits = 100_000),
                        accountBalance(id = 2, name = "Cash", balanceMinorUnits = 200_000, type = AccountType.CASH),
                    ),
                totalBalanceMinorUnits = 300_000,
            ),
        )
        composeRule.waitForIdle()

        val bank = composeRule.onNodeWithText("₱1,000.00").getUnclippedBoundsInRoot()
        val cash = composeRule.onNodeWithText("₱2,000.00").getUnclippedBoundsInRoot()

        // M3 holds a contained list apart with gaps, so one segment is 8dp below the last.
        assertEquals(8f, (cash.top - bank.bottom).value, 0.5f)
    }

    @Test
    fun `the extended FAB offers adding an entry`() {
        var addedEntry = false
        render(homeState(), onAddEntry = { addedEntry = true })

        composeRule.onNodeWithContentDescription("Add entry").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add entry").performClick()

        assertTrue(addedEntry)
    }

    @Test
    fun `the hero counts the balance up to its final value`() {
        composeRule.mainClock.autoAdvance = false
        render(homeState(), reducedMotion = false)

        composeRule.mainClock.advanceTimeBy(16)

        composeRule.onNodeWithText("₱2,500.00").assertDoesNotExist()

        composeRule.mainClock.advanceTimeBy(3_000)

        composeRule.onNodeWithText("₱2,500.00").assertExists()
    }

    @Test
    fun `reduced motion snaps the balance to its final value`() {
        composeRule.mainClock.autoAdvance = false
        render(homeState(), reducedMotion = true)

        composeRule.mainClock.advanceTimeBy(16)

        composeRule.onNodeWithText("₱2,500.00").assertExists()
    }

    @Test
    fun `the hero settles on the exact balance`() {
        // Far beyond the 2^24 a Float can hold to the centavo, so a count-up animated as a Float
        // would rest on a Balance that is not the Balance (ADR-0003).
        render(homeState().copy(totalBalanceMinorUnits = 3_141_592_653_589L))
        composeRule.waitForIdle()

        composeRule.onNodeWithText("₱31,415,926,535.89").assertExists()
    }

    private fun render(
        uiState: HomeUiState,
        reducedMotion: Boolean = false,
        onAddEntry: () -> Unit = {},
    ) {
        composeRule.setContent {
            KivoTheme {
                HomeContent(
                    uiState = uiState,
                    contentPadding = PaddingValues(0.dp),
                    onCreateAccount = {},
                    onAddEntry = onAddEntry,
                    reducedMotion = reducedMotion,
                )
            }
        }
    }

    private fun accountBalance(
        id: Long,
        name: String,
        balanceMinorUnits: Long,
        type: AccountType = AccountType.BANK,
    ): AccountBalance =
        AccountBalance(
            account =
                Account(
                    id = id,
                    name = name,
                    type = type,
                    openingBalanceMinorUnits = balanceMinorUnits,
                ),
            balanceMinorUnits = balanceMinorUnits,
        )

    private fun homeState(): HomeUiState =
        HomeUiState(
            accounts = listOf(accountBalance(id = 1, name = "Bank", balanceMinorUnits = 100_000)),
            totalBalanceMinorUnits = 250_000,
            spendMinorUnits = 12_000,
            incomeMinorUnits = 50_000,
            recentEntries =
                listOf(
                    EntrySummary(
                        entry =
                            ExpenseEntry(
                                id = 1,
                                accountId = 1,
                                categoryId = 1,
                                amountMinorUnits = 7_000,
                                occurredOnEpochDay = 20_000,
                            ),
                        accountName = "Bank",
                        categoryName = "Food",
                    ),
                ),
            isLoading = false,
        )
}
