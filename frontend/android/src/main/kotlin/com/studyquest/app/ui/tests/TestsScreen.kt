package com.studyquest.app.ui.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyquest.app.data.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TestsViewModel : ViewModel() {
    var tests by mutableStateOf<List<Test>>(emptyList())
    var isLoading by mutableStateOf(false)
    
    init {
        loadTests()
    }
    
    private fun loadTests() {
        isLoading = true
        // Mock data for demo purposes
        tests = listOf(
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
            ),
            Test(
                id = 3,
                userId = 1,
                title = "Chemistry: Organic Chemistry",
                gradeId = 3,
                subjectId = 3,
                textbookId = 5,
                pagesFrom = 120,
                pagesTo = 145,
                questionCount = 20,
                examDate = LocalDate.now().minusDays(5),
                scheduledReminders = true,
                createdAt = "2023-03-10T09:15:00Z",
                completed = true,
                score = 85
            )
        )
        isLoading = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(
    onNavigateToCreateTest: () -> Unit,
    viewModel: TestsViewModel = viewModel()
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
        ) {
            Text(
                text = "Your Tests",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Filter tabs
            var selectedTab by remember { mutableStateOf(0) }
            val tabs = listOf("All Tests", "Upcoming", "Completed")
            
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filter tests based on selected tab
            val filteredTests = when (selectedTab) {
                1 -> viewModel.tests.filter { !it.completed }
                2 -> viewModel.tests.filter { it.completed }
                else -> viewModel.tests
            }
            
            if (viewModel.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredTests.isEmpty()) {
                EmptyTestsView(onCreateTest = onNavigateToCreateTest)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTests) { test ->
                        TestListItem(test = test)
                    }
                }
            }
        }
    }
}

@Composable
fun TestListItem(test: Test) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Icon based on whether test is completed
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (test.completed) 
                            MaterialTheme.colorScheme.secondaryContainer 
                        else 
                            MaterialTheme.colorScheme.primaryContainer
                    )
            ) {
                Icon(
                    imageVector = if (test.completed) Icons.Filled.CheckCircle else Icons.Filled.LocalFireDepartment,
                    contentDescription = "Test Status",
                    tint = if (test.completed) 
                        MaterialTheme.colorScheme.secondary 
                    else 
                        MaterialTheme.colorScheme.primary
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
                
                if (test.completed && test.score != null) {
                    Text(
                        text = "Score: ${test.score}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    test.examDate?.let {
                        Text(
                            text = "Exam date: ${it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Text(
                    text = "${test.questionCount} questions (pp. ${test.pagesFrom}-${test.pagesTo})",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Button(
                onClick = { /* Navigate to test details/practice */ }
            ) {
                Text(if (test.completed) "Review" else "Practice")
            }
        }
    }
}

@Composable
fun EmptyTestsView(onCreateTest: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Empty state illustration would go here
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = "No Tests",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No tests found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Create a new test to start practicing",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onCreateTest
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Create a Test")
        }
    }
}
