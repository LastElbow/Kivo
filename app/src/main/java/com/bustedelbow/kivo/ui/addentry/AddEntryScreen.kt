package com.bustedelbow.kivo.ui.addentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.EntryType
import com.bustedelbow.kivo.domain.model.EntryValidationError
import com.bustedelbow.kivo.ui.LocalAppContainer
import com.bustedelbow.kivo.ui.components.labelRes
import com.bustedelbow.kivo.ui.format.PESO_SIGN
import com.bustedelbow.kivo.ui.format.formatEpochDay
import com.bustedelbow.kivo.ui.navigation.KivoRoute
import com.bustedelbow.kivo.ui.theme.KivoType

/**
 * The full-screen Add Entry flow: amount, Account, Category, date and note, recording an Expense or
 * Income and returning Home when done (issue #4).
 */
@Composable
fun AddEntryScreen(
    contentPadding: PaddingValues,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val viewModel: AddEntryViewModel =
        viewModel(
            factory =
                AddEntryViewModel.factory(
                    accountRepository = container.accountRepository,
                    categoryRepository = container.categoryRepository,
                    entryRepository = container.entryRepository,
                ),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onDone()
    }

    AddEntryContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onTypeChange = viewModel::setType,
        onAmountChange = viewModel::setAmount,
        onAccountSelected = viewModel::selectAccount,
        onCategorySelected = viewModel::selectCategory,
        onDateChange = viewModel::setDate,
        onNoteChange = viewModel::setNote,
        onSave = viewModel::save,
        onClose = onDone,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEntryContent(
    uiState: AddEntryUiState,
    contentPadding: PaddingValues,
    onTypeChange: (EntryType) -> Unit,
    onAmountChange: (String) -> Unit,
    onAccountSelected: (Long) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onDateChange: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag(KivoRoute.ADD_ENTRY_TEST_TAG),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.add_entry_close),
                )
            }
            Text(
                text = stringResource(R.string.add_entry_title),
                style = KivoType.emphasized.titleLarge,
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FieldLabel(stringResource(R.string.add_entry_type_label))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RECORDABLE_TYPES.forEach { type ->
                    FilterChip(
                        selected = type == uiState.type,
                        onClick = { onTypeChange(type) },
                        label = { Text(text = stringResource(type.labelRes())) },
                    )
                }
            }

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = onAmountChange,
                label = { Text(text = stringResource(R.string.add_entry_amount_label)) },
                prefix = { Text(text = PESO_SIGN) },
                singleLine = true,
                isError = showAmountError(uiState),
                supportingText = amountSupportingText(uiState),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            FieldLabel(stringResource(R.string.add_entry_account_label))
            if (uiState.accounts.isEmpty()) {
                Text(
                    text = stringResource(R.string.add_entry_accounts_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.accounts.forEach { account ->
                        FilterChip(
                            selected = account.id == uiState.selectedAccountId,
                            onClick = { onAccountSelected(account.id) },
                            label = { Text(text = account.name) },
                        )
                    }
                }
            }

            FieldLabel(stringResource(R.string.add_entry_category_label))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.categories.forEach { category ->
                    FilterChip(
                        selected = category.id == uiState.selectedCategoryId,
                        onClick = { onCategorySelected(category.id) },
                        label = { Text(text = category.name) },
                    )
                }
            }

            FieldLabel(stringResource(R.string.add_entry_date_label))
            OutlinedButton(onClick = { isDatePickerVisible = true }) {
                Text(text = formatEpochDay(uiState.occurredOnEpochDay))
            }
            if (uiState.errors.contains(EntryValidationError.DATE_IN_FUTURE)) {
                Text(
                    text = stringResource(R.string.add_entry_date_error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChange,
                label = { Text(text = stringResource(R.string.add_entry_note_label)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSave,
                enabled = uiState.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.add_entry_save),
                    style = KivoType.emphasized.labelLarge,
                )
            }
        }
    }

    if (isDatePickerVisible) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = epochDayToUtcMillis(uiState.occurredOnEpochDay),
                selectableDates = datesUpTo(uiState.todayEpochDay),
            )
        DatePickerDialog(
            onDismissRequest = { isDatePickerVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateChange(utcMillisToEpochDay(it)) }
                        isDatePickerVisible = false
                    },
                ) {
                    Text(text = stringResource(R.string.add_entry_date_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerVisible = false }) {
                    Text(text = stringResource(R.string.add_entry_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
    )
}

private fun showAmountError(uiState: AddEntryUiState): Boolean =
    uiState.amount.isNotBlank() && uiState.errors.contains(EntryValidationError.AMOUNT_NOT_POSITIVE)

private fun amountSupportingText(uiState: AddEntryUiState): (@Composable () -> Unit)? =
    if (showAmountError(uiState)) {
        { Text(text = stringResource(R.string.add_entry_amount_error)) }
    } else {
        null
    }

private val RECORDABLE_TYPES = listOf(EntryType.EXPENSE, EntryType.INCOME)

private const val MILLIS_PER_DAY = 86_400_000L

private fun epochDayToUtcMillis(epochDay: Long): Long = epochDay * MILLIS_PER_DAY

private fun utcMillisToEpochDay(utcMillis: Long): Long = utcMillis / MILLIS_PER_DAY

/** A [SelectableDates] that allows anything up to and including [todayEpochDay]. */
@OptIn(ExperimentalMaterial3Api::class)
private fun datesUpTo(todayEpochDay: Long): SelectableDates =
    object : SelectableDates {
        private val todayMillis = epochDayToUtcMillis(todayEpochDay)

        override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayMillis
    }
