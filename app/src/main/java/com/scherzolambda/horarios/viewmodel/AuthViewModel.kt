package com.scherzolambda.horarios.viewmodel

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
    fun initializeApp() {
        viewModelScope.launch {
            try {

                val result = repository.initializeSession()
                if (result.isSuccessful) {
                    result.body()?.let {
                        setAccessToken(it.accessToken)
                    }
                    _isSplashVisible.value = false // Aqui esconde a splash screen
                } else {
                    Log.e("AuthViewModel", "Failed to initialize session: ${result.errorBody()?.string()}")
                    _isSplashVisible.value = false // Mesmo em caso de falha, esconde a splash
                }
            }catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during session initialization", e)
                _isSplashVisible.value = false // Mesmo em caso de exceção, esconde a splash
            }

        }
    }

    private suspend fun setAccessToken(token: String) {
        DataStoreHelper.setAccessToken(token)
    }

}