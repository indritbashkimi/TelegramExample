package com.ibashkimi.telegram.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.data.chats.ChatsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val client: TelegramClient,
    private val chatsPagingSource: ChatsPagingSource
) : ViewModel() {

    val chats = Pager(
        PagingConfig(pageSize = 30)
    ) {
        chatsPagingSource
    }.flow.cachedIn(viewModelScope)

}