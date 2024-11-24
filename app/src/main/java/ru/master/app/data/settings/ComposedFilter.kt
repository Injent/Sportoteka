package ru.master.app.data.settings

import kotlinx.serialization.Serializable

@Serializable
data class ComposedFilter(
    val id: Int? = null,
    val name: String,
    val filters: List<Filter>
)