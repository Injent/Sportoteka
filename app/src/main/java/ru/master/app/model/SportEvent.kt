package ru.master.app.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SportEvent(
    val id: Long,
    val sportName: String,
    val eventName: String,
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val description: String?,
    val location: String,
    val memberCount: Int,
    val teamName: String?,
    val programs: List<String>,
    val disciplines: List<String>,
    val performer: String?,
    val ekp: String
)