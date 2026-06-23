package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    // --- Students ---
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE grade = :grade ORDER BY name ASC")
    fun getStudentsByGrade(grade: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Int): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCountFlow(): Flow<Int>

    // --- Attendance ---
    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(attendanceList: List<Attendance>)

    @Query("DELETE FROM attendance WHERE studentId = :studentId AND date = :date")
    suspend fun deleteAttendanceRecord(studentId: Int, date: String)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Int)

    // --- Parent Comments / Messages ---
    @Query("SELECT * FROM parent_messages ORDER BY timestamp DESC")
    fun getAllParentMessages(): Flow<List<ParentMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentMessage(parentMessage: ParentMessage): Long

    @Query("DELETE FROM parent_messages WHERE id = :id")
    suspend fun deleteParentMessageById(id: Int)
}
