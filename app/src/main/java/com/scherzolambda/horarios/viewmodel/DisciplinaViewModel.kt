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
import com.scherzolambda.horarios.data_transformation.api.models.responses.AuthResponse
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.Identificacao
import com.scherzolambda.horarios.data_transformation.api.models.bodies.RegisterBody
import com.scherzolambda.horarios.data_transformation.api.repositories.AuthRepository
import com.scherzolambda.horarios.data_transformation.getTodayClasses2
import com.scherzolambda.horarios.data_transformation.models.HorarioSemanal
import com.scherzolambda.horarios.data_transformation.montarHorariosSemanaisDeDisciplinas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@HiltViewModel
class DisciplinaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileProcessor: FileProcessor
) : AndroidViewModel(context as Application) {
    private val _disciplinas = MutableStateFlow<List<Disciplina>>(emptyList())
    val disciplinas: StateFlow<List<Disciplina>> = _disciplinas
    private val repository = AuthRepository()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val weeklySchedule: StateFlow<List<HorarioSemanal>> = _disciplinas
        .map { disciplinas -> montarHorariosSemanaisDeDisciplinas(disciplinas) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todaysSchedule: StateFlow<List<HorarioSemanal>> = weeklySchedule
        .map { schedule ->
            getTodayClasses2(schedule)
        }
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
//        Log.d("DisciplinaViewModel", "Carregando disciplinas do arquivo: $filePath")
        val isFistAccess = DataStoreHelper.isFirstAccessFlow().map { it }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            true
        )
        viewModelScope.launch {
            _isLoading.value = true
            _disciplinas.value = emptyList()
            var userData: Pair<Identificacao?, List<List<Disciplina>>>
            withContext(Dispatchers.IO){
                userData = fileProcessor.extrairTabelasDeHtml(filePath)
            }
            val disciplinas = userData.second.firstOrNull { it.isNotEmpty() } ?: emptyList()
            val identificacao = userData.first

            val isFirstAccess = DataStoreHelper.isFirstAccessFlow().first()
//            Log.d("DisciplinaViewModel", "Identificação extraída: $isFirstAccess")
            when (isFirstAccess) {
                true -> {
                    saveUserData(identificacao)
                    DataStoreHelper.setFirstAccess(false)
                }
                false -> updateUserData(identificacao)
            }
            _disciplinas.value = disciplinas
            DataStoreHelper.setFileLoaded(true)
            salvarDisciplinasLocal(disciplinas)
            _isLoading.value = false
        }
    }

    fun saveUserData(user: Identificacao?) {
        val userData = RegisterBody(
            matricula = user?.matricula ?: "",
            nome = user?.nome ?: "",
            curso = user?.curso ?: "",
            formacao = user?.formacao ?: "",
            periodoLetivo = user?.periodoLetivo ?: "",
            appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "",
            osVersion = android.os.Build.VERSION.RELEASE,
            deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
        )
        viewModelScope.launch {
            val result = repository.saveUserData(userData)
            result.enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        Log.i("disciplinaVM", "User data saved successfully")
                    } else {
                        Log.e("DisciplinaVM", "Failed to save user data: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("DisciplinaVM", "Error saving user data ${t.message}", t)
                }
            })
        }
    }

    fun updateUserData(user: Identificacao?) {
        val userData = RegisterBody(
            matricula = user?.matricula ?: "",
            nome = user?.nome ?: "",
            curso = user?.curso ?: "",
            formacao = user?.formacao ?: "",
            periodoLetivo = user?.periodoLetivo ?: "",
            appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "",
            osVersion = android.os.Build.VERSION.RELEASE,
            deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
        )
        viewModelScope.launch {
            val result = repository.updateUserData(userData)
            if(result.isSuccessful) {
                Log.i("disciplinaVM", "User data Updated successfully")
            } else {
                Log.e("DisciplinaVM", "Failed to Update user data: ${result.errorBody()?.string()}")
            }
        }
    }

}