package com.bustedelbow.kivo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bustedelbow.kivo.ui.accounts.AccountsScreen
import com.bustedelbow.kivo.ui.home.HomeScreen
import com.bustedelbow.kivo.ui.navigation.KivoDestination
import com.bustedelbow.kivo.ui.settings.SettingsScreen

/**
 * The app shell: a [Scaffold] with a bottom navigation bar over a [NavHost] of the three
 * top-level destinations. Each destination's real content arrives in a later slice.
 */
@Composable
fun KivoApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { KivoBottomBar(navController = navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = KivoDestination.HOME.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(KivoDestination.HOME.route) { HomeScreen() }
            composable(KivoDestination.ACCOUNTS.route) { AccountsScreen() }
            composable(KivoDestination.SETTINGS.route) { SettingsScreen() }
        }
    }
}

@Composable
private fun KivoBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        KivoDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { navController.navigateToTopLevel(destination) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(text = stringResource(destination.labelRes)) },
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(destination: KivoDestination) {
    navigate(destination.route) {
        // One instance per tab: never stack duplicates, and restore each tab's saved state.
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
