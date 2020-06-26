package com.ibashkimi.telegram.data

import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository

class Repository(
    val client: TelegramClient,
    val chats: ChatsRepository,
    val messages: MessagesRepository,
    val users: UserRepository
)