package com.ibashkimi.telegram.ui.chat

import androidx.compose.Composable
import androidx.ui.core.Text
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun TextMessage(message: TdApi.MessageText) {
    Text(text = message.text.text)
}

@Composable
fun AudioMessage(message: TdApi.MessageAudio) {
    Text(text = "audio")
}

@Composable
fun VideoMessage(message: TdApi.MessageVideo) {
    Text(text = "video")
}

@Composable
fun StickerMessage(message: TdApi.MessageSticker) {
    Text(text = "sticker")
}

@Composable
fun AnimationMessage(message: TdApi.MessageAnimation) {
    Text(text = "GIF")
}

@Composable
fun CallMessage(message: TdApi.MessageCall) {
    Text(text = "Call")
}
