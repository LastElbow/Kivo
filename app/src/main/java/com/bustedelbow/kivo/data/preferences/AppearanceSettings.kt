package com.bustedelbow.kivo.data.preferences

/**
 * How Kivo should look: the chosen [mode] and whether dynamic (wallpaper) colour is on. The default
 * follows the device with dynamic colour off, matching `KivoTheme`'s own defaults.
 */
data class AppearanceSettings(
    val mode: AppearanceMode = AppearanceMode.SYSTEM,
    val dynamicColor: Boolean = false,
)
