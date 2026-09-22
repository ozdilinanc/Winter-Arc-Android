package com.example.data.api.anime

import com.example.data.model.anime.AnimeItem
import com.example.data.model.anime.AnimeSearchItem
import com.example.data.model.anime.AnimeWatchStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object AnimeApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

    suspend fun fetchUserAnimeList(username: String): Result<List<AnimeItem>> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim()
        if (cleanUsername.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Kullanıcı adı boş olamaz"))
        }

        val allItems = mutableListOf<AnimeItem>()
        var offset = 0
        val pageSize = 300
        val maxPages = 5 // Up to 1500 anime items

        try {
            for (page in 0 until maxPages) {
                val url = "https://myanimelist.net/animelist/$cleanUsername/load.json?offset=$offset&status=7"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "application/json, text/plain, */*")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        if (response.code == 404) {
                            return@withContext Result.failure(Exception("MyAnimeList kullanıcısı bulunamadı: $cleanUsername"))
                        }
                        if (page == 0) {
                            return@withContext Result.failure(Exception("MAL Hatası: HTTP ${response.code}"))
                        }
                        return@withContext Result.success(allItems)
                    }

                    val bodyString = response.body?.string() ?: ""
                    if (bodyString.isBlank() || bodyString == "[]") {
                        return@withContext Result.success(allItems)
                    }

                    val jsonArray = try {
                        JSONArray(bodyString)
                    } catch (e: Exception) {
                        if (page == 0) throw e else return@withContext Result.success(allItems)
                    }

                    val count = jsonArray.length()
                    if (count == 0) {
                        return@withContext Result.success(allItems)
                    }

                    for (i in 0 until count) {
                        val obj = jsonArray.getJSONObject(i)
                        val animeId = obj.optInt("anime_id")
                        val title = obj.optString("anime_title", "Bilinmeyen Anime")
                        val titleEng = obj.optString("anime_title_eng").takeIf { it.isNotBlank() }
                        val numEpisodes = obj.optInt("anime_num_episodes", 0)
                        val watched = obj.optInt("num_watched_episodes", 0)
                        val score = obj.optDouble("score", 0.0).toFloat()
                        val rawStatus = obj.optInt("status", 1)
                        val status = when (rawStatus) {
                            1 -> AnimeWatchStatus.WATCHING
                            2 -> AnimeWatchStatus.COMPLETED
                            3 -> AnimeWatchStatus.ON_HOLD
                            4 -> AnimeWatchStatus.DROPPED
                            6 -> AnimeWatchStatus.PLAN_TO_WATCH
                            else -> AnimeWatchStatus.WATCHING
                        }
                        val imagePath = obj.optString("anime_image_path", "")
                        val mediaType = obj.optString("anime_media_type_string", "TV")

                        val genresList = mutableListOf<String>()
                        val genresArr = obj.optJSONArray("genres")
                        if (genresArr != null) {
                            for (g in 0 until genresArr.length()) {
                                val gObj = genresArr.optJSONObject(g)
                                val name = gObj?.optString("name")
                                if (!name.isNullOrBlank()) genresList.add(name)
                            }
                        }

                        allItems.add(
                            AnimeItem(
                                id = "mal_$animeId",
                                malId = animeId,
                                title = title,
                                titleEnglish = titleEng,
                                imageUrl = imagePath,
                                watchedEpisodes = watched,
                                totalEpisodes = numEpisodes,
                                score = score,
                                status = status,
                                mediaType = mediaType,
                                genres = genresList,
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }

                    if (count < pageSize) {
                        break
                    }
                    offset += pageSize
                }
            }

            Result.success(allItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchAnime(query: String): Result<List<AnimeSearchItem>> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return@withContext Result.success(emptyList())
        }

        try {
            val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://api.jikan.moe/v4/anime?q=$encodedQuery&limit=20&sfw=true"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Arama servisi hatası: HTTP ${response.code}"))
                }

                val bodyString = response.body?.string() ?: "{}"
                val root = JSONObject(bodyString)
                val dataArr = root.optJSONArray("data") ?: JSONArray()
                val results = mutableListOf<AnimeSearchItem>()

                for (i in 0 until dataArr.length()) {
                    val obj = dataArr.getJSONObject(i)
                    val malId = obj.optInt("mal_id")
                    val title = obj.optString("title", "İsimsiz")
                    val titleEng = obj.optString("title_english").takeIf { it.isNotBlank() }
                    val episodes = obj.optInt("episodes", 0)
                    val score = obj.optDouble("score", 0.0).toFloat()
                    val mediaType = obj.optString("type", "TV")
                    val synopsis = obj.optString("synopsis", "")

                    var imageUrl = ""
                    val imagesObj = obj.optJSONObject("images")
                    if (imagesObj != null) {
                        val jpgObj = imagesObj.optJSONObject("jpg")
                        if (jpgObj != null) {
                            imageUrl = jpgObj.optString("large_image_url", jpgObj.optString("image_url", ""))
                        }
                    }

                    val genresList = mutableListOf<String>()
                    val genresArr = obj.optJSONArray("genres")
                    if (genresArr != null) {
                        for (g in 0 until genresArr.length()) {
                            val gObj = genresArr.optJSONObject(g)
                            val name = gObj?.optString("name")
                            if (!name.isNullOrBlank()) genresList.add(name)
                        }
                    }

                    results.add(
                        AnimeSearchItem(
                            malId = malId,
                            title = title,
                            titleEnglish = titleEng,
                            imageUrl = imageUrl,
                            totalEpisodes = episodes,
                            score = score,
                            mediaType = mediaType,
                            synopsis = synopsis,
                            genres = genresList
                        )
                    )
                }

                Result.success(results)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
