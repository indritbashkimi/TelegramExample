package com.ibashkimi.telegram.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
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
    val executed = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        },
        content = {
            if (executed.value) {
                CircularProgressIndicator()
            } else {
                val phoneNumber = remember { mutableStateOf(TextFieldValue()) }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = phoneNumber.value,
                        onValueChange = { phoneNumber.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.h5
                    )
                    if (message == null) {
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        Text(message, modifier = Modifier.padding(vertical = 16.dp))
                    }
                    Button(onClick = {
                        onEnter(phoneNumber.value.text)
                        executed.value = true
                    }, modifier = Modifier.align(Alignment.End)) {
                        Text("Enter")
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun AuthorizationScreenPreview() {
    WaitForNumberScreen {}
}
