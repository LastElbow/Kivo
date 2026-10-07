package com.bustedelbow.kivo.ui.settings

import com.bustedelbow.kivo.data.preferences.AppearanceMode
import com.bustedelbow.kivo.data.preferences.AppearanceRepository
import com.bustedelbow.kivo.data.preferences.AppearanceSettings
import com.bustedelbow.kivo.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Settings mirrors the persisted appearance and writes each choice back (issue #12). */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `reflects the persisted appearance`() =
        runTest {
            val repository =
                FakeAppearanceRepository(
                    AppearanceSettings(mode = AppearanceMode.DARK, dynamicColor = true),
                )
            val viewModel = SettingsViewModel(repository)

            val state = viewModel.uiState.first { it.mode == AppearanceMode.DARK }

            assertEquals(AppearanceMode.DARK, state.mode)
            assertTrue(state.dynamicColor)
        }

    @Test
    fun `setMode persists the choice`() =
        runTest {
            val repository = FakeAppearanceRepository()
            val viewModel = SettingsViewModel(repository)

            viewModel.setMode(AppearanceMode.LIGHT)

            assertEquals(AppearanceMode.LIGHT, repository.settings.first().mode)
            assertEquals(AppearanceMode.LIGHT, viewModel.uiState.first { it.mode == AppearanceMode.LIGHT }.mode)
        }

    @Test
    fun `setDynamicColor persists the choice`() =
        runTest {
            val repository = FakeAppearanceRepository()
            val viewModel = SettingsViewModel(repository)

            viewModel.setDynamicColor(true)

            assertTrue(repository.settings.first().dynamicColor)
            assertTrue(viewModel.uiState.first { it.dynamicColor }.dynamicColor)
        }
}

/** An in-memory [AppearanceRepository] for tests. */
private class FakeAppearanceRepository(
    initial: AppearanceSettings = AppearanceSettings(),
) : AppearanceRepository {
    private val state = MutableStateFlow(initial)

    override val settings: Flow<AppearanceSettings> = state

    override suspend fun setMode(mode: AppearanceMode) {
        state.value = state.value.copy(mode = mode)
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        state.value = state.value.copy(dynamicColor = enabled)
    }
}
