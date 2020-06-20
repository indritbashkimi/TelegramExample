package com.ibashkimi.telegram.ui.home

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.ui.NetworkImage
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.W500)
    )
}

@Composable
fun ChatSummary(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle1,
        maxLines = 2,
        modifier = Modifier.drawOpacity(0.6f)
    )
}

@Composable
fun ChatTime(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.caption,
        maxLines = 1,
        modifier = Modifier.drawOpacity(0.6f)
    )
}

@Composable
fun ChatItem(repository: ChatsRepository, chat: TdApi.Chat, modifier: Modifier = Modifier) {
    Log.d("ChatItem", "chat: $chat")
    Row(verticalGravity = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
        val imageModifier = Modifier.size(48.dp).clickable(onClick = {}).clip(shape = CircleShape)

        val chatPhoto = repository.chatImage(chat).collectAsState(initial = null)
        NetworkImage(
            url = chatPhoto.value,
            modifier = imageModifier,
            placeHolderRes = R.drawable.ic_person
        )
        Column(
            modifier = Modifier.fillMaxWidth() + modifier
        ) {
            val content: String = chat.lastMessage?.content?.let {
                when (it.constructor) {
                    TdApi.MessageText.CONSTRUCTOR -> {
                        (it as TdApi.MessageText).text.text
                    }
                    TdApi.MessageVideo.CONSTRUCTOR -> {
                        "Video"
                    }
                    TdApi.MessageCall.CONSTRUCTOR -> "Call"
                    TdApi.MessageAudio.CONSTRUCTOR -> "Audio"
                    TdApi.MessageSticker.CONSTRUCTOR -> (it as TdApi.MessageSticker).sticker.emoji + " Sticker"
                    TdApi.MessageAnimation.CONSTRUCTOR -> "GIF"
                    else -> it::class.java.simpleName
                }
            } ?: ""

            Row {
                ChatTitle(chat.title, modifier = Modifier.fillMaxWidth())
                chat.lastMessage?.date?.toLong()?.let { it * 1000 }?.let {
                    ChatTime(it.toRelativeTimeSpan())
                }
            }
            ChatSummary(content)
        }
    }
}

private fun Long.toRelativeTimeSpan(): String =
    DateUtils.getRelativeTimeSpanString(
        this,
        System.currentTimeMillis(),
        DateUtils.SECOND_IN_MILLIS
    ).toString()


@Composable
fun ClickableChatItem(repository: ChatsRepository, chat: TdApi.Chat, onClick: () -> Unit = {}) {
    ChatItem(
        repository,
        chat,
        modifier = Modifier.clickable(onClick = onClick).padding(16.dp, 8.dp, 16.dp, 8.dp)
    )
}
