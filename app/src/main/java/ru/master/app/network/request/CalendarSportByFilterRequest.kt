package ru.master.app.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.master.app.data.settings.Filter

@Serializable
data class CalendarSportByFilterRequest (
    @SerialName("calendarSportId"     ) var calendarSportId     : List<Int>,
    @SerialName("disciplineId"        ) var disciplineId        : List<Int>,
    @SerialName("programId"           ) var programId           : List<Int>,
    @SerialName("location"            ) var location            : String?  ,
    @SerialName("minCount"            ) var minCount            : Int?     ,
    @SerialName("maxCount"            ) var maxCount            : Int?     ,
    @SerialName("ageId"               ) var ageId               : List<Int>,
    @SerialName("sexId"               ) var sexId               : List<Int>,
    @SerialName("dateFrom"            ) var dateFrom            : String?  ,
    @SerialName("dateTo"              ) var dateTo              : String?  ,
    @SerialName("calendarSportTypeId" ) var calendarSportTypeId : List<Int>,
    @SerialName("page"                ) var page                : Int?     ,
    @SerialName("size"                ) var size                : Int?     ,
)

fun List<Filter>.toCalendarSportRequest(page: Int, pageSize: Int): CalendarSportByFilterRequest {
    return CalendarSportByFilterRequest(
        calendarSportId = filterIsInstance<Filter.Sport>().map(Filter.Sport::id),
        disciplineId = filterIsInstance<Filter.Discipline>().map(Filter.Discipline::id),
        programId = filterIsInstance<Filter.Program>().map(Filter.Program::id),
        location = null,
        minCount = filterIsInstance<Filter.MemberRange>().firstOrNull()?.start,
        maxCount = filterIsInstance<Filter.MemberRange>().firstOrNull()?.end,
        ageId = filterIsInstance<Filter.Age>().map(Filter.Age::id),
        sexId = filterIsInstance<Filter.Sex>().map(Filter.Sex::id),
        dateFrom = filterIsInstance<Filter.Date>().firstOrNull()?.startDate?.toString(),
        dateTo = filterIsInstance<Filter.Date>().firstOrNull()?.endDate?.toString(),
        calendarSportTypeId = filterIsInstance<Filter.SportType>().map(Filter.SportType::id),
        page = page,
        size = pageSize
    )
}