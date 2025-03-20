package com.studyquest.app.ui.createtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

// Data models
data class Grade(
    val id: Int,
    val name: String
)

data class Subject(
    val id: Int,
    val name: String,
    val icon: String,
    val color: String
)

data class Textbook(
    val id: Int,
    val name: String,
    val subjectId: Int,
    val gradeId: Int,
    val totalPages: Int
)

data class CreateTestRequest(
    val gradeId: Int,
    val subjectId: Int,
    val textbookId: Int,
    val title: String,
    val pagesFrom: Int,
    val pagesTo: Int,
    val questionCount: Int,
    val examDate: LocalDate?,
    val scheduledReminders: Boolean
)

// ViewModel for the multi-step form
class CreateTestViewModel : ViewModel() {
    // Form steps
    val steps = listOf(
        "Grade & Subject",
        "Textbook",
        "Pages & Content",
        "Scheduling"
    )
    
    // Form data
    var currentStep by mutableStateOf(1)
    var gradeId by mutableStateOf<Int?>(null)
    var subjectId by mutableStateOf<Int?>(null)
    var textbookId by mutableStateOf<Int?>(null)
    var title by mutableStateOf("")
    var pagesFrom by mutableStateOf(1)
    var pagesTo by mutableStateOf(10)
    var questionCount by mutableStateOf(10)
    var examDate by mutableStateOf<LocalDate?>(null)
    var scheduledReminders by mutableStateOf(true)
    
    // Sample data (in a real app, this would come from API)
    val grades = listOf(
        Grade(1, "Grade 9"),
        Grade(2, "Grade 10"),
        Grade(3, "Grade 11"),
        Grade(4, "Grade 12")
    )
    
    val subjects = listOf(
        Subject(1, "Mathematics", "calculator", "#4285F4"),
        Subject(2, "Physics", "science", "#EA4335"),
        Subject(3, "Chemistry", "flask", "#FBBC05"),
        Subject(4, "Biology", "leaf", "#34A853")
    )
    
    val textbooks = listOf(
        Textbook(1, "Algebra Fundamentals", 1, 1, 250),
        Textbook(2, "Geometry Essentials", 1, 1, 220),
        Textbook(3, "Advanced Trigonometry", 1, 2, 300),
        Textbook(4, "Mechanics & Dynamics", 2, 2, 280),
        Textbook(5, "Organic Chemistry", 3, 3, 340),
        Textbook(6, "Cell Biology", 4, 3, 320)
    )
    
    // Filtered textbooks based on selected grade and subject
    val filteredTextbooks: List<Textbook>
        get() = textbooks.filter { 
            (gradeId == null || it.gradeId == gradeId) && 
            (subjectId == null || it.subjectId == subjectId) 
        }
    
    // Get selected textbook
    val selectedTextbook: Textbook?
        get() = textbookId?.let { id -> textbooks.find { it.id == id } }
    
    // Check if current step is valid to proceed
    fun isCurrentStepValid(): Boolean {
        return when (currentStep) {
            1 -> gradeId != null && subjectId != null
            2 -> textbookId != null
            3 -> title.isNotBlank() && pagesFrom > 0 && pagesTo >= pagesFrom && questionCount in 1..50
            4 -> true // All fields in step 4 are optional
            else -> false
        }
    }
    
    // Reset the form
    fun resetForm() {
        currentStep = 1
        gradeId = null
        subjectId = null
        textbookId = null
        title = ""
        pagesFrom = 1
        pagesTo = 10
        questionCount = 10
        examDate = null
        scheduledReminders = true
    }
    
    // Create test data for submission
    fun createTestData(): CreateTestRequest {
        return CreateTestRequest(
            gradeId = gradeId ?: 0,
            subjectId = subjectId ?: 0,
            textbookId = textbookId ?: 0,
            title = title,
            pagesFrom = pagesFrom,
            pagesTo = pagesTo,
            questionCount = questionCount,
            examDate = examDate,
            scheduledReminders = scheduledReminders
        )
    }
    
    // Submit test data to API
    suspend fun submitTest(): Result<Unit> {
        // In a real app, this would make an API call
        // For now, just simulate a successful response
        return Result.success(Unit)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTestScreen(
    onNavigateBack: () -> Unit = {},
    onTestCreated: () -> Unit = {},
    viewModel: CreateTestViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create a New Test") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Progress Indicator
            StepProgressIndicator(
                steps = viewModel.steps,
                currentStep = viewModel.currentStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
            
            // Step Content
            when (viewModel.currentStep) {
                1 -> GradeAndSubjectStep(viewModel)
                2 -> TextbookStep(viewModel)
                3 -> PagesAndContentStep(viewModel)
                4 -> SchedulingStep(viewModel)
            }
            
            // Navigation buttons
            if (viewModel.currentStep > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { viewModel.currentStep-- },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Back")
                    }
                    
                    Button(
                        onClick = {
                            if (viewModel.currentStep < viewModel.steps.size) {
                                viewModel.currentStep++
                            } else {
                                // Submit form on last step
                                coroutineScope.launch {
                                    val result = viewModel.submitTest()
                                    result.fold(
                                        onSuccess = {
                                            snackbarHostState.showSnackbar("Test created successfully!")
                                            viewModel.resetForm()
                                            onTestCreated()
                                        },
                                        onFailure = { error ->
                                            snackbarHostState.showSnackbar("Failed to create test: ${error.message}")
                                        }
                                    )
                                }
                            }
                        },
                        enabled = viewModel.isCurrentStepValid(),
                        shape = RoundedCornerShape(50),
                    ) {
                        Text(if (viewModel.currentStep < viewModel.steps.size) "Continue" else "Create Test")
                    }
                }
            }
        }
    }
}

@Composable
fun StepProgressIndicator(
    steps: List<String>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            val stepNumber = index + 1
            val isActive = stepNumber <= currentStep
            val isCurrentStep = stepNumber == currentStep
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Step circle with number
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = stepNumber.toString(),
                        color = if (isActive) Color.White else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                
                // Step name
                Text(
                    text = step,
                    color = if (isCurrentStep) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (isCurrentStep) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // Connector line
                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(48.dp)
                            .background(
                                color = if (stepNumber < currentStep) MaterialTheme.colorScheme.primary else Color.LightGray
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun GradeAndSubjectStep(viewModel: CreateTestViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Your Grade and Subject",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
        // Grade Selection
        Text(
            text = "Grade Level",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(viewModel.grades) { grade ->
                val isSelected = viewModel.gradeId == grade.id
                val backgroundColor = if (isSelected) Color(0xFFD1E1FA) else Color(0xFFE6F0FF)
                val textColor = if (isSelected) Color(0xFF1A56DB) else Color(0xFF333333)
                val borderColor = if (isSelected) Color(0xFFADCCF7) else Color(0xFFCCE0FF)
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = backgroundColor,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .clickable { viewModel.gradeId = grade.id }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = grade.name,
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        
        // Subject Selection
        Text(
            text = "Subject",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(viewModel.subjects) { subject ->
                val isSelected = viewModel.subjectId == subject.id
                val backgroundColor = if (isSelected) Color(0xFFD1E1FA) else Color(0xFFE6F0FF)
                val textColor = if (isSelected) Color(0xFF1A56DB) else Color(0xFF333333)
                val borderColor = if (isSelected) Color(0xFFADCCF7) else Color(0xFFCCE0FF)
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = backgroundColor,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .clickable { viewModel.subjectId = subject.id }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Book,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = subject.name,
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Continue button for step 1 only
        if (viewModel.currentStep == 1) {
            Button(
                onClick = { viewModel.currentStep++ },
                enabled = viewModel.isCurrentStepValid(),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun TextbookStep(viewModel: CreateTestViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Your Textbook",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
        if (viewModel.filteredTextbooks.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                color = Color(0xFFF8F9FA),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "No textbooks available for the selected grade and subject",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedButton(onClick = { viewModel.currentStep = 1 }) {
                        Text("Go back and change your selection")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.filteredTextbooks) { textbook ->
                    val isSelected = viewModel.textbookId == textbook.id
                    val backgroundColor = if (isSelected) Color(0xFFD1E1FA) else Color(0xFFE6F0FF)
                    val textColor = if (isSelected) Color(0xFF1A56DB) else Color(0xFF333333)
                    val borderColor = if (isSelected) Color(0xFFADCCF7) else Color(0xFFCCE0FF)
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = backgroundColor,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.textbookId = textbook.id
                                viewModel.title = "${viewModel.subjects.find { it.id == textbook.subjectId }?.name}: ${textbook.name}"
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.Book,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                
                                Column(modifier = Modifier.padding(start = 12.dp)) {
                                    Text(
                                        text = textbook.name,
                                        color = textColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${textbook.totalPages} pages",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagesAndContentStep(viewModel: CreateTestViewModel) {
    val selectedTextbook = viewModel.selectedTextbook
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Pages & Question Count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
        // Test title
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text("Test Title") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        // Page range
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.pagesFrom.toString(),
                onValueChange = { 
                    val value = it.toIntOrNull() ?: 1
                    viewModel.pagesFrom = value.coerceIn(1, selectedTextbook?.totalPages ?: 100)
                },
                label = { Text("Starting Page") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = viewModel.pagesTo.toString(),
                onValueChange = {
                    val value = it.toIntOrNull() ?: viewModel.pagesFrom
                    viewModel.pagesTo = value.coerceIn(viewModel.pagesFrom, selectedTextbook?.totalPages ?: 100)
                },
                label = { Text("Ending Page") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Question count
        OutlinedTextField(
            value = viewModel.questionCount.toString(),
            onValueChange = {
                val value = it.toIntOrNull() ?: 10
                viewModel.questionCount = value.coerceIn(1, 50)
            },
            label = { Text("Number of Questions") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun SchedulingStep(viewModel: CreateTestViewModel) {
    // Calendar dialog state
    val calendarState = rememberSheetState()
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Schedule Your Test",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
        // Exam date selection
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = "Test Date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedButton(
                onClick = { calendarState.show() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = viewModel.examDate?.format(
                            DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                        ) ?: "Pick a date",
                        color = if (viewModel.examDate == null) Color.Gray else Color.Unspecified
                    )
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color.Gray
                    )
                }
            }
        }
        
        // Daily reminders toggle
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Checkbox(
                    checked = viewModel.scheduledReminders,
                    onCheckedChange = { viewModel.scheduledReminders = it }
                )
                
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = "Schedule daily reminders",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Get daily notifications to help you prepare for this test",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
    
    // Calendar dialog for date selection
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            viewModel.examDate = date
        }
    )
}

// In the actual app, you would call this in your MainActivity or NavHost
@Composable
fun StudyQuestApp() {
    MaterialTheme {
        // Set up navigation and app theme here
        CreateTestScreen(
            onNavigateBack = { /* Handle navigation back */ },
            onTestCreated = { /* Handle test created navigation */ }
        )
    }
}
