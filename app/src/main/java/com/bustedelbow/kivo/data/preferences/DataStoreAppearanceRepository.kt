package com.bustedelbow.kivo.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * The [AppearanceRepository] over an AndroidX [DataStore], so the appearance outlives the process.
 * A read error that is not an [IOException] is a bug and propagates rather than silently resetting
 * the user's choice.
 */
class DataStoreAppearanceRepository(
    private val dataStore: DataStore<Preferences>,
) : AppearanceRepository {
    override val settings: Flow<AppearanceSettings> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) emit(emptyPreferences()) else throw throwable
            }.map { preferences ->
                AppearanceSettings(
                    mode = AppearanceMode.fromStored(preferences[Keys.MODE]),
                    dynamicColor = preferences[Keys.DYNAMIC_COLOR] ?: false,
                )
            }

    override suspend fun setMode(mode: AppearanceMode) {
        dataStore.edit { preferences -> preferences[Keys.MODE] = mode.name }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[Keys.DYNAMIC_COLOR] = enabled }
    }

    private object Keys {
        val MODE = stringPreferencesKey("appearance_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }
}
