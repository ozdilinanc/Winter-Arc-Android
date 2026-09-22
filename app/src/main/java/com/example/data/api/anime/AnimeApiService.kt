package com.example.data.api.anime

import com.example.data.model.anime.AnimeItem
import com.example.data.model.anime.AnimeSearchItem
import com.example.data.model.anime.AnimeWatchStatus
import com.example.data.model.anime.MediaTypeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
                                malWatchedEpisodes = watched,
                                score = score,
                                status = status,
                                mediaType = mediaType,
                                category = MediaTypeCategory.ANIME,
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

    suspend fun fetchUserMangaList(username: String): Result<List<AnimeItem>> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim()
        if (cleanUsername.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Kullanıcı adı boş olamaz"))
        }

        val allItems = mutableListOf<AnimeItem>()
        var offset = 0
        val pageSize = 300
        val maxPages = 5

        try {
            for (page in 0 until maxPages) {
                val url = "https://myanimelist.net/mangalist/$cleanUsername/load.json?offset=$offset&status=7"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "application/json, text/plain, */*")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        if (response.code == 404) {
                            return@withContext Result.failure(Exception("MyAnimeList manga listesi bulunamadı: $cleanUsername"))
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
                        val mangaId = obj.optInt("manga_id")
                        val title = obj.optString("manga_title", "Bilinmeyen Manga")
                        val titleEng = obj.optString("manga_english").takeIf { it.isNotBlank() }
                        val numChapters = obj.optInt("manga_num_chapters", 0)
                        val readChapters = obj.optInt("num_read_chapters", 0)
                        val score = obj.optDouble("score", 0.0).toFloat()
                        val rawStatus = obj.optInt("status", 1)
                        val status = when (rawStatus) {
                            1 -> AnimeWatchStatus.WATCHING // Reading
                            2 -> AnimeWatchStatus.COMPLETED
                            3 -> AnimeWatchStatus.ON_HOLD
                            4 -> AnimeWatchStatus.DROPPED
                            6 -> AnimeWatchStatus.PLAN_TO_WATCH // Plan to Read
                            else -> AnimeWatchStatus.WATCHING
                        }
                        val imagePath = obj.optString("manga_image_path", "")
                        val mediaType = obj.optString("manga_media_type_string", "Manga")

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
                                id = "mal_manga_$mangaId",
                                malId = mangaId,
                                title = title,
                                titleEnglish = titleEng,
                                imageUrl = imagePath,
                                watchedEpisodes = readChapters,
                                totalEpisodes = numChapters,
                                malWatchedEpisodes = readChapters,
                                score = score,
                                status = status,
                                mediaType = mediaType,
                                category = MediaTypeCategory.MANGA,
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
        searchMedia(query, isManga = false)
    }

    suspend fun searchManga(query: String): Result<List<AnimeSearchItem>> = withContext(Dispatchers.IO) {
        searchMedia(query, isManga = true)
    }

    private suspend fun searchMedia(query: String, isManga: Boolean): Result<List<AnimeSearchItem>> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return@withContext Result.success(emptyList())
        }

        // 1. Birincil Arama: AniList GraphQL (Yüksek hız, sıfır 504 hatası, direkt MyAnimeList ID uyumluluğu)
        try {
            val aniListResults = searchAniList(trimmed, isManga)
            if (aniListResults.isNotEmpty()) {
                return@withContext Result.success(aniListResults)
            }
        } catch (_: Exception) {
            // AniList geçici olarak erişilemezse Jikan fallback'e geç
        }

        // 2. İkincil Arama (Yedek): Jikan REST API (sfw=true kaldırıldı, HTTP 504 timeout engellendi)
        try {
            val jikanResults = searchJikan(trimmed, isManga)
            if (jikanResults.isNotEmpty()) {
                return@withContext Result.success(jikanResults)
            }
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(Exception("Arama servisi yanıt vermedi, lütfen tekrar deneyin."))
        }
    }

    private fun searchAniList(query: String, isManga: Boolean): List<AnimeSearchItem> {
        val mediaType = if (isManga) "MANGA" else "ANIME"
        val graphQLQuery = """
            query (${'$'}search: String, ${'$'}type: MediaType) {
              Page(page: 1, perPage: 20) {
                media(search: ${'$'}search, type: ${'$'}type) {
                  id
                  idMal
                  title {
                    romaji
                    english
                    native
                  }
                  coverImage {
                    large
                    medium
                  }
                  episodes
                  chapters
                  format
                  averageScore
                  genres
                  description
                }
              }
            }
        """.trimIndent()

        val variables = JSONObject().apply {
            put("search", query)
            put("type", mediaType)
        }
        val requestJson = JSONObject().apply {
            put("query", graphQLQuery)
            put("variables", variables)
        }

        val jsonType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(jsonType)

        val request = Request.Builder()
            .url("https://graphql.anilist.co")
            .header("User-Agent", USER_AGENT)
            .header("Accept", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return emptyList()
            }
            val bodyString = response.body?.string() ?: return emptyList()
            val root = JSONObject(bodyString)
            val dataObj = root.optJSONObject("data") ?: return emptyList()
            val pageObj = dataObj.optJSONObject("Page") ?: return emptyList()
            val mediaArr = pageObj.optJSONArray("media") ?: return emptyList()

            val results = mutableListOf<AnimeSearchItem>()
            val htmlTagRegex = Regex("<[^>]*>")

            for (i in 0 until mediaArr.length()) {
                val media = mediaArr.getJSONObject(i)
                val malId = media.optInt("idMal", 0).let { if (it > 0) it else media.optInt("id", 0) }
                if (malId <= 0) continue

                val titleObj = media.optJSONObject("title")
                val titleEng = titleObj?.optString("english")?.takeIf { it.isNotBlank() }
                val titleRomaji = titleObj?.optString("romaji")?.takeIf { it.isNotBlank() }
                val titleNative = titleObj?.optString("native")?.takeIf { it.isNotBlank() }
                val title = titleEng ?: titleRomaji ?: titleNative ?: "İsimsiz"

                val coverObj = media.optJSONObject("coverImage")
                val imageUrl = coverObj?.optString("large") ?: coverObj?.optString("medium") ?: ""

                val totalUnits = if (isManga) media.optInt("chapters", 0) else media.optInt("episodes", 0)
                val rawScore = media.optDouble("averageScore", 0.0)
                val score = (rawScore / 10.0).toFloat().coerceIn(0f, 10f)

                val format = media.optString("format", if (isManga) "Manga" else "TV")
                val rawDesc = media.optString("description", "")
                val cleanDesc = rawDesc.replace(htmlTagRegex, "").trim()

                val genresList = mutableListOf<String>()
                val genresArr = media.optJSONArray("genres")
                if (genresArr != null) {
                    for (g in 0 until genresArr.length()) {
                        val name = genresArr.optString(g)
                        if (!name.isNullOrBlank()) genresList.add(name)
                    }
                }

                results.add(
                    AnimeSearchItem(
                        malId = malId,
                        title = title,
                        titleEnglish = titleEng ?: titleRomaji,
                        imageUrl = imageUrl,
                        totalEpisodes = totalUnits,
                        score = score,
                        mediaType = format,
                        synopsis = cleanDesc,
                        genres = genresList,
                        category = if (isManga) MediaTypeCategory.MANGA else MediaTypeCategory.ANIME
                    )
                )
            }
            return results
        }
    }

    private fun searchJikan(query: String, isManga: Boolean): List<AnimeSearchItem> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val endpoint = if (isManga) "manga" else "anime"
        // sfw filtresi Jikan sunucularında 504 Gateway Timeout'a yol açtığı için kaldırıldı
        val url = "https://api.jikan.moe/v4/$endpoint?q=$encodedQuery&limit=15"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            val bodyString = response.body?.string() ?: "{}"
            val root = JSONObject(bodyString)
            val dataArr = root.optJSONArray("data") ?: JSONArray()
            val results = mutableListOf<AnimeSearchItem>()

            for (i in 0 until dataArr.length()) {
                val obj = dataArr.getJSONObject(i)
                val malId = obj.optInt("mal_id")
                val title = obj.optString("title", "İsimsiz")
                val titleEng = obj.optString("title_english").takeIf { it.isNotBlank() }
                val totalUnits = if (isManga) obj.optInt("chapters", 0) else obj.optInt("episodes", 0)
                val score = obj.optDouble("score", 0.0).toFloat()
                val mediaType = obj.optString("type", if (isManga) "Manga" else "TV")
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
                        totalEpisodes = totalUnits,
                        score = score,
                        mediaType = mediaType,
                        synopsis = synopsis,
                        genres = genresList,
                        category = if (isManga) MediaTypeCategory.MANGA else MediaTypeCategory.ANIME
                    )
                )
            }
            return results
        }
    }
}
