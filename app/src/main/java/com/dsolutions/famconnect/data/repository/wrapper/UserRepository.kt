package com.dsolutions.famconnect.data.repository.wrapper

import android.util.Log
import com.dsolutions.famconnect.data.repository.IUserRepository
import com.dsolutions.famconnect.model.Family
import com.dsolutions.famconnect.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.io.path.exists

class UserRepository : IUserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val familiesCollection = db.collection("families")
    private val usersCollection = db.collection("users")

    /**
     * Gibt den aktuell bei Firebase Auth angemeldeten Benutzer zurück.
     * Kann null sein, wenn niemand angemeldet ist.
     */
    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Lädt die Daten eines Benutzers aus Firestore anhand seiner UID.
     */
    override suspend fun getUserData(userId: String): User? {
        Log.d("UserRepository", "Getting user data for user: $userId")
        try {
            val userDoc = usersCollection.document(userId).get().await()

            if (userDoc.exists()) {
                Log.d("UserRepository", "Dokument existiert. Daten: ${userDoc.data}")
                // Konvertiere das Dokument in ein User-Objekt.
                // Die familyId ist bereits im Dokument gespeichert.
                return userDoc.toObject(User::class.java)
            }
            // Wenn das Dokument nicht existiert, wird der Fehler im ViewModel behandelt.
            return null

        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user data for ID: $userId", e)
            return null
        }
    }


    /**
     * Lädt die Daten einer bestimmten Familie aus Firestore anhand ihrer ID.
     */
    override suspend fun getFamilyData(familyId: String): Family? {
        return try {
            val document = familiesCollection.document(familyId).get().await()

            // --- DEBUGGING-CODE HINZUFÜGEN ---
            if (document.exists()) {
                Log.d("UserRepository", "Dokument ${familyId} existiert. Daten: ${document.data}")
                val familyObject = document.toObject(Family::class.java)
                if (familyObject == null) {
                    Log.e("UserRepository", "FEHLER: Konnte Dokument nicht in Family-Objekt umwandeln! Überprüfe die Datenklasse.")
                }
                familyObject
            } else {
                Log.w("UserRepository", "Dokument ${familyId} existiert NICHT.")
                null
            }
            /*
            val document = familiesCollection.document(familyId).get().await()
            document.toObject(Family::class.java)
        */
        } catch (e: Exception) {
            // Fehler beim Laden, z.B. keine Netzwerkverbindung
            null
        }
    }

    /**
     * Fügt einen neuen Benutzer zur Top-Level "users"-Collection hinzu.
     */
    override suspend fun addUser(user: User) {
        // Die familyId muss im User-Objekt gesetzt sein, bevor diese Funktion aufgerufen wird.
        require(!user.familyId.isNullOrBlank()) { "addUser requires a non-blank familyId in the User object" }

        // Verwende die UID von Firebase Auth als Dokumenten-ID und speichere das Objekt.
        usersCollection.document(user.id).set(user).await()
    }

    /**
     * Aktualisiert die Daten eines Benutzers in der Top-Level "users"-Collection.
     */
    override suspend fun updateUser(user: User) {
        val id = user.id
        require(!id.isNullOrBlank()) { "User ID cannot be null for update." }

        // Aktualisiere das Dokument direkt.
        // Die familyId ist Teil des 'user'-Objekts und wird mit aktualisiert.
        usersCollection.document(id).set(user).await()
    }

    /**
     * Löscht einen Benutzer aus der Top-Level "users"-Collection.
     */
    override suspend fun deleteUser(user: User) {
        val id = user.id
        require(!id.isNullOrBlank()) { "User ID cannot be null for delete" }

        // Lösche das Dokument direkt.
        usersCollection.document(id).delete().await()
    }

    /**
     * Meldet einen Benutzer mit E-Mail und Passwort bei Firebase Authentication an.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @throws Exception, wenn die Anmeldung fehlschlägt (z.B. falsches Passwort, Benutzer nicht gefunden).
     *                     Die Exception wird zur weiteren Behandlung im ViewModel weitergeworfen.
     */
    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        try {
            // Schritt 1: Benutzer anmelden
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Login succeeded but user is null.")

            Log.d("UserRepository", "FirebaseUser $firebaseUser")

            // Schritt 2: WICHTIG - Auf das ID-Token warten, um den Auth-Status zu synchronisieren.
            firebaseUser.getIdToken(true).await()
            Log.d("UserRepository", "ID Token refreshed, auth state is guaranteed on backend.")

            // Schritt 3: Das erfolgreiche AuthResult zurückgeben
            return authResult

        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }

    /**
     * Erstellt einen neuen Benutzer in Firebase Authentication mit E-Mail und Passwort.
     * @param email Die E-Mail-Adresse des neuen Benutzers.
     * @param password Das Passwort des neuen Benutzers.
     * @return Das erstellte [FirebaseUser]-Objekt.
     * @throws Exception, wenn die Erstellung fehlschlägt (z.B. E-Mail bereits vorhanden, schwaches Passwort).
     *                     Die Exception wird zur weiteren Behandlung im ViewModel weitergeworfen.
     */
    override suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user ?: throw Exception("Firebase user creation failed: user is null.")
        } catch (e: Exception) {
            // Wirft die Exception weiter, damit das ViewModel sie fangen kann
            throw e
        }
    }

    /**
     * Versucht, einer bestehenden Familie beizutreten.
     * @param familyId Die vom Benutzer eingegebene Familien-ID.
     * @param familyPin Die vom Benutzer eingegebene Familien-PIN.
     * @return Die validierte Familien-ID bei Erfolg.
     * @throws Exception, wenn die Familie nicht gefunden wird oder die PIN falsch ist.
     */
    override suspend fun joinFamily(familyId: String, familyPin: String): String {
        try {
            val documentSnapshot = familiesCollection.document(familyId).get().await()

            if (!documentSnapshot.exists()) {
                throw Exception("Family with ID '$familyId' not found.")
            }

            // Wandle das Firestore-Dokument in dein Family-Datenmodell um
            val family = documentSnapshot.toObject(Family::class.java)
                ?: throw Exception("Failed to parse family data.")

            if (family.pin != familyPin) {
                throw Exception("Incorrect Family PIN.")
            }

            // Wenn alles korrekt ist, gib die ID zurück.
            return familyId
        } catch (e: Exception) {
            // Fange die Exception und wirf sie mit einer klareren Nachricht weiter.
            throw Exception("Could not join family: ${e.message}")
        }
    }

    /**
     * Erstellt eine neue Familie in Firestore.
     * @param familyName Der Name der neuen Familie.
     * @param familyPin Die PIN für die neue Familie.
     * @return Die automatisch generierte ID der neuen Familie.
     */
    override suspend fun createFamily(familyName: String, familyPin: String): String {
        val authUser = auth.currentUser
            ?: throw Exception("User not authenticated, cannot create family.")


        try {
            // Erstelle ein neues Dokument in der "families"-Collection.
            // Firestore generiert automatisch eine einzigartige ID.
            val familyDocument = familiesCollection.document()

            val newFamily = Family(
                id = familyDocument.id, // Die von Firestore generierte ID
                name = familyName,
                pin = familyPin,
                ownerId = authUser.uid
            )

            // Speichere das neue Familienobjekt in Firestore.
            familyDocument.set(newFamily).await()

            // Gib die ID des neuen Dokuments zurück.
            return familyDocument.id
        } catch (e: Exception) {
            // Fange die Exception und wirf sie mit einer klareren Nachricht weiter.
            throw Exception("Could not create family: ${e.message}")
        }
    }
}