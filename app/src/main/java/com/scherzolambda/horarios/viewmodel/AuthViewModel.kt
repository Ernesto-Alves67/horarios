package com.scherzolambda.horarios.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.api.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    val repository = AuthRepository()

    fun initializeApp() {
        viewModelScope.launch {
            val result = repository.initializeSession()
            if (result.isSuccessful) {
                //TODO : dados do dispositivo
                result.body()?.let {
                    setAccessToken(it.accessToken)
                }
            } else {
                Log.e("AuthViewModel", "Failed to initialize session: ${result.errorBody()?.string()}")
            }

        }
    }

    private suspend fun setAccessToken(token: String) {
        DataStoreHelper.setAccessToken(token)
    }

}