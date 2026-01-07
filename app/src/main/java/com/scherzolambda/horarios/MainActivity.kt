package com.scherzolambda.horarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.AppTheme
import com.scherzolambda.horarios.ui.theme.ApplicationTheme
import com.scherzolambda.horarios.ui.theme.ThemeViewModel
import com.scherzolambda.horarios.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

//@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        // Configuração da interface do usuário com o tema atual
        setContent {
            val appTheme by themeViewModel.theme.collectAsState()

            StatusBarAppearanceUpdater(appTheme) { isDark ->
                updateStatusBarAppearance(isDark)
            }

            // Aplica o tema selecionado e renderiza a navegação principal.
            ApplicationTheme(appTheme) {
                MainNavigation()
            }
        }
    }

    private fun updateStatusBarAppearance(isDark: Boolean) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = !isDark // Ícones escuros se tema for escuro
        controller.isAppearanceLightNavigationBars = false

        if (!isDark) {
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

    // Atualiza a aparência da barra de status quando o tema mudar
    LaunchedEffect(isDark) {
        onUpdate(isDark)
    }
}
