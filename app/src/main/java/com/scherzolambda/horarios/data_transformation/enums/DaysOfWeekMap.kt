package com.scherzolambda.horarios.data_transformation.enums

/**
 * Mapeamento dos dias da semana para facilitar uso em horários, tabelas, etc.
 * O valor pode ser adaptado para abreviações, nomes completos ou outros idiomas.
 */
object DaysOfWeekMap {
    val days: Map<Int, String> = mapOf(
        2 to "Segunda",
        3 to "Terça",
        4 to "Quarta",
        5 to "Quinta",
        6 to "Sexta",
        7 to "Sábado",
        8 to "Domingo"
    )

    /**
     * Retorna o nome do dia da semana pelo índice (1 = Segunda, 7 = Domingo)
     */
    fun getDay(index: Int): String? = days[index]
}

