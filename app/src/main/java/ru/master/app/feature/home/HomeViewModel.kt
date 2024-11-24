package ru.master.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.master.app.data.settings.SettingsRepository
import ru.master.app.model.SportEvent
import ru.master.app.navigation.Navigator
import ru.master.app.navigation.Screen
import ru.master.app.network.ServiceApi
import ru.master.app.network.request.NotificationRequest
import ru.master.app.util.eventbus.EventBus
import ru.master.app.util.eventbus.GlobalEvent

class HomeViewModel : ViewModel() {

    private val _selectedEvent = MutableStateFlow<SportEvent?>(null)
    val selectedEvent = _selectedEvent.asStateFlow()

    val staredEventIds = SettingsRepository.data
        .map { it.favourites.map(SportEvent::id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onFiltersClick() {
        viewModelScope.launch {
            Navigator.navigate(Screen.Filters)
        }
    }

    fun onSelectEvent(sportEvent: SportEvent) {
        _selectedEvent.value = sportEvent
    }

    fun onStar(event: SportEvent) {
        viewModelScope.launch {
            val data = SettingsRepository.data.first()

            if (event.id in staredEventIds.value) {
                data.subs[event.id]?.let { notifId ->
                    ServiceApi.unsubscribeEvent(notifId)
                }
                    ?.onSuccess {
                        SettingsRepository.removeFavouriteSportEvent(event)
                    }
                    ?.onFailure {
                        EventBus.post(GlobalEvent.DisplayError(it.details))
                    }
            } else {
                ServiceApi.subscribeToEvent(
                    NotificationRequest(
                        user = SettingsRepository.data.first().userId ?: -1,
                        calendarSportInfo = event.id,
                        name = event.eventName,
                        eventInfo = event.sportName
                    )
                )
                    .onSuccess {
                        SettingsRepository.addFavouriteSportEvent(event, it)
                    }
                    .onFailure {
                        EventBus.post(GlobalEvent.DisplayError(it.details))
                    }
            }
        }
    }

    fun onGeoCode(city: String) {
        viewModelScope.launch {
            ServiceApi.geoCode(city)
                .onSuccess {
                    EventBus.post(GlobalEvent.OpenMap(it))
                }
        }
    }

    fun onCloseSheet() {
        _selectedEvent.value = null
    }
}