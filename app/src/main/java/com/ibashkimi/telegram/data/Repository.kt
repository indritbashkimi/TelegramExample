package com.ibashkimi.telegram.data

import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class Repository @Inject constructor(
    val client: TelegramClient,
    val chats: ChatsRepository,
    val messages: MessagesRepository,
    val users: UserRepository
)