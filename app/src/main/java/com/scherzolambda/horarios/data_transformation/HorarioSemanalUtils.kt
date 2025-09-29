package com.scherzolambda.horarios.data_transformation

import com.scherzolambda.horarios.data_transformation.enums.HourType
import java.time.LocalDate


// Função utilitária para montar lista de HorarioSemanal a partir de uma lista de Disciplina
fun montarHorariosSemanaisDeDisciplinas(disciplinas: List<Disciplina>): List<HorarioSemanal> {
    val preparer = DataPreparation()
    val horarios = mutableListOf<HorarioSemanal>()
    for (disciplina in disciplinas) {
        val codigos = when (disciplina.horario) {
            is List<*> -> disciplina.horario.filterIsInstance<String>()
            is String -> listOf(disciplina.horario)
            else -> emptyList()
        }
        for (codigo in codigos) {
            val correspondencias = preparer.decomporMultiplosCodigos(codigo)
            for ((diaSemanaStr, periodoDiaStr, horarioStr) in correspondencias) {
                println("Disciplina: ${disciplina.componenteCurricular}, Código: $codigo -> Dia: $diaSemanaStr, Período: $periodoDiaStr, Horário: $horarioStr")
                if(diaSemanaStr.length == 2){
                    val dia1 = diaSemanaStr[0].toString().toIntOrNull() ?: continue
                    val dia2 = diaSemanaStr[1].toString().toIntOrNull() ?: continue
                    val periodo = try { HourType.valueOf(periodoDiaStr) } catch (e: Exception) { continue }
                    if(horarioStr.length >=2){
                        val horariosSplit = horarioStr.map { it.toString() }
                        for(horarioChunk in horariosSplit){
                            val horarioIntChunk = horarioChunk.toIntOrNull() ?: continue
                            horarios.add(
                                HorarioSemanal(
                                    diaSemana = dia1,
                                    periodo = periodo,
                                    horario = horarioIntChunk,
                                    disciplina = disciplina.componenteCurricular,
                                    local = disciplina.local
                                )
                            )
                            horarios.add(
                                HorarioSemanal(
                                    diaSemana = dia2,
                                    periodo = periodo,
                                    horario = horarioIntChunk,
                                    disciplina = disciplina.componenteCurricular,
                                    local = disciplina.local
                                )
                            )
                        }
                        continue
                    }
                    val horarioInt = horarioStr.toIntOrNull() ?: continue
                    horarios.add(
                        HorarioSemanal(
                            diaSemana = dia1,
                            periodo = periodo,
                            horario = horarioInt,
                            disciplina = disciplina.componenteCurricular,
                            local = disciplina.local
                        )
                    )
                    horarios.add(
                        HorarioSemanal(
                            diaSemana = dia2,
                            periodo = periodo,
                            horario = horarioInt,
                            disciplina = disciplina.componenteCurricular,
                            local = disciplina.local
                        )
                    )
                    continue
                }
                val diaSemanaInt = diaSemanaStr.toIntOrNull() ?: continue
                val periodo = try { HourType.valueOf(periodoDiaStr) } catch (e: Exception) { continue }
                if(horarioStr.length >=2){
                    val horariosSplit = horarioStr.map { it.toString() }
                    for(horarioChunk in horariosSplit){
                        val horarioIntChunk = horarioChunk.toIntOrNull() ?: continue
                        horarios.add(
                            HorarioSemanal(
                                diaSemana = diaSemanaInt,
                                periodo = periodo,
                                horario = horarioIntChunk,
                                disciplina = disciplina.componenteCurricular,
                                local = disciplina.local
                            )
                        )
                    }
                    continue
                }
                val horarioInt = horarioStr.toIntOrNull() ?: continue
                horarios.add(
                    HorarioSemanal(
                        diaSemana = diaSemanaInt,
                        periodo = periodo,
                        horario = horarioInt,
                        disciplina = disciplina.componenteCurricular,
                        local = disciplina.local
                    )
                )
            }
        }
    }
    return horarios
}

fun filtrarHorariosDoDiaAtual(horarios: List<HorarioSemanal>): List<HorarioSemanal> {
    val diaAtual = LocalDate.now().dayOfWeek.value + 1 // 1=Segunda, 7=Domingo
    return horarios.filter { it.diaSemana == diaAtual }
}

fun getTodayClasses(disciplinas: List<Disciplina>): List<HorarioSemanal> {
    val horarios = montarHorariosSemanaisDeDisciplinas(disciplinas)
    return filtrarHorariosDoDiaAtual(horarios)

}