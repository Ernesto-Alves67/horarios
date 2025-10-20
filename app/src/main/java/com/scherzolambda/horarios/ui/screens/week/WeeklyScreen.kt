package com.scherzolambda.horarios.ui.screens.week

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.ui.components.WeeklySchedule
import com.scherzolambda.horarios.ui.theme.LocalAppColors


@Composable
fun WeeklyScreen(
    horarios: List<HorarioSemanal>,
    isLoading: Boolean,
    isShowEmptyCells: Boolean,
    paddingValues: PaddingValues
) {

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(paddingValues)
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
            WeeklySchedule(horarios, isShowEmptyCells)
        }
    }
}

