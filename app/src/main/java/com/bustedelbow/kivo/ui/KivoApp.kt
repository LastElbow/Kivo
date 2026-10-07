package com.bustedelbow.kivo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bustedelbow.kivo.KivoApplication
import com.bustedelbow.kivo.ui.accounts.AccountsScreen
import com.bustedelbow.kivo.ui.home.HomeScreen
import com.bustedelbow.kivo.ui.navigation.KivoDestination
import com.bustedelbow.kivo.ui.settings.SettingsScreen

/**
 * The app shell: a [Scaffold] with a bottom navigation bar over a [NavHost] of the three top-level
 * destinations. It provides the [LocalAppContainer] every screen reads its repositories from, and
 * hands each screen the Scaffold's [androidx.compose.foundation.layout.PaddingValues] so content
 * can scroll behind the system bars.
 */
@Composable
fun KivoApp(modifier: Modifier = Modifier) {
    val container = (LocalContext.current.applicationContext as KivoApplication).container
    CompositionLocalProvider(LocalAppContainer provides container) {
        val navController = rememberNavController()
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = { KivoBottomBar(navController = navController) },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = KivoDestination.HOME.route,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(KivoDestination.HOME.route) {
                    HomeScreen(
                        contentPadding = innerPadding,
                        onCreateAccount = {
                            navController.navigateToTopLevel(KivoDestination.ACCOUNTS)
                        },
                    )
                }
                composable(KivoDestination.ACCOUNTS.route) {
                    AccountsScreen(contentPadding = innerPadding)
                }
                composable(KivoDestination.SETTINGS.route) {
                    SettingsScreen(contentPadding = innerPadding)
                }
            }
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
                modifier = Modifier.testTag(destination.navItemTestTag),
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
