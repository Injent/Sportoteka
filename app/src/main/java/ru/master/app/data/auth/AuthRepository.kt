package ru.master.app.data.auth

import ru.master.app.data.settings.SettingsRepository
import ru.master.app.network.ServiceApi
import ru.master.app.network.request.LoginRequest
import ru.master.app.network.request.RegistrationRequest
import ru.master.app.util.getOrElse
import ru.master.app.util.map

object AuthRepository {
    suspend fun login(login: String, password: String): LoginResult {
        return ServiceApi.login(LoginRequest(login, password))
            .onSuccess { response ->
                SettingsRepository.setAccessToken(response.token)
            }
            .map {
                LoginResult.Success(it.id)
            }
            .getOrElse { error ->
                LoginResult.Error(error = error.details)
            }
    }

    suspend fun loginAsGuest(): LoginResult {
        SettingsRepository.setAccessToken("guest")
        return LoginResult.Success(null)
    }

    suspend fun register(login: String, password: String): LoginResult {
        return ServiceApi.register(RegistrationRequest(login, password))
            .onSuccess { response ->
                SettingsRepository.setAccessToken(response.token)
            }
            .map {
                LoginResult.Success(it.id)
            }
            .getOrElse { error ->
                LoginResult.Error(error = error.details)
            }
    }

    suspend fun signOut() {
        SettingsRepository.setAccessToken(null)
    }
}

sealed interface LoginResult {
    data class Error(val error: String) : LoginResult
    data class Success(val userId: Int?) : LoginResult
}