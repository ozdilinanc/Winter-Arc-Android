package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.anime.AnimeApiService
import com.example.data.model.anime.AnimeItem
import com.example.data.model.anime.AnimeUserStats
import com.example.data.model.anime.AnimeWatchStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object AnimeRepository {

    private const val PREFS_NAME = "winter_arc_anime_storage"
    private const val KEY_ANIME_LIST = "anime_list_json"
    private const val KEY_LAST_MAL_USERNAME = "last_mal_username"
    private const val KEY_IS_INITIALIZED = "anime_initialized_v1"

    private val _animeFlow = MutableStateFlow<List<AnimeItem>>(emptyList())
    val animeFlow: StateFlow<List<AnimeItem>> = _animeFlow.asStateFlow()

    private var isLoaded = false

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun defaultSeedAnimes(): List<AnimeItem> {
        val now = System.currentTimeMillis()
        return listOf(
            AnimeItem(
                id = "mal_52299",
                malId = 52299,
                title = "Ore dake Level Up na Ken",
                titleEnglish = "Solo Leveling",
                imageUrl = "https://cdn.myanimelist.net/images/anime/1839/140815l.jpg",
                watchedEpisodes = 12,
                totalEpisodes = 12,
                score = 8.5f,
                status = AnimeWatchStatus.COMPLETED,
                mediaType = "TV",
                genres = listOf("Action", "Fantasy"),
                notes = "Gölge Hükümdarı Sung Jin-woo'nun uyanışı",
                updatedAt = now
            ),
            AnimeItem(
                id = "mal_16498",
                malId = 16498,
                title = "Shingeki no Kyojin",
                titleEnglish = "Attack on Titan",
                imageUrl = "https://cdn.myanimelist.net/images/anime/10/47347l.jpg",
                watchedEpisodes = 25,
                totalEpisodes = 25,
                score = 9.0f,
                status = AnimeWatchStatus.COMPLETED,
                mediaType = "TV",
                genres = listOf("Action", "Drama", "Suspense"),
                notes = "Özgürlük felsefesi ve duvarların ardı",
                updatedAt = now - 1000
            ),
            AnimeItem(
                id = "mal_52991",
                malId = 52991,
                title = "Sousou no Frieren",
                titleEnglish = "Frieren: Beyond Journey's End",
                imageUrl = "https://cdn.myanimelist.net/images/anime/1015/138075l.jpg",
                watchedEpisodes = 28,
                totalEpisodes = 28,
                score = 9.4f,
                status = AnimeWatchStatus.COMPLETED,
                mediaType = "TV",
                genres = listOf("Adventure", "Drama", "Fantasy"),
                notes = "Zamanın ve anıların derinliği",
                updatedAt = now - 2000
            ),
            AnimeItem(
                id = "mal_51009",
                malId = 51009,
                title = "Jujutsu Kaisen 2nd Season",
                titleEnglish = "Jujutsu Kaisen Season 2",
                imageUrl = "https://cdn.myanimelist.net/images/anime/1792/138022l.jpg",
                watchedEpisodes = 18,
                totalEpisodes = 23,
                score = 8.8f,
                status = AnimeWatchStatus.WATCHING,
                mediaType = "TV",
                genres = listOf("Action", "Supernatural"),
                notes = "Shibuya Olayı yayını devam ediyor",
                updatedAt = now - 3000
            ),
            AnimeItem(
                id = "mal_41467",
                malId = 41467,
                title = "Bleach: Sennen Kessen-hen",
                titleEnglish = "Bleach: Thousand-Year Blood War",
                imageUrl = "https://cdn.myanimelist.net/images/anime/1764/126627l.jpg",
                watchedEpisodes = 5,
                totalEpisodes = 13,
                score = 9.0f,
                status = AnimeWatchStatus.WATCHING,
                mediaType = "TV",
                genres = listOf("Action", "Adventure", "Fantasy"),
                notes = "Quincy savaşı ve Bankai güçleri",
                updatedAt = now - 4000
            ),
            AnimeItem(
                id = "mal_57555",
                malId = 57555,
                title = "Chainsaw Man Movie: Reze-hen",
                titleEnglish = "Chainsaw Man the Movie: Reze Arc",
                imageUrl = "https://cdn.myanimelist.net/images/anime/1805/140643l.jpg",
                watchedEpisodes = 0,
                totalEpisodes = 1,
                score = 0f,
                status = AnimeWatchStatus.PLAN_TO_WATCH,
                mediaType = "Movie",
                genres = listOf("Action", "Supernatural"),
                notes = "Vizyona girdiğinde izlenecek",
                updatedAt = now - 5000
            )
        )
    }

    @Synchronized
    fun getAnimeList(context: Context): List<AnimeItem> {
        val prefs = getPrefs(context)
        if (!isLoaded) {
            val jsonStr = prefs.getString(KEY_ANIME_LIST, null)
            val list = if (jsonStr != null) {
                deserializeList(jsonStr)
            } else {
                val defaults = defaultSeedAnimes()
                saveAnimeListInternal(prefs, defaults)
                prefs.edit().putBoolean(KEY_IS_INITIALIZED, true).apply()
                defaults
            }
            _animeFlow.value = list
            isLoaded = true
        }
        return _animeFlow.value
    }

    @Synchronized
    fun getLastMalUsername(context: Context): String? {
        return getPrefs(context).getString(KEY_LAST_MAL_USERNAME, null)
    }

    @Synchronized
    fun setLastMalUsername(context: Context, username: String) {
        getPrefs(context).edit().putString(KEY_LAST_MAL_USERNAME, username.trim()).apply()
    }

    @Synchronized
    fun incrementEpisode(context: Context, id: String, amount: Int = 1) {
        val currentList = getAnimeList(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = currentList[index]
            val newWatched = if (old.totalEpisodes > 0) {
                (old.watchedEpisodes + amount).coerceAtMost(old.totalEpisodes)
            } else {
                old.watchedEpisodes + amount
            }

            val newStatus = if (old.totalEpisodes > 0 && newWatched >= old.totalEpisodes) {
                AnimeWatchStatus.COMPLETED
            } else if (old.status == AnimeWatchStatus.PLAN_TO_WATCH && newWatched > 0) {
                AnimeWatchStatus.WATCHING
            } else {
                old.status
            }

            currentList[index] = old.copy(
                watchedEpisodes = newWatched,
                status = newStatus,
                updatedAt = System.currentTimeMillis()
            )
            saveAndEmit(context, currentList)
        }
    }

    @Synchronized
    fun decrementEpisode(context: Context, id: String, amount: Int = 1) {
        val currentList = getAnimeList(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = currentList[index]
            val newWatched = (old.watchedEpisodes - amount).coerceAtLeast(0)
            currentList[index] = old.copy(
                watchedEpisodes = newWatched,
                updatedAt = System.currentTimeMillis()
            )
            saveAndEmit(context, currentList)
        }
    }

    @Synchronized
    fun updateWatchedEpisodes(context: Context, id: String, episodes: Int) {
        val currentList = getAnimeList(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = currentList[index]
            val sanitized = if (old.totalEpisodes > 0) episodes.coerceIn(0, old.totalEpisodes) else episodes.coerceAtLeast(0)
            val newStatus = if (old.totalEpisodes > 0 && sanitized >= old.totalEpisodes) {
                AnimeWatchStatus.COMPLETED
            } else {
                old.status
            }
            currentList[index] = old.copy(
                watchedEpisodes = sanitized,
                status = newStatus,
                updatedAt = System.currentTimeMillis()
            )
            saveAndEmit(context, currentList)
        }
    }

    @Synchronized
    fun updateStatus(context: Context, id: String, status: AnimeWatchStatus) {
        val currentList = getAnimeList(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = currentList[index]
            val newWatched = if (status == AnimeWatchStatus.COMPLETED && old.totalEpisodes > 0 && old.watchedEpisodes < old.totalEpisodes) {
                old.totalEpisodes
            } else {
                old.watchedEpisodes
            }
            currentList[index] = old.copy(
                status = status,
                watchedEpisodes = newWatched,
                updatedAt = System.currentTimeMillis()
            )
            saveAndEmit(context, currentList)
        }
    }

    @Synchronized
    fun updateScore(context: Context, id: String, score: Float) {
        val currentList = getAnimeList(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = currentList[index]
            currentList[index] = old.copy(
                score = score.coerceIn(0f, 10f),
                updatedAt = System.currentTimeMillis()
            )
            saveAndEmit(context, currentList)
        }
    }

    @Synchronized
    fun updateNotes(context: Context, id: String, notes: String) {
        val currentList = getAnimeList(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = currentList[index]
            currentList[index] = old.copy(
                notes = notes,
                updatedAt = System.currentTimeMillis()
            )
            saveAndEmit(context, currentList)
        }
    }

    @Synchronized
    fun addOrUpdateAnime(context: Context, item: AnimeItem) {
        val currentList = getAnimeList(context).toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == item.id || (item.malId != null && it.malId == item.malId) }
        if (existingIndex != -1) {
            val old = currentList[existingIndex]
            currentList[existingIndex] = item.copy(
                id = old.id,
                notes = if (item.notes.isBlank()) old.notes else item.notes
            )
        } else {
            currentList.add(0, item)
        }
        saveAndEmit(context, currentList)
    }

    @Synchronized
    fun deleteAnime(context: Context, id: String) {
        val currentList = getAnimeList(context).toMutableList()
        currentList.removeAll { it.id == id }
        saveAndEmit(context, currentList)
    }

    suspend fun syncWithMyAnimeList(context: Context, username: String): Result<Int> {
        val result = AnimeApiService.fetchUserAnimeList(username)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull() ?: Exception("MAL Senkronizasyon hatası"))
        }

        val remoteList = result.getOrNull() ?: emptyList()
        setLastMalUsername(context, username)

        synchronized(this) {
            val currentList = getAnimeList(context).toMutableList()
            var addedOrUpdatedCount = 0

            for (remote in remoteList) {
                val index = currentList.indexOfFirst { it.malId != null && it.malId == remote.malId }
                if (index != -1) {
                    val existing = currentList[index]
                    currentList[index] = remote.copy(
                        id = existing.id,
                        notes = existing.notes.ifBlank { remote.notes }
                    )
                } else {
                    currentList.add(remote)
                }
                addedOrUpdatedCount++
            }

            saveAndEmit(context, currentList)
            return Result.success(addedOrUpdatedCount)
        }
    }

    fun computeStats(items: List<AnimeItem>): AnimeUserStats {
        val total = items.size
        var watching = 0
        var completed = 0
        var planToWatch = 0
        var totalEpisodes = 0

        for (item in items) {
            when (item.status) {
                AnimeWatchStatus.WATCHING -> watching++
                AnimeWatchStatus.COMPLETED -> completed++
                AnimeWatchStatus.PLAN_TO_WATCH -> planToWatch++
                else -> {}
            }
            totalEpisodes += item.watchedEpisodes
        }

        return AnimeUserStats(
            totalAnime = total,
            watchingCount = watching,
            completedCount = completed,
            planToWatchCount = planToWatch,
            totalWatchedEpisodes = totalEpisodes
        )
    }

    private fun saveAndEmit(context: Context, list: List<AnimeItem>) {
        val prefs = getPrefs(context)
        saveAnimeListInternal(prefs, list)
        _animeFlow.value = list
    }

    private fun saveAnimeListInternal(prefs: SharedPreferences, list: List<AnimeItem>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            if (item.malId != null) obj.put("malId", item.malId)
            obj.put("title", item.title)
            if (item.titleEnglish != null) obj.put("titleEnglish", item.titleEnglish)
            obj.put("imageUrl", item.imageUrl)
            obj.put("watchedEpisodes", item.watchedEpisodes)
            obj.put("totalEpisodes", item.totalEpisodes)
            obj.put("score", item.score.toDouble())
            obj.put("status", item.status.name)
            obj.put("mediaType", item.mediaType)
            val genresArr = JSONArray()
            item.genres.forEach { genresArr.put(it) }
            obj.put("genres", genresArr)
            obj.put("notes", item.notes)
            obj.put("updatedAt", item.updatedAt)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_ANIME_LIST, jsonArray.toString()).apply()
    }

    private fun deserializeList(jsonStr: String): List<AnimeItem> {
        val list = mutableListOf<AnimeItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "anime_$i")
                val malId = if (obj.has("malId")) obj.optInt("malId") else null
                val title = obj.optString("title", "")
                val titleEnglish = obj.optString("titleEnglish").takeIf { it.isNotBlank() }
                val imageUrl = obj.optString("imageUrl", "")
                val watchedEpisodes = obj.optInt("watchedEpisodes", 0)
                val totalEpisodes = obj.optInt("totalEpisodes", 0)
                val score = obj.optDouble("score", 0.0).toFloat()
                val statusStr = obj.optString("status", AnimeWatchStatus.WATCHING.name)
                val status = AnimeWatchStatus.fromName(statusStr)
                val mediaType = obj.optString("mediaType", "TV")
                val genresList = mutableListOf<String>()
                val genresArr = obj.optJSONArray("genres")
                if (genresArr != null) {
                    for (g in 0 until genresArr.length()) {
                        genresList.add(genresArr.getString(g))
                    }
                }
                val notes = obj.optString("notes", "")
                val updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())

                list.add(
                    AnimeItem(
                        id = id,
                        malId = malId,
                        title = title,
                        titleEnglish = titleEnglish,
                        imageUrl = imageUrl,
                        watchedEpisodes = watchedEpisodes,
                        totalEpisodes = totalEpisodes,
                        score = score,
                        status = status,
                        mediaType = mediaType,
                        genres = genresList,
                        notes = notes,
                        updatedAt = updatedAt
                    )
                )
            }
        } catch (_: Exception) {
            return defaultSeedAnimes()
        }
        return list
    }
}
