package com.bustedelbow.kivo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A place the user's money is held (GLOSSARY: Account).
 *
 * The walking skeleton stops at this first real table, so the database has something to
 * build and Room + KSP are proven end to end. DAOs, domain mapping and the remaining
 * tables land with the first real slice.
 *
 * `openingBalanceMinorUnits` is a `Long` in minor units (ADR-0003). `archived` records
 * that the Account is retired rather than deleted (ADR-0004). `type` holds the name of an
 * Account type (Bank, E-Wallet, Cash, Other); the typed enum arrives with the domain model.
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val openingBalanceMinorUnits: Long,
    val archived: Boolean = false,
)
