package com.ibashkimi.telegram.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import org.drinkless.td.libcore.telegram.TdApi
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    // TODO: inject chatId https://github.com/google/dagger/issues/2287
    val client: TelegramClient,
    val chatsRepository: ChatsRepository,
    val messagesRepository: MessagesRepository
) : ViewModel() {

    private var chatId: Long = -1

    lateinit var chat: Flow<TdApi.Chat?>
        private set

    lateinit var messagesPaged: Flow<PagingData<TdApi.Message>>
        private set

    fun setChatId(chatId: Long) {
        this.chatId = chatId
        this.chat = chatsRepository.getChat(chatId)
        this.messagesPaged = Pager(PagingConfig(pageSize = 30)) {
            messagesRepository.getMessagesPaged(chatId)
        }.flow.cachedIn(viewModelScope)
    }

    fun sendMessage(
        messageThreadId: Long = 0,
        replyToMessageId: Long = 0,
        options: TdApi.MessageSendOptions = TdApi.MessageSendOptions(),
        inputMessageContent: TdApi.InputMessageContent
    ): Deferred<TdApi.Message> {
        return messagesRepository.sendMessage(
            chatId, messageThreadId, replyToMessageId, options, inputMessageContent
        )
    }
}