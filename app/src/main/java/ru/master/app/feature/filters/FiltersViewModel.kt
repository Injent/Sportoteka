package ru.master.app.feature.filters

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import ru.master.app.data.filters.util.ReservedFilters
import ru.master.app.data.settings.ComposedFilter
import ru.master.app.data.settings.Filter
import ru.master.app.data.settings.SettingsRepository
import ru.master.app.navigation.Navigator
import ru.master.app.network.ServiceApi
import ru.master.app.util.eventbus.EventBus
import ru.master.app.util.eventbus.GlobalEvent
import ru.master.app.util.getOrElse

data class FiltersUiState(
    val showCalendar: Boolean = false,
    val composedFilter: ComposedFilter = ComposedFilter(
        id = null,
        name = "Пользовательский",
        filters = ReservedFilters.appliedFilters
    ),
    val savedFilters: List<ComposedFilter> = emptyList(),
    val authedAsGuest: Boolean = false
) {
    inline fun <reified T> getFilter(): T? {
        return composedFilter.filters.toList().filterIsInstance<T>().firstOrNull()
    }
    inline fun <reified T : Filter> getCheckedIds(): List<Int> {
        return composedFilter.filters.toList().filterIsInstance<T>().mapNotNull { it.id }
    }
    val usingExistingFilter = composedFilter.id != null
}

class FiltersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FiltersUiState())
    val uiState = _uiState.asStateFlow()

    val filterNameInputState = TextFieldState()

    init {
        viewModelScope.launch {
            SettingsRepository.data.collect { data ->
                _uiState.update {
                    var copy = it.copy(
                        savedFilters = data.composedFilters,
                        authedAsGuest = data.accessToken == "guest"
                    )

                    data.defaultFilter?.let { composedFilter ->
                        copy = copy.copy(composedFilter = composedFilter)
                        ReservedFilters.appliedFilters.apply {
                            clear()
                            addAll(composedFilter.filters)
                        }
                    }
                    copy
                }
            }
        }
        viewModelScope.launch {
            val token = SettingsRepository.data.first().accessToken
            if (token == null || token == "guest") return@launch

            ServiceApi.getMyFilters()
                .onSuccess {
                    SettingsRepository.setFilters(it)
                }
                .onFailure { e ->
                    EventBus.post(GlobalEvent.DisplayError(e.details))
                }
        }
    }

    fun onSelectComposedFilter(composedFilter: ComposedFilter) {
        _uiState.update {
            ReservedFilters.appliedFilters.clear()
            ReservedFilters.appliedFilters.addAll(composedFilter.filters)
            it.copy(composedFilter = composedFilter)
        }
    }

    fun onCreateFilter() {
        viewModelScope.launch {
            val filter = _uiState.value.composedFilter.copy(
                name = if (filterNameInputState.text.isNotEmpty()) {
                    filterNameInputState.text.toString()
                } else _uiState.value.composedFilter.name
            )

            val filterId = if (filter.id != null) {
                ServiceApi.updateFilter(filter)
                    .getOrElse { e ->
                        EventBus.post(GlobalEvent.DisplayError(e.details))
                        return@launch
                    }
                filter.id
            } else {
                ServiceApi.saveFilter(composedFilter = filter)
                    .getOrElse { e ->
                        EventBus.post(GlobalEvent.DisplayError(e.details))
                        return@launch
                    }
            }

            _uiState.update {
                it.copy(composedFilter = filter.copy(id = filterId))
            }
            SettingsRepository.upsertFilter(
                filter = filter.copy(id = filterId)
            )
        }
    }

    fun setShowCalendar(show: Boolean) {
        _uiState.update { it.copy(showCalendar = show) }
    }

    fun onSetFilter(filter: Filter) {
        _uiState.update {
            val existingFilterIndex = ReservedFilters.appliedFilters.indexOfFirst { f ->
                f.filterType == filter.filterType && f.id == filter.id
            }

            if (existingFilterIndex != -1) {
                ReservedFilters.appliedFilters[existingFilterIndex] = filter
            } else {
                ReservedFilters.appliedFilters.add(filter)
            }

            it.copy(
                composedFilter = it.composedFilter.copy(filters = ReservedFilters.appliedFilters.toImmutableList())
            )
        }
    }

    fun onDeleteFilter(filter: Filter) {
        _uiState.update {
            ReservedFilters.appliedFilters.remove(filter)
            it.copy(
                composedFilter = it.composedFilter.copy(filters = ReservedFilters.appliedFilters.toImmutableList())
            )
        }
    }

    fun onDeleteComposedFilter(composedFilter: ComposedFilter) {
        if (composedFilter.id == null) return

        viewModelScope.launch {
            ServiceApi.deleteFilter(composedFilter.id)
                .onSuccess {
                    _uiState.update {
                        if (composedFilter.id == it.composedFilter.id) {
                            onReset()
                        }

                        it.copy(savedFilters = it.savedFilters - composedFilter)
                    }
                }
                .onFailure { e ->
                    EventBus.post(GlobalEvent.DisplayError(e.details))
                }
        }
    }

    fun setAsDefault() {
        viewModelScope.launch {
            SettingsRepository.setDefaultFilter(_uiState.value.composedFilter)
        }
    }

    fun onReset() {
        _uiState.update {
            ReservedFilters.appliedFilters.clear()
            it.copy(composedFilter = ComposedFilter(
                id = null,
                name = "Пользовательский",
                filters = emptyList()
            ))
        }
    }

    fun onBack() {
        viewModelScope.launch { Navigator.navigateUp() }
    }
}