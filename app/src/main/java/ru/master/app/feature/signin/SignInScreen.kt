package ru.master.app.feature.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.master.app.R
import ru.master.app.navigation.Screen
import ru.master.app.ui.component.AppSecureTextField
import ru.master.app.ui.component.AppTextField
import ru.master.app.ui.theme.AppTheme
import ru.master.app.util.boxShadow

fun NavGraphBuilder.signInRoute() {
    composable<Screen.SignIn> {
        SignInRoute()
    }
}

@Composable
fun SignInRoute() {
    val viewModel = koinViewModel<SignInViewModel>()

    val registrationMode by viewModel.registrationMode.collectAsStateWithLifecycle()
    val error by viewModel.errorText.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    SignInScreen(
        login = viewModel.login,
        password = viewModel.password,
        onLogin = viewModel::onLogin,
        onLoginAsGuest = viewModel::onLoginAsGuest,
        error = error,
        loading = loading,
        registrationMode = registrationMode,
        onSwitchLoginMode = viewModel::switchLoginMode
    )
}

@Composable
private fun SignInScreen(
    login: TextFieldState,
    password: TextFieldState,
    onLogin: () -> Unit,
    error: String?,
    loading: Boolean,
    registrationMode: Boolean,
    onSwitchLoginMode: () -> Unit,
    onLoginAsGuest: () -> Unit,
) {
    Scaffold(
        topBar = {

        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(Modifier.weight(0.8f))
            Text(
                text = if (registrationMode) "Регистрация" else "Вход",
                style = AppTheme.typography.largeTitle,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .boxShadow(shape = LoginContainerShape, offset = DpOffset(0.dp, -2.dp))
                    .background(AppTheme.colorScheme.background1, LoginContainerShape)
                    .padding(32.dp)
                    .imePadding()
            ) {
                val focusRequesterPassword = remember { FocusRequester() }

                AppTextField(
                    state = login,
                    placeholder = "Логин",
                    onKeyboardAction = {
                        focusRequesterPassword.requestFocus()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AlternateEmail,
                            contentDescription = null
                        )
                    }
                )
                Spacer(Modifier.height(16.dp))
                AppSecureTextField(
                    state = password,
                    placeholder = "Пароль",
                    onKeyboardAction = {
                        onLogin()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .focusRequester(focusRequesterPassword)
                )

                AnimatedVisibility(
                    visible = error != null
                ) {
                    error?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = AppTheme.colorScheme.foregroundError
                            )
                            Text(
                                text = it,
                                style = AppTheme.typography.callout,
                                color = AppTheme.colorScheme.foregroundError
                            )
                        }
                    }
                }

                if (error == null) {
                    Spacer(Modifier.height(16.dp))
                }

                Button(
                    shape = AppTheme.shapes.default,
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = AppTheme.colorScheme.foregroundOnBrand,
                            strokeWidth = 2.2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (registrationMode) "Создать аккаунт" else "Войти",
                            style = AppTheme.typography.calloutButton
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.or),
                    style = AppTheme.typography.headline1,
                    color = AppTheme.colorScheme.foreground3,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                OutlinedButton(
                    shape = AppTheme.shapes.default,
                    border = BorderStroke(width = 1.dp, AppTheme.colorScheme.foreground),
                    onClick = onLoginAsGuest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = AppTheme.colorScheme.foreground1,
                            strokeWidth = 2.2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.login_as_guest),
                            style = AppTheme.typography.calloutButton,
                            color = AppTheme.colorScheme.foreground1
                        )
                    }
                }
            }
        }
    }
}

private val LoginContainerShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp
)