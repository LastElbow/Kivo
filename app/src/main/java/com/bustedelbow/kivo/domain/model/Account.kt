package com.bustedelbow.kivo.domain.model

/**
 * A place the user's money is held (GLOSSARY: Account).
 *
 * `openingBalanceMinorUnits` is the amount the Account is known to hold when created, as a
 * `Long` in minor units (ADR-0003). `archived` records that the Account is retired rather than
 * deleted (ADR-0004). The Account's current Balance is never stored; it is derived from the
 * Opening balance and the Account's Entries (ADR-0002).
 */
data class Account(
    val id: Long,
    val name: String,
    val type: AccountType,
    val openingBalanceMinorUnits: Long,
    val archived: Boolean = false,
)

/** The kind of an Account (GLOSSARY: Account type). */
enum class AccountType {
    BANK,
    E_WALLET,
    CASH,
    OTHER,
}
