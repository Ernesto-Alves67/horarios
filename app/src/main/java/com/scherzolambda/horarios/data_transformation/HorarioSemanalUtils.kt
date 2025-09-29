package com.scherzolambda.horarios.data_transformation

import android.util.Log
import com.scherzolambda.horarios.data_transformation.enums.HourMaps
import com.scherzolambda.horarios.data_transformation.enums.HourType
import com.scherzolambda.horarios.data_transformation.enums.DaysOfWeekMap
import java.time.LocalDate
import kotlin.text.map

// Função utilitária para montar lista de HorarioSemanal a partir de um texto e disciplina
fun montarHorariosSemanais(textos: List<String>, disciplina: String): List<HorarioSemanal> {
    val preparer = DataPreparation()
    val horarios = mutableListOf<HorarioSemanal>()
    for (texto in textos) {
        val (diaSemanaStr, periodoDiaStr, horarioStr) = preparer.decomporMultiplosCodigos(texto)
        if(diaSemanaStr.length == 2){
            val dia1 = diaSemanaStr[0].toString().toIntOrNull() ?: continue
            val dia2 = diaSemanaStr[1].toString().toIntOrNull() ?: continue
            val periodo = try { HourType.valueOf(periodoDiaStr) } catch (e: Exception) { continue }
            val horarioInt = horarioStr.toIntOrNull() ?: continue
            horarios.add(
                HorarioSemanal(
                    diaSemana = dia1,
                    periodo = periodo,
                    horario = horarioInt,
                    disciplina = disciplina
                )
            )
            horarios.add(
                HorarioSemanal(
                    diaSemana = dia2,
                    periodo = periodo,
                    horario = horarioInt,
                    disciplina = disciplina
                )
            )
            continue
        }
        val diaSemanaInt = diaSemanaStr.toIntOrNull() ?: continue
        val periodo = try { HourType.valueOf(periodoDiaStr) } catch (e: Exception) { continue }
        val horarioInt = horarioStr.toIntOrNull() ?: continue
        horarios.add(
            HorarioSemanal(
                diaSemana = diaSemanaInt,
                periodo = periodo,
                horario = horarioInt,
                disciplina = disciplina
            )
        )
    }
    return horarios
}

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
            val (diaSemanaStr, periodoDiaStr, horarioStr) = preparer.decomporMultiplosCodigos(codigo)
            if(diaSemanaStr.length == 2){
                val dia1 = diaSemanaStr[0].toString().toIntOrNull() ?: continue
                val dia2 = diaSemanaStr[1].toString().toIntOrNull() ?: continue
                val periodo = try { HourType.valueOf(periodoDiaStr) } catch (e: Exception) { continue }
                if(horarioStr.length >=2){
                    Log.d("WeekUtils", "${disciplina.componenteCurricular} Dividindo horário composto: $horarioStr")
                    val horariosSplit = horarioStr.map { it.toString() }
                    Log.d("WeekUtils", "Horários divididos: $horariosSplit")
                    for(horarioChunk in horariosSplit){
                        val horarioIntChunk = horarioChunk.toIntOrNull() ?: continue
                        horarios.add(
                            HorarioSemanal(
                                diaSemana = dia1,
                                periodo = periodo,
                                horario = horarioIntChunk,
                                disciplina = disciplina.componenteCurricular
                            )
                        )
                        horarios.add(
                            HorarioSemanal(
                                diaSemana = dia2,
                                periodo = periodo,
                                horario = horarioIntChunk,
                                disciplina = disciplina.componenteCurricular
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
                        disciplina = disciplina.componenteCurricular
                    )
                )
                horarios.add(
                    HorarioSemanal(
                        diaSemana = dia2,
                        periodo = periodo,
                        horario = horarioInt,
                        disciplina = disciplina.componenteCurricular
                    )
                )
                continue
            }
            val diaSemanaInt = diaSemanaStr.toIntOrNull() ?: continue
            val periodo = try { HourType.valueOf(periodoDiaStr) } catch (e: Exception) { continue }
            if(horarioStr.length >=2){
                Log.d("WeekUtils", "${disciplina.componenteCurricular}Dividindo horário composto: $horarioStr")
                val horariosSplit = horarioStr.map { it.toString() }
                Log.d("WeekUtils", "Horários divididos: $horariosSplit")
                for(horarioChunk in horariosSplit){
                    val horarioIntChunk = horarioChunk.toIntOrNull() ?: continue
                    horarios.add(
                        HorarioSemanal(
                            diaSemana = diaSemanaInt,
                            periodo = periodo,
                            horario = horarioIntChunk,
                            disciplina = disciplina.componenteCurricular
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
                    disciplina = disciplina.componenteCurricular
                )
            )
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