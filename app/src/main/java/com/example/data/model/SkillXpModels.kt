package com.example.data.model

/**
 * Represents an achievement badge awarded to the user upon hitting milestones.
 */
data class SkillBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

/**
 * Comprehensive user XP profile containing current level, progression metrics, and badges.
 */
data class UserXpProfile(
    val totalXp: Int = 0,
    val level: Int = 1,
    val currentLevelXp: Int = 0,
    val xpForNextLevel: Int = XP_PER_LEVEL,
    val levelProgressPercent: Float = 0f,
    val completedSkillCount: Int = 0,
    val unlockedBadges: List<SkillBadge> = emptyList(),
    val allBadges: List<SkillBadge> = emptyList(),
    val rankTitle: String = "Junior Engineer"
) {
    val nextLevelTarget: Int
        get() = level * XP_PER_LEVEL

    companion object {
        const val XP_PER_LEVEL = 200

        fun calculateLevel(totalXp: Int): Int {
            return (totalXp / XP_PER_LEVEL) + 1
        }

        fun calculateCurrentLevelXp(totalXp: Int): Int {
            return totalXp % XP_PER_LEVEL
        }

        fun calculateRankTitle(level: Int): String {
            return when {
                level >= 10 -> "Principal Cloud Architect"
                level >= 7 -> "Senior Systems Engineer"
                level >= 5 -> "Core Staff Engineer"
                level >= 3 -> "Mid-Level Software Engineer"
                level >= 2 -> "Junior Software Engineer"
                else -> "Apprentice Engineer"
            }
        }
    }
}

/**
 * Event notification triggered when a user changes a skill status to Completed,
 * granting XP and unlocking badges or level ups.
 */
data class RewardNotification(
    val skillId: String,
    val skillName: String,
    val xpEarned: Int,
    val isLevelUp: Boolean,
    val oldLevel: Int,
    val newLevel: Int,
    val newBadges: List<SkillBadge> = emptyList()
)

/**
 * Central catalog of all unlockable engineering achievement badges.
 */
object SkillBadgeCatalog {
    val ALL_BADGES: List<SkillBadge> = listOf(
        SkillBadge(
            id = "badge_first_skill",
            title = "First Commit",
            description = "Completed your first skill node on the roadmap.",
            iconEmoji = "🚀",
            category = "Milestone",
            xpReward = 50
        ),
        SkillBadge(
            id = "badge_dotnet_pioneer",
            title = ".NET Explorer",
            description = "Completed 2+ .NET Backend architecture skills.",
            iconEmoji = "💻",
            category = ".NET / Backend",
            xpReward = 100
        ),
        SkillBadge(
            id = "badge_dotnet_master",
            title = "C# Core Artisan",
            description = "Completed 4+ .NET Backend deep-dive skills.",
            iconEmoji = "⚙️",
            category = ".NET / Backend",
            xpReward = 150
        ),
        SkillBadge(
            id = "badge_android_dev",
            title = "Mobile Crafter",
            description = "Completed 2+ Android & Kotlin mobile skills.",
            iconEmoji = "📱",
            category = "Android",
            xpReward = 100
        ),
        SkillBadge(
            id = "badge_systems_devops",
            title = "DevOps Navigator",
            description = "Completed 2+ Distributed Systems or DevOps skills.",
            iconEmoji = "☸️",
            category = "Distributed Systems",
            xpReward = 100
        ),
        SkillBadge(
            id = "badge_cs_core",
            title = "Algorithmist",
            description = "Completed 2+ Foundational Computer Science skills.",
            iconEmoji = "🧠",
            category = "CS Foundational",
            xpReward = 100
        ),
        SkillBadge(
            id = "badge_security",
            title = "Cyber Sentinel",
            description = "Completed a Cyber Security & Defense skill.",
            iconEmoji = "🔐",
            category = "Security",
            xpReward = 75
        ),
        SkillBadge(
            id = "badge_polyglot",
            title = "T-Shaped Engineer",
            description = "Completed skills spanning at least 3 distinct engineering branches.",
            iconEmoji = "🌐",
            category = "Versatility",
            xpReward = 150
        ),
        SkillBadge(
            id = "badge_sprint_5",
            title = "Momentum V",
            description = "Completed 5 skills total across all branches.",
            iconEmoji = "⚡",
            category = "Milestone",
            xpReward = 100
        ),
        SkillBadge(
            id = "badge_master_10",
            title = "Decade Architect",
            description = "Completed 10 skills total across all branches.",
            iconEmoji = "🏆",
            category = "Milestone",
            xpReward = 200
        ),
        SkillBadge(
            id = "badge_level_3",
            title = "Level 3 Vanguard",
            description = "Progressed your engineer level to 3.",
            iconEmoji = "🥉",
            category = "Level",
            xpReward = 50
        ),
        SkillBadge(
            id = "badge_level_5",
            title = "Level 5 Architect",
            description = "Achieved Level 5 engineering mastery.",
            iconEmoji = "🥇",
            category = "Level",
            xpReward = 150
        )
    )

    fun evaluateBadges(
        completedSkills: List<SkillNode>,
        currentLevel: Int,
        alreadyUnlockedIds: Set<String>
    ): List<SkillBadge> {
        val newlyUnlocked = mutableListOf<SkillBadge>()
        val completedCount = completedSkills.size
        val completedBranches = completedSkills.map { it.branchId }.distinct().size

        val dotnetCount = completedSkills.count { it.branchId == BranchId.BACKEND_DOTNET }
        val androidCount = completedSkills.count { it.branchId == BranchId.ANDROID_MOBILE }
        val devopsCount = completedSkills.count { it.branchId == BranchId.DISTRIBUTED_DEVOPS }
        val csCount = completedSkills.count { it.branchId == BranchId.COMPUTER_SCIENCE }
        val securityCount = completedSkills.count { it.branchId == BranchId.CYBER_SECURITY }

        for (badge in ALL_BADGES) {
            if (badge.id in alreadyUnlockedIds) continue

            val isConditionMet = when (badge.id) {
                "badge_first_skill" -> completedCount >= 1
                "badge_dotnet_pioneer" -> dotnetCount >= 2
                "badge_dotnet_master" -> dotnetCount >= 4
                "badge_android_dev" -> androidCount >= 2
                "badge_systems_devops" -> devopsCount >= 2
                "badge_cs_core" -> csCount >= 2
                "badge_security" -> securityCount >= 1
                "badge_polyglot" -> completedBranches >= 3
                "badge_sprint_5" -> completedCount >= 5
                "badge_master_10" -> completedCount >= 10
                "badge_level_3" -> currentLevel >= 3
                "badge_level_5" -> currentLevel >= 5
                else -> false
            }

            if (isConditionMet) {
                newlyUnlocked.add(badge.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
            }
        }

        return newlyUnlocked
    }
}
