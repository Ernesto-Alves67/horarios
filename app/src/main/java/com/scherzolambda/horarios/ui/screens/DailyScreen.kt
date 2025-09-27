package com.scherzolambda.horarios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType

@Composable
fun HoursOfDayComponent(hourType: HourType = HourType.M) {
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
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text("$index", modifier = Modifier.padding(end = 8.dp))
                    Text(hour)
                }
            }
        }
    }
}

@Composable
fun DailyScreen() {
    Column {
        Text("Aulas de hoje",
            modifier = Modifier.padding(16.dp),
            fontSize = 22.sp)
        HoursOfDayComponent(hourType = HourType.M)
        HoursOfDayComponent(hourType = HourType.T)
        Spacer(modifier = Modifier.padding(8.dp))
    }
}