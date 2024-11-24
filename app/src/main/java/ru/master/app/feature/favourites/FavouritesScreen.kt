package ru.master.app.feature.favourites

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME
import android.provider.CalendarContract.EXTRA_EVENT_END_TIME
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import javatimefun.localdate.extensions.atEndOfDay
import javatimefun.localdate.extensions.print
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import ru.master.app.R
import ru.master.app.feature.home.SportEventContent
import ru.master.app.feature.home.component.SportEventCard
import ru.master.app.model.SportEvent
import ru.master.app.navigation.Screen
import ru.master.app.ui.theme.AppTheme
import java.time.ZoneOffset

fun NavGraphBuilder.favouritesRoute() {
    composable<Screen.Favourites> {
        FavouritesRoute()
    }
}

@Composable
fun FavouritesRoute() {
    val viewModel = koinViewModel<FavouritesViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

    val events by viewModel.favouritesEvents.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()

    FavouritesRouteScreen(
        events = events,
        onRemove = viewModel::removeEvent,
        selectedEvent = selectedEvent,
        onSelect = viewModel::onSelect,
        onCloseSheet = viewModel::onCloseSheet,
        onOpenMap = viewModel::onOpenMap
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouritesRouteScreen(
    events: List<SportEvent>,
    onRemove: (SportEvent) -> Unit,
    onSelect: (SportEvent) -> Unit,
    selectedEvent: SportEvent?,
    onCloseSheet: () -> Unit,
    onOpenMap: (String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favourites),
                        style = AppTheme.typography.title3,
                        color = AppTheme.colorScheme.foreground1
                    )
                }
            )
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
                Column {
                    SportEventContent(
                        event = selectedEvent,
                        onOpenMap = onOpenMap
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { context.addCalendarEvent(selectedEvent) },
                        shape = AppTheme.shapes.default,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colorScheme.backgroundBrand
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.mark_in_calendar),
                            style = AppTheme.typography.calloutButton,
                            color = AppTheme.colorScheme.foregroundOnBrand
                        )
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                if (events.isEmpty()) {
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

            items(
                items = events,
                key = { it.id }
            ) { event ->
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
                    onClick = { onSelect(event) },
                    hasStar = true,
                    onStar = { onRemove(event) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

private fun Context.addCalendarEvent(event: SportEvent) {
    val intent = Intent(Intent.ACTION_EDIT).apply {
        type = "vnd.android.cursor.item/event"

        putExtra(CalendarContract.EXTRA_EVENT_ID, event.id)
        putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
        putExtra(EXTRA_EVENT_BEGIN_TIME, event.dateFrom.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())
        putExtra(CalendarContract.Events.ALL_DAY, true)
        putExtra(EXTRA_EVENT_END_TIME, event.dateTo.toJavaLocalDate().atEndOfDay().toInstant(
            ZoneOffset.UTC).toEpochMilli())
        putExtra(CalendarContract.Events.TITLE, event.eventName)
        event.description?.let { putExtra(CalendarContract.Events.DESCRIPTION, it) }
    }
    startActivity(intent)
}