package ru.master.app.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.master.app.feature.favourites.FavouritesViewModel
import ru.master.app.feature.filters.FiltersViewModel
import ru.master.app.feature.home.HomeViewModel
import ru.master.app.feature.profile.ProfileViewModel
import ru.master.app.feature.signin.SignInViewModel

val AppModule = module {
    viewModel {
        SignInViewModel()
    }
    viewModel {
        HomeViewModel()
    }
    viewModel {
        FiltersViewModel()
    }
    viewModel {
        ProfileViewModel()
    }
    viewModel {
        FavouritesViewModel()
    }
}