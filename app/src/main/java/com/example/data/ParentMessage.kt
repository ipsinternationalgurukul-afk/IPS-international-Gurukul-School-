package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_messages")
data class ParentMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val studentName: String,
    val parentName: String,
    val message: String,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)
