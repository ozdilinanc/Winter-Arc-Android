package com.example

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CustomTopicItem
import com.example.data.repository.CustomTopicRepository
import com.example.data.model.RoadmapDataStore
import com.example.data.model.RoadmapProgressHelper
import com.example.data.model.TopicProgressState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CustomTopicRepositoryTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences(CustomTopicRepository.PREFS_CUSTOM_TOPICS, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun verifySampleItemsCountIsExactlyThree() {
        val booksRoadmap = RoadmapDataStore.allRoadmaps["sub_reading_books"]
        assertNotNull(booksRoadmap)
        val bookItems = booksRoadmap!!.sections.flatMap { it.items }
        assertEquals("Kitap Dünyası must contain exactly 3 sample items", 3, bookItems.size)

        val cardsRoadmap = RoadmapDataStore.allRoadmaps["sub_card_sleights"]
        assertNotNull(cardsRoadmap)
        val cardItems = cardsRoadmap!!.sections.flatMap { it.items }
        assertEquals("Kart Numaraları must contain exactly 3 sample items", 3, cardItems.size)

        val animeRoadmap = RoadmapDataStore.allRoadmaps["sub_anime_manhwa"]
        assertNotNull(animeRoadmap)
        val animeItems = animeRoadmap!!.sections.flatMap { it.items }
        assertEquals("Anime & Manhwa must contain exactly 3 sample items", 3, animeItems.size)
    }

    @Test
    fun verifyGenresCoverageAndCustomizability() {
        assertTrue(CustomTopicRepository.isCustomizable("sub_reading_books"))
        assertTrue(CustomTopicRepository.isCustomizable("sub_card_sleights"))
        assertTrue(CustomTopicRepository.isCustomizable("sub_anime_manhwa"))
        assertFalse(CustomTopicRepository.isCustomizable("sub_backend_dotnet"))

        val bookGenres = CustomTopicRepository.getGenresForSubItem("sub_reading_books")
        assertTrue("Book genres must have at least 10 categories", bookGenres.size >= 10)
        assertTrue(bookGenres.any { it.title.contains("Tarih") })
        assertTrue(bookGenres.any { it.title.contains("Felsefe") })
        assertTrue(bookGenres.any { it.title.contains("Roman") })
        assertTrue(bookGenres.any { it.title.contains("Bilim Kurgu") })

        val cardGenres = CustomTopicRepository.getGenresForSubItem("sub_card_sleights")
        assertTrue("Card sleight genres must have at least 8 categories", cardGenres.size >= 8)
        assertTrue(cardGenres.any { it.title.contains("Sleight of Hand") })
        assertTrue(cardGenres.any { it.title.contains("Mentalizm") })
        assertTrue(cardGenres.any { it.title.contains("Flourish") })

        val animeGenres = CustomTopicRepository.getGenresForSubItem("sub_anime_manhwa")
        assertTrue("Anime/Manhwa genres must have at least 12 categories", animeGenres.size >= 12)
        assertTrue(animeGenres.any { it.title.contains("Shounen") })
        assertTrue(animeGenres.any { it.title.contains("Manhwa") })
        assertTrue(animeGenres.any { it.title.contains("Murim") })
        assertTrue(animeGenres.any { it.title.contains("Seinen") })
    }

    @Test
    fun testAddCustomBookAndPersistence() {
        val initialRoadmap = CustomTopicRepository.getEffectiveRoadmap(prefs, "sub_reading_books")
        assertNotNull(initialRoadmap)
        val initialCount = initialRoadmap!!.sections.sumOf { it.items.size }
        assertEquals(3, initialCount)

        val newBook = CustomTopicItem(
            id = "custom_bk_dune_1",
            subItemId = "sub_reading_books",
            genreTitle = "Bilim Kurgu & Fantastik",
            genreEmoji = "🚀",
            title = "Frank Herbert - Dune",
            description = "Çöl gezegeni Arrakis, baharat savaşı ve Paul Atreides'in mesih yolculuğu.",
            practiceTask = "Günde 30 sayfa oku."
        )

        CustomTopicRepository.addCustomItem(prefs, newBook)

        // Verify stored in custom items
        val customItems = CustomTopicRepository.getCustomItems(prefs, "sub_reading_books")
        assertEquals(1, customItems.size)
        assertEquals("Frank Herbert - Dune", customItems[0].title)

        // Verify effective roadmap merges the new genre section
        val updatedRoadmap = CustomTopicRepository.getEffectiveRoadmap(prefs, "sub_reading_books")
        assertNotNull(updatedRoadmap)
        val totalItems = updatedRoadmap!!.sections.sumOf { it.items.size }
        assertEquals(4, totalItems)

        val duneSection = updatedRoadmap.sections.find { it.title == "Bilim Kurgu & Fantastik" }
        assertNotNull("Section for Bilim Kurgu & Fantastik should be created", duneSection)
        assertTrue(duneSection!!.items.any { it.id == "custom_bk_dune_1" })
    }

    @Test
    fun testDeleteCustomItem() {
        val newAnime = CustomTopicItem(
            id = "custom_ani_steins_gate",
            subItemId = "sub_anime_manhwa",
            genreTitle = "Bilim Kurgu, Cyberpunk & Mecha",
            genreEmoji = "🤖",
            title = "Steins;Gate",
            description = "Zaman yolculuğu, mikrodalga fırın ve dünya çizgileri paradoksu.",
            practiceTask = "24 bölümü tamamla."
        )

        CustomTopicRepository.addCustomItem(prefs, newAnime)
        assertEquals(1, CustomTopicRepository.getCustomItems(prefs, "sub_anime_manhwa").size)

        // Now delete it
        CustomTopicRepository.deleteItem(prefs, "sub_anime_manhwa", "custom_ani_steins_gate")
        assertEquals(0, CustomTopicRepository.getCustomItems(prefs, "sub_anime_manhwa").size)

        val roadmap = CustomTopicRepository.getEffectiveRoadmap(prefs, "sub_anime_manhwa")
        assertNotNull(roadmap)
        val hasDeletedItem = roadmap!!.sections.flatMap { it.items }.any { it.id == "custom_ani_steins_gate" }
        assertFalse(hasDeletedItem)
    }

    @Test
    fun testDeleteSampleSeedItem() {
        // "crd_grips" is one of the initial seed items
        val initialRoadmap = CustomTopicRepository.getEffectiveRoadmap(prefs, "sub_card_sleights")
        assertTrue(initialRoadmap!!.sections.flatMap { it.items }.any { it.id == "crd_grips" })

        // Delete the seed item
        CustomTopicRepository.deleteItem(prefs, "sub_card_sleights", "crd_grips")

        val updatedRoadmap = CustomTopicRepository.getEffectiveRoadmap(prefs, "sub_card_sleights")
        assertFalse(updatedRoadmap!!.sections.flatMap { it.items }.any { it.id == "crd_grips" })
        assertEquals(2, updatedRoadmap.sections.sumOf { it.items.size })
    }

    @Test
    fun testDynamicProgressCalculationWithCustomItems() {
        val initialStats = RoadmapProgressHelper.getSubItemStats("sub_reading_books", prefs)
        assertEquals(3, initialStats.totalCount)
        assertEquals(0, initialStats.completedCount)
        assertEquals(0, initialStats.progressPercent)

        // Add 1 custom book
        val customBook = CustomTopicItem(
            id = "custom_bk_test_1",
            subItemId = "sub_reading_books",
            genreTitle = "Felsefe & Stoa",
            genreEmoji = "🗿",
            title = "Epiktetos - Söylevler",
            description = "Kontrol alanı felsefesi",
            practiceTask = "Her sabah 1 söylev oku."
        )
        CustomTopicRepository.addCustomItem(prefs, customBook)

        // Total should now be 4
        val statsAfterAdd = RoadmapProgressHelper.getSubItemStats("sub_reading_books", prefs)
        assertEquals(4, statsAfterAdd.totalCount)

        // Complete 2 items (1 seed, 1 custom)
        prefs.edit()
            .putString("status_bk_hist_sapiens", TopicProgressState.COMPLETED.key)
            .putString("status_custom_bk_test_1", TopicProgressState.COMPLETED.key)
            .commit()

        val statsAfterCompletion = RoadmapProgressHelper.getSubItemStats("sub_reading_books", prefs)
        assertEquals(4, statsAfterCompletion.totalCount)
        assertEquals(2, statsAfterCompletion.completedCount)
        assertEquals(50, statsAfterCompletion.progressPercent)
    }
}
