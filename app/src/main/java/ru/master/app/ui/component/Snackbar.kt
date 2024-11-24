package ru.master.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.radusalagean.infobarcompose.BaseInfoBarMessage
import ru.master.app.ui.theme.AppTheme

class CustomSnackMessage(
    val text: String,
    val icon: ImageVector,
    val iconColor: Color,
    val action: () -> Unit,
    val actionText: String,
    override val displayTimeSeconds: Int = 4,
) : BaseInfoBarMessage() {
    override val containsControls = false
    override val backgroundColor = Color.Unspecified
}

@Composable
fun CustomSnackbarVisual(
    message: CustomSnackMessage,
    modifier: Modifier = Modifier
) {
    AppCard(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = message.icon,
                contentDescription = null,
                tint = message.iconColor
            )

            Text(
                text = message.text,
                style = AppTheme.typography.body,
                color = AppTheme.colorScheme.foreground1
            )

            Spacer(Modifier.weight(1f))

            TextButton(
                onClick = message.action,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppTheme.colorScheme.foreground
                )
            ) {
                Text(
                    text = message.actionText,
                    style = AppTheme.typography.calloutButton
                )
            }
        }
    }
}