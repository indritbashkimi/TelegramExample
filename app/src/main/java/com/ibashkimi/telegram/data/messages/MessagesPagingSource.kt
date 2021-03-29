package com.ibashkimi.telegram.data.messages

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.drinkless.td.libcore.telegram.TdApi

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesPagingSource(
    private val chatId: Long,
    private val messages: MessagesRepository
) : PagingSource<Long, TdApi.Message>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, TdApi.Message> {
        return try {
            val response = messages.getMessages(
                chatId = chatId,
                fromMessageId = params.key ?: 0,
                limit = params.loadSize
            )
            val messages = response.first()
            LoadResult.Page(
                data = messages,
                prevKey = null,
                nextKey = messages.lastOrNull()?.id
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, TdApi.Message>): Long? {
        return null
    }
}
