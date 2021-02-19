package com.ibashkimi.telegram.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.ui.util.TelegramImage
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun DrawerContent(
    client: TelegramClient,
    newGroup: () -> Unit,
    contacts: () -> Unit,
    calls: () -> Unit,
    savedMessages: () -> Unit,
    settings: () -> Unit
) {
    LazyColumn {
        item {
            DrawerContentHeader(client)
        }
        navigationItem(
            imageVector = Icons.Outlined.People,
            text = "New group",
            onClick = newGroup
        )
        navigationItem(
            imageVector = Icons.Outlined.Person,
            text = "Contacts",
            onClick = contacts
        )
        navigationItem(
            imageVector = Icons.Outlined.Call,
            text = "Calls",
            onClick = calls
        )
        navigationItem(
            imageVector = Icons.Outlined.BookmarkBorder,
            text = "Saved Messages",
            onClick = savedMessages
        )
        navigationItem(
            imageVector = Icons.Outlined.Settings,
            text = "Settings",
            onClick = settings
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerContentHeader(client: TelegramClient) {
    val me = client.send<TdApi.User>(TdApi.GetMe()).collectAsState(initial = null)
    Column(Modifier.background(MaterialTheme.colors.primary)) {
        TelegramImage(
            client,
            me.value?.profilePhoto?.small,
            Modifier
                .padding(16.dp)
                .clip(shape = CircleShape)
                .size(72.dp)
        )
        ListItem(secondaryText = {
            Text(me.value?.phoneNumber ?: "", color = MaterialTheme.colors.onPrimary)
        }) {
            Text(me.value?.firstName ?: "", color = MaterialTheme.colors.onPrimary)
        }
    }
}

private fun LazyListScope.navigationItem(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    item {
        NavigationListItem(imageVector, text, Modifier.clickable(onClick = onClick))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NavigationListItem(
    imageVector: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        ListItem(modifier = modifier,
            icon = {
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription
                )
            },
            text = { Text(text) })
    }
}