package com.scherzolambda.horarios.data_transformation

import com.scherzolambda.horarios.data_transformation.enums.HourType
import java.time.LocalDate


/** Modelo de dados para representar uma disciplina
 * @param componenteCurricular Nome ou código da disciplina
 * @param horario Código(s) do horário associado a essa disciplina (pode ser uma String ou uma lista de Strings)
 * @param local Local onde a disciplina será ministrada
 */
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
//                println("Disciplina: ${disciplina.componenteCurricular}, Código: $codigo -> Dia: $diaSemanaStr, Período: $periodoDiaStr, Horário: $horarioStr")
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
                                    local = disciplina.local,
                                    docente = disciplina.docente
                                )
                            )
                            horarios.add(
                                HorarioSemanal(
                                    diaSemana = dia2,
                                    periodo = periodo,
                                    horario = horarioIntChunk,
                                    disciplina = disciplina.componenteCurricular,
                                    local = disciplina.local,
                                    docente = disciplina.docente
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
                            local = disciplina.local,
                            docente = disciplina.docente
                        )
                    )
                    horarios.add(
                        HorarioSemanal(
                            diaSemana = dia2,
                            periodo = periodo,
                            horario = horarioInt,
                            disciplina = disciplina.componenteCurricular,
                            local = disciplina.local,
                            docente = disciplina.docente
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
                                local = disciplina.local,
                                docente = disciplina.docente
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
                        local = disciplina.local,
                        docente = disciplina.docente
                    )
                )
            }
        }
    }
    return horarios
}

/** Filtra os horários para retornar apenas aqueles que correspondem ao dia atual da semana.
 * Chamada em `getTodayClasses()`
 * @param horarios Lista de horários semanais a serem filtrados
 * @return Lista de horários que ocorrem no dia atual da semana
 */
fun filtrarHorariosDoDiaAtual(horarios: List<HorarioSemanal>): List<HorarioSemanal> {
    val diaAtual = 3 //LocalDate.now().dayOfWeek.value + 1 // 2=segunda
    return horarios.filter { it.diaSemana == diaAtual }
}

/** Obtém as disciplinas que ocorrem hoje a partir de uma lista de disciplinas
 * @param disciplinas Lista de disciplinas a serem processadas
 * @return Lista de horários semanais que ocorrem no dia atual da semana
 */
fun getTodayClasses(disciplinas: List<Disciplina>): List<HorarioSemanal> {
    val horarios = montarHorariosSemanaisDeDisciplinas(disciplinas)
    return filtrarHorariosDoDiaAtual(horarios)

}