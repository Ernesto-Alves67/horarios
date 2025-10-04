package com.scherzolambda.horarios.data_transformation.api.models.bodies

import com.google.gson.annotations.SerializedName

data class RegisterBody(
    @SerializedName("matricula")
    val matricula: String,
    @SerializedName("periodo_letivo")
    val periodoLetivo: String,
   @SerializedName("nome")
    val nome: String,
    @SerializedName("curso")
    val curso: String,
    @SerializedName("formacao")
    val formacao: String,
    @SerializedName("app_version")
    val appVersion: String,
    @SerializedName("os_version")
    val osVersion: String,
    @SerializedName("device_name")
    val deviceName: String
)