package com.ibashkimi.telegram.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.state
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun NetworkImage(url: String?, placeHolderRes: Int? = null, modifier: Modifier = Modifier) {
    val context = ContextAmbient.current
    val loadedImage = state<ImageAsset?> { null }
    val placeholderDrawable =
        state { placeHolderRes?.let { ContextCompat.getDrawable(context, it) } }
    onCommit(url) {
        val glide = Glide.with(context)
        val target = object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                loadedImage.value = null
                placeholderDrawable.value = placeholder
            }

            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                loadedImage.value = bitmap.asImageAsset()
            }
        }
        glide.asBitmap()
            .load(url)
            .apply {
                placeHolderRes?.let { placeholder(it) }
            }
            .into(target)

        onDispose {
            loadedImage.value = null
            placeholderDrawable.value = null
            glide.clear(target)
        }
    }

    (loadedImage.value ?: placeholderDrawable.value?.toBitmap()?.asImageAsset())?.let {
        Image(it, modifier, contentScale = ContentScale.Crop)
    }
}
