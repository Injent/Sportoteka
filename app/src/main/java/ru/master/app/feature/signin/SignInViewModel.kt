package ru.master.app.feature.signin

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.master.app.data.auth.AuthRepository
import ru.master.app.data.auth.LoginResult
import ru.master.app.data.settings.SettingsRepository
import ru.master.app.navigation.Navigator
import ru.master.app.navigation.Screen

class SignInViewModel : ViewModel() {
    val login = TextFieldState("admin")
    val password = TextFieldState("admin")

    val registrationMode = MutableStateFlow(false)

    val errorText = MutableStateFlow<String?>(null)
    val loading = MutableStateFlow(false)

    fun onLoginAsGuest() {
        viewModelScope.launch {
            AuthRepository.loginAsGuest()
            Navigator.navigate(Screen.Home) {
                launchSingleTop = true
                popUpTo(0)
            }
        }
    }

    fun onLogin() {
        viewModelScope.launch {
            loading.value = true
            errorText.value = null

            val result = if (registrationMode.value) {
                AuthRepository.register(
                    login = login.text.toString(),
                    password = password.text.toString()
                )
            } else {
                AuthRepository.login(
                    login = login.text.toString(),
                    password = password.text.toString()
                )
            }

            when (result) {
                is LoginResult.Error -> errorText.value = result.error
                is LoginResult.Success -> {
                    SettingsRepository.setUserId(result.userId)
                    Navigator.navigate(Screen.Home) {
                        launchSingleTop = true
                        popUpTo(0)
                    }
                }
            }
            loading.value = false
        }
    }

    fun switchLoginMode() {
        registrationMode.value = !registrationMode.value
    }
}