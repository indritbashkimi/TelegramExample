package com.ibashkimi.telegram

import androidx.lifecycle.ViewModel
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.data.UserRepository
import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(private val client: TelegramClient) : ViewModel() {

    val repository = Repository(
        client,
        ChatsRepository(client),
        MessagesRepository(client),
        UserRepository(client)
    )

    val authState = client.authState

}