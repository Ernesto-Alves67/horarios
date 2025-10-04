package com.scherzoVoid.reverseSocial.api.models.responses

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String
)