package com.ibashkimi.telegram.data.messages

import com.ibashkimi.telegram.data.TelegramClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.td.libcore.telegram.TdApi

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesRepository(private val client: TelegramClient) {

    fun getMessages(chatId: Long): Flow<List<TdApi.Message>> = callbackFlow {
        client.client.send(TdApi.GetChatHistory(chatId, 0, 0, 100, false)) {
            when (it.constructor) {
                TdApi.Messages.CONSTRUCTOR -> {
                    offer((it as TdApi.Messages).messages.toList())
                }
                TdApi.Error.CONSTRUCTOR -> {
                    error("")
                }
                else -> {
                    error("")
                }
            }
        }
        awaitClose { }
    }

    fun getMessage(chatId: Long, messageId: Long): Flow<TdApi.Message> = callbackFlow {
        client.client.send(TdApi.GetMessage(chatId, messageId)) {
            when (it.constructor) {
                TdApi.Message.CONSTRUCTOR -> {
                    offer(it as TdApi.Message)
                }
                TdApi.Error.CONSTRUCTOR -> {
                    error("Something went wrong")
                }
                else -> {
                    error("Something went wrong")
                }
            }
        }
        awaitClose { }
    }

    fun sendMessage(
        chatId: Long,
        replyToMessageId: Long = 0,
        options: TdApi.SendMessageOptions = TdApi.SendMessageOptions(),
        inputMessageContent: TdApi.InputMessageContent
    ): Deferred<TdApi.Message> = sendMessage(
        TdApi.SendMessage(
            chatId,
            replyToMessageId,
            options,
            null,
            inputMessageContent
        )
    )

    fun sendMessage(sendMessage: TdApi.SendMessage): Deferred<TdApi.Message> {
        val result = CompletableDeferred<TdApi.Message>()
        client.client.send(sendMessage) {
            when (it.constructor) {
                TdApi.Message.CONSTRUCTOR -> {
                    result.complete(it as TdApi.Message)
                }
                else -> {
                    result.completeExceptionally(error("Something went wrong"))
                }
            }
        }
        return result
    }
}