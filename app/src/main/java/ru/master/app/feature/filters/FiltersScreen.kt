package ru.master.app.feature.filters

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import javatimefun.localdate.extensions.print
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.koin.androidx.compose.koinViewModel
import ru.master.app.R
import ru.master.app.data.auth.AuthRepository
import ru.master.app.data.settings.ComposedFilter
import ru.master.app.data.settings.Filter
import ru.master.app.data.settings.FilterType
import ru.master.app.feature.filters.component.DropDownMenuWithSearch
import ru.master.app.feature.filters.component.EditFilter
import ru.master.app.model.AgeFilter
import ru.master.app.model.CalendarSportFilter
import ru.master.app.model.CalendarSportTypeFilter
import ru.master.app.model.ProgramInfoFilter
import ru.master.app.model.SexFilter
import ru.master.app.model.TeamFilter
import ru.master.app.navigation.Screen
import ru.master.app.network.ServiceApi
import ru.master.app.ui.component.AppChip
import ru.master.app.ui.component.AppTextField
import ru.master.app.ui.component.CalendarTheme
import ru.master.app.ui.component.FakeTextField
import ru.master.app.ui.component.Headline
import ru.master.app.ui.theme.AppTheme
import ru.master.app.util.Either
import ru.master.app.util.boxShadow
import ru.master.app.util.getOrElse
import kotlin.math.roundToInt

fun NavGraphBuilder.filtersRoute() {
    composable<Screen.Filters> {
        FiltersRoute()
    }
}

@Composable
fun FiltersRoute() {
    val viewModel = koinViewModel<FiltersViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FiltersScreen(
        uiState = uiState,
        onSetShowCalendar = viewModel::setShowCalendar,
        onSetFilter = viewModel::onSetFilter,
        onDeleteFilter = viewModel::onDeleteFilter,
        onBack = viewModel::onBack,
        onSelectComposedFilter = viewModel::onSelectComposedFilter,
        onCreateFilter = viewModel::onCreateFilter,
        filterNameInputState = viewModel.filterNameInputState,
        onReset = viewModel::onReset,
        onDeleteComposedFilter = viewModel::onDeleteComposedFilter,
        setAsDefault = viewModel::setAsDefault
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FiltersScreen(
    uiState: FiltersUiState,
    onSetShowCalendar: (Boolean) -> Unit,
    onSetFilter: (Filter) -> Unit,
    onDeleteFilter: (Filter) -> Unit,
    onBack: () -> Unit,
    onSelectComposedFilter: (ComposedFilter) -> Unit,
    onCreateFilter: () -> Unit,
    setAsDefault: () -> Unit,
    filterNameInputState: TextFieldState,
    onReset: () -> Unit,
    onDeleteComposedFilter: (ComposedFilter) -> Unit,
) {
    val context = LocalContext.current

    PickPeriodCalendar(
        show = uiState.showCalendar,
        onDismissRequest = { onSetShowCalendar(false) },
        onSetFilter = onSetFilter
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title =  {
                        Text(
                            text = stringResource(R.string.filters),
                            style = AppTheme.typography.title3,
                            color = AppTheme.colorScheme.foreground1
                        )
                    },
                    navigationIcon = {
                        FilledIconButton(
                            onClick = onBack,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = AppTheme.colorScheme.foreground3
                            ),
                            shape = AppTheme.shapes.default,
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = onReset,
                            shape = AppTheme.shapes.default
                        ) {
                            Text(
                                text = stringResource(R.string.reset),
                                style = AppTheme.typography.calloutButton,
                                color = AppTheme.colorScheme.foreground
                            )
                        }
                    }
                )
            }
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        val lazyListState = rememberLazyListState()

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Column {
                    var showFilterNameInputDialog by rememberSaveable { mutableStateOf(false) }
                    if (showFilterNameInputDialog) {
                        InputNameDialog(
                            state = filterNameInputState,
                            onDismissRequest = { showFilterNameInputDialog = false },
                            onCreate = { onCreateFilter(); showFilterNameInputDialog = false }
                        )
                    }

                    var menuExpanded by rememberSaveable { mutableStateOf(false) }

                    if (uiState.authedAsGuest) {
                        Box(
                            modifier = Modifier.height(56.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append(context.getString(R.string.signIn_to_save))
                                    append(" ")
                                    withStyle(
                                        SpanStyle(
                                            color = AppTheme.colorScheme.foreground
                                        )
                                    ) {
                                        append(context.getString(R.string.filters))
                                    }
                                },
                                style = AppTheme.typography.body,
                                color = AppTheme.colorScheme.foreground2,
                                modifier = Modifier
                                    .clickable {
                                        runBlocking { AuthRepository.signOut() }
                                    }
                            )
                        }
                    } else {
                        EditFilter(
                            label = stringResource(R.string.current_filter),
                            action = uiState.composedFilter.name,
                            onClick = { menuExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = AppTheme.colorScheme.background2,
                        shape = AppTheme.shapes.default,
                        modifier = Modifier.widthIn(min = 250.dp),
                        offset = DpOffset(x = 32.dp, y = 0.dp)
                    ) {
                        if (uiState.usingExistingFilter) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Save,
                                        contentDescription = null,
                                        tint = AppTheme.colorScheme.foreground
                                    )
                                },
                                text = {
                                    Text(
                                        text = stringResource(R.string.save),
                                        style = AppTheme.typography.calloutButton,
                                        color = AppTheme.colorScheme.foreground1
                                    )
                                },
                                onClick = { onCreateFilter(); menuExpanded = false }
                            )
                        } else {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = AppTheme.colorScheme.foreground
                                    )
                                },
                                text = {
                                    Text(
                                        text = stringResource(R.string.create),
                                        style = AppTheme.typography.calloutButton,
                                        color = AppTheme.colorScheme.foreground1
                                    )
                                },
                                onClick = { showFilterNameInputDialog = true; menuExpanded = false }
                            )
                        }

                        uiState.savedFilters.forEach { composedFilter ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = composedFilter.name,
                                        style = AppTheme.typography.calloutButton,
                                        color = AppTheme.colorScheme.foreground1
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.FilterAlt,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = null,
                                        tint = AppTheme.colorScheme.foregroundError,
                                        modifier = Modifier.clickable {
                                            onDeleteComposedFilter(composedFilter)
                                        }
                                    )
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = AppTheme.colorScheme.foreground1,
                                    leadingIconColor = AppTheme.colorScheme.foreground2
                                ),
                                onClick = {
                                    onSelectComposedFilter(composedFilter)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = uiState.composedFilter.filters.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        HorizontalDivider(color = AppTheme.colorScheme.stroke1)
                        Headline(
                            text = stringResource(R.string.active_filters),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            uiState.composedFilter.filters.forEach { filter ->
                                AppChip(
                                    label = filter.textRepresentation,
                                    onDelete = { onDeleteFilter(filter) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Headline(
                    text = stringResource(R.string.time_interval),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    FilledIconButton(
                        onClick = {
                            onSetShowCalendar(true)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = AppTheme.colorScheme.background2,
                            contentColor = AppTheme.colorScheme.foreground
                        ),
                        shape = AppTheme.shapes.default,
                        modifier = Modifier
                            .boxShadow(shape = AppTheme.shapes.default)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = stringResource(R.string.from_date),
                        style = AppTheme.typography.callout,
                        color = AppTheme.colorScheme.foreground2
                    )
                    val (startDate, endDate) = remember(uiState.composedFilter) {
                        val filter = uiState.getFilter<Filter.Date>()

                        filter?.startDate?.toJavaLocalDate()?.print("dd.MM.yyyy") to
                                filter?.endDate?.toJavaLocalDate()?.print("dd.MM.yyyy")
                    }
                    var datePicking by remember { mutableStateOf(null as Int?) }

                    val singleDatePicker = rememberUseCaseState()

                    LaunchedEffect(datePicking) {
                        if (datePicking != null) {
                            singleDatePicker.show()
                        }
                    }
                    CalendarTheme {
                        CalendarDialog(
                            state = singleDatePicker,
                            selection = CalendarSelection.Date { date ->
                                val filter = uiState.getFilter<Filter.Date>()
                                if (datePicking == 1) {
                                    onSetFilter(Filter.Date(
                                        startDate = date.toKotlinLocalDate(),
                                        endDate = filter?.endDate
                                    ))
                                } else if (datePicking == 2) {
                                    onSetFilter(Filter.Date(
                                        endDate = date.toKotlinLocalDate(),
                                        startDate = filter?.startDate
                                    ))
                                }
                            }
                        )
                    }

                    FakeTextField(
                        placeholder = startDate ?: "",
                        onClick = { datePicking = 1 },
                        trailingIcon = {},
                        textColor = AppTheme.colorScheme.foreground1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.to_date),
                        style = AppTheme.typography.callout,
                        color = AppTheme.colorScheme.foreground2
                    )
                    FakeTextField(
                        placeholder = endDate ?: "",
                        onClick = { datePicking = 2 },
                        trailingIcon = {},
                        textColor = AppTheme.colorScheme.foreground1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Headline(
                    text = stringResource(R.string.members_count_headline),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val memberRange = remember(uiState.composedFilter.filters) {
                    val memberRange = uiState.getFilter<Filter.MemberRange>()

                    runCatching {
                        memberRange!!.start.toFloat()..memberRange.end
                            .coerceAtMost(100).toFloat()
                    }.getOrDefault(1f..100f)
                }

                Column {
                    RangeSlider(
                        valueRange = 1f..100f,
                        value = memberRange,
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.colorScheme.foreground,
                        ),
                        onValueChange = { range ->
                            val end = if (range.endInclusive == 100f) {
                                5000
                            } else range.endInclusive.roundToInt()
                            onSetFilter(Filter.MemberRange(start = range.start.roundToInt(), end = end))
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = memberRange.start.roundToInt().toString(),
                            style = AppTheme.typography.body,
                            color = AppTheme.colorScheme.foreground2
                        )
                        Text(
                            text = memberRange.endInclusive.roundToInt().toString(),
                            style = AppTheme.typography.body,
                            color = AppTheme.colorScheme.foreground2
                        )
                    }
                }
            }

            fun search(
                @StringRes titleResId: Int,
                checkedIds: List<Int>,
                filterType: FilterType,
            ) {
                item {
                    Headline(
                        text = stringResource(titleResId),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    DropDownMenuWithSearch(
                        checkedIds = checkedIds,
                        onSetFilter = onSetFilter,
                        onDeleteFilter = onDeleteFilter,
                        onQuery = { q ->
                            if (q.trim().isEmpty()) return@DropDownMenuWithSearch emptyList()
                            when (filterType) {
                                FilterType.SEX -> ServiceApi.searchFilter<SexFilter>(filterType, q)
                                FilterType.TEAM -> ServiceApi.searchFilter<TeamFilter>(filterType, q)
                                FilterType.AGE -> ServiceApi.searchFilter<AgeFilter>(filterType, q)
                                FilterType.CALENDAR_SPORT -> ServiceApi.searchFilter<CalendarSportFilter>(filterType, q)
                                FilterType.CALENDAR_SPORT_TYPE -> ServiceApi.searchFilter<CalendarSportTypeFilter>(filterType, q)
                                FilterType.DISCIPLINE -> ServiceApi.searchDisc(q)
                                FilterType.PROGRAM -> ServiceApi.searchFilter<ProgramInfoFilter>(filterType, q)
                                else -> Either.Success(emptyList())
                            }.getOrElse { emptyList() }
                        }
                    )
                }
            }

            search(
                titleResId = R.string.program,
                checkedIds = uiState.getCheckedIds<Filter.Program>(),
                filterType = FilterType.PROGRAM
            )

            search(
                titleResId = R.string.discipline,
                checkedIds = uiState.getCheckedIds<Filter.Discipline>(),
                filterType = FilterType.DISCIPLINE
            )

            search(
                titleResId = R.string.sex_filter,
                checkedIds = uiState.getCheckedIds<Filter.Sex>(),
                filterType = FilterType.SEX
            )

            search(
                titleResId = R.string.age_filter,
                checkedIds = uiState.getCheckedIds<Filter.Age>(),
                filterType = FilterType.AGE
            )

            search(
                titleResId = R.string.sport_type,
                checkedIds = uiState.getCheckedIds<Filter.SportType>(),
                filterType = FilterType.CALENDAR_SPORT_TYPE
            )

            search(
                titleResId = R.string.sport,
                checkedIds = uiState.getCheckedIds<Filter.Sport>(),
                filterType = FilterType.CALENDAR_SPORT
            )

            search(
                titleResId = R.string.team,
                checkedIds = uiState.getCheckedIds<Filter.Team>(),
                filterType = FilterType.TEAM
            )

            item {
                Spacer(Modifier.height(128.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickPeriodCalendar(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSetFilter: (Filter) -> Unit,
) {
    val state = rememberUseCaseState(onDismissRequest = { onDismissRequest() })

    LaunchedEffect(show) {
        if (show) {
            state.show()
        } else state.hide()
    }

    CalendarTheme {
        CalendarDialog(
            state = state,
            selection = CalendarSelection.Period { startDate, endDate ->
                onSetFilter(
                    Filter.Date(
                        startDate = startDate.toKotlinLocalDate(),
                        endDate = endDate.toKotlinLocalDate()
                    )
                )
            },
            config = CalendarConfig(
                yearSelection = true,
                style = CalendarStyle.MONTH
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNameDialog(
    state: TextFieldState,
    onDismissRequest: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Surface(
            shape = AppTheme.shapes.large,
            color = AppTheme.colorScheme.background1
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter_name),
                    style = AppTheme.typography.title3,
                    color = AppTheme.colorScheme.foreground1
                )
                AppTextField(
                    state = state,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    placeholder = stringResource(R.string.filter_name)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(
                        onClick = { onDismissRequest(); state.clearText() }
                    ) {
                        Text(
                            text = stringResource(android.R.string.cancel),
                            style = AppTheme.typography.calloutButton,
                            color = AppTheme.colorScheme.foregroundError
                        )
                    }
                    TextButton(
                        onClick = { onCreate(); state.clearText() }
                    ) {
                        Text(
                            text = stringResource(android.R.string.ok),
                            style = AppTheme.typography.calloutButton,
                            color = AppTheme.colorScheme.foreground
                        )
                    }
                }
            }
        }
    }
}