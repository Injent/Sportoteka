package ru.master.app

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.master.app.data.messaging.FirebaseTokenWorker
import ru.master.app.data.settings.SettingsRepository
import ru.master.app.navigation.Navigator
import ru.master.app.navigation.Screen
import ru.master.app.ui.App
import ru.master.app.ui.theme.MasterAppTheme
import ru.master.app.util.ClearFocusWithImeEffect

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsRepository.init(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { authState ->
                    when (authState) {
                        AuthState.NOT_AUTHED -> Navigator.navigate(Screen.SignIn) {
                            popUpTo(0)
                        }
                        AuthState.AUTHED -> {
                            FirebaseTokenWorker.start(
                                this@MainActivity,
                                Firebase.messaging.token.await()
                            )
                            Navigator.navigate(Screen.Home) {
                                popUpTo(0)
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        AuthState.GUEST -> {
                            Navigator.navigate(Screen.Home) {
                                popUpTo(0)
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }

        askNotificationPermission()

        enableEdgeToEdge()
        setContent {
            ClearFocusWithImeEffect()

            MasterAppTheme {
                App()
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {

            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        overrideLargeFontSize()
    }

    private fun overrideLargeFontSize() {
        Configuration().apply {
            setTo(baseContext.resources.configuration)
            fontScale = fontScale.coerceAtMost(1f)
            applyOverrideConfiguration(this)
        }
    }
}