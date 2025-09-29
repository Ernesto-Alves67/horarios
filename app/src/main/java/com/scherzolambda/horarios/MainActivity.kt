package com.scherzolambda.horarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.HorariosTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Compose-aware state to control an overlay that shows the app name during splash
    private val isSplashVisible = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the AndroidX splash screen and keep it while `isSplashVisible` is true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isSplashVisible.value }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Make sure we keep the splash for a short moment (or until initialization finishes)
        lifecycleScope.launch {
            // Adjust delay as needed; you can replace this with real init work
            delay(600)
            isSplashVisible.value = false
        }

        setContent {
            HorariosTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    MainNavigation()

                    // Simple overlay shown while splash is visible. This renders immediately
                    // after the system splash is dismissed (we keep them in sync above).
                    if (isSplashVisible.value) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
