package com.dsolutions.famconnect.view.user

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dsolutions.famconnect.R
import com.dsolutions.famconnect.view.common.HeaderRow
import com.dsolutions.famconnect.viewmodel.UserViewModel
import java.lang.Error
import androidx.compose.runtime.collectAsState

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderRow(
            drawableRes = R.drawable.ic_logo,
            contentDescription = "Logo",
            title = "FAMconnect"
        )

        Text(
            text = "Welcome to FAMconnect. Please login to connect your FAMily.",
            style = MaterialTheme.typography.bodyLarge, // Verwendet einen größeren, definierten Textstil
            textAlign = TextAlign.Center, // Zentriert den Text
            modifier = Modifier
                .fillMaxWidth() // Nimmt die volle Breite ein, um die Zentrierung zu ermöglichen
                .padding(vertical = 24.dp, horizontal = 16.dp) // Fügt vertikalen und horizontalen Abstand hinzu
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    error = null // Fehler zurücksetzen
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true // Ladezustand starten
                        // --- DIESEN TEIL WIEDER AKTIVIEREN ---
                        userViewModel.loginUser(
                            email, password,
                            onSuccess = {
                                Log.d("LoginScreen", "Login successful")
                                isLoading = false // Ladezustand beenden
                                onLoginSuccess() // Navigation zum Hauptbildschirm
                            },
                            onError = { errorMessage ->
                                Log.e("LoginScreen", "Login failed: $errorMessage")
                                isLoading = false // Ladezustand beenden
                                error = "Login failed: $errorMessage" // Fehlermeldung anzeigen
                            }
                        )
                    } else {
                        error = "Email and password cannot be empty."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Button deaktivieren, während geladen wird
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Da wir kein vollständiges Theme haben, wrappen wir es für eine bessere Vorschau.
    // Falls du ein eigenes App-Theme hast (z.B. FamConnectTheme), solltest du es hier verwenden.
    MaterialTheme {
        LoginScreen(
            userViewModel = userViewModel,
            onLoginSuccess = { Log.d("Preview", "Login success clicked") },
            onNavigateToRegister = { Log.d("Preview", "Navigate to register clicked") }
        )
    }
}
 */