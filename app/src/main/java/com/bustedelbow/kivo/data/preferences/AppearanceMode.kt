package com.bustedelbow.kivo.data.preferences

/**
 * The user's chosen appearance for the app: a fixed Light or Dark scheme, or a [SYSTEM] that follows
 * the device. Persisted by [AppearanceRepository] and resolved by the theme.
 */
enum class AppearanceMode {
    LIGHT,
    DARK,
    SYSTEM,
    ;

    /**
     * Whether the dark colour scheme should be used, given the device's current setting. Only
     * [SYSTEM] defers to [systemInDarkTheme]; [LIGHT] and [DARK] force the answer.
     */
    fun isDark(systemInDarkTheme: Boolean): Boolean =
        when (this) {
            LIGHT -> false
            DARK -> true
            SYSTEM -> systemInDarkTheme
        }

    companion object {
        /** Reads a mode stored under [name], falling back to [SYSTEM] for a missing or unknown value. */
        fun fromStored(name: String?): AppearanceMode = entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}
