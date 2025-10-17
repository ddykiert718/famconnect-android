package com.dsolutions.famconnect.view.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dsolutions.famconnect.R
import com.dsolutions.famconnect.model.Event
import com.dsolutions.famconnect.ui.theme.customDateColors
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    date: LocalDate?,
    onDismiss: () -> Unit,
    onSave: (event: Event) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    initialEvent: Event? = null
) {
    val textFieldShape = RoundedCornerShape(4.dp)

    var mandatoryFieldsMissing by remember { mutableStateOf(false) }
    var titleFieldValue by remember { mutableStateOf("") }
    var participantsFieldValue by remember { mutableStateOf("") }
    var locationFieldValue by remember { mutableStateOf("") }
    var notificationFieldValue by remember { mutableStateOf("") }
    var repeatFieldValue by remember { mutableStateOf("") }
    var notesFieldValue by remember { mutableStateOf("") }
    var allDayChecked by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val initialStartDateTime = if (initialEvent != null) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(initialEvent.startDate), ZoneId.systemDefault())
    } else {
        date?.atStartOfDay() // Fallback, falls kein Event, aber ein Datum vom Kalender da ist
    }

    val initialEndDateTime = if (initialEvent != null) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(initialEvent.endDate), ZoneId.systemDefault())
    } else {
        date?.atStartOfDay() // Fallback
    }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialStartDateTime?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialEndDateTime?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    )
    val startTimePickerState = rememberTimePickerState(
        initialHour = initialStartDateTime?.hour ?: 0,
        initialMinute = initialStartDateTime?.minute ?: 0,
        is24Hour = true
    )
    val endTimePickerState = rememberTimePickerState(
        initialHour = initialEndDateTime?.hour ?: 0,
        initialMinute = initialEndDateTime?.minute ?: 0,
        is24Hour = true
    )

    var selectedStartDate by remember { mutableStateOf(initialStartDateTime?.toLocalDate()) }
    var selectedEndDate by remember { mutableStateOf(initialEndDateTime?.toLocalDate()) }
    var selectedStartTime by remember { mutableStateOf(Pair(initialStartDateTime?.hour ?: 0, initialStartDateTime?.minute ?: 0)) }
    var selectedEndTime by remember { mutableStateOf(Pair(initialEndDateTime?.hour ?: 0, initialEndDateTime?.minute ?: 0)) }


    // Aktualisiere selectedStartDate und selectedEndDate, wenn sich die DatePicker-Zustände ändern
    LaunchedEffect(startDatePickerState.selectedDateMillis) {
        startDatePickerState.selectedDateMillis?.let { millis ->
            val newStartDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
            selectedStartDate = newStartDate

            if (selectedEndDate != null && newStartDate.isAfter(selectedEndDate)) {
                selectedEndDate = newStartDate
            }
        }
        if (showStartDatePicker) { // Nur schließen, wenn er offen war und ein Datum ausgewählt wurde
            showStartDatePicker = false
        }
    }

    LaunchedEffect(endDatePickerState.selectedDateMillis) {
        endDatePickerState.selectedDateMillis?.let { millis ->
            val newEndDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
            selectedEndDate = newEndDate

            if (selectedStartDate != null && newEndDate.isBefore(selectedStartDate)) {
                selectedStartDate = newEndDate
            }
        }
        if (showEndDatePicker) { // Nur schließen, wenn er offen war und ein Datum ausgewählt wurde
            showEndDatePicker = false
        }
    }

    LaunchedEffect(startTimePickerState.hour, startTimePickerState.minute) {
        selectedStartTime = Pair(startTimePickerState.hour, startTimePickerState.minute)
    }

    LaunchedEffect(endTimePickerState.hour, endTimePickerState.minute) {
        selectedEndTime = Pair(endTimePickerState.hour, endTimePickerState.minute)
    }

    if (initialEvent !== null) {
        titleFieldValue = initialEvent.title
        allDayChecked = initialEvent.allDay
        participantsFieldValue = initialEvent.participants.joinToString(separator = ",")
        locationFieldValue = initialEvent.location
        notificationFieldValue = initialEvent.notification
        repeatFieldValue = initialEvent.repeat
        notesFieldValue = initialEvent.notes
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("New Event", style = MaterialTheme.typography.titleLarge)

            if (mandatoryFieldsMissing) {
                Text("Please fill in all mandatory fields", color = Color.Red)
            }

            OutlinedTextField(
                value = titleFieldValue,
                onValueChange = {
                    titleFieldValue = it
                },
                label = {
                    Text(
                        "Title *", fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (mandatoryFieldsMissing) {
                            Modifier.border(width = 1.dp, Color.Red)
                        } else {
                            Modifier
                        }
                    ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.title),
                        contentDescription = "Title"
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { allDayChecked = !allDayChecked }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = allDayChecked,
                    onCheckedChange = { isChecked ->
                        allDayChecked = isChecked
                    }
                )
                Text(
                    text = "All day",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Start date and start time
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (showStartDatePicker || showStartTimePicker) 2.dp else 1.dp,
                            color = if (showStartDatePicker || showStartTimePicker) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = textFieldShape
                        ),
                ) {
                    Text(
                        "Start",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background, // Oder eine spezifischere Farbe, falls abweichend
                            contentColor = MaterialTheme.colorScheme.onSurface // Optional: Textfarbe anpassen
                        ),
                        onClick = {
                            showStartDatePicker = !showStartDatePicker
                            showEndDatePicker = false
                            showStartTimePicker = false
                            showEndTimePicker = false
                        },
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text(selectedStartDate?.toString() ?: "Date")
                    }

                    if (!allDayChecked) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            onClick = {
                                showStartTimePicker = !showStartTimePicker
                                showEndTimePicker = false
                                showStartDatePicker = false
                                showEndDatePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(formatTime(selectedStartTime.first, selectedStartTime.second))
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (showEndDatePicker || showEndTimePicker) 2.dp else 1.dp,
                            color = if (showEndDatePicker || showEndTimePicker) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = textFieldShape
                        ),
                ) {
                    Text(
                        "End",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background, // Oder eine spezifischere Farbe, falls abweichend
                            contentColor = MaterialTheme.colorScheme.onSurface // Optional: Textfarbe anpassen
                        ),
                        onClick = {
                            showEndDatePicker = !showEndDatePicker
                            showStartDatePicker = false
                            showStartTimePicker = false
                            showEndTimePicker = false
                        },
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text(selectedEndDate?.toString() ?: "Date")
                    }

                    if (!allDayChecked)
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            onClick = {
                                showEndTimePicker = !showEndTimePicker
                                showStartTimePicker = false
                                showStartDatePicker = false
                                showEndDatePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(formatTime(selectedEndTime.first, selectedEndTime.second))
                        }
                }
            }

            if (showStartDatePicker) {
                DatePicker(
                    state = startDatePickerState,
                    title = {
                        Text(
                            text = "Start date", // Ihr gewünschter Titel
                            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                    // Optional: Füge hier weitere Anpassungen für den DatePicker hinzu
                )
            }

            if (showEndDatePicker) {
                DatePicker(
                    state = endDatePickerState,
                    title = {
                        Text(
                            text = "End date", // Ihr gewünschter Titel
                            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                    // Optional: Füge hier weitere Anpassungen für den DatePicker hinzu
                )
            }

            if (showStartTimePicker) {
                TimePicker(state = startTimePickerState, modifier = Modifier.fillMaxWidth())
            }

            if (showEndTimePicker) {
                TimePicker(state = endTimePickerState, modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(
                value = participantsFieldValue,
                onValueChange = {
                    participantsFieldValue = it
                },
                label = { Text("Participants") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Participants"
                    )
                }
            )

            OutlinedTextField(
                value = locationFieldValue,
                onValueChange = {
                    locationFieldValue = it
                },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location"
                    )
                }
            )

            OutlinedTextField(
                value = notificationFieldValue,
                onValueChange = {
                    notificationFieldValue = it
                },
                label = { Text("Notifications") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            )

            OutlinedTextField(
                value = repeatFieldValue,
                onValueChange = {
                    repeatFieldValue = it
                },
                label = { Text("Repeat") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Repeat"
                    )
                }
            )

            OutlinedTextField(
                value = notesFieldValue,
                onValueChange = {
                    notesFieldValue = it
                },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.description),
                        contentDescription = "Notes"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titleFieldValue.isBlank()) {
                        mandatoryFieldsMissing = true
                    } else {
                        val event = Event(
                            id = initialEvent?.id ?: "",
                            title = titleFieldValue,
                            allDay = allDayChecked,
                            startDate = if (allDayChecked) {
                                combineDateAndTime(selectedStartDate, Pair(0, 0))
                            } else {
                                combineDateAndTime(selectedStartDate, selectedStartTime)
                            },
                            endDate = if (allDayChecked) {
                                combineDateAndTime(selectedEndDate, Pair(23, 59))
                            } else {
                                combineDateAndTime(selectedEndDate, selectedEndTime)
                            },
                            participants = listOf(participantsFieldValue),
                            location = locationFieldValue,
                            notification = notificationFieldValue,
                            repeat = repeatFieldValue,
                            notes = notesFieldValue,
                            createdBy = "")
                        onSave(event)
                        onDismiss()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val formattedHour = hour.toString().padStart(2, '0')
    val formattedMinute = minute.toString().padStart(2, '0')
    return "$formattedHour:$formattedMinute"
}

fun combineDateAndTime(date: LocalDate?, timePair: Pair<Int, Int>): Long {
    if (date == null) return 0L
    val time = LocalTime.of(timePair.first, timePair.second)
    val dateTime = LocalDateTime.of(date, time)
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
