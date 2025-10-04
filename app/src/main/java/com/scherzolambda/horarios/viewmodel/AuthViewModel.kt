package com.scherzolambda.horarios.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scherzoVoid.reverseSocial.api.models.responses.AuthResponse
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.api.RetrofitClient
import com.scherzolambda.horarios.data_transformation.api.models.bodies.RegisterBody
import com.scherzolambda.horarios.data_transformation.api.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    val repository = AuthRepository()

    fun initializeApp() {
        viewModelScope.launch {
            val result = repository.initializeSession()
            if (result.isSuccessful) {
                Log.i("AuthViewModel", "Session initialized successfully ${result.body()}")
                result.body()?.let {
                    setAccessToken(it.accessToken)
                }
            } else {
                Log.e("AuthViewModel", "Failed to initialize session: ${result.errorBody()?.string()}")
            }
//            result.enqueue(object : Callback<AuthResponse> {
//                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
//                    if (response.isSuccessful) {
//                        Log.i("AuthViewModel", "Session initialized successfully ${response.body()}")
//                        viewModelScope.launch {
//                            setAccessToken(response.body()!!.accessToken)
//                        }
//                    } else {
//                        Log.e("AuthViewModel", "Failed to initialize session: ${response.errorBody()?.string()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                    Log.e("AuthViewModel", "Error initializing session ${t.message}", t)
//                }
//            })
        }
    }

    private suspend fun setAccessToken(token: String) {
        DataStoreHelper.setAccessToken(token)
    }

    fun saveUserData(user: RegisterBody) {
        viewModelScope.launch {
            val result = repository.saveUserData(user)
            result.enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        Log.i("AuthViewModel", "User data saved successfully")
                    } else {
                        Log.e("AuthViewModel", "Failed to save user data: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("AuthViewModel", "Error saving user data ${t.message}", t)
                }
            })
        }
    }
}