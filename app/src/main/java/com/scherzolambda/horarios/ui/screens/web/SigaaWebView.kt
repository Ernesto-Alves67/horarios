package com.scherzolambda.horarios.ui.screens.web

import android.annotation.SuppressLint
import android.net.http.SslError
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SigaaWebView(
    modifier: Modifier = Modifier,
    url: String = "https://sigaa.sistemas.ufcat.edu.br/sigaa/mobile/touch/public/principal.jsf",
    webViewRef: (WebView) -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(true) }
    var isSSLError by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        clearCache(true)
                    }

                    webViewClient = object : WebViewClient() {
                        @SuppressLint("WebViewClientOnReceivedSslError")
                        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//                            handler?.proceed()
                            val primaryError_ = error?.primaryError
                            when(primaryError_){
                                SslError.SSL_UNTRUSTED -> {
                                    // Certificado não é confiável
                                    Log.d("SigaaWebView", "SSL_UNTRUSTED error>>>>")
                                    handler?.cancel()
                                }
                                SslError.SSL_EXPIRED -> {
                                    // Certificado expirado
                                    handler?.cancel()
                                }
                                SslError.SSL_IDMISMATCH -> {
                                    // Nome do host não corresponde
                                    handler?.cancel()
                                }
                                SslError.SSL_NOTYETVALID -> {
                                    // Certificado ainda não é válido
                                    handler?.cancel()
                                }
                                SslError.SSL_DATE_INVALID -> {
                                    // Data inválida no certificado
                                    handler?.cancel()
                                }
                                SslError.SSL_INVALID -> {
                                    // Erro genérico de SSL
                                    handler?.cancel()
                                }
                                else -> {
                                    handler?.cancel()
                                }
                            }
                            Log.d("SigaaWebView", "SSL Error encountered: $error")
                            isSSLError = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
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

        if (isSSLError){
            Column (
                modifier = Modifier.align(Alignment.Center)
            ){
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.scherzolambda.horarios.R.drawable.ic_alert),
                    contentDescription = "SSL Error",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Erro no certificado SSL.\nA conexão não é segura.\n \n Fale com o suporte.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 23.sp
                )
            }
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
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            webViewRef(webView)
        },
    )
}
