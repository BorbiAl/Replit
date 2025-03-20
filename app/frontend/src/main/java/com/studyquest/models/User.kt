package com.studyquest.models

data class User(
    val id: Int = 0,
    val username: String,
    val email: String? = null,
    val name: String? = null,
    val streakCount: Int = 0,
    val points: Int = 0,
    val level: Int = 1
)