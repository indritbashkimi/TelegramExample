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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.ibashkimi.telegram.Screen
import com.ibashkimi.telegram.data.TelegramClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.drinkless.td.libcore.telegram.TdApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun HomeContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    showSnackbar: (String) -> Unit
) {
    val chats = viewModel.chats.collectAsLazyPagingItems()
    ChatsLoaded(
        viewModel.client,
        chats,
        modifier,
        onChatClicked = { navController.navigate(Screen.Chat.buildRoute(it)) },
        showSnackbar
    )
}

@Composable
private fun ChatsLoaded(
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
private fun LoadingChats(modifier: Modifier = Modifier) {
    LinearProgressIndicator(modifier = modifier.fillMaxWidth())
}

