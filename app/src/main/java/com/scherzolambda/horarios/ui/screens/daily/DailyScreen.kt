package com.scherzolambda.horarios.ui.screens.daily

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scherzolambda.horarios.BuildConfig
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.data_transformation.getTodayClasses2
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.ui.screens.updater.UpdateDialog
import com.scherzolambda.horarios.ui.screens.week.DialogInfoRow
import com.scherzolambda.horarios.ui.theme.AppTypography
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.M_PeriodColor
import com.scherzolambda.horarios.ui.theme.N_PeriodColor
import com.scherzolambda.horarios.ui.theme.T_PeriodColor
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.ui.theme.UfcatOrangeDark
import com.scherzolambda.horarios.ui.theme.UfcatRed
import com.scherzolambda.horarios.ui.utils.compareVersionsSimple
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import com.scherzolambda.horarios.viewmodel.UpdateViewModel

/**
 * Tela que exibe as aulas do dia atual, organizadas por turno (manhã, tarde, noite).
 * @param paddingValues Espaçamento interno para a tela, geralmente fornecido pelo Scaffold.
 * @param disciplinaViewModel ViewModel que gerencia o estado das disciplinas e horários.
 */
@Composable
fun DailyScreen(
    paddingValues: PaddingValues,
    disciplinaViewModel: DisciplinaViewModel,
    updateViewModel: UpdateViewModel
) {

    val disciplinasState = disciplinaViewModel.disciplinas.collectAsState()
    val disciplinas = disciplinasState.value
    val isLoading by disciplinaViewModel.isLoading.collectAsState()
    val latestVersion by updateViewModel.latestVersion.collectAsState()
    val downloadUrl by updateViewModel.downloadUrl.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val horariosSemanalState by disciplinaViewModel.weeklySchedule.collectAsState()
    val disciplinasHoje = remember(horariosSemanalState) {
        getTodayClasses2(horariosSemanalState)
    }

    LaunchedEffect(latestVersion, downloadUrl) {
        if( latestVersion != null) {
            val currentVersion = BuildConfig.VERSION_NAME
            val isNewer = currentVersion.compareVersionsSimple(latestVersion!!)
            val isNewer2 = latestVersion.compareVersionsSimple(currentVersion!!)
            Log.d("DailyScreen", "Versão atual: $currentVersion, Última versão: $latestVersion, isNewer: $isNewer")
            Log.d("DailyScreen", "isNewer2: $isNewer2")
            if (
                downloadUrl != null && isNewer == -1
            ) {
                Log.d("DailyScreen", "Nova versão disponível: $latestVersion")
                showDialog = true
            }
        }
    }
    var selectedCell by remember { mutableStateOf<HorarioSemanal?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (disciplinas.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nenhuma disciplina encontrada.",
                    modifier = Modifier.padding(8.dp),
                    color = LocalAppColors.current.content.blackText)
                Text(
                    "Por favor, vá para a aba 'Status' ou 'SIGAA' para carregar seus horários.",
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    color = LocalAppColors.current.content.blackText
                )
            }
        } else {

            when(disciplinasHoje.isEmpty()){
                true -> {
                    InfoCollumn(
                        title = "Nenhuma aula hoje",
                        info = "Você não tem aulas agendadas para hoje. Aproveite o dia!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                false -> {
                    // Header fixo no topo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        UfcatRed,
                                        Color(0xFFFF3366), // tom rosa-avermelhado
                                        Color(0xFFFF6600),  // tom laranja
                                        UfcatOrangeDark
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
                            color = LocalAppColors.current.content.blackText,
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
                        DialogInfoRow(
                            "Horário",
                            HourMaps.getHourRange(selectedCell!!.periodo, selectedCell!!.horario)
                        )
                        DialogInfoRow("Docente", selectedCell!!.docente)
                    }
                }
            )
        }

        // Update dialog
        if (showDialog && latestVersion != null && downloadUrl != null) {
            UpdateDialog(
                latestVersion = latestVersion,
                downloadUrl = downloadUrl,
                onDismiss = {
//                    downloadUrl = null
//                    latestVersion.value = ""
                    showDialog = false }
            )
        }
    }
}

@Composable
fun HoursOfDayComponent(
    hourType: HourType,
    disciplinasHoje: List<HorarioSemanal>,
    onDisciplinaClick: (HorarioSemanal) -> Unit
) {
    val hourMap = HourMaps.getHourMap(hourType)
    val disciplinasPorHora = remember(disciplinasHoje, hourType) {
        disciplinasHoje
            .filter { it.periodo == hourType }
            .groupBy { it.horario }
    }

    Card(
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppColors.current.content.whiteText
        )
    ) {
        // determine background color for this turno (same as WeeklyScreen)
        val (periodoColor, turnoLabel) = when (hourType) {
            HourType.M -> M_PeriodColor to "Turno Manhã"
            HourType.T -> T_PeriodColor to "Turno Tarde"
            HourType.N -> N_PeriodColor to "Turno Noite"
        }
        Text(turnoLabel,
            modifier = Modifier.padding(16.dp),
            color = LocalAppColors.current.content.blackText,
            fontWeight = Bold, fontSize = 20.sp)


        // Espaçamento entre blocos de horário
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            hourMap.forEach { (index, hour) ->
                val disciplinasNoHorario = disciplinasPorHora[index].orEmpty()


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
                            fontWeight = Bold,
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

@Composable
fun InfoCollumn(
    title: String,
    info: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = Bold,
            color = LocalAppColors.current.content.blackText
        )
        Text(
            text = info,
            fontSize = 16.sp,
            color = LocalAppColors.current.content.blackText
        )
    }
}

fun existeDisciplinaNoTurno(disciplinasHoje: List<HorarioSemanal>, hourType: HourType): Boolean {
    return disciplinasHoje.any { it.periodo == hourType }
}