package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RoadmapProgressHelper
import com.example.data.model.anime.AnimeItem
import com.example.data.model.anime.AnimeWatchStatus
import com.example.data.model.anime.MediaTypeCategory
import com.example.data.repository.AnimeRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AnimeRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("winter_arc_anime_storage", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        AnimeRepository.resetForTesting()
    }

    @Test
    fun verifyEmptyByDefaultWhenNoSeed() {
        val list = AnimeRepository.getAnimeList(context)
        assertEquals("Mock veriler temizlendiğinden liste boş başlamalı", 0, list.size)
    }

    @Test
    fun verifyAddCustomAnime() {
        val customAnime = AnimeItem(
            id = "mal_1535",
            malId = 1535,
            title = "Death Note",
            titleEnglish = "Death Note",
            imageUrl = "https://cdn.myanimelist.net/images/anime/9/9453.jpg",
            watchedEpisodes = 15,
            totalEpisodes = 37,
            malWatchedEpisodes = 15,
            score = 9.0f,
            status = AnimeWatchStatus.WATCHING,
            mediaType = "TV",
            category = MediaTypeCategory.ANIME,
            genres = listOf("Suspense", "Supernatural"),
            notes = "Kira vs L",
            updatedAt = System.currentTimeMillis()
        )

        AnimeRepository.addOrUpdateAnime(context, customAnime)

        val list = AnimeRepository.getAnimeList(context)
        assertEquals(1, list.size)
        val item = list.first()
        assertEquals("Death Note", item.displayTitle)
        assertEquals(15, item.watchedEpisodes)
        assertEquals(15, item.malWatchedEpisodes)
        assertFalse("Başlangıçta MAL ile yerel senkronize olmalı", item.isOutOfSync)
    }

    @Test
    fun verifyIncrementEpisodeAndOutOfSync() {
        val customAnime = AnimeItem(
            id = "mal_51009",
            malId = 51009,
            title = "Jujutsu Kaisen 2nd Season",
            titleEnglish = "Jujutsu Kaisen Season 2",
            imageUrl = "https://cdn.myanimelist.net/images/anime/1792/138022.jpg",
            watchedEpisodes = 18,
            totalEpisodes = 23,
            malWatchedEpisodes = 18,
            score = 8.8f,
            status = AnimeWatchStatus.WATCHING,
            category = MediaTypeCategory.ANIME,
            updatedAt = System.currentTimeMillis()
        )

        AnimeRepository.addOrUpdateAnime(context, customAnime)

        // Increment episode by 1 locally
        AnimeRepository.incrementEpisode(context, customAnime.id, 1)

        val updated = AnimeRepository.getAnimeList(context).first { it.id == customAnime.id }
        assertEquals(19, updated.watchedEpisodes)
        assertEquals(18, updated.malWatchedEpisodes)
        assertTrue("Yerel bölüm artırıldığında MAL ile fark algılanmalı (isOutOfSync = true)", updated.isOutOfSync)
        assertEquals(1, updated.syncDiff)

        // Increment to 23 (completion)
        AnimeRepository.incrementEpisode(context, customAnime.id, 4)
        val completed = AnimeRepository.getAnimeList(context).first { it.id == customAnime.id }
        assertEquals(23, completed.watchedEpisodes)
        assertEquals(AnimeWatchStatus.COMPLETED, completed.status)
        assertTrue(completed.isOutOfSync)
        assertEquals(5, completed.syncDiff)
    }

    @Test
    fun verifyDecrementEpisode() {
        val item = AnimeItem(
            id = "mal_100",
            malId = 100,
            title = "Test Anime",
            watchedEpisodes = 5,
            totalEpisodes = 12,
            malWatchedEpisodes = 5
        )
        AnimeRepository.addOrUpdateAnime(context, item)

        AnimeRepository.decrementEpisode(context, item.id, 2)
        var updated = AnimeRepository.getAnimeList(context).first { it.id == item.id }
        assertEquals(3, updated.watchedEpisodes)

        AnimeRepository.decrementEpisode(context, item.id, 10)
        updated = AnimeRepository.getAnimeList(context).first { it.id == item.id }
        assertEquals(0, updated.watchedEpisodes)
    }

    @Test
    fun verifyMangaItemSupport() {
        val mangaItem = AnimeItem(
            id = "mal_manga_35243",
            malId = 35243,
            title = "Haikyuu!!",
            titleEnglish = "Haikyu!!",
            imageUrl = "https://cdn.myanimelist.net/images/manga/2/1792.jpg",
            watchedEpisodes = 407,
            totalEpisodes = 407,
            malWatchedEpisodes = 407,
            score = 10.0f,
            status = AnimeWatchStatus.COMPLETED,
            mediaType = "Manga",
            category = MediaTypeCategory.MANGA,
            genres = listOf("Sports", "Award Winning"),
            updatedAt = System.currentTimeMillis()
        )

        AnimeRepository.addOrUpdateAnime(context, mangaItem)

        val list = AnimeRepository.getAnimeList(context)
        val retrieved = list.first { it.id == "mal_manga_35243" }
        assertEquals(MediaTypeCategory.MANGA, retrieved.category)
        assertEquals(407, retrieved.watchedEpisodes)
        assertEquals("Tamamlandı", retrieved.status.getLabel(retrieved.category))
    }

    @Test
    fun verifyComputeStatsWithCategory() {
        val anime1 = AnimeItem(
            id = "a1",
            title = "A1",
            watchedEpisodes = 12,
            totalEpisodes = 12,
            status = AnimeWatchStatus.COMPLETED,
            category = MediaTypeCategory.ANIME
        )
        val anime2 = AnimeItem(
            id = "a2",
            title = "A2",
            watchedEpisodes = 5,
            totalEpisodes = 24,
            status = AnimeWatchStatus.WATCHING,
            category = MediaTypeCategory.ANIME
        )
        val manga1 = AnimeItem(
            id = "m1",
            title = "M1",
            watchedEpisodes = 50,
            totalEpisodes = 100,
            status = AnimeWatchStatus.WATCHING,
            category = MediaTypeCategory.MANGA
        )

        AnimeRepository.addOrUpdateAnime(context, anime1)
        AnimeRepository.addOrUpdateAnime(context, anime2)
        AnimeRepository.addOrUpdateAnime(context, manga1)

        val allList = AnimeRepository.getAnimeList(context)
        val animeStats = AnimeRepository.computeStats(allList, MediaTypeCategory.ANIME)
        assertEquals(2, animeStats.totalCount)
        assertEquals(1, animeStats.inProgressCount)
        assertEquals(1, animeStats.completedCount)
        assertEquals(17, animeStats.totalWatchedUnits)

        val mangaStats = AnimeRepository.computeStats(allList, MediaTypeCategory.MANGA)
        assertEquals(1, mangaStats.totalCount)
        assertEquals(1, mangaStats.inProgressCount)
        assertEquals(0, mangaStats.completedCount)
        assertEquals(50, mangaStats.totalWatchedUnits)
    }

    @Test
    fun verifyRoadmapProgressHelper() {
        val prefs = context.getSharedPreferences(RoadmapProgressHelper.PREFS_ROADMAP, Context.MODE_PRIVATE)

        val item = AnimeItem(
            id = "a1",
            title = "A1",
            watchedEpisodes = 12,
            totalEpisodes = 12,
            status = AnimeWatchStatus.COMPLETED,
            category = MediaTypeCategory.ANIME
        )
        AnimeRepository.addOrUpdateAnime(context, item)

        val stats = RoadmapProgressHelper.getSubItemStats("sub_anime_manhwa", prefs)
        assertTrue(stats.totalCount >= 1)
        assertTrue(stats.completedCount >= 1)
        assertTrue(stats.progressFraction in 0f..1f)
    }
}
