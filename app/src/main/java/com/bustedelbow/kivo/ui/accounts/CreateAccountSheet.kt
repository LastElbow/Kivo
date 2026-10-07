package com.bustedelbow.kivo.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.NewAccount
import com.bustedelbow.kivo.ui.components.labelRes
import com.bustedelbow.kivo.ui.format.PESO_SIGN
import com.bustedelbow.kivo.ui.format.parsePhpToMinorUnits
import com.bustedelbow.kivo.ui.theme.KivoType
import com.bustedelbow.kivo.ui.theme.withTabularFigures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Stable tags for the creation form's fields, shared with UI tests. */
internal const val CREATE_ACCOUNT_NAME_TEST_TAG = "create_account_name"

/** Stable tag for the creation form's focal Opening balance field, shared with UI tests. */
internal const val CREATE_ACCOUNT_OPENING_BALANCE_TEST_TAG = "create_account_opening_balance"

/** Stable tag for the creation form itself, shared with UI tests. */
internal const val CREATE_ACCOUNT_FORM_TEST_TAG = "create_account_form"

/** The sheet's 28dp top corners; its bottom pair sits off-screen, so only these are drawn. */
private val SheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

/** The widest the sheet grows, so it stays readable on a tablet or a free-form window. */
private val SheetMaxWidth = 640.dp

/** The prominent height of the form's primary action. */
private val CREATE_BUTTON_HEIGHT = 56.dp

/** The gap between the form's fields. */
private val FieldGap = 12.dp

/**
 * The share of the window the form takes, and so the height the sheet opens at: half, the slice
 * decision's cap. The action is pinned to the bottom of that height, so it is on screen without a
 * drag even when the fields above it have to scroll.
 */
private const val SHEET_HEIGHT_FRACTION = 0.5f

/**
 * Creates an Account in a modal bottom sheet: 28dp top corners, a 640dp width cap and half the
 * window's height, with the Create action pinned on screen. The close affordance dismisses it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountSheet(
    onDismiss: () -> Unit,
    onConfirm: (NewAccount) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = SheetMaxWidth,
        shape = SheetShape,
        // No drag handle: the form already fills the half-window the sheet is sized to, so a
        // handle above it would push the pinned action past the bottom of the screen. Close is
        // the affordance; the scrim and system back still dismiss too.
        dragHandle = null,
    ) {
        // The sheet is exactly half the window; the form fills that height with its action pinned
        // to the bottom, so Create is on screen without a drag whatever the window size.
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(SHEET_HEIGHT_FRACTION),
        ) {
            CreateAccountForm(
                // Close and Create play the same hide animation a scrim tap does before the sheet
                // leaves composition.
                onDismiss = { scope.hideSheetThen(sheetState, onDismiss) },
                onConfirm = { account -> scope.hideSheetThen(sheetState) { onConfirm(account) } },
            )
        }
    }
}

/**
 * Runs [sheetState]'s hide animation and only then [action], so an in-sheet affordance dismisses
 * the sheet exactly as a scrim tap or a drag does.
 */
@OptIn(ExperimentalMaterial3Api::class)
private fun CoroutineScope.hideSheetThen(
    sheetState: SheetState,
    action: () -> Unit,
) {
    launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) action()
    }
}

/**
 * Collects the name, type and Opening balance for a new Account: a segmented Account type selector,
 * a focal Opening balance field and the Create action. It fills the height it is given, scrolling
 * the fields above the pinned action. Amount parsing happens here, at the UI edge (ADR-0003);
 * confirmation is disabled until the name is present and the amount is a whole-centavo value.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateAccountForm(
    onDismiss: () -> Unit,
    onConfirm: (NewAccount) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var typeName by rememberSaveable { mutableStateOf(AccountType.BANK.name) }
    var openingBalance by rememberSaveable { mutableStateOf("") }

    val selectedType = AccountType.valueOf(typeName)
    val parsedOpeningBalance =
        if (openingBalance.isBlank()) 0L else parsePhpToMinorUnits(openingBalance)
    val canConfirm = name.isNotBlank() && parsedOpeningBalance != null
    val balanceTextStyle = KivoType.emphasized.headlineMedium.withTabularFigures()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                .testTag(CREATE_ACCOUNT_FORM_TEST_TAG),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.create_account_title),
                style = KivoType.emphasized.headlineSmall,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.create_account_close),
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(FieldGap),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = stringResource(R.string.create_account_name_label)) },
                singleLine = true,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(CREATE_ACCOUNT_NAME_TEST_TAG),
            )

            Text(
                text = stringResource(R.string.create_account_type_label),
                style = MaterialTheme.typography.labelLarge,
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                AccountType.entries.forEachIndexed { index, candidate ->
                    SegmentedButton(
                        selected = candidate == selectedType,
                        onClick = { typeName = candidate.name },
                        shape =
                            SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = AccountType.entries.size,
                            ),
                        label = { Text(text = stringResource(candidate.labelRes())) },
                    )
                }
            }

            OutlinedTextField(
                value = openingBalance,
                onValueChange = { openingBalance = it },
                label = { Text(text = stringResource(R.string.create_account_opening_balance_label)) },
                prefix = { Text(text = PESO_SIGN, style = balanceTextStyle) },
                textStyle = balanceTextStyle,
                singleLine = true,
                isError = parsedOpeningBalance == null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(CREATE_ACCOUNT_OPENING_BALANCE_TEST_TAG),
            )
        }

        Button(
            onClick = {
                if (canConfirm) {
                    onConfirm(
                        NewAccount(
                            name = name.trim(),
                            type = selectedType,
                            openingBalanceMinorUnits = parsedOpeningBalance,
                        ),
                    )
                }
            },
            enabled = canConfirm,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = FieldGap)
                    .height(CREATE_BUTTON_HEIGHT),
        ) {
            Text(
                text = stringResource(R.string.create_account_confirm),
                style = KivoType.emphasized.labelLarge,
            )
        }
    }
}
