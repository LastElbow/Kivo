package com.bustedelbow.kivo.ui.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.ledger.Ledger
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.ui.format.formatPhp

/**
 * One Entry in Home's recent history: its Category and Account names with the signed amount
 * (GLOSSARY: Entry).
 */
@Composable
fun EntryRow(
    summary: EntrySummary,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(text = summary.categoryName ?: stringResource(R.string.entry_uncategorised))
        },
        supportingContent = { Text(text = summary.accountName) },
        trailingContent = {
            Text(
                text = formatPhp(Ledger.signedAmountOf(summary.entry)),
                style = MaterialTheme.typography.titleMedium,
            )
        },
    )
}
