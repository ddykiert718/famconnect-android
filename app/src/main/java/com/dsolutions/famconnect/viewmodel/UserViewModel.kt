package com.dsolutions.famconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.dsolutions.famconnect.data.repository.IUserRepository
import com.dsolutions.famconnect.model.Family
import com.dsolutions.famconnect.model.User
import com.dsolutions.famconnect.view.common.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed interface FamilyValidationState {
    data object Idle : FamilyValidationState // Ruhezustand
    data object Loading : FamilyValidationState // Prüfung läuft
    data class Success(val familyId: String, val familyName: String) : FamilyValidationState // Prüfung erfolgreich
    data class Error(val message: String) : FamilyValidationState // Prüfung fehlgeschlagen
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: IUserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.UNKNOWN)
    val authState: StateFlow<AuthState> = _authState

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    private val _familyState = MutableStateFlow<Family?>(null)
    val familyState: StateFlow<Family?> = _familyState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _familyValidationState = MutableStateFlow<FamilyValidationState>(FamilyValidationState.Idle)
    val familyValidationState: StateFlow<FamilyValidationState> = _familyValidationState.asStateFlow()

    init {
        // Lauschen Sie auf Änderungen des Authentifizierungsstatus
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }

        checkCurrentUser()
    }

    /**
     * Überprüft, ob bereits ein Benutzer von einer früheren Sitzung angemeldet ist.
     */
    private fun checkCurrentUser() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)

            if (repository.getCurrentUser() != null) {
                // Benutzer ist bereits eingeloggt, lade Daten und gehe zum MainScreen
                _authState.value = AuthState.AUTHENTICATED
                loadUserDataAndFamily()
                _currentScreen.value = Screen.Home
                Log.d("UserViewModel", "User is authenticated.")
            } else {
                // Kein Benutzer eingeloggt, zeige LoginScreen
                _authState.value = AuthState.UNAUTHENTICATED
                _currentScreen.value = Screen.Login
                Log.d("UserViewModel", "User is not authenticated.")
            }
        }
    }

    /**
     * Validiert Familien-ID und PIN, bevor zum Registrierungsbildschirm navigiert wird.
     * Diese Funktion wird vom ChooseFamilyScreen aufgerufen.
     */
    fun validateFamilyCredentials(familyId: String, familyPin: String) {
        viewModelScope.launch {
            _familyValidationState.value = FamilyValidationState.Loading
            try {
                // Dein UserRepository hat bereits die perfekte Funktion dafür!
                // Sie wirft eine Exception bei falscher ID oder falschem PIN.
                repository.joinFamily(familyId, familyPin)

                // Wenn kein Fehler auftritt, holen wir noch den Familiennamen für die UI
                val familyData = repository.getFamilyData(familyId)
                if (familyData != null) {
                    _familyValidationState.value = FamilyValidationState.Success(familyData.id, familyData.name)
                } else {
                    // Sollte nicht passieren, wenn joinFamily erfolgreich war, aber zur Sicherheit.
                    throw Exception("Konnte Familiendaten nach erfolgreicher Validierung nicht laden.")
                }

            } catch (e: Exception) {
                // Hier fangen wir die Fehler "Family not found" oder "Incorrect PIN" ab.
                Log.e("UserViewModel", "Family validation failed", e)
                _familyValidationState.value = FamilyValidationState.Error(e.message ?: "Invalid family id or pin.")
            }
        }
    }

    /**
     * Setzt den Validierungsstatus zurück, wenn der Benutzer den Screen verlässt
     * oder einen neuen Versuch startet.
     */
    fun resetFamilyValidationState() {
        _familyValidationState.value = FamilyValidationState.Idle
    }

    /**
     * Lädt die Daten für den aktuell angemeldeten Benutzer und die zugehörige Familie.
     */
    fun loadUserDataAndFamily() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                // 1. Aktuellen Firebase-Benutzer holen
                val firebaseUser = repository.getCurrentUser()
                if (firebaseUser == null) {
                    // Kein Benutzer angemeldet, zum Login-Screen navigieren
                    _authState.value = AuthState.UNAUTHENTICATED
                    _currentScreen.value = Screen.Login
                    throw Exception("No user is currently logged in.")
                }

                Log.d("Family", "Family ${firebaseUser.uid}")
                Log.d("Family", "UserData ${repository.getUserData(firebaseUser.uid)}")


                // 2. Benutzerdaten aus Firestore laden
                val user = repository.getUserData(firebaseUser.uid)
                if (user == null) {
                    throw Exception("User data not found in Firestore.")
                }
                _userState.value = user

                // 3. Familiendaten aus Firestore laden
                val familyId = user.familyId
                if (familyId.isNullOrBlank()) {
                    throw Exception("User not assigned to any family.")
                }

                val family = repository.getFamilyData(familyId)
                if (family == null) {
                    throw Exception("Family data not found for familyId: $familyId")
                }
                _familyState.value = family

                Log.d("UserViewModel", "Successfully loaded family data: ${family.name}")

            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to load user and family data", e)
                _errorState.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Schritt 1: Benutzer anmelden
                val authResult = repository.signInWithEmailAndPassword(email, password)
                val firebaseUser = authResult.user
                    ?: throw IllegalStateException("Login succeeded but user is null.")

                // Schritt 2: WICHTIG - Auf das ID-Token warten.
                // Dies erzwingt die Synchronisation des Auth-Status mit dem Backend.
                // Der suspendCoroutine-Wrapper in einer Hilfsfunktion ist hier ideal.
                firebaseUser.getIdToken(true) // 'true' erzwingt eine Aktualisierung
                Log.d("UserViewModel", "ID Token refreshed, auth state is guaranteed on backend.")

                // Schritt 3: Erst JETZT die Daten laden und zum MainScreen navigieren
                // Da der Login erfolgreich war, können wir direkt die Daten laden.
                loadUserDataAndFamily()

                // Schritt 4: UI benachrichtigen
                onSuccess()
            } catch (e: Exception) {
                // Bei Fehler die Nachricht an die UI weitergeben
                onError(e.message ?: "An unknown login error occurred.")
            }
        }
    }

    fun addUser(
        user: User,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // User in Firestore speichern
                repository.addUser(user)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateUser(
        user: User,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateUser(user)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun deleteUser(
        user: User,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.deleteUser(user)

                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun registerUser(
        user: User,
        password: String,
        familySelection: String,
        familyIdOrName: String,
        familyPin: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Schritt 1: Benutzer in Firebase Authentication erstellen
                val firebaseUser = repository.createUserWithEmailAndPassword(user.email, password)
                val userId = firebaseUser.uid
                Log.d("UserViewModel", "Firebase Auth user created with UID: $userId")

                // Schritt 2: Familienlogik (Erstellen oder Beitreten)
                val familyId: String
                if (familySelection == "create") {
                    // Neue Familie erstellen und die generierte ID erhalten
                    familyId = repository.createFamily(familyIdOrName, familyPin)
                    Log.d("UserViewModel", "New family created with ID: $familyId")
                } else { // "join"
                    // Beim Beitreten nehmen wir einfach die vom Benutzer eingegebene ID.
                    // Die Validierung erfolgt durch die Sicherheitsregel bei `addUser`.
                    familyId = familyIdOrName
                }

                // Schritt 3: Benutzerobjekt mit den neuen IDs aktualisieren
                val finalUser = user.copy(
                    id = userId,
                    familyId = familyId
                )

                finalUser.familyPin = familyPin

                // Schritt 4: Benutzerdokument in Firestore speichern.
                // HIER findet die eigentliche PIN-Prüfung durch die Sicherheitsregeln statt.
                // Wenn der Pin falsch ist, schlägt DIESER Aufruf mit PERMISSION_DENIED fehl.
                repository.addUser(finalUser)
                Log.d("UserViewModel", "User document saved to Firestore for user: $userId")

                // Alles erfolgreich
                onSuccess()

            } catch (e: Exception) {
                // Bei jedem Fehler wird die Exception abgefangen
                Log.e("UserViewModel", "Registration failed", e)
                // Wenn der Fehler hier auftritt, war der Pin falsch oder die Familie existiert nicht.
                if (e.message?.contains("PERMISSION_DENIED") == true) {
                    onError("Error while joining: family id or pin invalid.")
                }
                // Die Fehlermeldung wird an die UI weitergegeben
                onError(e.message ?: "An unknown error occurred.")
            }
        }
    }

}
