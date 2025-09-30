package com.scherzolambda.horarios.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scherzolambda.horarios.data_transformation.FileProcessor
import com.scherzolambda.horarios.data_transformation.Disciplina
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import android.util.Log
import com.scherzolambda.horarios.data_transformation.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.montarHorariosSemanaisDeDisciplinas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DisciplinaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileProcessor: FileProcessor
) : AndroidViewModel(context as Application) {
    private val _disciplinas = MutableStateFlow<List<Disciplina>>(emptyList())
    val disciplinas: StateFlow<List<Disciplina>> = _disciplinas

    init {
        carregarDisciplinasLocal()
    }

    fun carregarDisciplinasLocal() {
        viewModelScope.launch {
            val disciplinas = com.scherzolambda.horarios.data_transformation.lerDisciplinasLocal(context)
            _disciplinas.value = disciplinas
        }
    }

    fun salvarDisciplinasLocal(novas: List<Disciplina>) {
        viewModelScope.launch {
            com.scherzolambda.horarios.data_transformation.salvarDisciplinasLocal(context, novas)
            _disciplinas.value = novas
        }
    }

    fun carregarDeArquivoHtml(filePath: String) {
        Log.d("DisciplinaViewModel", "Carregando disciplinas do arquivo: $filePath")
        viewModelScope.launch {
            _disciplinas.value = emptyList() // Limpa a lista antes de carregar novas disciplinas
            val tabelas = fileProcessor.extrairTabelasDeHtml(filePath)
            // Supondo que a tabela principal é a primeira não vazia
            val disciplinas = tabelas.firstOrNull { it.isNotEmpty() } ?: emptyList()
            _disciplinas.value = disciplinas
            salvarDisciplinasLocal(disciplinas)
        }
    }

    fun getDisciplinasHoje(){
        // Implementar lógica para filtrar disciplinas do dia atual

    }

    fun getWeeklySchedule(): List<HorarioSemanal>{
        return montarHorariosSemanaisDeDisciplinas(_disciplinas.value)
    }
}
