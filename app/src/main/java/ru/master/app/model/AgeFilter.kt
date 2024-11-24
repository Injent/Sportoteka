package ru.master.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgeFilter(
    @SerialName("id")
    override val id: Int,
    @SerialName("age")
    override val name: String
) : IdBasedFilter
