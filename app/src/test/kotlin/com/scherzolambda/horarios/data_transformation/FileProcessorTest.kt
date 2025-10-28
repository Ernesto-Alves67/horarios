package com.scherzolambda.horarios.data_transformation

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class FileProcessorTest {

    @Test
    fun `extrair tabelas de html deve retornar identificacao e disciplinas`() {
        val resource = javaClass.getResource("/fixtures/identificacao_e_disciplinas.html")
        assertNotNull("Fixture não encontrada", resource)
        val file = File(resource!!.toURI())
        val processor = FileProcessor()

        val (identificacao, tabelas) = processor.extrairTabelasDeHtml(file.absolutePath)

        assertNotNull("Identificação não deveria ser nula", identificacao)
        assertEquals("2025.2", identificacao?.periodoLetivo)
        assertEquals("202000103", identificacao?.matricula)
        assertTrue(identificacao?.nome?.contains("ERNESTO ALVES") == true)

        // Esperamos 2 tabelas no fixture: identificacao e disciplinas
        assertEquals(2, tabelas.size)

        val disciplinasTabela = tabelas[1]
        assertEquals(1, disciplinasTabela.size)
        val primeira = disciplinasTabela[0]
        assertEquals("CSP101", primeira.codigo)
        assertEquals("Introdução à Programação", primeira.componenteCurricular.trim())
        assertEquals("Prof. Fulano", primeira.docente.trim())
        assertEquals("A", primeira.turma.trim())
        assertEquals("ATIVA", primeira.status.trim())
        // horário no fixture contem '(seg-sex)' — comparamos somente a parte da hora e usamos trim
        assertEquals("08:00", primeira.horario.trim().split(" ")[0])
        assertEquals("Sala 1", primeira.local.trim())
    }
}
