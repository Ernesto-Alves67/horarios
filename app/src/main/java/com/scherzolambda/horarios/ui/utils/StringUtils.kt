package com.scherzolambda.horarios.ui.utils

/**
 * Gera um acrônimo a partir de um texto, ignorando palavras curtas/preposições comuns.
 * * @param texto O texto de entrada.
 * @return O acrônimo gerado.
 */
fun gerarAcronimo(texto: String): String {
    if (texto.isBlank()) {
        return ""
    }

    // Lista de palavras (preposições, artigos, conjunções) para ignorar
    val palavrasAIgnorar = setOf("de", "da", "do", "das", "dos", "com", "e", "para", "a", "o", "as", "os", "em", "um", "uma")

    return texto.split(Regex("\\s+")) // Divide a string por espaços
        .filter { it.isNotBlank() } // Remove strings vazias
        .filter { palavra -> // Filtra (ignora) palavras curtas/preposições
            palavra.lowercase() !in palavrasAIgnorar && palavra.length > 1
        }
        .map { it.first().uppercaseChar() } // Pega a 1ª letra e transforma em maiúscula
        .joinToString("") // Junta as letras
}

/**
 * Compara esta string de versão com outra string de versão,
 * tratando cada segmento numericamente.
 */
fun String.compareVersionsSimple(other: String): Int {
    // Quebra as versões em listas de strings
    val thisSegments = this.split('.')
    val otherSegments = other.split('.')

    // Determina o máximo de segmentos para iterar
    val maxLen = maxOf(thisSegments.size, otherSegments.size)

    for (i in 0 until maxLen) {
        // Pega o valor do segmento, usando '0' se não houver segmento (versão mais curta)
        val thisValue = thisSegments.getOrElse(i) { "0" }.toIntOrNull() ?: 0
        val otherValue = otherSegments.getOrElse(i) { "0" }.toIntOrNull() ?: 0

        if (thisValue != otherValue) {
            // -1 se thisValue < otherValue
            //  1 se thisValue > otherValue
            return thisValue.compareTo(otherValue)
        }
    }

    // as versões são iguais
    return 0
}