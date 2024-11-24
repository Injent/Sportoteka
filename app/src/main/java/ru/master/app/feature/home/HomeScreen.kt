package ru.master.app.feature.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ahmad_hamwi.compose.pagination.PaginatedLazyColumn
import io.github.ahmad_hamwi.compose.pagination.rememberPaginationState
import javatimefun.localdate.extensions.print
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import ru.master.app.R
import ru.master.app.data.filters.util.ReservedFilters
import ru.master.app.feature.home.component.SportEventCard
import ru.master.app.model.GeoPoint
import ru.master.app.model.SportEvent
import ru.master.app.navigation.ObserveAsEvents
import ru.master.app.navigation.Screen
import ru.master.app.network.ServiceApi
import ru.master.app.ui.component.AppChip
import ru.master.app.ui.component.FakeTextField
import ru.master.app.ui.component.Headline
import ru.master.app.ui.theme.AppTheme
import ru.master.app.util.boxShadow
import ru.master.app.util.eventbus.EventBus
import ru.master.app.util.eventbus.GlobalEvent


fun NavGraphBuilder.homeRoute() {
    composable<Screen.Home> {
        HomeRoute()
    }
}

@Composable
fun HomeRoute() {
    val viewModel = koinViewModel<HomeViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val staredEventIds by viewModel.staredEventIds.collectAsStateWithLifecycle()

    HomeRouteScreen(
        onFiltersClick = viewModel::onFiltersClick,
        selectedEvent = selectedEvent,
        onSelectEvent = viewModel::onSelectEvent,
        onCloseSheet = viewModel::onCloseSheet,
        staredEventIds = staredEventIds,
        onStar = viewModel::onStar,
        onOpenMap = viewModel::onGeoCode
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HomeRouteScreen(
    onFiltersClick: () -> Unit,
    selectedEvent: SportEvent?,
    onSelectEvent: (SportEvent) -> Unit,
    staredEventIds: List<Long>,
    onCloseSheet: () -> Unit,
    onStar: (SportEvent) -> Unit,
    onOpenMap: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val paginationState = rememberPaginationState(
        initialPageKey = 1,
        onRequestPage = { pageKey ->
            scope.launch(Dispatchers.IO) {
                ServiceApi.getSportEvents(
                    filters = ReservedFilters.appliedFilters,
                    page = pageKey,
                )
                    .onFailure {
                        setError(IllegalStateException())
                    }
                    .onSuccess {
                        appendPage(
                            items = it,
                            nextPageKey = pageKey + 1,
                            isLastPage = it.size < 10
                        )
                    }
            }
        }
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        FakeTextField(
                            placeholder = stringResource(android.R.string.search_go),
                            onClick = onFiltersClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = AppTheme.colorScheme.foreground2
                                )
                            }
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = onFiltersClick
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Tune,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.foreground2
                            )
                        }
                    }
                )
            }
        },
        modifier = Modifier.padding(bottom = 56.dp)
    ) { innerPadding ->
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        if (selectedEvent != null) {
            ModalBottomSheet(
                sheetState = bottomSheetState,
                onDismissRequest = onCloseSheet,
                shape = AppTheme.shapes.large,
                containerColor = AppTheme.colorScheme.background2
            ) {
                SportEventContent(
                    event = selectedEvent,
                    onOpenMap = onOpenMap
                )
            }
        }

        PaginatedLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            paginationState = paginationState,
            firstPageProgressIndicator = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            newPageProgressIndicator = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            firstPageErrorIndicator = {
                Text(
                    text = "Ошибка"
                )
            },
            newPageErrorIndicator = {
                Text(
                    text = "Ошибка"
                )
            }
        ) {
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ReservedFilters.appliedFilters.forEach { filter ->
                        AppChip(
                            label = filter.textRepresentation,
                            onDelete = {
                                ReservedFilters.appliedFilters.remove(filter)
                                paginationState.refresh(initialPageKey = 1)
                            }
                        )
                    }
                }
            }
            item {
                if (paginationState.allItems?.isEmpty() == true) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.empty),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.nothingIsFound),
                            style = AppTheme.typography.headline1,
                            color = AppTheme.colorScheme.foreground1
                        )
                    }
                }
            }
            itemsIndexed(
                items = paginationState.allItems!!,
                key = { index, _ -> index }
            ) { _, event ->
                SportEventCard(
                    title = event.eventName,
                    date = remember(event) {
                        event.dateFrom.toJavaLocalDate().print("dd.MM.yyyy") +
                                "-${event.dateTo.toJavaLocalDate().print("dd.MM.yyyy")}"
                    },
                    sport = event.sportName,
                    type = event.teamName,
                    memberCount = event.memberCount,
                    city = event.location,
                    onStar = { onStar(event) },
                    hasStar = event.id in staredEventIds,
                    onClick = { onSelectEvent(event) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun SportEventContent(
    event: SportEvent,
    onOpenMap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    ObserveAsEvents(EventBus.events) { e ->
        if (e is GlobalEvent.OpenMap) {
            context.openMap(e.geoPoint)
        }
    }
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = event.eventName,
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foreground1
        )
        Text(
            text = event.sportName,
            style = AppTheme.typography.body,
            color = AppTheme.colorScheme.foreground2
        )

        Text(
            text = stringResource(R.string.info),
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )

        event.description?.let {
            Headline(
                text = stringResource(R.string.description),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = it,
                style = AppTheme.typography.body,
                color = AppTheme.colorScheme.foreground1
            )
        }

        event.programs.takeIf { it.isNotEmpty() }?.let {
            Headline(
                text = stringResource(R.string.program),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppTheme.shapes.default)
            ) {
                it.forEachIndexed { index, program ->
                    Text(
                        text = program.replace("\n", ""),
                        style = AppTheme.typography.callout,
                        color = AppTheme.colorScheme.foreground2,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (index % 2 == 0) {
                                    AppTheme.colorScheme.foreground3.copy(.15f)
                                } else AppTheme.colorScheme.foreground3.copy(.25f)
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .then(
                                if (index == 0) {
                                    Modifier.padding(top = 4.dp)
                                } else if (index + 1 == it.size) {
                                    Modifier.padding(bottom = 4.dp)
                                } else Modifier
                            )
                    )
                }
            }
        }

        event.disciplines.takeIf { it.isNotEmpty() }?.let {
            Headline(
                text = stringResource(R.string.discipline),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppTheme.shapes.default)
            ) {
                it.forEachIndexed { index, program ->
                    Text(
                        text = program.replace("\n", " "),
                        style = AppTheme.typography.callout,
                        color = AppTheme.colorScheme.foreground2,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (index % 2 == 0) {
                                    AppTheme.colorScheme.foreground3.copy(.15f)
                                } else AppTheme.colorScheme.foreground3.copy(.25f)
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .then(
                                if (index == 0) {
                                    Modifier.padding(top = 4.dp)
                                } else if (index + 1 == it.size) {
                                    Modifier.padding(bottom = 4.dp)
                                } else Modifier
                            )
                    )
                }
            }
        }

        event.performer?.let {
            Headline(
                text = stringResource(R.string.performer),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = it,
                style = AppTheme.typography.body,
                color = AppTheme.colorScheme.foreground1
            )
        }

        Headline(
            text = stringResource(R.string.members_count_headline),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = event.memberCount.toString(),
            style = AppTheme.typography.body,
            color = AppTheme.colorScheme.foreground1
        )

        event.teamName?.let {
            Headline(
                text = stringResource(R.string.team),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = it,
                style = AppTheme.typography.body,
                color = AppTheme.colorScheme.foreground1
            )
        }

        Column {
            Headline(
                text = stringResource(R.string.location),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = event.location.replace("\n", " "),
                    style = AppTheme.typography.body,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier.weight(1f)
                )

                FilledIconButton(
                    onClick = { onOpenMap(event.location) },
                    shape = AppTheme.shapes.default,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = AppTheme.colorScheme.background1
                    ),
                    modifier = Modifier.boxShadow(shape = AppTheme.shapes.default)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = AppTheme.colorScheme.foreground
                    )
                }
            }
        }

        Headline(
            text = stringResource(R.string.ekp),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = event.ekp,
            style = AppTheme.typography.body,
            color = AppTheme.colorScheme.foreground1
        )
    }
}

private fun Context.openMap(geoPoint: GeoPoint) {
    val long = geoPoint.latitude
    val lati = geoPoint.longitude

    val url = "yandexnavi://show_point_on_map?lat=$lati&lon=$long&zoom=12&no-balloon=0"
    val intentYandex = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intentYandex.setPackage("ru.yandex.yandexmaps")

    val uriGoogle = "geo:$lati,$long"
    val intentGoogle = Intent(Intent.ACTION_VIEW, Uri.parse(uriGoogle))

    val chooserIntent = Intent.createChooser(intentGoogle, "")
    chooserIntent.putExtra(
        Intent.EXTRA_INITIAL_INTENTS,
        listOf(intentYandex).toTypedArray<Parcelable>()
    )
    startActivity(chooserIntent)
}