package ru.master.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.radusalagean.infobarcompose.InfoBar
import ru.master.app.feature.favourites.favouritesRoute
import ru.master.app.feature.filters.filtersRoute
import ru.master.app.feature.home.homeRoute
import ru.master.app.feature.profile.profileRoute
import ru.master.app.feature.signin.signInRoute
import ru.master.app.loadingRoute
import ru.master.app.navigation.NavigationAction
import ru.master.app.navigation.Navigator
import ru.master.app.navigation.ObserveAsEvents
import ru.master.app.navigation.Screen
import ru.master.app.ui.component.AppBottomNavigation
import ru.master.app.ui.component.CustomSnackMessage
import ru.master.app.ui.component.CustomSnackbarVisual
import ru.master.app.ui.component.NavigationItems
import ru.master.app.ui.component.topLevelRoutes
import ru.master.app.ui.theme.AppTheme
import ru.master.app.util.eventbus.EventBus
import ru.master.app.util.eventbus.GlobalEvent

@Composable
fun App() {
    val context = LocalContext.current
    val navHostController: NavHostController = rememberNavController()

    ObserveAsEvents(flow = Navigator.navigationActions) { action ->
        when (action) {
            is NavigationAction.Navigate -> navHostController.navigate(
                action.destination
            ) { action.navOptions(this) }
            NavigationAction.NavigateUp -> navHostController.navigateUp()
        }
    }

    Box {
        var snackMessage by remember { mutableStateOf<CustomSnackMessage?>(null) }
        val errorColor = AppTheme.colorScheme.foregroundError

        LaunchedEffect(Unit) {
            EventBus.subscribe<GlobalEvent.DisplayError> { event ->
                snackMessage = CustomSnackMessage(
                    text = event.text,
                    icon = Icons.Rounded.Error,
                    iconColor = errorColor,
                    action = { snackMessage = null },
                    actionText = context.getString(android.R.string.ok),
                )
            }
        }

        NavHost(
            navController = navHostController,
            startDestination = Screen.Loading,
            modifier = Modifier.fillMaxSize()
        ) {
            loadingRoute()
            signInRoute()
            homeRoute()
            profileRoute()
            filtersRoute()
            favouritesRoute()
        }

        InfoBar(
            offeredMessage = snackMessage,
            content = { CustomSnackbarVisual(it) },
            onDismiss = {
                snackMessage = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f)
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )

        val visibleState = remember { MutableTransitionState(true) }

        LaunchedEffect(navHostController) {
            navHostController.addOnDestinationChangedListener { _, destination, _ ->
                visibleState.targetState = destination.isTopLevelDestination()
            }
        }

        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            AppBottomNavigation(
                modifier = Modifier
                    .zIndex(1f)
                    .navigationBarsPadding()
            ) {
                NavigationItems(
                    navController = navHostController,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

fun NavDestination?.isTopLevelDestination(): Boolean {
    return topLevelRoutes.any {
        this?.hasRoute(it.route::class) ?: false
    }
}