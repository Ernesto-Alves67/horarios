package com.scherzolambda.horarios.data_transformation.api.models.responses

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String
)