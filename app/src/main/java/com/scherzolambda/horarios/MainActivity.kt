package com.scherzolambda.horarios

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.ApplicationTheme
import com.scherzolambda.horarios.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.scherzolambda.horarios.viewmodel.AuthViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.scherzolambda.horarios.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val isSplashVisible = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isSplashVisible.value }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configurar as cores das barras dinamicamente
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        window.statusBarColor = android.graphics.Color.TRANSPARENT
//        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        lifecycleScope.launch {
            delay(600)
            isSplashVisible.value = false
        }

        AuthViewModel().initializeApp()
        val themeViewModel: ThemeViewModel by viewModels()
        setContent {
            val appTheme by themeViewModel.theme.collectAsState()
            StatusBarAppearanceUpdater(appTheme) { isDark ->
                updateStatusBarAppearance(isDark)
            }
            ApplicationTheme(appTheme) {
                MainNavigation()
            }
        }
    }
    private fun updateStatusBarAppearance(isDark: Boolean) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (isDark) {
            controller.isAppearanceLightStatusBars = false // ícones claros
        } else {
            controller.isAppearanceLightStatusBars = true // ícones escuros
            controller.isAppearanceLightNavigationBars = false
            window.navigationBarColor = 0
        }
    }

}

@Composable
fun StatusBarAppearanceUpdater(appTheme: AppTheme, onUpdate: (Boolean) -> Unit) {
    val isDark = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    LaunchedEffect(isDark) {
        onUpdate(isDark)
    }
}
