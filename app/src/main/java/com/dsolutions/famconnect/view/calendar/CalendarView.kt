package com.dsolutions.famconnect.view.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsolutions.famconnect.model.Event
import com.dsolutions.famconnect.util.toLocalDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    events: List<Event>,
    onDateSelected: (LocalDate) -> Unit,
) {
    val today = remember { LocalDate.now() }
    val baseMonth = remember { YearMonth.now() }
    val initialPage = Int.MAX_VALUE / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )

    val scope = rememberCoroutineScope()

    val currentMonth = baseMonth.plusMonths((pagerState.currentPage - initialPage).toLong())

    // Header with arrows
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
        }
        Text(
            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                    if (currentMonth.year != today.year) " ${currentMonth.year}" else "",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "So").forEach { day ->
            Text(
                text = day,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val month = baseMonth.plusMonths((page - initialPage).toLong())
        MonthContent(
            month = month,
            today = today,
            events = events,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
private fun MonthContent(
    month: YearMonth,
    today: LocalDate,
    events: List<Event>,
    onDateSelected: (LocalDate) -> Unit
) {
    val maxVisibleRows = 3

    val daysInMonth = remember(month) {
        val firstOfMonth = month.atDay(1)
        val days = mutableListOf<LocalDate>()
        val firstDayOfWeek = (firstOfMonth.dayOfWeek.value + 6) % 7
        for (i in 0 until firstDayOfWeek) {
            days.add(firstOfMonth.minusDays((firstDayOfWeek - i).toLong()))
        }
        for (day in 1..month.lengthOfMonth()) {
            days.add(month.atDay(day))
        }
        while (days.size % 7 != 0) {
            days.add(days.last().plusDays(1))
        }
        days
    }

    val weeks = daysInMonth.chunked(7)

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(weeks) { week ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { date ->
                        val isToday = date == today
                        val isCurrentMonth = date.month == month.month

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onDateSelected(date) }
                                .padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(
                                        if (isToday) Color.Red.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = when {
                                        isToday -> Color.Red
                                        !isCurrentMonth -> Color.Gray
                                        else -> Color.Black
                                    }
                                )
                            }
                        }
                    }
                }

                val maxSlotsPerDayVisible = 3
                val maxRenderSlots = maxSlotsPerDayVisible + 1

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .height((maxRenderSlots * 22).dp)   // <-- Platz für 3 Slots + "+X weitere"
                        .padding(top = 25.dp)
                ) {
                    val occupied = Array(7) { BooleanArray(maxRenderSlots) }
                    val usedSlotCount = IntArray(7)

                    val weekEvents = events.filter { e ->
                        val start = e.startDate.toLocalDate()
                        val end = e.endDate.toLocalDate()
                        !start.isAfter(week.last()) && !end.isBefore(week.first())
                        !start.isAfter(week.last()) && !end.isBefore(week.first())
                    }

                    // find slot for every event
                    weekEvents.forEach { event ->
                        val startDate = event.startDate.toLocalDate()
                        val endDate = event.endDate.toLocalDate()

                        val overlapStart = maxOf(startDate, week.first())
                        val overlapEnd = minOf(endDate, week.last())

                        val startIndex = week.indexOf(overlapStart).takeIf { it >= 0 } ?: 0
                        val endIndex = week.indexOf(overlapEnd).takeIf { it >= 0 } ?: 6

                        var slot = 0
                        while (slot < maxVisibleRows &&
                            (startIndex..endIndex).any { occupied[it][slot] }
                        ) {
                            slot++
                        }

                        for (i in startIndex..endIndex) {
                            occupied[i][slot] = true
                            usedSlotCount[i] = usedSlotCount[i] + 1
                        }

                        if (slot < maxSlotsPerDayVisible) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = (slot * 22).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (startIndex > 0) {
                                    Spacer(modifier = Modifier.weight(startIndex.toFloat()))
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 1.dp)
                                        .weight((endIndex - startIndex + 1).toFloat())
                                        .height(20.dp)
                                        .background(
                                            MaterialTheme.colorScheme.tertiary,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable { onDateSelected(event.startDate.toLocalDate()) }
                                        .padding(start = 2.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = event.title,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (endIndex < 6) {
                                    Spacer(modifier = Modifier.weight((6 - endIndex).toFloat()))
                                }
                            }
                        }

                    }

                    // Nach dem Zeichnen der Events: pro Tag prüfen, ob es versteckte Slots gibt
                    (0..6).forEach { dayIndex ->
                        val totalUsed = usedSlotCount[dayIndex]
                        if (totalUsed > maxSlotsPerDayVisible) {
                            val moreCount = totalUsed - maxSlotsPerDayVisible
                            // "Slot" für das "+X weitere" steht direkt unter dem letzten sichtbaren Slot
                            val visibleSlot = maxSlotsPerDayVisible
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = (visibleSlot * 22).dp)
                            ) {
                                if (dayIndex > 0) {
                                    Spacer(modifier = Modifier.weight(dayIndex.toFloat()))
                                }
                                Text(
                                    text = "+$moreCount",
                                    fontSize = 10.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (dayIndex < 6) {
                                    Spacer(modifier = Modifier.weight((6 - dayIndex).toFloat()))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
