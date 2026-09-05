package com.example.ui.components.school

data class SchoolCourse(
    val id: String,
    val code: String,
    val name: String,
    val credits: Int,
    val semester: String,
    val grade: String = "Devam Ediyor",
    val isCompleted: Boolean = false
)

data class TrackedBook(
    val id: String,
    val title: String,
    val shortTitle: String,
    val authorOrDomain: String,
    val whyItMatters: String,
    val totalPages: Int,
    val currentPage: Int,
    val keyTopics: List<String>,
    val coverEmoji: String
) {
    val progressPercent: Float
        get() = if (totalPages > 0) (currentPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f) else 0f
}

data class ProjectMilestone(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)
