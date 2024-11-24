package ru.master.app.feature.filters.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.master.app.R
import ru.master.app.ui.theme.AppTheme

@Composable
fun EditFilter(
    label: String,
    action: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = AppTheme.shapes.default,
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Sell,
                contentDescription = null,
                tint = AppTheme.colorScheme.foreground2,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = label,
                    style = AppTheme.typography.footnote,
                    color = AppTheme.colorScheme.foreground2
                )
                Text(
                    text = action,
                    style = AppTheme.typography.body,
                    color = AppTheme.colorScheme.foreground1
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.select),
                    style = AppTheme.typography.calloutButton,
                    color = AppTheme.colorScheme.foreground2
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.foreground2,
                    modifier = Modifier.size(8.dp)
                )
            }
        }
    }
}