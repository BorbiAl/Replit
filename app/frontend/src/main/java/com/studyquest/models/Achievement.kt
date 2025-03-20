package com.studyquest.models

import java.util.Date

data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    val emoji: String,
    val requiredPoints: Int = 0,
    val isEarned: Boolean = false,
    val earnedAt: Date? = null
)