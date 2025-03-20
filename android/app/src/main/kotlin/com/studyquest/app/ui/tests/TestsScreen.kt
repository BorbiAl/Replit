package com.studyquest.app.ui.tests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.studyquest.app.ui.dashboard.TestInfo
import com.studyquest.app.ui.dashboard.sampleTests
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past", "All")
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    
    val filteredTests = when (selectedTab) {
        0 -> sampleTests.filter { !it.completed }
        1 -> sampleTests.filter { it.completed }
        else -> sampleTests
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tests") },
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
                    icon = { Icon(Icons.Default.Star, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Tests") },
                    label = { Text("Tests") }
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
            
            // Filter and Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = false,
                    onClick = { /* Filter by subject */ },
                    label = { Text("Subject") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilterChip(
                    selected = false,
                    onClick = { /* Sort by date */ },
                    label = { Text("Date") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = "Sort",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "${filteredTests.size} tests",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            // Tests List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(filteredTests) { test ->
                    TestListItem(test = test, formatter = formatter)
                }
            }
        }
    }
}

@Composable
fun TestListItem(test: TestInfo, formatter: DateTimeFormatter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = test.subject,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                if (test.completed) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Text(
                            text = "COMPLETED",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "UPCOMING",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                
                Text(
                    text = test.date.format(formatter),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (!test.completed) {
                    Button(
                        onClick = { /* Start test */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Start")
                    }
                }
            }
        }
    }
}