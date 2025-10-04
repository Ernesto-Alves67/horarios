package com.scherzolambda.horarios.data_transformation.api.repositories

import com.scherzoVoid.reverseSocial.api.services.AuthService
import com.scherzolambda.horarios.data_transformation.api.RetrofitClient
import com.scherzolambda.horarios.data_transformation.api.models.bodies.RegisterBody

class AuthRepository {

    private val authService = RetrofitClient.getRetrofitInstance().create(AuthService::class.java)

    suspend fun initializeSession() = authService.initAuth()
    fun saveUserData(body: RegisterBody) = authService.registerUser(body)

    suspend fun updateUserData(body: RegisterBody) = authService.updateUser(body)

    suspend fun downloadApk(apkUrl: String) = authService.downloadApk(apkUrl)
}