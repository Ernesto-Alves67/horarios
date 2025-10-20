package com.scherzolambda.horarios.ui.navigation.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
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
import com.scherzolambda.horarios.ui.navigation.OuterScreen
import com.scherzolambda.horarios.ui.navigation.Screen
import com.scherzolambda.horarios.ui.navigation.screens
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.ui.theme.UfcatGreen


@Composable
fun MainContainer(
    content: @Composable (PaddingValues) -> Unit = {},
    onDownloadClick: (() -> Unit)? = null,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    navController: NavHostController,
    currentRoute: String
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
        containerColor = LocalAppColors.current.content.grayElements,
        bottomBar = { BottomNavBar(navController, currentRoute) }
    ) { innerPadding ->
        content(innerPadding)
    }
}



@Preview
@Composable
fun TopBarPreview() {
    TopBar(showDownloadButton = false, onDownloadClick = {}, onConfigClick = {})
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
                        modifier = Modifier.size(40.dp)
                            .padding(3.dp))
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
