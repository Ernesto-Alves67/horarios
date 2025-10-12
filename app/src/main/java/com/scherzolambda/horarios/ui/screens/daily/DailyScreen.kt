package com.scherzolambda.horarios.ui.screens.daily

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.scherzolambda.horarios.BuildConfig
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.ui.screens.daily.components.HoursOfDayComponent
import com.scherzolambda.horarios.ui.screens.daily.components.InfoCollumn
import com.scherzolambda.horarios.ui.screens.daily.components.existeDisciplinaNoTurno
import com.scherzolambda.horarios.ui.screens.updater.UpdateDialog
import com.scherzolambda.horarios.ui.screens.week.DialogInfoRow
import com.scherzolambda.horarios.ui.theme.AppTypography
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.Transparent
import com.scherzolambda.horarios.ui.theme.UfcatOrangeDark
import com.scherzolambda.horarios.ui.theme.UfcatRed
import com.scherzolambda.horarios.ui.utils.compareVersionsSimple
import com.scherzolambda.horarios.viewmodel.AppUpdateInfo

/**
 * Tela que exibe as aulas do dia atual, organizadas por turno (manhã, tarde, noite).
 * @param paddingValues Espaçamento interno para a tela, geralmente fornecido pelo Scaffold.
 * @param disciplinaViewModel ViewModel que gerencia o estado das disciplinas e horários.
 */
@Composable
fun DailyScreen(
    paddingValues: PaddingValues,
    disciplinasHoje: List<HorarioSemanal>,
    updateInfo: AppUpdateInfo,
    isLoading: Boolean,
    isShowEmptyCells: Boolean,
) {

    var latestVersion by remember { mutableStateOf<String?>(updateInfo.latestVersion) }
    var downloadUrl by remember { mutableStateOf<String?>(updateInfo.downloadUrl) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(latestVersion, downloadUrl) {
        if( latestVersion != null) {
            val currentVersion = BuildConfig.VERSION_NAME
            val isNewer = currentVersion.compareVersionsSimple(latestVersion!!)
            if (
                downloadUrl != null && isNewer == -1
            ) {
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
        } else if (disciplinasHoje.isEmpty()) {
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
                    val glassBrush = Brush.horizontalGradient(
                        colors = listOf(
                            UfcatRed,
                            Color(0xFFFF3366), // tom rosa-avermelhado
                            Color(0xFFFF6600),  // tom laranja
                            UfcatOrangeDark
                        )

                    )
                    // Header fixo no topo
                    Card(
                        modifier = Modifier.padding(16.dp)
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
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        elevation = CardDefaults.cardElevation(8.dp),){

                        Text(
                            text = "Aulas de Hoje",
                            fontSize = 32.sp,
                            fontWeight = Bold,
                            color = LocalAppColors.current.content.blackText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(glassBrush)
                                .align(Alignment.CenterHorizontally)
                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                                .background(color = Color.Unspecified)
//
//                        ) {
//                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(4.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (existeDisciplinaNoTurno(disciplinasHoje, HourType.M)) {
                            HoursOfDayComponent(hourType = HourType.M, disciplinasHoje = disciplinasHoje, onDisciplinaClick = { selectedCell = it }, isShowEmpty = isShowEmptyCells)
                        }
                        if (existeDisciplinaNoTurno(disciplinasHoje, HourType.T)) {
                            HoursOfDayComponent(hourType = HourType.T, disciplinasHoje = disciplinasHoje, onDisciplinaClick = { selectedCell = it }, isShowEmpty = isShowEmptyCells)
                        }
                        if (existeDisciplinaNoTurno(disciplinasHoje, HourType.N)) {
                            HoursOfDayComponent(hourType = HourType.N, disciplinasHoje = disciplinasHoje, onDisciplinaClick = { selectedCell = it }, isShowEmpty = isShowEmptyCells)
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
                latestVersion = latestVersion.toString(),
                downloadUrl = downloadUrl.toString(),
                onDismiss = {
                    downloadUrl = null
                    latestVersion = null
                    showDialog = false }
            )
        }
    }
}
