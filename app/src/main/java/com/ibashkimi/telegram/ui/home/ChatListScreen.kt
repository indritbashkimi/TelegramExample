package com.ibashkimi.telegram.ui.home

import android.util.Log
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import com.ibashkimi.telegram.Navigation
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.Screen
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.Response
import com.ibashkimi.telegram.data.asResponse
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun HomeScreen(repository: Repository) {
    val chats = repository.chats.getChats().asResponse().collectAsState()
    when (val response = chats.value) {
        null -> {
            LoadingChats()
        }
        is Response.Success -> {
            ChatsLoaded(repository, response.data)
        }
        is Response.Error -> {
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
private fun ChatsLoaded(repository: Repository, chats: List<TdApi.Chat>) {
    Log.d("HomeScreen", "chat: $chats")
    AdapterList(chats) {
        ClickableChatItem(repository, it) {
            Navigation.navigateTo(Screen.Chat(it))
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
