package com.bustedelbow.kivo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bustedelbow.kivo.data.preferences.AppearanceMode
import com.bustedelbow.kivo.data.preferences.AppearanceRepository
import com.bustedelbow.kivo.data.preferences.AppearanceSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Drives Settings: it mirrors the persisted appearance and writes each choice back, so the theme
 * reacts immediately and the choice survives the next launch.
 */
class SettingsViewModel(
    private val appearanceRepository: AppearanceRepository,
) : ViewModel() {
    /** The persisted appearance, and what the theme reads; a sensible default until the store answers. */
    val uiState: StateFlow<AppearanceSettings> =
        appearanceRepository.settings
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = AppearanceSettings(),
            )

    /** Persists [mode] as the appearance choice. */
    fun setMode(mode: AppearanceMode) {
        viewModelScope.launch { appearanceRepository.setMode(mode) }
    }

    /** Persists whether dynamic (wallpaper) colour is enabled. */
    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { appearanceRepository.setDynamicColor(enabled) }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        fun factory(repository: AppearanceRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { SettingsViewModel(repository) }
            }
    }
}
