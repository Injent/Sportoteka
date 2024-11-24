package ru.master.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisciplineInfoFilter(
    @SerialName("id")
    override val id: Int,
    @SerialName("name")
    override val name: String,
    @SerialName("parentId")
    val parentId: Int? = null
) : IdBasedFilter