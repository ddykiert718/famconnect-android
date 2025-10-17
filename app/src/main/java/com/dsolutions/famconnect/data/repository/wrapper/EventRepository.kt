package com.dsolutions.famconnect.data.repository.wrapper

import com.dsolutions.famconnect.data.repository.IEventRepository
import com.dsolutions.famconnect.model.Event
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EventRepository : IEventRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun getEventSubCollection(familyId: String): CollectionReference {
        return db.collection("families")
            .document(familyId)
            .collection("events")
    }

    override suspend fun addEvent(event: Event) {
        val familyId = event.familyId
        require(!familyId.isNullOrBlank()) { "familyId must not be null or blank" }

        val eventsSubCollection = getEventSubCollection(familyId)


        val docRef = eventsSubCollection.document()
        event.id = docRef.id
        docRef.set(event).await()
    }

    override fun getEventsForFamilyFlow(familyId: String): Flow<List<Event>> = callbackFlow {
        val eventsSubCollection = getEventSubCollection(familyId)

        val listenerRegistration: ListenerRegistration = eventsSubCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { it.toObject<Event>() } ?: emptyList()
                trySend(events).isSuccess
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun updateEvent(event: Event) {
        val id = event.id ?: throw IllegalArgumentException("Event-ID darf nicht null sein für ein Update.")
        val eventsSubCollection = getEventSubCollection(event.familyId)

        eventsSubCollection.document(id).set(event).await()
    }

    override suspend fun deleteEvent(event: Event) {
        val eventsSubCollection = getEventSubCollection(event.familyId)

        eventsSubCollection.document(event.id).delete().await()
    }
}
