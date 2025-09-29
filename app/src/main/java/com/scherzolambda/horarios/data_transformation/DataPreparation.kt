package com.scherzolambda.horarios.data_transformation

import android.util.Log

class DataPreparation {

    fun decomporMultiplosCodigos(texto: String): List<Triple<String, String, String>> {
        // A expressão com 3 grupos de captura: (Dígitos)(Letra)(Dígitos)
        val regexPadrao = Regex("""(\d+)([A-Z])(\d+)""")

//        println("Testando a string: \"$texto\"")

        val matches = regexPadrao.findAll(texto)
        val resultados = mutableListOf<Triple<String, String, String>>()
        matches.forEach { match ->
            val diaSemana = match.groupValues[1]
            val periodoDia = match.groupValues[2]
            val horario = match.groupValues[3]

            Log.d("DataPreparation", "Código completo encontrado: ${match.groupValues[0]}")
            resultados.add(Triple(diaSemana, periodoDia, horario))
        }
        Log.d("DataPreparation", "Encontradas ${resultados.size} correspondências na string: \"$texto\"")
        return resultados
    }
}