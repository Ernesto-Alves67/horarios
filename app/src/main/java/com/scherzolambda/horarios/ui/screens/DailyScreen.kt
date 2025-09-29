package com.scherzolambda.horarios.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import androidx.hilt.navigation.compose.hiltViewModel
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import androidx.compose.runtime.collectAsState
import com.scherzolambda.horarios.data_transformation.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.getTodayClasses
import java.time.LocalDate
import java.time.DayOfWeek

@Composable
fun HoursOfDayComponent(
    hourType: HourType = HourType.M,
    disciplinasHoje: List<HorarioSemanal>
) {
    val hourMap = HourMaps.getHourMap(hourType)
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        when(hourType) {
            HourType.T -> Text("Turno Tarde", modifier = Modifier.padding(8.dp))
            HourType.N -> Text("Turno Noite", modifier = Modifier.padding(8.dp))
            HourType.M -> Text("Turno Manhã", modifier = Modifier.padding(8.dp))
        }
        Column(modifier = Modifier.padding(8.dp)) {
            hourMap.forEach { (index, hour) ->
                val disciplinasNoHorario = disciplinasHoje.filter {
                    it.periodo == hourType && it.horario == index
                }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text("$index", modifier = Modifier.padding(end = 8.dp))
                    Text(hour)
                }
                disciplinasNoHorario.forEach { disciplina ->
                    Log.d("HoursOfDayComponent", "Disciplina no horário: ${disciplina.local}")
                    Text(
                        text = disciplina.disciplina,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 24.dp, bottom = 2.dp)
                    )
                    Text(
                        text = disciplina.local,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 24.dp, bottom = 2.dp)
                    )
                }
            }
        }
    }
}

fun existeDisciplinaNoTurno(disciplinasHoje: List<HorarioSemanal>, hourType: HourType): Boolean {
    return disciplinasHoje.any { it.periodo == hourType }
}

@Composable
fun DailyScreen(
    paddingValues: PaddingValues
) {
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val disciplinasState = disciplinaViewModel.disciplinas.collectAsState()
    val disciplinas = disciplinasState.value
    val hoje = LocalDate.now().dayOfWeek.value +1// 2=Segunda, 3=terça

    Log.d("DailyScreen", "Hoje é dia da semana: $hoje")
    Log.d("DailyScreen", "Disciplinas lidas: $disciplinas")
    val disciplinasHoje = getTodayClasses(disciplinas)

//    LaunchedEffect(Unit) {
//        disciplinaViewModel.carregarDisciplinasLocal()
//    }
    Column(
        modifier = Modifier
            .padding(paddingValues)
    ){
        Text("Aulas de hoje",
            modifier = Modifier.padding(16.dp),
            fontSize = 22.sp)
        if (existeDisciplinaNoTurno(disciplinasHoje, HourType.M)) {
            HoursOfDayComponent(hourType = HourType.M, disciplinasHoje = disciplinasHoje)
        }
        if (existeDisciplinaNoTurno(disciplinasHoje, HourType.T)) {
            HoursOfDayComponent(hourType = HourType.T, disciplinasHoje = disciplinasHoje)
        }
        if (existeDisciplinaNoTurno(disciplinasHoje, HourType.N)) {
            HoursOfDayComponent(hourType = HourType.N, disciplinasHoje = disciplinasHoje)
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }
}