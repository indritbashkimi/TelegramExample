package com.ibashkimi.telegram

import androidx.lifecycle.ViewModel
import com.ibashkimi.telegram.data.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(val client: TelegramClient) : ViewModel() {

    val authState = client.authState

}