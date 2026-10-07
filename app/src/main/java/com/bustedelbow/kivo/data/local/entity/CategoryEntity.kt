package com.bustedelbow.kivo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A user-defined label describing what an Expense or Income is for (GLOSSARY: Category).
 *
 * `type` holds the name of a [com.bustedelbow.kivo.domain.model.CategoryType] (EXPENSE or INCOME);
 * `archived` records that the Category is retired rather than deleted (ADR-0004). Domain types are
 * kept out of the Room entities and mapped in [com.bustedelbow.kivo.data.mapper].
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val archived: Boolean = false,
)
