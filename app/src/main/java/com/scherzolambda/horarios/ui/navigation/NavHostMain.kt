package com.scherzolambda.horarios.ui.navigation

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scherzolambda.horarios.R
import com.scherzolambda.horarios.ui.screens.DailyScreen
import com.scherzolambda.horarios.ui.screens.StatusScreen
import com.scherzolambda.horarios.ui.screens.WeeklyScreen
import com.scherzolambda.horarios.ui.screens.web.SigaaWebScreen
import com.scherzolambda.horarios.ui.theme.UFCATGreen
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.scherzolambda.horarios.ui.theme.UfcatOrange
import com.scherzolambda.horarios.ui.theme.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject

/**
 * Define as telas principais do aplicativo com suas rotas, rótulos e ícones.
 * Utilizado para configurar a barra de navegação inferior.
 */
sealed class Screen(val route: String, val label: String, val iconRes: Int) {
    object Daily : Screen("daily", "Hoje", R.drawable.ic_notebook_filled)
    object Weekly : Screen("weekly", "Semana", R.drawable.ic_calendar)
    object Status : Screen("status", "Status", R.drawable.ic_info)
    object Sigaa : Screen("sigaa", "SIGAA", R.drawable.ic_internet) // Adicione um ícone apropriado
}

val screens = listOf(Screen.Daily, Screen.Weekly, Screen.Status, Screen.Sigaa)


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Daily.route

    // Estado elevado para WebView
    var sigaaWebView by remember { mutableStateOf<WebView?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Lógica de download
    val onDownloadClick: (() -> Unit)? = if (currentRoute == Screen.Sigaa.route) {
        {
            // TODO: Melhorar a extração do HTML para garantir que o conteúdo completo seja capturado
            // e que a codificação seja tratada corretamente.
            sigaaWebView?.evaluateJavascript(
                "document.documentElement.outerHTML"
            ) { html ->
                // Decodifica corretamente o HTML usando JSONObject
                val decodedHtml = try {
                    JSONObject("{\"html\":$html}").getString("html")
                } catch (e: Exception) {
                    html.trim('"') // fallback simples
                }
                Log.d("Download", "HTML decodificado: $decodedHtml")
                val metaTag = "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"
                val headIndex = decodedHtml.indexOf("<head>")
                val htmlWithMeta = if (headIndex != -1 && !decodedHtml.contains(metaTag)) {
                    decodedHtml.replaceFirst("<head>", "<head>$metaTag")
                } else {
                    decodedHtml
                }
                Log.d("Download", "HTML final para salvar: $htmlWithMeta")
                // Validação do formato do comprovante
                val isComprovante =
                    htmlWithMeta.contains("<div id=\"relatorio-container\">") ||
                    htmlWithMeta.contains("<h3>Comprovante de Matrícula</h3>")
                if (!isComprovante) {
                    Toast.makeText(context, "Esta página não é um comprovante de matrícula válido!", Toast.LENGTH_LONG).show()
                    Log.d("Download", "HTML inválido para comprovante: $htmlWithMeta")
                    return@evaluateJavascript
                }
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "sigaa_${timeStamp}.html"
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "text/html")
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    try {
                        resolver.openOutputStream(uri)?.use { it.write(htmlWithMeta.toByteArray(
                            Charsets.UTF_8)) }
                        val filePath = getFilePathFromUri(context, uri) ?: uri.toString()
                        disciplinaViewModel.carregarDeArquivoHtml(filePath)
                        Toast.makeText(context, "Página salva e carregada!", Toast.LENGTH_LONG).show()
                        navController.navigate(Screen.Daily.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro ao salvar/carregar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Erro ao criar arquivo", Toast.LENGTH_LONG).show()
                }
            }
        }
    } else null

    Scaffold(
        topBar = {
            TopBar(
                showDownloadButton = currentRoute == Screen.Sigaa.route,
                onDownloadClick = onDownloadClick
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute) }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding,
            disciplinaViewModel = disciplinaViewModel,
            sigaaWebViewRef = { sigaaWebView = it }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    showDownloadButton: Boolean = false,
    onDownloadClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    painterResource(R.drawable.ic_logo_ufcat),
                    contentDescription = "icone da UFCAT",
                    modifier = Modifier.size(80.dp),
                    tint = White
                )
                Text("Horários", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
            }
        },
        actions = {
            if (showDownloadButton && onDownloadClick != null) {
                IconButton(onClick = onDownloadClick) {
                    Icon(painterResource(R.drawable.ic_download), contentDescription = "Baixar HTML")
                }
            }
        }
    )
}

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = UFCATGreen) {
        screens.forEach { screen ->
            val selected = currentRoute == screen.route
            val fontWeight by animateIntAsState(targetValue = if (selected) 700 else 400)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                // bold the label when this item is selected
                label = { Text(screen.label, fontSize = 18.sp, fontWeight = FontWeight(fontWeight)) },
                // Use the GradientIcon so we can render a linear gradient inside the icon shape
                icon = {
                    val painter = painterResource(id = screen.iconRes)
                    val gradient = if (selected) {
                        // selected gradient (you can customize these colors)
                        Brush.horizontalGradient(listOf(UfcatOrange, Color(0xFFFF3366)))
                    } else {
                        // unselected subtle gradient
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.6f)))
                    }
                    GradientIcon(
                        painter = painter,
                        contentDescription = screen.label,
                        modifier = Modifier.size(if (selected) 32.dp else 24.dp),
                        brush = gradient
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF6600),
                    unselectedIconColor = Color.White.copy(0.7f),
                    selectedTextColor = Color(0xFFFF6600),
                    unselectedTextColor = Color.White.copy(0.7f),
                    // Remove default circular indicator so the icon silhouette (gradient)
                    // is visible without a circle behind it.
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

/**
 * Draws [painter] then paints [brush] using BlendMode.SrcIn so the gradient is masked by
 * the painted icon shape — producing a gradient-colored icon.
 */
@Composable
fun GradientIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    brush: Brush
) {
    // This implementation draws the painter to a temporary layer, then paints the
    // gradient with BlendMode.SrcIn into that layer so the gradient is visible only
    // where the painter has alpha. This is robust for both vectors and bitmaps.
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.drawWithCache {
            onDrawWithContent {
                // create a paint (no-op) and save a layer
                val paint = androidx.compose.ui.graphics.Paint()
                val layerBounds = Rect(Offset.Zero, size)
                // saveLayer -> draw painter -> draw gradient with SrcIn -> restore
                drawContext.canvas.saveLayer(layerBounds, paint)
                // draw the icon into the layer
                drawContent()
                // paint the gradient using SrcIn so it remains only where the icon is opaque
                drawRect(brush = brush, blendMode = BlendMode.SrcIn)
                drawContext.canvas.restore()
            }
        },
        // Use white tint so vector paths become an opaque mask. For bitmaps, this will
        // apply a color filter; ensure bitmaps have transparency for the correct mask.
        tint = Color.White
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    disciplinaViewModel: DisciplinaViewModel,
    sigaaWebViewRef: (WebView?) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Daily.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Daily.route) { DailyScreen(innerPadding, disciplinaViewModel) }
        composable(Screen.Weekly.route) { WeeklyScreen(innerPadding, disciplinaViewModel) }
        composable(Screen.Status.route) { StatusScreen(disciplinaViewModel) }
        composable(Screen.Sigaa.route) {
            SigaaWebScreen(
                webViewRef = sigaaWebViewRef
            )
        }
    }
}

// Função utilitária para obter o caminho do arquivo a partir do URI
fun getFilePathFromUri(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Downloads.DATA)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATA)
        if (cursor.moveToFirst()) {
            return cursor.getString(columnIndex)
        }
    }
    return null
}
