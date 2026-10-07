package com.bustedelbow.kivo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bustedelbow.kivo.data.local.entity.AccountEntity

/**
 * The single local Room database (ADR-0001: local-first, no backend).
 *
 * This slice only proves the Room + KSP wiring and builds the database at application
 * start, so it declares the first real table and nothing more. DAOs and the remaining
 * tables arrive with the first real slice.
 */
@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
abstract class KivoDatabase : RoomDatabase() {
    companion object {
        const val NAME = "kivo.db"
    }
}
