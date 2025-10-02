package com.scherzolambda.horarios.ui.screens.week

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.enums.DaysOfWeekMap
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.ui.components.DaysOfWeekHeader
import com.scherzolambda.horarios.ui.theme.AppTypography
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.M_PeriodColor
import com.scherzolambda.horarios.ui.theme.N_PeriodColor
import com.scherzolambda.horarios.ui.theme.T_PeriodColor
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.ui.theme.UfcatGray
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel


@Composable
fun WeeklyScreen(
    disciplinaViewModel: DisciplinaViewModel
) {
    val horarios by disciplinaViewModel.weeklySchedule.collectAsState()
    val isLoading by disciplinaViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxHeight()
            .background(LocalAppColors.current.content.background)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            WeeklySchedule(horarios,context)
        }
    }
}


@Composable
fun WeeklySchedule(
    horarios: List<HorarioSemanal>,
    context: Context) {
    val diasUteis = DaysOfWeekMap.days.filterKeys { it in 2..6 }
    val periodos = listOf(HourType.M, HourType.T)
    val horariosPorPeriodo = mapOf(
        HourType.M to HourMaps.M.keys,
        HourType.T to HourMaps.T.keys
    )

    val horariosMap by remember(horarios) {
        mutableStateOf(horarios.associateBy { Triple(it.diaSemana, it.periodo, it.horario) })
    }

    var selectedCell by remember { mutableStateOf<HorarioSemanal?>(null) }
    val isFileLoaded by DataStoreHelper.isFileLoadedFlow(context).collectAsState(false)
    Column(modifier = Modifier
        .fillMaxSize()
        .background(LocalAppColors.current.content.background)) {
        // Cabeçalho fixo
        DaysOfWeekHeader(diasUteis.values.toList())

        val gridItems = remember(diasUteis, periodos, horariosPorPeriodo, horariosMap) {
            val list = mutableListOf<Triple<String, Color, HorarioSemanal?>>()
            periodos.forEach { periodo ->
                val periodoColor = when (periodo) {
                    HourType.M -> M_PeriodColor
                    HourType.T -> T_PeriodColor
                    HourType.N -> N_PeriodColor
                }
                horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                    diasUteis.keys.forEach { diaKey ->
                        val celula = horariosMap[Triple(diaKey, periodo, horarioNum)]
                        list.add(Triple(celula?.disciplina ?: "-----", periodoColor, celula))
                    }
                }
            }
            list
        }

       when (isFileLoaded) {
            true -> LazyVerticalGrid(
                columns = GridCells.Fixed(diasUteis.size),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(gridItems.size, key = { index -> index }) { index ->
                    val (text, color, celula) = gridItems[index]
                    if(text.equals("-----")){
                        // Célula vazia, não renderiza nada
                    } else {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxWidth()
                                .background(color, shape = RoundedCornerShape(4.dp))
                                .padding(2.dp)
                                .clickable {
                                    celula?.let {
                                        selectedCell = it
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = text,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UfcatBlack,
                                textAlign = TextAlign.Center,
                                maxLines = 3
                            )
                        }
                    }
                }
            }
            false -> MessageInfoCard(
                title = "Nenhum horário disponível",
                info = "Parece que não há disciplinas cadastradas no momento.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        selectedCell?.let { cell ->
            AlertDialog(
                onDismissRequest = { selectedCell = null },
                confirmButton = { /* nenhum botão */ },
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = cell.disciplina,
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
                    Column(modifier = Modifier.padding(8.dp)) {
                        if (cell.local.isNotBlank()) {
                            DialogInfoRow(label = "Local:", value = cell.local)
                        }
                        DialogInfoRow(label = "Horário:", value = HourMaps.getHourRange(cell.periodo, cell.horario))
                        DialogInfoRow(label = "Período:", value = HourMaps.getHourName(cell.periodo))
                        DialogInfoRow(label = "Docente:", value = cell.docente)
                    }
                }
            )
        }
    }
}

@Composable
fun DialogInfoRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                text = value,
                style = AppTypography.bodyLarge,
                modifier = Modifier.weight(2f)
            )
        }
    }
}

@Composable
fun MessageInfoCard(
    title: String,
    info: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(LocalAppColors.current.content.whiteText,
                shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = AppTypography.headlineSmall,
                color = LocalAppColors.current.content.blackText,
                textAlign = TextAlign.Center
            )
            if (info.isNotEmpty()) {
                Text(
                    text = "$info \n Baixe seu comprovante de matricula pela aba 'SIGAA'",
                    style = AppTypography.bodyMedium,
                    color = LocalAppColors.current.content.blackText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}