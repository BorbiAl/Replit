package com.studyquest.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyquest.app.data.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.LocalFireDepartment

class DashboardViewModel : ViewModel() {
    // User data
    var firstName by mutableStateOf("John")
    var streak by mutableStateOf(5)
    var xp by mutableStateOf(1250)
    var level by mutableStateOf(8)
    
    // Tests data
    var upcomingTests by mutableStateOf<List<Test>>(emptyList())
    var isLoading by mutableStateOf(false)
    
    init {
        // In a real app, these would be loaded from the API
        loadUserData()
        loadUpcomingTests()
    }
    
    private fun loadUserData() {
        // This would make an API call in a real app
    }
    
    private fun loadUpcomingTests() {
        isLoading = true
        // Mock data for demo purposes
        upcomingTests = listOf(
            Test(
                id = 1,
                userId = 1,
                title = "Mathematics: Algebra Fundamentals",
                gradeId = 1,
                subjectId = 1,
                textbookId = 1,
                pagesFrom = 10,
                pagesTo = 30,
                questionCount = 15,
                examDate = LocalDate.now().plusDays(5),
                scheduledReminders = true,
                createdAt = "2023-03-15T10:30:00Z",
                completed = false,
                score = null
            ),
            Test(
                id = 2,
                userId = 1,
                title = "Physics: Mechanics & Dynamics",
                gradeId = 2,
                subjectId = 2,
                textbookId = 4,
                pagesFrom = 45,
                pagesTo = 72,
                questionCount = 10,
                examDate = LocalDate.now().plusDays(10),
                scheduledReminders = true,
                createdAt = "2023-03-16T14:20:00Z",
                completed = false,
                score = null
            )
        )
        isLoading = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCreateTest: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTest,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Create Test",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Welcome section
            WelcomeSection(
                firstName = viewModel.firstName,
                testsCount = viewModel.upcomingTests.size
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // User stats section
            UserStatsSection(
                streak = viewModel.streak,
                xp = viewModel.xp,
                level = viewModel.level
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Upcoming tests section
            UpcomingTestsSection(
                tests = viewModel.upcomingTests,
                isLoading = viewModel.isLoading,
                onCreateTest = onNavigateToCreateTest
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress section
            ProgressSection()
        }
    }
}

@Composable
fun WelcomeSection(firstName: String, testsCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Welcome back, $firstName!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (testsCount > 0) "You have $testsCount upcoming tests" else "No upcoming tests",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* Open tests page */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("View All Tests")
            }
        }
    }
}

@Composable
fun UserStatsSection(streak: Int, xp: Int, level: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Streak card
        Card(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Whatshot,
                        contentDescription = "Streak",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$streak days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Current Streak",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // XP and Level card
        Card(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "XP",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$xp XP",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Level $level",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun UpcomingTestsSection(
    tests: List<Test>,
    isLoading: Boolean,
    onCreateTest: () -> Unit
) {
    Column {
        Text(
            text = "Upcoming Tests",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                CircularProgressIndicator()
            }
        } else if (tests.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "No upcoming tests",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = onCreateTest) {
                        Text("Create a Test")
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tests.forEach { test ->
                    TestCard(test = test)
                }
            }
        }
    }
}

@Composable
fun TestCard(test: Test) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = "Test",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = test.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                test.examDate?.let {
                    Text(
                        text = "Exam date: ${it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = "${test.questionCount} questions (pp. ${test.pagesFrom}-${test.pagesTo})",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Button(
                onClick = { /* Open test */ }
            ) {
                Text("Practice")
            }
        }
    }
}

@Composable
fun ProgressSection() {
    Column {
        Text(
            text = "Your Progress",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProgressStat(title = "Average Score", value = "85%")
                    ProgressStat(title = "Tests Completed", value = "12")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProgressStat(title = "Study Time", value = "24h")
                    ProgressStat(title = "Questions Answered", value = "156")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { /* Navigate to progress page */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("View Detailed Progress")
                }
            }
        }
    }
}

@Composable
fun ProgressStat(title: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
