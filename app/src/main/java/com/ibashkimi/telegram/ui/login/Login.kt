package com.ibashkimi.telegram.ui.login

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun WaitForNumberScreen(onEnter: (String) -> Unit) {
    AuthorizationScreen(
        title = "Enter phone number",
        message = "Please enter your number in international format",
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
fun WaitForPasswordScreen(onEnter: (String) -> Unit) {
    AuthorizationScreen(
        title = "Enter password",
        onEnter = onEnter
    )
}

@Composable
private fun AuthorizationScreen(title: String, message: String? = null, onEnter: (String) -> Unit) {
    val executed = state { false }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        },
        bodyContent = {
            if (executed.value) {
                CircularProgressIndicator()
            } else {
                val phoneNumber = state { TextFieldValue() }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = phoneNumber.value,
                        onValueChange = { phoneNumber.value = it },
                        label = { },
                        textStyle = MaterialTheme.typography.h5
                    )
                    Divider(
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (message == null) {
                        Spacer(modifier = Modifier.preferredHeight(16.dp))
                    } else {
                        Text(message, modifier = Modifier.padding(16.dp))
                    }
                    Button(modifier = Modifier.gravity(Alignment.End), content = {
                        Text("Enter")
                    }, onClick = {
                        onEnter(phoneNumber.value.text)
                        executed.value = true
                    })
                }
            }
        }
    )
}
