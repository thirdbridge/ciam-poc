package com.example.oktaauthnintegration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<SessionTokenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Text(text = "Auth N Flow")
                LoginUI { username, password -> viewModel.login(username, password)}
                Spacer(modifier = Modifier.height(16.dp))
                OtpUi { otp -> viewModel.submitOtp(otp) }
                Button(onClick = { viewModel.useRefreshToken() }) {
                    Text("Refresh token")
                }
            }
        }
    }

    @Composable
    fun LoginUI(onLogin: (String, String) -> Unit) {
        val username = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }

        Column {
            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onLogin(username.value, password.value) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onLogin(username.value, password.value) }) {
                Text("Login")
            }
        }
    }

    @Composable
    fun OtpUi(onOtp: (String) -> Unit) {
        val otp = remember { mutableStateOf("") }

        Column {
            OutlinedTextField(
                value = otp.value,
                onValueChange = { otp.value = it },
                label = { Text("Otp") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )

            Button(onClick = { onOtp(otp.value) }) {
                Text("Send Passcode")
            }
        }
    }
}