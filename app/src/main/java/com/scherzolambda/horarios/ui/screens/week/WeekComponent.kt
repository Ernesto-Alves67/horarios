package com.scherzolambda.horarios.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.enums.DaysOfWeekMap
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType

@Composable
fun WeekComponent(
    modifier: Modifier = Modifier
) {
    val diasUteis = DaysOfWeekMap.days.filterKeys { it in 1..5 }
    val periodos = listOf(HourType.M, HourType.T)
    val horariosPorPeriodo = mapOf(
        HourType.M to HourMaps.M.keys,
        HourType.T to HourMaps.T.keys,
    )

    Column(modifier = modifier.padding(8.dp)) {
        // Cabeçalho dos dias da semana
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            diasUteis.values.forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier
                        .weight(2f)
                        .padding(4.dp),
                    fontSize = 14.sp
                )
            }
        }
        // Linhas de horários
        periodos.forEach { periodo ->
            horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Coluna do horário
                    val intervalo = HourMaps.getHourMap(periodo)[horarioNum] ?: ""
                    Text(
                        text = intervalo,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        fontSize = 12.sp
                    )
                    // Células dos dias
                    diasUteis.keys.forEach { _ ->
                        Card(
                            modifier = Modifier
                                .weight(2f)
                                .padding(2.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            // Conteúdo vazio, pode ser preenchido depois
                            Text(
                                text = "NAda",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
