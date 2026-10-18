package com.example.data.model

/** Reviewed against BTK's course pages and official career guide on 2026-10-19. */
data class BtkCourse(val id: String, val title: String, val url: String, val group: String)

object BtkCourseCatalog {
    private const val GUIDE = "https://kariyerrehberi.btkakademi.gov.tr/gelisim_programlari/yazilim-gelistirme.html"
    val courses = listOf(
        BtkCourse("btk_dotnet_webapi", "Asp.Net Core Web API", "https://www.btkakademi.gov.tr/portal/course/asp-net-core-web-api-23993", "Backend"),
        BtkCourse("btk_design_patterns", "Yazılım Tasarım Desenleri", "https://www.btkakademi.gov.tr/portal/course/yazilim-tasarim-desenleri-12150", "Backend"),
        BtkCourse("btk_kotlin_core", "Kotlin Programlama Dili", GUIDE, "Android"),
        BtkCourse("btk_android_basic", "Kotlin ile Android Mobil Uygulama Geliştirme Temelleri", "https://www.btkakademi.gov.tr/portal/tr/course/30370", "Android"),
        BtkCourse("btk_android_adv", "Kotlin ile Android Mobil Uygulama Geliştirme (İleri Seviye)", GUIDE, "Android"),
        BtkCourse("btk_sql_postgres", "PostgreSQL için SQL Dili", "https://www.btkakademi.gov.tr/portal/en/catalog?categoryId=3204", "Destekleyici"),
        BtkCourse("btk_docker_basics", "Docker Temelleri", "https://www.btkakademi.gov.tr/portal/course/docker-temelleri-21907", "Destekleyici"),
        BtkCourse("btk_api_testing", "API ve API Testi", "https://www.btkakademi.gov.tr/portal/course/api-ve-api-testi-12025", "Destekleyici"),
        BtkCourse("btk_software_test_intro", "Yazılım Testine Giriş", "https://www.btkakademi.gov.tr/portal/course/yazilim-testine-giris-28883", "Destekleyici")
    )
    val roadmap = SubItemRoadmap(
        "sub_btk_akademi", "BTK kursları", "Backend, Android ve destekleyici eğitimler", "🎓",
        "Kendi hızında", "İlgilendiğin kursları takip et. Eğitim ve sertifika koşullarını BTK sayfasından kontrol edebilirsin.",
        courses.groupBy { it.group }.map { (group, entries) ->
            TopicSection(group, "", entries.map { TopicCheckItem(it.id, it.title) })
        }
    )
}
