package com.scherzolambda.horarios.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.ui.theme.AppTypography
import com.scherzolambda.horarios.ui.theme.UFCATGreen
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

@Composable
fun StatusScreen(
    disciplinaViewModel: DisciplinaViewModel
) {
    val disciplinasState = disciplinaViewModel.disciplinas.collectAsState()
    val disciplinas = disciplinasState.value
    val isLoading by disciplinaViewModel.isLoading.collectAsState()
    
    var htmlUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var salvarStatus by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> htmlUri = uri }
    )
    val isFileLoaded by DataStoreHelper.isFileLoadedFlow(context).collectAsState(initial = false)

    // Quando o arquivo HTML é selecionado, extrai as tabelas e salva automaticamente
    LaunchedEffect(htmlUri) {
        htmlUri?.let { uri ->
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
            salvarStatus = "Arquivo de disciplinas substituído com sucesso!"
            Toast.makeText(context, salvarStatus, Toast.LENGTH_SHORT).show()
            DataStoreHelper.setFileLoaded(context, true)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val (lazyListRef, fileLoadedRef, loadingRef) = createRefs()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .constrainAs(loadingRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else if (disciplinas.isNotEmpty()) {
            Log.d("StatusScreen", "Exibindo ${disciplinas.size} disciplinas")
            StatusInfoCard(
                title = "Arquivo carregado",
                info = "",
                textButton = "Alterar arquivo HTML",
                modifier = Modifier
                    .constrainAs(fileLoadedRef) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                onClickButton = { launcher.launch(arrayOf("text/html")) })
            LazyColumn(
                modifier = Modifier
                    .constrainAs(lazyListRef) {
                        top.linkTo(fileLoadedRef.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(disciplinas.size) { index ->
                    val disciplina = disciplinas[index]
                    if (disciplina.codigo.isNotEmpty()) {
                        Card(
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(disciplina.codigo, style = AppTypography.headlineSmall)
                                Text(
                                    disciplina.componenteCurricular,
                                    style = AppTypography.headlineSmall
                                )
                            }
                        }
                    }
                }
            }


        }
        else {
            Log.d("StatusScreen", "Nenhuma disciplina para exibir")

            StatusInfoCard(
                title = "Nenhum arquivo carregado",
                info = "Por favor, selecione um arquivo HTML.",
                textButton = "Selecionar arquivo HTML",
                modifier = Modifier
                    .constrainAs(fileLoadedRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                onClickButton = { launcher.launch(arrayOf("text/html")) }
            )
        }
    }
}

@Composable
fun StatusInfoCard(
    title: String,
    info: String,
    textButton: String,
    modifier: Modifier = Modifier,
    onClickButton: (() -> Unit)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = AppTypography.headlineSmall)
            Spacer(modifier = Modifier.size(8.dp))
            if (!(info.isEmpty())){
                Text(info, style = AppTypography.bodyMedium)
            }

            Button(
                onClick = { onClickButton() },
                colors = ButtonDefaults.buttonColors(containerColor = UFCATGreen, contentColor = UfcatBlack)
            ) {
                Text(textButton, fontSize = 18.sp)
            }
        }
    }
}
