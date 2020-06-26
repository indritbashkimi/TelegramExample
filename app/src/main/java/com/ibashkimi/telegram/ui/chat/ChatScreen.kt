package com.ibashkimi.telegram.ui.chat

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Send
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.Response
import com.ibashkimi.telegram.data.asResponse
import com.ibashkimi.telegram.ui.NetworkImage
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatScreen(repository: Repository, chat: TdApi.Chat, modifier: Modifier = Modifier) {
    val history = repository.messages.getMessages(chat.id).asResponse().collectAsState()
    when (val response = history.value) {
        null -> {
            ChatLoading(modifier)
        }
        is Response.Success -> {
            Stack(modifier = modifier.fillMaxWidth()) {
                ChatHistory(
                    repository,
                    messages = response.data,
                    modifier = Modifier.fillMaxWidth()
                )
                MessageInput(modifier = Modifier.gravity(Alignment.BottomCenter)) {
                    repository.messages.sendMessage()
                }
            }
        }
        is Response.Error -> {
            Text(
                text = "Cannot load messages",
                style = MaterialTheme.typography.h5,
                modifier = modifier.fillMaxSize().wrapContentSize(Alignment.Center)
            )
        }
    }
}

@Composable
fun ChatLoading(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.loading),
        style = MaterialTheme.typography.h5,
        modifier = modifier.fillMaxSize().wrapContentSize(Alignment.Center)
    )
}

@Composable
fun ChatHistory(
    repository: Repository,
    messages: List<TdApi.Message>,
    modifier: Modifier = Modifier
) {
    AdapterList(data = messages, modifier = modifier) {
        MessageItem(repository, it)
    }
}

@Composable
private fun MessageItem(
    repository: Repository,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    Row(
        verticalGravity = Alignment.Bottom,
        modifier = Modifier.clickable(onClick = {}) + modifier.fillMaxWidth()
    ) {
        val userPhoto = repository.users.getUser(message.senderUserId).collectAsState()
        val imageModifier = Modifier.padding(16.dp).size(40.dp).clip(shape = CircleShape)
        NetworkImage(
            url = userPhoto.value?.profilePhoto?.small?.local?.path,
            modifier = imageModifier,
            placeHolderRes = null
        )
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
fun MessageInput(modifier: Modifier = Modifier, onEnter: (String) -> Unit) {
    Card(elevation = 8.dp, modifier = modifier.fillMaxWidth()) {
        Row(verticalGravity = Alignment.CenterVertically) {
            val input = state { TextFieldValue("Message") }
            TextField(
                value = input.value,
                modifier = Modifier.weight(1.0f) + Modifier.padding(16.dp),
                onValueChange = { input.value = it },
                textStyle = MaterialTheme.typography.body1
            )
            Image(
                modifier = Modifier.clickable(onClick = { onEnter(input.value.text) })
                    .padding(16.dp).clip(CircleShape),
                asset = Icons.Default.Send,
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    }
}