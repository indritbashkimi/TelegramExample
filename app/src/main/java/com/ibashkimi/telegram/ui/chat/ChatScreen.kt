package com.ibashkimi.telegram.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
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
import androidx.paging.compose.itemsIndexed
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.ui.util.TelegramImage
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
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
        content = {
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
        itemsIndexed(messages) { i, message ->
            message?.let {
                val userId = (message.sender as TdApi.MessageSenderUser).userId
                val previousMessageUserId =
                    if (i > 0) (messages[i - 1]?.sender as TdApi.MessageSenderUser?)?.userId else null
                MessageItem(
                    isSameUserFromPreviousMessage = userId == previousMessageUserId,
                    repository,
                    it
                )
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun MessageItem(
    isSameUserFromPreviousMessage: Boolean,
    repository: Repository,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    val client = repository.client
    if (message.isOutgoing) {
        Box(
            Modifier.clickable(onClick = {}).fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            MessageItemCard(modifier = Modifier.padding(8.dp, 4.dp, 8.dp, 4.dp)) {
                MessageItemContent(
                    repository.messages.client,
                    message,
                    modifier = Modifier.background(Color.Green.copy(alpha = 0.2f)).padding(8.dp)
                )
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.clickable(onClick = {}) then modifier.fillMaxWidth()
        ) {
            if (!isSameUserFromPreviousMessage) {
                ChatUserIcon(
                    client,
                    (message.sender as TdApi.MessageSenderUser).userId,
                    Modifier.padding(8.dp).clip(shape = CircleShape).size(42.dp)
                )
            } else {
                Box(Modifier.padding(8.dp).size(42.dp))
            }
            MessageItemCard(modifier = Modifier.padding(0.dp, 4.dp, 8.dp, 4.dp)) {
                MessageItemContent(
                    repository.messages.client,
                    message,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun MessageItemCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Card(
    elevation = 2.dp,
    shape = RoundedCornerShape(8.dp),
    modifier = modifier,
    content = content
)

@Composable
private fun MessageItemContent(
    client: TelegramClient,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    when (message.content) {
        is TdApi.MessageText -> TextMessage(message, modifier)
        is TdApi.MessageVideo -> VideoMessage(message, modifier)
        is TdApi.MessageCall -> CallMessage(message, modifier)
        is TdApi.MessageAudio -> AudioMessage(message, modifier)
        is TdApi.MessageSticker -> StickerMessage(client, message, modifier)
        is TdApi.MessageAnimation -> AnimationMessage(client, message, modifier)
        is TdApi.MessagePhoto -> PhotoMessage(client, message, Modifier)
        is TdApi.MessageVideoNote -> VideoNoteMessage(client, message, modifier)
        is TdApi.MessageVoiceNote -> VoiceNoteMessage(message, modifier)
        else -> UnsupportedMessage()
    }
}

@Composable
private fun ChatUserIcon(client: TelegramClient, userId: Int, modifier: Modifier) {
    val user = client.send<TdApi.User>(TdApi.GetUser(userId)).collectAsState(initial = null).value
    TelegramImage(client, user?.profilePhoto?.small, modifier = modifier)
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
                    Icon(
                        imageVector = Icons.Default.Gif,
                        contentDescription = null
                    )
                }
            },
            trailingIcon = {
                if (input.value.text.isEmpty()) {
                    Row {
                        IconButton(onClick = attachFile) {
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null
                            )
                        }
                    }
                } else {
                    IconButton(onClick = { sendMessage(input.value.text) }) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = null
                        )
                    }
                }
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
        )
    }
}