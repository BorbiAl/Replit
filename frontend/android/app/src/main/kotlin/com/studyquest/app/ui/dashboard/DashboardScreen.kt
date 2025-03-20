package com.studyquest.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyquest.app.devices.DeviceDetector
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    
    // Device detector for S24 Ultra optimizations
    val deviceDetector = remember { DeviceDetector.getInstance() }
    val s24UltraModule = remember { deviceDetector.getS24UltraModule() }
    
    // Apply S24 Ultra high performance mode for dashboard animations
    LaunchedEffect(Unit) {
        s24UltraModule?.enableHighPerformanceMode()
    }
    
    // Student profile data (for now hardcoded, would come from repository)
    val studentName = "Alex Johnson"
    val streakDays = 15
    val xpPoints = 4280
    val level = 8
    
    // Upcoming tests (would come from repository)
    val upcomingTests = remember {
        listOf(
            TestInfo(
                id = 1,
                title = "Math Midterm",
                date = LocalDate.now().plusDays(2),
                subject = "Mathematics",
                isCompleted = false
            ),
            TestInfo(
                id = 2,
                title = "Physics Quiz",
                date = LocalDate.now().plusDays(5),
                subject = "Physics",
                isCompleted = false
            ),
            TestInfo(
                id = 3,
                title = "Literature Essay",
                date = LocalDate.now().plusDays(7),
                subject = "Literature",
                isCompleted = false
            )
        )
    }
    
    // Learning path progress
    val subjectProgress = remember {
        listOf(
            SubjectProgress("Math", 78),
            SubjectProgress("Physics", 45),
            SubjectProgress("Chemistry", 62),
            SubjectProgress("Biology", 30)
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "StudyQuest",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome section
            item {
                WelcomeSection(
                    name = studentName,
                    streakDays = streakDays,
                    xpPoints = xpPoints,
                    level = level
                )
            }
            
            // Daily streak
            item {
                DailyStreakCard(streakDays = streakDays)
            }
            
            // Subject progress
            item {
                Text(
                    "Learning Progress",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjectProgress) { subject ->
                        SubjectProgressCard(subject = subject)
                    }
                }
            }
            
            // Upcoming tests
            item {
                Text(
                    "Upcoming Tests",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            
            items(upcomingTests) { test ->
                UpcomingTestCard(test = test)
            }
            
            // S24 Ultra specific feature call-out
            item {
                if (s24UltraModule?.isS24Ultra() == true) {
                    S24UltraFeatureCard()
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun WelcomeSection(
    name: String,
    streakDays: Int,
    xpPoints: Int,
    level: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar place (would use actual image)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Welcome back,",
                    fontSize = 16.sp
                )
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "$streakDays day streak",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Level $level · $xpPoints XP",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyStreakCard(streakDays: Int) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔥 $streakDays DAY STREAK 🔥",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Past days (would generate dynamically)
                DayIndicator(day = "Mon", isCompleted = true, isToday = false)
                DayIndicator(day = "Tue", isCompleted = true, isToday = false)
                DayIndicator(day = "Wed", isCompleted = true, isToday = false)
                DayIndicator(day = "Thu", isCompleted = true, isToday = false)
                DayIndicator(day = "Fri", isCompleted = true, isToday = false)
                DayIndicator(day = "Sat", isCompleted = true, isToday = false)
                DayIndicator(day = "Sun", isCompleted = false, isToday = true)
            }
        }
    }
}

@Composable
fun DayIndicator(day: String, isCompleted: Boolean, isToday: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isToday -> MaterialTheme.colorScheme.primary
                        isCompleted -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isCompleted) "✓" else "",
                color = if (isToday) MaterialTheme.colorScheme.onPrimary 
                        else MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = day,
            fontSize = 12.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SubjectProgressCard(subject: SubjectProgress) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = subject.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CircularProgressIndicator(
                progress = subject.progress / 100f,
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${subject.progress}%",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun UpcomingTestCard(test: TestInfo) {
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
                Text(
                    text = test.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Text(
                    text = test.subject,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = test.date.format(dateFormatter),
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), test.date)
                Text(
                    text = if (daysUntil <= 0) "Today!" else "in $daysUntil days",
                    fontSize = 12.sp,
                    fontWeight = if (daysUntil <= 2) FontWeight.Bold else FontWeight.Normal,
                    color = if (daysUntil <= 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun S24UltraFeatureCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Samsung S24 Ultra",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "You're using a premium device! Enjoy enhanced features like S Pen support and optimized performance.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { /* Navigate to S Pen features */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Try S Pen Features")
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

// Data classes
data class TestInfo(
    val id: Int,
    val title: String,
    val date: LocalDate,
    val subject: String,
    val isCompleted: Boolean
)

data class SubjectProgress(
    val name: String,
    val progress: Int // 0-100
)