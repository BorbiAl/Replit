package com.studyquest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studyquest.app.ui.auth.AuthScreen
import com.studyquest.app.ui.dashboard.DashboardScreen
import com.studyquest.app.ui.theme.StudyQuestTheme
import com.studyquest.app.ui.tests.TestsScreen
import com.studyquest.app.ui.duolingo.DuolingoStyleDashboard

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
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "auth") {
                        composable("auth") {
                            AuthScreen(navController = navController)
                        }
                        composable("dashboard") {
                            DuolingoStyleDashboard(navController = navController)
                        }
                        composable("tests") {
                            TestsScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}