package com.scherzolambda.horarios.data_transformation.api.services

import com.scherzolambda.horarios.data_transformation.api.models.GitHubRelease
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import javax.inject.Inject

interface UpdateService {
    @GET("repos/Ernesto-Alves67/horarios/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease
}


class GitHubService @Inject constructor() {
    val api: UpdateService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpdateService::class.java)
    }
}


