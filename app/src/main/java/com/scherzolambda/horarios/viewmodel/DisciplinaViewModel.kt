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
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.montarHorariosSemanaisDeDisciplinas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class DisciplinaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileProcessor: FileProcessor
) : AndroidViewModel(context as Application) {
    private val _disciplinas = MutableStateFlow<List<Disciplina>>(emptyList())
    val disciplinas: StateFlow<List<Disciplina>> = _disciplinas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Cache do horário semanal para evitar recálculos
    val weeklySchedule: StateFlow<List<HorarioSemanal>> = _disciplinas
        .map { disciplinas -> montarHorariosSemanaisDeDisciplinas(disciplinas) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        carregarDisciplinasLocal()
    }

    fun carregarDisciplinasLocal() {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                val disciplinas = com.scherzolambda.horarios.data_transformation.lerDisciplinasLocal(context)
                _disciplinas.value = disciplinas
            }
            _isLoading.value = false
        }
    }

    fun salvarDisciplinasLocal(novas: List<Disciplina>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                com.scherzolambda.horarios.data_transformation.salvarDisciplinasLocal(context, novas)
            }
            _disciplinas.value = novas
        }
    }

    fun carregarDeArquivoHtml(filePath: String) {
        Log.d("DisciplinaViewModel", "Carregando disciplinas do arquivo: $filePath")
        viewModelScope.launch {
            _isLoading.value = true
            _disciplinas.value = emptyList() // Limpa a lista antes de carregar novas disciplinas
            val disciplinas = withContext(Dispatchers.IO) {
                val tabelas = fileProcessor.extrairTabelasDeHtml(filePath)
                // Supondo que a tabela principal é a primeira não vazia
                tabelas.firstOrNull { it.isNotEmpty() } ?: emptyList()
            }
            _disciplinas.value = disciplinas
            salvarDisciplinasLocal(disciplinas)
            _isLoading.value = false
        }
    }
}