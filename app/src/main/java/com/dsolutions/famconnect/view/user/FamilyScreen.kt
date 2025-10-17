package com.dsolutions.famconnect.view.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dsolutions.famconnect.viewmodel.UserViewModel
import com.dsolutions.famconnect.viewmodel.AuthState

@Composable
fun FamilyScreen(
    userViewModel: UserViewModel, // Wird per Hilt oder manuell übergeben
    onNavigateToLogin: () -> Unit
) {
    val authState by userViewModel.authState.collectAsState()

    // Rufe loadUserDataAndFamily NUR DANN auf, wenn der Auth-Status sich
    // zu AUTHENTICATED ändert.
    LaunchedEffect(authState) {
        if (authState == AuthState.AUTHENTICATED) {
            userViewModel.loadUserDataAndFamily()
        } else if (authState == AuthState.UNAUTHENTICATED) {
            // Wenn der Benutzer ausgeloggt ist, schicke ihn zum Login-Screen.
            onNavigateToLogin()
        }
    }

    // Die Zustände aus dem ViewModel "einsammeln"
    val user by userViewModel.userState.collectAsState()
    val family by userViewModel.familyState.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.errorState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (authState) {
            AuthState.UNKNOWN -> {
                // Zeige einen Ladeindikator, während der Login-Status geprüft wird.
                CircularProgressIndicator()
                Text("Checking authentication...")
            }

            AuthState.AUTHENTICATED -> {
                if (isLoading) {
                    // Ladeindikator anzeigen, während die Daten geholt werden
                    CircularProgressIndicator()
                } else if (error != null) {
                    // Eine Fehlermeldung anzeigen
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                } else {
                    // Die eigentlichen Daten anzeigen, wenn sie geladen sind
                    Text(
                        text = "Welcome, ${user?.firstName ?: "User"}!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // HIER WIRD DER FAMILIENNAME ANGEZEIGT
                    Text(
                        text = "Your Family: ${family?.name ?: "Unknown"}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Family ID: ${family?.id ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            AuthState.UNAUTHENTICATED -> {
                // Wenn der Benutzer nicht angemeldet ist, zeige ihn den Login-Screen.
            }
        }
    }
}