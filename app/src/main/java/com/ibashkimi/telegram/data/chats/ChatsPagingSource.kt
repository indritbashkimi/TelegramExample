package com.ibashkimi.telegram.data.chats

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.drinkless.td.libcore.telegram.TdApi

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsPagingSource(
    private val chats: ChatsRepository
) : PagingSource<Long, TdApi.Chat>() {

    override suspend fun load(
        params: LoadParams<Long>
    ): LoadResult<Long, TdApi.Chat> {
        try {
            val nextPageNumber = params.key ?: Long.MAX_VALUE
            val response = chats.getChats(
                nextPageNumber,
                params.loadSize
            )
            val chats = response.first()
            return LoadResult.Page(
                data = chats,
                prevKey = null,
                nextKey = chats.lastOrNull()?.positions?.firstOrNull()?.order
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, TdApi.Chat>): Long? {
        return null
    }
}
