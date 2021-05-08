package com.ibashkimi.telegram.ui.createchat

import androidx.lifecycle.ViewModel
import com.ibashkimi.telegram.data.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.drinkless.td.libcore.telegram.TdApi
import javax.inject.Inject

@HiltViewModel
class CreateChatViewModel @Inject constructor(
    val client: TelegramClient
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val users: Flow<List<TdApi.User>> =
        client.send<TdApi.Users>(TdApi.GetContacts()).map { result ->
            result.userIds.map { client.send<TdApi.User>(TdApi.GetUser(it)) }
        }.flatMapLatest {
            combine(it) { users -> users.toList() }
        }.map { users -> users.sortedBy { it.firstName } }
}