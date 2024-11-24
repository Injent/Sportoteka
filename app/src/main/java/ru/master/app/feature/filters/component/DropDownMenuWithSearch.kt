package ru.master.app.feature.filters.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext
import ru.master.app.data.settings.Filter
import ru.master.app.data.settings.toFilter
import ru.master.app.model.IdBasedFilter
import ru.master.app.ui.component.AppTextField
import ru.master.app.ui.theme.AppTheme

@OptIn(FlowPreview::class)
@Composable
fun DropDownMenuWithSearch(
    checkedIds: List<Int>,
    onSetFilter: (Filter) -> Unit,
    onDeleteFilter: (Filter) -> Unit,
    onQuery: suspend (String) -> List<IdBasedFilter>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
    ) {
        val state = remember { TextFieldState() }
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()

        AppTextField(
            state = state,
            interactionSource = interactionSource,
            placeholder = stringResource(android.R.string.search_go),
            modifier = Modifier.fillMaxWidth()
        )

        var results by remember { mutableStateOf(emptyList<IdBasedFilter>()) }
        var loading by remember { mutableStateOf(false) }

        LaunchedEffect(isFocused) {
            if (!isFocused) state.clearText()
        }

        LaunchedEffect(state) {
            snapshotFlow { state.text.toString() }
                .debounce(300)
                .collect {
                    if (it.trim().isEmpty()) return@collect

                    loading = true
                    scope.async {
                        results = withContext(Dispatchers.IO) { onQuery(it).truncate(10) }
                        loading = false
                    }
                }
        }

        AnimatedVisibility(
            visible = isFocused
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                results.forEach { filter ->
                    Surface(
                        shape = AppTheme.shapes.default,
                        color = Color.Transparent,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = filter.id in checkedIds,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        filter.toFilter()?.let(onSetFilter)
                                    } else {
                                        filter.toFilter()?.let(onDeleteFilter)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkmarkColor = AppTheme.colorScheme.foregroundOnBrand,
                                    checkedColor = AppTheme.colorScheme.foreground,
                                    disabledIndeterminateColor = AppTheme.colorScheme.foreground2
                                )
                            )
                            Text(
                                text = filter.name,
                                style = AppTheme.typography.calloutButton,
                                color = AppTheme.colorScheme.foreground2
                            )
                        }
                    }
                }
            }
        }
    }
}

inline fun <reified T> List<T>.truncate(maxSize: Int): List<T> {
    return if (size > maxSize) {
        subList(0, maxSize)
    } else this
}