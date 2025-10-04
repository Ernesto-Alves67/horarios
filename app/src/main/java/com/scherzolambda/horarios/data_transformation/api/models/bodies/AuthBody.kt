package com.scherzoVoid.reverseSocial.api.models.bodies

data class AuthBody(
    var birthDate: String,
    var cpf: String,
    var email: String,
    var imageUrl: String,
    var name: String,
    var password: String,
    var phone: String,
    var role: String,
    var status: String,
    var type: String
)