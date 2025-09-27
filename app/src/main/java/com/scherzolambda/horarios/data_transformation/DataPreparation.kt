package com.scherzolambda.horarios.data_transformation

class DataPreparation {

    fun decomporMultiplosCodigos(texto: String): Triple<String, String, String> {
        // A expressão com 3 grupos de captura: (Dígitos)(Letra)(Dígitos)
        val regexPadrao = Regex("""(\d+)([A-Z])(\d+)""")

        println("Testando a string: \"$texto\"")
        println("-".repeat(30))

        val matches = regexPadrao.findAll(texto)

        if (matches.any()) {
            matches.forEachIndexed { index, match ->
                // match.groupValues[0] é o código completo (ex: "3T45")
                // match.groupValues[1], [2], [3] são as partes.

                val codigo_completo = match.groupValues[0]
                val diaSemana = match.groupValues[1]
                val periodoDia = match.groupValues[2]
                val horario = match.groupValues[3]

                return Triple<String,String,String>(diaSemana, periodoDia, horario)
            }
        } else {
            println("❌ Nenhuma correspondência de código encontrada.")
        }
        return Triple<String,String,String>("","","")
    }
}