package com.scherzolambda.horarios.ui.screens.web

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.http.SslError
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.scherzolambda.horarios.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SigaaWebView(
    modifier: Modifier = Modifier,
    url: String = "https://sigaa.sistemas.ufcat.edu.br/sigaa/mobile/touch/public/principal.jsf",
    webViewRef: (WebView?) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(true)
            webViewClient = object : WebViewClient() {
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    Log.e("SigaaWebView", "SSL Error: ${error?.primaryError}, URL: ${view?.url}, Error: $error")
                    handler?.proceed()
                }
            }
            loadUrl(url)
            webViewRef(this)
        }
    })
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigaaWebScreen(
    webViewRef: (WebView?) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

//    Column {
//        Row {
//            Text("SIGAA - UFCAT")
//            IconButton(onClick = {
//                webView?.evaluateJavascript(
//                    "document.documentElement.outerHTML"
//                ) { html ->
//                    val cleanHtml = html.trim('"')
//                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//                    val fileName = "sigaa_${timeStamp}.html"
//                    val resolver = context.contentResolver
//                    val contentValues = ContentValues().apply {
//                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
//                        put(MediaStore.Downloads.MIME_TYPE, "text/html")
//                    }
//                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//                    if (uri != null) {
//                        try {
//                            resolver.openOutputStream(uri)?.use { it.write(cleanHtml.toByteArray()) }
//                            Toast.makeText(context, "Página salva em Downloads: ${fileName}", Toast.LENGTH_LONG).show()
//                        } catch (e: Exception) {
//                            Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
//                        }
//                    } else {
//                        Toast.makeText(context, "Erro ao criar arquivo", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }) {
//                Icon(painterResource(R.drawable.ic_download), contentDescription = "Baixar HTML")
//            }
//        }
//    }
    SigaaWebView(
        modifier = Modifier.fillMaxSize(),
        url = "https://sigaa.sistemas.ufcat.edu.br/sigaa/mobile/touch/public/principal.jsf",
        webViewRef = webViewRef
    )
}
