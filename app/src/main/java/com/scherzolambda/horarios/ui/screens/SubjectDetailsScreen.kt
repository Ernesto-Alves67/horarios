package com.scherzolambda.horarios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scherzolambda.horarios.viewmodel.DisciplinaViewModel

@Composable
fun SubjectDetailsScreen(codigo: String) {
    val disciplinaViewModel: DisciplinaViewModel = hiltViewModel()
    val disciplinas = disciplinaViewModel.disciplinas.collectAsState().value
    val disciplina = disciplinas.find { it.codigo == codigo }

    if (disciplina == null) {
        Text("Disciplina não encontrada", modifier = Modifier.padding(16.dp))
        return
    }

    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = disciplina.componenteCurricular, fontSize = 22.sp)
            Text(text = "Código: ${disciplina.codigo}", fontSize = 16.sp)
            Text(text = "Turma: ${disciplina.turma}", fontSize = 16.sp)
            Text(text = "Status: ${disciplina.status}", fontSize = 16.sp)
            Text(text = "Horário: ${disciplina.horario}", fontSize = 16.sp)
            Text(text = "Local: ${disciplina.local}", fontSize = 16.sp)
        }
    }
}

