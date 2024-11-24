package ru.master.app.feature.favourites

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
import ru.master.app.network.ServiceApi
import ru.master.app.util.eventbus.EventBus
import ru.master.app.util.eventbus.GlobalEvent

class FavouritesViewModel : ViewModel() {

    private val _selectedEvent = MutableStateFlow<SportEvent?>(null)
    val selectedEvent = _selectedEvent.asStateFlow()

    val favouritesEvents = SettingsRepository.data
        .map { it.favourites }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun removeEvent(event: SportEvent) {
        viewModelScope.launch {
            val data = SettingsRepository.data.first()

            data.subs[event.id]?.let { notifId ->
                ServiceApi.unsubscribeEvent(notifId)
            }
                ?.onSuccess {
                    SettingsRepository.removeFavouriteSportEvent(event)
                }
                ?.onFailure {
                    EventBus.post(GlobalEvent.DisplayError(it.details))
                }
        }
    }

    fun onSelect(event: SportEvent) {
        _selectedEvent.value = event
    }

    fun onCloseSheet() {
        _selectedEvent.value = null
    }

    fun onOpenMap(city: String) {
        viewModelScope.launch {
            ServiceApi.geoCode(city)
                .onSuccess {
                    EventBus.post(GlobalEvent.OpenMap(it))
                }
        }
    }
}