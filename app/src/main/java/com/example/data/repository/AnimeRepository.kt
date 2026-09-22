package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.anime.AnimeApiService
import com.example.data.model.anime.AnimeItem
import com.example.data.model.anime.AnimeUserStats
import com.example.data.model.anime.AnimeWatchStatus
import com.example.data.model.anime.MediaTypeCategory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object AnimeRepository {

    private const val PREFS_NAME = "winter_arc_anime_storage"
    private const val KEY_ANIME_LIST = "anime_list_json"
    private const val KEY_LAST_MAL_USERNAME = "last_mal_username"
    private const val KEY_IS_INITIALIZED = "anime_initialized_v2"

    private val _animeFlow = MutableStateFlow<List<AnimeItem>>(emptyList())
    val animeFlow: StateFlow<List<AnimeItem>> = _animeFlow.asStateFlow()

    private var isLoaded = false

    fun resetForTesting() {
        isLoaded = false
        _animeFlow.value = emptyList()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Mockup data has been removed per user request.
     * New installations start clean; data comes from MAL sync or user additions.
     */
    fun defaultSeedAnimes(): List<AnimeItem> = emptyList()

    @Synchronized
    fun getAnimeList(context: Context): List<AnimeItem> {
        val prefs = getPrefs(context)
        if (!isLoaded) {
            val jsonStr = prefs.getString(KEY_ANIME_LIST, null)
            val list = if (jsonStr != null) {
                val deserialized = deserializeList(jsonStr)
                // Filter out any leftover initial mock items
                cleanMockupData(deserialized)
            } else {
                emptyList()
            }
            saveAnimeListInternal(prefs, list)
            _animeFlow.value = list
            isLoaded = true
        }
        return _animeFlow.value
    }

    private fun cleanMockupData(items: List<AnimeItem>): List<AnimeItem> {
        val mockNotes = setOf(
            "Gölge Hükümdarı Sung Jin-woo'nun uyanışı",
            "Özgürlük felsefesi ve duvarların ardı",
            "Zamanın ve anıların derinliği",
            "Shibuya Olayı yayını devam ediyor",
            "Quincy savaşı ve Bankai güçleri",
            "Vizyona girdiğinde izlenecek"
        )
        return items.filterNot { it.notes in mockNotes }
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
        val existingIndex = currentList.indexOfFirst { it.id == item.id || (item.malId != null && it.malId == item.malId && it.category == item.category) }
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

    suspend fun syncWithMyAnimeList(context: Context, username: String): Result<Pair<Int, Int>> = coroutineScope {
        val animeDeferred = async { AnimeApiService.fetchUserAnimeList(username) }
        val mangaDeferred = async { AnimeApiService.fetchUserMangaList(username) }

        val animeResult = animeDeferred.await()
        val mangaResult = mangaDeferred.await()

        if (animeResult.isFailure && mangaResult.isFailure) {
            val errorMsg = animeResult.exceptionOrNull()?.message ?: mangaResult.exceptionOrNull()?.message ?: "MAL Senkronizasyon hatası"
            return@coroutineScope Result.failure(Exception(errorMsg))
        }

        val remoteAnimeList = animeResult.getOrDefault(emptyList())
        val remoteMangaList = mangaResult.getOrDefault(emptyList())
        val allRemote = remoteAnimeList + remoteMangaList

        setLastMalUsername(context, username)

        synchronized(this@AnimeRepository) {
            val currentList = getAnimeList(context).toMutableList()
            var addedOrUpdatedAnime = 0
            var addedOrUpdatedManga = 0

            for (remote in allRemote) {
                val index = currentList.indexOfFirst {
                    it.malId != null && it.malId == remote.malId && it.category == remote.category
                }

                if (index != -1) {
                    val existing = currentList[index]
                    // If user watched more locally, preserve user's local advance, but record MAL's official count
                    val localWatched = if (existing.watchedEpisodes > remote.watchedEpisodes) {
                        existing.watchedEpisodes
                    } else {
                        remote.watchedEpisodes
                    }

                    currentList[index] = remote.copy(
                        id = existing.id,
                        watchedEpisodes = localWatched,
                        malWatchedEpisodes = remote.watchedEpisodes,
                        notes = existing.notes.ifBlank { remote.notes }
                    )
                } else {
                    currentList.add(remote)
                }

                if (remote.category == MediaTypeCategory.ANIME) {
                    addedOrUpdatedAnime++
                } else {
                    addedOrUpdatedManga++
                }
            }

            saveAndEmit(context, currentList)
            return@coroutineScope Result.success(Pair(addedOrUpdatedAnime, addedOrUpdatedManga))
        }
    }

    fun computeStats(items: List<AnimeItem>, category: MediaTypeCategory? = null): AnimeUserStats {
        val filtered = if (category != null) items.filter { it.category == category } else items
        val total = filtered.size
        var inProgress = 0
        var completed = 0
        var plan = 0
        var totalUnits = 0
        var outOfSync = 0

        for (item in filtered) {
            when (item.status) {
                AnimeWatchStatus.WATCHING -> inProgress++
                AnimeWatchStatus.COMPLETED -> completed++
                AnimeWatchStatus.PLAN_TO_WATCH -> plan++
                else -> {}
            }
            totalUnits += item.watchedEpisodes
            if (item.isOutOfSync) {
                outOfSync++
            }
        }

        return AnimeUserStats(
            totalCount = total,
            inProgressCount = inProgress,
            completedCount = completed,
            planCount = plan,
            totalWatchedUnits = totalUnits,
            outOfSyncCount = outOfSync
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
            obj.put("malWatchedEpisodes", item.malWatchedEpisodes)
            obj.put("score", item.score.toDouble())
            obj.put("status", item.status.name)
            obj.put("mediaType", item.mediaType)
            obj.put("category", item.category.name)
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
                val malWatched = obj.optInt("malWatchedEpisodes", watchedEpisodes)
                val score = obj.optDouble("score", 0.0).toFloat()
                val statusStr = obj.optString("status", AnimeWatchStatus.WATCHING.name)
                val status = AnimeWatchStatus.fromName(statusStr)
                val mediaType = obj.optString("mediaType", "TV")
                val categoryStr = obj.optString("category", MediaTypeCategory.ANIME.name)
                val category = MediaTypeCategory.fromName(categoryStr)

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
                        malWatchedEpisodes = malWatched,
                        score = score,
                        status = status,
                        mediaType = mediaType,
                        category = category,
                        genres = genresList,
                        notes = notes,
                        updatedAt = updatedAt
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }
}
