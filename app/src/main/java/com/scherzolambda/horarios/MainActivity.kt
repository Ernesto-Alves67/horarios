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
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.AppColors
import com.scherzolambda.horarios.ui.theme.AppTheme
import com.scherzolambda.horarios.ui.theme.ApplicationTheme
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.ThemeViewModel
import com.scherzolambda.horarios.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

//@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO: Refatorar para usar o SplashScreen API nativa do Android 12+ e criar uma tela de carregamento personalizada para versões anteriores.
        //TODO: MElhorar tutorial de assinatura do apk
        //TODO: botões de compartilhamento com qrcode e feedback
        // TODO: implementar definição de lembretes para uma disciplna específica
        //TODO: implementar notificações para as disciplinas
        //TODO: implmentar alarme caso o usuário queira ser lembrado de uma aula específica
        //TODO: implementar adição e edição manual de disciplinas e dados do usuario
        //TODO: botao para limpas dados do app
        //TODO: tutorial de uso semelahnte a versão web
        //TODO: corrigir background da tela politica de privacidade e permitir copiar texto da tela
        //TODO: Melhorar configuraçao de layout
        //TODO: basear tela de configuração no app do reddit mobile
        //TODO:
        //TODO:
        //TODO:
        //TODO:
        //TODO:
        //TODO:
        //TODO:
        //TODO:
        //TODO:
        //TODO:

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
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        window.statusBarColor = android.graphics.Color.rgb(41,122,125)
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
