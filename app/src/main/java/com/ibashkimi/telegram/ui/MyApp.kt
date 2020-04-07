package com.ibashkimi.telegram.ui

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.ripple.ripple
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ibashkimi.telegram.Navigation
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.Screen
import com.ibashkimi.telegram.Status
import com.ibashkimi.telegram.data.Authentication
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.ui.chat.ChatScreen
import com.ibashkimi.telegram.ui.home.HomeScreen
import com.ibashkimi.telegram.ui.login.WaitForCodeScreen
import com.ibashkimi.telegram.ui.login.WaitForNumberScreen

@Composable
fun MyApp(client: TelegramClient) {
    val isDark = isSystemInDarkTheme()
    MaterialTheme(
        colors = if (isDark) darkColorPalette() else lightColorPalette()
    ) {
        val authState = client.authState
        android.util.Log.d("MyApp", "auth state: ${authState.auth}")
        when (authState.auth) {
            Authentication.UNKNOWN -> {
                Text(
                    "Waiting for client to initialize",
                    modifier = Modifier.fillMaxWidth() + Modifier.wrapContentSize(Alignment.Center)
                )
            }
            Authentication.UNAUTHENTICATED -> {
                client.startAuthentication()
                Text(
                    "Starting authentication",
                    modifier = Modifier.fillMaxWidth() + Modifier.wrapContentSize(Alignment.Center)
                )
            }
            Authentication.WAIT_FOR_NUMBER -> {
                WaitForNumberScreen {
                    client.insertPhoneNumber(it)
                }
            }
            Authentication.WAIT_FOR_CODE -> {
                WaitForCodeScreen {
                    client.insertCode(it)
                }
            }
            Authentication.AUTHENTICATED -> {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen() {
    val destination = Status.currentScreen
    val title = destination.title
    Scaffold(
        topAppBar = {
            if (destination == Screen.ChatList) {
                TopAppBar(title = { Text(stringResource(R.string.app_name)) })
            } else {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        Clickable(
                            onClick = { Navigation.pop() },
                            modifier = Modifier.ripple() + Modifier.padding(16.dp)
                        ) {
                            Image(
                                asset = Icons.Default.ArrowBack,
                                alignment = Alignment.Center,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                            )
                        }
                    })
            }
        },
        bodyContent = {
            AppContent(destination, modifier = Modifier.fillMaxWidth())
        }
    )
}

@Composable
private fun AppContent(screen: Screen, modifier: Modifier = Modifier.None) {
    Surface(color = MaterialTheme.colors.background, modifier = modifier) {
        when (screen) {
            is Screen.ChatList -> {
                HomeScreen(chatsRequest = TelegramClient.loadChats())
            }
            is Screen.Chat -> ChatScreen(chat = screen.chat)
        }
    }
}
