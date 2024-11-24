package ru.master.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.master.app.data.auth.AuthRepository
import ru.master.app.data.settings.SettingsRepository

data class ProfileUiState(
    val notificationsEnabled: Boolean = false,
    val authedAsGuest: Boolean = false
)

class ProfileViewModel : ViewModel() {
    val data = SettingsRepository.data
        .map {
            ProfileUiState(
                authedAsGuest = it.accessToken == "guest",
                notificationsEnabled = it.notificationsEnabled
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState()
        )

    fun onSignOut() {
        viewModelScope.launch {
            AuthRepository.signOut()
        }
    }

    fun onSetNotifications(enabled: Boolean) {
        viewModelScope.launch {
            SettingsRepository.setNotifications(enabled)

            if (!enabled) {
                Firebase.messaging.unsubscribeFromTopic("general")
            }
        }
    }
}