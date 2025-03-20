package com.studyquest.app.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Weekly", "Monthly", "All Time")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("dashboard") },
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
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Leaderboard") },
                    label = { Text("Rank") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Your Stats
            YourStatsCard(
                rank = 15,
                xp = 1250,
                weeklyXp = 250
            )
            
            // Leaderboard
            Text(
                text = "${tabs[selectedTab]} Leaderboard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(sampleLeaderboard) { index, user ->
                    LeaderboardItem(
                        position = index + 1,
                        user = user,
                        isCurrentUser = index == 14 // Position 15 (for our mock user)
                    )
                }
            }
        }
    }
}

@Composable
fun YourStatsCard(rank: Int, xp: Int, weeklyXp: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Your Rank",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Keep studying to improve your position!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "$xp",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                Text(
                    text = "+$weeklyXp this week",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

data class LeaderboardUser(
    val name: String,
    val xp: Int,
    val streak: Int,
    val level: Int
)

val sampleLeaderboard = listOf(
    LeaderboardUser("Emma S.", 4850, 32, 15),
    LeaderboardUser("Liam T.", 4720, 45, 14),
    LeaderboardUser("Olivia R.", 4615, 28, 14),
    LeaderboardUser("Noah W.", 4580, 22, 14),
    LeaderboardUser("Ava P.", 4470, 38, 13),
    LeaderboardUser("Ethan M.", 4350, 19, 13),
    LeaderboardUser("Sophia K.", 4220, 27, 13),
    LeaderboardUser("Lucas G.", 4100, 31, 12),
    LeaderboardUser("Isabella F.", 3980, 25, 12),
    LeaderboardUser("Mason D.", 3870, 18, 12),
    LeaderboardUser("Mia B.", 3750, 29, 11),
    LeaderboardUser("James A.", 3630, 16, 11),
    LeaderboardUser("Charlotte Z.", 3520, 24, 11),
    LeaderboardUser("Benjamin Y.", 3410, 21, 10),
    LeaderboardUser("John D.", 1250, 7, 5),  // Our mock user
    LeaderboardUser("Amelia W.", 1150, 10, 4),
    LeaderboardUser("Elijah V.", 980, 8, 4),
    LeaderboardUser("Harper T.", 820, 5, 3),
    LeaderboardUser("Matthew S.", 650, 3, 2),
    LeaderboardUser("Abigail R.", 480, 2, 2)
)

@Composable
fun LeaderboardItem(position: Int, user: LeaderboardUser, isCurrentUser: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentUser) 4.dp else 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position
            Box(
                modifier = when (position) {
                    1 -> Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700)) // Gold
                    2 -> Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC0C0C0)) // Silver
                    3 -> Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCD7F32)) // Bronze
                    else -> Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$position",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (position <= 3) Color.White else Color.DarkGray
                )
            }
            
            // User info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = user.name + (if (isCurrentUser) " (You)" else ""),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color.Unspecified
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${user.streak} day streak",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Level",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Level ${user.level}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            // XP
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${user.xp}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                Text(
                    text = "total XP",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}