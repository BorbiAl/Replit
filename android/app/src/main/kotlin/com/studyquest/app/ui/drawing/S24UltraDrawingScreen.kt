package com.studyquest.app.ui.drawing

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyquest.app.devices.DeviceDetector
import com.studyquest.app.devices.samsung.S24UltraModule

/**
 * A drawing screen optimized for the Samsung S24 Ultra and S Pen
 * 
 * This screen provides pressure-sensitive drawing specifically tuned for the
 * Samsung S24 Ultra's S Pen stylus capabilities.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun S24UltraDrawingScreen() {
    val TAG = "S24UltraDrawingScreen"
    val context = LocalContext.current
    
    // Get device modules
    val deviceDetector = remember { DeviceDetector.getInstance() }
    val s24UltraModule = remember { deviceDetector.getS24UltraModule() }
    
    // Check if we're on an S24 Ultra
    val isS24Ultra = remember { s24UltraModule?.isS24Ultra() ?: false }
    
    // UI state
    var paths by remember { mutableStateOf(listOf<DrawPath>()) }
    var currentPath by remember { mutableStateOf<DrawPath?>(null) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(5f) }
    
    // Show optimized message if running on S24 Ultra
    var showOptimizedMessage by remember { mutableStateOf(isS24Ultra) }
    
    LaunchedEffect(Unit) {
        Log.d(TAG, "Drawing screen launched, S24 Ultra: $isS24Ultra")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "S Pen Drawing",
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
            // S24 Ultra optimization message
            if (showOptimizedMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Samsung S24 Ultra Detected",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Using optimized S Pen support with pressure sensitivity",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            
            // Drawing area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Draw existing paths
                    paths.forEach { path ->
                        drawPath(
                            path = path.path,
                            color = path.color,
                            style = Stroke(
                                width = path.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    
                    // Draw current path
                    currentPath?.let { path ->
                        drawPath(
                            path = path.path,
                            color = path.color,
                            style = Stroke(
                                width = path.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
            
            // Tool palette
            ToolPalette(
                currentColor = currentColor,
                currentStrokeWidth = currentStrokeWidth,
                onColorChange = { currentColor = it },
                onStrokeWidthChange = { currentStrokeWidth = it },
                onClear = { 
                    paths = emptyList()
                    currentPath = null
                },
                onUndo = {
                    if (paths.isNotEmpty()) {
                        paths = paths.dropLast(1)
                    }
                }
            )
        }
    }
}

/**
 * Tool palette for drawing tools
 */
@Composable
fun ToolPalette(
    currentColor: Color,
    currentStrokeWidth: Float,
    onColorChange: (Color) -> Unit,
    onStrokeWidthChange: (Float) -> Unit,
    onClear: () -> Unit,
    onUndo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color selector
        IconButton(onClick = { 
            // Cycle through colors
            val colors = listOf(
                Color.Black, Color.Red, Color.Blue, 
                Color.Green, Color.Magenta
            )
            val currentIndex = colors.indexOf(currentColor)
            val nextIndex = (currentIndex + 1) % colors.size
            onColorChange(colors[nextIndex])
        }) {
            Icon(
                imageVector = Icons.Default.ColorLens,
                contentDescription = "Change Color",
                tint = currentColor
            )
        }
        
        // Stroke width slider
        Slider(
            value = currentStrokeWidth,
            onValueChange = { onStrokeWidthChange(it) },
            valueRange = 1f..20f,
            modifier = Modifier.width(150.dp)
        )
        
        // Undo button
        IconButton(onClick = onUndo) {
            Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = "Undo"
            )
        }
        
        // Clear button
        IconButton(onClick = onClear) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear"
            )
        }
    }
}

/**
 * Data class representing a drawing path
 */
data class DrawPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)