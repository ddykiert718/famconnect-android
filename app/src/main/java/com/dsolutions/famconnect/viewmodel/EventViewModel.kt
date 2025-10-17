package com.dsolutions.famconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsolutions.famconnect.data.repository.IEventRepository
import com.dsolutions.famconnect.model.Event
import com.dsolutions.famconnect.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: IEventRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    // Ein Job, um das Laden der Events zu steuern
    private var loadEventsJob: Job? = null

    private var currentFamilyId: String? = null

    // Diese Funktion wird aufgerufen, sobald der Benutzer angemeldet ist.
    fun startLoadingEvents(familyId: String) {
        // Verhindert mehrfaches Starten
        if (loadEventsJob?.isActive == true && currentFamilyId == familyId) return

        // Wenn keine familyId vorhanden ist, brechen Sie ab.
        if (familyId.isBlank()) {
            // Optional: Loggen Sie einen Fehler oder behandeln Sie den Fall
            return
        }

        this.currentFamilyId = familyId

        loadEventsJob = viewModelScope.launch {
            // Hier Ihre Logik zum Laden der Events
            repository.getEventsForFamilyFlow(familyId).collect {
                _events.value = it
            }
        }
    }

    // Optional: Wenn der Benutzer sich abmeldet, stoppen Sie das Lauschen auf Events
    fun stopLoadingEvents() {
        loadEventsJob?.cancel()
        _events.value = emptyList() // Leeren Sie die Liste
    }

    /*
    init {
        //loadEvents()
    }

    fun loadEvents(familyId: String = "testFAM") {
        viewModelScope.launch {
            repository.getEventsForFamilyFlow(familyId).collect {
                _events.value = it
            }
        }
    }
    */

    fun getEventsForDate(date: LocalDate): List<Event> {
        return _events.value.filter {
            it.startDate.toLocalDate() == date
        }
    }

    fun addEvent(
        event: Event,
        userId: String?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Event lokal und in Firestore speichern
                if (event.familyId.isBlank() && currentFamilyId != null) {
                    event.familyId = currentFamilyId!!
                }
                event.createdBy = userId ?: ""
                repository.addEvent(event)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateEvent(
        event: Event,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (event.familyId.isBlank() && currentFamilyId != null) {
                    event.familyId = currentFamilyId!!
                }
                repository.updateEvent(event)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun deleteEvent(event: Event, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                if (event.familyId.isBlank() && currentFamilyId != null) {
                    event.familyId = currentFamilyId!!
                }
                repository.deleteEvent(event)
                _events.update { currentList ->
                    currentList.filterNot { it.id == event.id }
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /*
    fun getEventsForDate(date: LocalDate): List<Event> {
        val zoneId = ZoneId.systemDefault()
        val startMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        Log.d("getEventsForDate", "*******")
        return events.value.filter {
            Log.d("getEventsForDate", "Info: filter get events" + it.startDate.toLocalDate().dayOfMonth)
            it.startDate in startMillis until endMillis
        }
    }*/
}
