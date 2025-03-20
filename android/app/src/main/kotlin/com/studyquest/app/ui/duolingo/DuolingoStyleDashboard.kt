package com.studyquest.app.ui.duolingo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuolingoStyleDashboard(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StudyQuest") },
                actions = {
                    IconButton(onClick = { /* Open profile */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("tests") },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Tests") },
                    label = { Text("Tests") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Open leaderboard */ },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Leaderboard") },
                    label = { Text("Rank") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Create test */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Test",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DuolingoUserCard(
                    name = "John",
                    streak = 7,
                    xp = 1250,
                    level = 5
                )
            }
            
            item {
                DailyGoalCard(
                    completedToday = false,
                    xpToday = 25,
                    dailyGoal = 50
                )
            }
            
            item {
                Text(
                    text = "📚 Your Study Path",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(subjectLessons) { lesson ->
                SubjectLessonCard(lesson = lesson)
            }
            
            item {
                Text(
                    text = "📅 Upcoming Tests",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(upcomingTests) { test ->
                DuolingoTestCard(test = test)
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun DuolingoUserCard(name: String, streak: Int, xp: Int, level: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().toString(),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Hi, $name!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Level",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Level $level",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥",
                        fontSize = 20.sp
                    )
                    Text(
                        text = "$streak",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "day streak",
                    fontSize = 12.sp
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐",
                        fontSize = 20.sp
                    )
                    Text(
                        text = "$xp",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = "total XP",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DailyGoalCard(completedToday: Boolean, xpToday: Int, dailyGoal: Int) {
    val progress = (xpToday.toFloat() / dailyGoal).coerceIn(0f, 1f)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (completedToday) "Daily Goal Completed! 🎉" else "Today's Goal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (completedToday) MaterialTheme.colorScheme.tertiary else Color.Unspecified
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$xpToday XP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (completedToday) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "$dailyGoal XP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (!completedToday) {
                Text(
                    text = "Keep going! You need ${dailyGoal - xpToday} more XP to reach your goal.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "Great job! You've reached your daily goal.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

data class SubjectLesson(
    val id: Int,
    val name: String,
    val emoji: String,
    val totalLessons: Int,
    val completedLessons: Int,
    val color: Color
)

val subjectLessons = listOf(
    SubjectLesson(1, "Mathematics", "🧮", 10, 4, Color(0xFF58C9B9)),
    SubjectLesson(2, "Physics", "⚛️", 8, 2, Color(0xFF9A77CF)),
    SubjectLesson(3, "History", "📜", 12, 5, Color(0xFFF26CA7))
)

@Composable
fun SubjectLessonCard(lesson: SubjectLesson) {
    val progress = (lesson.completedLessons.toFloat() / lesson.totalLessons).coerceIn(0f, 1f)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(lesson.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lesson.emoji,
                    fontSize = 24.sp
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = lesson.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = lesson.color,
                    trackColor = Color.LightGray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${lesson.completedLessons}/${lesson.totalLessons} completed",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Button(
                onClick = { /* Open lesson */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lesson.color
                ),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text("Study")
            }
        }
    }
}

data class DuolingoTest(
    val id: Int,
    val title: String,
    val subject: String,
    val emoji: String,
    val date: LocalDate,
    val xpReward: Int
)

val upcomingTests = listOf(
    DuolingoTest(1, "Algebra Quiz", "Mathematics", "🧮", LocalDate.now().plusDays(2), 50),
    DuolingoTest(2, "Physics Test", "Physics", "⚛️", LocalDate.now().plusDays(5), 100)
)

@Composable
fun DuolingoTestCard(test: DuolingoTest) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = test.emoji,
                    fontSize = 24.sp
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = test.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = test.subject,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = test.date.format(formatter),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+${test.xpReward} XP",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Button(
                onClick = { /* Prepare for test */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text("Prepare")
            }
        }
    }
}