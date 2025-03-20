package com.studyquest.models

import java.util.Date

data class Test(
    val id: Int,
    val title: String,
    val subject: Subject,
    val textbook: String,
    val pagesFrom: Int,
    val pagesTo: Int,
    val questionCount: Int = 10,
    val examDate: Date? = null,
    val isCompleted: Boolean = false,
    val score: Int? = null,
    val createdAt: Date = Date()
)