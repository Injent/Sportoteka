package ru.master.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.master.app.model.SportEvent

object SettingsRepository {
    private lateinit var dataStore: DataStore<UserPrefs>

    fun init(context: Context) {
        if (this::dataStore.isInitialized) return

        dataStore = DataStoreFactory.create(
            serializer = UserPrefsSerializer,
            produceFile = { context.dataStoreFile("settings.json") }
        )
    }

    val data: Flow<UserPrefs>
        get() {
            return try {
                dataStore.data
            } catch (e: ExceptionInInitializerError) {
                return emptyFlow()
            }
        }

    suspend fun addFavouriteSportEvent(sportEvent: SportEvent, notifId: Int) {
        dataStore.updateData {
            val subs = it.subs.toMutableMap()

            subs[sportEvent.id] = notifId

            it.copy(
                subs = subs,
                favourites = it.favourites + sportEvent
            )
        }
    }

    suspend fun setUserId(id: Int?) {
        dataStore.updateData { it.copy(userId = id) }
    }

    suspend fun removeFavouriteSportEvent(sportEvent: SportEvent) {
        dataStore.updateData {
            val subs = it.subs.toMutableMap()

            subs.remove(sportEvent.id)
            it.copy(
                subs = subs,
                favourites = it.favourites.filter { f -> f.id != sportEvent.id }
            )
        }
    }

    suspend fun setAccessToken(accessToken: String?) {
        dataStore.updateData {
            it.copy(accessToken = accessToken)
        }
    }

    suspend fun upsertFilter(filter: ComposedFilter) {
        dataStore.updateData {
            val temp = mutableListOf(*it.composedFilters.toTypedArray())

            val index = temp.indexOfFirst { f -> f.id == filter.id }
            if (index != -1) {
                temp[index] = filter
            } else {
                temp.add(filter)
            }

            it.copy(composedFilters = temp)
        }
    }

    suspend fun setDefaultFilter(filter: ComposedFilter) {
        dataStore.updateData {
            it.copy(defaultFilterId = filter.id)
        }
    }

    suspend fun setFilters(filters: List<ComposedFilter>) {
        dataStore.updateData {
            it.copy(
                composedFilters = filters
            )
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        dataStore.updateData {
            it.copy(
                notificationsEnabled = enabled
            )
        }
    }
}