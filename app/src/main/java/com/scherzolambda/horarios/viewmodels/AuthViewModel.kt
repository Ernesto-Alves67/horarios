package com.scherzolambda.horarios.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.api.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _isSplashVisible = mutableStateOf(true)
    val isSplashVisible: State<Boolean> = _isSplashVisible
    val repository = AuthRepository()

    init {
        initializeApp()
    }
    //TODO: Analisar desempenho do app quando sem acesso a internet na inicialização
    fun initializeApp() {
        viewModelScope.launch {
            try {
                val result = repository.initializeSession()
                if (result.isSuccessful) {
                    result.body()?.let {
                        setAccessToken(it.accessToken)
                    }
                } else {
                    Log.e("AuthViewModel", "Failed to initialize session: ${result.errorBody()?.string()}")
                }
            } catch (e: java.io.IOException) {
                Log.e("AuthViewModel", "Network error initializing session", e)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error initializing session", e)
            } finally {
                _isSplashVisible.value = false // Mesmo em caso de falha, esconde a splash
            }

        }
    }

    private suspend fun setAccessToken(token: String) {
        DataStoreHelper.setAccessToken(token)
    }

}