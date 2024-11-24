package ru.master.app.data.filters.util

import androidx.compose.runtime.mutableStateListOf
import ru.master.app.data.settings.Filter
import ru.master.app.network.response.FiltersResponse

object ReservedFilters {
    var filters: FiltersResponse? = null
    val appliedFilters = mutableStateListOf<Filter>()
}