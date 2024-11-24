package ru.master.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object AppTheme {
    val typography: AppTypography
        @Composable get() = LocalTypography.current
    val shapes: AppShapes
        @Composable get() = LocalShapes.current
    val colorScheme: NewColorScheme
        @Composable get() = LocalNewColorScheme.current
}

@Composable
fun MasterAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val newColorScheme = if (darkTheme) {
        NewDarkColorScheme
    } else NewLightColorScheme

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF0C86FF),
            onPrimary = Color.White,
            surface = newColorScheme.background2,
            background = newColorScheme.background2
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF0C86FF),
            onPrimary = Color.White,
            surface = newColorScheme.background2,
            background = newColorScheme.background2
        )
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
            window.decorView.setBackgroundColor(newColorScheme.background2.toArgb())
        }
    }

    CompositionLocalProvider(
        LocalTypography provides Typography,
        LocalShapes provides Shapes,
        LocalNewColorScheme provides newColorScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}