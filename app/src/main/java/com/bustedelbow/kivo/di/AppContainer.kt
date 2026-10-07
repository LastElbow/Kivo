package com.bustedelbow.kivo.di

import android.content.Context
import androidx.room.Room
import com.bustedelbow.kivo.data.local.KivoDatabase

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
    ).build()
}
