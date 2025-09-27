package com.scherzolambda.horarios.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.scherzolambda.horarios.data_transformation.Disciplina
import com.scherzolambda.horarios.data_transformation.FileProcessor
import com.scherzolambda.horarios.data_transformation.salvarDisciplinasLocal
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.lerDisciplinasLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.InputStream

@Composable
fun StatusScreen() {
    var htmlUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var tabelas by remember { mutableStateOf<List<List<Disciplina>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var salvarStatus by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> htmlUri = uri }
    )

    val isFirstAccess by DataStoreHelper.isFirstAccessFlow(context).collectAsState(initial = true)
    val isFileLoaded by DataStoreHelper.isFileLoadedFlow(context).collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    // Quando o arquivo HTML é selecionado, extrai as tabelas
    LaunchedEffect(htmlUri) {
        htmlUri?.let { uri ->
            isLoading = true
            val tabelasExtraidas = withContext(Dispatchers.IO) {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val tempFile = kotlin.io.path.createTempFile(suffix = ".html").toFile()
                val outputStream = tempFile.outputStream()
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                val processor = FileProcessor()
                processor.extrairTabelasDeHtml(tempFile.absolutePath)
            }
            tabelas = tabelasExtraidas
            isLoading = false
        }
    }

    LaunchedEffect(isFirstAccess) {
        if (isFirstAccess) {
            DataStoreHelper.setFirstAccess(context, false)
        }
    }

    // Carrega disciplinas do cache se já houver arquivo salvo
    LaunchedEffect(isFileLoaded) {
        if (isFileLoaded && tabelas.isEmpty()) {
            val disciplinas = lerDisciplinasLocal(context)
            if (disciplinas.isNotEmpty()) {
                tabelas = listOf(disciplinas)
                salvarStatus = "Disciplinas carregadas do cache."
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFirstAccess) {
            Text("Bem-vindo! Este é seu primeiro acesso.", modifier = Modifier.padding(8.dp))
        }
        if (isFileLoaded) {
            Text("Um arquivo já foi carregado anteriormente.", modifier = Modifier.padding(8.dp))
        }
        Button(
            onClick = { launcher.launch(arrayOf("text/html")) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Selecionar arquivo HTML")
        }
        if (tabelas.isNotEmpty()) {
            Button(
                onClick = {
                    val disciplinas = tabelas.flatten()
                    val sucesso = salvarDisciplinasLocal(context, disciplinas)
                    salvarStatus = if (sucesso) "Disciplinas salvas com sucesso!" else "Erro ao salvar disciplinas."
                    Toast.makeText(context, salvarStatus, Toast.LENGTH_SHORT).show()
                    // Marca que um arquivo foi carregado
                    coroutineScope.launch {
                        DataStoreHelper.setFileLoaded(context, true)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Salvar disciplinas")
            }
        }
        if (htmlUri != null) {
            Text("Arquivo selecionado: $htmlUri", modifier = Modifier.padding(8.dp))
        }
        if (isLoading) {
            Text("Extraindo tabelas... Aguarde.", modifier = Modifier.padding(8.dp))
        } else if (tabelas.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                items(tabelas) { tabela ->
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            tabela.forEach { disciplina ->
                                if (disciplina.codigo.isNotEmpty()) {

                                    Row{
                                        Text(disciplina.codigo)
//                                    Text(disciplina.componenteCurricular)
                                        Text(disciplina.turma)
//                                    Text(disciplina.status)
                                        Text(disciplina.horario)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text("Nenhuma tabela encontrada.", modifier = Modifier.padding(8.dp))
        }
    }
}
