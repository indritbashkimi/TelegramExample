package com.ibashkimi.telegram.ui.chat

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Send
import androidx.ui.material.ripple.ripple
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.Response
import com.ibashkimi.telegram.data.asResponse
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatScreen(repository: Repository, chat: TdApi.Chat, modifier: Modifier = Modifier) {
    val history = repository.messages.getMessages(chat.id).asResponse().collectAsState()
    when (val response = history.value) {
        null -> {
            ChatLoading()
        }
        is Response.Success -> {
            Column(modifier = modifier + Modifier.fillMaxWidth()) {
                ChatHistory(repository, response.data, modifier = Modifier.fillMaxWidth())
                MessageInput {
                    repository.messages.sendMessage()
                }
            }
        }
        is Response.Error -> {
            Text(
                text = "Cannot load messages",
                style = MaterialTheme.typography.h5,
                modifier = modifier + Modifier.fillMaxSize() + Modifier.wrapContentSize(Alignment.Center)
            )
        }
    }
}

@Composable
fun ChatLoading(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.loading),
        style = MaterialTheme.typography.h5,
        modifier = modifier + Modifier.fillMaxSize() + Modifier.wrapContentSize(Alignment.Center)
    )
}

@Composable
fun ChatHistory(
    repository: Repository,
    messages: List<TdApi.Message>,
    modifier: Modifier = Modifier
) {
    AdapterList(data = messages) {
        MessageItem(
            repository, it, modifier = Modifier.padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 8.dp
            )
        )
    }
}

@Composable
private fun MessageItem(
    repository: Repository,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    when (val content = message.content) {
        is TdApi.MessageText -> TextMessage(content, modifier)
        is TdApi.MessageVideo -> VideoMessage(content, modifier)
        is TdApi.MessageCall -> CallMessage(content, modifier)
        is TdApi.MessageAudio -> AudioMessage(content, modifier)
        is TdApi.MessageSticker -> StickerMessage(content, modifier)
        is TdApi.MessageAnimation -> AnimationMessage(content, modifier)
        else -> Text(message::class.java.simpleName)
    }
}

@Composable
fun MessageInput(modifier: Modifier = Modifier, onEnter: (String) -> Unit) {
    val input = state { TextFieldValue() }
    Card(elevation = 4.dp, modifier = modifier + Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.End) {
            Column(
                modifier = Modifier.fillMaxWidth() + Modifier.padding(16.dp)
            ) {
                TextField(
                    value = input.value,
                    onValueChange = { input.value = it },
                    textStyle = MaterialTheme.typography.body1
                )
                Divider(
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.padding(end = 16.dp))
            Image(
                modifier = Modifier.clickable(onClick = { onEnter(input.value.text) }) + Modifier.ripple(),
                asset = Icons.Default.Send,
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
            )
        }
    }
}