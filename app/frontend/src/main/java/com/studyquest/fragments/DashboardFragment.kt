package com.studyquest.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.studyquest.R
import com.studyquest.activities.CreateTestActivity
import com.studyquest.models.User
import com.studyquest.utils.PreferenceManager

class DashboardFragment : Fragment() {
    
    private lateinit var welcomeTextView: TextView
    private lateinit var streakCountTextView: TextView
    private lateinit var streakDaysTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var createTestButton: FloatingActionButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        
        // Initialize views
        welcomeTextView = view.findViewById(R.id.welcome_text_view)
        streakCountTextView = view.findViewById(R.id.streak_count_text_view)
        streakDaysTextView = view.findViewById(R.id.streak_days_text_view)
        levelTextView = view.findViewById(R.id.level_text_view)
        createTestButton = view.findViewById(R.id.create_test_button)
        
        // Set up the Create Test button
        createTestButton.setOnClickListener {
            val intent = Intent(activity, CreateTestActivity::class.java)
            startActivity(intent)
        }
        
        loadUserData()
        
        return view
    }
    
    private fun loadUserData() {
        // In a real app, this would load user data from the API or local database
        // For now, we'll use dummy data
        val user = User(
            username = "student123",
            streakCount = 5,
            level = 3,
            points = 750
        )
        
        welcomeTextView.text = "Welcome back, ${user.username}!"
        streakCountTextView.text = "${user.streakCount}"
        streakDaysTextView.text = "day streak 🔥"
        levelTextView.text = "Level ${user.level}"
    }
    
    override fun onResume() {
        super.onResume()
        loadUserData() // Refresh data when returning to the fragment
    }
}