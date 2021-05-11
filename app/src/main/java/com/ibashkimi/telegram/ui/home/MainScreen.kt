package com.ibashkimi.telegram.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import androidx.paging.compose.collectAsLazyPagingItems
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    when (uiState) {
        UiState.Loading -> {
            LoadingScreen(modifier)
        }
        UiState.Loaded -> {
            MainScreenScaffold(navController = navController, modifier = modifier)
        }
        UiState.Login -> {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MainScreenScaffold(
    navController: NavController,
    modifier: Modifier,
    viewModel: HomeViewModel = viewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) }, navigationIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.CreateChat.route) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "New message"
                )
            }
        },
        drawerContent = {
            DrawerContent(client = viewModel.client,
                newGroup = {},
                contacts = {},
                calls = {},
                savedMessages = {},
                settings = {})
        },
        content = {
            Surface(color = MaterialTheme.colors.background) {
                HomeContent(
                    navController,
                    modifier = Modifier.fillMaxWidth(),
                    viewModel = viewModel
                ) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(it)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun HomeContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    showSnackbar: (String) -> Unit
) {
    val chats = viewModel.chats.collectAsLazyPagingItems()
    ChatsLoaded(
        viewModel.client,
        chats,
        modifier,
        onChatClicked = { navController.navigate(Screen.Chat.buildRoute(it)) },
        showSnackbar
    )
}