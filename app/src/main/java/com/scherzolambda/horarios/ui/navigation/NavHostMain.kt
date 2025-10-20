package com.scherzolambda.horarios.ui.navigation

import android.webkit.WebView
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scherzolambda.horarios.R
import com.scherzolambda.horarios.data_transformation.download.DownloadResult
import com.scherzolambda.horarios.data_transformation.download.DownloadService
import com.scherzolambda.horarios.ui.navigation.components.BottomNavBar
import com.scherzolambda.horarios.ui.navigation.components.MainContainer
import com.scherzolambda.horarios.ui.screens.config.ConfigScreen
import com.scherzolambda.horarios.ui.screens.config.policy.PrivacyPolicyScreen
import com.scherzolambda.horarios.ui.screens.config.usercontract.UserAgreementScreen
import com.scherzolambda.horarios.ui.screens.daily.DailyScreen
import com.scherzolambda.horarios.ui.screens.status.StatusScreen
import com.scherzolambda.horarios.ui.screens.web.SigaaWebScreen
import com.scherzolambda.horarios.ui.screens.week.WeeklyScreen
import com.scherzolambda.horarios.ui.theme.ThemeViewModel
import com.scherzolambda.horarios.viewmodels.ConfigViewModel
import com.scherzolambda.horarios.viewmodels.DisciplinaViewModel
import com.scherzolambda.horarios.viewmodels.UpdateViewModel
import kotlinx.coroutines.launch

/**
 * Define as telas principais do aplicativo com suas rotas, rótulos e ícones.
 * Utilizado para configurar a barra de navegação inferior.
 *
 * @see BottomNavBar
 */
sealed class Screen(val route: String, val label: String, val iconRes: Int) {
    object Daily : Screen("daily", "Hoje", R.drawable.ic_notebook_filled)
    object Weekly : Screen("weekly", "Semana", R.drawable.ic_calendar)
    object Status : Screen("status", "Status", R.drawable.ic_info)
    object Sigaa : Screen("sigaa", "SIGAA", R.drawable.ic_internet)
}

sealed class OuterScreen(val route: String) {
    object Config : OuterScreen("config")
    object PrivacyPolicy : OuterScreen("privacy_policy")
    object UserContract : OuterScreen("user_contract")
}

val screens = listOf(Screen.Daily, Screen.Weekly, Screen.Status, Screen.Sigaa)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val updateViewModel: UpdateViewModel = hiltViewModel()
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val configViewModel: ConfigViewModel = hiltViewModel()
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

    AppNavHost(
        navController = navController,
        disciplinaViewModel = disciplinaViewModel,
        updateViewModel = updateViewModel,
        themeViewModel = themeViewModel,
        configViewModel = configViewModel,
        sigaaWebViewRef = { sigaaWebView = it },
        onDownloadClick = onDownloadClick,
        snackbarHostState = snackbarHostState
    )
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    disciplinaViewModel: DisciplinaViewModel,
    updateViewModel: UpdateViewModel,
    themeViewModel: ThemeViewModel,
    configViewModel: ConfigViewModel,
    sigaaWebViewRef: (WebView?) -> Unit,
    onDownloadClick: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState
) {
    val horariosSemanalState by disciplinaViewModel.weeklySchedule.collectAsStateWithLifecycle()
    val disciplinasHoje by disciplinaViewModel.todaysSchedule.collectAsStateWithLifecycle()
    val isLoading by disciplinaViewModel.isLoading.collectAsStateWithLifecycle()
    val isShowEmptyCells by configViewModel.showEmptyWeeklyCell.collectAsStateWithLifecycle()
    val isShowEmptyDailyCells by configViewModel.showEmptyDailyCell.collectAsStateWithLifecycle()
    val updateInfoData = updateViewModel.updateInfo
    NavHost(
        navController = navController,
        startDestination = Screen.Daily.route,
//        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Daily.route) {
            MainContainer(
                snackbarHostState = snackbarHostState,
                navController = navController,
                currentRoute = Screen.Daily.route,
                content = { innerPadding ->
                    DailyScreen(
                        paddingValues = innerPadding,
                        disciplinasHoje= disciplinasHoje,
                        updateInfo = updateInfoData,
                        isLoading = isLoading,
                        isShowEmptyCells = isShowEmptyDailyCells,
                    )
                }
            )

        }
        composable(Screen.Weekly.route) {
            MainContainer(
                snackbarHostState = snackbarHostState,
                navController = navController,
                currentRoute = Screen.Weekly.route,
                content = { innerPadding ->
                    WeeklyScreen(
                        horarios = horariosSemanalState,
                        isLoading = isLoading,
                        isShowEmptyCells = isShowEmptyCells,
                        paddingValues = innerPadding)
                }
            )

        }
        composable(Screen.Status.route) {
            MainContainer(
                snackbarHostState = snackbarHostState,
                navController = navController,
                currentRoute = Screen.Status.route,
                content = { innerPadding ->
                    StatusScreen(disciplinaViewModel, innerPadding)
                }
            ) }
        composable(Screen.Sigaa.route) {
            MainContainer(
                snackbarHostState = snackbarHostState,
                navController = navController,
                currentRoute = Screen.Sigaa.route,
                onDownloadClick = onDownloadClick,
                content = { innerPadding ->
                    SigaaWebScreen(
                        webViewRef = sigaaWebViewRef,
                    )
                }
            )

        }

        composable(OuterScreen.Config.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }) {
            ConfigScreen(
                themeViewModel = themeViewModel,
                onBack = { navController.popBackStack() },
                configViewModel = configViewModel,
                onNavigateToPrivacyPolicy = {
                    navController.navigate(OuterScreen.PrivacyPolicy.route)
                },
                onNavigateToUserContract = {
                    navController.navigate(OuterScreen.UserContract.route)
                }
            )
        }

        composable(OuterScreen.PrivacyPolicy.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }){
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(OuterScreen.UserContract.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }){
            UserAgreementScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

