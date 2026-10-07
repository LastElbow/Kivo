package com.bustedelbow.kivo.ui.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.ledger.Ledger
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.ui.theme.KivoType

/**
 * One Entry in Home's recent history: its leading type shape, its Category and Account names, and
 * the signed amount rendered emphasized in tabular figures (GLOSSARY: Entry).
 */
@Composable
fun EntryRow(
    summary: EntrySummary,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        leadingContent = { LeadingTypeShape(iconRes = summary.entry.iconRes()) },
        headlineContent = {
            Text(text = summary.categoryName ?: stringResource(R.string.entry_uncategorised))
        },
        supportingContent = { Text(text = summary.accountName) },
        trailingContent = {
            AmountText(
                amountMinorUnits = Ledger.signedAmountOf(summary.entry),
                kind = summary.entry.amountKind(),
                style = KivoType.emphasized.titleMedium,
            )
        },
    )
}
