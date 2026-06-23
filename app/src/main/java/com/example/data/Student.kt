package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val rollNumber: String,
    val grade: String, // e.g. "Grade 1", "Grade 5", "Grade 10"
    val parentName: String,
    val parentContact: String,
    val parentEmail: String, // Used for parent login!
    val admissionNumber: String,
    val gender: String, // "Male", "Female", "Other"
    val status: String = "Active" // "Active", "Inactive"
)
