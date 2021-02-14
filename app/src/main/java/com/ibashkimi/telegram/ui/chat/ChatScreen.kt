package com.ibashkimi.telegram.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Repository
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ChatScreen(
    repository: Repository,
    chatId: Long,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val chat = repository.chats.getChat(chatId).collectAsState(null)
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(chat.value?.title ?: "", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Image(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                        )
                    }
                })
        },
        bodyContent = {
            ChatContent(chatId, repository)
        }
    )
}

@Composable
fun ChatContent(chatId: Long, repository: Repository, modifier: Modifier = Modifier) {
    val history = remember {
        Pager(PagingConfig(pageSize = 30)) {
            repository.messages.getMessagesPaged(chatId)
        }.flow
    }.cachedIn(rememberCoroutineScope()).collectAsLazyPagingItems()

    Column(modifier = modifier.fillMaxWidth()) {
        ChatHistory(
            repository,
            messages = history,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        )
        val input = remember { mutableStateOf(TextFieldValue("")) }
        val scope = rememberCoroutineScope()
        MessageInput(
            input = input,
            insertGif = {
                // TODO
            }, attachFile = {
                // todo
            }, sendMessage = {
                scope.launch {
                    repository.messages.sendMessage(
                        chatId = chatId,
                        inputMessageContent = TdApi.InputMessageText(
                            TdApi.FormattedText(
                                it,
                                emptyArray()
                            ), false, false
                        )
                    ).await()
                    input.value = TextFieldValue()
                    history.refresh()
                }
            })
    }
}

@Composable
fun ChatLoading(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.loading),
        style = MaterialTheme.typography.h5,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun ChatHistory(
    repository: Repository,
    messages: LazyPagingItems<TdApi.Message>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, reverseLayout = true) {
        when {
            messages.loadState.refresh == LoadState.Loading -> {
                item {
                    ChatLoading()
                }
            }
            messages.loadState.refresh is LoadState.Error -> {
                item {
                    Text(
                        text = "Cannot load messages",
                        style = MaterialTheme.typography.h5,
                        modifier = modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
            messages.loadState.refresh is LoadState.NotLoading && messages.itemCount == 0 -> {
                item {
                    Text("Empty")
                }
            }
        }
        items(messages) { message ->
            message?.let { MessageItem(repository, it) }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun MessageItem(
    repository: Repository,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.clickable(onClick = {}) then modifier.fillMaxWidth()
    ) {
        val userPhoto =
            repository.users.getUser((message.sender as TdApi.MessageSenderUser).userId)
            .collectAsState(null, Dispatchers.IO)
        val imageModifier = Modifier
            .padding(16.dp)
            .size(40.dp)
            .clip(shape = CircleShape)
        userPhoto.value?.profilePhoto?.small?.local?.path?.let {
            CoilImage(
                data = it,
                contentDescription = null,
                modifier = imageModifier,
            )
        } ?: Box(imageModifier)
        Card(
            elevation = 1.dp,
            modifier = Modifier.padding(0.dp, 4.dp, 8.dp, 4.dp)
        ) {
            val messageModifier = Modifier.padding(8.dp)
            when (val content = message.content) {
                is TdApi.MessageText -> TextMessage(content, messageModifier)
                is TdApi.MessageVideo -> VideoMessage(content, messageModifier)
                is TdApi.MessageCall -> CallMessage(content, messageModifier)
                is TdApi.MessageAudio -> AudioMessage(content, messageModifier)
                is TdApi.MessageSticker -> StickerMessage(content, messageModifier)
                is TdApi.MessageAnimation -> AnimationMessage(content, messageModifier)
                else -> Text(message::class.java.simpleName)
            }
        }
    }
}

@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    input: MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue("")) },
    insertGif: () -> Unit = {},
    attachFile: () -> Unit = {},
    sendMessage: (String) -> Unit = {}
) {
    Surface(modifier, color = MaterialTheme.colors.surface, elevation = 6.dp) {
        TextField(
            value = input.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { input.value = it },
            textStyle = MaterialTheme.typography.body1,
            placeholder = {
                Text("Message")
            },
            leadingIcon = {
                IconButton(onClick = insertGif) {
                    Image(
                        imageVector = Icons.Default.Gif,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                    )
                }
            },
            trailingIcon = {
                if (input.value.text.isEmpty()) {
                    Row {
                        IconButton(onClick = attachFile) {
                            Image(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                            )
                        }
                        IconButton(onClick = { }) {
                            Image(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                            )
                        }
                    }
                } else {
                    IconButton(onClick = { sendMessage(input.value.text) }) {
                        Image(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
                        )
                    }
                }
            },
            backgroundColor = MaterialTheme.colors.surface
        )
    }
}