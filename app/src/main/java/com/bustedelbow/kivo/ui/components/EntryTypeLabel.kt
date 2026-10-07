package com.bustedelbow.kivo.ui.components

import androidx.annotation.StringRes
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.EntryType

/** The English label for the Entry kinds the Add Entry flow records (Expense or Income). */
@StringRes
fun EntryType.labelRes(): Int =
    when (this) {
        EntryType.EXPENSE -> R.string.entry_type_expense
        EntryType.INCOME -> R.string.entry_type_income
        EntryType.TRANSFER, EntryType.ADJUSTMENT -> error("$this has no Add Entry label")
    }
