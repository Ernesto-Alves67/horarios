package com.scherzolambda.horarios.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scherzolambda.horarios.R
import com.scherzolambda.horarios.ui.screens.DailyScreen
import com.scherzolambda.horarios.ui.screens.StatusScreen
import com.scherzolambda.horarios.ui.screens.WeeklyScreen
import com.scherzolambda.horarios.ui.theme.UFCATGreen

// Agora a sealed class aceita tanto ImageVector quanto Painter
sealed class Screen(val route: String, val label: String, val iconRes: Int) {
    object Daily : Screen("daily", "Hoje", R.drawable.ic_notebook_filled)
    object Weekly : Screen("weekly", "Semana", R.drawable.ic_calendar)
    object Status : Screen("status", "Status", R.drawable.ic_info)
}

val screens = listOf(Screen.Daily, Screen.Weekly, Screen.Status)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Daily.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Horários") }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = UFCATGreen) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    // Navegação mais fluida e sem recriar telas
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = { Text(screen.label) },
                        icon = {
                            Icon(painter = painterResource(screen.iconRes),
                                contentDescription = screen.label,
                                modifier = Modifier.size(28.dp))
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Daily.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Daily.route) { DailyScreen(innerPadding) }
            composable(Screen.Weekly.route) { WeeklyScreen(innerPadding) }
            composable(Screen.Status.route) { StatusScreen() }
        }
    }
}
