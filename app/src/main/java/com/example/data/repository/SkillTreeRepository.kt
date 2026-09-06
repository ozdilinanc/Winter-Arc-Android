package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.ProjectEntity
import com.example.data.model.*
import com.example.data.seed.SkillTreeSeed
import com.example.data.state.SkillProgressManager
import com.example.data.state.SkillProgressSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SkillTreeRepository(
    private val database: AppDatabase,
    private val staticSkills: List<SkillNode> = SkillTreeSeed.getInitialSkills(),
    private val context: Context? = null
) {

    private val skillDao = database.skillDao()
    val progressManager = SkillProgressManager(skillDao, staticSkills)

    val skillsFlow: Flow<List<SkillNode>> = progressManager.skillsFlow
    val progressSummaryFlow: Flow<SkillProgressSummary> = progressManager.progressSummaryFlow
    val userXpFlow: Flow<UserXpProfile> = progressManager.userXpFlow
    val rewardNotificationFlow: Flow<RewardNotification> = progressManager.rewardNotificationFlow

    val projectsFlow: Flow<List<EngineeringProject>> = skillDao.getAllProjects().map { entities ->
        if (entities.isEmpty() && context != null) {
            val prefs = context.getSharedPreferences("winter_arc_projects_seed", Context.MODE_PRIVATE)
            val alreadySeeded = prefs.getBoolean("is_projects_seeded_v1", false)
            if (!alreadySeeded) {
                val initialEntities = SkillTreeSeed.initialProjects.map { project ->
                    ProjectEntity(
                        id = project.id,
                        title = project.title,
                        description = project.description,
                        category = project.category,
                        stageName = project.currentStage.name,
                        githubRepo = project.githubRepo,
                        mediumArticleUrl = project.mediumArticleUrl,
                        notes = project.notes,
                        tagsCsv = project.tags.joinToString(","),
                        updatedAt = System.currentTimeMillis()
                    )
                }
                skillDao.insertAllProjects(initialEntities)
                prefs.edit().putBoolean("is_projects_seeded_v1", true).apply()
                return@map SkillTreeSeed.initialProjects
            }
        }

        entities.map { entity ->
            val stage = try {
                ProjectWorkflowStage.valueOf(entity.stageName)
            } catch (e: Exception) {
                ProjectWorkflowStage.IDEA
            }
            EngineeringProject(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                category = entity.category,
                currentStage = stage,
                githubRepo = entity.githubRepo,
                mediumArticleUrl = entity.mediumArticleUrl,
                notes = entity.notes,
                tags = if (entity.tagsCsv.isNotBlank()) entity.tagsCsv.split(",") else emptyList()
            )
        }
    }

    suspend fun updateSkillStatus(skillId: String, newStatus: SkillStatus) {
        progressManager.setSkillStatus(skillId, newStatus)
    }

    suspend fun cycleSkillStatus(skillId: String): SkillStatus {
        return progressManager.cycleSkillStatus(skillId)
    }

    suspend fun updateSkillNotes(skillId: String, notes: String) {
        progressManager.saveSkillNotes(skillId, notes)
    }

    suspend fun addCustomResource(skillId: String, resourceUrl: String) {
        progressManager.addCustomResource(skillId, resourceUrl)
    }

    suspend fun addCustomProjectToSkill(skillId: String, projectName: String) {
        progressManager.addCustomProject(skillId, projectName)
    }

    suspend fun saveProject(project: EngineeringProject) = withContext(Dispatchers.IO) {
        val entity = ProjectEntity(
            id = project.id,
            title = project.title,
            description = project.description,
            category = project.category,
            stageName = project.currentStage.name,
            githubRepo = project.githubRepo,
            mediumArticleUrl = project.mediumArticleUrl,
            notes = project.notes,
            tagsCsv = project.tags.joinToString(","),
            updatedAt = System.currentTimeMillis()
        )
        skillDao.upsertProject(entity)
    }

    suspend fun advanceProjectStage(projectId: String, nextStage: ProjectWorkflowStage) = withContext(Dispatchers.IO) {
        val entity = ProjectEntity(
            id = projectId,
            title = "",
            description = "",
            category = "",
            stageName = nextStage.name,
            updatedAt = System.currentTimeMillis()
        )
        // Check if existing
        val all = skillDao.getAllProjects()
        // If not in DB yet, pull from seed
        val seed = SkillTreeSeed.initialProjects.find { it.id == projectId }
        if (seed != null) {
            saveProject(seed.copy(currentStage = nextStage))
        }
    }

    suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
        skillDao.deleteProjectById(projectId)
    }
}
