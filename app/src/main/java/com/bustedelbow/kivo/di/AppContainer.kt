package com.bustedelbow.kivo.di

import android.content.Context
import androidx.room.Room
import com.bustedelbow.kivo.data.local.KivoDatabase
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.data.repository.CategoryRepository

/**
 * The manual dependency container (ADR-0001 keeps the graph small enough not to need Hilt).
 *
 * It owns the process-wide collaborators and is created once, from [com.bustedelbow.kivo.KivoApplication].
 */
class AppContainer(context: Context) {

    val database: KivoDatabase = Room.databaseBuilder(
        context.applicationContext,
        KivoDatabase::class.java,
        KivoDatabase.NAME,
    )
        .addCallback(KivoDatabase.seedCallback)
        // Development-only: the schema is still changing and the pre-release database holds no
        // user data. Replace with real Migrations once the schema stabilises and data matters.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    val accountRepository: AccountRepository =
        AccountRepository(database.accountDao(), database.entryDao())

    val categoryRepository: CategoryRepository = CategoryRepository(database.categoryDao())
}
