package ru.master.app.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.master.app.ui.theme.AppTheme

@Composable
fun CalendarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = AppTheme.colorScheme.background1,
            onSurface = AppTheme.colorScheme.foreground1,
            primary = AppTheme.colorScheme.foreground,
            onPrimary = AppTheme.colorScheme.foregroundOnBrand,
            secondaryContainer = AppTheme.colorScheme.backgroundBrand,
        ),
        content = content
    )
}