package com.bustedelbow.kivo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.bustedelbow.kivo.domain.model.AdjustmentEntry
import com.bustedelbow.kivo.domain.model.Entry
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.domain.model.IncomeEntry
import com.bustedelbow.kivo.domain.model.TransferEntry
import com.bustedelbow.kivo.ui.format.formatPhp
import com.bustedelbow.kivo.ui.format.formatSignedPhp
import com.bustedelbow.kivo.ui.theme.withTabularFigures

/**
 * What an amount reads as, which selects its sign and colour (one shared amount component: the
 * redesign spec's money semantics).
 *
 * - [INCOME] is money arriving: an explicit `+`, `tertiary` green.
 * - [EXPENSE] is money leaving: an explicit `−`, `onSurface`.
 * - [NEUTRAL] is a Balance or another amount that is not Income or Expense: `onSurface`, signed
 *   only when negative.
 *
 * Income and Expense always draw their sign, so a colour-blind reader still tells them apart;
 * colour never carries the meaning alone.
 */
enum class AmountKind {
    INCOME,
    EXPENSE,
    NEUTRAL,
}

/**
 * The money semantics an [Entry]'s signed amount reads as: an Income arrives, an Expense leaves,
 * and a Transfer or Adjustment is a neutral correction (GLOSSARY: Entry).
 */
fun Entry.amountKind(): AmountKind =
    when (this) {
        is IncomeEntry -> AmountKind.INCOME
        is ExpenseEntry -> AmountKind.EXPENSE
        is TransferEntry, is AdjustmentEntry -> AmountKind.NEUTRAL
    }

/**
 * The one place every amount is drawn: tabular figures stop its digits shifting as the value
 * changes, [kind] supplies the sign and colour, and rows and summaries pass [style] to emphasize
 * the amount.
 *
 * [color] overrides the colour [kind] would pick. An amount sitting on a container other than the
 * body surface needs that container's paired `on*` role — the hero Balance card draws on
 * `primaryContainer`, so it passes `onPrimaryContainer`. The sign never changes with the colour, so
 * Income and Expense still read without it.
 */
@Composable
fun AmountText(
    amountMinorUnits: Long,
    kind: AmountKind,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color? = null,
) {
    Text(
        text = kind.format(amountMinorUnits),
        modifier = modifier,
        color = color ?: kind.color(),
        style = style.withTabularFigures(),
    )
}

private fun AmountKind.format(amountMinorUnits: Long): String =
    when (this) {
        AmountKind.INCOME, AmountKind.EXPENSE -> formatSignedPhp(amountMinorUnits)
        AmountKind.NEUTRAL -> formatPhp(amountMinorUnits)
    }

@Composable
private fun AmountKind.color(): Color =
    when (this) {
        AmountKind.INCOME -> MaterialTheme.colorScheme.tertiary
        AmountKind.EXPENSE, AmountKind.NEUTRAL -> MaterialTheme.colorScheme.onSurface
    }
