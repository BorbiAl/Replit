package com.studyquest.utils

import android.content.Context
import android.content.SharedPreferences
import com.studyquest.models.User
import org.json.JSONObject

class PreferenceManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "StudyQuestPrefs"
        private const val KEY_USER = "user"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_LAST_ACTIVE = "last_active"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    fun saveUser(user: User) {
        val jsonObject = JSONObject().apply {
            put("id", user.id)
            put("username", user.username)
            user.email?.let { put("email", it) }
            user.name?.let { put("name", it) }
            put("streakCount", user.streakCount)
            put("points", user.points)
            put("level", user.level)
        }
        
        prefs.edit()
            .putString(KEY_USER, jsonObject.toString())
            .apply()
    }
    
    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        
        return try {
            val jsonObject = JSONObject(userJson)
            
            User(
                id = jsonObject.getInt("id"),
                username = jsonObject.getString("username"),
                email = if (jsonObject.has("email")) jsonObject.getString("email") else null,
                name = if (jsonObject.has("name")) jsonObject.getString("name") else null,
                streakCount = jsonObject.getInt("streakCount"),
                points = jsonObject.getInt("points"),
                level = jsonObject.getInt("level")
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun saveAuthToken(token: String) {
        prefs.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun clearUserData() {
        prefs.edit()
            .remove(KEY_USER)
            .remove(KEY_AUTH_TOKEN)
            .apply()
    }
    
    fun updateLastActive() {
        prefs.edit()
            .putLong(KEY_LAST_ACTIVE, System.currentTimeMillis())
            .apply()
    }
    
    fun getLastActive(): Long {
        return prefs.getLong(KEY_LAST_ACTIVE, 0)
    }
    
    fun hasActiveStreak(): Boolean {
        val lastActive = getLastActive()
        if (lastActive == 0L) return false
        
        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000
        
        return (currentTime - lastActive) <= oneDayInMillis
    }
}