package ru.master.app.ui.component

import android.view.SoundEffectConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import ru.master.app.ui.component.AppSwitchTokens.Gap
import ru.master.app.ui.component.AppSwitchTokens.TrackCornerRadius
import ru.master.app.ui.component.AppSwitchTokens.TrackHeight
import ru.master.app.ui.component.AppSwitchTokens.TrackWidth
import ru.master.app.ui.theme.AppTheme

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val view = LocalView.current
    val thumbRadius = (TrackHeight / 2) - Gap

    val animatePosition by animateFloatAsState(
        targetValue = if (checked)
            with(LocalDensity.current) { (TrackWidth - thumbRadius - Gap).toPx() }
        else
            with(LocalDensity.current) { (thumbRadius + Gap).toPx() },
        label = "",
        animationSpec = tween()
    )

    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> AppTheme.colorScheme.backgroundDisabled.copy(.5f)
            checked -> AppTheme.colorScheme.backgroundBrand
            else -> AppTheme.colorScheme.backgroundDisabled
        },
        animationSpec = tween()
    )
    val thumbColor = Color.White

    Canvas(
        modifier = modifier
            .size(width = TrackWidth, height = TrackHeight)
            .pointerInput(checked) {
                detectTapGestures(
                    onTap = {
                        onCheckedChange(!checked)
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    }
                )
            }
    ) {
        // Track
        drawRoundRect(
            color = trackColor,
            cornerRadius = TrackCornerRadius,
        )

        // Thumb
        drawCircle(
            color = thumbColor,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePosition,
                y = size.height / 2
            )
        )
    }
}

object AppSwitchTokens {
    val TrackWidth = 50.dp
    val TrackHeight = 30.dp
    val Gap = 4.dp
    val TrackCornerRadius = CornerRadius(x = 100f, y = 100f)
}