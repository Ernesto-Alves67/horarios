package com.scherzolambda.horarios.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scherzolambda.horarios.ui.screens.DailyScreen
import com.scherzolambda.horarios.ui.screens.StatusScreen
import com.scherzolambda.horarios.ui.screens.WeeklyScreen
import com.scherzolambda.horarios.ui.theme.HorariosTheme
import com.scherzolambda.horarios.ui.theme.UFCATGreen



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHost() {
    val items = listOf("Hoje", "Semana", "Status")
    var selectedIndex by remember { mutableStateOf(0) }
    val icons = listOf(
        Icons.Filled.Call, // Diário
        Icons.Filled.DateRange,     // Semanal
        Icons.Filled.Info           // Status
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { items.size  }
    )

    // Sincroniza o pager quando selectedIndex muda
    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Horarios") }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = UFCATGreen
            ) {
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(label) },
                        icon = { Icon(imageVector = icons[index], contentDescription = label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Removido .padding(innerPadding) do HorizontalPager para evitar conflitos de scroll/padding
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> DailyScreen()
                1 -> WeeklyScreen(innerPadding)
                2 -> StatusScreen()
            }
        }
    }
    // Sincroniza o bottom navigation com o pager
    LaunchedEffect(pagerState.currentPage) {
        if (selectedIndex != pagerState.currentPage) {
            selectedIndex = pagerState.currentPage
        }
    }
}