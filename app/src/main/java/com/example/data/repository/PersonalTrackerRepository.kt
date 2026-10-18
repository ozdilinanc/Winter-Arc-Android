package com.example.data.repository

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class TrackerKind(val title: String, val singular: String, val states: List<String>) {
    GITHUB("GitHub projeleri", "Proje", listOf("Fikir", "Geliştiriliyor", "Tamamlandı")),
    PLAY_STORE("Uygulamalarım", "Uygulama", listOf("Fikir", "Geliştiriliyor", "Yayında")),
    CARD_TRICKS("Kart numaraları", "Numara", listOf("Öğreniyorum", "Çalışıyorum", "Öğrendim"))
}

data class TrackerEntry(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val status: Int = 0,
    val url: String = "",
    val requirements: String = "",
    val notes: String = "",
    val discipline: String = "Mobil"
)

object PersonalTrackerRepository {
    const val PREFS_NAME = "winter_arc_personal_trackers"

    fun getEntries(prefs: SharedPreferences, kind: TrackerKind): List<TrackerEntry> {
        val raw = prefs.getString(kind.name, null) ?: return if (kind == TrackerKind.PLAY_STORE) {
            listOf(TrackerEntry(id = "app_clawssary", name = "Clawssary", status = 2,
                url = "https://play.google.com/store/apps/details?id=com.ozdilinanc.clawssary",
                notes = "İngilizce ve kelime çalıştığım uygulamam."))
        } else emptyList()
        // Keep malformed storage intact; never replace it with defaults on a failed read.
        return JSONArray(raw).let { array ->
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                TrackerEntry(
                    id = item.getString("id"), name = item.getString("name"),
                    status = item.optInt("status").coerceIn(kind.states.indices),
                    url = item.optString("url"), requirements = item.optString("requirements"),
                    notes = item.optString("notes"), discipline = item.optString("discipline", "Mobil")
                )
            }
        }
    }

    fun save(prefs: SharedPreferences, kind: TrackerKind, entry: TrackerEntry) {
        require(entry.name.isNotBlank())
        val current = getEntries(prefs, kind)
        val updated = if (current.any { it.id == entry.id }) current.map { if (it.id == entry.id) entry else it }
            else current + entry
        write(prefs, kind, updated)
    }

    fun delete(prefs: SharedPreferences, kind: TrackerKind, id: String) =
        write(prefs, kind, getEntries(prefs, kind).filterNot { it.id == id })

    private fun write(prefs: SharedPreferences, kind: TrackerKind, entries: List<TrackerEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(JSONObject().apply {
                put("id", entry.id); put("name", entry.name); put("status", entry.status)
                put("url", entry.url); put("requirements", entry.requirements)
                put("notes", entry.notes); put("discipline", entry.discipline)
            })
        }
        prefs.edit().putString(kind.name, array.toString()).apply()
    }
}
