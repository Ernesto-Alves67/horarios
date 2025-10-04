package com.scherzolambda.horarios.data_transformation
import android.util.Log
import org.jsoup.Jsoup
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


data class Disciplina(
    val codigo: String,
    val componenteCurricular: String,
    val docente: String = "",
    val turma: String = "",
    val status: String = "",
    val horario: String = "",
    val local: String = ""
)

@Serializable
data class DisciplinaSerializable(
    val codigo: String,
    val componenteCurricular: String,
    val docente: String = "",
    val turma: String = "",
    val status: String = "",
    val horario: String = "",
    val local: String = ""
)

data class Identificacao(
    val periodoLetivo: String = "",
    val matricula: String = "",
    val nome: String = "",
    val curso: String = "",
    val formacao: String = ""
)

class FileProcessor {
    /**
     * Extrai todas as tabelas de um arquivo HTML específico.
     * @param filePath O caminho para o arquivo HTML.
     * @return Pair contendo os dados de identificação e uma lista de listas de Disciplinas.
     */
    fun extrairTabelasDeHtml(filePath: String): Pair<Identificacao?, List<List<Disciplina>>> {
        val tabelasExtraidas = mutableListOf<List<Disciplina>>()
        var identificacao: Identificacao? = null

        // 1. Carregar o documento HTML
        val doc = try {
            Jsoup.parse(File(filePath), null)
        } catch (e: Exception) {
            println("Erro ao carregar ou analisar o arquivo HTML: ${e.message}")
            return Pair(null, emptyList())
        }

        // 2. Encontrar todas as tags <table>
        val tables = doc.select("table")

        if (tables.isEmpty()) {
            println("Nenhuma tabela encontrada no documento HTML.")
            return Pair(null, emptyList())
        }

        tables.forEachIndexed { tableIndex, table ->
            val disciplinasDaTabela = mutableListOf<Disciplina>()
            val rows = table.select("tbody tr")
            if (!(tableIndex == 0 || tableIndex == 2)) {
                // ...processamento das disciplinas...
                rows.forEach { row ->
                    val cells = row.select("td")
                    try {
                        val componenteCurricular = cells[1].select("span.componente").text()
                        val localRaw = cells[1].select("span.local").text()
                        val local = localRaw.replaceFirst(Regex("(?i)\\s*Local\\s*:\\s*"), "").trim()
                        val docente = cells[1].select("span.docente").text()
                        val horarioBruto = cells[4].text()
                        val horarioLimpo = horarioBruto.replace(Regex("\\s*\\(.*?\\)"), "")
                        Log.d("FileProcessor", " Horário limpo: '$local | $docente'")
                        val disciplina = Disciplina(
                            codigo = cells[0].text(),
                            componenteCurricular = componenteCurricular,
                            docente = docente,
                            turma = cells[2].text(),
                            status = cells[3].text(),
                            horario = horarioLimpo,
                            local = local
                        )
                        disciplinasDaTabela.add(disciplina)
                    } catch (e: Exception) {
                        println("Erro ao processar linha da Tabela ${tableIndex + 1}: ${e.message}")
                    }
                }
            } else {
                // Processa tabela de identificação
                if (table.id() == "identificacao") {
                    val map = mutableMapOf<String, String>()
                    rows.forEach { row ->
                        val cells = row.select("td")
                        if (cells.size >= 2) {
                            val key = cells[0].text().replace(":", "").trim()
                            val value = cells[1].select("strong").text().ifEmpty { cells[1].text() }.trim()
                            map[key] = value
                        }
                        if (cells.size >= 4) {
                            val key2 = cells[2].text().replace(":", "").trim()
                            val value2 = cells[3].select("strong").text().ifEmpty { cells[3].text() }.trim()
                            map[key2] = value2
                        }
                    }
                    identificacao = Identificacao(
                        periodoLetivo = map["Período Letivo"] ?: "",
                        matricula = map["Matrícula"] ?: "",
                        nome = map["Nome"] ?: "",
                        curso = map["Curso"] ?: "",
                        formacao = map["Formação"] ?: ""
                    )
                }
            }
            tabelasExtraidas.add(disciplinasDaTabela)
        }
        Log.d("DisciplinaViewModel", "Disciplinas extraídas: ${identificacao}" )
        return Pair(identificacao, tabelasExtraidas)
    }
}

fun salvarDisciplinasLocal(context: android.content.Context, disciplinas: List<Disciplina>, fileName: String = "disciplinas.json"): Boolean {
    return try {
        val serializaveis = disciplinas.map {
            DisciplinaSerializable(
                codigo = it.codigo,
                componenteCurricular = it.componenteCurricular,
                docente = it.docente,
                turma = it.turma,
                status = it.status,
                horario = it.horario,
                local = it.local,
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
                docente = it.docente,
                turma = it.turma,
                status = it.status,
                horario = it.horario,
                local = it.local
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
