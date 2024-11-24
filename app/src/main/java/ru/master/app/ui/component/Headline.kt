package ru.master.app.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.master.app.ui.theme.AppTheme

@Composable
fun Headline(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = AppTheme.typography.subheadline,
        color = AppTheme.colorScheme.foreground2,
        modifier = modifier
    )
}