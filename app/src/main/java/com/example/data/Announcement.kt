package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val targetType: String, // "All", "Grade", "Individual"
    val targetValue: String, // Empty for All, e.g. "Grade 5" for Grade, parent email for Individual
    val date: String, // Date representation string
    val type: String // "Alert", "Announcement", "Reminder"
)
