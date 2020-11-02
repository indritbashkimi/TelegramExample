package com.ibashkimi.telegram.ui.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.ibashkimi.telegram.Navigation
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.Screen
import com.ibashkimi.telegram.data.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.drinkless.td.libcore.telegram.TdApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun HomeScreen(
    repository: Repository,
    modifier: Modifier = Modifier,
    showSnackbar: (String) -> Unit
) {
    val chats = remember {
        Pager(
            PagingConfig(pageSize = 30)
        ) {
            repository.chats.getChatsPaged()
        }.flow
    }.cachedIn(rememberCoroutineScope()).collectAsLazyPagingItems()
    ChatsLoaded(repository, chats, modifier, showSnackbar)
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
private fun ChatsLoaded(
    repository: Repository,
    chats: LazyPagingItems<TdApi.Chat>,
    modifier: Modifier = Modifier,
    showSnackbar: (String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        when {
            chats.loadState.refresh == LoadState.Loading -> {
                item {
                    LoadingChats()
                }
            }
            chats.loadState.refresh is LoadState.Error -> {
                item {
                    showSnackbar(stringResource(R.string.chats_error))
                }
            }
            chats.loadState.refresh is LoadState.NotLoading && chats.itemCount == 0 -> {
                item {
                    Text("Empty")
                }
            }
        }
        if (chats.loadState.prepend == LoadState.Loading) {
            item {
                LoadingMore()
            }
        }
        itemsIndexed(chats) { index, item ->
            item?.let { chat ->
                ChatItem(
                    repository,
                    chat,
                    modifier = Modifier.clickable(onClick = {
                        Navigation.navigateTo(Screen.Chat(chat))
                    })
                )
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    startIndent = 64.dp
                )
            }

        }
        if (chats.loadState.append == LoadState.Loading) {
            item {
                LoadingMore()
            }
        }
    }
}

@Composable
private fun LoadingChats(modifier: Modifier = Modifier) {
    LinearProgressIndicator(modifier = modifier.fillMaxWidth())
}

@Composable
private fun LoadingMore(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
    )
}
