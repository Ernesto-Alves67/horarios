package com.scherzolambda.horarios.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.InputStream
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StatusScreen() {
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val disciplinasState = disciplinaViewModel.disciplinas.collectAsState()
    val disciplinas = disciplinasState.value
    var htmlUri by remember { mutableStateOf<android.net.Uri?>(null) }
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

    // Quando o arquivo HTML é selecionado, extrai as tabelas e salva automaticamente
    LaunchedEffect(htmlUri) {
        htmlUri?.let { uri ->
//            isLoading = true
            val tempFile = withContext(Dispatchers.IO) {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val tempFile = kotlin.io.path.createTempFile(suffix = ".html").toFile()
                val outputStream = tempFile.outputStream()
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                tempFile
            }
            disciplinaViewModel.carregarDeArquivoHtml(tempFile.absolutePath)
//            isLoading = false
            salvarStatus = "Arquivo de disciplinas substituído com sucesso!"
            Toast.makeText(context, salvarStatus, Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                DataStoreHelper.setFileLoaded(context, true)
            }
        }
    }

    LaunchedEffect(isFirstAccess) {
        if (isFirstAccess) {
            DataStoreHelper.setFirstAccess(context, false)
        }
    }

    // Carrega disciplinas do cache se já houver arquivo salvo E nenhum arquivo HTML está selecionado
    LaunchedEffect(isFileLoaded, htmlUri) {
        if (isFileLoaded && disciplinas.isEmpty() && htmlUri == null) {
            disciplinaViewModel.carregarDisciplinasLocal()
            salvarStatus = "Disciplinas carregadas do cache."
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
            Text("Selecionar arquivo HTML para substituir disciplinas")
        }
        if (disciplinas.isNotEmpty()) {
//            Button(
//                onClick = { launcher.launch(arrayOf("text/html")) },
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth()
//            ) {
//                Text("Selecionar arquivo HTML para substituir disciplinas")
//            }
            LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            disciplinas.forEach { disciplina ->
                                if (disciplina.codigo.isNotEmpty()) {
                                    Row{
                                        Text(disciplina.codigo)
                                        Spacer(modifier = Modifier.padding(4.dp))
                                        Text(disciplina.componenteCurricular)
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
