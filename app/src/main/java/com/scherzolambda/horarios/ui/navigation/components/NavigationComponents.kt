package com.scherzolambda.horarios.ui.navigation.components

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.scherzolambda.horarios.R
import com.scherzolambda.horarios.data_transformation.Disciplina
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.ui.navigation.OuterScreen
import com.scherzolambda.horarios.ui.navigation.Screen
import com.scherzolambda.horarios.ui.navigation.screens
import com.scherzolambda.horarios.ui.screens.daily.DailyScreen
import com.scherzolambda.horarios.ui.screens.status.StatusScreen
import com.scherzolambda.horarios.ui.screens.web.SigaaWebScreen
import com.scherzolambda.horarios.ui.screens.week.WeeklyScreen
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.ui.theme.UfcatGreen
import com.scherzolambda.horarios.viewmodels.AppUpdateInfo
import com.scherzolambda.horarios.viewmodels.DisciplinaViewModel
import kotlinx.coroutines.launch


@Composable
fun MainContainer(
    content: @Composable (PaddingValues) -> Unit = {},
    onDownloadClick: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    currentRoute: String
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF297A7D)) // 👈 SUA COR
    ) {

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBar(
                    showDownloadButton = currentRoute == Screen.Sigaa.route,
                    onDownloadClick = onDownloadClick,
                    onConfigClick= {navController.navigate(OuterScreen.Config.route)}
                )
            },
            containerColor = LocalAppColors.current.content.primary,
            bottomBar = { BottomNavBar(navController, currentRoute) }
        ) { innerPadding ->
            content(innerPadding)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    showDownloadButton: Boolean = false,
    onDownloadClick: (() -> Unit)? = null,
    onConfigClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = LocalAppColors.current.content.background,
        ),
        title = {
            ConstraintLayout {
                // Create references for the composables
                val (icon, text) = createRefs()

                // Icon
                Icon(
                    painter = painterResource(R.drawable.ic_logo_ufcat),
                    contentDescription = "icone da UFCAT",
                    modifier = Modifier
                        .size(65.dp)
                        .constrainAs(icon) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    tint = Color.Unspecified
                )

                // Text
                Text(
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.constrainAs(text) {
                        start.linkTo(icon.end)
                        bottom.linkTo(icon.bottom, margin = 5.dp)
                    }
                )
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
            if (onConfigClick != null && !showDownloadButton) {

                IconButton(onConfigClick) {
                    Icon(
                        painterResource(R.drawable.ic_settings3),
                        contentDescription = "Configurações",
                        tint = LocalAppColors.current.content.primary,
                        modifier = Modifier.size(36.dp)
                            .padding(4.dp),
                    )
                }
            }
        }
    )
}
@Preview
@Composable
fun TopBarPreview() {
    TopBar(showDownloadButton = false, onDownloadClick = {}, onConfigClick = {})
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
//                    val fontWeight by animateIntAsState(targetValue = if (selected) 700 else 400)

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
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
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

// ============= EXPERIMENTAL ==================
//@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen2(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,

    // states
    horariosSemanalState: List<HorarioSemanal>, // ajusta pro seu tipo
    disciplinasHoje: List<HorarioSemanal>,
    disciplinasTotal: List<Disciplina>,
    isLoading: Boolean,
    isShowEmptyCells: Boolean,
    isShowEmptyDailyCells: Boolean,

    // actions
    updateInfoData: AppUpdateInfo, // ajusta pro seu tipo
    onDownloadClick: (() -> Unit)?,
    sigaaWebViewRef: (WebView?) -> Unit,
    disciplinaViewModel: DisciplinaViewModel
) {

    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    val currentRoute = when (pagerState.currentPage) {
        0 -> Screen.Daily.route
        1 -> Screen.Weekly.route
        2 -> Screen.Status.route
        3 -> Screen.Sigaa.route
        else -> Screen.Daily.route
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LocalAppColors.current.content.grayElements,

        topBar = {
            TopBar(
                showDownloadButton = currentRoute == Screen.Sigaa.route,
                onDownloadClick = onDownloadClick,
                onConfigClick = {
                    navController.navigate(OuterScreen.Config.route)
                }
            )
        },

        bottomBar = {
            BottomNavBar2(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    val index = when (route) {
                        Screen.Daily.route -> 0
                        Screen.Weekly.route -> 1
                        Screen.Status.route -> 2
                        Screen.Sigaa.route -> 3
                        else -> 0
                    }

                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }

    ) { innerPadding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(LocalAppColors.current.content.grayElements)
                .padding(innerPadding)
        ) { page ->

            when (page) {

                0 -> DailyScreen(
                    paddingValues = PaddingValues(),
                    disciplinasHoje = disciplinasHoje,
                    updateInfo = updateInfoData,
                    isLoading = isLoading,
                    isShowEmptyCells = isShowEmptyDailyCells
                )

                1 -> WeeklyScreen(
                    horarios = horariosSemanalState,
                    isLoading = isLoading,
                    isShowEmptyCells = isShowEmptyCells,
                    paddingValues = PaddingValues()
                )

                2 -> StatusScreen(
                    totalDisciplinas = disciplinasTotal,
                    paddingValues = PaddingValues(),
                    isLoading = isLoading,
                    onLoadFileClick = {
                        disciplinaViewModel.carregarDeArquivoHtml(it)
                    },
                    onClickButton = disciplinaViewModel::updateUserTest
                )

                3 -> SigaaWebScreen(
                    paddingValues = PaddingValues(),
                    webViewRef = sigaaWebViewRef
                )
            }
        }
    }




}


@Composable
fun BottomNavBar2(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    val items = remember { screens }
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }
    NavigationBar(containerColor = UfcatGreen)  {

        items.forEachIndexed { index, screen ->
            key(screen.route) {
                val selected = index == selectedIndex
                val icon = painterResource(screen.iconRes)
//                    val fontWeight by animateIntAsState(targetValue = if (selected) 700 else 400)

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            onItemClick(screen.route)
                        }
                    },
                    label = {
                        Text(
                            text = screen.label,
                            fontSize = 18.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
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
                        indicatorColor = Color.White.copy(alpha = 1.7f)
                    )
                )
            }
        }
    }
}