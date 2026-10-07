package com.bustedelbow.kivo.ui.addentry

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.Category
import com.bustedelbow.kivo.domain.model.CategoryType
import com.bustedelbow.kivo.domain.model.EntryType
import com.bustedelbow.kivo.domain.model.EntryValidationError
import com.bustedelbow.kivo.ui.navigation.KivoRoute
import com.bustedelbow.kivo.ui.theme.KivoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

/**
 * The redesigned Add Entry content (issue #10): the app bar closes the flow, the segmented selector
 * reports the chosen kind, the save button follows validation, and the save hero moment confirms
 * before returning Home — unless reduced motion is on, when it returns directly.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AddEntryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `the app bar names the screen and closes it`() {
        var closed = false
        render(uiState = validState(), onClose = { closed = true })

        composeRule.onNodeWithText("New entry").assertExists()
        composeRule.onNodeWithContentDescription("Close").performClick()

        assertTrue(closed)
    }

    @Test
    fun `the segmented selector reports the chosen kind`() {
        val changes = mutableListOf<EntryType>()
        render(uiState = validState(), onTypeChange = { changes += it })

        composeRule.onNodeWithText("Income").performClick()

        assertEquals(listOf(EntryType.INCOME), changes)
    }

    @Test
    fun `the save button follows validation`() {
        val invalid = validState().copy(errors = setOf(EntryValidationError.AMOUNT_NOT_POSITIVE))
        render(uiState = invalid)

        composeRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `a valid form enables saving`() {
        render(uiState = validState())

        composeRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun `saving shows the affirmation`() {
        render(uiState = validState().copy(isSaved = true), reducedMotion = false)

        composeRule.onNodeWithTag(ADD_ENTRY_SAVED_TEST_TAG).assertExists()
        composeRule.onNodeWithTag(KivoRoute.ADD_ENTRY_TEST_TAG).assertExists()
    }

    @Test
    fun `reduced motion returns home without the affirmation`() {
        var closed = false
        render(uiState = validState().copy(isSaved = true), reducedMotion = true, onClose = { closed = true })

        composeRule.waitForIdle()

        composeRule.onNodeWithTag(ADD_ENTRY_SAVED_TEST_TAG).assertDoesNotExist()
        assertTrue(closed)
    }

    private fun render(
        uiState: AddEntryUiState,
        reducedMotion: Boolean = false,
        onTypeChange: (EntryType) -> Unit = {},
        onSave: () -> Unit = {},
        onClose: () -> Unit = {},
    ) {
        composeRule.setContent {
            KivoTheme {
                AddEntryContent(
                    uiState = uiState,
                    contentPadding = PaddingValues(0.dp),
                    onTypeChange = onTypeChange,
                    onAmountChange = {},
                    onAccountSelected = {},
                    onCategorySelected = {},
                    onDateChange = {},
                    onNoteChange = {},
                    onSave = onSave,
                    onClose = onClose,
                    reducedMotion = reducedMotion,
                )
            }
        }
    }

    private fun validState(): AddEntryUiState =
        AddEntryUiState(
            type = EntryType.EXPENSE,
            amount = "125.00",
            accounts = listOf(Account(id = 1, name = "Bank", type = AccountType.BANK, openingBalanceMinorUnits = 100_000)),
            categories = listOf(Category(id = 10, name = "Food", type = CategoryType.EXPENSE)),
            selectedAccountId = 1,
            selectedCategoryId = 10,
            occurredOnEpochDay = TODAY,
            todayEpochDay = TODAY,
            errors = emptySet(),
            isLoading = false,
        )

    private companion object {
        val TODAY: Long = LocalDate.of(2026, 10, 7).toEpochDay()
    }
}
