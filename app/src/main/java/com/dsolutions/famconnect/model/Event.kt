package com.dsolutions.famconnect.model

data class Event(
    var id: String = "",
    var title: String = "",
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var allDay: Boolean = false,
    var location: String = "",
    var notification: String = "",
    var repeat: String = "",
    var notes: String = "",
    var createdBy: String = "",
    var familyId: String = "",
    var participants: List<String> = emptyList(),
)