package ru.master.app.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Loading : Screen

    @Serializable
    data object SignIn : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Filters : Screen

    @Serializable
    data object Favourites : Screen
}