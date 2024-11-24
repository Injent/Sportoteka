package ru.master.app.network.response

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import ru.master.app.model.SportEvent

@Serializable
data class CalendarSportInfoResponse(
    val data: List<Item>
) {
    @Serializable
    data class Item(
        val id: Long,
        val calendarSportName: String?,
        val calendarSportTypeName: String?,
        val dateFrom: Instant,
        val dateTo: Instant,
        val description: String?,
        val location: String,
        val teamName: String,
        val programInfoList: List<Value>,
        val disciplineInfoList: List<Value>,
        val performer: String? = null,
        val count: Int,
        val ekp: String
    )

    @Serializable
    data class Value(
        val id: Int,
        val name: String
    )
}

fun CalendarSportInfoResponse.Item.toSportEvent() = SportEvent(
    id = id,
    eventName = calendarSportTypeName?.removeNextLines() ?: "Без имени",
    sportName = calendarSportName?.removeNextLines() ?: "Без имени",
    dateTo = dateTo.plus(3, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault()).date,
    dateFrom = dateFrom.plus(3, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault()).date,
    location = location.removeNextLines(),
    description = description?.removeNextLines(),
    programs = programInfoList.map { it.name.removeNextLines() },
    disciplines = disciplineInfoList.map { it.name.removeNextLines() },
    teamName = teamName.removeNextLines(),
    performer = performer?.removeNextLines(),
    memberCount = count,
    ekp = ekp
)

private fun String.removeNextLines(): String {
    return replace("\n", " ").trim()
}