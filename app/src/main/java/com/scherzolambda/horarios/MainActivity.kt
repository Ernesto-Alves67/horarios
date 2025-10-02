package com.scherzolambda.horarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.ApplicationTheme
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


        lifecycleScope.launch {
            delay(600)
            isSplashVisible.value = false
        }

        setContent {
            ApplicationTheme {
                MainNavigation()
            }
        }
    }
}
