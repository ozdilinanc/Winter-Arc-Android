package com.example.data.model.anime

enum class MediaTypeCategory(val label: String, val unitLabel: String, val emoji: String) {
    ANIME("Anime", "Bölüm", "🎬"),
    MANGA("Manga & Manhwa", "Bölüm", "📖");

    companion object {
        fun fromName(name: String?): MediaTypeCategory =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: ANIME
    }
}

enum class AnimeWatchStatus(val id: Int, val animeLabel: String, val mangaLabel: String, val emoji: String) {
    WATCHING(1, "İzleniyor", "Okunuyor", "▶️"),
    COMPLETED(2, "Tamamlandı", "Tamamlandı", "✅"),
    ON_HOLD(3, "Beklemede", "Beklemede", "⏸️"),
    DROPPED(4, "Bırakıldı", "Bırakıldı", "⏹️"),
    PLAN_TO_WATCH(6, "İzlenecek", "Okunacak", "📋");

    fun getLabel(category: MediaTypeCategory): String =
        if (category == MediaTypeCategory.MANGA) mangaLabel else animeLabel

    val label: String get() = animeLabel

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
    val totalEpisodes: Int = 0, // 0 = Unknown / Airing / Publishing
    val malWatchedEpisodes: Int = watchedEpisodes, // Synced count from MyAnimeList
    val score: Float = 0f, // 0 to 10 (read-only from MAL)
    val status: AnimeWatchStatus = AnimeWatchStatus.WATCHING,
    val mediaType: String = "TV", // TV, Movie, Manga, Manhwa, etc.
    val category: MediaTypeCategory = MediaTypeCategory.ANIME,
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

    // MAL vs Local Sync Difference
    val isOutOfSync: Boolean
        get() = malId != null && watchedEpisodes != malWatchedEpisodes

    val syncDiff: Int
        get() = watchedEpisodes - malWatchedEpisodes
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
    val genres: List<String>,
    val category: MediaTypeCategory = MediaTypeCategory.ANIME
)

data class AnimeUserStats(
    val totalCount: Int,
    val inProgressCount: Int,
    val completedCount: Int,
    val planCount: Int,
    val totalWatchedUnits: Int,
    val outOfSyncCount: Int = 0
) {
    // Backwards compatibility helpers
    val totalAnime: Int get() = totalCount
    val watchingCount: Int get() = inProgressCount
    val planToWatchCount: Int get() = planCount
    val totalWatchedEpisodes: Int get() = totalWatchedUnits
}
