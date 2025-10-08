package com.scherzolambda.horarios.data_transformation.api


//import com.scherzolambda.horarios.BuildConfig
import com.scherzolambda.horarios.Constants
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.EnvConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {

    companion object {
        private lateinit var retrofit: Retrofit
        private var accessToken: String? = getAccessTokenSync()
        private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        init {
            coroutineScope.launch {
                DataStoreHelper.getAccessTokenFlow().collect { token ->
                    accessToken = token
                }
            }
        }

        fun getRetrofitInstance(): Retrofit {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                        .header("Content-Type", "application/json")
                        .header("X-API-Key", EnvConfig.get("API_SECRET_KEY"))
                        .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36")

                    // Adiciona o token ao header, se disponível
                    accessToken?.let { token ->
                        requestBuilder.header("Authorization", "Bearer $token")
                    }

                    chain.proceed(requestBuilder.build())
                }
                .build()

            if (!::retrofit.isInitialized) {
                retrofit = Retrofit.Builder()
                    .baseUrl(EnvConfig.get("BASE_URL"))
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }


        fun getAccessTokenSync(): String? = runBlocking {
            DataStoreHelper.getAccessTokenFlow().firstOrNull()
        }
    }
}