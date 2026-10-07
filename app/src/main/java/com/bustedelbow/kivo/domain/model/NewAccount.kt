package com.bustedelbow.kivo.domain.model

/**
 * The details needed to create an Account (GLOSSARY: Account): its name, type and Opening
 * balance. Passed as one value from the creation UI down to the repository so the three fields
 * cannot drift apart.
 */
data class NewAccount(
    val name: String,
    val type: AccountType,
    val openingBalanceMinorUnits: Long,
)
