package com.scherzolambda.horarios.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


val White = Color(0xFFFFFFFF)
val UfcatBlack = Color(0xFF000000)
val UfcatBlack2 = Color(0xFF0B2408)


val UfcatOrange = Color(0xFFFD841A)
val UfcatGreen = Color(0xFF297A7D)
val UfcatGray = Color(0xFFE1DEE3)
//val UfcatGrayDark = Color(0xFF3B3B3B)
val UfcatGrayDark = Color(0xFF242121)
val UfcatRed = Color(0xFFEE2D55)
val UfcatOrangeDark = Color(0xFFFF8C00)
val Transparent = Color(0x40FFFFFF)


val M_PeriodColor = Color(0xFFB2DFDB)
val T_PeriodColor = Color(0xFFFFCC80)
val N_PeriodColor = Color(0xFFCE93D8)


/**
 * Estrutura as cores fundamentais de conteúdo da UI, como textos e fundos.
 *
 * @property textPrimary A cor principal para textos e ícones de alta ênfase.
 * @property textSecondary A cor para textos e elementos de UI de menor ênfase.
 * @property background A cor de fundo principal das telas.
 * @property surface A cor de fundo para componentes elevados, como cards e menus.
 */
data class ContentColors(
    val primary: Color = UfcatGreen,
    val whiteText: Color = White,
    val blackText: Color = UfcatBlack,
    val grayElements: Color = UfcatGray,
    val background: Color = White,
    val white: Color = White,
//    val transparent: Color = Transparent,
//    val textPrimary: Color,
//    val textSecondary: Color,
//    val surface: Color,
//    val surface200: Color,
//    val surface600: Color,
//    val backgroundSettings: Color,
//    val backgroundCardProfile: Color,
//    val lineDivider: Color,
//    val cardBackgroud: Color,
//    val componentText: Color,

    )

/**
 * Agrega todas as estruturas de cores customizadas em um único objeto de tema.
 *
 * Esta classe é a que é fornecida para a UI através de `CompositionLocal`,
 * oferecendo acesso a todas as paletas de forma organizada via `AppTheme.colors`.
 */
data class AppColors(
    val content: ContentColors,
)

/**
 * Instância pré-configurada de [AppColors] para o tema claro.
 */
val LightAppColors = AppColors(
    content = ContentColors()
)

/**
 * Instância pré-configurada de [AppColors] para o tema escuro.
 */
val DarkAppColors = AppColors(
    content = ContentColors(
        primary = UfcatOrange,
        whiteText = UfcatGrayDark,
        blackText = White,
        grayElements = UfcatBlack,
        background = UfcatBlack
    )
)

/**
 * Provedor `CompositionLocal` interno para o sistema de cores.
 *
 * É o mecanismo que permite que o `ApplicationTheme` injete a instância correta
 * de [AppColors] (clara ou escura) na árvore de componentes, tornando-a acessível
 * via `AppTheme.colors`.
 */
internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }