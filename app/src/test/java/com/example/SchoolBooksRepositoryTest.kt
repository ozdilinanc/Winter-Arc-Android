package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SchoolBooksRepository
import com.example.data.model.school.BookReadingStatus
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SchoolBooksRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("winter_arc_school_books", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun verifyDefaultBooksLoaded() {
        val books = SchoolBooksRepository.getBooks(context)
        assertEquals("4 temel başucu kitabı yüklenmeli", 4, books.size)
        val ids = books.map { it.id }
        assertTrue(ids.contains("book_ostep"))
        assertTrue(ids.contains("book_ddia"))
        assertTrue(ids.contains("book_csapp"))
        assertTrue(ids.contains("book_networks"))
    }

    @Test
    fun verifySaveAndLoadBooksProgress() {
        val initialBooks = SchoolBooksRepository.getBooks(context)
        val ostep = initialBooks.first { it.id == "book_ostep" }

        // Complete first chapter and update page & status
        val updatedSections = ostep.sections.mapIndexed { sIndex, sec ->
            if (sIndex == 0) {
                sec.copy(chapters = sec.chapters.mapIndexed { cIndex, ch ->
                    if (cIndex == 0) ch.copy(isCompleted = true) else ch
                })
            } else sec
        }
        val modifiedOstep = ostep.copy(
            currentPage = 150,
            status = BookReadingStatus.READING,
            personalNotes = "Sanallaştırma prensipleri harika",
            sections = updatedSections
        )

        val modifiedList = initialBooks.map { if (it.id == "book_ostep") modifiedOstep else it }
        SchoolBooksRepository.saveBooks(context, modifiedList)

        // Reload from repo
        val reloaded = SchoolBooksRepository.getBooks(context)
        val reloadedOstep = reloaded.first { it.id == "book_ostep" }

        assertEquals(150, reloadedOstep.currentPage)
        assertEquals(BookReadingStatus.READING, reloadedOstep.status)
        assertEquals("Sanallaştırma prensipleri harika", reloadedOstep.personalNotes)
        assertTrue(reloadedOstep.sections[0].chapters[0].isCompleted)
        assertFalse(reloadedOstep.sections[0].chapters[1].isCompleted)
    }
}
