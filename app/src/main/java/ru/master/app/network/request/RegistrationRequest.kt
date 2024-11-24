package ru.master.app.network.request

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val login: String,
    val password: String,
    val idRole: Int = 2,
    val tgChat: String = "sorry",
    val email: String = login,
)
