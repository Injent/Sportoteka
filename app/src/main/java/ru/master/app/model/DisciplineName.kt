package ru.master.app.model

import kotlinx.serialization.Serializable

@Serializable
data class DisciplineName(
    val name: String,
    val id: Int
)