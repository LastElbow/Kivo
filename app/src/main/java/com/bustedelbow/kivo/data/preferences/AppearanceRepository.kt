package com.bustedelbow.kivo.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow

/**
 * Reads and writes the persisted appearance. It is the single seam through which Settings changes
 * the theme and the theme observes those changes.
 */
interface AppearanceRepository {
    /** The stored appearance, re-emitted whenever it changes. */
    val settings: Flow<AppearanceSettings>

    /** Persists [mode] as the appearance choice. */
    suspend fun setMode(mode: AppearanceMode)

    /** Persists whether dynamic (wallpaper) colour is enabled. */
    suspend fun setDynamicColor(enabled: Boolean)
}

private val Context.appearanceDataStore: DataStore<Preferences> by preferencesDataStore(name = "appearance")

/** Builds the process-wide [AppearanceRepository] backed by the app's DataStore file. */
fun createAppearanceRepository(context: Context): AppearanceRepository =
    DataStoreAppearanceRepository(context.applicationContext.appearanceDataStore)
