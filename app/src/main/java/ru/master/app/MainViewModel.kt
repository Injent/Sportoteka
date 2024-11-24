package ru.master.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.master.app.data.settings.SettingsRepository

enum class AuthState {
    LOADING,
    AUTHED,
    NOT_AUTHED,
    GUEST
}

class MainViewModel : ViewModel() {
    val authState = SettingsRepository.data.map { data ->
        if (data.notificationsEnabled) {
            Firebase.messaging.subscribeToTopic("general")
        }

        if (data.accessToken == "guest") return@map AuthState.GUEST
        if (data.accessToken != null) AuthState.AUTHED else AuthState.NOT_AUTHED
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.LOADING
        )
}