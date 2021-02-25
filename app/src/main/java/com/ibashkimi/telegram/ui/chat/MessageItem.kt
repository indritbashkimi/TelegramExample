package com.ibashkimi.telegram.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.ui.util.TelegramImage
import dev.chrisbanes.accompanist.coil.CoilImage
import org.drinkless.td.libcore.telegram.TdApi
import java.io.File
import java.util.*

@Composable
fun TextMessage(message: TdApi.Message, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        TextMessage(message.content as TdApi.MessageText)
        MessageStatus(message)
    }
}

@Composable
private fun TextMessage(content: TdApi.MessageText, modifier: Modifier = Modifier) {
    Text(text = content.text.text, modifier = modifier)
}

@Composable
fun AudioMessage(message: TdApi.Message, modifier: Modifier = Modifier) {
    val content = message.content as TdApi.MessageAudio
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text(text = "Audio ${content.audio.duration}", modifier = modifier)
        content.caption.text.takeIf { it.isNotBlank() }?.let {
            Text(it)
        }
        MessageStatus(message)
    }
}

@Composable
fun VideoMessage(message: TdApi.Message, modifier: Modifier = Modifier) {
    val content = message.content as TdApi.MessageVideo
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text(text = "Video ${content.video.duration}", modifier = modifier)
        content.caption.text.takeIf { it.isNotBlank() }?.let {
            Text(it)
        }
        MessageStatus(message)
    }
}

@Composable
fun StickerMessage(
    client: TelegramClient,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        StickerMessage(client, message.content as TdApi.MessageSticker)
        MessageStatus(message)
    }
}

@Composable
private fun StickerMessage(
    client: TelegramClient,
    content: TdApi.MessageSticker,
    modifier: Modifier = Modifier
) {
    if (content.sticker.isAnimated) {
        Text(text = "<Animated Sticker> ${content.sticker.emoji}", modifier = modifier)
    } else {
        Box(contentAlignment = Alignment.BottomEnd) {
            TelegramImage(client = client, file = content.sticker.sticker)
            content.sticker.emoji.takeIf { it.isNotBlank() }?.let {
                Text(text = it, modifier = modifier)
            }
        }
    }
}

@Composable
fun AnimationMessage(
    client: TelegramClient,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    val content = message.content as TdApi.MessageAnimation
    val path =
        client.downloadableFile(content.animation.animation).collectAsState(initial = null)
    Column {
        path.value?.let { filePath ->
            CoilImage(data = File(filePath), modifier = Modifier.size(56.dp)) {

            }
        } ?: Text(text = "path null", modifier = modifier)
        Text(text = "path: ${path.value}")
    }
}

@Composable
fun CallMessage(message: TdApi.Message, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        CallMessage(message.content as TdApi.MessageCall)
        MessageStatus(message)
    }
}

@Composable
private fun CallMessage(content: TdApi.MessageCall, modifier: Modifier = Modifier) {
    val msg = when (content.discardReason) {
        is TdApi.CallDiscardReasonHungUp -> {
            "Incoming call"
        }
        is TdApi.CallDiscardReasonDeclined -> {
            "Declined call"
        }
        is TdApi.CallDiscardReasonDisconnected -> {
            "Call disconnected"
        }
        is TdApi.CallDiscardReasonMissed -> {
            "Missed call"
        }
        is TdApi.CallDiscardReasonEmpty -> {
            "Call: Unknown state"
        }
        else -> "Call: Unknown state"
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = msg, modifier = modifier)
        Image(
            imageVector = Icons.Outlined.Call,
            contentDescription = null,
            modifier = Modifier.padding(8.dp).size(18.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
        )
    }
}

@Composable
fun PhotoMessage(client: TelegramClient, message: TdApi.Message, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        PhotoMessage(client, message.content as TdApi.MessagePhoto)
        MessageStatus(message, Modifier.padding(4.dp))
    }
    /*Box(modifier, contentAlignment = Alignment.BottomEnd) {
        PhotoMessage(client, message.content as TdApi.MessagePhoto)
        MessageStatus(message = message, modifier = Modifier.padding(8.dp).background(Color.Magenta))
    }*/
}

@Composable
fun VideoNoteMessage(
    client: TelegramClient,
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text("<Video note>")
        TelegramImage(
            client,
            (message.content as TdApi.MessageVideoNote).videoNote.thumbnail?.file,
            Modifier.size(150.dp)
        )
        MessageStatus(message)
    }
}

@Composable
fun VoiceNoteMessage(
    message: TdApi.Message,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text("<Voice note>")
        (message.content as TdApi.MessageVoiceNote).caption.text.takeIf { it.isNotBlank() }?.let {
            Text(it, Modifier.padding(4.dp, 4.dp, 4.dp, 0.dp))
        }
        MessageStatus(message)
    }
}

@Composable
private fun PhotoMessage(
    client: TelegramClient,
    message: TdApi.MessagePhoto,
    modifier: Modifier = Modifier
) {
    val photo = message.photo.sizes.last()
    val width: Dp = with(LocalDensity.current) {
        photo.width.toDp()
    }
    Column(modifier.width(min(200.dp, width))) {
        TelegramImage(
            client,
            message.photo.sizes.last().photo,
            modifier = Modifier.fillMaxWidth()
        )
        message.caption.text.takeIf { it.isNotEmpty() }
            ?.let { Text(text = it, Modifier.padding(4.dp, 4.dp, 4.dp, 0.dp)) }
    }
}

@Composable
fun UnsupportedMessage(modifier: Modifier = Modifier, title: String? = null) {
    Text(title ?: "<Unsupported message>", modifier = modifier)
}

@Composable
private fun MessageStatus(message: TdApi.Message, modifier: Modifier = Modifier) {
    if (message.isOutgoing) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            MessageTime(message = message)
            MessageSendingState(message.sendingState, modifier.size(16.dp))
        }
    } else {
        MessageTime(message = message, modifier = modifier)
    }
}

@Composable
private fun MessageTime(message: TdApi.Message, modifier: Modifier = Modifier) {
    val date = Date(message.date.toLong())
    val calendar = Calendar.getInstance().apply { time = date }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    MessageTime(text = "$hour:$minute", modifier = modifier.alpha(0.6f))
}

@Composable
private fun MessageTime(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.caption,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
private fun MessageSendingState(
    sendingState: TdApi.MessageSendingState?,
    modifier: Modifier = Modifier
) {
    val colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
    when (sendingState) {
        is TdApi.MessageSendingStatePending -> {
            Image(
                imageVector = Icons.Outlined.Pending,
                contentDescription = null,
                modifier = modifier,
                colorFilter = colorFilter
            )
        }
        is TdApi.MessageSendingStateFailed -> {
            Image(
                imageVector = Icons.Outlined.SyncProblem,
                contentDescription = null,
                modifier = modifier,
                colorFilter = colorFilter
            )
        }
        else -> {
            Image(
                imageVector = Icons.Outlined.Done,
                contentDescription = null,
                modifier = modifier,
                colorFilter = colorFilter
            )
        }
    }
}
