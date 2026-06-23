package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Student::class, Attendance::class, Notification::class, ParentMessage::class, Announcement::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "school_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val dao = database.schoolDao()
            val announcementDao = database.announcementDao()
            // Initial mock students representing various grades
            // User provided contact is 8770206476. We will set this for several students
            val initialStudents = listOf(
                Student(
                    name = "Kabir Singh",
                    rollNumber = "S101",
                    grade = "Grade 10",
                    parentName = "Rajesh Singh",
                    parentContact = "8770206476",
                    parentEmail = "ipsinternationalgurukul@gmail.com",
                    admissionNumber = "ADM501",
                    gender = "Male",
                    status = "Active"
                ),
                Student(
                    name = "Ananya Iyer",
                    rollNumber = "S102",
                    grade = "Grade 10",
                    parentName = "Subramanian Iyer",
                    parentContact = "9123456780",
                    parentEmail = "ananya@gmail.com",
                    admissionNumber = "ADM502",
                    gender = "Female",
                    status = "Active"
                ),
                Student(
                    name = "Ganesh Kumar",
                    rollNumber = "S103",
                    grade = "Grade 12",
                    parentName = "Ganesh Kumar Sahu",
                    parentContact = "8770206476",
                    parentEmail = "ganesh@gmail.com",
                    admissionNumber = "ADM503",
                    gender = "Male",
                    status = "Active"
                ),
                Student(
                    name = "Meera Patel",
                    rollNumber = "S104",
                    grade = "Grade 10",
                    parentName = "Vijay Patel",
                    parentContact = "9345678912",
                    parentEmail = "meera@gmail.com",
                    admissionNumber = "ADM504",
                    gender = "Female",
                    status = "Active"
                ),
                Student(
                    name = "Rohan Verma",
                    rollNumber = "S105",
                    grade = "Grade 9",
                    parentName = "Sanjay Verma",
                    parentContact = "9898989898",
                    parentEmail = "rohan@gmail.com",
                    admissionNumber = "ADM505",
                    gender = "Male",
                    status = "Active"
                ),
                Student(
                    name = "Yupeshwari Sahu",
                    rollNumber = "S106",
                    grade = "Grade 12",
                    parentName = "Yupeshwari Sahu",
                    parentContact = "8770206476",
                    parentEmail = "yupeshwari@gmail.com",
                    admissionNumber = "ADM506",
                    gender = "Female",
                    status = "Active"
                ),
                Student(
                    name = "Aditya Rao",
                    rollNumber = "S107",
                    grade = "Grade 8",
                    parentName = "Lakshman Rao",
                    parentContact = "9555444333",
                    parentEmail = "aditya@gmail.com",
                    admissionNumber = "ADM507",
                    gender = "Male",
                    status = "Active"
                ),
                Student(
                    name = "Diya Sen",
                    rollNumber = "S108",
                    grade = "Grade 8",
                    parentName = "Amit Sen",
                    parentContact = "9444333222",
                    parentEmail = "diya@gmail.com",
                    admissionNumber = "ADM508",
                    gender = "Female",
                    status = "Active"
                ),
                Student(
                    name = "Ishaan Gupta",
                    rollNumber = "S109",
                    grade = "Grade 11",
                    parentName = "Arun Gupta",
                    parentContact = "9333222111",
                    parentEmail = "ishaan@gmail.com",
                    admissionNumber = "ADM509",
                    gender = "Male",
                    status = "Active"
                ),
                Student(
                    name = "Tanya Kapoor",
                    rollNumber = "S110",
                    grade = "Grade 11",
                    parentName = "Anil Kapoor",
                    parentContact = "8770206476",
                    parentEmail = "ipsinternationalgurukul@gmail.com",
                    admissionNumber = "ADM510",
                    gender = "Female",
                    status = "Active"
                )
            )

            for (student in initialStudents) {
                dao.insertStudent(student)
            }

            // Populate some initial attendance records
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val tempAttendance = listOf(
                Attendance(studentId = 1, date = today, status = "Present"),
                Attendance(studentId = 2, date = today, status = "Present"),
                Attendance(studentId = 3, date = today, status = "Absent", remarks = "Medical Leave"),
                Attendance(studentId = 4, date = today, status = "Late", remarks = "Bus delay"),
                Attendance(studentId = 5, date = today, status = "Present")
            )
            dao.insertAttendanceList(tempAttendance)

            // Populate initial notifications
            val initialNotifications = listOf(
                Notification(
                    title = "Annual Day Celebration 2026",
                    content = "Dear Parents and Students, our Annual Day Ceremony is scheduled for July 15th. Practices will begin tomorrow. Attendance is mandatory.",
                    type = "Announcement",
                    targetType = "All",
                    targetValue = "All",
                    timestamp = System.currentTimeMillis() - 86400000 * 2
                ),
                Notification(
                    title = "Grade 10 Science Exhibition Project",
                    content = "All Grade 10 students must submit their Science Exhibition group project outline by this Friday. Contact Science HOD for guidelines.",
                    type = "Event Reminder",
                    targetType = "Specific Grade",
                    targetValue = "Grade 10",
                    timestamp = System.currentTimeMillis() - 86400000
                ),
                Notification(
                    title = "Urgent: Direct Admin Hotline",
                    content = "You can contact the IPS school administrative hotline directly at 8770206476 for any queries related to admissions or fee structures.",
                    type = "Emergency Alert",
                    targetType = "Individual Parent",
                    targetValue = "8770206476",
                    timestamp = System.currentTimeMillis()
                )
            )

            for (notification in initialNotifications) {
                dao.insertNotification(notification)
            }

            // Populate initial announcements
            val initialAnnouncements = listOf(
                Announcement(
                    title = "Academic Session 2026 Reopening",
                    content = "IPS International Gurukul School will reopen for the new academic session on July 1st, 2026. Please ensure uniforms and textbooks are ready.",
                    targetType = "All",
                    targetValue = "All",
                    date = "June 25, 2026, 09:00 AM",
                    type = "Announcement"
                ),
                Announcement(
                    title = "Urgent: Direct Parent hotline enabled",
                    content = "For any administrative oversight, direct comments, and comments, message directly to the Principal's board at 8770206476 or 7987424380.",
                    targetType = "All",
                    targetValue = "All",
                    date = "June 26, 2026, 10:30 AM",
                    type = "Alert"
                ),
                Announcement(
                    title = "Science Project Submission Reminder",
                    content = "Grade 10 pupils must submit their science projects. Marks will be uploaded to final transcripts directly.",
                    targetType = "Grade",
                    targetValue = "Grade 10",
                    date = "June 27, 2026, 02:00 PM",
                    type = "Reminder"
                )
            )
            for (announcement in initialAnnouncements) {
                announcementDao.insertAnnouncement(announcement)
            }
        }
    }
}
