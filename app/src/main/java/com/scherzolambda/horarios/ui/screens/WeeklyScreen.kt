package com.scherzolambda.horarios.ui.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.scherzolambda.horarios.ui.theme.UfcatBlack
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel
import kotlin.collections.forEach


@Composable
fun WeeklyScreen(
    paddingValues: PaddingValues
) {
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val disciplinasState = disciplinaViewModel.disciplinas.collectAsState()
    val disciplinas = disciplinasState.value
    Log.d("WeeklyScreen", "Disciplinas lidas: $disciplinas")

    val horarios = disciplinaViewModel.getWeeklySchedule()
    Log.d("WeeklyScreen", "Horários gerados: $horarios")
//    horarios.forEach {
//        Log.d("WeeklyScreen", "Item: diaSemana=${it.diaSemana}, periodo=${it.periodo}, horario=${it.horario}, disciplina=${it.disciplina}")
//    }
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
//            .padding(paddingValues)
            .padding(4.dp)
            .fillMaxHeight()
    ) {

        WeeklyScheduleOptimizedFinalNoEmpty(horarios)
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

@Composable
fun GridSemanalTable2(horarios: List<HorarioSemanal>) {
    val diasUteis = DaysOfWeekMap.days.filterKeys { it in 2..6 }
    val periodos = listOf(HourType.M, HourType.T)
    val horariosPorPeriodo = mapOf(
        HourType.M to HourMaps.M.keys,
        HourType.T to HourMaps.T.keys,
    )

//    val scrollState = rememberScrollState()

//    Column(
//        modifier = Modifier
//            .padding(4.dp)
//            .verticalScroll(scrollState)
//    ) {
//        DaysOfWeekHeader(diasUteis.values.toList()) // cabeçalho fixo
//
//        periodos.forEach { periodo ->
//            val periodoColor = when (periodo) {
//                HourType.M -> Color(0xFFE3F2FD)
//                HourType.T -> Color(0xFFFFF9C4)
//                HourType.N -> Color(0xFFFFCDD2)
//            }
//
//            horariosPorPeriodo[periodo]?.forEach { horarioNum ->
//
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    diasUteis.keys.forEach { diaKey ->
//                        val celula = horarios.find {
//                            it.diaSemana == diaKey && it.periodo == periodo && it.horario == horarioNum
//                        }
//
//                        ScheduleCell(
//                            text = celula?.disciplina ?: "-----",
//                            backgroundColor = periodoColor,
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(2.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxHeight(),
        state = scrollState
    ) {
        // Cabeçalho fixo
        item {
            DaysOfWeekHeader(diasUteis.values.toList())
        }

        periodos.forEach { periodo ->
            val periodoColor = when (periodo) {
                HourType.M -> Color(0xFFE3F2FD)
                HourType.T -> Color(0xFFFFF9C4)
                HourType.N -> Color(0xFFFFCDD2)
            }

            horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        diasUteis.keys.forEach { diaKey ->
                            val celula = horarios.find {
                                it.diaSemana == diaKey && it.periodo == periodo && it.horario == horarioNum
                            }

                            ScheduleCell(
                                text = celula?.disciplina ?: "-----",
                                backgroundColor = periodoColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekHeader(dias: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF263238))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dias.forEach { day ->
            Text(
                text = day,
                color = Color.White,
                fontWeight = Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ScheduleCell(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor, contentColor = Color.Black)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(6.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = Bold,
            maxLines = 3
        )
    }
}



@Composable
fun WeeklyScheduleOptimized(horarios: List<HorarioSemanal>) {
    val diasUteis = DaysOfWeekMap.days.filterKeys { it in 2..6 }
    val periodos = listOf(HourType.M, HourType.T)
    val horariosPorPeriodo = mapOf(
        HourType.M to HourMaps.M.keys,
        HourType.T to HourMaps.T.keys
    )

    // Lookup O(1)
    val horariosMap by remember(horarios) {
        mutableStateOf(horarios.associateBy { Triple(it.diaSemana, it.periodo, it.horario) })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Cabeçalho fixo
        DaysOfWeekHeader(diasUteis.values.toList())

        // Grid com scroll preguiçoso
        val columnCount = diasUteis.size
        val gridItems = remember(diasUteis, periodos, horariosPorPeriodo, horariosMap) {
            val list = mutableListOf<Pair<String, Color>>()
            periodos.forEach { periodo ->
                val periodoColor = when (periodo) {
                    HourType.M -> Color(0xFFE3F2FD)
                    HourType.T -> Color(0xFFFFF9C4)
                    HourType.N -> Color(0xFFFFCDD2)
                }
                horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                    diasUteis.keys.forEach { diaKey ->
                        val celula = horariosMap[Triple(diaKey, periodo, horarioNum)]
                        list.add((celula?.disciplina ?: "-----") to periodoColor)
                    }
                }
            }
            list
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentPadding = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(gridItems.size) { index ->
                val (text, color) = gridItems[index]
                if(text.equals("-----")){
                    Log.d("WeeklyScheduleOptimized", "Célula vazia encontrada no índice $index")
                }else{

                    Box(
                        modifier = Modifier
                            .background(color, shape = RoundedCornerShape(4.dp))
                            .padding(6.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f), // mantém células quadradas
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            fontSize = 11.sp,
                            color = UfcatBlack,
                            fontWeight = Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyScheduleOptimizedFinalNoEmpty(horarios: List<HorarioSemanal>) {
    val diasUteis = DaysOfWeekMap.days.filterKeys { it in 2..6 }
    val periodos = listOf(HourType.M, HourType.T)
    val horariosPorPeriodo = mapOf(
        HourType.M to HourMaps.M.keys,
        HourType.T to HourMaps.T.keys
    )

    // Lookup O(1)
    val horariosMap by remember(horarios) {
        mutableStateOf(horarios.associateBy { Triple(it.diaSemana, it.periodo, it.horario) })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Cabeçalho fixo
        DaysOfWeekHeader(diasUteis.values.toList())

        // Cria lista apenas com células não vazias
        val gridItems = remember(diasUteis, periodos, horariosPorPeriodo, horariosMap) {
            val list = mutableListOf<Pair<String, Color>>()
            periodos.forEach { periodo ->
                val periodoColor = when (periodo) {
                    HourType.M -> Color(0xFFE3F2FD)
                    HourType.T -> Color(0xFFFFF9C4)
                    HourType.N -> Color(0xFFFFCDD2)
                }
                horariosPorPeriodo[periodo]?.forEach { horarioNum ->
                    diasUteis.keys.forEach { diaKey ->
                        val celula = horariosMap[Triple(diaKey, periodo, horarioNum)]
//                        celula?.disciplina?.let {
//                            list.add(it to periodoColor)
//                        }
                        list.add((celula?.disciplina ?: "-----") to periodoColor)
                    }
                }
            }
            list
        }

        if (gridItems.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(diasUteis.size),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(gridItems.size, key = { index -> index }) { index ->
                    val (text, color) = gridItems[index]
                    if(text.equals("-----")){
//                        Log.d("WeeklyScheduleOptimized", "Célula vazia encontrada no índice $index")
                    }else{

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxWidth()
                                .background(color, shape = RoundedCornerShape(4.dp))
                                .padding(2.dp),
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
        }
    }
}


