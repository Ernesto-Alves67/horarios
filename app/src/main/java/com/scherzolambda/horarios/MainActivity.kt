package com.scherzolambda.horarios

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.ApplicationTheme
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.UfcatGreen
import com.scherzolambda.horarios.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val isSplashVisible = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isSplashVisible.value }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configurar as cores das barras dinamicamente
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        updateStatusBarAppearance()

        lifecycleScope.launch {
            delay(600)
            isSplashVisible.value = false
        }

        AuthViewModel().initializeApp()
        setContent {
            ApplicationTheme {
                MainNavigation()
            }
        }
    }
    private fun updateStatusBarAppearance() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        val corDoFundo = getColor(R.color.ufcat_green)
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            controller.isAppearanceLightStatusBars = false
        } else {
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = false
            window.navigationBarColor = 0
        }
    }

}
