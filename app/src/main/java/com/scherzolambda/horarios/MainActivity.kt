package com.scherzolambda.horarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.ApplicationTheme
import com.scherzolambda.horarios.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import com.scherzolambda.horarios.viewmodels.AuthViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.scherzolambda.horarios.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instala o SplashScreen e controla a visibilidade.
        installSplashScreen().apply {
            setKeepOnScreenCondition { authViewModel.isSplashVisible.value }
        }

        enableEdgeToEdge()

        // Configuração da interface do usuário com o tema atual
        setContent {
            val appTheme by themeViewModel.theme.collectAsState()

            // Atualiza a aparência da barra de status com base no tema.
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
