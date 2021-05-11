package com.ibashkimi.telegram.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.ibashkimi.telegram.data.TelegramClient
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatsLoaded(
    client: TelegramClient,
    chats: LazyPagingItems<TdApi.Chat>,
    modifier: Modifier = Modifier,
    onChatClicked: (Long) -> Unit,
    showSnackbar: (String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        if (chats.loadState.refresh is LoadState.Loading) {
            item {
                LoadingChats()
            }
        }
        itemsIndexed(chats) { index, item ->
            item?.let { chat ->
                ChatItem(
                    client,
                    chat,
                    modifier = Modifier.clickable(onClick = {
                        onChatClicked(item.id)
                    })
                )
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    startIndent = 64.dp
                )
            }
        }
    }
}

@Composable
fun LoadingChats(modifier: Modifier = Modifier) {
    LinearProgressIndicator(modifier = modifier.fillMaxWidth())
}

