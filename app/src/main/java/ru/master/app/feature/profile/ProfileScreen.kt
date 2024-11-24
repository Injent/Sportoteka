package ru.master.app.feature.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.master.app.R
import ru.master.app.navigation.Screen
import ru.master.app.ui.component.AppCard
import ru.master.app.ui.component.AppSwitch
import ru.master.app.ui.theme.AppTheme

fun NavGraphBuilder.profileRoute() {
    composable<Screen.Profile> {
        ProfileRoute()
    }
}

@Composable
fun ProfileRoute() {
    val viewModel = koinViewModel<ProfileViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

    val data by viewModel.data.collectAsStateWithLifecycle()

    ProfileScreen(
        onSignOut = viewModel::onSignOut,
        onSetNotifications = viewModel::onSetNotifications,
        uiState = data,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    onSignOut: () -> Unit,
    onSetNotifications: (Boolean) -> Unit,
    uiState: ProfileUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile),
                        style = AppTheme.typography.title3,
                        color = AppTheme.colorScheme.foreground1
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            if (uiState.authedAsGuest) {
                NotAuthedContent(
                    onSignOut = onSignOut
                )
            } else {
                AuthedContent(
                    onSignOut = onSignOut,
                    notificationsEnabled = uiState.notificationsEnabled,
                    onSetNotifications = onSetNotifications
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NotAuthedContent(
    onSignOut: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                tint = AppTheme.colorScheme.foreground2,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = stringResource(R.string.signIn_to_save_filters),
                style = AppTheme.typography.headline1,
                color = AppTheme.colorScheme.foreground2
            )
            Button(
                onClick = onSignOut,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colorScheme.foreground
                )
            ) {
                Text(
                    text = stringResource(R.string.sign_out),
                    style = AppTheme.typography.calloutButton,
                    color = AppTheme.colorScheme.foregroundOnBrand
                )
            }
        }
    }
}

@Composable
fun AuthedContent(
    onSignOut: () -> Unit,
    notificationsEnabled: Boolean,
    onSetNotifications: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NotificationsCard(
            checked = notificationsEnabled,
            onCheckedChange = onSetNotifications
        )

        ProfileCard(
            onClick = onSignOut,
            icon = Icons.AutoMirrored.Rounded.Logout,
            text = stringResource(R.string.sign_out),
            tint = AppTheme.colorScheme.foregroundError,
            showArrow = false
        )
    }
}

@Composable
fun ProfileCard(
    onClick: () -> Unit,
    icon: ImageVector,
    tint: Color,
    text: String,
    showArrow: Boolean = true,
    modifier: Modifier = Modifier
) {
    AppCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint
            )
            Text(
                text = text,
                style = AppTheme.typography.headline2,
                color = AppTheme.colorScheme.foreground1
            )

            if (showArrow) {
                Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.foreground2
                )
            }
        }
    }
}

@Composable
fun NotificationsCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = AppTheme.colorScheme.foreground
            )
            Text(
                text = stringResource(R.string.notifications),
                style = AppTheme.typography.headline2,
                color = AppTheme.colorScheme.foreground1
            )

            Spacer(Modifier.weight(1f))

            AppSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

