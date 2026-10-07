package com.bustedelbow.kivo.ui.navigation

/**
 * Routes that are pushed full-screen rather than shown as bottom-bar tabs. Keeping them beside
 * [KivoDestination] stops the two sets drifting apart.
 */
object KivoRoute {
    /** The full-screen Add Entry flow. */
    const val ADD_ENTRY = "add_entry"

    /** Stable tag for the Add Entry screen, shared with UI tests. */
    const val ADD_ENTRY_TEST_TAG = "add_entry_screen"
}
