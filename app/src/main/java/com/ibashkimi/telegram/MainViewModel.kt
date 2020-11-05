package com.ibashkimi.telegram

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.data.UserRepository
import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private var client: TelegramClient = TelegramClient(app)

    val repository = Repository(
        client,
        ChatsRepository(client),
        MessagesRepository(client),
        UserRepository(client)
    )

    val authState = repository.client.authState

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}