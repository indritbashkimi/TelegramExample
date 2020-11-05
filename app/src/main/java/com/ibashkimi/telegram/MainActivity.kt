package com.ibashkimi.telegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import com.ibashkimi.telegram.ui.TelegramApp
import kotlinx.coroutines.ExperimentalCoroutinesApi


class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()
            TelegramApp(this, viewModel)
        }
    }
}
