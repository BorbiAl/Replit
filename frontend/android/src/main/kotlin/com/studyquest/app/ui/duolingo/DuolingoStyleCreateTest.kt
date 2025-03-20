package com.studyquest.app.ui.duolingo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

class DuolingoCreateTestViewModel : ViewModel() {
    // Step state
    var currentStep by mutableStateOf(1)
    
    // Form data
    var gradeId by mutableStateOf<Int?>(null)
    var subjectId by mutableStateOf<Int?>(null)
    var textbookId by mutableStateOf<Int?>(null)
    var title by mutableStateOf("")
    var pagesFrom by mutableStateOf(1)
    var pagesTo by mutableStateOf(10)
    var questionCount by mutableStateOf(10)
    var examDate by mutableStateOf<LocalDate?>(null)
    var scheduledReminders by mutableStateOf(true)
    
    // Mock data
    val grades = listOf(
        Grade(1, "Grade 9"),
        Grade(2, "Grade 10"),
        Grade(3, "Grade 11"),
        Grade(4, "Grade 12")
    )
    
    val subjects = listOf(
        Subject(1, "Mathematics", Icons.Filled.Calculate, "#4285F4"),
        Subject(2, "Physics", Icons.Filled.Science, "#EA4335"),
        Subject(3, "Chemistry", Icons.Filled.Science, "#FBBC05"),
        Subject(4, "Biology", Icons.Filled.Biotech, "#34A853")
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
            1 -> gradeId != null
            2 -> subjectId != null
            3 -> textbookId != null
            4 -> title.isNotBlank() && pagesFrom > 0 && pagesTo >= pagesFrom && questionCount in 1..50
            5 -> true // All fields in step 5 are optional
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
}

data class Grade(
    val id: Int,
    val name: String
)

data class Subject(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val colorHex: String
)

data class Textbook(
    val id: Int,
    val name: String,
    val subjectId: Int,
    val gradeId: Int,
    val totalPages: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuolingoStyleCreateTest(
    onNavigateBack: () -> Unit,
    onTestCreated: () -> Unit,
    viewModel: DuolingoCreateTestViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Create Test")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Progress steps
            TestCreationProgress(
                totalSteps = 5,
                currentStep = viewModel.currentStep
            )
            
            // Step content with sliding animation
            AnimatedContent(
                targetState = viewModel.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        // Moving forward
                        (slideInHorizontally(animationSpec = tween(300)) { width -> width } + 
                        fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + 
                            fadeOut(animationSpec = tween(300))
                        )
                    } else {
                        // Moving backward
                        (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + 
                        fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutHorizontally(animationSpec = tween(300)) { width -> width } + 
                            fadeOut(animationSpec = tween(300))
                        )
                    }
                }
            ) { step ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    when (step) {
                        1 -> GradeSelectionStep(viewModel)
                        2 -> SubjectSelectionStep(viewModel)
                        3 -> TextbookSelectionStep(viewModel)
                        4 -> ContentConfigStep(viewModel)
                        5 -> SchedulingStep(viewModel, onTestCreated)
                    }
                }
            }
        }
    }
}

@Composable
fun TestCreationProgress(
    totalSteps: Int,
    currentStep: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step $currentStep of $totalSteps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = when (currentStep) {
                    1 -> "Select Grade"
                    2 -> "Select Subject"
                    3 -> "Select Textbook"
                    4 -> "Set Content"
                    5 -> "Schedule Test"
                    else -> ""
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Progress bar
        LinearProgressIndicator(
            progress = currentStep.toFloat() / totalSteps,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
fun GradeSelectionStep(
    viewModel: DuolingoCreateTestViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction
        Text(
            text = "What grade are you in?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Grade options
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            viewModel.grades.forEach { grade ->
                val isSelected = viewModel.gradeId == grade.id
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) 
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                    else 
                        null,
                    onClick = { viewModel.gradeId = grade.id }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Grade indicator
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade.id.toString(),
                                color = if (isSelected) 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        
                        // Grade name
                        Text(
                            text = grade.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Selected indicator
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
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation button
        Button(
            onClick = { viewModel.currentStep = 2 },
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.isCurrentStepValid()
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun SubjectSelectionStep(
    viewModel: DuolingoCreateTestViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction
        Text(
            text = "Choose a subject",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Subject options
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            viewModel.subjects.forEach { subject ->
                val isSelected = viewModel.subjectId == subject.id
                val subjectColor = Color(android.graphics.Color.parseColor(subject.colorHex))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) 
                            subjectColor.copy(alpha = 0.15f) 
                        else 
                            MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) 
                        BorderStroke(2.dp, subjectColor) 
                    else 
                        null,
                    onClick = { viewModel.subjectId = subject.id }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Subject icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) 
                                        subjectColor
                                    else 
                                        subjectColor.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = subject.icon,
                                contentDescription = subject.name,
                                tint = if (isSelected) 
                                    Color.White
                                else 
                                    subjectColor
                            )
                        }
                        
                        // Subject name
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Selected indicator
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = subjectColor
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.currentStep = 1 },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            
            Button(
                onClick = { viewModel.currentStep = 3 },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isCurrentStepValid()
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun TextbookSelectionStep(
    viewModel: DuolingoCreateTestViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction
        Text(
            text = "Select your textbook",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Filtered textbooks based on selected grade and subject
        if (viewModel.filteredTextbooks.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = "No Textbooks",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "No textbooks found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Please go back and choose a different grade or subject",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Textbook options
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                viewModel.filteredTextbooks.forEach { textbook ->
                    val isSelected = viewModel.textbookId == textbook.id
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) 
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                        else 
                            null,
                        onClick = { 
                            viewModel.textbookId = textbook.id
                            val subject = viewModel.subjects.find { it.id == textbook.subjectId }
                            viewModel.title = "${subject?.name}: ${textbook.name}"
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Textbook icon
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Book,
                                    contentDescription = "Textbook",
                                    tint = if (isSelected) 
                                        MaterialTheme.colorScheme.onPrimary 
                                    else 
                                        MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            // Textbook info
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = textbook.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Text(
                                    text = "${textbook.totalPages} pages",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            
                            // Selected indicator
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
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.currentStep = 2 },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            
            Button(
                onClick = { viewModel.currentStep = 4 },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isCurrentStepValid()
            ) {
                Text("Continue")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentConfigStep(
    viewModel: DuolingoCreateTestViewModel
) {
    val selectedTextbook = viewModel.selectedTextbook
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction
        Text(
            text = "Configure your test content",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Title field
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text("Test Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        
        // Page range fields
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.pagesFrom.toString(),
                onValueChange = { 
                    val value = it.toIntOrNull() ?: 1
                    viewModel.pagesFrom = value.coerceIn(1, selectedTextbook?.totalPages ?: 100)
                },
                label = { Text("From Page") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
            
            OutlinedTextField(
                value = viewModel.pagesTo.toString(),
                onValueChange = { 
                    val value = it.toIntOrNull() ?: viewModel.pagesFrom
                    viewModel.pagesTo = value.coerceIn(viewModel.pagesFrom, selectedTextbook?.totalPages ?: 100)
                },
                label = { Text("To Page") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
        }
        
        // Question count with slider
        Text(
            text = "Number of Questions: ${viewModel.questionCount}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Slider(
            value = viewModel.questionCount.toFloat(),
            onValueChange = { viewModel.questionCount = it.toInt() },
            valueRange = 5f..30f,
            steps = 5,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // XP reward card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = "XP Reward",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
                
                Column {
                    Text(
                        text = "XP Reward",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Complete this test to earn ${viewModel.questionCount * 5} XP",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.currentStep = 3 },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            
            Button(
                onClick = { viewModel.currentStep = 5 },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isCurrentStepValid()
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun SchedulingStep(
    viewModel: DuolingoCreateTestViewModel,
    onTestCreated: () -> Unit
) {
    val calendarState = rememberSheetState()
    var isCreating by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction
        Text(
            text = "Schedule your test",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Exam date field
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            onClick = { calendarState.show() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Exam Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Exam Date",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = viewModel.examDate?.format(
                            DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                        ) ?: "Not set (optional)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                IconButton(onClick = { calendarState.show() }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Date"
                    )
                }
            }
        }
        
        // Reminders toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Reminders",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Daily Reminders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Get daily notifications to help you prepare",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Switch(
                    checked = viewModel.scheduledReminders,
                    onCheckedChange = { viewModel.scheduledReminders = it }
                )
            }
        }
        
        // Gamification benefit card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Whatshot,
                    contentDescription = "Streak Protection",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
                
                Column {
                    Text(
                        text = "Streak Protection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Complete this test to protect your streak for 2 days",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Navigation and creation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.currentStep = 4 },
                modifier = Modifier.weight(1f),
                enabled = !isCreating
            ) {
                Text("Back")
            }
            
            Button(
                onClick = { 
                    isCreating = true
                    // Simulate API call
                    android.os.Handler().postDelayed({
                        isCreating = false
                        onTestCreated()
                    }, 1500)
                },
                modifier = Modifier.weight(1f),
                enabled = !isCreating
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Create Test")
                }
            }
        }
    }
    
    // Calendar dialog
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
