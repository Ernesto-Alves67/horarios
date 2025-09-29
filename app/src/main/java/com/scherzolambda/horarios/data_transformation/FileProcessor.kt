package com.scherzolambda.horarios.data_transformation
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


data class Disciplina(
    val codigo: String,
    val componenteCurricular: String,
    val turma: String = "",
    val status: String = "",
    val horario: String = ""
)

@Serializable
data class DisciplinaSerializable(
    val codigo: String,
    val componenteCurricular: String,
    val turma: String = "",
    val status: String = "",
    val horario: String = ""
)

class FileProcessor {
    /**
     * Extrai todas as tabelas de um arquivo HTML específico.
     * @param filePath O caminho para o arquivo HTML.
     * @return Uma lista de listas de Disciplinas (um conjunto de disciplinas para cada tabela).
     */
    fun extrairTabelasDeHtml(filePath: String): List<List<Disciplina>> {
        val tabelasExtraidas = mutableListOf<List<Disciplina>>()

        // 1. Carregar o documento HTML
        val doc = try {
            Jsoup.parse(File(filePath), "UTF-8")
        } catch (e: Exception) {
            println("Erro ao carregar ou analisar o arquivo HTML: ${e.message}")
            return emptyList()
        }

        // 2. Encontrar todas as tags <table>
        val tables = doc.select("table")

        if (tables.isEmpty()) {
            println("Nenhuma tabela encontrada no documento HTML.")
            return emptyList()
        }


        tables.forEachIndexed { tableIndex, table ->
            val disciplinasDaTabela = mutableListOf<Disciplina>()
            val rows = table.select("tbody tr")
//            println("Processando Tabela ${tableIndex + 1} com ${rows} linhas.\n")
            rows.forEach { row ->

                val cells = row.select("td")
//                Log.d("FileProcessor", "Tabela ${tableIndex + 1}, Linha: ${row.text()}, Células: ${cells.size}")
                if ( !(tableIndex == 0 || tableIndex == 2)) {
                    try {
                        val componenteCurricular = cells[1].select("span.componente").text()
//                        println("  - Componente Curricular: $componenteCurricular")
                        val disciplina = Disciplina(
                            // Mapeamento baseado na ordem das colunas:
                            codigo = cells[0].text(),
                            componenteCurricular = componenteCurricular,
                            turma = cells[2].text(),
                            status = cells[3].text(),
                            horario = cells[4].text()
                        )
                        disciplinasDaTabela.add(disciplina)
                    } catch (e: Exception) {
                        println("Erro ao processar linha da Tabela ${tableIndex + 1}: ${e.message}")
                    }
                }
            }
            tabelasExtraidas.add(disciplinasDaTabela)
        }

        return tabelasExtraidas
    }
}

fun salvarDisciplinasLocal(context: android.content.Context, disciplinas: List<Disciplina>, fileName: String = "disciplinas.json"): Boolean {
    return try {
        val serializaveis = disciplinas.map {
            DisciplinaSerializable(
                codigo = it.codigo,
                componenteCurricular = it.componenteCurricular,
                turma = it.turma,
                status = it.status,
                horario = it.horario
            )
        }
        val json = Json.encodeToString(serializaveis)
        val file = File(context.filesDir, fileName)
        file.writeText(json)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun lerDisciplinasLocal(context: android.content.Context, fileName: String = "disciplinas.json"): List<Disciplina> {
    return try {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val serializaveis = Json.decodeFromString<List<DisciplinaSerializable>>(json)
        serializaveis.map {
            Disciplina(
                codigo = it.codigo,
                componenteCurricular = it.componenteCurricular,
                turma = it.turma,
                status = it.status,
                horario = it.horario
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
