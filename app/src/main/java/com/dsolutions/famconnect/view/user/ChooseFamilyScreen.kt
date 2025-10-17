package com.dsolutions.famconnect.view.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.dsolutions.famconnect.R
import com.dsolutions.famconnect.view.common.HeaderRow
import com.dsolutions.famconnect.viewmodel.FamilyValidationState
import com.dsolutions.famconnect.viewmodel.UserViewModel
import kotlin.text.isNotBlank

@Composable
fun ChooseFamilyScreen(
    userViewModel: UserViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: (
        selection: String,
        idOrName: String,
        pin: String?,
        lastName: String
    ) -> Unit
) {
    val validationState by userViewModel.familyValidationState.collectAsState()

    var familyIdInput by remember { mutableStateOf("") }
    var familyPinInput by remember { mutableStateOf("") }

    // Zustand, um die Auswahl des Benutzers zu speichern: "create" oder "join"
    var selection by rememberSaveable { mutableStateOf<String?>("create") }

    // Zustände nur für die familienbezogenen Eingabefelder
    var familyName by rememberSaveable { mutableStateOf("") }
    var familyPin by rememberSaveable { mutableStateOf("") }
    var repeatFamilyPin by rememberSaveable { mutableStateOf("") }
    var familyId by rememberSaveable { mutableStateOf("") }
    var clientSideError by rememberSaveable { mutableStateOf<String?>(null) }

    // Effekt, der auf den Erfolgsfall reagiert und weiter navigiert
    LaunchedEffect(validationState) {
        if (validationState is FamilyValidationState.Success) {
            val successState = validationState as FamilyValidationState.Success
            // Navigiere zum RegisterScreen und übergib die validierten Infos
            onNavigateToRegister("join", successState.familyId, null, successState.familyName)
            // Setze den Status zurück für den Fall, dass der Benutzer zurückkehrt
            userViewModel.resetFamilyValidationState()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderRow(
            drawableRes = R.drawable.ic_logo,
            contentDescription = "Logo",
            title = "FAMconnect",
            subTitle = "Join or Create a Family"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Auswahl-Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { selection = "create" },
                    modifier = Modifier.weight(1f),
                    colors = if (selection == "create") ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Create New Family")
                }
                OutlinedButton(
                    onClick = { selection = "join" },
                    modifier = Modifier.weight(1f),
                    colors = if (selection == "join") ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Join Family")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bedingte Anzeige der Felder basierend auf der Auswahl
            when (selection) {
                "create" -> {
                    // --- Felder zum Erstellen einer neuen Familie ---
                    OutlinedTextField(
                        value = familyName,
                        onValueChange = { familyName = it },
                        label = { Text("Family Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = "Family Name") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = familyPin,
                        onValueChange = { familyPin = it },
                        label = { Text("Family PIN (4-digit) *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Family PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = repeatFamilyPin,
                        onValueChange = { repeatFamilyPin = it },
                        label = { Text("Repeat Family PIN *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Repeat Family PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                        visualTransformation = PasswordVisualTransformation(), // Versteckt die PIN-Eingabe
                        singleLine = true,
                        // Zeigt einen Fehler an, wenn die Pins nicht übereinstimmen und das Feld nicht leer ist
                        isError = repeatFamilyPin.isNotEmpty() && familyPin != repeatFamilyPin
                    )
                }

                "join" -> {
                    // --- Felder zum Beitreten einer Familie ---
                    OutlinedTextField(
                        value = familyId,
                        onValueChange = { familyId = it },
                        label = { Text("FAMily ID *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Family ID") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = familyPin,
                        onValueChange = { familyPin = it },
                        label = { Text("FAMily PIN *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Family PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            }

            // Fehlermeldung
            val errorMessage = clientSideError ?: (validationState as? FamilyValidationState.Error)?.message
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "Next"-Button
            Button(
                onClick = {
                    clientSideError = null
                    userViewModel.resetFamilyValidationState() // NEU: Status vor jedem Klick zurücksetzen

                    when (selection) {
                        "create" -> {
                            if (familyName.isNotBlank() && familyPin.isNotBlank() && repeatFamilyPin.isNotBlank()) {
                                if (familyPin.length < 4) {
                                    clientSideError = "The PIN must be at least 4 digits long."
                                } else if (familyPin != repeatFamilyPin) {
                                    clientSideError = "The PINs do not match. Please check your input."
                                } else {
                                    /*
                                        onNext("create", familyName, familyPin)
                                    */
                                    onNavigateToRegister(
                                        "create",
                                        familyName,
                                        familyPin,
                                        familyName
                                    )
                                }
                            } else {
                                clientSideError = "Please fill out all fields for the new family."
                            }
                        }

                        "join" -> {
                            if (familyId.isNotBlank() && familyPin.isNotBlank()) {
                                userViewModel.validateFamilyCredentials(familyId, familyPin)
                            } else {
                                clientSideError = "Please provide the Family ID and PIN to join."
                            }
                        }

                        else -> {
                            clientSideError = "Please make a selection first."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // Der Button ist nur aktiv, wenn eine Auswahl getroffen wurde
                enabled = selection != null && validationState !is FamilyValidationState.Loading
            ) {
                if (selection == "join" && validationState is FamilyValidationState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Next")
                }
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login")
            }
        }
    }
}