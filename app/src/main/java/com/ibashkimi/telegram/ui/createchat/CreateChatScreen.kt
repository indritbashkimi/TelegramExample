package com.ibashkimi.telegram.ui.createchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.ui.util.TelegramImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.drinkless.td.libcore.telegram.TdApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CreateChatScreen(
    client: TelegramClient,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("New message") },
                navigationIcon = {
                    IconButton(onClick = { navigateUp() }) {
                        Image(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search contact",
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Image(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = "New contact",
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
                )
            }
        },
        bodyContent = {
            val users = client.send<TdApi.Users>(TdApi.GetContacts()).map { result ->
                result.userIds.map { client.send<TdApi.User>(TdApi.GetUser(it)) }
            }.flatMapLatest {
                combine(it) { users -> users.toList() }
            }.map { users -> users.sortedBy { it.firstName } }
            CreateChatContent(client, users)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
private fun CreateChatContent(
    client: TelegramClient,
    users: Flow<List<TdApi.User>>,
    modifier: Modifier = Modifier
) {
    val usersState = users.collectAsState(initial = null)
    LazyColumn(modifier = modifier) {
        item {
            ListItem(icon = {
                Image(imageVector = Icons.Outlined.People, contentDescription = null)
            }) {
                Text("New Group")
            }
        }
        item {
            ListItem(icon = {
                Image(imageVector = Icons.Outlined.Lock, contentDescription = null)
            }) {
                Text("New Secret Chat")
            }
        }
        item {
            ListItem(icon = {
                Image(imageVector = Icons.Outlined.Speaker, contentDescription = null)
            }) {
                Text("New Channel")
            }
        }
        usersState.value?.let { userList ->
            item {
                Divider()
            }
            items(userList) {
                ContactItem(client, it)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactItem(client: TelegramClient, user: TdApi.User, modifier: Modifier = Modifier) {
    ListItem(modifier = modifier,
        icon = {
            TelegramImage(
                client = client,
                file = user.profilePhoto?.small,
                modifier = Modifier.clip(shape = CircleShape).size(42.dp)
            )
        },
        secondaryText = {
            Text(user.username ?: "")
        }
    ) {
        Text(user.run { "$firstName $lastName" })
    }
}
