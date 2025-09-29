package com.scherzolambda.horarios.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.scherzolambda.horarios.ui.theme.UfcatOrange
import com.scherzolambda.horarios.ui.theme.White

/**
 * Define as telas principais do aplicativo com suas rotas, rótulos e ícones.
 * Utilizado para configurar a barra de navegação inferior.
 */
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
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Daily.route

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(navController, currentRoute) }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding,
            disciplinaViewModel = disciplinaViewModel
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    painterResource(R.drawable.ic_logo_ufcat),
                    contentDescription = "icone da UFCAT",
                    modifier = Modifier.size(80.dp),
                    tint = White
                )
                Text("Horários", fontWeight = Bold, textAlign = TextAlign.Justify)
            }
        }
    )
}

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = UFCATGreen) {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
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
                    Icon(
                        painter = painterResource(screen.iconRes),
                        contentDescription = screen.label,
                        modifier = Modifier.size(28.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF6600),
                    unselectedIconColor = Color.White.copy(0.7f),
                    selectedTextColor = Color(0xFFFF6600),
                    unselectedTextColor = Color.White.copy(0.7f),
                    indicatorColor = Color.White.copy(0.15f)
                )
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    disciplinaViewModel: DisciplinaViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Daily.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Daily.route) { DailyScreen(innerPadding, disciplinaViewModel) }
        composable(Screen.Weekly.route) { WeeklyScreen(innerPadding, disciplinaViewModel) }
        composable(Screen.Status.route) { StatusScreen(disciplinaViewModel) }
    }
}

