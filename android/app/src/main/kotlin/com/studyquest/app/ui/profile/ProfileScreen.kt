package com.studyquest.app.ui.profile

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyquest.app.devices.DeviceDetector
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val deviceDetector = remember { DeviceDetector.getInstance() }
    val s24UltraModule = remember { deviceDetector.getS24UltraModule() }
    
    // S24 Ultra optimizations
    LaunchedEffect(Unit) {
        s24UltraModule?.enableAdaptiveMode()
    }
    
    // Check if we have S24 Ultra features
    val isS24Ultra = remember { s24UltraModule?.isS24Ultra() ?: false }
    
    // Student profile data (would come from repository)
    val studentName = "Alex Johnson"
    val email = "alex.johnson@example.com"
    val xpPoints = 6950
    val level = 8
    val streakDays = 15
    val testsCompleted = 21
    val badges = listOf(
        Badge("🔥", "15-Day Streak", "Maintained a 15-day streak"),
        Badge("🥇", "Math Master", "Scored 90%+ on 5 Math tests"),
        Badge("📚", "Bookworm", "Studied from 10 different textbooks"),
        Badge("⚡", "Quick Learner", "Completed 3 tests in one day")
    )
    
    val studyStats = listOf(
        StatItem("Tests Completed", testsCompleted.toString(), Icons.Default.Assignment),
        StatItem("Average Score", "85%", Icons.Default.Grade),
        StatItem("Study Hours", "42h", Icons.Default.AccessTime),
        StatItem("Streak", "$streakDays days", Icons.Default.LocalFireDepartment)
    )
    
    val recentActivity = listOf(
        ActivityItem(
            "Completed Physics Quiz",
            "85% score",
            LocalDate.now().minusDays(1),
            Icons.Default.Check
        ),
        ActivityItem(
            "Started Chemistry Review",
            "In progress",
            LocalDate.now().minusDays(2),
            Icons.Default.PlayArrow
        ),
        ActivityItem(
            "Earned Math Master Badge",
            "Achievement unlocked",
            LocalDate.now().minusDays(4),
            Icons.Default.EmojiEvents
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* Open settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
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
            // Profile header
            item {
                ProfileHeader(name = studentName, email = email, level = level, xp = xpPoints)
            }
            
            // S24 Ultra specific features
            if (isS24Ultra) {
                item {
                    S24UltraFeatures()
                }
            }
            
            // Stats section
            item {
                Text(
                    "Study Statistics",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                StatsGrid(stats = studyStats)
            }
            
            // Badges section
            item {
                Text(
                    "Achievements",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            // Badges grid
            item {
                BadgesGrid(badges = badges)
            }
            
            // Recent activity section
            item {
                Text(
                    "Recent Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            // Activity items
            items(recentActivity) { activity ->
                ActivityCard(activity = activity)
            }
            
            // Settings section
            item {
                Text(
                    "Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                SettingsSection()
            }
            
            // Add some space at the bottom
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String, level: Int, xp: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            
            // Email
            Text(
                text = email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Level and XP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Level $level",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$xp XP",
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Edit profile button
            Button(
                onClick = { /* Edit profile */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }
        }
    }
}

@Composable
fun S24UltraFeatures() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Samsung S24 Ultra Features",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // S Pen feature
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "S Pen Ready",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                // Enhanced display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Enhanced UI",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                // Performance boost
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Performance Boost",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun StatsGrid(stats: List<StatItem>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                stats.take(2).forEach { stat ->
                    StatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                stats.takeLast(2).forEach { stat ->
                    StatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(stat: StatItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(4.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = stat.value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Text(
                    text = stat.name,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BadgesGrid(badges: List<Badge>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            badges.forEach { badge ->
                BadgeItem(badge = badge)
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Badge icon
        Text(
            text = badge.emoji,
            fontSize = 32.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Badge name
        Text(
            text = badge.name,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
fun ActivityCard(activity: ActivityItem) {
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
            // Activity icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                
                Text(
                    text = activity.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = activity.date.format(dateFormatter),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SettingsItem(
                title = "Notifications",
                icon = Icons.Default.Notifications,
                onClick = { /* Open notifications settings */ }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SettingsItem(
                title = "Dark Mode",
                icon = Icons.Default.DarkMode,
                onClick = { /* Toggle dark mode */ }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SettingsItem(
                title = "Privacy",
                icon = Icons.Default.Lock,
                onClick = { /* Open privacy settings */ }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SettingsItem(
                title = "Help & Support",
                icon = Icons.Default.Help,
                onClick = { /* Open help */ }
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Data classes
data class Badge(
    val emoji: String,
    val name: String,
    val description: String
)

data class StatItem(
    val name: String,
    val value: String,
    val icon: ImageVector
)

data class ActivityItem(
    val title: String,
    val description: String,
    val date: LocalDate,
    val icon: ImageVector
)