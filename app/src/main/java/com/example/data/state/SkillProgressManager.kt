package com.example.data.state

import com.example.data.db.SkillDao
import com.example.data.db.SkillProgressEntity
import com.example.data.db.UserXpEntity
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * Summary metrics of the user's progress across all skill nodes.
 */
data class SkillProgressSummary(
    val totalSkills: Int = 0,
    val notStartedCount: Int = 0,
    val inProgressCount: Int = 0,
    val practicedCount: Int = 0,
    val completedCount: Int = 0,
    val masteredCount: Int = 0,
    val overallMasteryPercentage: Int = 0
)

/**
 * State management layer for tracking, updating, and querying user progress for skill nodes.
 * Interfaces directly with the local Room Database to ensure offline-first persistence.
 */
class SkillProgressManager(
    private val skillDao: SkillDao,
    private val initialSkills: List<SkillNode>
) {

    private val _rewardNotificationFlow = MutableSharedFlow<RewardNotification>(extraBufferCapacity = 5)
    val rewardNotificationFlow: SharedFlow<RewardNotification> = _rewardNotificationFlow.asSharedFlow()

    /**
     * Reactive stream of all skills merged with real-time user progress stored in Room.
     */
    val skillsFlow: Flow<List<SkillNode>> = skillDao.getAllSkillProgress().map { progressList ->
        val progressMap = progressList.associateBy { it.skillId }
        initialSkills.map { baseSkill ->
            val progress = progressMap[baseSkill.id]
            if (progress != null) {
                val parsedStatus = SkillStatus.fromString(progress.status)
                baseSkill.copy(
                    status = parsedStatus,
                    personalNotes = progress.personalNotes,
                    connectedProjects = if (progress.customProjects.isNotBlank()) {
                        progress.customProjects.split(",")
                    } else {
                        baseSkill.connectedProjects
                    },
                    recommendedResources = if (progress.customResources.isNotBlank()) {
                        baseSkill.recommendedResources + progress.customResources.split(",").filter { it.isNotBlank() }
                    } else {
                        baseSkill.recommendedResources
                    }
                )
            } else {
                baseSkill
            }
        }
    }

    /**
     * Reactive summary of progress counts and completion percentage.
     */
    val progressSummaryFlow: Flow<SkillProgressSummary> = skillsFlow.map { skills ->
        val total = skills.size
        val notStarted = skills.count { it.status == SkillStatus.NOT_STARTED }
        val inProgress = skills.count { it.status == SkillStatus.IN_PROGRESS }
        val practiced = skills.count { it.status == SkillStatus.PRACTICED }
        val completed = skills.count { it.status == SkillStatus.COMPLETED }
        val mastered = skills.count { it.status == SkillStatus.STRONG }
        val successful = completed + mastered
        val percent = if (total > 0) ((successful.toFloat() / total) * 100).toInt() else 0

        SkillProgressSummary(
            totalSkills = total,
            notStartedCount = notStarted,
            inProgressCount = inProgress,
            practicedCount = practiced,
            completedCount = completed,
            masteredCount = mastered,
            overallMasteryPercentage = percent
        )
    }

    /**
     * Reactive flow of the user's XP profile, levels, and unlocked achievement badges.
     */
    val userXpFlow: Flow<UserXpProfile> = combine(
        skillDao.getUserXpFlow(),
        skillsFlow
    ) { xpEntity, skills ->
        val entity = xpEntity ?: UserXpEntity()
        val totalXp = entity.totalXp
        val level = UserXpProfile.calculateLevel(totalXp)
        val currentLevelXp = UserXpProfile.calculateCurrentLevelXp(totalXp)
        val unlockedBadgeIds = if (entity.unlockedBadgeIdsCsv.isNotBlank()) {
            entity.unlockedBadgeIdsCsv.split(",").toSet()
        } else {
            emptySet()
        }

        val allBadges = SkillBadgeCatalog.ALL_BADGES.map { badge ->
            badge.copy(isUnlocked = badge.id in unlockedBadgeIds)
        }
        val unlockedBadges = allBadges.filter { it.isUnlocked }
        val completedCount = skills.count { it.status.isCompletedOrMastered }
        val progressPercent = (currentLevelXp.toFloat() / UserXpProfile.XP_PER_LEVEL).coerceIn(0f, 1f)

        UserXpProfile(
            totalXp = totalXp,
            level = level,
            currentLevelXp = currentLevelXp,
            xpForNextLevel = UserXpProfile.XP_PER_LEVEL,
            levelProgressPercent = progressPercent,
            completedSkillCount = completedCount,
            unlockedBadges = unlockedBadges,
            allBadges = allBadges,
            rankTitle = UserXpProfile.calculateRankTitle(level)
        )
    }

    /**
     * Evaluates XP gain, level transitions, and achievement badges when a skill is completed.
     */
    private suspend fun handleSkillCompletionXp(skillId: String, newStatus: SkillStatus) {
        if (!newStatus.isCompletedOrMastered) return

        val targetSkill = initialSkills.find { it.id == skillId } ?: return
        val currentXpEntity = skillDao.getUserXp() ?: UserXpEntity()

        val alreadyAwardedIds = if (currentXpEntity.completedSkillIdsCsv.isNotBlank()) {
            currentXpEntity.completedSkillIdsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        } else {
            mutableSetOf()
        }

        val wasAlreadyAwarded = alreadyAwardedIds.contains(skillId)
        if (wasAlreadyAwarded) {
            // Already earned completion XP for this specific skill
            return
        }

        alreadyAwardedIds.add(skillId)

        // Core branches award +150 XP, others award +100 XP
        val isCoreBranch = targetSkill.branchId == BranchId.BACKEND_DOTNET ||
                targetSkill.branchId == BranchId.COMPUTER_SCIENCE
        val baseSkillXp = if (isCoreBranch) 150 else 100
        var totalXpEarnedInEvent = baseSkillXp

        val currentTotalXp = currentXpEntity.totalXp
        val oldLevel = currentXpEntity.level

        // Collect all completed skills to evaluate badges
        val allProgressList = skillDao.getAllSkillProgressList()
        val progressMap = allProgressList.associateBy { it.skillId }
        val allCurrentCompletedSkills = initialSkills.filter { skill ->
            val status = progressMap[skill.id]?.let { SkillStatus.fromString(it.status) } ?: skill.status
            status.isCompletedOrMastered || skill.id == skillId
        }

        val unlockedBadgeIds = if (currentXpEntity.unlockedBadgeIdsCsv.isNotBlank()) {
            currentXpEntity.unlockedBadgeIdsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        } else {
            mutableSetOf()
        }

        val intermediateXp = currentTotalXp + baseSkillXp
        val intermediateLevel = UserXpProfile.calculateLevel(intermediateXp)

        val newlyUnlockedBadges = SkillBadgeCatalog.evaluateBadges(
            completedSkills = allCurrentCompletedSkills,
            currentLevel = intermediateLevel,
            alreadyUnlockedIds = unlockedBadgeIds
        )

        for (badge in newlyUnlockedBadges) {
            unlockedBadgeIds.add(badge.id)
            totalXpEarnedInEvent += badge.xpReward
        }

        val finalTotalXp = currentTotalXp + totalXpEarnedInEvent
        val finalLevel = UserXpProfile.calculateLevel(finalTotalXp)
        val isLevelUp = finalLevel > oldLevel

        // Persist to Room
        val updatedXpEntity = currentXpEntity.copy(
            totalXp = finalTotalXp,
            level = finalLevel,
            completedSkillIdsCsv = alreadyAwardedIds.joinToString(","),
            unlockedBadgeIdsCsv = unlockedBadgeIds.joinToString(","),
            updatedAt = System.currentTimeMillis()
        )
        skillDao.upsertUserXp(updatedXpEntity)

        // Emit celebration event
        _rewardNotificationFlow.emit(
            RewardNotification(
                skillId = skillId,
                skillName = targetSkill.name,
                xpEarned = totalXpEarnedInEvent,
                isLevelUp = isLevelUp,
                oldLevel = oldLevel,
                newLevel = finalLevel,
                newBadges = newlyUnlockedBadges
            )
        )
    }

    /**
     * Updates the status of a specific skill node in Room database.
     * Common progression: Not Started -> In Progress -> Completed -> Mastered.
     */
    suspend fun setSkillStatus(skillId: String, newStatus: SkillStatus) = withContext(Dispatchers.IO) {
        val existing = skillDao.getSkillProgressById(skillId)
        val updated = existing?.copy(
            status = newStatus.name,
            updatedAt = System.currentTimeMillis()
        ) ?: SkillProgressEntity(
            skillId = skillId,
            status = newStatus.name,
            personalNotes = "",
            updatedAt = System.currentTimeMillis()
        )
        skillDao.upsertSkillProgress(updated)

        if (newStatus.isCompletedOrMastered) {
            handleSkillCompletionXp(skillId, newStatus)
        }
    }

    /**
     * Cycles the status of a skill node to the next logical stage:
     * Not Started -> In Progress -> Completed -> Mastered -> Not Started.
     * Returns the newly applied status.
     */
    suspend fun cycleSkillStatus(skillId: String): SkillStatus = withContext(Dispatchers.IO) {
        val existing = skillDao.getSkillProgressById(skillId)
        val currentStatus = if (existing != null) {
            SkillStatus.fromString(existing.status)
        } else {
            initialSkills.find { it.id == skillId }?.status ?: SkillStatus.NOT_STARTED
        }

        val nextStatus = when (currentStatus) {
            SkillStatus.NOT_STARTED -> SkillStatus.IN_PROGRESS
            SkillStatus.IN_PROGRESS, SkillStatus.LEARNING -> SkillStatus.COMPLETED
            SkillStatus.PRACTICED -> SkillStatus.COMPLETED
            SkillStatus.COMPLETED -> SkillStatus.STRONG
            SkillStatus.STRONG -> SkillStatus.NOT_STARTED
        }

        val updated = existing?.copy(
            status = nextStatus.name,
            updatedAt = System.currentTimeMillis()
        ) ?: SkillProgressEntity(
            skillId = skillId,
            status = nextStatus.name,
            personalNotes = "",
            updatedAt = System.currentTimeMillis()
        )
        skillDao.upsertSkillProgress(updated)

        if (nextStatus.isCompletedOrMastered) {
            handleSkillCompletionXp(skillId, nextStatus)
        }
        nextStatus
    }


    /**
     * Saves user's personal engineering notes for a skill in Room.
     */
    suspend fun saveSkillNotes(skillId: String, notes: String) = withContext(Dispatchers.IO) {
        val existing = skillDao.getSkillProgressById(skillId)
        val defaultStatus = initialSkills.find { it.id == skillId }?.status ?: SkillStatus.NOT_STARTED
        val updated = existing?.copy(
            personalNotes = notes,
            updatedAt = System.currentTimeMillis()
        ) ?: SkillProgressEntity(
            skillId = skillId,
            status = defaultStatus.name,
            personalNotes = notes,
            updatedAt = System.currentTimeMillis()
        )
        skillDao.upsertSkillProgress(updated)
    }

    /**
     * Appends a custom resource link to the skill's resource list.
     */
    suspend fun addCustomResource(skillId: String, resourceUrl: String) = withContext(Dispatchers.IO) {
        val existing = skillDao.getSkillProgressById(skillId)
        val defaultStatus = initialSkills.find { it.id == skillId }?.status ?: SkillStatus.NOT_STARTED
        val current = existing?.customResources ?: ""
        val updatedList = if (current.isBlank()) resourceUrl else "$current,$resourceUrl"
        val updated = existing?.copy(
            customResources = updatedList,
            updatedAt = System.currentTimeMillis()
        ) ?: SkillProgressEntity(
            skillId = skillId,
            status = defaultStatus.name,
            customResources = resourceUrl,
            updatedAt = System.currentTimeMillis()
        )
        skillDao.upsertSkillProgress(updated)
    }

    /**
     * Associates a custom project with the skill node.
     */
    suspend fun addCustomProject(skillId: String, projectName: String) = withContext(Dispatchers.IO) {
        val existing = skillDao.getSkillProgressById(skillId)
        val defaultStatus = initialSkills.find { it.id == skillId }?.status ?: SkillStatus.NOT_STARTED
        val current = existing?.customProjects ?: ""
        val updatedList = if (current.isBlank()) projectName else "$current,$projectName"
        val updated = existing?.copy(
            customProjects = updatedList,
            updatedAt = System.currentTimeMillis()
        ) ?: SkillProgressEntity(
            skillId = skillId,
            status = defaultStatus.name,
            customProjects = projectName,
            updatedAt = System.currentTimeMillis()
        )
        skillDao.upsertSkillProgress(updated)
    }

    /**
     * Resets progress tracking for all nodes.
     */
    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        skillDao.clearAllSkillProgress()
    }
}
