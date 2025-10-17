package com.dsolutions.famconnect.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.dsolutions.famconnect.theme.FamConnectTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dsolutions.famconnect.view.user.ChooseFamilyScreen
import com.dsolutions.famconnect.view.user.LoginScreen
import com.dsolutions.famconnect.view.user.RegisterScreen
import com.dsolutions.famconnect.viewmodel.EventViewModel
import com.dsolutions.famconnect.viewmodel.UserViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FamConnectTheme {
                var currentScreen by rememberSaveable { mutableStateOf("login") }

                var familySelection by rememberSaveable { mutableStateOf<String?>(null) }
                var familyIdOrName by rememberSaveable { mutableStateOf<String?>(null) }
                var familyPin by rememberSaveable { mutableStateOf<String?>(null) }

                val systemUiController = rememberSystemUiController()
                val backgroundColor = MaterialTheme.colorScheme.background
                val useDarkIcons = true

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = backgroundColor,
                        darkIcons = useDarkIcons
                    )
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    topBar = { /* AppBar etc. */ },
                    content = { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(padding)
                        ) {
                            val userViewModel: UserViewModel = hiltViewModel()
                            val eventViewModel: EventViewModel = hiltViewModel()

                            var familyLastName by rememberSaveable { mutableStateOf<String?>(null) }

                            when (currentScreen) {
                                "login" -> {
                                    val testEmail = "d.dykiert@gmail.com"
                                    val testPassword = "12345678"

                                    loginWithTestValues(
                                        userViewModel,
                                        "d.dykiert@gmail.com",
                                        "12345678",
                                        onLoginSuccess = { currentScreen = "main" },
                                    )
                                    Log.d("Login", "LoginScreen called")
                                    Log.d("Login", "Email $testEmail")
                                    Log.d("Login", "Password $testPassword")
                                    Log.d("Login", "authState ${userViewModel.authState.collectAsState().value}")
                                }
                                    /*LoginScreen(
                                        userViewModel = userViewModel,
                                        onLoginSuccess = { currentScreen = "main" },
                                        onNavigateToRegister = { currentScreen = "chooseFamily" }
                                    )*/

                                "register" -> RegisterScreen(
                                    userViewModel = userViewModel,
                                    onRegisterSuccess = { currentScreen = "main" },
                                    onNavigateToLogin = { currentScreen = "login" },
                                    familySelection = familySelection,
                                    familyIdOrName = familyIdOrName,
                                    familyPin = familyPin,
                                    prefilledLastName = familyLastName
                                )

                                "main" -> MainScreen(
                                    userViewModel = userViewModel,
                                    eventViewModel = eventViewModel
                                )

                                "chooseFamily" -> ChooseFamilyScreen(
                                    userViewModel = userViewModel,
                                    onNavigateToLogin = { currentScreen = "login" },
                                    onNavigateToRegister = { selection, idOrName, pin, lastName ->
                                        familySelection = selection
                                        familyIdOrName = idOrName
                                        familyPin = pin
                                        familyLastName = lastName
                                        currentScreen = "register"
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

fun loginWithTestValues(userViewModel: UserViewModel, email: String, password: String, onLoginSuccess: () -> Unit) {
    if (email.isNotBlank() && password.isNotBlank()) {
        userViewModel.loginUser(
            email, password,
            onSuccess = {
                Log.d("LoginScreen", "Login successful")
                onLoginSuccess() // Navigation zum Hauptbildschirm
            },
            onError = { errorMessage ->
                Log.e("LoginScreen", "Login failed: $errorMessage")
            }
        )
    }
}