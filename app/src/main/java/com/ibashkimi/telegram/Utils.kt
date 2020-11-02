package com.ibashkimi.telegram

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take

suspend fun <T> Flow<T>.asDeferred(): Deferred<T> {
    val result = CompletableDeferred<T>()
    this.catch { result.completeExceptionally(it) }.take(1).collect { result.complete(it) }
    return result
}

suspend fun <T> Flow<T>.await(): T = asDeferred().await()