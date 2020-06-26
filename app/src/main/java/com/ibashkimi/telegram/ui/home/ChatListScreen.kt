package com.ibashkimi.telegram.ui.home

import android.util.Log
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ibashkimi.telegram.Navigation
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.Screen
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.Response
import com.ibashkimi.telegram.data.asResponse
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun HomeScreen(repository: Repository, modifier: Modifier = Modifier) {
    val chats = repository.chats.getChats().asResponse().collectAsState()
    when (val response = chats.value) {
        null -> {
            LoadingChats(modifier)
        }
        is Response.Success -> {
            ChatsLoaded(repository, response.data, modifier)
        }
        is Response.Error -> {
            LoadingChatsError(modifier)
        }
    }
}

@Composable
private fun LoadingChats(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.loading),
        style = MaterialTheme.typography.h5,
        modifier = modifier.fillMaxSize().wrapContentSize(Alignment.Center)
    )
}

@Composable
private fun ChatsLoaded(
    repository: Repository,
    chats: List<TdApi.Chat>,
    modifier: Modifier = Modifier
) {
    Log.d("HomeScreen", "chat: $chats")
    AdapterList(chats, modifier = modifier.padding(start=16.dp)) {
        ClickableChatItem(repository, it) {
            Navigation.navigateTo(Screen.Chat(it))
        }
    }
}

@Composable
private fun LoadingChatsError(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.chats_error),
        style = MaterialTheme.typography.h5,
        modifier = modifier.fillMaxSize().wrapContentSize(Alignment.Center)
    )
}
