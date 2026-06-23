package com.example.ui

import com.example.data.Student

sealed class UserSession {
    object Idle : UserSession()
    data class Parent(val student: Student) : UserSession()
    object Admin : UserSession()
}
