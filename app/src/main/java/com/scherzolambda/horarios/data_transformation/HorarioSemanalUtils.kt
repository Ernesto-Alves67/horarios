package com.scherzolambda.horarios.data_transformation

import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.data_transformation.enums.DaysOfWeekMap

// Função utilitária para montar lista de HorarioSemanal a partir de um texto e disciplina
fun montarHorariosSemanais(textos: List<String>, disciplina: String): List<HorarioSemanal> {
    val preparer = DataPreparation()
    val horarios = mutableListOf<HorarioSemanal>()
    for (texto in textos) {
        val (diaSemanaStr, periodoDiaStr, horarioStr) = preparer.decomporMultiplosCodigos(texto)
        val diaSemanaInt = diaSemanaStr.toIntOrNull() ?: continue
        val periodo = try { HourType.valueOf(periodoDiaStr) } catch (e: Exception) { continue }
        val horarioInt = horarioStr.toIntOrNull() ?: continue
        val intervalo = HourMaps.getHourMap(periodo)[horarioInt] ?: "Horário inválido"
        horarios.add(
            HorarioSemanal(
                diaSemana = diaSemanaInt,
                periodo = periodo,
                horario = horarioInt,
                intervalo = intervalo,
                disciplina = disciplina
            )
        )
    }
    return horarios
}
