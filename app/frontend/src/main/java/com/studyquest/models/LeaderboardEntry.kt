package com.studyquest.models

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val name: String? = null,
    val score: Int,
    val isCurrentUser: Boolean = false
)