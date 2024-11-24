package ru.master.app.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import ru.master.app.navigation.Screen
import ru.master.app.ui.theme.AppTheme
import ru.master.app.util.boxShadow

typealias RuStore = Firebase
typealias RuStorePushClient = FirebaseMessagingService

class TopLevelRoute<T : Any>(
    val route: T,
    val icon: ImageVector,
    val name: String,
)

val topLevelRoutes = listOf(
    TopLevelRoute(route = Screen.Home, Icons.Rounded.Home, "Главная"),
    TopLevelRoute(route = Screen.Favourites, Icons.Rounded.Bookmark, "Избранное"),
    TopLevelRoute(route = Screen.Profile, Icons.Rounded.Person, "Профиль"),
)

@Composable
fun AppBottomNavigation(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .boxShadow(offset = DpOffset(x = 0.dp, y = -1.dp))
            .background(AppTheme.colorScheme.background3)
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun BottomBarTabItem(
    label: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onItemSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedOffset = remember { Animatable(0f) }

    LaunchedEffect(selected) {
        if (!selected) return@LaunchedEffect
        withContext(NonCancellable) {
            animatedOffset.animateTo(
                targetValue = -20f,
                animationSpec = tween(
                    durationMillis = 150,
                    easing = EaseOutBack
                )
            )
            animatedOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 150,
                    easing = EaseOutBack
                )
            )
        }
    }

    Box(
        modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onItemSelected() }
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val color by animateColorAsState(
                targetValue = if (selected) {
                    AppTheme.colorScheme.foreground
                } else AppTheme.colorScheme.foreground3, label = ""
            )
            CompositionLocalProvider(LocalContentColor provides color) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(x = 0, y = animatedOffset.value.toInt())
                        }
                ) {
                    icon()
                }
                Text(
                    text = label,
                    style = AppTheme.typography.caption1,
                    lineHeight = 1.6.em,
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}

@Composable
fun NavigationItems(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    topLevelRoutes.forEach { tab ->
        val backStackEntry by navController.currentBackStackEntryAsState()
        val selected = backStackEntry?.destination
            ?.hierarchy
            ?.any { it.hasRoute(tab.route::class) }
            ?: false

        BottomBarTabItem(
            modifier = modifier,
            label = tab.name,
            selected = selected,
            onItemSelected = { navController.navigateTopDestination(tab.route) },
            icon = {
                DefaultIcon(
                    icon = tab.icon
                )
            }
        )
    }
}

@Composable
fun DefaultIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.size(24.dp)
    )
}

fun NavController.navigateTopDestination(tab: Any) {
    if (currentDestination?.hasRoute(tab::class) == true) return

    navigate(tab) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}