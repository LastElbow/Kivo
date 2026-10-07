package com.bustedelbow.kivo.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bustedelbow.kivo.KivoApplication
import com.bustedelbow.kivo.ui.accounts.AccountsScreen
import com.bustedelbow.kivo.ui.addentry.AddEntryScreen
import com.bustedelbow.kivo.ui.home.HomeScreen
import com.bustedelbow.kivo.ui.navigation.KivoDestination
import com.bustedelbow.kivo.ui.navigation.KivoRoute
import com.bustedelbow.kivo.ui.settings.SettingsScreen

/** The window width at which the shell swaps its bottom bar for a navigation rail. */
private val NavigationRailMinWidth = 600.dp

/**
 * The app shell: a [Scaffold] with an expressive navigation area over a [NavHost] of the three
 * top-level destinations — a [ShortNavigationBar] on compact windows and a [WideNavigationRail] on
 * medium and wider ones. It provides the [LocalAppContainer] every screen reads its repositories
 * from, and hands each screen the Scaffold's [PaddingValues] so content can scroll behind the
 * system bars.
 */
@Composable
fun KivoApp(modifier: Modifier = Modifier) {
    val container = (LocalContext.current.applicationContext as KivoApplication).container
    CompositionLocalProvider(LocalAppContainer provides container) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val isTopLevelDestination = KivoDestination.entries.any { it.route == currentRoute }
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val useRail = maxWidth >= NavigationRailMinWidth
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (!useRail && isTopLevelDestination) {
                        KivoShortNavigationBar(navController, currentRoute)
                    }
                },
            ) { innerPadding ->
                Row(modifier = Modifier.fillMaxSize()) {
                    if (useRail && isTopLevelDestination) {
                        KivoWideNavigationRail(navController, currentRoute)
                    }
                    KivoNavHost(
                        navController = navController,
                        contentPadding = innerPadding,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun KivoNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = KivoDestination.HOME.route,
        modifier = modifier.fillMaxSize(),
    ) {
        composable(KivoDestination.HOME.route) {
            HomeScreen(
                contentPadding = contentPadding,
                onCreateAccount = {
                    navController.navigateToTopLevel(KivoDestination.ACCOUNTS)
                },
                onAddEntry = {
                    navController.navigate(KivoRoute.ADD_ENTRY)
                },
            )
        }
        composable(KivoDestination.ACCOUNTS.route) {
            AccountsScreen(contentPadding = contentPadding)
        }
        composable(KivoDestination.SETTINGS.route) {
            SettingsScreen(contentPadding = contentPadding)
        }
        composable(KivoRoute.ADD_ENTRY) {
            AddEntryScreen(
                contentPadding = contentPadding,
                onDone = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun KivoShortNavigationBar(
    navController: NavHostController,
    currentRoute: String?,
) {
    ShortNavigationBar {
        KivoDestination.entries.forEach { destination ->
            ShortNavigationBarItem(
                modifier = Modifier.testTag(destination.navItemTestTag),
                selected = currentRoute == destination.route,
                onClick = { navController.navigateToTopLevel(destination) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(text = stringResource(destination.labelRes)) },
            )
        }
    }
}

@Composable
private fun KivoWideNavigationRail(
    navController: NavHostController,
    currentRoute: String?,
) {
    val railState = rememberWideNavigationRailState()
    WideNavigationRail(state = railState) {
        KivoDestination.entries.forEach { destination ->
            WideNavigationRailItem(
                modifier = Modifier.testTag(destination.navItemTestTag),
                selected = currentRoute == destination.route,
                onClick = { navController.navigateToTopLevel(destination) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(text = stringResource(destination.labelRes)) },
                railExpanded = railState.targetValue == WideNavigationRailValue.Expanded,
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
