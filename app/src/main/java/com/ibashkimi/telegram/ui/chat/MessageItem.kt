package com.ibashkimi.telegram.ui.chat

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun TextMessage(message: TdApi.MessageText, modifier: Modifier = Modifier.None) {
    Text(text = message.text.text, modifier = modifier)
}

@Composable
fun AudioMessage(message: TdApi.MessageAudio, modifier: Modifier = Modifier.None) {
    Text(text = "audio", modifier = modifier)
}

@Composable
fun VideoMessage(message: TdApi.MessageVideo, modifier: Modifier = Modifier.None) {
    Text(text = "video", modifier = modifier)
}

@Composable
fun StickerMessage(message: TdApi.MessageSticker, modifier: Modifier = Modifier.None) {
    Text(text = "sticker", modifier = modifier)
}

@Composable
fun AnimationMessage(message: TdApi.MessageAnimation, modifier: Modifier = Modifier.None) {
    Text(text = "GIF", modifier = modifier)
}

@Composable
fun CallMessage(message: TdApi.MessageCall, modifier: Modifier = Modifier.None) {
    Text(text = "Call", modifier = modifier)
}
