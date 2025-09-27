package com.scherzolambda.horarios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.enums.DaysOfWeekMap
import com.scherzolambda.horarios.data_transformation.montarHorariosSemanais
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType


@Composable
fun WeeklyScreen() {
    val codigos = listOf("2T1","4M3")
    val disciplina = "Matemática"
    val horarios = montarHorariosSemanais(codigos, disciplina)
    Column {
        DaysOfWeekComponent()
        GridSemanalTable(horarios)
    }
}

@Composable
fun DaysOfWeekComponent() {
    Card(
        modifier = Modifier.padding(16.dp),
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
    val periodos = listOf(HourType.M, HourType.T, HourType.N)
    val horariosPorPeriodo = mapOf(
        HourType.M to HourMaps.M.keys,
        HourType.T to HourMaps.T.keys,
        HourType.N to HourMaps.N.keys
    )
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        // Corpo da tabela
        periodos.forEach { periodo ->
            horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Coluna do horário
                    val intervalo = HourMaps.getHourMap(periodo)[horarioNum] ?: ""
//                    Text(text = "${periodo.name} $horarioNum\n$intervalo", modifier = Modifier.weight(1f))
                    // Células dos dias
                    diasUteis.keys.forEach { diaKey ->
                        val celula = horarios.find {
                            it.diaSemana == diaKey && it.periodo == periodo && it.horario == horarioNum
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Text(
                                text = celula?.disciplina ?: "",
                                modifier = Modifier.padding(4.dp),
                                maxLines = 2,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
