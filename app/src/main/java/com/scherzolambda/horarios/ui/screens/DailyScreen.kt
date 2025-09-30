package com.scherzolambda.horarios.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.data_transformation.getTodayClasses
import com.scherzolambda.horarios.ui.theme.AppTypography
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.ui.theme.UfcatOrangeDark
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import com.scherzolambda.horarios.data_transformation.DataStoreHelper

/**
 * Tela que exibe as aulas do dia atual, organizadas por turno (manhã, tarde, noite).
 * Utiliza o ViewModel para obter a lista de disciplinas e filtra as aulas do dia.
 * Cada turno é exibido em um Card separado, mostrando os horários e detalhes das disciplinas.
 *
 * @param paddingValues Espaçamento interno para a tela, geralmente fornecido pelo Scaffold.
 */
@Composable
fun DailyScreen(
    paddingValues: PaddingValues,
    disciplinaViewModel: DisciplinaViewModel
) {
    val disciplinasState = disciplinaViewModel.disciplinas.collectAsState()
    val disciplinas = disciplinasState.value
    val disciplinasHoje = getTodayClasses(disciplinas)
    var selectedCell by remember { mutableStateOf<HorarioSemanal?>(null) }

    var htmlUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var salvarStatus by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> htmlUri = uri }
    )
    val isFirstAccess by DataStoreHelper.isFirstAccessFlow(context).collectAsState(initial = true)
    val isFileLoaded by DataStoreHelper.isFileLoadedFlow(context).collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
    ) {

        if (disciplinas.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nenhuma disciplina encontrada.", modifier = Modifier.padding(8.dp))
                androidx.compose.material3.Button(
                    onClick = { launcher.launch(arrayOf("text/html")) },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.scherzolambda.horarios.ui.theme.UFCATGreen)
                ) {
                    Text("Selecionar arquivo HTML", fontSize = 18.sp)
                }
            }
        } else {
            // Header fixo no topo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF3366), // tom rosa-avermelhado
                                Color(0xFFFF6600)  // tom laranja
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "Aulas de Hoje",
                    fontSize = 32.sp,
                    fontWeight = Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(4.dp),
                verticalArrangement = Arrangement.Top
            ) {
                if (existeDisciplinaNoTurno(disciplinasHoje, HourType.M)) {
                    HoursOfDayComponent(hourType = HourType.M, disciplinasHoje = disciplinasHoje, onDisciplinaClick = { selectedCell = it })
                }
                if (existeDisciplinaNoTurno(disciplinasHoje, HourType.T)) {
                    HoursOfDayComponent(hourType = HourType.T, disciplinasHoje = disciplinasHoje, onDisciplinaClick = { selectedCell = it })
                }
                if (existeDisciplinaNoTurno(disciplinasHoje, HourType.N)) {
                    HoursOfDayComponent(hourType = HourType.N, disciplinasHoje = disciplinasHoje, onDisciplinaClick = { selectedCell = it })
                }

                Spacer(modifier = Modifier.padding(8.dp))
            }
        }


        // Dialog (fora da área rolável) para ficar acima do conteúdo quando aberto
        if (selectedCell != null) {
            AlertDialog(
                onDismissRequest = { selectedCell = null },
                confirmButton = { },
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = selectedCell!!.disciplina,
                            style = AppTypography.displayLarge,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        IconButton(
                            onClick = { selectedCell = null },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Fechar"
                            )
                        }
                    }
                },
                text = {
                    Column(modifier = Modifier.padding(4.dp)) {
                        if (selectedCell!!.local.isNotBlank()) {

                            DialogInfoRow("Local", selectedCell!!.local)
                        }
                        DialogInfoRow("Horário",HourMaps.getHourRange(selectedCell!!.periodo, selectedCell!!.horario))
                        DialogInfoRow("Docente", selectedCell!!.docente)
                    }
                }
            )
        }

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

    }
}

@Composable
fun HoursOfDayComponent(
    hourType: HourType = HourType.M,
    disciplinasHoje: List<HorarioSemanal>,
    onDisciplinaClick: (HorarioSemanal) -> Unit
) {
    val hourMap = HourMaps.getHourMap(hourType)
    // Pre-group disciplines by hour index for O(1) lookup
    val disciplinasPorHora = remember(disciplinasHoje, hourType) {
        disciplinasHoje
            .filter { it.periodo == hourType }
            .groupBy { it.horario }
    }

    Card(
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // determine background color for this turno (same as WeeklyScreen)
        val periodoColor = when(hourType) {
            HourType.M -> Color(0xFFE3F2FD)
            HourType.T -> Color(0xFFFFF9C4)
            HourType.N -> Color(0xFFFFCDD2)
        }
        when(hourType) {
            HourType.T -> Text("Turno Tarde", modifier = Modifier.padding(16.dp), fontWeight = Bold, fontSize = 20.sp)
            HourType.N -> Text("Turno Noite", modifier = Modifier.padding(16.dp), fontWeight = Bold, fontSize = 20.sp)
            HourType.M -> Text("Turno Manhã", modifier = Modifier.padding(16.dp), fontWeight = Bold, fontSize = 20.sp)
        }


        // Espaçamento entre blocos de horário
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            hourMap.forEach { (index, hour) ->
                val disciplinasNoHorario = disciplinasPorHora[index].orEmpty()
                if (disciplinasNoHorario.isEmpty()){
//                    Log.d("DailyScreen", "No classes at $hour ($index) in $hourType") TODO: Passar para as configs
                }else{

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = periodoColor, shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Cabeçalho do horário (índice + label)
                        Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp,bottom = 4.dp)) {
                            Text("$index - ",
                                color = UfcatBlack,
//                                modifier = Modifier.padding(end = 8.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp)
                            Text(hour, color = UfcatBlack)
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            disciplinasNoHorario.forEach { disciplina ->
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = disciplina.disciplina,
                                        fontSize = 18.sp,
                                        color = UfcatBlack,
                                        fontWeight = Bold,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clickable { onDisciplinaClick(disciplina) }
                                    )
                                    Text(
                                        text = disciplina.local,
                                        fontSize = 14.sp,
                                        color = UfcatBlack,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun existeDisciplinaNoTurno(disciplinasHoje: List<HorarioSemanal>, hourType: HourType): Boolean {
    return disciplinasHoje.any { it.periodo == hourType }
}
