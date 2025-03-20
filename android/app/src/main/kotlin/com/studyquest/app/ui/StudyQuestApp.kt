package com.studyquest.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.studyquest.app.devices.DeviceDetector
import com.studyquest.app.ui.dashboard.DashboardScreen
import com.studyquest.app.ui.drawing.S24UltraDrawingScreen
import com.studyquest.app.ui.leaderboard.LeaderboardScreen
import com.studyquest.app.ui.profile.ProfileScreen
import com.studyquest.app.ui.tests.TestsScreen

/**
 * Main composable for the StudyQuest application
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyQuestApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Device detector for S24 Ultra
    val deviceDetector = remember { DeviceDetector.getInstance() }
    val isS24Ultra = remember { deviceDetector.getS24UltraModule()?.isS24Ultra() ?: false }
    
    // Check for S24 Ultra-specific features
    val showS24UltraFeatures = remember { isS24Ultra }
    
    // Bottom navigation items
    val bottomNavItems = remember {
        listOf(
            BottomNavItem(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                route = "dashboard"
            ),
            BottomNavItem(
                title = "Tests",
                selectedIcon = Icons.Filled.Quiz,
                unselectedIcon = Icons.Outlined.Quiz,
                route = "tests"
            ),
            BottomNavItem(
                title = "Leaderboard",
                selectedIcon = Icons.Filled.Leaderboard,
                unselectedIcon = Icons.Outlined.Leaderboard,
                route = "leaderboard"
            ),
            BottomNavItem(
                title = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                route = "profile"
            )
        )
    }
    
    // If S24 Ultra, add S Pen drawing feature
    val allNavItems = if (showS24UltraFeatures) {
        bottomNavItems + BottomNavItem(
            title = "S Pen",
            selectedIcon = Icons.Filled.Home, // Should be S Pen icon
            unselectedIcon = Icons.Outlined.Home, // Should be S Pen icon
            route = "s24-drawing"
        )
    } else {
        bottomNavItems
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                allNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { 
                            Text(
                                item.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Main navigation
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable("dashboard") {
                DashboardScreen()
            }
            composable("tests") {
                TestsScreen()
            }
            composable("leaderboard") {
                LeaderboardScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
            
            // S24 Ultra specific routes
            if (showS24UltraFeatures) {
                composable("s24-drawing") {
                    S24UltraDrawingScreen()
                }
            }
        }
    }
}

/**
 * Data class for bottom navigation items
 */
data class BottomNavItem(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)