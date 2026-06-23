package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val appDatabase = AppDatabase.getDatabase(application)
    private val repository = SchoolRepository(appDatabase)

    private val _dbLoaded = MutableStateFlow(false)
    val dbLoaded: StateFlow<Boolean> = _dbLoaded.asStateFlow()

    // Expose UserSession state
    private val _session = MutableStateFlow<UserSession>(UserSession.Idle)
    val session: StateFlow<UserSession> = _session.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Student list and count flows
    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentCount: StateFlow<Int> = repository.allStudents
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10)

    // Announcements
    val announcements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Message comments feed
    val allParentMessages: StateFlow<List<ParentMessage>> = repository.allParentMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active attendance recording and queries state
    private val _selectedDate = MutableStateFlow(getCurrentDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()
    val selectedAttendanceDate: StateFlow<String> = _selectedDate.asStateFlow() // Alias support

    private val _selectedGradeFilter = MutableStateFlow("Grade 10")
    val selectedGradeFilter: StateFlow<String> = _selectedGradeFilter.asStateFlow()
    val attendanceGradeFilter: StateFlow<String> = _selectedGradeFilter.asStateFlow() // Alias support

    val currentDateAttendance: StateFlow<List<Attendance>> = _selectedDate
        .flatMapLatest { date -> repository.getAttendanceForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceForSelectedDate: StateFlow<List<Attendance>> = currentDateAttendance // Alias support

    // Attendance record helpers for parent view
    val parentStudentAttendance: StateFlow<List<Attendance>> = _session
        .filter { it is UserSession.Parent }
        .flatMapLatest { session ->
            val student = (session as UserSession.Parent).student
            repository.getAttendanceForStudent(student.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            // Keep spinning until database gets initialized or seeded
            try {
                repository.allStudents.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _dbLoaded.value = true
            }
        }
    }

    fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // --- Active Actions ---
    fun loginAdmin(passcode: String): Boolean {
        _loginError.value = null
        val trimmed = passcode.trim()
        return if (trimmed == "8770" || trimmed == "8770206476" || trimmed == "7987424380" || trimmed == "1234") {
            _session.value = UserSession.Admin
            true
        } else {
            _loginError.value = "Incorrect admin passcode. Contact Principal board if lost."
            false
        }
    }

    fun loginParent(studentName: String, emailID: String): Boolean {
        _loginError.value = null
        val matched = allStudents.value.find {
            it.name.trim().lowercase() == studentName.trim().lowercase() &&
            it.parentEmail.trim().lowercase() == emailID.trim().lowercase()
        }
        return if (matched != null) {
            _session.value = UserSession.Parent(matched)
            true
        } else {
            _loginError.value = "Login mismatch. Enter student's first and last name accompanied by parent email id matching registered registry."
            false
        }
    }

    fun logout() {
        _session.value = UserSession.Idle
        _loginError.value = null
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    // --- Attendance Operations ---
    fun setAttendanceDate(date: String) {
        _selectedDate.value = date
    }

    fun setGradeFilter(grade: String) {
        _selectedGradeFilter.value = grade
    }

    fun markStudentAttendance(studentId: Int, status: String) {
        viewModelScope.launch {
            repository.markAttendance(studentId, _selectedDate.value, status)
        }
    }

    fun markAttendance(studentId: Int, status: String) {
        markStudentAttendance(studentId, status)
    }

    fun markAllAsPresent(students: List<Student>) {
        viewModelScope.launch {
            students.forEach { s ->
                repository.markAttendance(s.id, _selectedDate.value, "Present")
            }
        }
    }

    // --- Student CRUD ---
    fun addStudent(student: Student) {
        viewModelScope.launch {
            repository.insertStudent(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    // --- Notices & Announcements ---
    fun sendNotification(title: String, content: String, targetType: String, targetValue: String, type: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault()).format(Date())
            repository.insertAnnouncement(
                Announcement(
                    title = title,
                    content = content,
                    targetType = targetType,
                    targetValue = targetValue,
                    date = dateStr,
                    type = type
                )
            )
            // also seed as standard Notification just in case
            repository.insertNotification(
                Notification(
                    title = title,
                    content = content,
                    type = type,
                    targetType = if (targetType == "All") "All" else if (targetType == "Grade") "Specific Grade" else "Individual Parent",
                    targetValue = targetValue,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteNotification(announcement: Announcement) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            repository.deleteNotificationById(id)
        }
    }

    // --- Parent Messages ---
    fun submitParentMessage(studentId: Int, studentName: String, parentName: String, message: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault()).format(Date())
            repository.insertParentMessage(
                ParentMessage(
                    studentId = studentId,
                    studentName = studentName,
                    parentName = parentName,
                    message = message,
                    dateString = dateStr,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteParentMessage(id: Int) {
        viewModelScope.launch {
            repository.deleteParentMessageById(id)
        }
    }
}
