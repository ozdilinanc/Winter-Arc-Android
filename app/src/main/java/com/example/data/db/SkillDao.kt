package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    // ---- Skill Progress Tracking ----
    @Query("SELECT * FROM skill_progress")
    fun getAllSkillProgress(): Flow<List<SkillProgressEntity>>

    @Query("SELECT * FROM skill_progress")
    suspend fun getAllSkillProgressList(): List<SkillProgressEntity>

    @Query("SELECT * FROM skill_progress WHERE skillId = :skillId LIMIT 1")

    suspend fun getSkillProgressById(skillId: String): SkillProgressEntity?

    @Query("SELECT * FROM skill_progress WHERE skillId = :skillId LIMIT 1")
    fun getSkillProgressFlow(skillId: String): Flow<SkillProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSkillProgress(progress: SkillProgressEntity)

    @Query("UPDATE skill_progress SET status = :status, updatedAt = :updatedAt WHERE skillId = :skillId")
    suspend fun updateSkillStatus(skillId: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM skill_progress WHERE skillId = :skillId")
    suspend fun deleteSkillProgress(skillId: String)

    @Query("DELETE FROM skill_progress")
    suspend fun clearAllSkillProgress()

    // ---- Backward Compatibility Overrides ----
    @Query("SELECT * FROM skill_user_overrides")
    fun getAllSkillOverrides(): Flow<List<SkillUserOverrideEntity>>

    @Query("SELECT * FROM skill_user_overrides WHERE skillId = :skillId LIMIT 1")
    suspend fun getSkillOverrideById(skillId: String): SkillUserOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSkillOverride(override: SkillUserOverrideEntity)

    // ---- Engineering Projects ----
    @Query("SELECT * FROM engineering_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjects(projects: List<ProjectEntity>)

    @Query("DELETE FROM engineering_projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    @Query("DELETE FROM engineering_projects")
    suspend fun clearAllProjects()

    // ---- User XP & Level Persistence ----
    @Query("SELECT * FROM user_xp_profile WHERE id = 'primary_user' LIMIT 1")
    fun getUserXpFlow(): Flow<UserXpEntity?>

    @Query("SELECT * FROM user_xp_profile WHERE id = 'primary_user' LIMIT 1")
    suspend fun getUserXp(): UserXpEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserXp(userXp: UserXpEntity)
}

