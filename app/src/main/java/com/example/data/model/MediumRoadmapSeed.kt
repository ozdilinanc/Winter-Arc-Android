package com.example.data.model

object MediumRoadmapSeed {

    const val ARTICLE_TEMPLATE_MARKDOWN = """# [Konu Başlığı]

## 1. Nedir?
## 2. Neden Var?
## 3. Nasıl Çalışır?
## 4. Temel Kavramlar
## 5. Nasıl Kullanılır?
## 6. Örnek
## 7. Avantajları
## 8. Dezavantajları / Trade-off'ları
## 9. Yaygın Hatalar
## 10. Ne Zaman Kullanılmalı?
## 11. İlgili Konular
## 12. Özet"""

    val templateSteps = listOf(
        "1. Nedir?",
        "2. Neden Var?",
        "3. Nasıl Çalışır?",
        "4. Temel Kavramlar",
        "5. Nasıl Kullanılır?",
        "6. Örnek (Kod / Senaryo)",
        "7. Avantajları",
        "8. Dezavantajları / Trade-off'ları",
        "9. Yaygın Hatalar & Gotchas",
        "10. Ne Zaman Kullanılmalı?",
        "11. İlgili Konular & Bağlantılar",
        "12. Özet & Takeaways"
    )

    val mediumRoadmap = SubItemRoadmap(
        subItemId = "sub_medium",
        title = "Medium",
        subtitle = "Teknik Hafıza & Dijital Kütüphane (285 Makale)",
        emoji = "✍️",
        targetLevel = "Hedef: 285 Kapsamlı Teknik Yazı & Dijital Mühendislik Hafızası",
        overview = "Öğrenilen her kavramı, mimari kararı, hata çözümünü ve CS temelini kalıcı bir teknik hafızaya dönüştür. Her makale için 12 adımlık evrensel yazı iskeletini kullan.",
        sections = listOf(
            // ----------------------------------------------------
            // 1. PROGRAMLAMA TEMELLERİ & YAZILIM DÜŞÜNCESİ
            // ----------------------------------------------------
            TopicSection(
                title = "1. PROGRAMLAMA TEMELLERİ & YAZILIM DÜŞÜNCESİ",
                emoji = "🎓",
                items = listOf(
                    TopicCheckItem("med_art_1", "1. Programlama Dillerini Anlamak: Değişken, Bellek, Scope ve Type Sistemleri", "Değişkenler, bellek alanı, scope sınırları ve tip sistemlerinin çekirdek mantığı."),
                    TopicCheckItem("med_art_2", "2. Value Type ve Reference Type Mantığı", "Değer ve referans tipleri arasındaki temel davranışsal ve mimari farklar."),
                    TopicCheckItem("med_art_3", "3. Stack, Heap ve Bellekte Veri Yaşam Döngüsü", "Stack'in hızı, Heap'in dinamik tahsisi ve bellek yönetiminin altında yatanlar."),
                    TopicCheckItem("med_art_4", "4. Mutable ve Immutable Veri Yapıları", "Değiştirilebilirlik vs değişmezlik, thread safety ve yan etkilerden arınma."),
                    TopicCheckItem("med_art_5", "5. Compile Time ve Runtime Arasındaki Fark", "Derleme zamanı kontrolleri ile çalışma zamanı dinamiklerinin ayrımı."),
                    TopicCheckItem("med_art_6", "6. Static Typing ve Dynamic Typing", "Tip güvenliği, performans ve hata yakalama yaklaşımları."),
                    TopicCheckItem("med_art_7", "7. Garbage Collection Nasıl Çalışır?", "Otomatik bellek temizliği, mark-and-sweep ve jenerasyonel GC döngüleri."),
                    TopicCheckItem("med_art_8", "8. Memory Management Neden Önemlidir?", "Bellek sızıntıları, bellek şişmesi ve modern sistemlerde kaynak optimizasyonu."),
                    TopicCheckItem("med_art_9", "9. Concurrency ve Parallelism Nedir?", "Eşzamanlılık ve paralellik arasındaki farklar ve çoklu görev mimarisi."),
                    TopicCheckItem("med_art_10", "10. Synchronous ve Asynchronous Programming", "Bloklayan vs bloklamayan işlemler ve I/O bound operasyonların yönetimi."),
                    TopicCheckItem("med_art_11", "11. Exception Handling ve Hata Yönetimi", "try-catch maliyeti, hata fırlatma stratejileri ve Result Pattern."),
                    TopicCheckItem("med_art_12", "12. Clean Code: Okunabilir ve Sürdürülebilir Kod Yazmanın Temelleri", "İsimlendirme standartları, küçük fonksiyonlar ve kendini açıklayan kod."),
                    TopicCheckItem("med_art_13", "13. Debugging: Bir Programdaki Problemi Sistematik Olarak Bulmak", "Hata ayıklama metodolojisi, breakpoint'ler ve log analizi."),
                    TopicCheckItem("med_art_14", "14. Logging Nedir ve Uygulama Geliştirirken Nasıl Kullanılmalıdır?", "Log seviyeleri, audit trail ve log gürültüsünü önleme stratejileri.")
                )
            ),

            // ----------------------------------------------------
            // 2. C# / .NET
            // ----------------------------------------------------
            TopicSection(
                title = "2. C# / .NET",
                emoji = "💻",
                items = listOf(
                    TopicCheckItem("med_art_15", "15. Modern C#'a Giriş: OOP, Interface, Generics ve Extension Methods", "Modern C# sözdizimi, nesne yönelim, generic sözleşmeler ve extension metodlar."),
                    TopicCheckItem("med_art_16", "16. C#'ta Lambda Expressions, Delegates ve Higher-Order Functions", "Func, Action, closure'lar ve fonksiyonel C# yaklaşımları."),
                    TopicCheckItem("med_art_17", "17. C# Nullable Reference Types ve Null Safety", "NRT, null-forgiving operator (!) ve NullReferenceException'dan kaçınma."),
                    TopicCheckItem("med_art_18", "18. C# Async/Await ve Task Tabanlı Asenkron Programlama", "Task, ValueTask, ConfigureAwait, deadlock'lar ve async en iyi pratikleri."),
                    TopicCheckItem("med_art_19", "19. C# Collections: List, Dictionary, HashSet ve Ne Zaman Hangisini Kullanmalı?", "Koleksiyonların zaman karmaşıklığı ve doğru koleksiyon seçimi."),
                    TopicCheckItem("med_art_20", "20. IEnumerable, IQueryable ve Deferred Execution Mantığı", "Tembel yükleme, ertelenmiş çalışma ve LINQ filtreleme farkları."),
                    TopicCheckItem("med_art_21", "21. Records, Classes ve Structs: Veri Modelleme Yaklaşımları", "Immutable records, with anahtar kelimesi ve veri taşıma modelleri."),
                    TopicCheckItem("med_art_22", "22. Dependency Injection ve Inversion of Control", "IoC prensibi, ServiceCollection, Transient, Scoped ve Singleton yaşam döngüleri."),
                    TopicCheckItem("med_art_23", "23. ASP.NET Core Request Pipeline: Routing, Middleware, Controller ve Response Akışı", "HTTP isteğinin ASP.NET Core içerisindeki katmanlı yolculuğu."),
                    TopicCheckItem("med_art_24", "24. ASP.NET Core Configuration & Secrets Management", "Environment Variables, .env, User Secrets, Secret Management."),
                    TopicCheckItem("med_art_25", "25. ASP.NET Core'da Validation, Serialization ve Model Binding", "FluentValidation, System.Text.Json custom converter'lar ve FromQuery/FromBody."),
                    TopicCheckItem("med_art_26", "26. ASP.NET Core'da Global Error Handling ve ProblemDetails", "IExceptionHandler, custom error middleware ve RFC 7807 standardı."),
                    TopicCheckItem("med_art_27", "27. Logging ve Structured Logging ile Production Uygulamalarını İzlemek", "Serilog, structured JSON loglar ve log zenginleştirme (enrichers)."),
                    TopicCheckItem("med_art_28", "28. Swagger / OpenAPI ile API'ları Belgelemek", "Swashbuckle, XML comments, JWT yetkilendirme entegrasyonu."),
                    TopicCheckItem("med_art_29", "29. HttpClient ile Dış API'larla Haberleşmek", "IHttpClientFactory, connection pool tükenmesini önleme ve Polly entegrasyonu."),
                    TopicCheckItem("med_art_30", "30. REST API Tasarımının Temelleri", "HTTP methods, status codes, headers, body, resource design."),
                    TopicCheckItem("med_art_31", "31. Pagination, Filtering, Sorting ve Searching API Tasarımında Nasıl Ele Alınır?", "Sayfalama standartları, dinamik filtreleme ve query string yapısı."),
                    TopicCheckItem("med_art_32", "32. API Versioning, Idempotency ve Backward Compatibility", "Asp.Versioning, URI/header sürümleme, idempotent endpoint'ler ve Idempotency-Key.")
                )
            ),

            // ----------------------------------------------------
            // 3. DATABASE / SQL / EF CORE
            // ----------------------------------------------------
            TopicSection(
                title = "3. DATABASE / SQL / EF CORE",
                emoji = "🗄️",
                items = listOf(
                    TopicCheckItem("med_art_33", "33. Veritabanlarının Temelleri: Table, Relation, Primary Key, Foreign Key ve Normalization", "İlişkisel şema tasarımı, 1NF-2NF-3NF ve veri bütünlüğü kuralları."),
                    TopicCheckItem("med_art_34", "34. SQL Sorgularının Temelleri: SELECT, JOIN, GROUP BY, Subquery ve Aggregate Functions", "Kapsamlı SQL sorgulama yetkinliği ve birleşim operatörleri."),
                    TopicCheckItem("med_art_35", "35. Database Index Nedir ve Sorguları Nasıl Hızlandırır?", "B-tree index mantığı, clustered vs non-clustered index ve composite indexler."),
                    TopicCheckItem("med_art_36", "36. Database Transaction, ACID ve Isolation Levels", "Atomicity, Consistency, Isolation, Durability ve izolasyon seviyeleri."),
                    TopicCheckItem("med_art_37", "37. Concurrency, Race Condition ve Database Locking", "Pessimistic vs Optimistic locking, [Timestamp] ve row-level locklar."),
                    TopicCheckItem("med_art_38", "38. Query Optimization ve Execution Plan Mantığı", "EXPLAIN ANALYZE, index scan vs index seek ve sorgu maliyeti okuma."),
                    TopicCheckItem("med_art_39", "39. ORM Nedir?", "Object-Relational Mapping, avantajları, dezavantajları ve abstraction maliyeti."),
                    TopicCheckItem("med_art_40", "40. Entity Framework Core'u Anlamak", "DbContext, Entity, DbSet, Tracking, LINQ ve Database Interaction."),
                    TopicCheckItem("med_art_41", "41. EF Core ile Database Migrations ve Code-First Yaklaşımı", "dotnet ef migrations mantığı, script üretimi ve güvenli migration stratejileri."),
                    TopicCheckItem("med_art_42", "42. EF Core Loading Stratejileri", "Eager, Explicit, Lazy Loading + N+1 problemi ve AsSplitQuery çözümü."),
                    TopicCheckItem("med_art_43", "43. LINQ'tan SQL'e: Expression Tree ve Query Translation", "Expression trees, istemci tarafında değerlendirme uyarısı ve SQL çevirisi."),
                    TopicCheckItem("med_art_44", "44. Repository Pattern ile EF Core Kullanımı Gerekli mi?", "EF Core zaten DbContext/DbSet ile Repository/UnitOfWork sunarken soyutlama tartışması."),
                    TopicCheckItem("med_art_45", "45. Database Seeding ve Uygulama Başlangıç Verilerinin Yönetimi", "HasData, migration seeding ve bağımsız veri başlatma betikleri."),
                    TopicCheckItem("med_art_46", "46. PostgreSQL'i Backend Geliştiricisi Gözüyle Anlamak", "PostgreSQL mimarisi, JSONB desteği, connection pooling ve psql pratikleri.")
                )
            ),

            // ----------------------------------------------------
            // 4. SOFTWARE ARCHITECTURE
            // ----------------------------------------------------
            TopicSection(
                title = "4. SOFTWARE ARCHITECTURE",
                emoji = "🏗️",
                items = listOf(
                    TopicCheckItem("med_art_47", "47. Yazılım Mimarisi Nedir ve Neden İhtiyaç Duyarız?", "Mimari kararlar, teknik borç ve yazılımın evrilebilirliği."),
                    TopicCheckItem("med_art_48", "48. Separation of Concerns ve Katmanlı Mimari", "Sorumlulukların ayrımı, katmanlar arası sınırlar ve akış yönü."),
                    TopicCheckItem("med_art_49", "49. MVC, MVP ve MVVM Yaklaşımlarının Karşılaştırılması", "Kullanıcı arayüzü desenlerinin evrimi ve modern frontend/mobil etkileri."),
                    TopicCheckItem("med_art_50", "50. Clean Architecture ve Dependency Rule", "Domain merkezli bağımlılık kuralı ve katman izolasyonu."),
                    TopicCheckItem("med_art_51", "51. Onion Architecture ve Katmanların Sorumlulukları", "Core, Application, Infrastructure ve Presentation katmanları."),
                    TopicCheckItem("med_art_52", "52. SOLID Principles: Gerçek Kod Üzerinden Anlamak", "SRP, OCP, LSP, ISP, DIP ilkelerinin somut refactoring örnekleri."),
                    TopicCheckItem("med_art_53", "53. Dependency Inversion, Dependency Injection ve Test Edilebilirlik İlişkisi", "Arayüzlere bağımlılık ve mock'lanabilir birim tasarımı."),
                    TopicCheckItem("med_art_54", "54. DTO, Entity ve Domain Model Arasındaki Farklar", "Veri transfer nesneleri ile zengin domain modellerinin ayrımı."),
                    TopicCheckItem("med_art_55", "55. Repository, Service ve Unit of Work Pattern'leri", "Veri soyutlama, iş mantığı izolasyonu ve transaction yönetimi."),
                    TopicCheckItem("med_art_56", "56. Design Pattern Nedir?", "Pattern kullanmak ile gereksiz abstraction arasındaki çizgi."),
                    TopicCheckItem("med_art_57", "57. Composition vs Inheritance", "Kalıtım hiyerarşisinin tuzakları ve kompozisyonun esnekliği."),
                    TopicCheckItem("med_art_58", "58. Architecture Decision: Küçük Projede Ne Kadar Mimari Gerekir?", "Proje boyutuna göre mimari karmaşıklık seçimi ve YAGNI prensibi."),
                    TopicCheckItem("med_art_59", "59. Monolith Nedir?", "Avantajları, dezavantajları ve ne zaman mantıklıdır?")
                )
            ),

            // ----------------------------------------------------
            // 5. TESTING
            // ----------------------------------------------------
            TopicSection(
                title = "5. TESTING",
                emoji = "🧪",
                items = listOf(
                    TopicCheckItem("med_art_60", "60. Software Testing'e Giriş: Unit, Integration ve End-to-End Testler", "Yazılım test piramidinin temelleri ve test türleri."),
                    TopicCheckItem("med_art_61", "61. Test Piramidi ve Test Stratejisi", "Hızlı birim testleri ile kapsamlı entegrasyon testlerinin dengesi."),
                    TopicCheckItem("med_art_62", "62. Mock, Stub, Fake ve Test Double Kavramları", "Test çiftleri arasındaki farklar ve Moq/NSubstitute kullanımı."),
                    TopicCheckItem("med_art_63", "63. Test Edilebilir Kod Nasıl Tasarlanır?", "Yan etkisiz fonksiyonlar, constructor injection ve gevşek bağlılık."),
                    TopicCheckItem("med_art_64", "64. ASP.NET Core'da Unit ve Integration Testing", "xUnit, FluentAssertions ve WebApplicationFactory entegrasyonu."),
                    TopicCheckItem("med_art_65", "65. Database Integration Testleri Nasıl Düşünülmeli?", "Testcontainers veya in-memory DB ile gerçekçi veritabanı testleri."),
                    TopicCheckItem("med_art_66", "66. Test Coverage Nedir ve Neyi Gerçekten Ölçer?", "Kod kapsam yüzdesi illüzyonu ve nitelikli test senaryoları.")
                )
            ),

            // ----------------------------------------------------
            // 6. SECURITY
            // ----------------------------------------------------
            TopicSection(
                title = "6. SECURITY",
                emoji = "🔐",
                items = listOf(
                    TopicCheckItem("med_art_67", "67. Authentication ve Authorization Nedir?", "Kimlik doğrulama ile yetki denetiminin mimari ayrımı."),
                    TopicCheckItem("med_art_68", "68. Password Security: Hashing, Salting, BCrypt ve Argon2", "Geri döndürülemez parola güvenliği, tuzlama ve maliyet faktörü."),
                    TopicCheckItem("med_art_69", "69. Hashing vs Encryption vs Encoding", "Özetleme, çift yönlü şifreleme ve format kodlama farkları."),
                    TopicCheckItem("med_art_70", "70. Session-Based Authentication ve JWT Karşılaştırması", "Stateful cookie/session vs stateless token yaklaşımları."),
                    TopicCheckItem("med_art_71", "71. JWT'yi Baştan Sona Anlamak", "Header, Payload, Signature, Claims, JTI."),
                    TopicCheckItem("med_art_72", "72. Access Token, Refresh Token ve Token Lifecycle", "Kısa ömürlü access token ve güvenli token yenileme mekanizması."),
                    TopicCheckItem("med_art_73", "73. Role-Based ve Policy-Based Authorization", "Rol kontrollerinden claim ve policy tabanlı dinamik yetkilendirmeye geçiş."),
                    TopicCheckItem("med_art_74", "74. OAuth 2.0 ve OpenID Connect'e Giriş", "Yetki devri, SSO ve Google/GitHub ile üçüncü parti giriş akışı."),
                    TopicCheckItem("med_art_75", "75. HTTPS ve TLS Nasıl Çalışır?", "Sertifika zinciri, asymmetric handshake ve simetrik veri iletimi."),
                    TopicCheckItem("med_art_76", "76. Web Security'nin Temelleri", "SQL Injection, XSS, CSRF, CORS."),
                    TopicCheckItem("med_art_77", "77. API Security: Rate Limiting, Input Validation, Secrets ve Authorization", "API suiistimalini önleme ve savunma derinliği (defense-in-depth)."),
                    TopicCheckItem("med_art_78", "78. Secret Management: API Key, Environment Variable ve Secret Store Kullanımı", "Hassas anahtarların kod dışına çıkarılması ve Key Vault yönetimi.")
                )
            ),

            // ----------------------------------------------------
            // 7. BACKGROUND JOBS / OBSERVABILITY
            // ----------------------------------------------------
            TopicSection(
                title = "7. BACKGROUND JOBS / OBSERVABILITY",
                emoji = "⚙️",
                items = listOf(
                    TopicCheckItem("med_art_79", "79. Background Processing Nedir?", "Asenkron arka plan görevleri ve istek döngüsünden yük boşaltma."),
                    TopicCheckItem("med_art_80", "80. .NET Hosted Service ve BackgroundService", "IHostedService yaşam döngüsü ve periyodik worker'lar."),
                    TopicCheckItem("med_art_81", "81. Hangfire ile Scheduled ve Recurring Jobs", "Kalıcı kuyruklar, dashboard yönetimi ve cron tabanlı işler."),
                    TopicCheckItem("med_art_82", "82. Background Job'larda Retry, Failure ve Idempotency", "Hata durumlarında tekrar deneme ve mükerrer işlem koruması."),
                    TopicCheckItem("med_art_83", "83. Logging, Metrics ve Tracing: Observability'nin Üç Temeli", "Loglar, sayısal metrikler ve dağıtık izleme şelaleleri."),
                    TopicCheckItem("med_art_84", "84. Prometheus ve Grafana ile Monitoring'e Giriş", "Metrik toplama, Time-Series veritabanı ve görsel gösterge panoları."),
                    TopicCheckItem("med_art_85", "85. Health Checks ve Application Monitoring", "/healthz kontrolleri ve otomatik servis kurtarma.")
                )
            ),

            // ----------------------------------------------------
            // 8. DEVOPS
            // ----------------------------------------------------
            TopicSection(
                title = "8. DEVOPS",
                emoji = "🐳",
                items = listOf(
                    TopicCheckItem("med_art_86", "86. Docker Nedir?", "Image, Container, Layer, Registry kavramları."),
                    TopicCheckItem("med_art_87", "87. Dockerfile ve Container Image Oluşturma Mantığı", "Multi-stage build ile hafif ve güvenli production image'ları."),
                    TopicCheckItem("med_art_88", "88. Docker Volume, Network ve Environment Variables", "Kalıcı veriler, container arası izole ağlar ve konfigürasyon."),
                    TopicCheckItem("med_art_89", "89. Docker Compose ile Multi-Container Application", "Çok servisli sistemleri tek komutla koordine etme."),
                    TopicCheckItem("med_art_90", "90. .NET + PostgreSQL'i Docker Compose ile Çalıştırmak", "Yerel geliştirme ortamının tam containerize edilmesi."),
                    TopicCheckItem("med_art_91", "91. Container vs Virtual Machine", "İşletim sistemi sanallaştırma ile container izolasyonunun farkları."),
                    TopicCheckItem("med_art_92", "92. CI/CD Nedir?", "Sürekli entegrasyon, sürekli teslimat ve otomatik doğrulama kültürü."),
                    TopicCheckItem("med_art_93", "93. GitHub Actions ile Basit CI/CD Pipeline", "Commit sonrası test, derleme ve otomatik yayın akışı."),
                    TopicCheckItem("med_art_94", "94. Deployment Türleri ve Release Stratejileri", "Blue-Green, Rolling ve Canary deployment modelleri."),
                    TopicCheckItem("med_art_95", "95. Kubernetes'e Giriş", "Pod, Deployment, Service, ConfigMap, Secret."),
                    TopicCheckItem("med_art_96", "96. Kubernetes'te Scaling, Health Check ve Service Discovery", "HPA, liveness/readiness probları ve iç DNS çözümlemesi."),
                    TopicCheckItem("med_art_97", "97. Containerized Application'larda Configuration ve Secrets", "Kapsüllenmiş uygulamalarda dinamik konfigürasyon yönetimi.")
                )
            ),

            // ----------------------------------------------------
            // 9. DISTRIBUTED SYSTEMS
            // ----------------------------------------------------
            TopicSection(
                title = "9. DISTRIBUTED SYSTEMS",
                emoji = "📨",
                items = listOf(
                    TopicCheckItem("med_art_98", "98. Distributed Systems Nedir ve Neden Zordur?", "Ağ gecikmesi, kısmi arızalar (partial failures) ve dağıtık mimari karmaşıklığı."),
                    TopicCheckItem("med_art_99", "99. Monolith vs Microservices", "Bütünleşik mimari ile bağımsız servislerin gerçek dünya karşılaştırması."),
                    TopicCheckItem("med_art_100", "100. Synchronous vs Asynchronous Communication", "Senkron REST/gRPC çağrıları vs asenkron event tabanlı haberleşme."),
                    TopicCheckItem("med_art_101", "101. Message Queue ve Event-Driven Architecture", "Servis ayrışması (decoupling), pub-sub ve yük tamponlama."),
                    TopicCheckItem("med_art_102", "102. RabbitMQ'yu Anlamak", "Exchange, Queue, Binding, Producer, Consumer."),
                    TopicCheckItem("med_art_103", "103. Message Delivery Semantics", "At-most-once, at-least-once, exactly-once."),
                    TopicCheckItem("med_art_104", "104. Retry, Dead Letter Queue ve Failure Handling", "Hatalı mesajların izolasyonu ve DLQ mimarisi."),
                    TopicCheckItem("med_art_105", "105. Idempotency ve Distributed Systems", "Tekrarlanan mesajlarda veri tutarlılığını garanti altına alma."),
                    TopicCheckItem("med_art_106", "106. Eventual Consistency Nedir?", "Güçlü tutarlılık yerine nihai tutarlılık ve CAP teoremi."),
                    TopicCheckItem("med_art_107", "107. Distributed Systems'te Fault Tolerance", "Circuit breaker, fallback stratejileri ve servis dayanıklılığı."),
                    TopicCheckItem("med_art_108", "108. Redis Nedir?", "Cache, data store ve kullanım senaryoları."),
                    TopicCheckItem("med_art_109", "109. Cache Strategies ve Cache Invalidation", "Cache-Aside, Write-Through ve cache geçersiz kılma zorluğu.")
                )
            ),

            // ----------------------------------------------------
            // 10. ANDROID / KOTLIN
            // ----------------------------------------------------
            TopicSection(
                title = "10. ANDROID / KOTLIN",
                emoji = "📱",
                items = listOf(
                    TopicCheckItem("med_art_110", "110. Kotlin'i Anlamak: Null Safety, Data Class, Sealed Class ve Extension Functions", "Kotlin'in modern dil özellikleri ve ifade gücü."),
                    TopicCheckItem("med_art_111", "111. Kotlin'de Collections ve Functional Programming Yaklaşımı", "Filter, map, flatMap, fold ve immutable koleksiyon akışları."),
                    TopicCheckItem("med_art_112", "112. Kotlin Coroutines ile Asenkron Programlama", "Suspend fonksiyonlar, Dispatchers, structured concurrency ve Job yönetimi."),
                    TopicCheckItem("med_art_113", "113. Kotlin Flow ve Reactive Data Stream Yaklaşımı", "Cold vs Hot streams, StateFlow, SharedFlow ve reaktif veri akışı."),
                    TopicCheckItem("med_art_114", "114. Android Lifecycle'ı Baştan Sona Anlamak", "Activity/Fragment yaşam döngüsü, configuration changes ve bellek sızıntıları."),
                    TopicCheckItem("med_art_115", "115. ViewModel, State ve UI Lifecycle İlişkisi", "Veri koruma, repeatOnLifecycle ve lifecycle-aware bileşenler."),
                    TopicCheckItem("med_art_116", "116. MVVM ile Android Uygulama Mimarisi", "Model-View-ViewModel ve tek yönlü veri akışı (UDF)."),
                    TopicCheckItem("med_art_117", "117. Single Activity Architecture ve Navigation Component", "Tek aktivite, fragment/compose yönlendirmesi ve deep linkler."),
                    TopicCheckItem("med_art_118", "118. Android UI State Yönetimi", "Loading, Success, Error, Empty durumlarının modellenmesi."),
                    TopicCheckItem("med_art_119", "119. RecyclerView Nasıl Çalışır ve Nasıl Optimize Edilir?", "ViewHolder deseni, DiffUtil ve gereksiz render'ların önlenmesi."),
                    TopicCheckItem("med_art_120", "120. ConstraintLayout ile Responsive Android UI", "Karmaşık hiyerarşileri düzleştirme ve ekran uyumluluğu."),
                    TopicCheckItem("med_art_121", "121. XML vs Jetpack Compose", "Imperative View sistemi ile deklaratif UI paradigmasının karşılaştırması."),
                    TopicCheckItem("med_art_122", "122. Compose'un State ve Recomposition Mantığı", "Recomposition, remember, derivedStateOf ve akıcı Compose performansı."),
                    TopicCheckItem("med_art_123", "123. Retrofit ile REST API Entegrasyonu", "OkHttp, converter factory'ler ve network arayüz tanımları."),
                    TopicCheckItem("med_art_124", "124. Interceptor, Authentication ve Network Error Handling", "Auth interceptor ile otomatik bearer token ekleme ve hata yakalama."),
                    TopicCheckItem("med_art_125", "125. Android'de Local Storage", "SharedPreferences, DataStore, Room."),
                    TopicCheckItem("med_art_126", "126. Repository Pattern ile Network + Local Data Yönetimi", "Single source of truth ve offline-first Android mimarisi."),
                    TopicCheckItem("med_art_127", "127. Android'de Pagination ve Büyük Veri Setlerini Yönetmek", "Paging 3 kütüphanesi ve sonsuz liste akışları.")
                )
            ),

            // ----------------------------------------------------
            // 11. MOBILE SECURITY & PUBLISHING
            // ----------------------------------------------------
            TopicSection(
                title = "11. MOBILE SECURITY & PUBLISHING",
                emoji = "🛡️",
                items = listOf(
                    TopicCheckItem("med_art_128", "128. Android'de API Key ve Secret Management", "BuildConfig, local.properties ve NDK ile gizleme yaklaşımları."),
                    TopicCheckItem("med_art_129", "129. Android Keystore ve Güvenli Veri Saklama", "Donanım destekli şifreleme ve hassas kullanıcı token'larının korunması."),
                    TopicCheckItem("med_art_130", "130. Android Application Signing ve Keystore Mantığı", "Release keystore üretimi, SHA-256 parmak izi ve imzalama süreci."),
                    TopicCheckItem("med_art_131", "131. APK vs AAB", "Monolitik APK ile Android App Bundle ve dinamik teslimatın farkı."),
                    TopicCheckItem("med_art_132", "132. Google Play Release Süreci", "Versioning, signing, release tracks."),
                    TopicCheckItem("med_art_133", "133. Google Play Data Safety ve Privacy Requirements", "Veri güvenliği formu, izin politikaları ve mağaza gereksinimleri."),
                    TopicCheckItem("med_art_134", "134. Mobil Uygulamalarda Authentication ve Token Storage", "Biyometrik doğrulama ve güvenli oturum yönetimi.")
                )
            ),

            // ----------------------------------------------------
            // 12. ALGORITHMS & DATA STRUCTURES
            // ----------------------------------------------------
            TopicSection(
                title = "12. ALGORITHMS & DATA STRUCTURES",
                emoji = "🧮",
                items = listOf(
                    TopicCheckItem("med_art_135", "135. Big-O Complexity'yi Gerçekten Anlamak", "Zaman ve alan karmaşıklığı analizi ve asimptotik gösterim."),
                    TopicCheckItem("med_art_136", "136. Array, Linked List, Stack ve Queue", "Doğrusal veri yapılarının bellek yerleşimi ve ekleme/silme maliyetleri."),
                    TopicCheckItem("med_art_137", "137. Hash Table ve Hashing Mantığı", "Çakışma çözümü (collision handling), chaining, open addressing ve O(1) arama."),
                    TopicCheckItem("med_art_138", "138. Tree ve Binary Search Tree", "Ağaç veri modelleri, BST arama dengesi ve traversal yöntemleri."),
                    TopicCheckItem("med_art_139", "139. Graph, BFS ve DFS", "Çizge algoritmaları, en kısa yol, derinlik ve genişlik öncelikli arama."),
                    TopicCheckItem("med_art_140", "140. Recursion ve Recursive Problem Solving", "Özyineleme temelleri, base case ve call stack sınırları."),
                    TopicCheckItem("med_art_141", "141. Sorting Algorithms ve Complexity Karşılaştırması", "QuickSort, MergeSort, HeapSort ve O(n log n) sınırı."),
                    TopicCheckItem("med_art_142", "142. Binary Search ve Search Optimization", "Sıralı dizilerde logaritmik arama ve sınır optimizasyonları."),
                    TopicCheckItem("med_art_143", "143. Levenshtein Distance ve String Similarity", "Dinamik programlama ile iki metin arasındaki düzenleme mesafesi."),
                    TopicCheckItem("med_art_144", "144. Jaccard Similarity ve Set-Based Matching", "Kümeler üzerinden benzerlik katsayısı hesaplama."),
                    TopicCheckItem("med_art_145", "145. Haversine Formula ile Coğrafi Mesafe Hesaplama", "Küre üzerinde iki GPS koordinatı arasındaki mesafeyi bulma.")
                )
            ),

            // ----------------------------------------------------
            // 13. OPERATING SYSTEMS
            // ----------------------------------------------------
            TopicSection(
                title = "13. OPERATING SYSTEMS",
                emoji = "🖥️",
                items = listOf(
                    TopicCheckItem("med_art_146", "146. Operating System Nedir ve Ne İş Yapar?", "Donanım soyutlama, kaynak tahsisi ve işletim sistemi çekirdeği."),
                    TopicCheckItem("med_art_147", "147. Process ve Thread Kavramları", "Bağımsız adres alanı vs paylaşılan bellek ve iş parçacıkları."),
                    TopicCheckItem("med_art_148", "148. Concurrency, Parallelism ve Context Switching", "CPU zaman dilimleme ve context switch maliyeti."),
                    TopicCheckItem("med_art_149", "149. Race Condition, Mutex, Semaphore ve Synchronization", "Kritik bölge koruması ve senkronizasyon araçları."),
                    TopicCheckItem("med_art_150", "150. Deadlock Nedir?", "Kilitlenme koşulları (Coffman kriterleri) ve deadlock'tan kaçınma."),
                    TopicCheckItem("med_art_151", "151. Virtual Memory Nasıl Çalışır?", "Fiziksel bellek izolasyonu ve adres soyutlama mantığı."),
                    TopicCheckItem("med_art_152", "152. Paging, Page Table ve Page Fault", "Sayfalama mekanizması ve diske takas (swapping) döngüsü."),
                    TopicCheckItem("med_art_153", "153. Memory Fragmentation", "İç ve dış parçalanma (internal/external fragmentation) problemleri."),
                    TopicCheckItem("med_art_154", "154. User Space vs Kernel Space", "Kullanıcı alanı, çekirdek ayrıcalıkları ve CPU ring korumaları."),
                    TopicCheckItem("med_art_155", "155. System Calls Nasıl Çalışır?", "Kullanıcı kodundan çekirdeğe geçiş (software interrupts/trap)."),
                    TopicCheckItem("med_art_156", "156. File Systems ve Disk I/O'nun Temelleri", "İ-node yapısı, disk blokları, caching ve dosya okuma/yazma akışı.")
                )
            ),

            // ----------------------------------------------------
            // 14. COMPUTER ARCHITECTURE / MEMORY
            // ----------------------------------------------------
            TopicSection(
                title = "14. COMPUTER ARCHITECTURE / MEMORY",
                emoji = "💾",
                items = listOf(
                    TopicCheckItem("med_art_157", "157. CPU Nasıl Çalışır?", "Fetch, Decode, Execute döngüsü ve ALU/Registers mimarisi."),
                    TopicCheckItem("med_art_158", "158. RAM, Cache ve Memory Hierarchy", "Kayıtçıdan sabit diske bellek hiyerarşisi ve hız farkları."),
                    TopicCheckItem("med_art_159", "159. CPU Cache ve Cache Locality", "L1/L2/L3 cache, spatial ve temporal locality prensipleri."),
                    TopicCheckItem("med_art_160", "160. Virtual Address ve Physical Address", "Sanal bellek adreslerinin donanım düzeyinde fiziksel RAM'e çevrimi."),
                    TopicCheckItem("med_art_161", "161. MMU ve Address Translation", "Memory Management Unit'in donanımsal adres çeviri rolü."),
                    TopicCheckItem("med_art_162", "162. TLB Nedir?", "Translation Lookaside Buffer: Sayfa tablosu sorgu hızlandırıcısı."),
                    TopicCheckItem("med_art_163", "163. DMA Nedir?", "Direct Memory Access: CPU'yu meşgul etmeden doğrudan bellek transferi."),
                    TopicCheckItem("med_art_164", "164. Memory-Mapped I/O ve mmap", "Dosyaları ve donanım register'larını sanal belleğe eşleme."),
                    TopicCheckItem("med_art_165", "165. CPU Performance ve Memory Bottlenecks", "Bellek darboğazları, cache miss cezası ve von Neumann darboğazı.")
                )
            ),

            // ----------------------------------------------------
            // 15. COMPUTER NETWORKS
            // ----------------------------------------------------
            TopicSection(
                title = "15. COMPUTER NETWORKS",
                emoji = "🌐",
                items = listOf(
                    TopicCheckItem("med_art_166", "166. Computer Networks'ün Temelleri", "Ağ topolojileri, paket anahtarlama ve veri iletim prensipleri."),
                    TopicCheckItem("med_art_167", "167. OSI ve TCP/IP Modeli", "Katmanlı ağ modelleri ve protokollerin görev dağılımı."),
                    TopicCheckItem("med_art_168", "168. IP Address, Subnet ve Routing", "IPv4/IPv6 adresleme, alt ağ maskeleri ve paket yönlendirme."),
                    TopicCheckItem("med_art_169", "169. TCP vs UDP", "Güvenilir, sıralı akış (TCP) vs hızlı, bağlantısız veri iletimi (UDP)."),
                    TopicCheckItem("med_art_170", "170. TCP Connection ve Three-Way Handshake", "SYN, SYN-ACK, ACK adımları ve bağlantı sonlandırma."),
                    TopicCheckItem("med_art_171", "171. DNS Nasıl Çalışır?", "Hiyerarşik isim çözümleme, Root/TLD sunucuları ve DNS caching."),
                    TopicCheckItem("med_art_172", "172. HTTP/HTTPS Nasıl Çalışır?", "HTTP/1.1, HTTP/2 multiplexing, HTTP/3 QUIC ve TLS güvenliği."),
                    TopicCheckItem("med_art_173", "173. Socket Nedir?", "Ağ üzerinden iki yönlü iletişim kanalı açma ve socket programlama."),
                    TopicCheckItem("med_art_174", "174. Reverse Proxy ve Load Balancer", "Nginx/YARP, ters proxy güvenliği ve yük dengeleme algoritmaları."),
                    TopicCheckItem("med_art_175", "175. CDN ve Caching", "İçerik dağıtım ağları, edge caching ve statik varlık optimizasyonu."),
                    TopicCheckItem("med_art_176", "176. Bir HTTP Request'inin Baştan Sona Yolculuğu", "DNS, TCP, TLS, Gateway, Sunucu ve Yanıt akışını birbirine bağlayan büyük resim.")
                )
            ),

            // ----------------------------------------------------
            // 16. LLM / AI
            // ----------------------------------------------------
            TopicSection(
                title = "16. LLM / AI & KV CACHE",
                emoji = "🤖",
                items = listOf(
                    TopicCheckItem("med_art_177", "177. LLM Nedir?", "Büyük Dil Modelleri, temel mimari ve üretim mantığı."),
                    TopicCheckItem("med_art_178", "178. Tokenization ve Token Nedir?", "BPE, WordPiece ve metinlerin sayısal vektörlere dönüşümü."),
                    TopicCheckItem("med_art_179", "179. Context Window ve Sequence Length", "Bağlam penceresi sınırları ve uzun girdi zorlukları."),
                    TopicCheckItem("med_art_180", "180. Transformer Mimarisi", "Encoder-Decoder yapıları ve modern decoder-only modeller."),
                    TopicCheckItem("med_art_181", "181. Attention Mekanizması", "Öz-dikkat (Self-Attention) ve kelimeler arası ilişki ağırlıkları."),
                    TopicCheckItem("med_art_182", "182. Query, Key, Value Nasıl Çalışır?", "Matris çarpımları, softmax ve bilgi getirme mantığı."),
                    TopicCheckItem("med_art_183", "183. Multi-Head Attention", "Farklı temsil uzaylarında paralel dikkat mekanizması."),
                    TopicCheckItem("med_art_184", "184. LLM Inference Nedir?", "Prefill (prompt) ve Decode fazlarının ayrımı."),
                    TopicCheckItem("med_art_185", "185. Latency, Throughput ve Token Generation", "TTFT (Time to First Token) ve TPOT (Time Per Output Token)."),
                    TopicCheckItem("med_art_186", "186. KV Cache Nedir?", "Önceki token'ların key ve value tensörlerinin bellekte saklanması."),
                    TopicCheckItem("med_art_187", "187. KV Cache Neden Bellek Problemi Oluşturur?", "Uzun bağlamda ve yüksek batch size'da bellek patlaması."),
                    TopicCheckItem("med_art_188", "188. PagedAttention ve Memory Management", "İşletim sistemi sayfalama mantığını KV Cache tensörlerine uygulama (vLLM)."),
                    TopicCheckItem("med_art_189", "189. vAttention ve KV Cache Yönetimi", "Sanal bellek haritalama ile contiguous buffer yönetimi."),
                    TopicCheckItem("med_art_190", "190. PagedAttention vs vAttention", "İki modern bellek yönetim yaklaşımının derin teknik karşılaştırması."),
                    TopicCheckItem("med_art_191", "191. LLM Inference'ta Memory Fragmentation", "Dinamik token üretiminde dahili ve harici bellek parçalanması."),
                    TopicCheckItem("med_art_192", "192. GPU Memory ve LLM Inference", "HBM bant genişliği, VRAM sınırları ve bellek darboğazları."),
                    TopicCheckItem("med_art_193", "193. FlashAttention Nedir?", "SRAM-HBM veri transferini minimize eden donanım odaklı dikkat algoritması."),
                    TopicCheckItem("med_art_194", "194. Llama Modellerini Anlamak", "RoPE (Rotary Positional Embedding), SwiGLU ve modern Llama mimarisi."),
                    TopicCheckItem("med_art_195", "195. Ollama Nasıl Çalışır?", "Llama.cpp altyapısı, GGUF formatı ve tek komutla yerel model çalıştırma."),
                    TopicCheckItem("med_art_196", "196. Local LLM Çalıştırma ve Inference Stack", "GPU offloading, quantization (INT4/INT8) ve yerel çıkarım stack'i."),
                    TopicCheckItem("med_art_197", "197. LLM Performance Benchmarking", "Token/saniye ölçümü, bellek profilleme ve test senaryoları."),
                    TopicCheckItem("med_art_198", "198. LLM Inference'ta Bottleneck Analizi", "Compute-bound vs Memory-bound fazların tespiti.")
                )
            ),

            // ----------------------------------------------------
            // 17. C / C++ / LOW LEVEL
            // ----------------------------------------------------
            TopicSection(
                title = "17. C / C++ / LOW LEVEL",
                emoji = "🧠",
                items = listOf(
                    TopicCheckItem("med_art_199", "199. C/C++ Memory Management'ın Temelleri", "Manuel bellek yönetimi ve işletim sistemi tahsisleri."),
                    TopicCheckItem("med_art_200", "200. Pointer ve Pointer Arithmetic", "Ham bellek adresleri, referanslar ve pointer aritmetiği."),
                    TopicCheckItem("med_art_201", "201. malloc/free vs new/delete", "C bellek fonksiyonları ile C++ tip güvenli tahsis farkı."),
                    TopicCheckItem("med_art_202", "202. Memory Allocation Nasıl Çalışır?", "brk, sbrk, mmap ve glibc ptmalloc mimarisi."),
                    TopicCheckItem("med_art_203", "203. RAII ve C++ Resource Management", "Resource Acquisition Is Initialization ve otomatik kaynak temizliği."),
                    TopicCheckItem("med_art_204", "204. Smart Pointers: unique_ptr, shared_ptr, weak_ptr", "Sahiplik modelleri ve referans sayımı ile güvenli bellek."),
                    TopicCheckItem("med_art_205", "205. Copy Semantics vs Move Semantics", "Rvalue referansları, std::move ve gereksiz bellek kopyalamalarını önleme."),
                    TopicCheckItem("med_art_206", "206. C++ Object Lifetime", "Statik, otomatik ve dinamik depolama süreleri."),
                    TopicCheckItem("med_art_207", "207. Memory Fragmentation ve Allocator Problemleri", "Genel amaçlı tahsis edicilerin yüksek frekanslı tahsislerde tıkanması."),
                    TopicCheckItem("med_art_208", "208. Custom Allocator Nedir?", "Özelleştirilmiş bellek havuzları ve sıfır gecikmeli tahsis."),
                    TopicCheckItem("med_art_209", "209. Arena Allocator", "Büyük tek parça bellek tahsisi ve tek seferde toplu serbest bırakma."),
                    TopicCheckItem("med_art_210", "210. Pool Allocator", "Sabit boyutlu nesneler için O(1) tahsis havuzları."),
                    TopicCheckItem("med_art_211", "211. C++ Performance Optimization'ın Temelleri", "Inlining, loop unrolling ve derleyici optimizasyon bayrakları (-O3)."),
                    TopicCheckItem("med_art_212", "212. Profiling ve Benchmarking", "Perf, Valgrind, gprof ve Google Benchmark kullanımı.")
                )
            ),

            // ----------------------------------------------------
            // 18. GPU / CUDA
            // ----------------------------------------------------
            TopicSection(
                title = "18. GPU / CUDA",
                emoji = "🎮",
                items = listOf(
                    TopicCheckItem("med_art_213", "213. CPU vs GPU", "Düşük gecikmeli az çekirdek vs yüksek verimli binlerce çekirdek mimarisi."),
                    TopicCheckItem("med_art_214", "214. GPU Parallelism Nasıl Çalışır?", "SIMT (Single Instruction, Multiple Threads) çalışma mantığı."),
                    TopicCheckItem("med_art_215", "215. CUDA Programlama Modeli", "Host ve Device kodu ayrımı ve __global__ kernel fonksiyonları."),
                    TopicCheckItem("med_art_216", "216. CUDA Thread, Block ve Grid", "3 boyutlu thread hiyerarşisi ve warp scheduling (32 threads)."),
                    TopicCheckItem("med_art_217", "217. GPU Memory Hierarchy", "Registers, Shared Memory, L1/L2 Cache ve Global Memory."),
                    TopicCheckItem("med_art_218", "218. CUDA Memory Types", "Constant, Texture ve Local Memory kullanım alanları."),
                    TopicCheckItem("med_art_219", "219. Unified Memory", "cudaMallocManaged ile CPU ve GPU arasında paylaşılan sanal adres alanı."),
                    TopicCheckItem("med_art_220", "220. CUDA Virtual Memory Management", "cuMemMap ve düşük seviyeli sanal bellek tahsis API'ları."),
                    TopicCheckItem("med_art_221", "221. CPU-GPU Data Transfer ve PCIe Maliyeti", "Host-Device veri transfer darboğazı ve async stream kopyalamaları."),
                    TopicCheckItem("med_art_222", "222. GPU Memory Bottleneck'leri", "Bank conflicts, uncoalesced memory access ve gecikmeler."),
                    TopicCheckItem("med_art_223", "223. CUDA ile Performance Benchmarking", "NVIDIA Nsight Systems, Nsight Compute ve kernel profilleme.")
                )
            ),

            // ----------------------------------------------------
            // 19. LINUX / TERMINAL
            // ----------------------------------------------------
            TopicSection(
                title = "19. LINUX / TERMINAL",
                emoji = "🐧",
                items = listOf(
                    TopicCheckItem("med_art_224", "224. Linux Filesystem'i Anlamak", "/etc, /var, /proc, /sys ve tek köklü dosya ağacı."),
                    TopicCheckItem("med_art_225", "225. Linux Permissions ve chmod", "Read, Write, Execute yetkileri, octal gösterim ve chown."),
                    TopicCheckItem("med_art_226", "226. Process Management", "ps, top, htop, kill, process sinyalleri (SIGTERM, SIGKILL)."),
                    TopicCheckItem("med_art_227", "227. stdin, stdout, stderr ve Pipe Mantığı", "Standart akışlar ve komutları boru hattıyla (|) bağlama sanatı."),
                    TopicCheckItem("med_art_228", "228. grep, sed, awk ve Unix Philosophy", "Her program tek bir işi mükemmel yapar ve metin işler."),
                    TopicCheckItem("med_art_229", "229. Linux Environment Variables", "PATH, export, ~/.bashrc ve kalıcı çevre değişkenleri."),
                    TopicCheckItem("med_art_230", "230. Bash Scripting'e Giriş", "Otomasyon betikleri, döngüler, koşullar ve exit kodları."),
                    TopicCheckItem("med_art_231", "231. SSH ve Remote Server Yönetiminin Temelleri", "SSH keypair üretimi, ssh-agent ve güvenli uzak bağlantı."),
                    TopicCheckItem("med_art_232", "232. curl ile HTTP/API Test Etmek", "Terminalden header, auth ve body ile gelişmiş API testleri."),
                    TopicCheckItem("med_art_233", "233. Linux Network Debugging", "ss, lsof, ping, traceroute ile açık port ve bağlantı denetimi."),
                    TopicCheckItem("med_art_234", "234. LazyVim ile C++ Development Environment", "Modern Neovim, LSP entegrasyonu ve klavye odaklı geliştirme.")
                )
            ),

            // ----------------------------------------------------
            // 20. GIT / GITHUB
            // ----------------------------------------------------
            TopicSection(
                title = "20. GIT / GITHUB",
                emoji = "🔧",
                items = listOf(
                    TopicCheckItem("med_art_235", "235. Git'in Çalışma Mantığı", "DAG (Directed Acyclic Graph), blob, tree ve commit nesneleri."),
                    TopicCheckItem("med_art_236", "236. Commit, Branch ve Merge", "Dallanma modelleri ve commit geçmişi oluşturma."),
                    TopicCheckItem("med_art_237", "237. Rebase vs Merge", "Düz commit geçmişi vs korunan dallanma tarihi karşılaştırması."),
                    TopicCheckItem("med_art_238", "238. Git Conflict Çözme Mantığı", "Çakışma blokları (<<<<<<<, =======, >>>>>>>) ve temiz birleştirme."),
                    TopicCheckItem("med_art_239", "239. reset, revert, restore Arasındaki Farklar", "Geçmişi geri alma ve güvenli geri alma yöntemleri."),
                    TopicCheckItem("med_art_240", "240. Git Stash ve Temporary Changes", "Yarım kalan işleri rafa kaldırma ve geri getirme."),
                    TopicCheckItem("med_art_241", "241. Pull Request ve Code Review Kültürü", "İyi bir PR açıklaması ve yapıcı kod inceleme iletişimi."),
                    TopicCheckItem("med_art_242", "242. İyi Commit ve Branch Stratejisi", "Conventional Commits (feat, fix, docs) ve GitFlow / Trunk-Based."),
                    TopicCheckItem("med_art_243", "243. GitHub Repository Nasıl Düzenlenir?", "Issue şablonları, releases, lisans ve dosya organizasyonu."),
                    TopicCheckItem("med_art_244", "244. İyi README Nasıl Yazılır?", "Mimar şeması, badge'ler, kurulum adımları ve ekran görüntüleri."),
                    TopicCheckItem("med_art_245", "245. GitHub Actions'a Giriş", "Otomatik workflow'lar, trigger'lar ve CI test koşumları.")
                )
            ),

            // ----------------------------------------------------
            // 21. SOFTWARE ENGINEERING GENERAL CULTURE
            // ----------------------------------------------------
            TopicSection(
                title = "21. SOFTWARE ENGINEERING GENERAL CULTURE",
                emoji = "🌍",
                items = listOf(
                    TopicCheckItem("med_art_246", "246. Software Development Life Cycle (SDLC)", "Gereksinimden bakıma yazılımın yaşam döngüsü adımları."),
                    TopicCheckItem("med_art_247", "247. Git Flow ve Development Workflow'ları", "Feature branch'ler, release branch'ler ve ana akış disiplini."),
                    TopicCheckItem("med_art_248", "248. Code Review Neden Yapılır?", "Bilgi paylaşımı, kod kalitesi ve kolektif sorumluluk."),
                    TopicCheckItem("med_art_249", "249. Technical Debt Nedir?", "Bilinçli ve bilinçsiz teknik borç ve faiz maliyeti."),
                    TopicCheckItem("med_art_250", "250. Refactoring Nedir?", "Dış davranışı bozmadan iç yapıyı iyileştirme sanatı."),
                    TopicCheckItem("med_art_251", "251. Coupling ve Cohesion", "Düşük bağımlılık (Loose Coupling) ve yüksek odak (High Cohesion)."),
                    TopicCheckItem("med_art_252", "252. Abstraction Nedir ve Ne Zaman Kullanılmalı?", "Karmaşıklığı gizleme ve erken soyutlamanın tehlikeleri."),
                    TopicCheckItem("med_art_253", "253. Scalability Nedir?", "Dikey büyüme (Scale-Up) vs yatay büyüme (Scale-Out)."),
                    TopicCheckItem("med_art_254", "254. Reliability, Availability ve Fault Tolerance", "9'lar kuralı (%99.99), arıza toleransı ve güvenilirlik."),
                    TopicCheckItem("med_art_255", "255. Performance ve Optimization Nasıl Düşünülmeli?", "Ölçmeden optimize etmeme ve kritik patika analizi."),
                    TopicCheckItem("med_art_256", "256. Documentation Neden Önemlidir?", "Kodun 'nasıl'ını kod, 'neden'ini dokümantasyon anlatır."),
                    TopicCheckItem("med_art_257", "257. Semantic Versioning", "MAJOR.MINOR.PATCH semantiği ve kırıcı değişiklik kuralları."),
                    TopicCheckItem("med_art_258", "258. Backward Compatibility", "Geriye dönük uyumluluğun korunması ve API deprecation süreçleri."),
                    TopicCheckItem("med_art_259", "259. Configuration vs Code", "Kodu yeniden derlemeden davranışı değiştirebilme esnekliği."),
                    TopicCheckItem("med_art_260", "260. Convention vs Configuration", "Standart uzlaşılar ile gereksiz ayar yükünden kurtulma."),
                    TopicCheckItem("med_art_261", "261. Overengineering Nedir?", "Gereksiz karmaşıklık, geleceği tahmin etme yanılgısı ve YAGNI."),
                    TopicCheckItem("med_art_262", "262. Premature Optimization Neden Tehlikelidir?", "Zamanından önce yapılan optimizasyonun okunabilirlik bedeli."),
                    TopicCheckItem("med_art_263", "263. Technical Decision Nasıl Verilir?", "Architecture Decision Records (ADR) ve kararların belgelenmesi."),
                    TopicCheckItem("med_art_264", "264. Trade-off Kavramı ve Software Engineering", "Gümüş kurşun yoktur: Her mimari seçimin bir bedeli vardır."),
                    TopicCheckItem("med_art_265", "265. Production'a Çıkacak Bir Yazılımı Düşünmek", "Canlıya alma kontrol listesi, izlenebilirlik ve acil durum planı.")
                )
            ),

            // ----------------------------------------------------
            // 22. CS KİTAPLARINDAN ÇIKACAK DERİN YAZILAR
            // ----------------------------------------------------
            TopicSection(
                title = "22. CS KİTAPLARINDAN ÇIKACAK DERİN YAZILAR",
                emoji = "📖",
                items = listOf(
                    TopicCheckItem("med_art_266", "266. “Program Gerçekte Nasıl Çalışıyor?” — Computer Systems'a Giriş", "CS:APP perspektifinden programın donanım katmanında çalışması."),
                    TopicCheckItem("med_art_267", "267. Bir Programın CPU'da Çalışmasına Kadar Geçen Süreç", "Kaynak koddan derleyici, assembler, linker ve loader aşamaları."),
                    TopicCheckItem("med_art_268", "268. Programdan Process'e: İşletim Sisteminin Rolü", "OSTEP perspektifinden bellek haritalama ve process başlatma."),
                    TopicCheckItem("med_art_269", "269. Bellekten CPU Cache'ine: Memory Hierarchy", "Donanım önbelleklerinin modern yazılım performansına etkisi."),
                    TopicCheckItem("med_art_270", "270. Database Neden Hızlı veya Yavaş Olabilir?", "DDIA perspektifinden storage engine'ler, disk I/O ve B-tree vs LSM-tree."),
                    TopicCheckItem("med_art_271", "271. Distributed Systems Neden Zordur?", "Zaman uyumsuzluğu, ağ bölünmeleri (netsplit) ve mutlak hakikat eksikliği."),
                    TopicCheckItem("med_art_272", "272. Network, Database ve Application'ın Birlikte Çalışması", "Üç temel bileşenin uyum içinde yüksek hacimli trafiği taşıması."),
                    TopicCheckItem("med_art_273", "273. Reliability Nedir?", "Donanım ve yazılım hatalarına rağmen doğru çalışmaya devam etme."),
                    TopicCheckItem("med_art_274", "274. Scalability Nedir?", "Artan yük karşısında performansı koruyabilme kapasitesi."),
                    TopicCheckItem("med_art_275", "275. Consistency ve Availability Arasındaki Trade-off'lar", "CAP teoremi ve PACELC teoremine derin bakış.")
                )
            ),

            // ----------------------------------------------------
            // 23. “BÜYÜK KAVRAMLARI BİRLEŞTİREN” MAKALELER
            // ----------------------------------------------------
            TopicSection(
                title = "23. “BÜYÜK KAVRAMLARI BİRLEŞTİREN” MAKALELER",
                emoji = "🧩",
                items = listOf(
                    TopicCheckItem("med_art_276", "276. Bir Backend Request'inin Baştan Sona Yolculuğu", "DNS → TCP → TLS → HTTP → ASP.NET → Middleware → Controller → Service → EF Core → PostgreSQL → Response."),
                    TopicCheckItem("med_art_277", "277. Bir Android Uygulamasında Kullanıcı Tıklamasından Database'e Kadar Ne Oluyor?", "UI → ViewModel → Repository → Retrofit → HTTP → ASP.NET → EF Core → PostgreSQL."),
                    TopicCheckItem("med_art_278", "278. Authentication'ın Baştan Sona Yolculuğu", "Login → Password Hash → Database → JWT → Client Storage → Authorization → API Request."),
                    TopicCheckItem("med_art_279", "279. Bir API'nin Production'da Ayakta Kalması İçin Neler Gerekir?", "Configuration → Logging → Monitoring → Health Checks → Docker → Reverse Proxy → Database → Backup → Security."),
                    TopicCheckItem("med_art_280", "280. Modern Backend'in Temel Yapı Taşları", "API → Database → Cache → Queue → Background Job → Logging → Monitoring."),
                    TopicCheckItem("med_art_281", "281. Bir Kullanıcı İsteğinin Bir Distributed System İçinde Yolculuğu", "Load Balancer → API → Service → Queue → Worker → Database → Cache."),
                    TopicCheckItem("med_art_282", "282. Memory'den Uygulamaya: Bir Verinin CPU'dan Database'e ve Geriye Yolculuğu", "CS, backend ve sistem seviyesini birleştiren derin makale."),
                    TopicCheckItem("med_art_283", "283. Modern Yazılım Sistemlerinde State Nerede Tutulur?", "RAM → Database → Cache → Session → JWT → Client Storage."),
                    TopicCheckItem("med_art_284", "284. Performance Problemi Nasıl Araştırılır?", "Latency → Profiling → Logging → Metrics → Database → Network → CPU → Memory."),
                    TopicCheckItem("med_art_285", "285. Bir Yazılım Sisteminde “Güvenli” Olmak Ne Demektir?", "Authentication → Authorization → Secrets → Encryption → Input Validation → Network Security → Logging.")
                )
            )
        )
    )
}
