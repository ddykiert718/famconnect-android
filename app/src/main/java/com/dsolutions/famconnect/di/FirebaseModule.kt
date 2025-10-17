package com.dsolutions.famconnect.di // Passen Sie den Paketnamen an Ihr Projekt an

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Sagt Hilt, dass diese Anweisungen für die gesamte App gelten
object FirebaseModule {

    @Provides
    @Singleton // Wir wollen nur eine Instanz von FirebaseAuth für die gesamte App
    fun provideFirebaseAuth(): FirebaseAuth {
        // Hier ist die Anleitung: "Wenn jemand FirebaseAuth anfordert,
        // rufe FirebaseAuth.getInstance() auf und gib das Ergebnis zurück."
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton // Dasselbe für Firestore
    fun provideFirebaseFirestore(): FirebaseFirestore {
        // Anleitung für Firestore: "Rufe FirebaseFirestore.getInstance() auf."
        return FirebaseFirestore.getInstance()
    }

    // Fügen Sie hier weitere @Provides-Funktionen für andere Firebase-Dienste hinzu,
    // falls Sie diese benötigen (z.B. Firebase Storage).
}