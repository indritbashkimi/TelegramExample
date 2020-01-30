package com.ibashkimi.telegram.ui.login

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.EditorModel
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.unit.dp

@Composable
fun WaitForNumberScreen(onEnter: (String) -> Unit) {
    AuthorizationScreen(
            title = "Enter phone number",
            onEnter = onEnter
    )
}

@Composable
fun WaitForCodeScreen(onEnter: (String) -> Unit) {
    AuthorizationScreen(
            title = "Enter code",
            onEnter = onEnter
    )
}

@Composable
private fun AuthorizationScreen(title: String, onEnter: (String) -> Unit) {
    val executed = state { false }
    Scaffold(
            topAppBar = {
                TopAppBar(title = { Text(title) })
            },
            bodyContent = {
                if (executed.value) {
                    CircularProgressIndicator()
                } else {
                    val phoneNumber = state { EditorModel("") }
                    Padding(padding = 16.dp) {
                        Column(arrangement = Arrangement.Center) {
                            TextField(
                                    value = phoneNumber.value,
                                    onValueChange = { phoneNumber.value = it },
                                    textStyle = ((MaterialTheme.typography()).h5)
                            )
                            Divider(color = ((MaterialTheme.colors()).onBackground), modifier = LayoutWidth.Fill)
                            Spacer(modifier = LayoutHeight(16.dp))
                            Button(text = "Enter", modifier = LayoutGravity.End, onClick = {
                                onEnter(phoneNumber.value.text)
                                executed.value = true
                            })
                        }
                    }
                }
            }
    )
}
