package com.ibashkimi.telegram.ui.chat

import android.widget.Toast
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.state
import androidx.ui.core.*
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Card
import androidx.ui.unit.dp
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.TdRequest
import com.ibashkimi.telegram.data.TdResult
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.ui.VectorImageButton
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatScreen(chat: TdApi.Chat) {
    val history = TelegramClient.getMessages(chat.id)
    Column(modifier = LayoutWidth.Fill) {
        ChatHistory(history = history, modifier = LayoutFlexible(1f))
        val context = ambient(ContextAmbient)
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
                            Padding(left = 16.dp, top = 8.dp, right = 16.dp, bottom = 8.dp) {
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
private fun MessageItem(message: TdApi.Message) {
    when (val content = message.content) {
        is TdApi.MessageText -> TextMessage(content)
        is TdApi.MessageVideo -> VideoMessage(content)
        is TdApi.MessageCall -> CallMessage(content)
        is TdApi.MessageAudio -> AudioMessage(content)
        is TdApi.MessageSticker -> StickerMessage(content)
        is TdApi.MessageAnimation -> AnimationMessage(content)
        else -> Text(message::class.java.simpleName)
    }
}

@Composable
fun MessageInput(onEnter: (String) -> Unit) {
    val input = state { EditorModel("") }
    Card(elevation = 4.dp, modifier = LayoutWidth.Fill) {
        Container(padding = EdgeInsets(16.dp)) {
            Row(arrangement = Arrangement.End) {
                Column(arrangement = Arrangement.Center, modifier = LayoutFlexible(1f)) {
                    TextField(
                            value = input.value,
                            onValueChange = { input.value = it },
                            textStyle = ((MaterialTheme.typography()).body1)
                    )
                    Divider(color = ((MaterialTheme.colors()).onBackground), modifier = LayoutWidth.Fill)
                }
                Spacer(modifier = LayoutPadding(right = 16.dp))
                VectorImageButton(id = R.drawable.ic_send, onClick = {
                    onEnter(input.value.text)
                })
            }
        }
    }
}