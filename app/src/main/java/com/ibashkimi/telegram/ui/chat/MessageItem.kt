package com.ibashkimi.telegram.ui.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun TextMessage(message: TdApi.MessageText, modifier: Modifier = Modifier) {
    Text(text = message.text.text, modifier = modifier)
}

@Composable
fun AudioMessage(message: TdApi.MessageAudio, modifier: Modifier = Modifier) {
    Text(text = "audio", modifier = modifier)
}

@Composable
fun VideoMessage(message: TdApi.MessageVideo, modifier: Modifier = Modifier) {
    Text(text = "video", modifier = modifier)
}

@Composable
fun StickerMessage(message: TdApi.MessageSticker, modifier: Modifier = Modifier) {
    Text(text = "sticker", modifier = modifier)
}

@Composable
fun AnimationMessage(message: TdApi.MessageAnimation, modifier: Modifier = Modifier) {
    Text(text = "GIF", modifier = modifier)
}

@Composable
fun CallMessage(message: TdApi.MessageCall, modifier: Modifier = Modifier) {
    Text(text = "Call", modifier = modifier)
}
