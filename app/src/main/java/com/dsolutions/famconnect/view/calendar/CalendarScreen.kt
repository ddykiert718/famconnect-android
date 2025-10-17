package com.dsolutions.famconnect.view.calendar

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dsolutions.famconnect.util.toLocalDate
import com.dsolutions.famconnect.model.Event
import com.dsolutions.famconnect.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

// Datei: CalendarScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    eventViewModel: EventViewModel,
    userId: String?
) {
    val events by eventViewModel.events.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showEventDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editingEvent by remember { mutableStateOf<Event?>(null) }

    if (editingEvent != null) {
        EventDialog(
            date = selectedDate!!,
            initialEvent = editingEvent,
            onDismiss = { editingEvent = null },
            onSave = { updatedEvent ->
                eventViewModel.updateEvent(
                    updatedEvent,
                    onSuccess = {
                        Log.d("Event", "Event successfully updated")
                        editingEvent = null
                    },
                    onError = { e ->
                        Log.e("Event", "Error while updating the event: ${e.message}")
                        editingEvent = null
                    }
                )
            },
            sheetState = sheetState
        )
    }

    if (showEventDialog && selectedDate != null) {
        EventDialog(
            date = selectedDate!!,
            onSave = { event ->
                eventViewModel.addEvent(
                    event,
                    userId,
                    onSuccess = {
                        Log.d("Event", "Event successfully saved")
                    },
                    onError = { e ->
                        Log.e("Event", "Error while saving the event: ${e.message}")
                    }
                )
            },
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showEventDialog = false
                }
            },
            sheetState = sheetState
        )
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                sheetState.show()
            }
        }
    }

    // Hauptlayout mit Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Kalender nimmt den ganzen verfügbaren Platz
        CalendarView(
            events = events,
            onDateSelected = { date ->
                selectedDate = date
            },
        )

    }

    selectedDate?.let { date ->
        val eventsForDate = events.filter { date in it.startDate.toLocalDate()..it.endDate.toLocalDate() }
        EventBottomSheet(
            date = date,
            events = eventsForDate,
            onAddEventClicked = {
                showEventDialog = true
            },
            onDismiss = { selectedDate = null },
            onEditClicked = { event -> editingEvent = event },
            onDeleteClicked = { event ->
                eventViewModel.deleteEvent(
                    event,
                    onSuccess = { Log.d("Event", "Event successfully deleted") },
                    onError = { e -> Log.e("Event", "Error while deleting event: ${e.message}") }
                )
            }
        )
    }
}


