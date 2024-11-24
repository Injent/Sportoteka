package ru.master.app.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.master.app.model.AgeFilter
import ru.master.app.model.CalendarSportFilter
import ru.master.app.model.CalendarSportTypeFilter
import ru.master.app.model.DisciplineInfoFilter
import ru.master.app.model.ProgramInfoFilter
import ru.master.app.model.SexFilter
import ru.master.app.model.TeamFilter

@Serializable
data class FiltersResponse(
    @SerialName("ageCategory")
    val ageFilter: List<AgeFilter>,
    @SerialName("calendarSport")
    val calendarSportFilter: List<CalendarSportFilter>,
    @SerialName("calendarSportType")
    val calendarSportTypeFilter: List<CalendarSportTypeFilter>,
    @SerialName("disciplineInfo")
    val disciplineInfoFilter: List<DisciplineInfoFilter>,
    @SerialName("programInfo")
    val programInfoFilter: List<ProgramInfoFilter>,
    @SerialName("sexCategory")
    val sexFilter: List<SexFilter>,
    @SerialName("teamInfo")
    val teamFilter: List<TeamFilter>
) {
    val associatedDisciplines = associateDisciplinesWithSubDisciplines()

    private fun associateDisciplinesWithSubDisciplines(): Map<ProgramInfoFilter, List<DisciplineInfoFilter>> {
        val children = mutableListOf<DisciplineInfoFilter>()

        disciplineInfoFilter.forEach { item ->
            if (item.parentId != null) {
                children.add(DisciplineInfoFilter(item.id, item.name, item.parentId))
            }
        }

        return programInfoFilter.associateWith { parent ->
            children.filter { it.parentId == parent.id }
        }.also { println(it) }
    }
}