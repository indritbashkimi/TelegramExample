package com.ibashkimi.telegram

import androidx.navigation.NavBackStackEntry

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Screen(val route: String) {

    object Home : Screen("home")

    object EnterPhoneNumber : Screen("login/enterPhoneNumber")

    object EnterCode : Screen("login/enterCode")

    object EnterPassword : Screen("login/enterPassword")

    object Chat : Screen("chat/{chatId}") {
        fun buildRoute(chatId: Long): String = "chat/${chatId}"
        fun getChatId(entry: NavBackStackEntry): Long =
            entry.arguments!!.getString("chatId")?.toLong()
                ?: throw IllegalArgumentException("chatId argument missing.")
    }
}

