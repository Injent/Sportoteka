package ru.master.app.ui.component

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import ru.master.app.ui.theme.AppTheme
import ru.master.app.util.boxShadow

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    color: Color = AppTheme.colorScheme.background1,
    shape: Shape = AppTheme.shapes.default,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    contentColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit
) {
    val view = LocalView.current

    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp,
    ) {
        val colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = contentColor
        )
        val theModifier = Modifier
            .boxShadow(shape = shape)

        val contentInColumn = remember(content, contentPadding) {
            movableContentOf {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
        }
        onClick?.let {
            Card(
                onClick = {
                    it.invoke()
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                },
                shape = shape,
                colors = colors,
                modifier = modifier.then(theModifier),
            ) {
                contentInColumn()
            }
        } ?: run {
            Card(
                shape = shape,
                colors = colors,
                modifier = modifier.then(theModifier)
            ) {
                contentInColumn()
            }
        }
    }
}