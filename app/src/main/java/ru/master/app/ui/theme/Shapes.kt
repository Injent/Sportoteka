package ru.master.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class AppShapes(
    val default: RoundedCornerShape,
    val large: RoundedCornerShape,
    val small: RoundedCornerShape,
    val extraSmall: RoundedCornerShape,
    val defaultTopCarved: RoundedCornerShape,
    val defaultBottomCarved: RoundedCornerShape
)

val LocalShapes = staticCompositionLocalOf { Shapes }

val Shapes = AppShapes(
    default = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
    defaultTopCarved = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    defaultBottomCarved = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
    extraSmall = RoundedCornerShape(4.dp)
)