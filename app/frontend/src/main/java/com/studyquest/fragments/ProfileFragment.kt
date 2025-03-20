package com.studyquest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studyquest.R
import com.studyquest.adapters.AchievementAdapter
import com.studyquest.models.Achievement
import com.studyquest.models.User

class ProfileFragment : Fragment() {
    
    private lateinit var usernameTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var pointsTextView: TextView
    private lateinit var achievementsRecyclerView: RecyclerView
    private lateinit var achievementAdapter: AchievementAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        // Initialize views
        usernameTextView = view.findViewById(R.id.username_text_view)
        levelTextView = view.findViewById(R.id.level_text_view)
        pointsTextView = view.findViewById(R.id.points_text_view)
        achievementsRecyclerView = view.findViewById(R.id.achievements_recycler_view)
        
        // Set up RecyclerView
        achievementsRecyclerView.layoutManager = GridLayoutManager(context, 3)
        achievementAdapter = AchievementAdapter()
        achievementsRecyclerView.adapter = achievementAdapter
        
        loadUserData()
        loadAchievements()
        
        return view
    }
    
    private fun loadUserData() {
        // In a real app, this would load user data from the API or local database
        val user = User(
            username = "student123",
            streakCount = 5,
            level = 3,
            points = 750
        )
        
        usernameTextView.text = user.username
        levelTextView.text = "Level ${user.level}"
        pointsTextView.text = "${user.points} points"
    }
    
    private fun loadAchievements() {
        // In a real app, this would load achievements from the API or local database
        val achievements = listOf(
            Achievement(
                id = 1,
                name = "First Test",
                description = "Created your first test",
                emoji = "🎯",
                isEarned = true
            ),
            Achievement(
                id = 2,
                name = "Streak Master",
                description = "Maintained a 3-day streak",
                emoji = "🔥",
                isEarned = true
            ),
            Achievement(
                id = 3,
                name = "Perfect Score",
                description = "Got 100% on a test",
                emoji = "💯",
                isEarned = false
            ),
            Achievement(
                id = 4,
                name = "Study Guru",
                description = "Completed 10 tests",
                emoji = "🧠",
                isEarned = false
            ),
            Achievement(
                id = 5,
                name = "Math Whiz",
                description = "Aced 5 math tests",
                emoji = "🔢",
                isEarned = false
            ),
            Achievement(
                id = 6,
                name = "Science Explorer",
                description = "Completed tests in all science subjects",
                emoji = "🔬",
                isEarned = false
            )
        )
        
        achievementAdapter.setAchievements(achievements)
    }
}