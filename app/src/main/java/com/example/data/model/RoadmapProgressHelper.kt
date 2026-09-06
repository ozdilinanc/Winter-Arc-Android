package com.example.data.model

import android.content.SharedPreferences

data class SubItemProgressStats(
    val totalCount: Int,
    val completedCount: Int,
    val practiceCount: Int,
    val theoryCount: Int,
    val earnedPoints: Float,
    val progressFraction: Float,
    val progressPercent: Int
)

object RoadmapProgressHelper {
    const val PREFS_ROADMAP = "winter_arc_roadmap_progress"

    fun getSubItemStats(
        subItemId: String,
        prefs: SharedPreferences,
        projects: List<EngineeringProject> = emptyList()
    ): SubItemProgressStats {
        if (subItemId == "sub_personal_projects" || subItemId == "sub_projects") {
            val total = projects.size
            val completed = projects.count { it.currentStage.order == 9 }
            val inProgress = projects.count { it.currentStage.order in 2..8 }
            val points = completed * 1.0f + inProgress * 0.5f
            val fraction = if (total > 0) (points / total.toFloat()).coerceIn(0f, 1f) else 0f
            val percent = (fraction * 100).toInt().coerceIn(0, 100)
            return SubItemProgressStats(
                totalCount = total,
                completedCount = completed,
                practiceCount = inProgress,
                theoryCount = 0,
                earnedPoints = points,
                progressFraction = fraction,
                progressPercent = percent
            )
        }

        val roadmap = RoadmapDataStore.allRoadmaps[subItemId]
            ?: return SubItemProgressStats(0, 0, 0, 0, 0f, 0f, 0)

        val items = roadmap.sections.flatMap { it.items }
        val total = items.size
        var completed = 0
        var practice = 0
        var theory = 0

        for (item in items) {
            val key = prefs.getString("status_${item.id}", null)
            when (TopicProgressState.fromKey(key)) {
                TopicProgressState.COMPLETED -> completed++
                TopicProgressState.PRACTICED -> practice++
                TopicProgressState.THEORY -> theory++
                TopicProgressState.NOT_STARTED -> {}
            }
        }

        val points = theory * 0.35f + practice * 0.70f + completed * 1.0f
        val fraction = if (total > 0) (points / total.toFloat()).coerceIn(0f, 1f) else 0f
        val percent = (fraction * 100).toInt().coerceIn(0, 100)

        return SubItemProgressStats(
            totalCount = total,
            completedCount = completed,
            practiceCount = practice,
            theoryCount = theory,
            earnedPoints = points,
            progressFraction = fraction,
            progressPercent = percent
        )
    }

    fun getCategoryStats(
        subItemIds: List<String>,
        prefs: SharedPreferences,
        projects: List<EngineeringProject> = emptyList()
    ): SubItemProgressStats {
        var total = 0
        var completed = 0
        var practice = 0
        var theory = 0
        var points = 0f

        for (subItemId in subItemIds) {
            val stats = getSubItemStats(subItemId, prefs, projects)
            total += stats.totalCount
            completed += stats.completedCount
            practice += stats.practiceCount
            theory += stats.theoryCount
            points += stats.earnedPoints
        }

        val fraction = if (total > 0) (points / total.toFloat()).coerceIn(0f, 1f) else 0f
        val percent = (fraction * 100).toInt().coerceIn(0, 100)

        return SubItemProgressStats(
            totalCount = total,
            completedCount = completed,
            practiceCount = practice,
            theoryCount = theory,
            earnedPoints = points,
            progressFraction = fraction,
            progressPercent = percent
        )
    }
}
