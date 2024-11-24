package ru.master.app.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.master.app.ui.theme.AppTheme

@Composable
fun AppTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    tip: String? = null,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    onKeyboardAction: KeyboardActionHandler? = null,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    inputTransformation: InputTransformation = InputTransformation {},
    interactionSource: MutableInteractionSource? = null,
    maxTextLenght: Int = 300,
    maxLines: Int = 1
) {
    val mutableInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by mutableInteractionSource.collectIsFocusedAsState()

    val cursorColor = AppTheme.colorScheme.foreground
    val cursonBrush = remember { SolidColor(cursorColor) }

    val containerColor by animateColorAsState(
        targetValue = if (readOnly) {
            AppTheme.colorScheme.background4
        } else {
            when {
                !enabled -> AppTheme.colorScheme.backgroundDisabled
                isFocused -> AppTheme.colorScheme.background4
                else -> AppTheme.colorScheme.background4.copy(.7f)
            }
        },
    )

    val iconColor by animateColorAsState(
        targetValue = if (enabled) {
            AppTheme.colorScheme.foreground3
        } else AppTheme.colorScheme.foreground4,
    )

    Column(modifier) {
        label?.let {
            Text(
                text = label,
                style = AppTheme.typography.subheadline,
                color = AppTheme.colorScheme.foreground2
            )
            Spacer(Modifier.height(8.dp))
        }
        BasicTextField(
            state = state,
            modifier = Modifier,
            textStyle = AppTheme.typography.callout.copy(
                color = AppTheme.colorScheme.foreground1
            ),
            lineLimits = lineLimits,
            enabled = enabled,
            readOnly = readOnly,
            interactionSource = mutableInteractionSource,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            cursorBrush = cursonBrush,
            inputTransformation = InputTransformation {
                if (asCharSequence().length > maxTextLenght) {
                    revertAllChanges()
                    return@InputTransformation
                }
                if (maxLines < asCharSequence().count { it == '\n' }) {
                    revertAllChanges()
                }
            }.then(inputTransformation),
            decorator = { textLayout ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = if (lineLimits is TextFieldLineLimits.MultiLine) {
                        Alignment.Top
                    } else Alignment.CenterVertically,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .background(containerColor, AppTheme.shapes.default)
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        )
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides iconColor,
                        content = leadingIcon
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Box {
                            textLayout()
                            if (state.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = AppTheme.typography.callout,
                                    color = AppTheme.colorScheme.foreground4,
                                )
                            }
                        }
                        if (maxLines > 1) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${state.text.length}/$maxTextLenght",
                                style = AppTheme.typography.footstrike,
                                color = AppTheme.colorScheme.foreground4,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides iconColor,
                        content = trailingIcon
                    )
                }
            }
        )

        tip?.let { tip ->
            Text(
                text = tip,
                color = AppTheme.colorScheme.foreground2,
                style = AppTheme.typography.footnote,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun AppSecureTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String = "",
    textVisible: Boolean = false,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    imeAction: ImeAction = ImeAction.Default,
    onKeyboardAction: KeyboardActionHandler? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val cursorColor = AppTheme.colorScheme.foreground
    val cursorBrush = remember { SolidColor(cursorColor) }

    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> AppTheme.colorScheme.backgroundDisabled
            isFocused -> AppTheme.colorScheme.background4
            else -> AppTheme.colorScheme.background4.copy(.7f)
        },
    )

    val iconColor by animateColorAsState(
        targetValue = if (enabled) {
            AppTheme.colorScheme.foreground3
        } else AppTheme.colorScheme.foreground4,
        label = "container transition"
    )

    Column(modifier) {
        label?.let {
            Text(
                text = label,
                style = AppTheme.typography.callout,
                color = AppTheme.colorScheme.foreground1
            )
            Spacer(Modifier.height(8.dp))
        }
        BasicSecureTextField(
            state = state,
            modifier = Modifier,
            textStyle = AppTheme.typography.callout.copy(
                color = AppTheme.colorScheme.foreground1
            ),
            enabled = enabled,
            interactionSource = interactionSource,
            textObfuscationMode = if (textVisible) {
                TextObfuscationMode.Visible
            } else TextObfuscationMode.RevealLastTyped,
            onKeyboardAction = onKeyboardAction,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            cursorBrush = cursorBrush,
            decorator = { textLayout ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .background(containerColor, AppTheme.shapes.default)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides iconColor,
                        content = leadingIcon
                    )
                    Box(Modifier.weight(1f)) {
                        textLayout()
                        if (state.text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = AppTheme.typography.callout,
                                color = AppTheme.colorScheme.foreground4
                            )
                        }
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides iconColor,
                        content = trailingIcon
                    )
                }
            },
        )
    }
}

@Composable
fun FakeTextField(
    placeholder: String,
    onClick: () -> Unit,
    trailingIcon: @Composable () -> Unit,
    textColor: Color = AppTheme.colorScheme.foreground4,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = AppTheme.shapes.default,
        color = AppTheme.colorScheme.background4,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
        ) {
            trailingIcon()
            Text(
                text = placeholder,
                style = AppTheme.typography.callout,
                color = textColor
            )
        }
    }
}