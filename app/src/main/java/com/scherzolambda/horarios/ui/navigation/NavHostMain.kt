package com.scherzolambda.horarios.ui.navigation

import android.webkit.WebView
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scherzolambda.horarios.R
import com.scherzolambda.horarios.data_transformation.download.DownloadResult
import com.scherzolambda.horarios.data_transformation.download.DownloadService
import com.scherzolambda.horarios.ui.screens.daily.DailyScreen
import com.scherzolambda.horarios.ui.screens.status.StatusScreen
import com.scherzolambda.horarios.ui.screens.week.WeeklyScreen
import com.scherzolambda.horarios.ui.screens.web.SigaaWebScreen
import com.scherzolambda.horarios.ui.theme.AppTypography
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.UfcatGreen
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import kotlinx.coroutines.launch

/**
 * Define as telas principais do aplicativo com suas rotas, rótulos e ícones.
 * Utilizado para configurar a barra de navegação inferior.
 */
sealed class Screen(val route: String, val label: String, val iconRes: Int) {
    object Daily : Screen("daily", "Hoje", R.drawable.ic_notebook_filled)
    object Weekly : Screen("weekly", "Semana", R.drawable.ic_calendar)
    object Status : Screen("status", "Status", R.drawable.ic_info)
    object Sigaa : Screen("sigaa", "SIGAA", R.drawable.ic_internet)
}

val screens = listOf(Screen.Daily, Screen.Weekly, Screen.Status, Screen.Sigaa)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Daily.route
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val downloadService = remember { DownloadService() }

    // Estado elevado para WebView
    var sigaaWebView by remember { mutableStateOf<WebView?>(null) }

    // Função de download
    val onDownloadClick = if (currentRoute == Screen.Sigaa.route) {
        {
            downloadService.handleDownload(
                context = context,
                webView = sigaaWebView,
                onResult = { result ->
                    scope.launch {
                        when (result) {
                            is DownloadResult.Success -> {
                                disciplinaViewModel.carregarDeArquivoHtml(result.filePath)
                                snackbarHostState.showSnackbar(result.message)
                            }
                            is DownloadResult.Error -> {
                                snackbarHostState.showSnackbar(result.message)
                            }
                        }
                    }
                },
                navController = navController
            )
        }
    } else null

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                showDownloadButton = currentRoute == Screen.Sigaa.route,
                onDownloadClick = onDownloadClick
            )
        },
        containerColor = LocalAppColors.current.content.grayElements,
        bottomBar = { BottomNavBar(navController, currentRoute) }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding,
            disciplinaViewModel = disciplinaViewModel,
            sigaaWebViewRef = { sigaaWebView = it },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    showDownloadButton: Boolean = false,
    onDownloadClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = LocalAppColors.current.content.background,
        ),
        title = {
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    painterResource(R.drawable.ic_logo_ufcat),
                    contentDescription = "icone da UFCAT",
                    modifier = Modifier.size(80.dp),
                    tint = Color.Unspecified
                )
                Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
            }
        },
        actions = {
            if (showDownloadButton && onDownloadClick != null) {
                IconButton(onClick = onDownloadClick) {
                    Icon(painterResource(
                        R.drawable.ic_download),
                        contentDescription = "Botão para Baixar HTML")
                }
            }
        }
    )
}

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String) {
    val items = remember { screens }
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }

    NavigationBar(containerColor = UfcatGreen) {
        items.forEachIndexed { index, screen ->
            key(screen.route) {
                val selected = index == selectedIndex
                val icon = painterResource(screen.iconRes)
                val fontWeight by animateIntAsState(targetValue = if (selected) 700 else 400)

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    label = {
                        Text(
                            text = screen.label,
                            fontSize = 18.sp,
                            fontWeight = FontWeight(fontWeight),
                            color = if (selected) UfcatBlack else Color.White.copy(alpha = 0.7f)
                        )
                    },
                    icon = {
                        Icon(
                            painter = icon,
                            contentDescription = screen.label,
                            modifier = Modifier.size(24.dp),
                            tint = UfcatBlack
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UfcatBlack,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}



@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    disciplinaViewModel: DisciplinaViewModel,
    sigaaWebViewRef: (WebView?) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Daily.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Daily.route) { DailyScreen(innerPadding, disciplinaViewModel) }
        composable(Screen.Weekly.route) { WeeklyScreen(disciplinaViewModel) }
        composable(Screen.Status.route) { StatusScreen(disciplinaViewModel) }
        composable(Screen.Sigaa.route) {
            SigaaWebScreen(
                webViewRef = sigaaWebViewRef,
            )
        }
    }
}

