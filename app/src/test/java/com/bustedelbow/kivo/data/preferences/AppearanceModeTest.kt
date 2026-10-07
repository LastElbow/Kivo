package com.bustedelbow.kivo.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The persisted appearance resolves to the dark-theme flag the theme takes (issue #12). */
class AppearanceModeTest {
    @Test
    fun `light forces light regardless of the device`() {
        assertFalse(AppearanceMode.LIGHT.isDark(systemInDarkTheme = true))
        assertFalse(AppearanceMode.LIGHT.isDark(systemInDarkTheme = false))
    }

    @Test
    fun `dark forces dark regardless of the device`() {
        assertTrue(AppearanceMode.DARK.isDark(systemInDarkTheme = true))
        assertTrue(AppearanceMode.DARK.isDark(systemInDarkTheme = false))
    }

    @Test
    fun `system follows the device`() {
        assertTrue(AppearanceMode.SYSTEM.isDark(systemInDarkTheme = true))
        assertFalse(AppearanceMode.SYSTEM.isDark(systemInDarkTheme = false))
    }

    @Test
    fun `an unknown stored value falls back to system`() {
        assertEquals(AppearanceMode.SYSTEM, AppearanceMode.fromStored("SEPIA"))
        assertEquals(AppearanceMode.SYSTEM, AppearanceMode.fromStored(null))
        assertEquals(AppearanceMode.DARK, AppearanceMode.fromStored("DARK"))
    }
}
