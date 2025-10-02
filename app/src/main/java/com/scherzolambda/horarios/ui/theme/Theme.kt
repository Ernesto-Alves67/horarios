package com.scherzolambda.horarios.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = UfcatGray,
    secondary = UfcatOrangeDark,
    tertiary = White,
    background = UfcatBlack2
)

private val LightColorScheme = lightColorScheme(
    primary = UfcatGreen,
    secondary = UfcatOrange,
    tertiary = UfcatOrangeDark,
    background = UfcatGray

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = appColors.content.primary,
            background = appColors.content.background,
            onPrimary = appColors.content.background,
            onBackground = appColors.content.grayElements
        )
    } else {
        lightColorScheme(
            background = appColors.content.background,
            onPrimary = appColors.content.background
        )
    }

    CompositionLocalProvider(
        LocalAppColors provides appColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

object HorariosThemes {
    object color {
        val background @Composable get() = MaterialTheme.colorScheme.background
        val primary @Composable get() = MaterialTheme.colorScheme.primary
        val secondary @Composable get() = MaterialTheme.colorScheme.secondary
    }
}