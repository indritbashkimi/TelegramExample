package com.ibashkimi.telegram.ui.home

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatTitle(text: String, modifier: Modifier = Modifier.None) {
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
fun ChatItem(chat: TdApi.Chat) {
    Log.d("ChatItem", "chat: $chat")
    Column(modifier = Modifier.fillMaxWidth() + Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp)) {
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

private fun Long.toRelativeTimeSpan(): String =
    DateUtils.getRelativeTimeSpanString(
        this,
        System.currentTimeMillis(),
        DateUtils.SECOND_IN_MILLIS
    ).toString()


@Composable
fun ClickableChatItem(chat: TdApi.Chat, onClick: () -> Unit = {}) {
    Clickable(onClick = onClick, modifier = Modifier.ripple()) {
        ChatItem(chat)
    }
}
