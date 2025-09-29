package com.scherzolambda.horarios.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scherzolambda.horarios.data_transformation.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.enums.DaysOfWeekMap
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.ui.theme.Transparent
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel


@Composable
fun WeeklyScreen(
    paddingValues: PaddingValues
) {
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val disciplinasState = disciplinaViewModel.disciplinas.collectAsState()
    val disciplinas = disciplinasState.value
    Log.d("WeeklyScreen", "Disciplinas lidas: $disciplinas")
    LaunchedEffect(Unit) {
        disciplinaViewModel.carregarDisciplinasLocal()
    }
    val horarios = disciplinaViewModel.getWeeklySchedule()
    Log.d("WeeklyScreen", "Horários gerados: $horarios")
    horarios.forEach {
        Log.d("WeeklyScreen", "Item: diaSemana=${it.diaSemana}, periodo=${it.periodo}, horario=${it.horario}, disciplina=${it.disciplina}")
    }
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
//            .padding(paddingValues)
            .padding(4.dp)
            .fillMaxHeight()
    ) {

        GridSemanalTable(horarios)
    }
}

@Composable
fun DaysOfWeekComponent() {
    Card(
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DaysOfWeekMap.days.values.forEach { day ->
                if(day != "Sábado" && day != "Domingo"){
                    Text(day, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }
}

@Composable
fun GridSemanalTable(horarios: List<HorarioSemanal>) {
    val diasUteis = DaysOfWeekMap.days.filterKeys { it in 1..5 }
    val periodos = listOf(HourType.M, HourType.T)
    val horariosPorPeriodo = mapOf(
        HourType.M to HourMaps.M.keys,
        HourType.T to HourMaps.T.keys,
    )
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(4.dp)
            .verticalScroll(scrollState)
    ) {
        Log.d("WeekScreen", "Dias úteis: $horarios")
        DaysOfWeekComponent()
        // Corpo da tabela
        periodos.forEach { periodo ->
            //cores para M, T, N
            val periodoColor = when (periodo) {
                HourType.M -> 0xFFE3F2FD // Azul claro para Manhã
                HourType.T -> 0xFFFFF9C4 // Amarelo claro para Tarde
                HourType.N -> 0xFFFFCDD2 // Vermelho claro para Noite
            }
            horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                Row(modifier = Modifier.fillMaxWidth().background(Color(periodoColor))) {
                    diasUteis.keys.forEach { diaKey ->
                        val celula = horarios.find {
                            it.diaSemana == diaKey && it.periodo == periodo && it.horario == horarioNum
                        }
                        Card(
                            modifier = Modifier
                                .weight(10f)
                                .padding(2.dp).background(Transparent),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(periodoColor), // fundo do card
                                contentColor = Color.Black          // cor do texto
                            ),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Text(
                                text = celula?.disciplina ?: "-----",
                                modifier = Modifier.padding(4.dp),
                                textAlign = TextAlign.Center,
                                maxLines = 3,
                                fontWeight = Bold,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
