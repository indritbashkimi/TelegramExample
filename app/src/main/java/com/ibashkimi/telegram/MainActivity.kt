package com.ibashkimi.telegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.data.UserRepository
import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository
import com.ibashkimi.telegram.ui.MyApp
import kotlinx.coroutines.ExperimentalCoroutinesApi


class MainActivity : AppCompatActivity() {

    private var client: TelegramClient? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val newClient = TelegramClient(this.application)
            client = newClient
            val repository = Repository(
                newClient,
                ChatsRepository(newClient),
                MessagesRepository(newClient),
                UserRepository(newClient)
            )
            MyApp(repository)
        }
    }

    override fun onBackPressed() {
        if (!Navigation.pop())
            super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        client?.close()
        client = null
    }
}
