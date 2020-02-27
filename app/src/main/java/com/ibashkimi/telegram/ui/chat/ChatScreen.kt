package com.ibashkimi.telegram.ui.chat

import android.widget.Toast
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Send
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.unit.dp
import com.ibashkimi.telegram.data.TdRequest
import com.ibashkimi.telegram.data.TdResult
import com.ibashkimi.telegram.data.TelegramClient
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatScreen(chat: TdApi.Chat, modifier: Modifier = Modifier.None) {
    val history = TelegramClient.getMessages(chat.id)
    val context = ContextAmbient.current
    Column(modifier = modifier + LayoutWidth.Fill) {
        ChatHistory(history = history, modifier = LayoutFlexible(1f))
        MessageInput {
            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ChatHistory(history: TdRequest<TdApi.Messages>, modifier: Modifier = Modifier.None) {
    Column(modifier = modifier) {
        when (val result = history.result) {
            is TdResult.Loading -> Text("Loading")
            is TdResult.Error -> Text("Error")
            is TdResult.Success -> {
                VerticalScroller {
                    Column {
                        result.result.messages.forEach {
                            Container(
                                modifier = LayoutPadding(
                                    left = 16.dp,
                                    top = 8.dp,
                                    right = 16.dp,
                                    bottom = 8.dp
                                )
                            ) {
                                MessageItem(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageItem(message: TdApi.Message, modifier: Modifier = Modifier.None) {
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
fun MessageInput(modifier: Modifier = Modifier.None, onEnter: (String) -> Unit) {
    val input = state { "" }
    Card(elevation = 4.dp, modifier = modifier + LayoutWidth.Fill) {
        Row(arrangement = Arrangement.End) {
            Column(
                arrangement = Arrangement.End,
                modifier = LayoutFlexible(1f) + LayoutPadding(16.dp)
            ) {
                TextField(
                    value = input.value,
                    onValueChange = { input.value = it },
                    textStyle = ((MaterialTheme.typography()).body1)
                )
                Divider(
                    color = ((MaterialTheme.colors()).onBackground),
                    modifier = LayoutWidth.Fill
                )
            }
            Spacer(modifier = LayoutPadding(right = 16.dp))
            Ripple(bounded = false) {
                Clickable(onClick = { onEnter(input.value) }) {
                    DrawVector(Icons.Default.Send)
                }
            }
        }
    }
}