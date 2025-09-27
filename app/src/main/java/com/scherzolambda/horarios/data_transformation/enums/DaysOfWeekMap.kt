package com.scherzolambda.horarios.data_transformation.enums

/**
 * Mapeamento dos dias da semana para facilitar uso em horários, tabelas, etc.
 * O valor pode ser adaptado para abreviações, nomes completos ou outros idiomas.
 */
object DaysOfWeekMap {
    val days: Map<Int, String> = mapOf(
        1 to "Segunda",
        2 to "Terça",
        3 to "Quarta",
        4 to "Quinta",
        5 to "Sexta",
        6 to "Sábado",
        7 to "Domingo"
    )

    /**
     * Retorna o nome do dia da semana pelo índice (1 = Segunda, 7 = Domingo)
     */
    fun getDay(index: Int): String? = days[index]
}

