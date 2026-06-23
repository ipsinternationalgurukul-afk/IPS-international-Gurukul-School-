package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val date: String, // format "YYYY-MM-DD"
    val status: String, // "Present", "Absent", "Late", "Excused"
    val remarks: String = ""
)
