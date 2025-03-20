package com.studyquest.app.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
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
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen() {
    val deviceDetector = remember { DeviceDetector.getInstance() }
    val s24UltraModule = remember { deviceDetector.getS24UltraModule() }
    
    // S24 Ultra optimizations - use high performance for animations
    LaunchedEffect(Unit) {
        s24UltraModule?.enableHighPerformanceMode()
    }
    
    // Local student
    val currentStudentId = remember { 4 } // ID of the current user
    
    // Time period tabs
    val timePeriods = listOf("Daily", "Weekly", "Monthly", "All Time")
    var selectedTabIndex by remember { mutableStateOf(1) } // Weekly by default
    
    // Leaderboard categories
    val categories = listOf("XP", "Streak", "Tests")
    var selectedCategory by remember { mutableStateOf("XP") }
    
    // Leaderboard data (would come from repository)
    val leaderboardEntries = remember {
        listOf(
            LeaderboardEntry(1, "Emma Wilson", 8520, 21, 32),
            LeaderboardEntry(2, "James Smith", 7840, 15, 28),
            LeaderboardEntry(3, "Sophie Chen", 7210, 30, 25),
            LeaderboardEntry(4, "Alex Johnson", 6950, 15, 21),
            LeaderboardEntry(5, "Michael Brown", 6420, 12, 18),
            LeaderboardEntry(6, "Olivia Davis", 5980, 8, 15),
            LeaderboardEntry(7, "Noah Williams", 5740, 5, 14),
            LeaderboardEntry(8, "Isabella Jones", 5120, 9, 13),
            LeaderboardEntry(9, "Ethan Miller", 4870, 7, 12),
            LeaderboardEntry(10, "Ava Garcia", 4650, 3, 10)
        )
    }
    
    // Sort based on selected category
    val sortedEntries = when (selectedCategory) {
        "XP" -> leaderboardEntries.sortedByDescending { it.xpPoints }
        "Streak" -> leaderboardEntries.sortedByDescending { it.streakDays }
        "Tests" -> leaderboardEntries.sortedByDescending { it.testsCompleted }
        else -> leaderboardEntries.sortedByDescending { it.xpPoints }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Leaderboard",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Time period tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                timePeriods.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
            
            // Category selection chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            
            // Top 3 highlight
            TopThreeSection(
                entries = sortedEntries.take(3),
                category = selectedCategory,
                currentStudentId = currentStudentId
            )
            
            Divider()
            
            // Full leaderboard list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(sortedEntries) { index, entry ->
                    LeaderboardItem(
                        position = index + 1,
                        entry = entry,
                        category = selectedCategory,
                        isCurrentStudent = entry.id == currentStudentId
                    )
                }
                
                // Add some space at the bottom
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun TopThreeSection(
    entries: List<LeaderboardEntry>,
    category: String,
    currentStudentId: Int
) {
    // Ensure we have up to 3 entries to display
    val displayEntries = entries.take(3)
    val hasThreeEntries = displayEntries.size >= 3
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Second place
            if (hasThreeEntries) {
                TopPositionItem(
                    entry = displayEntries[1],
                    position = 2,
                    category = category,
                    isCurrentStudent = displayEntries[1].id == currentStudentId,
                    modifier = Modifier.weight(1f),
                    height = 160.dp
                )
            }
            
            // First place (center, taller)
            if (displayEntries.isNotEmpty()) {
                TopPositionItem(
                    entry = displayEntries[0],
                    position = 1,
                    category = category,
                    isCurrentStudent = displayEntries[0].id == currentStudentId,
                    modifier = Modifier.weight(1f),
                    height = 200.dp
                )
            }
            
            // Third place
            if (hasThreeEntries) {
                TopPositionItem(
                    entry = displayEntries[2],
                    position = 3,
                    category = category,
                    isCurrentStudent = displayEntries[2].id == currentStudentId,
                    modifier = Modifier.weight(1f),
                    height = 120.dp
                )
            }
        }
    }
}

@Composable
fun TopPositionItem(
    entry: LeaderboardEntry,
    position: Int,
    category: String,
    isCurrentStudent: Boolean,
    modifier: Modifier = Modifier,
    height: Dp
) {
    val containerColor = when (position) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = when (position) {
        1 -> MaterialTheme.colorScheme.onPrimary
        2 -> MaterialTheme.colorScheme.onSecondary
        3 -> MaterialTheme.colorScheme.onTertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Position with emoji crown for first place
        if (position == 1) {
            Text(
                text = "👑",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        // Profile card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentStudent) 
                    MaterialTheme.colorScheme.primaryContainer
                else 
                    containerColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Position number
                Text(
                    text = "#$position",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isCurrentStudent)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        textColor
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCurrentStudent)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.name.first().toString(),
                        fontSize = 20.sp,
                        color = if (isCurrentStudent)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Name
                Text(
                    text = entry.name.split(" ").first(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = if (isCurrentStudent)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        textColor
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Score based on category
                val score = when (category) {
                    "XP" -> "${entry.xpPoints} XP"
                    "Streak" -> "${entry.streakDays} days"
                    "Tests" -> "${entry.testsCompleted} tests"
                    else -> "${entry.xpPoints} XP"
                }
                
                // Icon based on category
                val icon = when (category) {
                    "XP" -> Icons.Default.EmojiEvents
                    "Streak" -> Icons.Default.LocalFireDepartment
                    "Tests" -> Icons.Default.Person
                    else -> Icons.Default.EmojiEvents
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isCurrentStudent)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = score,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isCurrentStudent)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            textColor
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(
    position: Int,
    entry: LeaderboardEntry,
    category: String,
    isCurrentStudent: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentStudent) 
                MaterialTheme.colorScheme.primaryContainer
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position number
            Text(
                text = position.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrentStudent)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.name.first().toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentStudent)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Name
            Text(
                text = entry.name,
                fontWeight = if (isCurrentStudent) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            
            // Score based on selected category
            val (score, icon) = when (category) {
                "XP" -> Pair("${entry.xpPoints} XP", Icons.Default.EmojiEvents)
                "Streak" -> Pair("${entry.streakDays} days", Icons.Default.LocalFireDepartment)
                "Tests" -> Pair("${entry.testsCompleted} tests", Icons.Default.Person)
                else -> Pair("${entry.xpPoints} XP", Icons.Default.EmojiEvents)
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isCurrentStudent)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = score,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// Data class for leaderboard entries
data class LeaderboardEntry(
    val id: Int,
    val name: String,
    val xpPoints: Int,
    val streakDays: Int,
    val testsCompleted: Int
)