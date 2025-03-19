package com.studyquest.app.ui.emojistyle

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyquest.app.data.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiStyleDashboard(
    onNavigateToCreateTest: () -> Unit
) {
    Scaffold(
        topBar = {
            EmojiTopBar()
        },
        bottomBar = {
            EmojiBottomNav()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Welcome card with streak
            WelcomeCard()
            
            // Learning path with emojis
            EmojiLearningPath()
            
            // Upcoming tests with emoji indicators
            UpcomingTestsSection(onCreateTest)
        }
    }
}

@Composable
fun EmojiTopBar() {
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
            // Profile with emoji
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 22.sp
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 20.sp
                    )
                    Text(
                        text = "7",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                // Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🪙",
                        fontSize = 20.sp
                    )
                    Text(
                        text = "320",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                // Hearts
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "❤️",
                        fontSize = 20.sp
                    )
                    Text(
                        text = "4",
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
fun EmojiBottomNav() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        NavigationBarItem(
            icon = { Text("📚", fontSize = 24.sp) },
            label = { Text("Learn") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Text("📝", fontSize = 24.sp) },
            label = { Text("Tests") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Text("📊", fontSize = 24.sp) },
            label = { Text("Progress") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Text("🛒", fontSize = 24.sp) },
            label = { Text("Shop") },
            selected = false,
            onClick = { }
        )
    }
}

@Composable
fun WelcomeCard() {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👋",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Hey, Scholar!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Daily goal
            Text(text = "Today's Goal: 25 XP 🎯")
            
            // Progress indicator
            LinearProgressIndicator(
                progress = 0.4f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.background
            )
            
            Text(text = "10/25 XP earned today ✨")
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Weekly streak with emojis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayEmoji(day = "M", emoji = "✅")
                DayEmoji(day = "T", emoji = "✅")
                DayEmoji(day = "W", emoji = "📌", isToday = true)
                DayEmoji(day = "T", emoji = "⏱️")
                DayEmoji(day = "F", emoji = "⏱️")
                DayEmoji(day = "S", emoji = "⏱️")
                DayEmoji(day = "S", emoji = "⏱️")
            }
        }
    }
}

@Composable
fun DayEmoji(day: String, emoji: String, isToday: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isToday) MaterialTheme.colorScheme.primaryContainer
                    else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 18.sp
            )
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
fun EmojiLearningPath() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚀",
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Your Learning Path",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Learning path with emojis
        LearningPathItem(emoji = "💯", title = "Algebra Basics", status = "Completed! 🏆")
        LearningPathItem(emoji = "📏", title = "Linear Equations", status = "Mastered! 🎓")
        LearningPathItem(emoji = "📊", title = "Quadratic Functions", status = "3/4 lessons 🔄")
        LearningPathItem(emoji = "🔢", title = "Polynomials", status = "Locked 🔒", isLocked = true)
        LearningPathItem(emoji = "📐", title = "Trigonometry", status = "Locked 🔒", isLocked = true)
    }
}

@Composable
fun LearningPathItem(emoji: String, title: String, status: String, isLocked: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unit emoji
            Text(
                text = emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (!isLocked && !status.contains("Completed") && !status.contains("Mastered")) {
                Button(onClick = { }) {
                    Text("Continue ▶️")
                }
            }
        }
    }
}

@Composable
fun UpcomingTestsSection(onCreateTest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "📝",
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Your Tests",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            Button(onClick = onCreateTest) {
                Text("+ New Test")
            }
        }
        
        // Example test
        EmojiTestCard(
            emoji = "🧮",
            title = "Algebra Fundamentals",
            daysLeft = 5,
            pages = "10-30",
            questions = 15
        )
        
        EmojiTestCard(
            emoji = "🔬",
            title = "Physics: Mechanics",
            daysLeft = 10,
            pages = "45-72",
            questions = 10
        )
        
        // Create test card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ),
            onClick = onCreateTest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "➕",
                    fontSize = 32.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Create New Test",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Earn up to 150 XP! 🌟",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun EmojiTestCard(
    emoji: String,
    title: String,
    daysLeft: Int,
    pages: String,
    questions: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏰",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "$daysLeft days left",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                // XP reward badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "+${questions * 5} XP 🏆")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Test details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TestDetailChip(emoji = "📖", label = "Pages $pages")
                TestDetailChip(emoji = "❓", label = "$questions questions")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Practice button
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start Practice 🚀")
            }
        }
    }
}

@Composable
fun TestDetailChip(emoji: String, label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
