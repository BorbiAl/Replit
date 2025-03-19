package com.studyquest.app.data

import java.time.LocalDate

// User related models
data class User(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val streak: Int,
    val xp: Int,
    val level: Int
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

// Educational content models
data class Grade(
    val id: Int,
    val name: String
)

data class Subject(
    val id: Int,
    val name: String,
    val icon: String,
    val color: String
)

data class Textbook(
    val id: Int,
    val name: String,
    val subjectId: Int,
    val gradeId: Int,
    val totalPages: Int
)

// Test related models
data class Test(
    val id: Int,
    val userId: Int,
    val title: String,
    val gradeId: Int,
    val subjectId: Int,
    val textbookId: Int,
    val pagesFrom: Int,
    val pagesTo: Int,
    val questionCount: Int,
    val examDate: LocalDate?,
    val scheduledReminders: Boolean,
    val createdAt: String,
    val completed: Boolean,
    val score: Int?
)

data class CreateTestRequest(
    val gradeId: Int,
    val subjectId: Int,
    val textbookId: Int,
    val title: String,
    val pagesFrom: Int,
    val pagesTo: Int,
    val questionCount: Int,
    val examDate: LocalDate?,
    val scheduledReminders: Boolean
)

// Stats and progress models
data class Progress(
    val completedTests: Int,
    val totalTests: Int,
    val averageScore: Float,
    val studyTime: Int,
    val studyStreak: Int
)
