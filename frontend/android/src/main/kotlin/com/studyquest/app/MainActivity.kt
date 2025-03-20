package com.studyquest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studyquest.app.ui.auth.AuthScreen
import com.studyquest.app.ui.createtest.CreateTestScreen
import com.studyquest.app.ui.dashboard.DashboardScreen
import com.studyquest.app.ui.progress.ProgressScreen
import com.studyquest.app.ui.studyplan.StudyPlanScreen
import com.studyquest.app.ui.tests.TestsScreen
import com.studyquest.app.ui.theme.StudyQuestTheme
import com.studyquest.app.ui.components.StudyQuestBottomNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyQuestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyQuestApp()
                }
            }
        }
    }
}

@Composable
fun StudyQuestApp() {
    val navController = rememberNavController()
    
    // Track whether user is logged in
    var isLoggedIn by remember { mutableStateOf(false) }
    
    // For handling bottom navigation
    var currentRoute by remember { mutableStateOf("dashboard") }
    
    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                StudyQuestBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "dashboard" else "auth",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("auth") {
                AuthScreen(
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate("dashboard") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
                currentRoute = "auth"
            }
            
            composable("dashboard") {
                DashboardScreen(
                    onNavigateToCreateTest = { navController.navigate("create-test") }
                )
                currentRoute = "dashboard"
            }
            
            composable("progress") {
                ProgressScreen()
                currentRoute = "progress"
            }
            
            composable("study-plan") {
                StudyPlanScreen()
                currentRoute = "study-plan"
            }
            
            composable("tests") {
                TestsScreen(
                    onNavigateToCreateTest = { navController.navigate("create-test") }
                )
                currentRoute = "tests"
            }
            
            composable("create-test") {
                CreateTestScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTestCreated = {
                        navController.navigate("tests") {
                            popUpTo("dashboard")
                        }
                    }
                )
            }
        }
    }
}
