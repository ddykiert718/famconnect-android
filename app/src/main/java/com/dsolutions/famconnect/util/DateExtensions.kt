package com.dsolutions.famconnect.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun LocalDate?.toEpochMillis(): Long {
    return this?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L
}

fun LocalDateTime?.toEpochMillis(): Long {
    return this?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L
}

