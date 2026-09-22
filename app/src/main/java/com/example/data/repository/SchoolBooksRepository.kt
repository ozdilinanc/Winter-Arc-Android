package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.school.BookReadingStatus
import com.example.data.model.school.TrackedBook
import com.example.data.seed.defaultTrackedBooks
import org.json.JSONArray
import org.json.JSONObject

object SchoolBooksRepository {

    private const val PREFS_NAME = "winter_arc_school_books"
    private const val KEY_BOOKS_PROGRESS = "books_progress_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Synchronized
    fun getBooks(context: Context): List<TrackedBook> {
        val defaults = defaultTrackedBooks()
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_BOOKS_PROGRESS, null) ?: return defaults

        return try {
            val rootObj = JSONObject(jsonStr)
            defaults.map { book ->
                val bookObj = rootObj.optJSONObject(book.id)
                if (bookObj != null) {
                    val currentPage = bookObj.optInt("currentPage", book.currentPage)
                    val statusStr = bookObj.optString("status", book.status.name)
                    val status = try {
                        BookReadingStatus.valueOf(statusStr)
                    } catch (_: Exception) {
                        book.status
                    }
                    val personalNotes = bookObj.optString("personalNotes", book.personalNotes)
                    val completedIdsSet = mutableSetOf<String>()
                    val completedArr = bookObj.optJSONArray("completedChapterIds")
                    if (completedArr != null) {
                        for (i in 0 until completedArr.length()) {
                            completedIdsSet.add(completedArr.getString(i))
                        }
                    }

                    val updatedSections = book.sections.map { section ->
                        val updatedChapters = section.chapters.map { chapter ->
                            if (completedIdsSet.contains(chapter.id)) {
                                chapter.copy(isCompleted = true)
                            } else {
                                chapter.copy(isCompleted = false)
                            }
                        }
                        section.copy(chapters = updatedChapters)
                    }

                    book.copy(
                        currentPage = currentPage.coerceIn(0, book.totalPages),
                        status = status,
                        personalNotes = personalNotes,
                        sections = updatedSections
                    )
                } else {
                    book
                }
            }
        } catch (_: Exception) {
            defaults
        }
    }

    @Synchronized
    fun saveBooks(context: Context, books: List<TrackedBook>) {
        try {
            val rootObj = JSONObject()
            books.forEach { book ->
                val bookObj = JSONObject()
                bookObj.put("currentPage", book.currentPage)
                bookObj.put("status", book.status.name)
                bookObj.put("personalNotes", book.personalNotes)

                val completedArr = JSONArray()
                book.sections.forEach { sec ->
                    sec.chapters.forEach { ch ->
                        if (ch.isCompleted) {
                            completedArr.put(ch.id)
                        }
                    }
                }
                bookObj.put("completedChapterIds", completedArr)

                rootObj.put(book.id, bookObj)
            }

            getPrefs(context).edit().putString(KEY_BOOKS_PROGRESS, rootObj.toString()).apply()
        } catch (_: Exception) {
            // Silently fail or log in debug
        }
    }
}
