package com.example.data.model

import org.json.JSONObject

data class TopicGenre(
    val title: String,
    val emoji: String,
    val description: String = ""
)

data class CustomTopicItem(
    val id: String,
    val subItemId: String,
    val genreTitle: String,
    val genreEmoji: String,
    val title: String,
    val description: String = "",
    val practiceTask: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toTopicCheckItem(): TopicCheckItem {
        return TopicCheckItem(
            id = id,
            title = title,
            description = description,
            isCompleted = false,
            practiceTask = practiceTask
        )
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("subItemId", subItemId)
            put("genreTitle", genreTitle)
            put("genreEmoji", genreEmoji)
            put("title", title)
            put("description", description)
            put("practiceTask", practiceTask)
            put("createdAt", createdAt)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): CustomTopicItem {
            return CustomTopicItem(
                id = json.optString("id", ""),
                subItemId = json.optString("subItemId", ""),
                genreTitle = json.optString("genreTitle", ""),
                genreEmoji = json.optString("genreEmoji", "📌"),
                title = json.optString("title", ""),
                description = json.optString("description", ""),
                practiceTask = json.optString("practiceTask", ""),
                createdAt = json.optLong("createdAt", System.currentTimeMillis())
            )
        }
    }
}
