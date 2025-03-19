package com.studyquest.app.ui.duolingo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyquest.app.data.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class DuolingoStyleViewModel : ViewModel() {
    // User stats
    var streak by mutableStateOf(7)
    var xp by mutableStateOf(245)
    var coins by mutableStateOf(320)
    var hearts by mutableStateOf(4)
    
    // Study path data
    var currentPathPosition by mutableStateOf(3)
    val pathUnits = listOf(
        PathUnit("Algebra Basics", "Basic algebra concepts", 3, 3, PathUnitStatus.COMPLETED),
        PathUnit("Linear Equations", "Solving linear equations", 5, 5, PathUnitStatus.COMPLETED),
        PathUnit("Quadratic Equations", "Solving quadratic equations", 4, 3, PathUnitStatus.IN_PROGRESS),
        PathUnit("Polynomials", "Working with polynomial functions", 4, 0, PathUnitStatus.LOCKED),
        PathUnit("Trigonometry", "Introduction to trigonometry", 6, 0, PathUnitStatus.LOCKED)
    )
    
    // Upcoming tests
    var upcomingTests by mutableStateOf<List<Test>>(
        listOf(
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
    )
}

enum class PathUnitStatus {
    COMPLETED,
    IN_PROGRESS,
    LOCKED
}

data class PathUnit(
    val title: String,
    val description: String,
    val totalLessons: Int,
    val completedLessons: Int,
    val status: PathUnitStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuolingoStyleDashboard(
    onNavigateToCreateTest: () -> Unit,
    viewModel: DuolingoStyleViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            DuolingoStyleTopBar(
                streak = viewModel.streak,
                xp = viewModel.xp,
                coins = viewModel.coins,
                hearts = viewModel.hearts
            )
        },
        bottomBar = {
            DuolingoStyleBottomNav()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Daily goal section
            DailyGoalCard()
            
            // Learning path
            LearningPathSection(
                pathUnits = viewModel.pathUnits,
                currentPosition = viewModel.currentPathPosition
            )
            
            // Upcoming Tests Section
            UpcomingTestsSection(
                tests = viewModel.upcomingTests,
                onCreateTest = onNavigateToCreateTest
            )
        }
    }
}

@Composable
fun DuolingoStyleTopBar(
    streak: Int,
    xp: Int,
    coins: Int,
    hearts: Int
) {
    Surface(
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // XP
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "XP",
                        tint = Color(0xFFFFC940),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$xp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Color(0xFFFF9600),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$streak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                // Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$coins",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                // Hearts
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Hearts",
                        tint = Color(0xFFFF4B4B),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$hearts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DuolingoStyleBottomNav() {
    var selectedTab by remember { mutableStateOf(0) }
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Filled.School else Icons.Outlined.School,
                    contentDescription = "Learn"
                )
            },
            label = { Text("Learn") },
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 }
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (selectedTab == 1) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                    contentDescription = "Tests"
                )
            },
            label = { Text("Tests") },
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 }
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (selectedTab == 2) Icons.Filled.Leaderboard else Icons.Outlined.Leaderboard,
                    contentDescription = "Leaderboard"
                )
            },
            label = { Text("Progress") },
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 }
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (selectedTab == 3) Icons.Filled.Store else Icons.Outlined.Store,
                    contentDescription = "Shop"
                )
            },
            label = { Text("Shop") },
            selected = selectedTab == 3,
            onClick = { selectedTab = 3 }
        )
    }
}

@Composable
fun DailyGoalCard() {
    var goalExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Goal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { goalExpanded = !goalExpanded }
                ) {
                    Icon(
                        imageVector = if (goalExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (goalExpanded) "Show less" else "Show more"
                    )
                }
            }
            
            // Progress indicator
            LinearProgressIndicator(
                progress = 0.4f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.background
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "10 XP today",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Goal: 25 XP",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            AnimatedVisibility(
                visible = goalExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "Weekly streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DayProgressIndicator(day = "Mon", completed = true)
                        DayProgressIndicator(day = "Tue", completed = true)
                        DayProgressIndicator(day = "Wed", completed = false, isToday = true)
                        DayProgressIndicator(day = "Thu", completed = false)
                        DayProgressIndicator(day = "Fri", completed = false)
                        DayProgressIndicator(day = "Sat", completed = false)
                        DayProgressIndicator(day = "Sun", completed = false)
                    }
                }
            }
        }
    }
}

@Composable
fun DayProgressIndicator(
    day: String,
    completed: Boolean,
    isToday: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (completed) MaterialTheme.colorScheme.primary
                    else if (isToday) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (completed) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else if (isToday) {
                Text(
                    text = day.first().toString(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = day.first().toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Text(
            text = day,
            style = MaterialTheme.typography.bodySmall,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LearningPathSection(
    pathUnits: List<PathUnit>,
    currentPosition: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Learning Path",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        pathUnits.forEachIndexed { index, unit ->
            PathUnitItem(
                unit = unit,
                isFirst = index == 0,
                isLast = index == pathUnits.size - 1,
                isCurrent = index == currentPosition - 1
            )
        }
    }
}

@Composable
fun PathUnitItem(
    unit: PathUnit,
    isFirst: Boolean,
    isLast: Boolean,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Left line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(if (isFirst) 24.dp else 48.dp)
                .background(if (unit.status != PathUnitStatus.LOCKED) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f))
                .align(if (isFirst) Alignment.Bottom else Alignment.Top)
                .offset(x = 20.dp)
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = if (isFirst) 24.dp else 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (unit.status) {
                            PathUnitStatus.COMPLETED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            PathUnitStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
                            PathUnitStatus.LOCKED -> Color.Gray.copy(alpha = 0.1f)
                        }
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = unit.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (unit.status) {
                                PathUnitStatus.LOCKED -> Color.Gray
                                else -> MaterialTheme.colorScheme.onBackground
                            }
                        )
                        
                        Text(
                            text = unit.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (unit.status) {
                                PathUnitStatus.LOCKED -> Color.Gray
                                else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            }
                        )
                        
                        if (unit.status != PathUnitStatus.LOCKED) {
                            LinearProgressIndicator(
                                progress = unit.completedLessons.toFloat() / unit.totalLessons,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            
                            Text(
                                text = "${unit.completedLessons}/${unit.totalLessons} lessons",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    when (unit.status) {
                        PathUnitStatus.COMPLETED -> {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        PathUnitStatus.IN_PROGRESS -> {
                            FilledTonalButton(onClick = { /* Start unit */ }) {
                                Text("Continue")
                            }
                        }
                        PathUnitStatus.LOCKED -> {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Locked",
                                tint = Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Right line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(if (isLast) 24.dp else 48.dp)
                .background(if (unit.status != PathUnitStatus.LOCKED && !isLast) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f))
                .align(if (isLast) Alignment.Top else Alignment.Bottom)
                .offset(x = 20.dp)
        )
    }
}

@Composable
fun UpcomingTestsSection(
    tests: List<Test>,
    onCreateTest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Tests",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            FilledTonalButton(onClick = onCreateTest) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create Test",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Test")
            }
        }
        
        if (tests.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = "No Tests",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "No upcoming tests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Create a test to start practicing for your exams",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = onCreateTest) {
                        Text("Create Your First Test")
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tests.forEach { test ->
                    DuolingoStyleTestCard(test = test)
                }
            }
        }
    }
}

@Composable
fun DuolingoStyleTestCard(test: Test) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                test.title.contains("Math") -> Icons.Filled.Calculate
                                test.title.contains("Physics") -> Icons.Filled.Science
                                test.title.contains("Chemistry") -> Icons.Filled.Science
                                else -> Icons.Filled.MenuBook
                            },
                            contentDescription = "Subject Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                        
                        Text(
                            text = test.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // XP reward
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "XP",
                                tint = Color(0xFFFFC940),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "+${test.questionCount * 5} XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pages indicator
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = "Pages",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Pages ${test.pagesFrom}-${test.pagesTo}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Question count indicator
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QuestionAnswer,
                                contentDescription = "Questions",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${test.questionCount} questions",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                test.examDate?.let {
                    Text(
                        text = "Exam date: ${it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Practice button
                Button(
                    onClick = { /* Start practice */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Start Practice")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DuolingoStyleDashboardPreview() {
    MaterialTheme {
        DuolingoStyleDashboard(onNavigateToCreateTest = {})
    }
}
