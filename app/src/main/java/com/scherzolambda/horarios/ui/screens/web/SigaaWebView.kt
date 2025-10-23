package com.scherzolambda.horarios.ui.screens.web

import android.annotation.SuppressLint
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SigaaWebView(
    modifier: Modifier = Modifier,
    url: String = "https://sigaa.sistemas.ufcat.edu.br/sigaa/mobile/touch/public/principal.jsf",
    webViewRef: (WebView?) -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(true) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        // Habilitar zoom
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = true // Oculta botões de zoom para uma UI mais limpa
                        useWideViewPort = true
//                        loadWithOverviewMode = true
                        // Otimizar cache e renderização
                        cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                        allowFileAccess = false // Segurança
                        allowContentAccess = false // Segurança
                    }
                    setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
                    webViewClient = object : WebViewClient() {
                        @SuppressLint("WebViewClientOnReceivedSslError")
                        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                            handler?.proceed()
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            isLoading = newProgress < 100
                        }
                    }
                    loadUrl(url)
                    webViewRef(this)
                }
            }
        )

        // Indicador de carregamento
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigaaWebScreen(
    webViewRef: (WebView?) -> Unit
) {
    SigaaWebView(
        modifier = Modifier.fillMaxSize(),
        webViewRef = { webView ->
            webViewRef(webView)
        },
    )
}
