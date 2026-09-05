package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks the user's learning progress and mastery state for an individual skill node.
 * Persisted locally via Room.
 */
@Entity(tableName = "skill_progress")
data class SkillProgressEntity(
    @PrimaryKey val skillId: String,
    val status: String, // "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "STRONG"
    val personalNotes: String = "",
    val customResources: String = "",
    val customProjects: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "skill_user_overrides")
data class SkillUserOverrideEntity(
    @PrimaryKey val skillId: String,
    val status: String,
    val personalNotes: String,
    val customProjects: String = "",
    val customResources: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "engineering_projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val stageName: String,
    val githubRepo: String = "",
    val mediumArticleUrl: String = "",
    val notes: String = "",
    val tagsCsv: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Persists user XP, level, and unlocked badge records in Room database.
 */
@Entity(tableName = "user_xp_profile")
data class UserXpEntity(
    @PrimaryKey val id: String = "primary_user",
    val totalXp: Int = 0,
    val level: Int = 1,
    val completedSkillIdsCsv: String = "",
    val unlockedBadgeIdsCsv: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

