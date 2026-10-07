package com.bustedelbow.kivo.domain.model

/**
 * An [Account] paired with its derived Balance (GLOSSARY: Balance). The Balance is computed from
 * the Account's Opening balance and Entries, never read from storage (ADR-0002).
 */
data class AccountBalance(
    val account: Account,
    val balanceMinorUnits: Long,
)
