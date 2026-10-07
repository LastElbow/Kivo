package com.bustedelbow.kivo.ui.components

import androidx.annotation.DrawableRes
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.AdjustmentEntry
import com.bustedelbow.kivo.domain.model.Entry
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.domain.model.IncomeEntry
import com.bustedelbow.kivo.domain.model.TransferEntry

// The leading glyphs the row shapes carry. They are Material Symbols (Apache-2.0) bundled in
// `res/drawable`, because only `material-icons-core`'s small set is on the classpath and it holds
// no bank, wallet, cash or money-direction glyphs.

/** The glyph for an [AccountType]. */
@DrawableRes
fun AccountType.iconRes(): Int =
    when (this) {
        AccountType.BANK -> R.drawable.ic_account_bank
        AccountType.E_WALLET -> R.drawable.ic_account_e_wallet
        AccountType.CASH -> R.drawable.ic_account_cash
        AccountType.OTHER -> R.drawable.ic_account_other
    }

/** The glyph for an Entry's kind, whether it is money arriving, leaving or being corrected. */
@DrawableRes
fun Entry.iconRes(): Int =
    when (this) {
        is ExpenseEntry -> R.drawable.ic_entry_expense
        is IncomeEntry -> R.drawable.ic_entry_income
        is TransferEntry -> R.drawable.ic_entry_transfer
        is AdjustmentEntry -> R.drawable.ic_entry_adjustment
    }
