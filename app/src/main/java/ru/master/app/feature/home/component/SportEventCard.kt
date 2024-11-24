package ru.master.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.master.app.R
import ru.master.app.ui.component.AppCard
import ru.master.app.ui.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SportEventCard(
    title: String,
    date: String,
    sport: String,
    type: String?,
    memberCount: Int,
    city: String,
    onClick: () -> Unit,
    hasStar: Boolean,
    onStar: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        onClick = onClick,
        shape = AppTheme.shapes.large,
        contentPadding = PaddingValues(),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = city,
                style = AppTheme.typography.callout,
                color = AppTheme.colorScheme.foreground2,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = date,
                style = AppTheme.typography.callout,
                color = AppTheme.colorScheme.foreground2,
                maxLines = 1,
                modifier = Modifier
            )
        }

        Box(Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { onStar(!hasStar) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            ) {
                Icon(
                    imageVector = if (hasStar) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = if (hasStar) AppTheme.colorScheme.foreground else AppTheme.colorScheme.foreground2
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = AppTheme.typography.headline2,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = sport,
                    style = AppTheme.typography.body,
                    color = AppTheme.colorScheme.foreground2
                )

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    type?.let {
                        Text(
                            text = it,
                            style = AppTheme.typography.footnote,
                            color = AppTheme.colorScheme.foreground1,
                            modifier = Modifier
                                .background(
                                    color = AppTheme.colorScheme.statusExpert.copy(.25f),
                                    shape = CircleShape
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.members_count, memberCount),
                        style = AppTheme.typography.footnote,
                        color = AppTheme.colorScheme.foreground1,
                        modifier = Modifier
                            .background(
                                color = AppTheme.colorScheme.statusAwarded.copy(.25f),
                                shape = CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}