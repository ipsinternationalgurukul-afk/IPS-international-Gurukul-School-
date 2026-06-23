package com.example.data

import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val database: AppDatabase) {
    private val schoolDao = database.schoolDao()
    private val studentDao = database.studentDao()
    private val attendanceDao = database.attendanceDao()
    private val announcementDao = database.announcementDao()

    // --- Student Operations ---
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    
    fun getStudentsByGrade(grade: String): Flow<List<Student>> = studentDao.getStudentsByGrade(grade)
    
    suspend fun getStudentByNameAndEmail(name: String, email: String): Student? {
        return studentDao.getStudentByNameAndEmail(name, email)
    }

    suspend fun insertStudent(student: Student): Long {
        return studentDao.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
    }

    suspend fun getStudentCount(): Int = studentDao.getStudentCount()

    // --- Attendance Operations ---
    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>> = attendanceDao.getAttendanceForStudent(studentId)
    
    fun getAttendanceForDate(date: String): Flow<List<Attendance>> = attendanceDao.getAttendanceForDate(date)

    suspend fun markAttendance(studentId: Int, date: String, status: String, remarks: String = "") {
        val existing = attendanceDao.getAttendanceForDateAndStudent(date, studentId)
        val record = if (existing != null) {
            existing.copy(status = status, remarks = remarks)
        } else {
            Attendance(studentId = studentId, date = date, status = status, remarks = remarks)
        }
        attendanceDao.upsertAttendance(record)
    }

    suspend fun insertAttendanceList(attendanceList: List<Attendance>) {
        for (att in attendanceList) {
            val existing = attendanceDao.getAttendanceForDateAndStudent(att.date, att.studentId)
            val record = if (existing != null) {
                existing.copy(status = att.status, remarks = att.remarks)
            } else {
                att
            }
            attendanceDao.upsertAttendance(record)
        }
    }

    fun getAllAttendance(): Flow<List<Attendance>> = attendanceDao.getAllAttendance()

    // --- Announcement Operations ---
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    fun getAnnouncementsForParent(grade: String, parentEmail: String): Flow<List<Announcement>> {
        return announcementDao.getAnnouncementsForParent(grade, parentEmail)
    }

    suspend fun insertAnnouncement(announcement: Announcement) {
        announcementDao.insertAnnouncement(announcement)
    }

    suspend fun deleteAnnouncement(announcement: Announcement) {
        announcementDao.deleteAnnouncement(announcement)
    }

    // --- Notification Operations ---
    val allNotifications: Flow<List<Notification>> = schoolDao.getAllNotifications()

    suspend fun insertNotification(notification: Notification): Long {
        return schoolDao.insertNotification(notification)
    }

    suspend fun deleteNotificationById(id: Int) {
        schoolDao.deleteNotificationById(id)
    }

    // --- Parent Comments / Message Operations ---
    val allParentMessages: Flow<List<ParentMessage>> = schoolDao.getAllParentMessages()

    suspend fun insertParentMessage(parentMessage: ParentMessage): Long {
        return schoolDao.insertParentMessage(parentMessage)
    }

    suspend fun deleteParentMessageById(id: Int) {
        schoolDao.deleteParentMessageById(id)
    }
}
