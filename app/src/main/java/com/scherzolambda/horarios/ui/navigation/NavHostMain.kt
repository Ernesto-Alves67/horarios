package com.scherzolambda.horarios.ui.navigation

import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithCache
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.scherzolambda.horarios.ui.theme.UfcatOrange
import com.scherzolambda.horarios.ui.theme.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

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
                Text("Horários", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
            }
        }
    )
}

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = UFCATGreen) {
        screens.forEach { screen ->
            val selected = currentRoute == screen.route
            val fontWeight by animateIntAsState(targetValue = if (selected) 700 else 400)
            NavigationBarItem(
                selected = selected,
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
                // bold the label when this item is selected
                label = { Text(screen.label, fontSize = 18.sp, fontWeight = FontWeight(fontWeight)) },
                // Use the GradientIcon so we can render a linear gradient inside the icon shape
                icon = {
                    val painter = painterResource(id = screen.iconRes)
                    val gradient = if (selected) {
                        // selected gradient (you can customize these colors)
                        Brush.horizontalGradient(listOf(UfcatOrange, Color(0xFFFF3366)))
                    } else {
                        // unselected subtle gradient
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.6f)))
                    }
                    GradientIcon(
                        painter = painter,
                        contentDescription = screen.label,
                        modifier = Modifier.size(if (selected) 32.dp else 24.dp),
                        brush = gradient
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF6600),
                    unselectedIconColor = Color.White.copy(0.7f),
                    selectedTextColor = Color(0xFFFF6600),
                    unselectedTextColor = Color.White.copy(0.7f),
                    // Remove default circular indicator so the icon silhouette (gradient)
                    // is visible without a circle behind it.
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

/**
 * Draws [painter] then paints [brush] using BlendMode.SrcIn so the gradient is masked by
 * the painted icon shape — producing a gradient-colored icon.
 */
@Composable
fun GradientIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    brush: Brush
) {
    // This implementation draws the painter to a temporary layer, then paints the
    // gradient with BlendMode.SrcIn into that layer so the gradient is visible only
    // where the painter has alpha. This is robust for both vectors and bitmaps.
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.drawWithCache {
            onDrawWithContent {
                // create a paint (no-op) and save a layer
                val paint = androidx.compose.ui.graphics.Paint()
                val layerBounds = Rect(Offset.Zero, size)
                // saveLayer -> draw painter -> draw gradient with SrcIn -> restore
                drawContext.canvas.saveLayer(layerBounds, paint)
                // draw the icon into the layer
                drawContent()
                // paint the gradient using SrcIn so it remains only where the icon is opaque
                drawRect(brush = brush, blendMode = BlendMode.SrcIn)
                drawContext.canvas.restore()
            }
        },
        // Use white tint so vector paths become an opaque mask. For bitmaps, this will
        // apply a color filter; ensure bitmaps have transparency for the correct mask.
        tint = Color.White
    )
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
