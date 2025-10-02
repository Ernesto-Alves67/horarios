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