package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CustomTopicItem
import com.example.data.model.RoadmapDataStore
import com.example.data.model.SubItemRoadmap
import com.example.data.model.TopicCheckItem
import com.example.data.model.TopicGenre
import com.example.data.model.TopicSection
import org.json.JSONArray
import org.json.JSONObject

object CustomTopicRepository {

    const val PREFS_CUSTOM_TOPICS = "winter_arc_roadmap_progress"
    private const val KEY_CUSTOM_ITEMS_PREFIX = "custom_items_"
    private const val KEY_DELETED_ITEMS_PREFIX = "deleted_items_"

    val customizableSubItemIds = setOf(
        "sub_reading_books",
        "sub_card_sleights",
        "sub_anime_manhwa"
    )

    fun isCustomizable(subItemId: String): Boolean {
        return subItemId in customizableSubItemIds
    }

    fun getGenresForSubItem(subItemId: String): List<TopicGenre> = when (subItemId) {
        "sub_reading_books" -> listOf(
            TopicGenre("Tarih & Biyografi", "🏛️", "Tarihi olaylar, medeniyetler ve vizyoner lider hayatları"),
            TopicGenre("Kişisel Gelişim & Alışkanlıklar", "🌱", "Verimlilik, zihinsel disiplin ve alışkanlık inşası"),
            TopicGenre("Felsefe & Stoa", "🗿", "Stoacılık, varoluşçuluk ve kadim düşünce ekolleri"),
            TopicGenre("Roman & Dünya Klasikleri", "📖", "Dostoyevski, Tolstoy, Orwell gibi edebi başyapıtlar"),
            TopicGenre("Bilim Kurgu & Fantastik", "🚀", "Distopyalar, uzay operaları ve fütüristik kurgular"),
            TopicGenre("Bilim & Popüler Bilim", "🔬", "Fizik, astrofizik, biyoloji ve evrenin sırları"),
            TopicGenre("Finans, Ekonomi & İş Dünyası", "📈", "Yatırım, finansal özgürlük, girişimcilik ve şirket hikayeleri"),
            TopicGenre("Psikoloji & Zihin", "🧠", "Davranış bilimleri, bilişsel psikoloji ve insan doğası"),
            TopicGenre("Sosyoloji & Toplum", "🌍", "Toplumsal yapılar, insan ilişkileri ve antropoloji"),
            TopicGenre("Sanat, Tasarım & Kültür", "🎨", "Estetik, mimari, sinema ve görsel kültür"),
            TopicGenre("Sağlık, Beden & Mindfulness", "🧘", "Beslenme, uyku kalitesi, meditasyon ve beden farkındalığı"),
            TopicGenre("Genel Kültür & Notlar", "💡", "Farklı disiplinlerden denemeler, derlemeler ve notlar")
        )
        "sub_card_sleights" -> listOf(
            TopicGenre("Temel Tutuşlar & Mekanikler", "🖐️", "Mechanic's grip, biddle grip, pinky break ve deste kontrolü"),
            TopicGenre("Sleight of Hand & Kontroller", "✨", "Double lift, pass, palm, false cuts ve gizli manevralar"),
            TopicGenre("Klasik Numaralar & Rutinler", "🎩", "Ambitious card, triumph, sandwich effect ve efsanevi rutinler"),
            TopicGenre("Misdirection & Sahne Sunumu", "🎭", "Dikkat yönetimi, beden dili ve patter (hikaye) anlatımı"),
            TopicGenre("Mentalizm & Zihin Okuma", "🧠", "Düşünce okuma, önsezi ve psikolojik zorlama (forcing)"),
            TopicGenre("Flourish & Cardistry", "🤹", "Görsel kesmeler, şelale (waterfall), yay açma ve parmak akrobasisi"),
            TopicGenre("Self-Working (Matematiksel)", "🔢", "Parmak hilesi gerektirmeyen kusursuz mantık ve matematik numaraları"),
            TopicGenre("Hileli Deste (Gaff & Gimmick)", "🃏", "Stripper deck, Svengali, double backer ve özel kartlar"),
            TopicGenre("Hızlı & Sokak İllüzyonları", "⚡", "Hemen her yerde yapılabilen impromptu görsel efektler"),
            TopicGenre("Özgün Rutinler & Doğaçlama", "💡", "Geliştirdiğin özgün numaralar, kurtarma hamleleri ve fikirler")
        )
        "sub_anime_manhwa" -> listOf(
            TopicGenre("Kült & Başyapıt Anime", "🍿", "Tarihe geçen, kurgusuyla sarsan kült anime yapımları"),
            TopicGenre("Kült & Başyapıt Manhwa", "⚔️", "Solo Leveling, ORV gibi küresel fenomen webtoon'lar"),
            TopicGenre("Shounen & Yüksek Aksiyon", "🔥", "Dövüşler, güçlenmeler, dostluk ve adrenalin dolu seriler"),
            TopicGenre("Seinen & Psikolojik Gerilim", "🧠", "Yetişkin temalar, zeka savaşları, derin karakter arkları"),
            TopicGenre("Murim & Dövüş Sanatları", "🥋", "Kadim Çin/Kore kılıç sanatları, qi enerjisi ve tarikatlar"),
            TopicGenre("Leveling, Zindan & Avcı", "🏹", "Sistem arayüzleri, E'den S seviyeye yükseliş ve canavar zindanları"),
            TopicGenre("Isekai & Reenkarnasyon", "🌀", "Farklı bir dünyada veya geçmişte yeniden doğma temaları"),
            TopicGenre("Fantezi, Macera & Büyü", "🔮", "Kılıç ve büyü evrenleri, elf/cüce diyarları ve keşifler"),
            TopicGenre("Bilim Kurgu, Cyberpunk & Mecha", "🤖", "Gelecek teknolojileri, distopik şehirler ve robotlar"),
            TopicGenre("Gizem, Dedektiflik & Doğaüstü", "🕵️", "Sır perdeleri, dedektiflik, lanetler ve gerilim"),
            TopicGenre("Slice of Life & Dram", "☕", "Günlük hayatın huzuru, duygusal derinlik ve samimiyet"),
            TopicGenre("Romantizm & Rom-Com", "🌸", "Aşk, mizah, okul hayatı ve tatlı ilişkiler"),
            TopicGenre("Spor, Hırs & Rekabet", "⚽", "Futbol, basketbol, voleybol gibi takım ruhu ve zirve mücadelesi"),
            TopicGenre("Anime Filmleri & Mini Seriler", "🎬", "Ghibli, Shinkai başyapıtları ve tek sezonluk vurucu işler")
        )
        else -> emptyList()
    }

    fun getCustomItems(prefs: SharedPreferences, subItemId: String): List<CustomTopicItem> {
        val jsonStr = prefs.getString("$KEY_CUSTOM_ITEMS_PREFIX$subItemId", null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<CustomTopicItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(CustomTopicItem.fromJson(obj))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getCustomItems(context: Context, subItemId: String): List<CustomTopicItem> {
        val prefs = context.getSharedPreferences(PREFS_CUSTOM_TOPICS, Context.MODE_PRIVATE)
        return getCustomItems(prefs, subItemId)
    }

    fun addCustomItem(context: Context, item: CustomTopicItem) {
        val prefs = context.getSharedPreferences(PREFS_CUSTOM_TOPICS, Context.MODE_PRIVATE)
        val currentItems = getCustomItems(prefs, item.subItemId).toMutableList()
        currentItems.add(item)
        saveCustomItems(prefs, item.subItemId, currentItems)
    }

    fun addCustomItem(prefs: SharedPreferences, item: CustomTopicItem) {
        val currentItems = getCustomItems(prefs, item.subItemId).toMutableList()
        currentItems.add(item)
        saveCustomItems(prefs, item.subItemId, currentItems)
    }

    fun getDeletedItemIds(prefs: SharedPreferences, subItemId: String): Set<String> {
        return prefs.getStringSet("$KEY_DELETED_ITEMS_PREFIX$subItemId", emptySet()) ?: emptySet()
    }

    fun deleteItem(context: Context, subItemId: String, itemId: String) {
        val prefs = context.getSharedPreferences(PREFS_CUSTOM_TOPICS, Context.MODE_PRIVATE)
        deleteItem(prefs, subItemId, itemId)
    }

    fun deleteItem(prefs: SharedPreferences, subItemId: String, itemId: String) {
        // If it's a custom item, remove from custom list
        val currentCustomItems = getCustomItems(prefs, subItemId).toMutableList()
        val index = currentCustomItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            currentCustomItems.removeAt(index)
            saveCustomItems(prefs, subItemId, currentCustomItems)
        } else {
            // It's a seed item, mark as deleted
            val deletedIds = getDeletedItemIds(prefs, subItemId).toMutableSet()
            deletedIds.add(itemId)
            prefs.edit().putStringSet("$KEY_DELETED_ITEMS_PREFIX$subItemId", deletedIds).apply()
        }
        // Also remove status key if any
        prefs.edit().remove("status_$itemId").apply()
    }

    private fun saveCustomItems(prefs: SharedPreferences, subItemId: String, items: List<CustomTopicItem>) {
        val jsonArray = JSONArray()
        items.forEach { jsonArray.put(it.toJson()) }
        prefs.edit().putString("$KEY_CUSTOM_ITEMS_PREFIX$subItemId", jsonArray.toString()).apply()
    }

    fun getEffectiveRoadmap(context: Context, subItemId: String): SubItemRoadmap? {
        val prefs = context.getSharedPreferences(PREFS_CUSTOM_TOPICS, Context.MODE_PRIVATE)
        return getEffectiveRoadmap(prefs, subItemId)
    }

    fun getEffectiveRoadmap(prefs: SharedPreferences?, subItemId: String): SubItemRoadmap? {
        val baseRoadmap = RoadmapDataStore.allRoadmaps[subItemId] ?: return null
        if (!isCustomizable(subItemId) || prefs == null) {
            return baseRoadmap
        }

        val deletedIds = getDeletedItemIds(prefs, subItemId)
        val customItems = getCustomItems(prefs, subItemId)
        val genres = getGenresForSubItem(subItemId)

        // Group seed items by genre title (ignoring deleted ones)
        val seedItemsMap = mutableMapOf<String, MutableList<TopicCheckItem>>()
        baseRoadmap.sections.forEach { section ->
            val activeItems = section.items.filterNot { it.id in deletedIds }
            if (activeItems.isNotEmpty()) {
                seedItemsMap.getOrPut(section.title.uppercase()) { mutableListOf() }.addAll(activeItems)
            }
        }

        // Group custom items by genre
        val customItemsMap = mutableMapOf<String, MutableList<TopicCheckItem>>()
        customItems.forEach { custom ->
            if (custom.id !in deletedIds) {
                customItemsMap.getOrPut(custom.genreTitle.uppercase()) { mutableListOf() }.add(custom.toTopicCheckItem())
            }
        }

        // Build sections preserving genres list order
        val sections = mutableListOf<TopicSection>()
        val processedKeys = mutableSetOf<String>()

        genres.forEach { genre ->
            val key = genre.title.uppercase()
            processedKeys.add(key)
            val items = mutableListOf<TopicCheckItem>()
            seedItemsMap[key]?.let { items.addAll(it) }
            customItemsMap[key]?.let { items.addAll(it) }

            // If this genre has items, add it as a section
            if (items.isNotEmpty()) {
                sections.add(
                    TopicSection(
                        title = genre.title,
                        emoji = genre.emoji,
                        items = items
                    )
                )
            }
        }

        // Add any remaining sections from baseRoadmap that didn't match genres exactly
        baseRoadmap.sections.forEach { section ->
            val key = section.title.uppercase()
            if (key !in processedKeys) {
                val activeItems = section.items.filterNot { it.id in deletedIds }
                if (activeItems.isNotEmpty()) {
                    sections.add(section.copy(items = activeItems))
                }
            }
        }

        return baseRoadmap.copy(sections = sections)
    }
}
