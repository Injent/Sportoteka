package ru.master.app.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationRequest(
    val user: Int,
    @SerialName("calendar_sport_info")
    val calendarSportInfo: Long,
    val name: String,
    @SerialName("event_info")
    val eventInfo: String
)
