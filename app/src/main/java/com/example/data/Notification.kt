package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val type: String, // "Announcement", "Event Reminder", "Emergency Alert"
    val targetType: String, // "All", "Specific Grade", "Individual Parent"
    val targetValue: String, // "All", "Grade 10", or parent contact "8770206476"
    val timestamp: Long = System.currentTimeMillis()
)
