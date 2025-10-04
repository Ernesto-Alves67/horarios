package com.scherzoVoid.reverseSocial.api.services


import com.scherzoVoid.reverseSocial.api.models.responses.AuthResponse
import com.scherzolambda.horarios.data_transformation.api.models.bodies.RegisterBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Streaming
import retrofit2.http.Url

interface AuthService {

    @POST("api/auth/init")
    suspend fun initAuth(): Response<AuthResponse>

    @POST("api/users")
    fun registerUser(@Body body: RegisterBody): Call<AuthResponse>

    @PUT("api/users")
    suspend fun updateUser(@Body body: RegisterBody): Response<AuthResponse>

    @GET
    @Streaming
    suspend fun downloadApk(@Url apkUrl: String): Response<ResponseBody>

}