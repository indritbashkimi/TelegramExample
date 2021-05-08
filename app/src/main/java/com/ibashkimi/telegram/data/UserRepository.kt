package com.ibashkimi.telegram.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.td.libcore.telegram.TdApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepository @Inject constructor(private val client: TelegramClient) {

    fun getUser(userId: Int): Flow<TdApi.User> = callbackFlow {
        client.client.send(TdApi.GetUser(userId)) {
            offer(it as TdApi.User)
        }
        awaitClose { }
    }
}