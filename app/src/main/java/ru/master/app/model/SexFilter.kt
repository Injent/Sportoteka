package ru.master.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SexFilter(
    @SerialName("id")
    override val id: Int,
    @SerialName("sex")
    override val name: String,
) : IdBasedFilter