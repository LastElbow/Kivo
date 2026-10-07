package com.bustedelbow.kivo.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The appearance is written to and read back from the preference store, so a later reader sees the
 * choice an earlier writer made (issue #12).
 */
class DataStoreAppearanceRepositoryTest {
    @Test
    fun `defaults to system appearance with dynamic colour off`() =
        runTest {
            val repository = DataStoreAppearanceRepository(InMemoryPreferencesDataStore())

            assertEquals(AppearanceSettings(), repository.settings.first())
        }

    @Test
    fun `a later repository over the same store reads the persisted appearance`() =
        runTest {
            val store = InMemoryPreferencesDataStore()

            val writer = DataStoreAppearanceRepository(store)
            writer.setMode(AppearanceMode.DARK)
            writer.setDynamicColor(true)

            val reader = DataStoreAppearanceRepository(store)
            assertEquals(
                AppearanceSettings(mode = AppearanceMode.DARK, dynamicColor = true),
                reader.settings.first(),
            )
        }
}

/**
 * A [DataStore] holding its [Preferences] in memory, standing in for the device's preference file so
 * the repository is tested on any host.
 */
private class InMemoryPreferencesDataStore : DataStore<Preferences> {
    private val state = MutableStateFlow<Preferences>(emptyPreferences())

    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}
