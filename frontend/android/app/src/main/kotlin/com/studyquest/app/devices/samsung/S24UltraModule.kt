package com.studyquest.app.devices.samsung

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.Window

/**
 * Main module for Samsung S24 Ultra specific optimizations and features.
 * 
 * This module serves as the central access point for all S24 Ultra specific functionality,
 * providing an easy way to optimize the app experience for this premium device.
 */
class S24UltraModule(private val context: Context) {
    private val TAG = "S24UltraModule"
    
    // Sub-modules for different aspects of S24 Ultra optimization
    private val displayManager = S24UltraDisplayManager(context)
    private val sPenHandler = S24UltraSPenHandler(context)
    
    // Tracking state
    private var isInitialized = false
    
    /**
     * Initialize the S24 Ultra module
     */
    fun initialize() {
        if (!S24UltraOptimizations.isS24Ultra()) {
            Log.i(TAG, "Not running on Samsung S24 Ultra, skipping specialized optimizations")
            return
        }
        
        Log.i(TAG, "Initializing Samsung S24 Ultra module")
        
        try {
            // Basic optimizations
            S24UltraOptimizations.applyOptimizations(context)
            
            // Initialize display manager
            displayManager.initialize()
            
            // Initialize S Pen support
            sPenHandler.initialize()
            
            isInitialized = true
            Log.i(TAG, "Samsung S24 Ultra module initialization complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing S24 Ultra module", e)
        }
    }
    
    /**
     * Apply optimizations to an activity
     */
    fun optimizeActivity(activity: Activity) {
        if (!isInitialized || !S24UltraOptimizations.isS24Ultra()) return
        
        try {
            // Apply display optimizations
            displayManager.applyOptimalSettings(activity.window)
            
            Log.d(TAG, "Applied S24 Ultra optimizations to ${activity.javaClass.simpleName}")
        } catch (e: Exception) {
            Log.e(TAG, "Error applying S24 Ultra optimizations to activity", e)
        }
    }
    
    /**
     * Enable S Pen features for a specific view
     */
    fun enableSPenForView(view: View) {
        if (!isInitialized || !S24UltraOptimizations.isS24Ultra()) return
        
        try {
            sPenHandler.attachToView(view)
            Log.d(TAG, "Enabled S Pen support for ${view.javaClass.simpleName}")
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling S Pen support for view", e)
        }
    }
    
    /**
     * Switch to high performance mode
     * - Sets display to maximum refresh rate
     * - Optimizes processing priority
     */
    fun enableHighPerformanceMode() {
        if (!isInitialized || !S24UltraOptimizations.isS24Ultra()) return
        
        displayManager.enableHighPerformanceMode()
        Log.i(TAG, "Enabled high performance mode for S24 Ultra")
    }
    
    /**
     * Switch to battery saving mode
     * - Reduces refresh rate
     * - Optimizes for battery life
     */
    fun enableBatterySavingMode() {
        if (!isInitialized || !S24UltraOptimizations.isS24Ultra()) return
        
        displayManager.enableBatterySavingMode()
        Log.i(TAG, "Enabled battery saving mode for S24 Ultra")
    }
    
    /**
     * Switch to adaptive mode that balances performance and battery
     */
    fun enableAdaptiveMode() {
        if (!isInitialized || !S24UltraOptimizations.isS24Ultra()) return
        
        displayManager.enableAdaptiveMode()
        Log.i(TAG, "Enabled adaptive mode for S24 Ultra")
    }
    
    /**
     * Check if the device is a Samsung S24 Ultra
     */
    fun isS24Ultra(): Boolean {
        return S24UltraOptimizations.isS24Ultra()
    }
    
    /**
     * Check if the S Pen is currently being used
     */
    fun isUsingPen(): Boolean {
        if (!isInitialized || !S24UltraOptimizations.isS24Ultra()) return false
        return sPenHandler.isPenActive()
    }
    
    companion object {
        // Singleton instance
        @Volatile
        private var instance: S24UltraModule? = null
        
        /**
         * Get the S24 Ultra module instance
         */
        fun getInstance(context: Context): S24UltraModule {
            return instance ?: synchronized(this) {
                instance ?: S24UltraModule(context.applicationContext).also { instance = it }
            }
        }
    }
}