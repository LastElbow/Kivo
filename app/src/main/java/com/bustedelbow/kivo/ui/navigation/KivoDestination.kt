package com.bustedelbow.kivo.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.bustedelbow.kivo.R

/**
 * The three top-level destinations reachable from the bottom navigation bar.
 *
 * They live in one enum so the bar and the [androidx.navigation.NavHost] cannot drift apart.
 */
enum class KivoDestination(
    val route: String,
    @get:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    HOME(
        route = "home",
        labelRes = R.string.destination_home,
        icon = Icons.Filled.Home,
    ),
    ACCOUNTS(
        route = "accounts",
        labelRes = R.string.destination_accounts,
        icon = Icons.AutoMirrored.Filled.List,
    ),
    SETTINGS(
        route = "settings",
        labelRes = R.string.destination_settings,
        icon = Icons.Filled.Settings,
    ),
}
