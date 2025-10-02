package com.scherzolambda.horarios.data_transformation.models

import com.scherzolambda.horarios.data_transformation.enums.HourType

/** Modelo de dados para representar um horário semanal
 * @param diaSemana Dia da semana (2 = Segunda, 7 = Domingo)
 * @param periodo Período do dia (M = Matutino, T = Vespertino)
 * @param horario Número do horário dentro do período
 * @param disciplina Nome ou código da disciplina associada a esse horário
 */
data class HorarioSemanal(
    val diaSemana: Int,
    val periodo: HourType,
    val horario: Int,
    val disciplina: String,
    val local: String,
    val docente: String
)