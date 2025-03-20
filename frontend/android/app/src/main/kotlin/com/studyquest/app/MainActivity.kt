package com.studyquest.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.studyquest.app.devices.DeviceDetector
import com.studyquest.app.devices.samsung.S24UltraModule
import com.studyquest.app.ui.StudyQuestApp
import com.studyquest.app.ui.theme.StudyQuestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    
    // Device optimizations
    private var deviceDetector: DeviceDetector? = null
    private var s24UltraModule: S24UltraModule? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize device detection and optimizations
        initializeDeviceOptimizations()
        
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
        
        // Apply S24 Ultra specific optimizations if available
        s24UltraModule?.optimizeActivity(this)
        
        Log.i(TAG, "StudyQuest application started")
    }
    
    /**
     * Initialize device-specific optimizations
     */
    private fun initializeDeviceOptimizations() {
        try {
            // Initialize device detector
            deviceDetector = DeviceDetector.getInstance()
            deviceDetector?.initialize(applicationContext)
            
            // Get S24 Ultra module if available
            s24UltraModule = deviceDetector?.getS24UltraModule()
            
            if (s24UltraModule != null) {
                Log.i(TAG, "Samsung S24 Ultra optimizations applied")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing device optimizations", e)
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // Re-apply S24 Ultra optimizations in case they were modified
        s24UltraModule?.optimizeActivity(this)
    }
}