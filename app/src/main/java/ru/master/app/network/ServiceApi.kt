package ru.master.app.network

import IgnoreTrustManager
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import ru.master.app.data.settings.ComposedFilter
import ru.master.app.data.settings.Filter
import ru.master.app.data.settings.FilterType
import ru.master.app.data.settings.SettingsRepository
import ru.master.app.model.DisciplineInfoFilter
import ru.master.app.model.GeoPoint
import ru.master.app.network.request.LoginRequest
import ru.master.app.network.request.NotificationRequest
import ru.master.app.network.request.RegistrationRequest
import ru.master.app.network.request.toCalendarSportRequest
import ru.master.app.network.response.CalendarSportInfoResponse
import ru.master.app.network.response.FiltersResponse
import ru.master.app.network.response.LoginResponse
import ru.master.app.network.response.SaveFilterResponse
import ru.master.app.network.response.toSportEvent
import ru.master.app.util.Either
import ru.master.app.util.runResulting

internal object Routes {
    private const val BASE = "http://90.156.208.88:8080/bryansk/api"
    const val AUTH = "$BASE/auth/authentication"
    const val REGISTRATION = "$BASE/auth/registration"
    const val GET_ALL_FILTERS = "http://90.156.208.88:8080/bryansk/api/shared/get-all-directory"
    const val MY_FILTERS = "http://90.156.208.88:8080/bryansk/api/user/get-all-my-filter"
    const val SAVE_FILTER = "http://90.156.208.88:8080/bryansk/api/user/save-filter"
    const val UPDATE_FILTER = "http://90.156.208.88:8080/bryansk/api/user/update-filter"
    const val DELETE_FILTER = "http://90.156.208.88:8080/bryansk/api/user/delete-filter-by-id"
    const val REGISTER_FCM_TOKEN = "http://90.156.208.88:8080/bryansk/api/farebase/register-fcm-token"
    const val GET_SPORT_EVENTS = "http://90.156.208.88:8080/bryansk/api/calendar/get-calendar-sport-info-by-filter"
    const val GEOCODE = "http://94.228.127.47:8084/api/v1/geocode"
}

object ServiceApi {
    @OptIn(ExperimentalSerializationApi::class)
    val format = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        decodeEnumsCaseInsensitive = false
    }

    suspend fun getAllFilters() = runResulting {
        ktor.get {
            url(Routes.GET_ALL_FILTERS)
        }.body<FiltersResponse>()
    }

    suspend fun saveFilter(composedFilter: ComposedFilter) = runResulting {
        ktor.post {
            url(Routes.SAVE_FILTER)
            setBody(
                buildJsonObject {
                    put("name", composedFilter.name)
                    put("value", format.encodeToString(composedFilter.filters))
                }
            )
        }.body<SaveFilterResponse>().filterId
    }

    suspend inline fun <reified T> searchFilter(filterType: FilterType, query: String) = runResulting {
        ktor.get {
            val filterTypeText = when (filterType) {
                FilterType.SEX -> "sex-category"
                FilterType.TEAM -> "team-info"
                FilterType.AGE -> "age-category"
                FilterType.CALENDAR_SPORT -> "calendar-sport"
                FilterType.CALENDAR_SPORT_TYPE -> "calendar-sport-type"
                FilterType.DISCIPLINE -> "do"
                FilterType.PROGRAM -> "program-info"
                else -> error("Unsupported filter")
            }
            url("http://90.156.208.88:8080/bryansk/api/shared/get-all-$filterTypeText-by-name")
            parameter("name", query)
        }.body<JsonObject>().run {
            format.decodeFromJsonElement<List<T>>(get("data")!!)
        }
    }

    suspend fun subscribeToEvent(request: NotificationRequest) = runResulting {
        ktor.post {
            url("http://94.228.127.47:8084/api/v1/notifications")
            setBody(request)
        }.body<JsonObject>()["id"]!!.jsonPrimitive.int
    }

    suspend fun searchDisc(q: String) = runResulting {
        ktor.get {
            url("http://94.228.127.47:8084/api/v1/discipline-filter-search")
            parameter("name", q)
        }.body<List<DisciplineInfoFilter>>()
    }

    suspend fun unsubscribeEvent(notifId: Int) = runResulting {
        ktor.delete {
            url("http://94.228.127.47:8084/api/v1/notifications/$notifId")
        }
        Unit
    }

    suspend fun updateFilter(composedFilter: ComposedFilter) = runResulting {
        ktor.put {
            url(Routes.UPDATE_FILTER)
            setBody(
                buildJsonObject {
                    put("id", composedFilter.id)
                    put("name", composedFilter.name)
                    put("value", format.encodeToString(composedFilter.filters))
                }
            )
        }
        Unit
    }

    suspend fun registerFcmToken(token: String) = runResulting {
        ktor.post {
            url(Routes.REGISTER_FCM_TOKEN)
            setBody(
                buildJsonObject {
                    put("fcmToken", token)
                }
            )
        }
        Unit
    }

    suspend fun geoCode(address: String) = runResulting {
        ktor.get {
            url(Routes.GEOCODE)
            parameter("address", address)
        }.body<GeoPoint>()
    }

    suspend fun deleteFilter(filterId: Int) = runResulting {
        ktor.delete {
            url(Routes.DELETE_FILTER)
            parameter("id", filterId)
        }
        Unit
    }

    suspend fun getMyFilters() = runResulting {
        ktor.get {
            url(Routes.MY_FILTERS)
        }.body<JsonObject>().run {
            get("data")!!.jsonArray.map { el ->
                val obj = el.jsonObject

                ComposedFilter(
                    id = obj["id"]!!.jsonPrimitive.int,
                    name = obj["name"]!!.jsonPrimitive.content,
                    filters = format.decodeFromString(
                        ListSerializer(Filter.Serializer), obj["value"]!!.jsonPrimitive.content)
                )
            }
        }
    }

    suspend fun getSportEvents(filters: List<Filter>, page: Int, pageSize: Int = 10) = runResulting {
        ktor.post {
            url(Routes.GET_SPORT_EVENTS)
            setBody(filters.toCalendarSportRequest(page, pageSize))
        }.body<CalendarSportInfoResponse>().data.map { it.toSportEvent() }
    }

    suspend fun login(request: LoginRequest): Either<LoginResponse> = runResulting {
        ktor.post {
            url(Routes.AUTH)
            setBody(request)
        }.body<LoginResponse>()
    }

    suspend fun register(request: RegistrationRequest): Either<LoginResponse> = runResulting {
        ktor.post {
            url(Routes.REGISTRATION)
            setBody(request)
        }.body<LoginResponse>()
    }

    val ktor = HttpClient(CIO) {
        expectSuccess = true
        followRedirects = true

        engine {
            https {
                trustManager = IgnoreTrustManager(this)
            }
            endpoint {
                keepAliveTime = 5000
                connectTimeout = 5000
                connectAttempts = 2
            }
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
            }
            header(HttpHeaders.ContentType, "application/json")
        }

        install(ContentNegotiation) {
            json(format)
        }

        configureResponseValidator()

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", message)
                }
            }
            level = LogLevel.ALL
        }
    }.also {
        it.authInterceptor()
    }
}

private fun HttpClientConfig<*>.configureResponseValidator() {
    HttpResponseValidator {
        validateResponse { response ->


            if (response.status.isSuccess()) return@validateResponse

            throw HttpException(response.status)
        }
    }
}

fun HttpClient.authInterceptor() {
    plugin(HttpSend).intercept { request ->
        val accessToken = SettingsRepository.data.first().accessToken

        if (accessToken != null && accessToken != "guest") {
            request.bearerAuth(accessToken)
        }

        val originalCall = execute(request)

        if (originalCall.response.status == HttpStatusCode.Unauthorized && accessToken != null) {
            originalCall
        } else {
            originalCall
        }
    }
}