package com.bustedelbow.kivo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bustedelbow.kivo.data.local.dao.AccountDao
import com.bustedelbow.kivo.data.local.dao.CategoryDao
import com.bustedelbow.kivo.data.local.dao.EntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.CategoryEntity
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import com.bustedelbow.kivo.data.local.seed.DefaultCategories

/**
 * The single local Room database (ADR-0001: local-first, no backend). It holds Accounts, Entries
 * (the source of truth for derived Balances, ADR-0002) and Categories.
 */
@Database(
    entities = [AccountEntity::class, CategoryEntity::class, EntryEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class KivoDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun entryDao(): EntryDao

    companion object {
        const val NAME = "kivo.db"

        /**
         * Seeds the default Expense and Income Categories whenever the database is opened empty,
         * so a fresh install can record an Entry straight away (issue #3). Seeding on open rather
         * than on create also covers a database rebuilt by the destructive fallback below.
         */
        val seedCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                if (categoryCount(db) == 0L) {
                    DefaultCategories.all.forEach { category ->
                        db.execSQL(
                            "INSERT INTO categories (name, type, archived) VALUES (?, ?, 0)",
                            arrayOf(category.name, category.type.name),
                        )
                    }
                }
            }
        }

        private fun categoryCount(db: SupportSQLiteDatabase): Long =
            db.compileStatement("SELECT COUNT(*) FROM categories")
                .use { it.simpleQueryForLong() }
    }
}
