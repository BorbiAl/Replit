package com.studyquest.app.ui.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.studyquest.app.devices.DeviceDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen() {
    val deviceDetector = remember { DeviceDetector.getInstance() }
    val s24UltraModule = remember { deviceDetector.getS24UltraModule() }
    
    // S24 Ultra optimizations
    LaunchedEffect(Unit) {
        // Use standard mode for tests screen (not high performance)
        s24UltraModule?.enableAdaptiveMode()
    }
    
    // Filter state
    var showCompleted by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    
    // Test data (would come from repository)
    val allTests = remember {
        listOf(
            TestItem(
                id = 1,
                title = "Math Midterm",
                subject = "Mathematics",
                date = LocalDate.now().plusDays(2),
                completed = false,
                score = null
            ),
            TestItem(
                id = 2,
                title = "Physics Quiz",
                subject = "Physics",
                date = LocalDate.now().plusDays(5),
                completed = false,
                score = null
            ),
            TestItem(
                id = 3,
                title = "Chemistry Lab Report",
                subject = "Chemistry",
                date = LocalDate.now().plusDays(-1),
                completed = true,
                score = 85
            ),
            TestItem(
                id = 4,
                title = "Literature Essay",
                subject = "Literature",
                date = LocalDate.now().plusDays(7),
                completed = false,
                score = null
            ),
            TestItem(
                id = 5,
                title = "Biology Test",
                subject = "Biology",
                date = LocalDate.now().plusDays(-3),
                completed = true,
                score = 92
            )
        )
    }
    
    // Filter tests
    val filteredTests = allTests.filter { test ->
        (showCompleted || !test.completed) &&
        (selectedSubject == null || test.subject == selectedSubject)
    }
    
    // Available subjects
    val subjects = allTests.map { it.subject }.distinct()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Tests",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* Open filter dialog */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { /* Open add test form */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Test")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add new test */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Test")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            FilterSection(
                subjects = subjects,
                selectedSubject = selectedSubject,
                showCompleted = showCompleted,
                onSubjectSelected = { selectedSubject = it },
                onShowCompletedChanged = { showCompleted = it }
            )
            
            Divider()
            
            if (filteredTests.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tests match your filters",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Tests list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredTests) { test ->
                        TestCard(test = test)
                    }
                    
                    // Add some space at the bottom for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    subjects: List<String>,
    selectedSubject: String?,
    showCompleted: Boolean,
    onSubjectSelected: (String?) -> Unit,
    onShowCompletedChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Status filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status:",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            FilterChip(
                selected = !showCompleted,
                onClick = { onShowCompletedChanged(false) },
                label = { Text("Upcoming") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
            
            FilterChip(
                selected = showCompleted,
                onClick = { onShowCompletedChanged(true) },
                label = { Text("Completed") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subject filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Subject:",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            FilterChip(
                selected = selectedSubject == null,
                onClick = { onSubjectSelected(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
            
            subjects.take(3).forEach { subject ->
                FilterChip(
                    selected = selectedSubject == subject,
                    onClick = { onSubjectSelected(subject) },
                    label = { Text(subject.take(10)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TestCard(test: TestItem) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Subject color indicator
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getSubjectColor(test.subject)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = test.subject.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = test.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    if (test.completed) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = test.subject,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (test.completed) {
                            "Completed"
                        } else {
                            "Due ${test.date.format(dateFormatter)}"
                        },
                        fontSize = 14.sp,
                        color = if (test.completed) 
                            MaterialTheme.colorScheme.primary
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (test.score != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Score: ${test.score}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = getScoreColor(test.score)
                        )
                    }
                }
            }
        }
    }
}

// Helper functions
fun getSubjectColor(subject: String): Color {
    return when (subject.lowercase()) {
        "mathematics", "math" -> Color(0xFF4285F4)
        "physics" -> Color(0xFFEA4335)
        "chemistry" -> Color(0xFF34A853)
        "biology" -> Color(0xFFFBBC05)
        "literature" -> Color(0xFF9C27B0)
        else -> Color(0xFF607D8B)
    }
}

fun getScoreColor(score: Int): Color {
    return when {
        score >= 90 -> Color(0xFF4CAF50)  // Green
        score >= 80 -> Color(0xFF8BC34A)  // Light Green
        score >= 70 -> Color(0xFFFFC107)  // Amber
        score >= 60 -> Color(0xFFFF9800)  // Orange
        else -> Color(0xFFF44336)         // Red
    }
}

// Data class
data class TestItem(
    val id: Int,
    val title: String,
    val subject: String,
    val date: LocalDate,
    val completed: Boolean,
    val score: Int?
)