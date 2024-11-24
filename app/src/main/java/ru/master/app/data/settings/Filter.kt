package ru.master.app.data.settings

import javatimefun.localdate.extensions.print
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.master.app.model.AgeFilter
import ru.master.app.model.CalendarSportFilter
import ru.master.app.model.CalendarSportTypeFilter
import ru.master.app.model.DisciplineInfoFilter
import ru.master.app.model.IdBasedFilter
import ru.master.app.model.ProgramInfoFilter
import ru.master.app.model.SexFilter
import ru.master.app.model.TeamFilter

enum class FilterType {
    DATE,
    MEMBER_RANGE,
    SEX,
    TEAM,
    AGE,
    CALENDAR_SPORT,
    CALENDAR_SPORT_TYPE,
    DISCIPLINE,
    PROGRAM,
}

fun IdBasedFilter.toFilter(): Filter? {
    return when (this) {
        is SexFilter -> Filter.Sex(id, name)
        is AgeFilter -> Filter.Age(id, name)
        is CalendarSportTypeFilter -> Filter.SportType(id, name)
        is CalendarSportFilter -> Filter.Sport(id, name)
        is DisciplineInfoFilter -> Filter.Discipline(id, name)
        is ProgramInfoFilter -> Filter.Program(id, name)
        is TeamFilter -> Filter.Team(id, name)
        else -> error("Invalid type ${this::class}")
    }
}

@Serializable(with = Filter.Serializer::class)
sealed interface Filter {
    val id: Int?
    val filterType: FilterType
    val textRepresentation: String

    @Serializable
    data class Date(
        val startDate: LocalDate? = null,
        val endDate: LocalDate? = null
    ) : Filter {
        override val id = null
        override val filterType = FilterType.DATE
        @Transient
        override val textRepresentation = run {
            if (startDate == null && endDate != null) {
                return@run "до ${endDate.toJavaLocalDate().print("dd.MM")}"
            }
            if (endDate == null && startDate != null) {
                return@run "от ${startDate.toJavaLocalDate().print("dd.MM")}"
            }
            if (startDate != null && endDate != null) {
                return@run startDate.toJavaLocalDate().print("dd.MM") +
                        "-${endDate.toJavaLocalDate().print("dd.MM")}"
            }
            ""
        }
    }
    @Serializable
    data class SportType(override val id: Int, val name: String) : Filter {
        override val filterType = FilterType.CALENDAR_SPORT_TYPE
        @Transient
        override val textRepresentation = name
    }

    @Serializable
    data class Sport(override val id: Int, val name: String) : Filter {
        override val filterType = FilterType.CALENDAR_SPORT
        @Transient
        override val textRepresentation = name
    }

    @Serializable
    data class Team(override val id: Int, val name: String) : Filter {
        override val filterType = FilterType.TEAM
        @Transient
        override val textRepresentation = name
    }

    @Serializable
    data class Discipline(override val id: Int, val name: String) : Filter {
        override val filterType = FilterType.DISCIPLINE
        @Transient
        override val textRepresentation = name
    }

    @Serializable
    data class Age(override val id: Int, val name: String) : Filter {
        override val filterType = FilterType.AGE
        @Transient
        override val textRepresentation = name
    }

    @Serializable
    data class Program(override val id: Int, val name: String) : Filter {
        override val filterType = FilterType.PROGRAM
        @Transient
        override val textRepresentation = name
    }

    @Serializable
    data class Sex(override val id: Int, val name: String) : Filter {
        override val filterType = FilterType.SEX
        @Transient
        override val textRepresentation = name
    }

    @Serializable
    data class MemberRange(val start: Int, val end: Int) : Filter {
        override val id = null
        override val filterType = FilterType.MEMBER_RANGE
        @Transient
        override val textRepresentation = run {
            if (end >= 100) {
                "$start-100+ участников"
            } else {
                "$start-$end участников"
            }
        }
    }

    object Serializer : KSerializer<Filter> {
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = true
            coerceInputValues = true
        }
        override val descriptor = PolymorphicSerializer(Filter::class).descriptor

        override fun deserialize(decoder: Decoder): Filter {
            val jsonElement = (decoder as JsonDecoder).decodeJsonElement()
            return when (val itemType = jsonElement.jsonObject["filterType"]?.jsonPrimitive?.content) {
                FilterType.DATE.name -> json.decodeFromJsonElement(Date.serializer(), jsonElement)
                FilterType.CALENDAR_SPORT_TYPE.name -> json.decodeFromJsonElement(SportType.serializer(), jsonElement)
                FilterType.CALENDAR_SPORT.name -> json.decodeFromJsonElement(Sport.serializer(), jsonElement)
                FilterType.MEMBER_RANGE.name -> json.decodeFromJsonElement(MemberRange.serializer(), jsonElement)
                FilterType.SEX.name -> json.decodeFromJsonElement(Sex.serializer(), jsonElement)
                FilterType.TEAM.name -> json.decodeFromJsonElement(Team.serializer(), jsonElement)
                FilterType.DISCIPLINE.name -> json.decodeFromJsonElement(Discipline.serializer(), jsonElement)
                FilterType.PROGRAM.name -> json.decodeFromJsonElement(Program.serializer(), jsonElement)
                FilterType.AGE.name -> json.decodeFromJsonElement(Age.serializer(), jsonElement)
                else -> throw SerializationException("Unknown itemType: $itemType")
            }
        }

        override fun serialize(encoder: Encoder, value: Filter) {
            when (value) {
                is Date -> encoder.encodeSerializableValue(Date.serializer(), value)
                is SportType -> encoder.encodeSerializableValue(SportType.serializer(), value)
                is MemberRange -> encoder.encodeSerializableValue(MemberRange.serializer(), value)
                is Sex -> encoder.encodeSerializableValue(Sex.serializer(), value)
                is Age -> encoder.encodeSerializableValue(Age.serializer(), value)
                is Team -> encoder.encodeSerializableValue(Team.serializer(), value)
                is Sport -> encoder.encodeSerializableValue(Sport.serializer(), value)
                is Discipline -> encoder.encodeSerializableValue(Discipline.serializer(), value)
                is Program -> encoder.encodeSerializableValue(Program.serializer(), value)
            }
        }
    }
}