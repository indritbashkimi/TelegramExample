package com.ibashkimi.telegram

import androidx.compose.mutableStateOf
import org.drinkless.td.libcore.telegram.TdApi

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Screen(val title: String) {
    object ChatList : Screen("Telegram")
    class Chat(val chat: TdApi.Chat) : Screen(chat.title)
}

object Navigation {

    private val stack = ArrayList<Screen>().apply { add(Screen.ChatList) }

    var currentScreen = mutableStateOf<Screen>(Screen.ChatList)

    fun push(destination: Screen) {
        stack.add(destination)
        stackChanged()
    }

    fun pop(): Boolean {
        if (stack.size < 2)
            return false
        stack.removeAt(stack.size - 1)
        stackChanged()
        return true
    }

    fun navigateTo(destination: Screen) {
        push(destination)
    }

    fun replace(destination: Screen) {
        stack[stack.size - 1] = destination
        stackChanged()
    }

    private fun stackChanged() {
        currentScreen.value = stack.last()
    }
}
