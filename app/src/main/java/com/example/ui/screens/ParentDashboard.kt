package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Announcement
import com.example.data.Attendance
import com.example.data.Student
import com.example.ui.SchoolViewModel
import com.example.ui.UserSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboard(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val session by viewModel.session.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val attendanceLogs by viewModel.parentStudentAttendance.collectAsState()
    val sentComments by viewModel.allParentMessages.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Profile & Attendance, 1: School Notices, 2: Send Message

    // Guard against empty state or wrong session
    val student = (session as? UserSession.Parent)?.student ?: return

    val totalDays = attendanceLogs.size
    val presentDays = attendanceLogs.count { it.status == "Present" }
    val attendancePercentage = if (totalDays > 0) {
        (presentDays.toFloat() / totalDays.toFloat() * 100).toInt()
    } else {
        100
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "IPAC parent hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${student.name} (${student.grade})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log out",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Navigation tabs with 3 options
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Profile", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.AccountBox, null) },
                    modifier = Modifier.testTag("profile_tab")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Notices", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            if (announcements.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ) {
                                    Text(announcements.size.toString())
                                }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Notifications, null) },
                    modifier = Modifier.testTag("notices_tab")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Feedback", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.RateReview, null) },
                    modifier = Modifier.testTag("feedback_tab")
                )
            }

            when (activeTab) {
                0 -> {
                    // Profile & Attendance Scroll View
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Student Card Header
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text(
                                                text = student.name,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "Roll No: ${student.rollNumber} | Class: ${student.grade}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                text = "Adm No: ${student.admissionNumber}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (student.gender == "Female") Icons.Default.Face2 else Icons.Default.Face,
                                                contentDescription = "Avatar",
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("PARENT / GUARDIAN", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                            Text(student.parentName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("EMAIL RECORDED", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                            Text(student.parentEmail, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                }
                            }
                        }

                        // Attendance Graph Stats Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular Progress Percent
                                    Box(
                                        modifier = Modifier.size(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { attendancePercentage / 100f },
                                            modifier = Modifier.fillMaxSize(),
                                            color = when {
                                                attendancePercentage >= 85 -> MaterialTheme.colorScheme.primary
                                                attendancePercentage >= 75 -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.error
                                            },
                                            strokeWidth = 10.dp,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$attendancePercentage%",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Attendance",
                                                fontSize = 9.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(20.dp))

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "ATTENDANCE SCORE",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Status: ${if (attendancePercentage >= 75) "Excellent" else "Needs Improvement"}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "Total classes checked: $totalDays",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "Present: $presentDays days | Absent: ${totalDays - presentDays} days",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        // Contact School Support Helpdesk Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ContactPhone,
                                            contentDescription = "Contact school icon",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "IPAC SCHOOL COUNSEL",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = "Direct School Line: 8770206 476",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:8770206476"))
                                                context.startActivity(intent)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("call_school_button")
                                        ) {
                                            Icon(Icons.Default.PhoneInTalk, null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Call School Admin Desk")
                                        }
                                    }
                                }
                            }
                        }

                        // Student attendance check list
                        item {
                            Text(
                                text = "RECENT ATTENDANCE ROLL HISTORY",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (attendanceLogs.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Text(
                                        text = "No school attendance records uploaded by administrators yet.",
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(attendanceLogs) { log ->
                                AttendanceRecordRow(log)
                            }
                        }
                    }
                }
                1 -> {
                    // School Announcements/Emergency Alerts List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "NOTIFICATIONS & ANNOUNCEMENTS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        if (announcements.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsOff,
                                            contentDescription = "No announcements",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No pending notifications found.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = "Whenever administrators send grade-specific alerts or emergency notifications, they correspond real-time right here.",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        } else {
                            items(announcements) { notice ->
                                NoticeItemCard(notice)
                            }
                        }
                    }
                }
                2 -> {
                    // Parents Feedback & direct SMS Dispatch tab
                    ParentCommentsTab(viewModel = viewModel, student = student)
                }
            }
        }
    }
}

@Composable
fun ParentCommentsTab(
    viewModel: SchoolViewModel,
    student: Student
) {
    val context = LocalContext.current
    var commentText by remember { mutableStateOf("") }
    val sentMessages by viewModel.allParentMessages.collectAsState()

    // Filter messages belonging only to this student
    val studentMessagesList = remember(sentMessages, student.id) {
        sentMessages.filter { it.studentId == student.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("comment_form_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "IPS DIRECT PARENT FEEDBACK HELPDESK",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Submit comments, sickness notice, or any administrative requests. Submitted messages are instantly stored in the local server for school admins to view, and can be sent directly to admin phones via SMS.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("comment_input_box"),
                    placeholder = { Text("E.g., Dear Admin/Principal, My ward will be late today due to rain...", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick suggestion chips
                Text(
                    text = "Quick Template Prompts:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = { commentText = "Leave Application: Sick leave today." },
                        label = { Text("Sick Leave", fontSize = 11.sp) }
                    )
                    SuggestionChip(
                        onClick = { commentText = "Homework Query: Details of missing subjects." },
                        label = { Text("Subject Queries", fontSize = 11.sp) }
                    )
                    SuggestionChip(
                        onClick = { commentText = "Fee Query: Balance payment update info request." },
                        label = { Text("Fee Queries", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons representing the two administrators numbers contact
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (commentText.trim().isBlank()) {
                                android.widget.Toast.makeText(context, "Please enter your message comment text first!", android.widget.Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // 1. Save locally in local Room database
                            viewModel.submitParentMessage(
                                studentId = student.id,
                                studentName = student.name,
                                parentName = student.parentName,
                                message = commentText.trim()
                            )

                            // 2. Trigger native SMS dispatcher to Principal Ganesh's phone number
                            val recipientNumber = "8770206476"
                            val smsText = "From Parent of ${student.name} (${student.grade}):\n${commentText.trim()}"
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$recipientNumber")).apply {
                                putExtra("sms_body", smsText)
                            }
                            try {
                                context.startActivity(intent)
                                android.widget.Toast.makeText(context, "Comment saved to database! Launching SMS helper...", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Saved successfully! (Direct SMS client could not open)", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            commentText = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_send_principal_ganesh")
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SMS to Principal Ganesh Sahu", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (commentText.trim().isBlank()) {
                                android.widget.Toast.makeText(context, "Please enter your message comment text first!", android.widget.Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // 1. Save locally in local Room database
                            viewModel.submitParentMessage(
                                studentId = student.id,
                                studentName = student.name,
                                parentName = student.parentName,
                                message = commentText.trim()
                            )

                            // 2. Trigger native SMS dispatcher to Principal Mam Yupeshwari's phone number
                            val recipientNumber = "7987424380" // principal mam custom contact
                            val smsText = "From Parent of ${student.name} (${student.grade}):\n${commentText.trim()}"
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$recipientNumber")).apply {
                                putExtra("sms_body", smsText)
                            }
                            try {
                                context.startActivity(intent)
                                android.widget.Toast.makeText(context, "Comment saved to database! Launching SMS helper...", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Saved successfully! (Direct SMS client could not open)", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            commentText = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_send_principal_yupeshwari")
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SMS to Principal Yupeshwari Mam", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Timeline header
        Text(
            text = "YOUR FEEDBACK & MESSAGE LOG HISTORY",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (studentMessagesList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "You haven't submitted any feedback logs in this app session yet.",
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            studentMessagesList.forEach { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Logged Date: ${msg.dateString}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            IconButton(
                                onClick = { viewModel.deleteParentMessage(msg.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete comment log",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = msg.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceRecordRow(log: Attendance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            when (log.status) {
                                "Present" -> Color(0xFF10B981) // Green
                                "Absent" -> Color(0xFFEF4444)  // Red
                                "Late" -> Color(0xFFF59E0B)    // Orange
                                else -> Color.Gray
                            }
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = log.date,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (log.remarks.isNotBlank()) {
                        Text(
                            text = log.remarks,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Text(
                text = log.status,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = when (log.status) {
                    "Present" -> Color(0xFF059669)
                    "Absent" -> Color(0xFFDC2626)
                    "Late" -> Color(0xFFD97706)
                    else -> Color.DarkGray
                }
            )
        }
    }
}

@Composable
fun NoticeItemCard(notice: Announcement) {
    val borderBrush = Brush.horizontalGradient(
        colors = when (notice.type) {
            "Alert" -> listOf(Color.Red, Color.Red.copy(alpha = 0.5f))
            "Reminder" -> listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
            else -> listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notice_item_${notice.id}"),
        colors = CardDefaults.cardColors(
            containerColor = when (notice.type) {
                "Alert" -> Color(0xFFFEF2F2)
                "Reminder" -> Color(0xFFFFFBEB)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (notice.type) {
                            "Alert" -> Icons.Default.Warning
                            "Reminder" -> Icons.Default.EventNote
                            else -> Icons.Default.Campaign
                        },
                        contentDescription = "Notification type icon",
                        tint = when (notice.type) {
                            "Alert" -> Color.Red
                            "Reminder" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notice.type.uppercase(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (notice.type) {
                            "Alert" -> Color.Red
                            "Reminder" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }

                Text(
                    text = notice.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = notice.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = notice.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Addressed to: ${notice.targetType} (${if (notice.targetType == "All") "Global Announcement" else notice.targetValue})",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
