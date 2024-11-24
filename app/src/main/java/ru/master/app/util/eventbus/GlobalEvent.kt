package ru.master.app.util.eventbus

import ru.master.app.model.GeoPoint

sealed interface GlobalEvent {
    data class DisplayError(val text: String) : GlobalEvent
    data class OpenMap(val geoPoint: GeoPoint) : GlobalEvent
}