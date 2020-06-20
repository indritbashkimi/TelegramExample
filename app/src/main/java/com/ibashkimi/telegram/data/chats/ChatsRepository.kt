package com.ibashkimi.telegram.data.chats

import com.ibashkimi.telegram.data.TelegramClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.drinkless.td.libcore.telegram.TdApi

@ExperimentalCoroutinesApi
class ChatsRepository(private val client: TelegramClient) {

    private fun getChatIds(): Flow<LongArray> = callbackFlow {
        client.client.send(TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, 50)) {
            when (it.constructor) {
                TdApi.Chats.CONSTRUCTOR -> {
                    offer((it as TdApi.Chats).chatIds)
                }
                TdApi.Error.CONSTRUCTOR -> {
                    error("")
                }
                else -> {
                    error("")
                }
            }
            //close()
        }
        awaitClose { }
    }

    fun getChats(): Flow<List<TdApi.Chat>> = getChatIds()
        .map { ids -> ids.map { getChat(it) } }
        .flatMapLatest { chatsFlow ->
            combine(chatsFlow) { chats ->
                chats.toList()
            }
        }

    fun getChat(chatId: Long): Flow<TdApi.Chat> = callbackFlow {
        client.client.send(TdApi.GetChat(chatId)) {
            when (it.constructor) {
                TdApi.Chat.CONSTRUCTOR -> {
                    offer(it as TdApi.Chat)
                }
                TdApi.Error.CONSTRUCTOR -> {
                    error("Something went wrong")
                }
                else -> {
                    error("Something went wrong")
                }
            }
            //close()
        }
        awaitClose { }
    }

    fun chatImage(chat: TdApi.Chat): Flow<String?> =
        chat.photo?.small?.takeIf {
            it.local?.isDownloadingCompleted == false
        }?.id?.let { fileId ->
            TelegramClient.downloadFile(fileId).map { chat.photo?.small?.local?.path }
        } ?: flowOf(chat.photo?.small?.local?.path)
}