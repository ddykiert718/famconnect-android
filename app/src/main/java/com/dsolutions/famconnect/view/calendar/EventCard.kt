package com.dsolutions.famconnect.view.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.graphicsLayer
import com.dsolutions.famconnect.model.Event
import com.dsolutions.famconnect.util.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EventCard(
    event: Event,
    onEditClicked: (Event) -> Unit,
    onDeleteClicked: (Event) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .clickable { expanded = !expanded }
            .animateContentSize()  // animiert das Ein-/Ausklappen
    ) {
        // Titel + Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = event.title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row {
                IconButton(onClick = { onEditClicked(event) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDeleteClicked(event) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Show less" else "Show more",
                        modifier = Modifier.graphicsLayer { rotationZ = if (expanded) 0f else 270f }
                    )
                }
            }
        }

        // Zeit
        val formatter = if (event.allDay) {
            DateTimeFormatter.ofPattern("E, dd.MM.", Locale.GERMANY)
        } else {
            DateTimeFormatter.ofPattern("E, dd.MM., HH:mm", Locale.GERMANY)
        }
        val startFormatted = event.startDate.toLocalDateTime().format(formatter)
        val endFormatted = event.endDate.toLocalDateTime().format(formatter)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccessTime, contentDescription = "Time", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$startFormatted – $endFormatted",
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Ort
        if (!event.location.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = "Location", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = event.location,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Teilnehmer
        val validParticipants = event.participants.filter { it.isNotBlank() }
        if (validParticipants.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = "Participants", modifier = Modifier.size(18.dp))
                Text(
                    text = " Teilnehmer: ${validParticipants.joinToString(", ")}",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        // Erweiterte Details bei expandiertem Zustand
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))

            if (!event.repeat.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Wiederholung: ${event.repeat}",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!event.notification.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Erinnerung: ${event.notification}",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!event.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notizen: ${event.notes}",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }
}
