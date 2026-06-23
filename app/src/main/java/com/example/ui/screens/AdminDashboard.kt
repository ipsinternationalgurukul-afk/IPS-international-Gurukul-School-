package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Announcement
import com.example.data.Student
import com.example.ui.SchoolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val announcements by viewModel.announcements.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Students (CRUD), 1: Attendance, 2: Announcements, 3: School Info

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "IPAC ADMIN PORTAL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Full Power Control Deck",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("admin_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log out from Admin Panel",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
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
            // Admin bottom navigation style TabRow
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Group, null) },
                    text = { Text("Students", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_students")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.FactCheck, null) },
                    text = { Text("Attendance", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_attendance")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.Campaign, null) },
                    text = { Text("Notices", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_notices")
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Info, null) },
                    text = { Text("About Info", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_about")
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (activeTab) {
                    0 -> StudentsCrudTab(viewModel, allStudents)
                    1 -> AttendanceRollTab(viewModel, allStudents)
                    2 -> AnnouncementsDraftTab(viewModel, announcements)
                    3 -> SchoolAboutTab(allStudents.size)
                }
            }
        }
    }
}

// ==========================================
// 1. STUDENTS REGISTRY CRUD TAB
// ==========================================
@Composable
fun StudentsCrudTab(
    viewModel: SchoolViewModel,
    students: List<Student>
) {
    var searchKeyword by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }

    val filteredList = remember(students, searchKeyword) {
        if (searchKeyword.isBlank()) {
            students
        } else {
            students.filter {
                it.name.contains(searchKeyword, ignoreCase = true) ||
                it.grade.contains(searchKeyword, ignoreCase = true) ||
                it.rollNumber.contains(searchKeyword, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // General Enrollment Metric Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "SCHOOL STUDENT ENROLLMENT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Total Active Enrolled: 500",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Admin local sandbox profiles: ${students.size} of 500",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Search and Add student Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchKeyword,
                    onValueChange = { searchKeyword = it },
                    label = { Text("Search by student name or class...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchKeyword.isNotEmpty()) {
                            IconButton(onClick = { searchKeyword = "" }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("student_search_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(50.dp).testTag("add_student_fab")
                ) {
                    Icon(Icons.Default.Add, "Register new student")
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching students registered.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_item_${student.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (student.gender == "Male")
                                                    MaterialTheme.colorScheme.primaryContainer
                                                else
                                                    MaterialTheme.colorScheme.secondaryContainer
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (student.gender == "Male") Icons.Default.Face else Icons.Default.Face2,
                                            contentDescription = null,
                                            tint = if (student.gender == "Male")
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = student.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "${student.grade} | Roll No: ${student.rollNumber}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "Parent: ${student.parentName} (${student.parentEmail})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { studentToEdit = student },
                                        modifier = Modifier.testTag("edit_student_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit details",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteStudent(student) },
                                        modifier = Modifier.testTag("delete_student_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove student",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            StudentFormDialog(
                title = "REGISTER NEW GURUKUL STUDENT",
                onDismiss = { showAddDialog = false },
                onConfirm = { student ->
                    viewModel.addStudent(student)
                    showAddDialog = false
                }
            )
        }

        // Edit Dialog
        if (studentToEdit != null) {
            StudentFormDialog(
                title = "MODIFY STUDENT DETAILS",
                student = studentToEdit,
                onDismiss = { studentToEdit = null },
                onConfirm = { student ->
                    viewModel.updateStudent(student)
                    studentToEdit = null
                }
            )
        }
    }
}

@Composable
fun StudentFormDialog(
    title: String,
    student: Student? = null,
    onDismiss: () -> Unit,
    onConfirm: (Student) -> Unit
) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var rollNumber by remember { mutableStateOf(student?.rollNumber ?: "") }
    var grade by remember { mutableStateOf(student?.grade ?: "Grade 1") }
    var parentName by remember { mutableStateOf(student?.parentName ?: "") }
    var parentContact by remember { mutableStateOf(student?.parentContact ?: "") }
    var parentEmail by remember { mutableStateOf(student?.parentEmail ?: "") }
    var admissionNumber by remember { mutableStateOf(student?.admissionNumber ?: "") }
    var gender by remember { mutableStateOf(student?.gender ?: "Male") }

    val isValid = name.isNotBlank() && rollNumber.isNotBlank() && parentEmail.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text("Roll No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("dialog_roll_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = admissionNumber,
                        onValueChange = { admissionNumber = it },
                        label = { Text("Adm Id") },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f).testTag("dialog_adm_input")
                    )
                }

                // Grade Grade Selector Simple Outlined text or standard field
                OutlinedTextField(
                    value = grade,
                    onValueChange = { grade = it },
                    label = { Text("Class / Grade") },
                    placeholder = { Text("e.g. Grade 1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_grade_input")
                )

                // Gender Toggle
                Text("Gender", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    listOf("Male", "Female", "Other").forEach { g ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { gender = g }
                        ) {
                            RadioButton(selected = gender == g, onClick = { gender = g })
                            Text(g)
                        }
                    }
                }

                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = parentContact,
                    onValueChange = { parentContact = it },
                    label = { Text("Parent Mobile Contact") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = parentEmail,
                    onValueChange = { parentEmail = it },
                    label = { Text("Parent Log-in Email ID") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_email_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalStudent = student?.copy(
                        name = name.trim(),
                        rollNumber = rollNumber.trim(),
                        grade = grade.trim(),
                        parentName = parentName.trim(),
                        parentContact = parentContact.trim(),
                        parentEmail = parentEmail.trim(),
                        admissionNumber = if (admissionNumber.isBlank()) "IPS-ADM-${(100..999).random()}" else admissionNumber.trim(),
                        gender = gender
                    ) ?: Student(
                        name = name.trim(),
                        rollNumber = rollNumber.trim(),
                        grade = grade.trim(),
                        parentName = parentName.trim(),
                        parentContact = parentContact.trim(),
                        parentEmail = parentEmail.trim(),
                        admissionNumber = if (admissionNumber.isBlank()) "IPS-ADM-${(100..999).random()}" else admissionNumber.trim(),
                        gender = gender
                    )
                    onConfirm(finalStudent)
                },
                enabled = isValid,
                modifier = Modifier.testTag("dialog_confirm_button")
            ) {
                Text("Save and Secure")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abort")
            }
        }
    )
}

// ==========================================
// 2. DAILY ATTENDANCE MARKING TAB
// ==========================================
@Composable
fun AttendanceRollTab(
    viewModel: SchoolViewModel,
    students: List<Student>
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val rawGrade by viewModel.selectedGradeFilter.collectAsState()
    val dailyAttendanceList by viewModel.currentDateAttendance.collectAsState()

    // Collect list of registered grades
    val availableGrades = remember(students) {
        listOf("Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12")
    }

    val studentFilteredList = remember(students, rawGrade) {
        students.filter { it.grade.equals(rawGrade, ignoreCase = true) }
    }

    // Build mapping for current view
    val attendanceMap = remember(dailyAttendanceList) {
        dailyAttendanceList.associate { it.studentId to it.status }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Form Controls: Date picker simulation and Grade filter row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ATTENDANCE REGISTER CONTROLS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = { viewModel.setAttendanceDate(it) },
                            label = { Text("Log Date (YYYY-MM-DD)") },
                            leadingIcon = { Icon(Icons.Default.Today, null) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("attendance_date_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Quickly jump to today
                        IconButton(
                            onClick = {
                                val todayStr = java.text.SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    java.util.Locale.getDefault()
                                ).format(java.util.Date())
                                viewModel.setAttendanceDate(todayStr)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .testTag("attendance_today_button")
                        ) {
                            Icon(Icons.Default.Refresh, "Reset to Today")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grade Filter Selector Horizontal List
                    Text(
                        text = "FILTER AND ROLL BY CLASS:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Let's make small chips or row dropdown. We can make a simple scrollable horizontal row for grades
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(availableGrades) { gr ->
                                FilterChip(
                                    selected = gr == rawGrade,
                                    onClick = { viewModel.setGradeFilter(gr) },
                                    label = { Text(gr, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Marking statistics header and mass trigger helpers
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "STUDENTS FOR $rawGrade (${studentFilteredList.size} Found)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (studentFilteredList.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                studentFilteredList.forEach {
                                    viewModel.markStudentAttendance(it.id, "Present")
                                }
                            }
                        ) {
                            Text("ALL PRESENT", fontSize = 11.sp, color = Color(0xFF0F9D58), fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = {
                                studentFilteredList.forEach {
                                    viewModel.markStudentAttendance(it.id, "Absent")
                                }
                            }
                        ) {
                            Text("ALL ABSENT", fontSize = 11.sp, color = Color(0xFFDB4437), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Show students
        if (studentFilteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "No students are currently registered in '$rawGrade'. Add students in the Registries tab to log attendance here.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        } else {
            items(studentFilteredList) { student ->
                val markedStatus = attendanceMap[student.id] ?: "Present" // default to Present since normally kids are present

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("attendance_row_${student.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(student.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text("Roll: ${student.rollNumber}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }

                        // Switches for marking roll
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.markStudentAttendance(student.id, "Present") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (markedStatus == "Present") Color(0xFF10B981) else Color.LightGray.copy(alpha = 0.3f),
                                    contentColor = if (markedStatus == "Present") Color.White else Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("mark_present_${student.id}")
                            ) {
                                Text("Present", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { viewModel.markStudentAttendance(student.id, "Absent") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (markedStatus == "Absent") Color(0xFFEF4444) else Color.LightGray.copy(alpha = 0.3f),
                                    contentColor = if (markedStatus == "Absent") Color.White else Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("mark_absent_${student.id}")
                            ) {
                                Text("Absent", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. ANNOUNCEMENTS AND ALERTS CENTER TAB
// ==========================================
@Composable
fun AnnouncementsDraftTab(
    viewModel: SchoolViewModel,
    announcements: List<Announcement>
) {
    // Form Inputs
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var targetType by remember { mutableStateOf("All") } // "All", "Grade", "Individual"
    var targetValue by remember { mutableStateOf("") } // e.g. "Grade 1" or parent email
    var noticeType by remember { mutableStateOf("Announcement") } // "Announcement", "Reminder", "Alert"

    var messageStatus by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dispatch Center Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notice_compose_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COMPOSE NEW NOTIFICATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notice Type Category row selection
                    Text("Notification Type Category:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Announcement", "Reminder", "Alert").forEach { type ->
                            FilterChip(
                                selected = noticeType == type,
                                onClick = { noticeType = type },
                                label = { Text(type) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Target Receivers setup selection
                    Text("Target Receivers Audience:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All" to "All Students", "Grade" to "Specific Class", "Individual" to "Specific Parent").forEach { (typeKey, typeLabel) ->
                            FilterChip(
                                selected = targetType == typeKey,
                                onClick = {
                                    targetType = typeKey
                                    targetValue = when (typeKey) {
                                        "Grade" -> "Grade 5"
                                        "Individual" -> "sahu@example.com"
                                        else -> ""
                                    }
                                },
                                label = { Text(typeLabel) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Conditional Input for TargetValue
                    AnimatedVisibility(visible = targetType != "All") {
                        OutlinedTextField(
                            value = targetValue,
                            onValueChange = { targetValue = it },
                            label = {
                                Text(
                                    if (targetType == "Grade") "Specify Grade Name (e.g. Grade 5)"
                                    else "Specify Parent Account Email (e.g. sahu@example.com)"
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("notice_target_input")
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Announcement Title Topic") },
                        placeholder = { Text("e.g. Term Schedule Revisions") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("notice_title_input")
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Notification Content Specifics") },
                        placeholder = { Text("Type complete announcement details clearly...") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .padding(bottom = 16.dp)
                            .testTag("notice_content_input")
                    )

                    // Message success status banner
                    if (messageStatus != null) {
                        Text(
                            text = messageStatus ?: "",
                            color = Color(0xFF0F9D58),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isBlank() || content.isBlank()) {
                                messageStatus = "Title and content cannot be blank"
                                return@Button
                            }
                            viewModel.sendNotification(
                                title = title,
                                content = content,
                                targetType = targetType,
                                targetValue = targetValue,
                                type = noticeType
                            )
                            title = ""
                            content = ""
                            messageStatus = "✓ Notification successfully broadcasted!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("notice_send_button")
                    ) {
                        Icon(Icons.Default.Send, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast Secure Notification")
                    }
                }
            }
        }

        // historic sent announcements
        item {
            Text(
                text = "HISTORICAL NOTICES SENT LOG",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (announcements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "No recorded historic notices found in school system.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(announcements) { notice ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (notice.type) {
                                                "Alert" -> Color.Red
                                                "Reminder" -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${notice.type.uppercase()} | To: ${notice.targetType} ${if (notice.targetType == "All") "" else "(${notice.targetValue})"}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteNotification(notice) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("delete_notice_${notice.id}")
                            ) {
                                Icon(Icons.Default.Delete, "Retract", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notice.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(notice.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notice.date, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. SCHOOL OVERVIEW INFO TAB
// ==========================================
@Composable
fun SchoolAboutTab(
    localSandboxCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header style details
                Text(
                    text = "IPS INTERNATIONAL GURUKUL SCHOOL",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Establishment Portal Profile & Verification Registry",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Stats checklist items
                InfoCheckRow(Icons.Default.Group, "Total Matriculated Students Check", "500 Enrolled Profiles Status Active")
                InfoCheckRow(Icons.Default.Verified, "Verified Setup Registry Profiles", "$localSandboxCount local sandbox students seeded")
                InfoCheckRow(Icons.Default.PhoneCallback, "School Official Support Hotline", "+91 8770206 476")
                InfoCheckRow(Icons.Default.Map, "Campus Location Registered", "IPS Gurukul International Campus, Chhattisgarh")

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CHIEF STAFF ADMINISTRATION BOARD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                AdminStaffCard(
                    role = "PRINCIPAL CHAIRMAN",
                    name = "Mr. Ganesh Kumar Sahu",
                    desc = "Direct Administrator oversight, operations coordinator, and curriculum head of school."
                )

                Spacer(modifier = Modifier.height(12.dp))

                AdminStaffCard(
                    role = "PRINCIPAL VICE CHAIRPERSON",
                    name = "Mrs. Yupeshwari Sahu",
                    desc = "Executive curriculum co-principal, parent counselor, and campus wellness supervisor."
                )
            }
        }
    }
}

@Composable
fun InfoCheckRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(content, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun AdminStaffCard(role: String, name: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(role, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Text(name, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
