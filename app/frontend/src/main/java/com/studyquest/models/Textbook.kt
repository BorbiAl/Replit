package com.studyquest.models

data class Textbook(
    val id: Int,
    val title: String,
    val author: String? = null,
    val subjectId: Int,
    val gradeLevel: Int? = null,
    val totalPages: Int? = null
)