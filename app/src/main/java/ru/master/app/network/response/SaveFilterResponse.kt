package ru.master.app.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveFilterResponse(
    @SerialName("id")
    val filterId: Int
)
