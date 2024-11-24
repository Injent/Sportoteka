package ru.master.app.ui.component

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.master.app.ui.theme.AppTheme

@Composable
fun AppChip(
    label: String,
    color: Color = AppTheme.colorScheme.foreground,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(36.dp),
        color = color,
        shape = CircleShape,
        onClick = onDelete ?: {},
        contentColor = AppTheme.colorScheme.foregroundOnBrand
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                    horizontal = 12.dp
                )
        ) {
            Text(
                text = label,
                style = AppTheme.typography.calloutButton,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .width(IntrinsicSize.Max)
            )

            if (onDelete != null) {
                Spacer(Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.foregroundOnBrand,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}