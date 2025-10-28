package com.scherzolambda.horarios.data_transformation.api.repositories

import com.scherzolambda.horarios.data_transformation.api.models.responses.AuthResponse
import retrofit2.Response

interface IAuthRepository {
    suspend fun initializeSession(): Response<AuthResponse>
}

