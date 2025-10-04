package com.scherzolambda.horarios.ui.screens.updater

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.scherzolambda.horarios.data_transformation.api.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun UpdateDialog(
    latestVersion: String,
    downloadUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova versão disponível") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Uma nova versão ($latestVersion) está disponível!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(12.dp))
                Text("Deseja atualizar agora para ter as últimas melhorias?")
            }
        },
        confirmButton = {
            TextButton(onClick = { installApk(context, downloadUrl) }) {
                Text("Baixar e instalar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Talvez depois")
            }
        }
    )
}

/**
 * Faz o download do APK e abre o instalador.
 */
fun installApk(context: Context, apkUrl: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 1. Solicitar permissão para instalar apps desconhecidos
            if (!context.packageManager.canRequestPackageInstalls()) {

                withContext(Dispatchers.Main) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = "package:${context.packageName}".toUri()
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }
                return@launch
            }

            // 2. Configurar Retrofit
            val service = AuthRepository()
            val response = service.downloadApk(apkUrl)

            if (!response.isSuccessful) throw Exception("Erro ao baixar APK")

            val body = response.body() ?: throw Exception("Corpo da resposta nulo")
            val apkFile = File(context.cacheDir, "update.apk")

            body.byteStream().use { input ->
                apkFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // 3. Criar URI segura
            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                apkFile
            )

            // 4. Disparar intent de instalação
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            withContext(Dispatchers.Main) {
                context.startActivity(installIntent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

