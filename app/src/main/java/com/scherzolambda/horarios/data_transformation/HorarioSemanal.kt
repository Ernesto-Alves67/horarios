package com.scherzolambda.horarios.data_transformation

import com.scherzolambda.horarios.data_transformation.enums.HourType

// Representa um horário semanal para o grid
// diaSemana: Int (1=Segunda, 2=Terça, ...)
// periodo: HourType (T, N, M)
// horario: Int (número do horário)
// intervalo: String (ex: "13:00-13:50")
// disciplina: String (nome da disciplina)
data class HorarioSemanal(
    val diaSemana: Int,
    val periodo: HourType,
    val horario: Int,
    val intervalo: String,
    val disciplina: String
)
