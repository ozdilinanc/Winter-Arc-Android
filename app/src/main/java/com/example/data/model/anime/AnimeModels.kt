package com.example.data.model.anime

enum class AnimeWatchStatus(val id: Int, val label: String, val emoji: String) {
    WATCHING(1, "İzleniyor", "▶️"),
    COMPLETED(2, "Tamamlandı", "✅"),
    ON_HOLD(3, "Beklemede", "⏸️"),
    DROPPED(4, "Bırakıldı", "⏹️"),
    PLAN_TO_WATCH(6, "İzlenecek", "📋");

    companion object {
        fun fromId(id: Int): AnimeWatchStatus = entries.find { it.id == id } ?: WATCHING
        fun fromName(name: String?): AnimeWatchStatus =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: WATCHING
    }
}

data class AnimeItem(
    val id: String, // "mal_$malId" or local UUID
    val malId: Int? = null,
    val title: String,
    val titleEnglish: String? = null,
    val imageUrl: String = "",
    val watchedEpisodes: Int = 0,
    val totalEpisodes: Int = 0, // 0 = Unknown / Airing
    val score: Float = 0f, // 0 to 10
    val status: AnimeWatchStatus = AnimeWatchStatus.WATCHING,
    val mediaType: String = "TV",
    val genres: List<String> = emptyList(),
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val progressFraction: Float
        get() = when {
            totalEpisodes > 0 -> (watchedEpisodes.toFloat() / totalEpisodes.toFloat()).coerceIn(0f, 1f)
            watchedEpisodes > 0 -> 0.6f
            else -> 0f
        }

    val isCompleted: Boolean
        get() = status == AnimeWatchStatus.COMPLETED || (totalEpisodes > 0 && watchedEpisodes >= totalEpisodes)

    val displayTitle: String
        get() = if (!titleEnglish.isNullOrBlank()) titleEnglish else title
}

data class AnimeSearchItem(
    val malId: Int,
    val title: String,
    val titleEnglish: String?,
    val imageUrl: String,
    val totalEpisodes: Int,
    val score: Float,
    val mediaType: String,
    val synopsis: String,
    val genres: List<String>
)

data class AnimeUserStats(
    val totalAnime: Int,
    val watchingCount: Int,
    val completedCount: Int,
    val planToWatchCount: Int,
    val totalWatchedEpisodes: Int
)
