package com.ibashkimi.telegram.ui

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ibashkimi.telegram.Navigation
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.Screen
import com.ibashkimi.telegram.data.Authentication
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.ui.chat.ChatScreen
import com.ibashkimi.telegram.ui.home.HomeScreen
import com.ibashkimi.telegram.ui.login.WaitForCodeScreen
import com.ibashkimi.telegram.ui.login.WaitForNumberScreen
import com.ibashkimi.telegram.ui.login.WaitForPasswordScreen

@Composable
fun MyApp(repository: Repository) {
    val isDark = isSystemInDarkTheme()
    MaterialTheme(
        colors = if (isDark) darkColorPalette() else lightColorPalette()
    ) {
        val authState = repository.client.authState.collectAsState(Authentication.UNKNOWN)
        android.util.Log.d("MyApp", "auth state: ${authState.value}")
        when (authState.value) {
            Authentication.UNKNOWN -> {
                Text(
                    "Waiting for client to initialize",
                    modifier = Modifier.fillMaxWidth() + Modifier.wrapContentSize(Alignment.Center)
                )
            }
            Authentication.UNAUTHENTICATED -> {
                repository.client.startAuthentication()
                Text(
                    "Starting authentication",
                    modifier = Modifier.fillMaxWidth() + Modifier.wrapContentSize(Alignment.Center)
                )
            }
            Authentication.WAIT_FOR_NUMBER -> {
                WaitForNumberScreen {
                    repository.client.insertPhoneNumber(it)
                }
            }
            Authentication.WAIT_FOR_CODE -> {
                WaitForCodeScreen {
                    repository.client.insertCode(it)
                }
            }
            Authentication.WAIT_FOR_PASSWORD -> {
                WaitForPasswordScreen {
                    repository.client.insertPassword(it)
                }
            }
            Authentication.AUTHENTICATED -> {
                MainScreen(repository)
            }
        }
    }
}

@Composable
private fun MainScreen(repository: Repository) {
    val currentScreen = Navigation.currentScreen.collectAsState()
    val destination = currentScreen.value
    val title = destination.title
    Scaffold(
        topAppBar = {
            if (destination == Screen.ChatList) {
                TopAppBar(title = { Text(stringResource(R.string.app_name)) })
            } else {
                TopAppBar(
                    title = { Text(title, maxLines = 1) },
                    navigationIcon = {
                        Image(
                            modifier = Modifier.clickable(onClick = { Navigation.pop() })
                                .padding(16.dp),
                            asset = Icons.Default.ArrowBack,
                            alignment = Alignment.Center,
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                        )
                    })
            }
        },
        bodyContent = {
            AppContent(repository, destination, modifier = Modifier.fillMaxWidth())
        }
    )
}

@Composable
private fun AppContent(repository: Repository, screen: Screen, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colors.background, modifier = modifier) {
        when (screen) {
            is Screen.ChatList -> {
                HomeScreen(repository)
            }
            is Screen.Chat -> ChatScreen(repository, screen.chat)
        }
    }
}
