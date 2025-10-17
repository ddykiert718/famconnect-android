package com.dsolutions.famconnect.view.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dsolutions.famconnect.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventBottomSheet(
    date: LocalDate,
    events: List<Event>,
    onAddEventClicked: () -> Unit,
    onDismiss: () -> Unit,
    onEditClicked: (Event) -> Unit,
    onDeleteClicked: (Event) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    if (eventToDelete != null) {
        DeleteEventDialog(
            eventTitle = eventToDelete!!.title,
            onConfirm = {
                onDeleteClicked(eventToDelete!!)
                eventToDelete = null
            },
            onDismiss = {
                eventToDelete = null
            }
        )
    }

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss, sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.getDefault())), style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onAddEventClicked) {
                    Icon(Icons.Default.Add, contentDescription = "Add new event")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (events.isEmpty()) {
                Text("Keine Termine.")
            } else {
                events.forEach { event ->
                    EventCard(
                        event = event,
                        onEditClicked = { onEditClicked(event) },
                        onDeleteClicked = { eventToDelete = event }
                    )
                }
            }
        }
    }
}

/*
@Composable
fun EventCard(event: Event, onEditClicked: () -> Unit, onDeleteClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onEditClicked) {
                Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
            }

            IconButton(onClick = onDeleteClicked) {
                Icon(Icons.Default.Delete, contentDescription = "Löschen")
            }
        }


        val validParticipants = event.participants.filter { it.isNotBlank() }
        if (validParticipants.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Teilnehmer: ${event.participants.joinToString(", ")}",
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
*/