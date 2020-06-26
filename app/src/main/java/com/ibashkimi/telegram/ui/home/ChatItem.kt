package com.ibashkimi.telegram.ui.home

import android.text.format.DateUtils
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Mic
import androidx.ui.material.icons.filled.Videocam
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.ui.NetworkImage
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        maxLines = 1,
        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.W500)
    )
}

@Composable
fun ChatSummary(chat: TdApi.Chat, modifier: Modifier = Modifier) {
    chat.lastMessage?.content?.let {
        when (it.constructor) {
            TdApi.MessageText.CONSTRUCTOR -> BasicChatSummary(
                text = (it as TdApi.MessageText).text.text,
                modifier = modifier
            )
            TdApi.MessageVideo.CONSTRUCTOR -> HighlightedChatSummary("Video", modifier = modifier)
            TdApi.MessageCall.CONSTRUCTOR -> HighlightedChatSummary("Call", modifier = modifier)
            TdApi.MessageAudio.CONSTRUCTOR -> {
                val message = it as TdApi.MessageAudio
                Row(modifier = modifier) {
                    Image(
                        asset = Icons.Default.Mic,
                        alignment = Alignment.Center,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                    )
                    Text(
                        text = message.audio.duration.toTime(),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            TdApi.MessageSticker.CONSTRUCTOR -> BasicChatSummary(
                (it as TdApi.MessageSticker).sticker.emoji + " Sticker",
                modifier = modifier
            )
            TdApi.MessageAnimation.CONSTRUCTOR -> HighlightedChatSummary("GIF", modifier = modifier)
            TdApi.MessageLocation.CONSTRUCTOR -> HighlightedChatSummary(
                "Location",
                modifier = modifier
            )
            TdApi.MessageVoiceNote.CONSTRUCTOR -> {
                val message = it as TdApi.MessageVoiceNote
                Row(modifier = modifier) {
                    Image(
                        asset = Icons.Default.Mic,
                        alignment = Alignment.Center,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                    )
                    Text(
                        text = message.voiceNote.duration.toTime(),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            TdApi.MessageVideoNote.CONSTRUCTOR -> {
                val message = it as TdApi.MessageVideoNote
                Row(modifier = modifier) {
                    Image(
                        asset = Icons.Default.Videocam,
                        alignment = Alignment.Center,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                    )
                    Text(
                        text = message.videoNote.duration.toTime(),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            TdApi.MessageContactRegistered.CONSTRUCTOR -> HighlightedChatSummary(
                "Joined Telegram!",
                modifier = modifier
            )
            TdApi.MessageChatDeleteMember.CONSTRUCTOR -> HighlightedChatSummary(
                "${(it as TdApi.MessageChatDeleteMember).userId} left the chat",
                modifier = modifier
            )
            else -> it::class.java.simpleName
        }
    }
}

@Composable
fun BasicChatSummary(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle1,
        maxLines = 2,
        modifier = modifier
    )
}

@Composable
fun HighlightedChatSummary(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle1,
        color = MaterialTheme.colors.primaryVariant,
        maxLines = 2,
        modifier = modifier
    )
}

@Composable
fun ChatTime(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.caption,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun ChatItem(repository: Repository, chat: TdApi.Chat, modifier: Modifier = Modifier) {
    Row(verticalGravity = Alignment.CenterVertically) {
        val imageModifier = Modifier.size(48.dp).clip(shape = CircleShape)

        val chatPhoto =
            repository.chats.chatImage(chat)
                .collectAsState(initial = chat.photo?.small?.local?.path)
        NetworkImage(
            url = chatPhoto.value,
            modifier = imageModifier,
            placeHolderRes = R.drawable.ic_person
        )
        Column(modifier = modifier.fillMaxWidth()) {
            Row(verticalGravity = Alignment.CenterVertically) {
                ChatTitle(chat.title, modifier = Modifier.weight(1.0f))
                chat.lastMessage?.date?.toLong()?.let { it * 1000 }?.let {
                    ChatTime(it.toRelativeTimeSpan(), modifier = Modifier.drawOpacity(0.6f))
                }
            }
            ChatSummary(chat, modifier = Modifier.drawOpacity(0.6f))
        }
    }
}

private fun Long.toRelativeTimeSpan(): String =
    DateUtils.getRelativeTimeSpanString(
        this,
        System.currentTimeMillis(),
        DateUtils.SECOND_IN_MILLIS
    ).toString()

private fun Int.toTime(): String {
    val duration = this.toLong()
    val hours: Long = (duration / (60 * 60))
    val minutes = (duration % (60 * 60) / (60))
    val seconds = (duration % (60 * 60) % (60))
    return when {
        minutes == 0L && hours == 0L -> String.format("0:%02d", seconds)
        hours == 0L -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

@Composable
fun ClickableChatItem(
    repository: Repository,
    chat: TdApi.Chat,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ChatItem(
        repository,
        chat,
        modifier = modifier.clickable(onClick = onClick).padding(16.dp, 8.dp, 16.dp, 8.dp)
    )
}
