package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): Student?

    @Query("SELECT * FROM students WHERE grade = :grade ORDER BY name ASC")
    fun getStudentsByGrade(grade: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE LOWER(name) = LOWER(:name) AND LOWER(parentEmail) = LOWER(:email) LIMIT 1")
    suspend fun getStudentByNameAndEmail(name: String, email: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date AND studentId = :studentId LIMIT 1")
    suspend fun getAttendanceForDateAndStudent(date: String, studentId: Int): Attendance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<Attendance>>
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE targetType = 'All' OR (targetType = 'Grade' AND targetValue = :grade) OR (targetType = 'Individual' AND targetValue = :parentEmail) ORDER BY id DESC")
    fun getAnnouncementsForParent(grade: String, parentEmail: String): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)
}
