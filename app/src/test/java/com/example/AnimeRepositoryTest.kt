package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RoadmapProgressHelper
import com.example.data.model.anime.AnimeItem
import com.example.data.model.anime.AnimeWatchStatus
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
    }

    @Test
    fun verifyDefaultSeedAnimesLoaded() {
        val animes = AnimeRepository.getAnimeList(context)
        assertTrue("En az 5 seed anime yüklenmeli", animes.size >= 5)

        val titles = animes.map { it.title }
        val englishTitles = animes.mapNotNull { it.titleEnglish }

        assertTrue(englishTitles.contains("Solo Leveling"))
        assertTrue(englishTitles.contains("Attack on Titan") || titles.contains("Shingeki no Kyojin"))
        assertTrue(englishTitles.contains("Frieren: Beyond Journey's End") || titles.contains("Sousou no Frieren"))
    }

    @Test
    fun verifyIncrementEpisodeAndAutoComplete() {
        val animes = AnimeRepository.getAnimeList(context)
        val jjk = animes.first { it.titleEnglish?.contains("Jujutsu Kaisen", ignoreCase = true) == true }

        val initialWatched = jjk.watchedEpisodes
        assertEquals(18, initialWatched)
        assertEquals(23, jjk.totalEpisodes)
        assertEquals(AnimeWatchStatus.WATCHING, jjk.status)

        // Increment by 1
        AnimeRepository.incrementEpisode(context, jjk.id, 1)
        var updated = AnimeRepository.getAnimeList(context).first { it.id == jjk.id }
        assertEquals(19, updated.watchedEpisodes)
        assertEquals(AnimeWatchStatus.WATCHING, updated.status)

        // Increment until total (19 + 4 = 23)
        AnimeRepository.incrementEpisode(context, jjk.id, 4)
        updated = AnimeRepository.getAnimeList(context).first { it.id == jjk.id }
        assertEquals(23, updated.watchedEpisodes)
        assertEquals("Toplam bölüme ulaşınca otomatik COMPLETED olmalı", AnimeWatchStatus.COMPLETED, updated.status)

        // Cannot exceed total
        AnimeRepository.incrementEpisode(context, jjk.id, 5)
        updated = AnimeRepository.getAnimeList(context).first { it.id == jjk.id }
        assertEquals(23, updated.watchedEpisodes)
    }

    @Test
    fun verifyDecrementEpisode() {
        val animes = AnimeRepository.getAnimeList(context)
        val bleach = animes.first { it.titleEnglish?.contains("Bleach", ignoreCase = true) == true }

        val initial = bleach.watchedEpisodes
        assertEquals(5, initial)

        AnimeRepository.decrementEpisode(context, bleach.id, 2)
        var updated = AnimeRepository.getAnimeList(context).first { it.id == bleach.id }
        assertEquals(3, updated.watchedEpisodes)

        // Decrement below 0 floors at 0
        AnimeRepository.decrementEpisode(context, bleach.id, 10)
        updated = AnimeRepository.getAnimeList(context).first { it.id == bleach.id }
        assertEquals(0, updated.watchedEpisodes)
    }

    @Test
    fun verifyUpdateStatusAndScore() {
        val animes = AnimeRepository.getAnimeList(context)
        val first = animes.first()

        AnimeRepository.updateStatus(context, first.id, AnimeWatchStatus.ON_HOLD)
        AnimeRepository.updateScore(context, first.id, 9.7f)

        val updated = AnimeRepository.getAnimeList(context).first { it.id == first.id }
        assertEquals(AnimeWatchStatus.ON_HOLD, updated.status)
        assertEquals(9.7f, updated.score, 0.01f)
    }

    @Test
    fun verifyAddCustomAnimeAndDelete() {
        val customAnime = AnimeItem(
            id = "custom_death_note",
            malId = 1535,
            title = "Death Note",
            titleEnglish = "Death Note",
            imageUrl = "https://cdn.myanimelist.net/images/anime/9/9453.jpg",
            watchedEpisodes = 15,
            totalEpisodes = 37,
            score = 9.2f,
            status = AnimeWatchStatus.WATCHING,
            mediaType = "TV",
            genres = listOf("Suspense", "Supernatural"),
            notes = "Kira vs L akıl oyunları",
            updatedAt = System.currentTimeMillis()
        )

        AnimeRepository.addOrUpdateAnime(context, customAnime)

        val listAfterAdd = AnimeRepository.getAnimeList(context)
        val added = listAfterAdd.find { it.id == "custom_death_note" }
        assertNotNull("Eklenen anime listede bulunmalı", added)
        assertEquals("Death Note", added?.title)

        AnimeRepository.deleteAnime(context, "custom_death_note")
        val listAfterDelete = AnimeRepository.getAnimeList(context)
        assertNull("Silinen anime listede olmamalı", listAfterDelete.find { it.id == "custom_death_note" })
    }

    @Test
    fun verifyComputeStats() {
        val animes = AnimeRepository.getAnimeList(context)
        val stats = AnimeRepository.computeStats(animes)

        assertEquals(animes.size, stats.totalAnime)
        assertEquals(animes.count { it.status == AnimeWatchStatus.WATCHING }, stats.watchingCount)
        assertEquals(animes.count { it.status == AnimeWatchStatus.COMPLETED }, stats.completedCount)
        assertEquals(animes.count { it.status == AnimeWatchStatus.PLAN_TO_WATCH }, stats.planToWatchCount)
        assertEquals(animes.sumOf { it.watchedEpisodes }, stats.totalWatchedEpisodes)
    }

    @Test
    fun verifyRoadmapProgressHelperForAnime() {
        val prefs = context.getSharedPreferences(RoadmapProgressHelper.PREFS_ROADMAP, Context.MODE_PRIVATE)
        // Ensure anime repo is loaded
        AnimeRepository.getAnimeList(context)

        val stats = RoadmapProgressHelper.getSubItemStats("sub_anime_manhwa", prefs)
        assertTrue("Anime sayısı 0'dan büyük olmalı", stats.totalCount > 0)
        assertTrue("Tamamlanan seri sayısı 0'dan büyük olmalı", stats.completedCount > 0)
        assertTrue("İlerleme oranı 0 ile 1 arasında olmalı", stats.progressFraction in 0f..1f)
    }
}
