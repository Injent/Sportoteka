package ru.master.app.data.settings

import androidx.datastore.core.CorruptionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.master.app.model.SportEvent
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class UserPrefs(
    val accessToken: String? = null,
    val userId: Int? = null,
    val composedFilters: List<ComposedFilter> = emptyList(),
    val defaultFilterId: Int? = null,
    val favourites: List<SportEvent> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val subs: Map<Long, Int> = emptyMap()
) {
    val defaultFilter: ComposedFilter?
        get() = composedFilters.firstOrNull { it.id == defaultFilterId }
}

object UserPrefsSerializer : androidx.datastore.core.Serializer<UserPrefs> {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val defaultValue = UserPrefs()

    override suspend fun readFrom(input: InputStream): UserPrefs {
        try {
            val bytes = input.readBytes()
            val string = bytes.decodeToString()
            return json.decodeFromString(string)
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read stored data", e)
        }
    }

    override suspend fun writeTo(t: UserPrefs, output: OutputStream) = withContext(Dispatchers.IO) {
        val string = json.encodeToString(t)
        val bytes = string.encodeToByteArray()
        output.write(bytes)
    }
}