package com.dsolutions.famconnect.view.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsolutions.famconnect.model.Event
import com.dsolutions.famconnect.util.toLocalDate
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarViewCopy(
    events: List<Event>,
    onDateSelected: (LocalDate) -> Unit,
) {
    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = remember(currentMonth) {
        val firstOfMonth = currentMonth.atDay(1)
        val days = mutableListOf<LocalDate>()
        val firstDayOfWeek = (firstOfMonth.dayOfWeek.value + 6) % 7 // Monday = 1
        for (i in 0 until firstDayOfWeek) {
            days.add(firstOfMonth.minusDays((firstDayOfWeek - i).toLong()))
        }
        for (day in 1..currentMonth.lengthOfMonth()) {
            days.add(currentMonth.atDay(day))
        }
        while (days.size % 7 != 0) {
            days.add(days.last().plusDays(1))
        }

        days
    }

    // Month and navigation
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
        }
        Text(
            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    + " " + currentMonth.year,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Days of week
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So").forEach { day ->
            Text(
                text = day,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Days
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        //horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(daysInMonth) { date ->
            val isToday = date == today
            val isCurrentMonth = date.month == currentMonth.month
            val eventsForDate = events.filter {
                !it.startDate.toLocalDate().isAfter(date) &&
                        !it.endDate.toLocalDate().isBefore(date)
            }

            Box(
                modifier = Modifier
                    .height(120.dp) // Height of each calendar day
                    .fillMaxWidth()
                    .clip(
                        if (isToday) RoundedCornerShape(14.dp) // Today: Rounded corner!
                        else RoundedCornerShape(8.dp)
                    )
                    .background(
                        if (isToday) MaterialTheme.colorScheme.primary
                        else if (!isCurrentMonth) MaterialTheme.colorScheme.surfaceVariant
                        else Color.Transparent
                    )
                    .clickable {
                        onDateSelected(date)
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isToday) Color.White else if (isCurrentMonth) Color.Black else Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    val previewCount = 2
                    val previewEvents = eventsForDate.take(previewCount)
                    val remainingCount = eventsForDate.size - previewEvents.size

                    previewEvents.forEach { event ->
                        Spacer(modifier = Modifier.height(2.dp))

                        Box(
                            modifier = Modifier
                                //.padding(horizontal = 2.dp, vertical = 1.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    MaterialTheme.colorScheme.tertiary,
                                    RoundedCornerShape(4.dp)
                                )
                        ) {
                            Text(
                                text = event.title,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                softWrap = true
                            )
                        }
                    }

                    if (remainingCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+ $remainingCount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }

/*
                    // Event-Liste (max. 2 Events)
                    eventsForDate.take(2).forEach { event ->
                        Spacer(modifier = Modifier.height(2.dp))

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp, vertical = 1.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    MaterialTheme.colorScheme.tertiary,
                                    RoundedCornerShape(4.dp)
                                )
                        ) {
                            Text(
                                text = event.title,
                                fontSize = 10.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                softWrap = true
                            )
                        }
                    }*/
                }
            }
        }
    }
}