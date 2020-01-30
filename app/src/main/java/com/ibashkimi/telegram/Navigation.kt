package com.ibashkimi.telegram

import androidx.compose.Model
import androidx.ui.res.stringResource
import org.drinkless.td.libcore.telegram.TdApi

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Screen(val title: String) {
    object ChatList : Screen(stringResource(R.string.app_name))
    class Chat(val chat: TdApi.Chat): Screen(chat.title)
}

@Model
object Status {
    var currentScreen: Screen = Screen.ChatList
}

object Navigation {

    private val stack = ArrayList<Screen>().apply { add(Screen.ChatList) }

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

    fun replace(destination: Screen) {
        stack[stack.size - 1] = destination
        stackChanged()
    }

    private fun stackChanged() {
        Status.currentScreen = stack.last()
    }
}

/**
 * Temporary solution pending navigation support.
 */
fun navigateTo(destination: Screen) {
    Navigation.push(destination)
}