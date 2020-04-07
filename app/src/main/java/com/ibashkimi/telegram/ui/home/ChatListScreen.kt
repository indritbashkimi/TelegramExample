package com.ibashkimi.telegram.ui.home

import android.util.Log
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentSize
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
    Text(
        text = stringResource(R.string.loading),
        style = MaterialTheme.typography.h5,
        modifier = Modifier.fillMaxSize() + Modifier.wrapContentSize(Alignment.Center)
    )
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
    Text(
        text = stringResource(R.string.chats_error),
        style = MaterialTheme.typography.h5,
        modifier = Modifier.fillMaxSize() + Modifier.wrapContentSize(Alignment.Center)
    )
}
