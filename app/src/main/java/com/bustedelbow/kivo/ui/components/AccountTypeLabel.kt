package com.bustedelbow.kivo.ui.components

import androidx.annotation.StringRes
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.AccountType

/** The English label for an [AccountType]. */
@StringRes
fun AccountType.labelRes(): Int = when (this) {
    AccountType.BANK -> R.string.account_type_bank
    AccountType.E_WALLET -> R.string.account_type_e_wallet
    AccountType.CASH -> R.string.account_type_cash
    AccountType.OTHER -> R.string.account_type_other
}
