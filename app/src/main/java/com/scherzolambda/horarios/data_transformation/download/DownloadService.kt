package com.scherzolambda.horarios.data_transformation.download

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.scherzolambda.horarios.ui.navigation.Screen
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class DownloadResult {
    data class Success(val message: String, val filePath: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}

class DownloadService {
    /** Função para lidar com o download do HTML da WebView
     * Extrai o HTML, valida, salva e navega para a tela Daily
     * @param context O contexto
     * @param webView A WebView contendo o HTML
     * @param onFileSaved Callback com o caminho do arquivo salvo
     * @param navController O NavController para navegação
     */
    fun handleDownload(
        context: Context,
        webView: WebView?,
        onResult: (DownloadResult) -> Unit,
        navController: NavController
    ) {
        webView?.evaluateJavascript("document.documentElement.outerHTML") { html ->
            try {
                val decodedHtml = decodeHtml(html)
                val htmlWithMeta = addMetaCharset(decodedHtml)

                // Valida se é um comprovante válido
                if (!isValidComprovante(htmlWithMeta)) {
                    onResult(DownloadResult.Error("Esta página não é um comprovante de matrícula válido!"))
                    Log.d("Download", "HTML inválido para comprovante: $htmlWithMeta")
                    return@evaluateJavascript
                }

                // Salva o arquivo
                val fileName = generateFileName()
                val uri = saveHtmlToFile(context, fileName, htmlWithMeta)
                    ?: throw IllegalStateException("Erro ao criar arquivo")

                val filePath = getFilePathFromUri(context, uri) ?: uri.toString()
                onResult(DownloadResult.Success("Página salva e carregada!", filePath))
                navigateToDaily(navController)

            } catch (e: Exception) {
                onResult(DownloadResult.Error("Erro ao salvar/carregar: ${e.message}"))
                Log.e("Download", "Erro no processo de download", e)
            }
        }
    }

    /** Função para decodificar HTML
     * Usa JSONObject para decodificar entidades HTML
     * @param html O HTML codificado
     * @return O HTML decodificado
     */
    private fun decodeHtml(html: String): String {
        return try {
            JSONObject("{\"html\":$html}").getString("html")
        } catch (e: Exception) {
            html.trim('"') // Fallback simples
        }
    }

    /** Função para adicionar meta charset
     * Adiciona a tag meta charset UTF-8 dentro da tag head, se não existir
     * @param html O HTML original
     * @return O HTML modificado com a tag meta charset
     */
    private fun addMetaCharset(html: String): String {
        val metaTag = "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"
        val headIndex = html.indexOf("<head>")
        return if (headIndex != -1 && !html.contains(metaTag)) {
            html.replaceFirst("<head>", "<head>$metaTag")
        } else {
            html
        }
    }

    /** Função para validar se o HTML é um comprovante
     * Verifica a presença de elementos específicos no HTML
     * @param html O HTML a ser validado
     * @return true se for um comprovante válido, false caso contrário
     */
    private fun isValidComprovante(html: String): Boolean {
        return html.contains("<div id=\"relatorio-container\">") ||
                html.contains("<h3>Comprovante de Matrícula</h3>")
    }

    /** Função para gerar nome de arquivo com timestamp
     * Formato: sigaa_yyyyMMdd_HHmmss.html
     * @return O nome do arquivo gerado
     */
    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "sigaa_${timeStamp}.html"
    }

    /** Função para salvar HTML em arquivo
     * Usa MediaStore para salvar na pasta Downloads
     * @param context O contexto
     * @param fileName O nome do arquivo
     * @param html O conteúdo HTML
     * @return O URI do arquivo salvo ou null se falhar
     */
    private fun saveHtmlToFile(context: Context, fileName: String, html: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/html")
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(html.toByteArray(Charsets.UTF_8))
            }
        }
        return uri
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /** Função para navegar para a tela Daily
     * Usa NavController para navegar e limpar a pilha de navegação
     * @param navController O NavController para navegação
     */
    private fun navigateToDaily(navController: NavController) {
        navController.navigate(Screen.Daily.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    /** Função para obter o caminho do arquivo a partir do URI
     * @param context O contexto
     * @param uri O URI do arquivo
     * @return O caminho do arquivo ou null se não encontrado
     */
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
}
