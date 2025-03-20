package com.studyquest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.studyquest.R
import com.studyquest.adapters.LeaderboardAdapter
import com.studyquest.models.LeaderboardEntry

class RankingFragment : Fragment() {
    
    private lateinit var userRankTextView: TextView
    private lateinit var userPointsTextView: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    
    private var currentTab = "Points" // Default tab
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)
        
        // Initialize views
        userRankTextView = view.findViewById(R.id.user_rank_text_view)
        userPointsTextView = view.findViewById(R.id.user_points_text_view)
        tabLayout = view.findViewById(R.id.tab_layout)
        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_recycler_view)
        
        // Set up RecyclerView
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(context)
        leaderboardAdapter = LeaderboardAdapter()
        leaderboardRecyclerView.adapter = leaderboardAdapter
        
        // Set up tabs
        setupTabs()
        
        // Load leaderboard data
        loadLeaderboardData()
        
        return view
    }
    
    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Points"))
        tabLayout.addTab(tabLayout.newTab().setText("Streaks"))
        tabLayout.addTab(tabLayout.newTab().setText("Tests"))
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.text.toString()
                loadLeaderboardData()
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    
    private fun loadLeaderboardData() {
        // In a real app, this would load leaderboard data from the API based on the selected tab
        val leaderboardEntries = when (currentTab) {
            "Points" -> getPointsLeaderboard()
            "Streaks" -> getStreaksLeaderboard()
            "Tests" -> getTestsLeaderboard()
            else -> getPointsLeaderboard()
        }
        
        leaderboardAdapter.setEntries(leaderboardEntries)
        
        // Update the user's rank and score
        val userEntry = leaderboardEntries.find { it.isCurrentUser } ?: leaderboardEntries.first()
        userRankTextView.text = "Your Rank: #${userEntry.rank}"
        
        val scoreLabel = when (currentTab) {
            "Points" -> "Points"
            "Streaks" -> "Day Streak"
            "Tests" -> "Tests Completed"
            else -> "Points"
        }
        
        userPointsTextView.text = "${userEntry.score} $scoreLabel"
    }
    
    private fun getPointsLeaderboard(): List<LeaderboardEntry> {
        return listOf(
            LeaderboardEntry(rank = 1, username = "mathgenius", score = 1250, isCurrentUser = false),
            LeaderboardEntry(rank = 2, username = "sciencewhiz", score = 980, isCurrentUser = false),
            LeaderboardEntry(rank = 3, username = "student123", score = 750, isCurrentUser = true),
            LeaderboardEntry(rank = 4, username = "historybuff", score = 720, isCurrentUser = false),
            LeaderboardEntry(rank = 5, username = "litlover", score = 680, isCurrentUser = false),
            LeaderboardEntry(rank = 6, username = "chemistrypro", score = 650, isCurrentUser = false),
            LeaderboardEntry(rank = 7, username = "biostudent", score = 590, isCurrentUser = false),
            LeaderboardEntry(rank = 8, username = "physicsmaster", score = 570, isCurrentUser = false),
            LeaderboardEntry(rank = 9, username = "englishace", score = 520, isCurrentUser = false),
            LeaderboardEntry(rank = 10, username = "geographyexpert", score = 490, isCurrentUser = false)
        )
    }
    
    private fun getStreaksLeaderboard(): List<LeaderboardEntry> {
        return listOf(
            LeaderboardEntry(rank = 1, username = "streakmaster", score = 30, isCurrentUser = false),
            LeaderboardEntry(rank = 2, username = "dailylearner", score = 25, isCurrentUser = false),
            LeaderboardEntry(rank = 3, username = "consistentone", score = 20, isCurrentUser = false),
            LeaderboardEntry(rank = 4, username = "nevermissaday", score = 15, isCurrentUser = false),
            LeaderboardEntry(rank = 5, username = "student123", score = 5, isCurrentUser = true),
            LeaderboardEntry(rank = 6, username = "regularstudier", score = 4, isCurrentUser = false),
            LeaderboardEntry(rank = 7, username = "newstudent", score = 3, isCurrentUser = false),
            LeaderboardEntry(rank = 8, username = "juststarted", score = 2, isCurrentUser = false),
            LeaderboardEntry(rank = 9, username = "firstday", score = 1, isCurrentUser = false),
            LeaderboardEntry(rank = 10, username = "beginning", score = 1, isCurrentUser = false)
        )
    }
    
    private fun getTestsLeaderboard(): List<LeaderboardEntry> {
        return listOf(
            LeaderboardEntry(rank = 1, username = "testmaster", score = 45, isCurrentUser = false),
            LeaderboardEntry(rank = 2, username = "quizpro", score = 38, isCurrentUser = false),
            LeaderboardEntry(rank = 3, username = "examwhiz", score = 32, isCurrentUser = false),
            LeaderboardEntry(rank = 4, username = "testprep", score = 28, isCurrentUser = false),
            LeaderboardEntry(rank = 5, username = "quiztaker", score = 25, isCurrentUser = false),
            LeaderboardEntry(rank = 6, username = "examready", score = 22, isCurrentUser = false),
            LeaderboardEntry(rank = 7, username = "student123", score = 18, isCurrentUser = true),
            LeaderboardEntry(rank = 8, username = "testprepper", score = 15, isCurrentUser = false),
            LeaderboardEntry(rank = 9, username = "studypro", score = 12, isCurrentUser = false),
            LeaderboardEntry(rank = 10, username = "novicestudent", score = 10, isCurrentUser = false)
        )
    }
}