package com.studyquest.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz

@Composable
fun StudyQuestBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Timeline, contentDescription = "Progress") },
            label = { Text("Progress") },
            selected = currentRoute == "progress",
            onClick = { onNavigate("progress") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Study Plan") },
            label = { Text("Study Plan") },
            selected = currentRoute == "study-plan",
            onClick = { onNavigate("study-plan") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Quiz, contentDescription = "Tests") },
            label = { Text("Tests") },
            selected = currentRoute == "tests",
            onClick = { onNavigate("tests") }
        )
    }
}
