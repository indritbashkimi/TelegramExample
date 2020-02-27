package com.ibashkimi.telegram.ui.home

import android.util.Log
import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.layout.Center
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.Screen
import com.ibashkimi.telegram.data.TdRequest
import com.ibashkimi.telegram.data.TdResult
import com.ibashkimi.telegram.navigateTo
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun HomeScreen(chatsRequest: TdRequest<List<TdApi.Chat>>) {
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
private fun ChatsLoaded(chats: List<TdApi.Chat>) {
    Log.d("HomeScreen", "chat: $chats")
    AdapterList(chats) {
        ClickableChatItem(it) {
            navigateTo(Screen.Chat(it))
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
