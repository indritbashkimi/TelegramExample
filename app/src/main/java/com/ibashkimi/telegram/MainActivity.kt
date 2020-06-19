package com.ibashkimi.telegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.ui.MyApp


class MainActivity : AppCompatActivity() {

    private var client: TelegramClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val newClient = TelegramClient
            newClient.application = this.application
            client = newClient
            MyApp(newClient)
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
