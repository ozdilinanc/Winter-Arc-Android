package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class SkillStatus(val label: String) {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    LEARNING("In Progress"),
    PRACTICED("Practiced"),
    COMPLETED("Completed"),
    STRONG("Mastered");

    val color: Color
        get() = when (this) {
            NOT_STARTED -> StatusNotStarted
            IN_PROGRESS, LEARNING -> StatusLearning
            PRACTICED -> StatusPracticed
            COMPLETED -> StatusCompleted
            STRONG -> StatusStrong
        }

    val isCompletedOrMastered: Boolean
        get() = this == COMPLETED || this == STRONG

    companion object {
        fun fromString(value: String?): SkillStatus {
            if (value.isNullOrBlank()) return NOT_STARTED
            return when (value.trim().uppercase()) {
                "NOT_STARTED", "NOT STARTED" -> NOT_STARTED
                "IN_PROGRESS", "IN PROGRESS", "LEARNING" -> IN_PROGRESS
                "PRACTICED" -> PRACTICED
                "COMPLETED", "DONE" -> COMPLETED
                "STRONG", "MASTERED" -> STRONG
                else -> NOT_STARTED
            }
        }
    }
}

enum class BranchId(
    val title: String,
    val shortName: String,
    val iconEmoji: String,
    val priorityLabel: String,
    val accentColor: Color,
    val isCorePriority: Boolean = false
) {
    BACKEND_DOTNET(
        title = "BACKEND / .NET",
        shortName = ".NET Backend",
        iconEmoji = "💻",
        priorityLabel = "Highest Priority",
        accentColor = BranchDotNet,
        isCorePriority = true
    ),
    DISTRIBUTED_DEVOPS(
        title = "DISTRIBUTED SYSTEMS / DEVOPS",
        shortName = "DevOps & Systems",
        iconEmoji = "☸️",
        priorityLabel = "High Priority",
        accentColor = BranchDevOps
    ),
    ANDROID_MOBILE(
        title = "ANDROID / MOBILE",
        shortName = "Android",
        iconEmoji = "📱",
        priorityLabel = "Core Skill",
        accentColor = BranchAndroid
    ),
    COMPUTER_SCIENCE(
        title = "COMPUTER SCIENCE",
        shortName = "CS Core",
        iconEmoji = "🧠",
        priorityLabel = "Foundational",
        accentColor = BranchCS
    ),
    GRADUATION_PROJECT(
        title = "GRADUATION PROJECT / SYSTEMS",
        shortName = "KV Cache / LLM",
        iconEmoji = "🔬",
        priorityLabel = "Specialized Research",
        accentColor = BranchGraduation
    ),
    ENGINEERING_TOOLS(
        title = "ENGINEERING TOOLS",
        shortName = "Tools & Linux",
        iconEmoji = "🛠️",
        priorityLabel = "Essential Workflow",
        accentColor = BranchTools
    ),
    CYBER_SECURITY(
        title = "CYBER SECURITY",
        shortName = "Security",
        iconEmoji = "🔐",
        priorityLabel = "Production Defense",
        accentColor = BranchSecurity
    ),
    ENGLISH(
        title = "ENGLISH",
        shortName = "Active B2+",
        iconEmoji = "🇬🇧",
        priorityLabel = "Professional Global",
        accentColor = BranchEnglish
    ),
    PORTFOLIO_OUTPUT(
        title = "PORTFOLIO / OUTPUT",
        shortName = "Portfolio",
        iconEmoji = "🚀",
        priorityLabel = "Demonstrated Output",
        accentColor = BranchPortfolio
    ),
    KNOWLEDGE_MANAGEMENT(
        title = "KNOWLEDGE MANAGEMENT",
        shortName = "Knowledge Loop",
        iconEmoji = "✍️",
        priorityLabel = "Technical Memory",
        accentColor = BranchKnowledge
    ),
    CERTIFICATES(
        title = "CERTIFICATES",
        shortName = "Certificates",
        iconEmoji = "🎓",
        priorityLabel = "Optional Byproduct",
        accentColor = BranchCerts
    ),
    SECOND_LANGUAGE(
        title = "SECOND LANGUAGE",
        shortName = "Hobby Language",
        iconEmoji = "🌍",
        priorityLabel = "Optional Hobby",
        accentColor = BranchLanguage
    )
}

data class SkillNode(
    val id: String,
    val name: String,
    val branchId: BranchId,
    val category: String,
    val description: String,
    val prerequisites: List<String> = emptyList(),
    val recommendedResources: List<String> = emptyList(),
    val priorityTag: String = "Core",
    val status: SkillStatus = SkillStatus.NOT_STARTED,
    val personalNotes: String = "",
    val connectedProjects: List<String> = emptyList(),
    val connectedArticles: List<String> = emptyList()
)

enum class ProjectWorkflowStage(val stageName: String, val order: Int) {
    IDEA("Idea", 1),
    DEVELOP("Develop", 2),
    TEST("Test", 3),
    GITHUB("GitHub", 4),
    README("README", 5),
    RELEASE("Release", 6),
    PLAY_STORE("Play Store (if mobile)", 7),
    MEDIUM_ARTICLE("Medium article", 8),
    CV("CV / Portfolio", 9)
}

data class EngineeringProject(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val currentStage: ProjectWorkflowStage,
    val githubRepo: String = "",
    val mediumArticleUrl: String = "",
    val notes: String = "",
    val tags: List<String> = emptyList()
)

data class KnowledgeLoopStage(
    val stepNumber: Int,
    val name: String,
    val description: String,
    val actionHint: String
)

data class ReferenceBook(
    val title: String,
    val domain: String,
    val whyItMatters: String,
    val keyTopics: List<String>
)
