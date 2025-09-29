package com.scherzolambda.horarios.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.enums.DaysOfWeekMap
import com.scherzolambda.horarios.data_transformation.montarHorariosSemanais
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.data_transformation.lerDisciplinasLocal
import com.scherzolambda.horarios.data_transformation.montarHorariosSemanaisDeDisciplinas
import com.scherzolambda.horarios.ui.components.WeekComponent


@Composable
fun WeeklyScreen(
    paddingValues: PaddingValues
) {
    val context = LocalContext.current

    val disciplinas = lerDisciplinasLocal(context)
    Log.d("WeeklyScreen", "Disciplinas lidas: $disciplinas")

    val horarios = montarHorariosSemanaisDeDisciplinas(disciplinas)
    Log.d("WeeklyScreen", "Horários gerados: $horarios")
    horarios.forEach {
        Log.d("WeeklyScreen", "Item: diaSemana=${it.diaSemana}, periodo=${it.periodo}, horario=${it.horario}, disciplina=${it.disciplina}")
    }
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(paddingValues)
            .padding(4.dp)
            .fillMaxWidth()
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
        Log.d("GridSemanalTable", "Iniciando construção da tabela")
        Log.d("WeekScreen", "Dias úteis: $horarios")
        DaysOfWeekComponent()
        // Corpo da tabela
        periodos.forEach { periodo ->
            horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    diasUteis.keys.forEach { diaKey ->
                        val celula = horarios.find {
                            it.diaSemana == diaKey && it.periodo == periodo && it.horario == horarioNum
                        }
                        Card(
                            modifier = Modifier
                                .weight(10f)
                                .padding(2.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Text(
                                text = celula?.disciplina ?: "-----",
                                modifier = Modifier.padding(4.dp),
                                maxLines = 3,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
