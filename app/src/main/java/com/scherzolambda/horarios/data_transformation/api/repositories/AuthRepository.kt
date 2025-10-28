package com.scherzolambda.horarios.data_transformation.api.repositories

import com.scherzoVoid.reverseSocial.api.services.AuthService
import com.scherzolambda.horarios.data_transformation.api.RetrofitClient
import com.scherzolambda.horarios.data_transformation.api.models.bodies.RegisterBody
import com.scherzolambda.horarios.data_transformation.api.models.responses.AuthResponse
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor() : IAuthRepository {

    private val authService = RetrofitClient.getRetrofitInstance().create(AuthService::class.java)

    override suspend fun initializeSession(): Response<AuthResponse> = authService.initAuth()

    fun saveUserData(body: RegisterBody) = authService.registerUser(body)

    suspend fun updateUserData(body: RegisterBody) = authService.updateUser(body)

    suspend fun downloadApk(apkUrl: String) = authService.downloadApk(apkUrl)
}