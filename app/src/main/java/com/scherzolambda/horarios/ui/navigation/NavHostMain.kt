package com.scherzolambda.horarios.ui.navigation

import android.webkit.WebView
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.scherzolambda.horarios.ui.navigation.components.TopBar
import com.scherzolambda.horarios.ui.screens.config.ConfigScreen
import com.scherzolambda.horarios.ui.screens.config.policy.PrivacyPolicyScreen
import com.scherzolambda.horarios.ui.screens.config.usercontract.UserAgreementScreen
import com.scherzolambda.horarios.viewmodels.ConfigViewModel
import com.scherzolambda.horarios.ui.screens.daily.DailyScreen
import com.scherzolambda.horarios.ui.screens.status.StatusScreen
import com.scherzolambda.horarios.ui.screens.web.SigaaWebScreen
import com.scherzolambda.horarios.ui.screens.week.WeeklyScreen
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.ThemeViewModel
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.ui.theme.UfcatGreen
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
//
//    if (currentRoute == OuterScreen.Config.route) {
//
//        ConfigScreen(
//            onBack = { navController.popBackStack() },
//            themeViewModel = themeViewModel,
//            configViewModel = configViewModel
//        )
//
//        return
//    }
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        topBar = {
//            TopBar(
//                showDownloadButton = currentRoute == Screen.Sigaa.route,
//                onDownloadClick = onDownloadClick,
//                onConfigClick= {navController.navigate(OuterScreen.Config.route)}
//            )
//        },
//        containerColor = LocalAppColors.current.content.grayElements,
//        bottomBar = { BottomNavBar(navController, currentRoute) }
//    ) { innerPadding ->
//        AppNavHost(
//            navController = navController,
//            innerPadding = innerPadding,
//            disciplinaViewModel = disciplinaViewModel,
//            updateViewModel = updateViewModel,
//            themeViewModel = themeViewModel,
//            configViewModel = configViewModel,
//            sigaaWebViewRef = { sigaaWebView = it },
//        )
//    }
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
        composable(OuterScreen.Config.route) {
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

        composable(OuterScreen.PrivacyPolicy.route){
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(OuterScreen.UserContract.route){
            UserAgreementScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

