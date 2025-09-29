package com.scherzolambda.horarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.scherzolambda.horarios.ui.navigation.MainNavigation
import com.scherzolambda.horarios.ui.theme.HorariosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HorariosTheme {
                MainNavigation()
//                NavHostLegacy()
            }
        }
    }
}
