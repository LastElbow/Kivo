package com.bustedelbow.kivo.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single recorded movement of money (GLOSSARY: Entry). Entries are the source of truth for every
 * derived Balance (ADR-0002), so they are never cascaded away with their Account (ADR-0004).
 *
 * `type` names an [com.bustedelbow.kivo.domain.model.EntryType]. `accountId` is the one Account an
 * Expense/Income/Adjustment touches, and the source of a Transfer, whose destination is
 * `counterAccountId`. `amountMinorUnits` is a positive magnitude for Expense/Income/Transfer and a
 * signed delta for an Adjustment (ADR-0003).
 */
@Entity(
    tableName = "entries",
    indices = [Index("accountId"), Index("counterAccountId"), Index("categoryId")],
)
data class EntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val accountId: Long,
    val counterAccountId: Long? = null,
    val categoryId: Long? = null,
    val amountMinorUnits: Long,
    val occurredOnEpochDay: Long,
    val note: String? = null,
)
