package com.studyquest.utils

import android.content.Context
import android.util.Log
import com.studyquest.models.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class ApiClient(private val context: Context) {
    
    companion object {
        private const val BASE_URL = "http://localhost:5000/api"
        private const val TAG = "ApiClient"
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    
    fun login(username: String, password: String, callback: (User?, String?) -> Unit) {
        thread {
            try {
                val url = URL("$BASE_URL/login")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                val jsonInputString = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }.toString()
                
                connection.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                }
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = readResponse(connection)
                    val jsonObject = JSONObject(response)
                    
                    val user = User(
                        id = jsonObject.getInt("id"),
                        username = jsonObject.getString("username"),
                        email = if (jsonObject.has("email")) jsonObject.getString("email") else null,
                        name = if (jsonObject.has("name")) jsonObject.getString("name") else null,
                        streakCount = jsonObject.getInt("streakCount"),
                        points = jsonObject.getInt("points"),
                        level = jsonObject.getInt("level")
                    )
                    
                    callback(user, null)
                } else {
                    val error = "Login failed: ${connection.responseMessage}"
                    Log.e(TAG, error)
                    callback(null, error)
                }
                
                connection.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                callback(null, e.message ?: "Unknown error")
            }
        }
    }
    
    fun register(username: String, email: String, password: String, callback: (User?, String?) -> Unit) {
        thread {
            try {
                val url = URL("$BASE_URL/register")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                val jsonInputString = JSONObject().apply {
                    put("username", username)
                    put("email", email)
                    put("password", password)
                }.toString()
                
                connection.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                }
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    val response = readResponse(connection)
                    val jsonObject = JSONObject(response)
                    
                    val user = User(
                        id = jsonObject.getInt("id"),
                        username = jsonObject.getString("username"),
                        email = if (jsonObject.has("email")) jsonObject.getString("email") else null,
                        name = if (jsonObject.has("name")) jsonObject.getString("name") else null,
                        streakCount = 0,
                        points = 0,
                        level = 1
                    )
                    
                    callback(user, null)
                } else {
                    val error = "Registration failed: ${connection.responseMessage}"
                    Log.e(TAG, error)
                    callback(null, error)
                }
                
                connection.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "Registration error", e)
                callback(null, e.message ?: "Unknown error")
            }
        }
    }
    
    fun createTest(test: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        thread {
            try {
                val url = URL("$BASE_URL/tests")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                val jsonObject = JSONObject()
                test.forEach { (key, value) -> jsonObject.put(key, value) }
                
                connection.outputStream.use { os ->
                    val input = jsonObject.toString().toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                }
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    callback(true, null)
                } else {
                    val error = "Failed to create test: ${connection.responseMessage}"
                    Log.e(TAG, error)
                    callback(false, error)
                }
                
                connection.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "Create test error", e)
                callback(false, e.message ?: "Unknown error")
            }
        }
    }
    
    private fun readResponse(connection: HttpURLConnection): String {
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        reader.close()
        return response.toString()
    }
}