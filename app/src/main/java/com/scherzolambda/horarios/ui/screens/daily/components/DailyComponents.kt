package com.scherzolambda.horarios.ui.screens.daily.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.M_PeriodColor
import com.scherzolambda.horarios.ui.theme.N_PeriodColor
import com.scherzolambda.horarios.ui.theme.T_PeriodColor
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.orEmpty


@Composable
fun HoursOfDayComponent(
    hourType: HourType,
    isShowEmpty: Boolean,
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
                Log.d("DailyScreen", "HourType: $hourType, HourIndex: $index, Disciplinas: $disciplinasNoHorario")
                if(disciplinasNoHorario.isEmpty()){
                    if (isShowEmpty){
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
                            Text(
                                text = "Horário vago",
                                fontSize = 16.sp,
                                color = UfcatBlack,
                                fontWeight = Bold,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                    Log.d("DailyScreen", "Exibindo horário vazio para HourType: $hourType, HourIndex: $index")
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