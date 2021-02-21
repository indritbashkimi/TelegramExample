package com.ibashkimi.telegram.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ibashkimi.telegram.data.TelegramClient
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import org.drinkless.td.libcore.telegram.TdApi
import java.io.File

@Composable
fun TelegramImage(
    client: TelegramClient,
    file: TdApi.File?,
    modifier: Modifier = Modifier
) {
    val photo = file?.let {
        client.downloadableFile(file).collectAsState(file.local.path, Dispatchers.IO)
    } ?: mutableStateOf(null)
    photo.value?.let {
        CoilImage(
            data = File(it),
            contentDescription = null,
            modifier = modifier
        )
    } ?: Box(modifier.background(Color.LightGray))
}
