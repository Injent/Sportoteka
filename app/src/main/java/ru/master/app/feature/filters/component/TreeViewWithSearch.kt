package ru.master.app.feature.filters.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.bonsai.core.Bonsai
import cafe.adriel.bonsai.core.node.Branch
import cafe.adriel.bonsai.core.node.Leaf
import cafe.adriel.bonsai.core.tree.Tree
import kotlinx.coroutines.flow.debounce
import ru.master.app.data.settings.Filter
import ru.master.app.data.settings.toFilter
import ru.master.app.model.DisciplineInfoFilter
import ru.master.app.model.IdBasedFilter
import ru.master.app.model.ProgramInfoFilter
import ru.master.app.ui.component.AppTextField
import ru.master.app.ui.theme.AppTheme

@Composable
fun TreeViewWithSearch(
    checkedDisciplineIds: List<Int>,
    checkedProgramIds: List<Int>,
    onSetFilter: (Filter) -> Unit,
    onDeleteFilter: (Filter) -> Unit,
    onQuery: () -> Map<ProgramInfoFilter, List<DisciplineInfoFilter>>,
    modifier: Modifier = Modifier
) {
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

        var results by remember { mutableStateOf(emptyMap<ProgramInfoFilter, List<DisciplineInfoFilter>>()) }

        LaunchedEffect(isFocused) {
            if (!isFocused) state.clearText()
        }

        LaunchedEffect(state) {
            snapshotFlow { state.text.toString() }
                .debounce(300)
                .collect {
                    results = onQuery()
                        .filter { (program, disciplines) ->
                            val s = state.text.toString().lowercase()
                            if (s.trim().isEmpty()) return@filter true
                            s in program.name.lowercase() || disciplines.any { d -> s in d.name.lowercase() }
                        }
                }
        }

        AnimatedVisibility(
            visible = isFocused
        ) {
            val tree = Tree<IdBasedFilter> {
                results.forEach { (filter, subfilters) ->
                    Branch(
                        name = "root",
                        content = filter,
                        customName = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = filter.id in checkedProgramIds,
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
                                    color = AppTheme.colorScheme.foreground1,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    ) {
                        subfilters.forEach { subfilter ->
                            Leaf(
                                name = "child",
                                content = subfilter,
                                customIcon = {
                                    Checkbox(
                                        checked = subfilter.id in checkedDisciplineIds,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                subfilter.toFilter()?.let(onSetFilter)
                                            } else {
                                                subfilter.toFilter()?.let(onDeleteFilter)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkmarkColor = AppTheme.colorScheme.foregroundOnBrand,
                                            checkedColor = AppTheme.colorScheme.foreground,
                                            disabledIndeterminateColor = AppTheme.colorScheme.foreground2
                                        )
                                    )
                                },
                                customName = {
                                    Text(
                                        text = subfilter.name,
                                        style = AppTheme.typography.calloutButton,
                                        color = AppTheme.colorScheme.foreground2,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Bonsai(
                tree = tree,
                onClick = { node ->
                    if (node.name == "child") {
                        if (node.content.id !in checkedDisciplineIds) {
                            node.content.toFilter()?.let(onSetFilter)
                        } else {
                            node.content.toFilter()?.let(onDeleteFilter)
                        }
                    } else if (node.name == "root") {
                        if (node.content.id !in checkedProgramIds) {
                            node.content.toFilter()?.let(onSetFilter)
                        } else {
                            node.content.toFilter()?.let(onDeleteFilter)
                        }
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(350.dp)
            )
        }
    }
}