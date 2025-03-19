package com.studyquest.app.ui.emojistyle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EmojiCreateTestViewModel : ViewModel() {
    // Step state
    var currentStep by mutableStateOf(1)
    var totalSteps = 5
    
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
    
    // Mock data with emojis
    val grades = listOf(
        Grade(1, "Grade 9", "9️⃣"),
        Grade(2, "Grade 10", "🔟"),
        Grade(3, "Grade 11", "1️⃣1️⃣"),
        Grade(4, "Grade 12", "1️⃣2️⃣")
    )
    
    val subjects = listOf(
        Subject(1, "Mathematics", "🧮", "#4285F4"),
        Subject(2, "Physics", "🔬", "#EA4335"),
        Subject(3, "Chemistry", "🧪", "#FBBC05"),
        Subject(4, "Biology", "🧬", "#34A853")
    )
    
    val textbooks = listOf(
        Textbook(1, "Algebra Fundamentals", 1, 1, 250, "📘"),
        Textbook(2, "Geometry Essentials", 1, 1, 220, "📐"),
        Textbook(3, "Advanced Trigonometry", 1, 2, 300, "📚"),
        Textbook(4, "Mechanics & Dynamics", 2, 2, 280, "⚙️"),
        Textbook(5, "Organic Chemistry", 3, 3, 340, "🧪"),
        Textbook(6, "Cell Biology", 4, 3, 320, "🔬")
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
}

data class Grade(
    val id: Int,
    val name: String,
    val emoji: String
)

data class Subject(
    val id: Int,
    val name: String,
    val emoji: String,
    val colorHex: String
)

data class Textbook(
    val id: Int,
    val name: String,
    val subjectId: Int,
    val gradeId: Int,
    val totalPages: Int,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiStyleCreateTest(
    onNavigateBack: () -> Unit,
    onTestCreated: () -> Unit,
    viewModel: EmojiCreateTestViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Create Test")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text(
                            text = "⬅️",
                            fontSize = 24.sp
                        )
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
            // Progress steps with emojis
            EmojiProgressIndicator(
                currentStep = viewModel.currentStep,
                totalSteps = viewModel.totalSteps
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
fun EmojiProgressIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalSteps) {
                // Emoji indicators for each step
                Text(
                    text = when {
                        i < currentStep -> "✅"
                        i == currentStep -> when (currentStep) {
                            1 -> "9️⃣"
                            2 -> "📚"
                            3 -> "📖"
                            4 -> "📝"
                            5 -> "📅"
                            else -> "🔵"
                        }
                        else -> "⚪"
                    },
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                // Connector line
                if (i < totalSteps) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(2.dp)
                            .background(
                                if (i < currentStep) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Step label
        Text(
            text = when (currentStep) {
                1 -> "Step 1: Choose Your Grade 📚"
                2 -> "Step 2: Pick a Subject 🔍"
                3 -> "Step 3: Select Textbook 📖"
                4 -> "Step 4: Set Content 📝"
                5 -> "Step 5: Schedule Test ⏰"
                else -> ""
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun GradeSelectionStep(
    viewModel: EmojiCreateTestViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fun header
        Text(
            text = "What grade are you in? 🤔",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Grade options with emojis
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewModel.grades.forEach { grade ->
                val isSelected = viewModel.gradeId == grade.id
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = grade.emoji,
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = grade.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (isSelected) {
                            Text(
                                text = "Selected! ✓",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
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
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            enabled = viewModel.isCurrentStepValid()
        ) {
            Text("Continue ➡️", fontSize = 18.sp)
        }
    }
}

@Composable
fun SubjectSelectionStep(
    viewModel: EmojiCreateTestViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fun header
        Text(
            text = "Choose a subject! 🧠",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Subject options with emojis
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewModel.subjects.forEach { subject ->
                val isSelected = viewModel.subjectId == subject.id
                val subjectColor = Color(android.graphics.Color.parseColor(subject.colorHex))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = subject.emoji,
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (isSelected) {
                            Text(
                                text = "Great choice! ✓",
                                color = subjectColor,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.currentStep = 1 },
                modifier = Modifier.weight(1f)
            ) {
                Text("⬅️ Back")
            }
            
            Button(
                onClick = { viewModel.currentStep = 3 },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isCurrentStepValid()
            ) {
                Text("Next ➡️")
            }
        }
    }
}

@Composable
fun TextbookSelectionStep(
    viewModel: EmojiCreateTestViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fun header
        Text(
            text = "Pick your textbook! 📚",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Filtered textbooks with emojis
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
                    Text(
                        text = "😢",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "No textbooks found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Try a different grade or subject",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Textbook options with emojis
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viewModel.filteredTextbooks.forEach { textbook ->
                    val isSelected = viewModel.textbookId == textbook.id
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
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
                            // Textbook emoji
                            Text(
                                text = textbook.emoji,
                                fontSize = 36.sp
                            )
                            
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
                                    text = "📄 ${textbook.totalPages} pages",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            // Selected indicator
                            if (isSelected) {
                                Text(
                                    text = "✅",
                                    fontSize = 24.sp
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
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.currentStep = 2 },
                modifier = Modifier.weight(1f)
            ) {
                Text("⬅️ Back")
            }
            
            Button(
                onClick = { viewModel.currentStep = 4 },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isCurrentStepValid()
            ) {
                Text("Next ➡️")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentConfigStep(
    viewModel: EmojiCreateTestViewModel
) {
    val selectedTextbook = viewModel.selectedTextbook
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fun header
        Text(
            text = "Set your test content 📝",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Form with emoji labels
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("📌 Test Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Page range with emojis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.pagesFrom.toString(),
                    onValueChange = { 
                        val value = it.toIntOrNull() ?: 1
                        viewModel.pagesFrom = value.coerceIn(1, selectedTextbook?.totalPages ?: 100)
                    },
                    label = { Text("📄 Start Page") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = viewModel.pagesTo.toString(),
                    onValueChange = { 
                        val value = it.toIntOrNull() ?: viewModel.pagesFrom
                        viewModel.pagesTo = value.coerceIn(viewModel.pagesFrom, selectedTextbook?.totalPages ?: 100)
                    },
                    label = { Text("📑 End Page") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            // Question count with slider and emoji
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "❓",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Questions: ${viewModel.questionCount}")
            }
            
            Slider(
                value = viewModel.questionCount.toFloat(),
                onValueChange = { viewModel.questionCount = it.toInt() },
                valueRange = 5f..30f,
                steps = 5,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // XP reward card with emojis
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🏆 Rewards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Complete this test to earn:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Row(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "⭐ ${viewModel.questionCount * 5} XP",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            
                            Text(
                                text = "🪙 ${viewModel.questionCount * 2} coins",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.currentStep = 3 },
                modifier = Modifier.weight(1f)
            ) {
                Text("⬅️ Back")
            }
            
            Button(
                onClick = { viewModel.currentStep = 5 },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isCurrentStepValid()
            ) {
                Text("Next ➡️")
            }
        }
    }
}

@Composable
fun SchedulingStep(
    viewModel: EmojiCreateTestViewModel,
    onTestCreated: () -> Unit
) {
    var isCreating by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fun header
        Text(
            text = "Schedule your test ⏰",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Form with emoji decorations
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Exam date button with emoji
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { 
                    // Calendar would show here in a real app
                    viewModel.examDate = LocalDate.now().plusDays(14)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
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
                            ) ?: "Tap to set (optional)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Reminders toggle with emoji
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔔",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
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
                            text = "Get daily study reminders",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Switch(
                        checked = viewModel.scheduledReminders,
                        onCheckedChange = { viewModel.scheduledReminders = it }
                    )
                }
            }
            
            // Streak card with emojis
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    
                    Column {
                        Text(
                            text = "Streak Shield",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Complete this test to protect your streak for 2 days! 🛡️",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Create test button with emojis
            Button(
                onClick = { 
                    isCreating = true
                    // Simulate API call
                    android.os.Handler().postDelayed({
                        isCreating = false
                        onTestCreated()
                    }, 1500)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isCreating
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Create Test 🚀",
                        fontSize = 18.sp
                    )
                }
            }
            
            OutlinedButton(
                onClick = { viewModel.currentStep = 4 },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCreating
            ) {
                Text("⬅️ Back")
            }
        }
    }
}
