package com.bustedelbow.kivo.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.NewAccount
import com.bustedelbow.kivo.ui.components.labelRes
import com.bustedelbow.kivo.ui.format.PESO_SIGN
import com.bustedelbow.kivo.ui.format.parsePhpToMinorUnits

/**
 * Collects the name, type and Opening balance for a new Account. Amount parsing happens here, at
 * the UI edge (ADR-0003); confirmation is disabled until the name is present and the amount is a
 * whole-centavo value.
 */
@Composable
fun CreateAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (NewAccount) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var typeName by rememberSaveable { mutableStateOf(AccountType.BANK.name) }
    var openingBalance by rememberSaveable { mutableStateOf("") }

    val selectedType = AccountType.valueOf(typeName)
    val parsedOpeningBalance =
        if (openingBalance.isBlank()) 0L else parsePhpToMinorUnits(openingBalance)
    val canConfirm = name.isNotBlank() && parsedOpeningBalance != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.create_account_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(R.string.create_account_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.create_account_type_label),
                    style = MaterialTheme.typography.labelLarge,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccountType.entries.forEach { candidate ->
                        FilterChip(
                            selected = candidate == selectedType,
                            onClick = { typeName = candidate.name },
                            label = { Text(text = stringResource(candidate.labelRes())) },
                        )
                    }
                }
                OutlinedTextField(
                    value = openingBalance,
                    onValueChange = { openingBalance = it },
                    label = {
                        Text(text = stringResource(R.string.create_account_opening_balance_label))
                    },
                    prefix = { Text(text = PESO_SIGN) },
                    singleLine = true,
                    isError = parsedOpeningBalance == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
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
            ) {
                Text(text = stringResource(R.string.create_account_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.create_account_cancel))
            }
        },
    )
}
