package com.ibashkimi.telegram.ui.home

import android.util.Log
import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import com.ibashkimi.telegram.*
import com.ibashkimi.telegram.data.TdRequest
import com.ibashkimi.telegram.data.TdResult
import com.ibashkimi.telegram.data.TelegramClient
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun HomeScreen(chatsRequest: TdRequest<LongArray>) {
    when (val chats = chatsRequest.result) {
        is TdResult.Loading -> {
            LoadingChats()
        }
        is TdResult.Success -> {
            ChatsLoaded(chats.result)
        }
        is TdResult.Error -> {
            LoadingChatsError()
        }
    }
}

@Composable
private fun LoadingChats() {
    Center {
        Text(
                text = stringResource(R.string.loading),
                style = ((MaterialTheme.typography()).h5)
        )
    }
}

@Composable
private fun ChatsLoaded(chats: LongArray) {
    Log.d("HomeScreen", "chat: $chats")
    VerticalScroller {
        Column {
            chats.forEach {
                val chat = TelegramClient.getChat(it)
                RenderChat(chat)
            }
        }
    }
}

@Composable
private fun LoadingChatsError() {
    Center {
        Text(
                text = stringResource(R.string.chats_error),
                style = ((MaterialTheme.typography()).h5)
        )
    }
}

@Composable
private fun RenderChat(chat: TdRequest<TdApi.Chat>) {
    when (val c = chat.result) {
        is TdResult.Loading -> {
            Text("Loading", modifier = LayoutWidth.Fill)
        }
        is TdResult.Success -> {
            c.result.let { chat ->
                ClickableChatItem(chat) {
                    navigateTo(Screen.Chat(chat))
                }
            }
        }
        is TdResult.Error -> {
            Text("Error", modifier = LayoutWidth.Fill)
        }
    }
}