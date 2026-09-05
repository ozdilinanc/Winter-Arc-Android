package com.example.data.model

data class TopicCheckItem(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false
)

data class TopicSection(
    val title: String,
    val emoji: String,
    val items: List<TopicCheckItem>
)

data class SubItemRoadmap(
    val subItemId: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val targetLevel: String,
    val overview: String,
    val sections: List<TopicSection>
)

object RoadmapDataStore {

    val allRoadmaps: Map<String, SubItemRoadmap> = mapOf(
        // ==========================================
        // 1. BACKEND / .NET
        // ==========================================
        "sub_backend_dotnet" to SubItemRoadmap(
            subItemId = "sub_backend_dotnet",
            title = "Backend / .NET",
            subtitle = "C#, ASP.NET Core, EF Core & Mimari",
            emoji = "⚡",
            targetLevel = "Hedef: ~2 Yıllık Junior Seviyesi (Bağımsız Mimar)",
            overview = "Sıfırdan bir backend projesini ayağa kaldırabilen, mimarisini ve veritabanını tasarlayabilen, production-ready REST API'ler geliştirebilen güçlü bir .NET mühendisi olmak.",
            sections = listOf(
                TopicSection(
                    title = "PROGRAMLAMA (C#)",
                    emoji = "💻",
                    items = listOf(
                        TopicCheckItem("cs_syntax", "C# Temel Sözdizimi (Syntax)", "Veri tipleri, değişkenler, operatörler ve kontrol yapıları"),
                        TopicCheckItem("cs_oop", "OOP (Nesne Yönelimli Programlama)", "Encapsulation, Inheritance, Polymorphism, Abstraction"),
                        TopicCheckItem("cs_collections", "Collections & Generic Yapılar", "List, Dictionary, HashSet, IEnumerable, ICollection"),
                        TopicCheckItem("cs_generics", "Generics & Generic Constraints", "Type-safe kod yazımı, where T : class/struct"),
                        TopicCheckItem("cs_linq", "LINQ (Language Integrated Query)", "Select, Where, GroupBy, SelectMany, Any, All"),
                        TopicCheckItem("cs_async", "Async / Await & Concurrency", "Task, Task<T>, ThreadPool, ConfigureAwait, deadlock önleme"),
                        TopicCheckItem("cs_delegates", "Delegates & Events", "Action, Func, Predicate, EventHandler pratikleri"),
                        TopicCheckItem("cs_exceptions", "Exception Handling", "try-catch-finally, custom exception sınıfları"),
                        TopicCheckItem("cs_memory", "Memory & GC (Garbage Collector) Mantığı", "Stack vs Heap, Value vs Reference types, IDisposable")
                    )
                ),
                TopicSection(
                    title = "ASP.NET CORE",
                    emoji = "🌐",
                    items = listOf(
                        TopicCheckItem("asp_http_rest", "HTTP Protokolü & RESTful İlkeler", "HTTP metodları (GET, POST, PUT, DELETE), Status kodları"),
                        TopicCheckItem("asp_api_design", "API Tasarımı & Endpoint Hijyeni", "Resource isimlendirme, URI versiyonlama, standart response formatları"),
                        TopicCheckItem("asp_controllers", "Controllers & Minimal APIs", "ControllerBase, ActionResults, Endpoint routing"),
                        TopicCheckItem("asp_middleware", "Middleware Pipeline Mantığı", "Custom middleware yazımı, Request/Response akışı"),
                        TopicCheckItem("asp_di", "Dependency Injection (DI)", "Transient, Scoped, Singleton yaşam döngüleri"),
                        TopicCheckItem("asp_config", "Configuration & AppSettings", "IOptions<T> pattern, environment değişkenleri"),
                        TopicCheckItem("asp_routing", "Routing & Model Binding", "Route constraints, FromRoute, FromQuery, FromBody"),
                        TopicCheckItem("asp_validation", "Validation (FluentValidation)", "Model validasyonu, custom validator sınıfları"),
                        TopicCheckItem("asp_serialization", "Serialization (System.Text.Json)", "JSON serializer options, converter'lar"),
                        TopicCheckItem("asp_errors", "Global Error & Exception Handling", "ProblemDetails, IExceptionHandler, global middleware"),
                        TopicCheckItem("asp_logging", "Logging (Serilog / ILogger)", "Structured logging, log seviyeleri (Info, Warn, Error)"),
                        TopicCheckItem("asp_swagger", "Swagger / OpenAPI Dokümantasyonu", "Swashbuckle, XML comments, API test arayüzü")
                    )
                ),
                TopicSection(
                    title = "DATABASE & EF CORE",
                    emoji = "🗄️",
                    items = listOf(
                        TopicCheckItem("db_sql", "SQL Temelleri & Sorgu Yetkinliği", "SELECT, JOIN, Aggregations, Subqueries"),
                        TopicCheckItem("db_postgres", "PostgreSQL Yönetimi", "PostgreSQL veri tipleri, sequence'lar, psql kullanımı"),
                        TopicCheckItem("db_design", "Database Tasarımı & Modelleme", "Tablo ilişkileri (1-1, 1-N, N-N), Foreign Keys"),
                        TopicCheckItem("db_normalization", "Normalizasyon (1NF, 2NF, 3NF)", "Veri tekrarını önleme, tutarlı şema tasarımı"),
                        TopicCheckItem("db_indexes", "İndeksleme & Sorgu Optimizasyonu", "B-tree index, Composite index, EXPLAIN ANALYZE"),
                        TopicCheckItem("db_transactions", "Transactions & ACID Prensipleri", "Atomicity, Consistency, Isolation, Durability"),
                        TopicCheckItem("db_concurrency", "Concurrency & Isolation Seviyeleri", "Read Committed, Serializable, Optimistic/Pessimistic Locking"),
                        TopicCheckItem("db_efcore", "Entity Framework Core (EF Core)", "DbContext, DbSet, Fluent API konfigürasyonu"),
                        TopicCheckItem("db_migrations", "EF Core Migrations", "dotnet ef migrations add, database update pratikleri"),
                        TopicCheckItem("db_tracking", "Change Tracker & AsNoTracking", "Performans için AsNoTracking kullanımı, entity state"),
                        TopicCheckItem("db_linq_sql", "LINQ ➔ SQL Mantığı (Expression Trees)", "IQueryable vs IEnumerable, N+1 problemi & AsSplitQuery")
                    )
                ),
                TopicSection(
                    title = "MİMARİ & YAZILIM TASARIMI",
                    emoji = "🏛️",
                    items = listOf(
                        TopicCheckItem("arch_solid", "SOLID Prensipleri", "Single Responsibility, Open-Closed, Liskov, Interface Segregation, Dependency Inversion"),
                        TopicCheckItem("arch_layered", "Katmanlı Mimari (N-Tier)", "Presentation, Business, Data Access katmanları"),
                        TopicCheckItem("arch_clean", "Clean Architecture", "Domain, Application, Infrastructure, Presentation ayrımı"),
                        TopicCheckItem("arch_onion", "Onion Architecture", "Core domain merkezli bağımlılık yönü (Inward Dependency)"),
                        TopicCheckItem("arch_repo_service", "Repository & Service Pattern", "Generic repository, iş mantığının servislere izolasyonu"),
                        TopicCheckItem("arch_dto", "DTO & AutoMapper/Mapster", "Entity - DTO dönüşümleri, domain nesnelerini dışarı açmama"),
                        TopicCheckItem("arch_patterns", "Design Patterns (GoF)", "Factory, Strategy, Singleton, Decorator, Mediator"),
                        TopicCheckItem("arch_domain", "Domain Mantığı & Zengin Modeller", "Anemic domain modelden kaçınma, iş kuralları"),
                        TopicCheckItem("arch_testability", "Test Edilebilir Kod Tasarımı", "Mocking (Moq/NSubstitute), bağımlılıkların soyutlanması")
                    )
                ),
                TopicSection(
                    title = "AUTH & GÜVENLİK",
                    emoji = "🔐",
                    items = listOf(
                        TopicCheckItem("sec_authn_authz", "Authentication vs Authorization", "Kimlik doğrulama ile yetkilendirme farkı"),
                        TopicCheckItem("sec_jwt", "JWT (JSON Web Token)", "Header, Payload, Signature, Access & Refresh Token döngüsü"),
                        TopicCheckItem("sec_password", "Password Hashing (BCrypt / Argon2)", "Salt, hash maliyet faktörü, güvenli parola saklama"),
                        TopicCheckItem("sec_roles_claims", "Roles & Claims-Based Authorization", "Policy tabanlı yetkilendirme, [Authorize(Policy = ...)]"),
                        TopicCheckItem("sec_oauth", "OAuth2 & OpenID Connect Temelleri", "Token exchange, SSO ve üçüncü parti giriş mantığı"),
                        TopicCheckItem("sec_cors_https", "CORS & HTTPS Güvenliği", "Allowed origins, headers, SSL/TLS sertifikaları"),
                        TopicCheckItem("sec_vulns", "Genel Güvenlik Açıkları (OWASP)", "SQL Injection, XSS, CSRF, Mass Assignment önleme")
                    )
                ),
                TopicSection(
                    title = "PRODUCTION-READY BACKEND",
                    emoji = "🚀",
                    items = listOf(
                        TopicCheckItem("prod_testing", "Unit & Integration Testing (xUnit)", "xUnit, FluentAssertions, WebApplicationFactory"),
                        TopicCheckItem("prod_logging", "Structured Logging & Serilog", "Kayıtların JSON formatında merkezi log sistemlerine hazır olması"),
                        TopicCheckItem("prod_bg_jobs", "Background Jobs (Hangfire / IHostedService)", "Zamanlanmış görevler, periyodik veri işleme"),
                        TopicCheckItem("prod_caching", "Caching Stratejileri (Memory & Redis)", "In-memory cache, Distributed Redis cache, Cache Invalidation"),
                        TopicCheckItem("prod_rate_limit", "Rate Limiting & Throttling", "API abuse ve DoS saldırılarına karşı istek sınırlama"),
                        TopicCheckItem("prod_health", "Health Checks", "Veritabanı ve dış servis sağlık durum kontrolleri (/health)")
                    )
                )
            )
        ),

        // ==========================================
        // 2. DEVOPS & DISTRIBUTED SYSTEMS
        // ==========================================
        "sub_devops_dist" to SubItemRoadmap(
            subItemId = "sub_devops_dist",
            title = "DevOps / Distributed Systems",
            subtitle = "Docker, RabbitMQ, Microservices & K8s",
            emoji = "☸️",
            targetLevel = "Hedef: Aşinalık + Dağıtık Sistem Mantığını Kavrama",
            overview = "Bir backend uygulamasını Docker container'a koyup, RabbitMQ ile servisleri asenkron haberleştirebilen, Kubernetes ve monitoring (Prometheus/Grafana) mantığını anlayan bir sistem bakışı kazanmak.",
            sections = listOf(
                TopicSection(
                    title = "CONTAINERS (DOCKER)",
                    emoji = "🐳",
                    items = listOf(
                        TopicCheckItem("doc_fundamentals", "Docker Temelleri", "Container vs Sanal Makine, Docker daemon, Image vs Container"),
                        TopicCheckItem("doc_dockerfile", "Dockerfile Yazımı (.NET için)", "Multi-stage build, minimal image boyutları (alpine/chiseled)"),
                        TopicCheckItem("doc_compose", "Docker Compose", "Çoklu servis orkestrasyonu (.NET + Postgres + Redis + RabbitMQ)"),
                        TopicCheckItem("doc_volumes", "Volumes & Kalıcı Depolama", "Named volumes, bind mounts, veritabanı verisi saklama"),
                        TopicCheckItem("doc_networks", "Docker Networks", "Bridge network, container'lar arası DNS ve haberleşme")
                    )
                ),
                TopicSection(
                    title = "MESSAGING & QUEUES (RABBITMQ)",
                    emoji = "📨",
                    items = listOf(
                        TopicCheckItem("msg_concept", "Message Queue Mantığı & Neden?", "Asenkron işleme, servis ayrışması (decoupling), yük dengeleme"),
                        TopicCheckItem("msg_rabbitmq", "RabbitMQ Çekirdek Kavramları", "Producer, Consumer, Exchange, Queue, Routing Key"),
                        TopicCheckItem("msg_exchanges", "Exchange Tipleri", "Direct, Topic, Fanout, Headers exchange modelleri"),
                        TopicCheckItem("msg_retry_dlq", "Retry & Dead Letter Queue (DLQ)", "Hatalı mesajların tekrar denenmesi ve DLQ'ya aktarımı")
                    )
                ),
                TopicSection(
                    title = "MICROSERVICES",
                    emoji = "🧩",
                    items = listOf(
                        TopicCheckItem("ms_monolith", "Monolith vs Modular Monolith vs Microservices", "Ne zaman hangisi seçilmeli? Dağıtık monolit tuzağı"),
                        TopicCheckItem("ms_comm", "Servisler Arası Haberleşme", "Senkron (REST/gRPC) vs Asenkron (Event-Driven / Pub-Sub)"),
                        TopicCheckItem("ms_gateway", "API Gateway (Ocelot / YARP)", "Ters proxy, routing, merkezi kimlik denetimi"),
                        TopicCheckItem("ms_saga", "Dağıtık Transaction & Saga Pattern", "2PC yerine koreografi / orkestrasyon tabanlı tutarlılık")
                    )
                ),
                TopicSection(
                    title = "KUBERNETES & MONITORING",
                    emoji = "☸️",
                    items = listOf(
                        TopicCheckItem("k8s_basics", "Kubernetes Kavramları", "Cluster, Node, Pod, Deployment, ReplicaSet"),
                        TopicCheckItem("k8s_networking", "K8s Servisleri & Ingress", "ClusterIP, NodePort, LoadBalancer, Ingress Controller"),
                        TopicCheckItem("k8s_lens", "Kubernetes Yönetimi & Lens", "K8s cluster'ını görsel arayüzle izleme ve pod logları"),
                        TopicCheckItem("k8s_monitoring", "Prometheus & Grafana", "Metrik toplama, panolar, CPU/bellek ve istek sayısı takibi")
                    )
                ),
                TopicSection(
                    title = "CLOUD & CI/CD",
                    emoji = "☁️",
                    items = listOf(
                        TopicCheckItem("cloud_azure", "Azure Temelleri", "App Service, Azure SQL, Blob Storage mantığı"),
                        TopicCheckItem("cloud_cicd", "CI/CD Pipeline (GitHub Actions)", "Otomatik test koşumu, build alma ve container push")
                    )
                )
            )
        ),

        // ==========================================
        // 3. ANDROID (NATIVE)
        // ==========================================
        "sub_android" to SubItemRoadmap(
            subItemId = "sub_android",
            title = "Native Android",
            subtitle = "Kotlin, Jetpack Compose, MVVM & Hilt",
            emoji = "🤖",
            targetLevel = "Hedef: Bağımsız Mobil Ürün Geliştirme & Play Store",
            overview = "Backend uzmanlığına paralel olarak cebinde native Android üretim gücünü korumak; AI agent'ları ile küçük, şık ve üretime hazır uygulamalar geliştirip Play Store'da yayınlamak.",
            sections = listOf(
                TopicSection(
                    title = "KOTLIN DİL YETKİNLİĞİ",
                    emoji = "💎",
                    items = listOf(
                        TopicCheckItem("kt_fundamentals", "Kotlin Temelleri & Null Safety", "val vs var, nullables (?), safe calls (?.), elvis operator (?:)"),
                        TopicCheckItem("kt_oop", "Kotlin OOP (Data Classes, Sealed Classes)", "data class, sealed interface/class ile State modelleme"),
                        TopicCheckItem("kt_coroutines", "Coroutines & Asenkron Programlama", "suspend fonksiyonlar, launch, async, Dispatchers.IO/Main"),
                        TopicCheckItem("kt_flow", "Flow & StateFlow / SharedFlow", "Reaktif veri akışları, UI state yönetimi ve collectAsState"),
                        TopicCheckItem("kt_extensions", "Extension Functions & Scope Functions", "let, run, apply, also ve custom extension'lar")
                    )
                ),
                TopicSection(
                    title = "ANDROID & JETPACK",
                    emoji = "📱",
                    items = listOf(
                        TopicCheckItem("and_lifecycle", "Activity & Yaşam Döngüsü (Lifecycle)", "Lifecycle states, konfigürasyon değişiklikleri"),
                        TopicCheckItem("and_compose", "Jetpack Compose Modern UI", "Composables, State hoisting, LazyColumn, Canvas, Material 3"),
                        TopicCheckItem("and_viewmodel", "ViewModel & UI State Mimarisi", "Ekran döndürmede veri koruma, unidirectional data flow (UDF)"),
                        TopicCheckItem("and_retrofit", "Retrofit & REST API Entegrasyonu", "HTTP istekleri, Moshi/Gson serialization, Interceptors"),
                        TopicCheckItem("and_room", "Room Database (Offline-First)", "Entity, Dao, SQLite işlemleri, reaktif veri tabanı akışları"),
                        TopicCheckItem("and_nav", "Navigation Component", "Compose Navigation, deeplinks ve ekranlar arası parametre geçişi")
                    )
                ),
                TopicSection(
                    title = "MİMARİ & ÜRETİM DÖNGÜSÜ",
                    emoji = "🚀",
                    items = listOf(
                        TopicCheckItem("and_mvvm_clean", "MVVM & Clean Architecture", "Data Layer (Repository), Domain Layer (UseCases), UI Layer"),
                        TopicCheckItem("and_di", "Dependency Injection (Hilt / Koin)", "Android sınıflarına bağımlılık enjeksiyonu"),
                        TopicCheckItem("and_release", "Play Store Yayın Süreci", "Keystore imzalama, App Bundle (.aab), gizlilik politikası ve sürüm yayını")
                    )
                )
            )
        ),

        // ==========================================
        // 4. KİŞİSEL PROJELER
        // ==========================================
        "sub_personal_projects" to SubItemRoadmap(
            subItemId = "sub_personal_projects",
            title = "Kişisel Projeler",
            subtitle = "Uçtan Uca Geliştirilen Bağımsız Projeler",
            emoji = "🛠️",
            targetLevel = "Hedef: 2-3 Ciddi Backend + Mobil Portföy Projesi",
            overview = "Teorik bilgileri ürüne dönüştüren; temiz mimari, testler, CI/CD ve dokümantasyon içeren anahtar mühendislik projeleri.",
            sections = listOf(
                TopicSection(
                    title = "ÖNE ÇIKAN PROJELER",
                    emoji = "🏆",
                    items = listOf(
                        TopicCheckItem("proj_pharma", "Eczane Yönetim & Dağıtım Sistemi", "ASP.NET Core, Clean Architecture, PostgreSQL, JWT, Docker"),
                        TopicCheckItem("proj_kv", "LLM KV Cache / vAttention C++ Optimizasyonu", "Bitirme Tezi: Linux OS paging, C++, Ollama/LLaMA entegrasyonu"),
                        TopicCheckItem("proj_android_st", "Winter Arc: Mühendislik Roadmap & Skill Tree", "Native Android, Jetpack Compose, Room DB, M3 Dark Theme"),
                        TopicCheckItem("proj_order_ms", "Event-Driven Sipariş Mikroservisi", "RabbitMQ, MassTransit, Docker Compose, Outbox Pattern")
                    )
                ),
                TopicSection(
                    title = "PROJE KALİTE STANDARTLARI",
                    emoji = "✨",
                    items = listOf(
                        TopicCheckItem("pstd_readme", "Profesyonel README Vitrini", "Mimari diyagramlar, kurulum adımları, cURL örnekleri"),
                        TopicCheckItem("pstd_docker", "Tek Komutla Çalıştırma (docker-compose up)", "Veritabanı ve bağımlılıkların containerized ayağa kalkması"),
                        TopicCheckItem("pstd_tests", "Birim & Entegrasyon Testleri", "Kritik iş akışlarını doğrulayan xUnit testleri")
                    )
                )
            )
        ),

        // ==========================================
        // 5. BTK AKADEMİ
        // ==========================================
        "sub_btk_akademi" to SubItemRoadmap(
            subItemId = "sub_btk_akademi",
            title = "BTK Akademi",
            subtitle = "Sertifikalar: Öğrenmenin Yan Ürünü",
            emoji = "📜",
            targetLevel = "Hedef: İlgili Konuları Bitirdikçe Belgelendirme",
            overview = "Sertifika toplamak için değil; .NET, C#, SQL veya Git öğrenirken kaliteli bir BTK kursu varsa onu tamamlayıp yan ürün olarak sertifikasını almak.",
            sections = listOf(
                TopicSection(
                    title = "HEDEFLENEN EĞİTİMLER",
                    emoji = "🎓",
                    items = listOf(
                        TopicCheckItem("btk_csharp", "C# ile Nesne Yönelimli Programlama", "C# dil temelleri ve OOP konseptleri sertifikası"),
                        TopicCheckItem("btk_dotnet", "ASP.NET Core Web API Geliştirme", "RESTful API ve .NET backend geliştirme eğitimi"),
                        TopicCheckItem("btk_sql", "İlişkisel Veritabanları & SQL Eğitimi", "SQL sorgulama, tablo tasarımı ve indeksleme"),
                        TopicCheckItem("btk_git", "Versiyon Kontrol Sistemleri: Git & GitHub", "Git branching, PR yönetimi ve GitHub iş akışı"),
                        TopicCheckItem("btk_security", "Temel Siber Güvenlik & Ağ Temelleri", "Web ve ağ güvenliği genel kültür eğitimi")
                    )
                )
            )
        ),

        // ==========================================
        // 6. GIT / GITHUB
        // ==========================================
        "sub_git_github" to SubItemRoadmap(
            subItemId = "sub_git_github",
            title = "Git / GitHub",
            subtitle = "Versiyon Kontrol & İş Akışı Kültürü",
            emoji = "🐙",
            targetLevel = "Hedef: Kasım 2027'de Yemyeşil ve Nitelikli Portföy",
            overview = "Git'i sadece 'commit & push' için değil, profesyonel branching stratejileri, temiz commit geçmişi ve GitHub Actions ile entegre kullanmak.",
            sections = listOf(
                TopicSection(
                    title = "GIT YETKİNLİKLERİ",
                    emoji = "🌿",
                    items = listOf(
                        TopicCheckItem("git_branch", "Branching Stratejileri (Feature branching, GitFlow)", "Temiz branch yönetimi"),
                        TopicCheckItem("git_merge_rebase", "Merge vs Rebase & Cherry-Pick", "Rebase ile commit geçmişini düzleştirmek"),
                        TopicCheckItem("git_conflicts", "Conflict Çözümü & Stash", "Kod çakışmalarını terminalde güvenle çözmek"),
                        TopicCheckItem("git_commit_style", "Conventional Commits", "feat, fix, refactor, chore standartlarında commit mesajları")
                    )
                ),
                TopicSection(
                    title = "GITHUB & İŞBİRLİĞİ",
                    emoji = "🤝",
                    items = listOf(
                        TopicCheckItem("gh_pr", "Pull Request (PR) & Code Review", "Açıklayıcı PR açıklamaları ve kod inceleme kültürü"),
                        TopicCheckItem("gh_actions", "GitHub Actions CI Pipeline", "Push ve PR'larda otomatik test ve build tetikleme"),
                        TopicCheckItem("gh_profile", "GitHub Profil README & Vitrini", "En iyi projelerin pinlenmesi ve teknik yetenek vitrini")
                    )
                )
            )
        ),

        // ==========================================
        // 7. LINUX / TERMINAL
        // ==========================================
        "sub_linux_terminal" to SubItemRoadmap(
            subItemId = "sub_linux_terminal",
            title = "Linux / Terminal",
            subtitle = "Bash, CLI Araçları & Çekirdek Mantığı",
            emoji = "🐧",
            targetLevel = "Hedef: Terminali Tercih Eden Bir Mühendis Olmak",
            overview = "Zaten Linux kullanıcısı olarak terminal refleksini derinleştirmek; dosya hiyerarşisi, process kontrolü ve C++ için terminal/LazyVim iş akışına hakim olmak.",
            sections = listOf(
                TopicSection(
                    title = "GÜNLÜK CLI ARAÇLARI",
                    emoji = "⚡",
                    items = listOf(
                        TopicCheckItem("lin_fs_perm", "Dosya Hiyerarşisi & İzinler (chmod, chown)", "/etc, /var, /proc, rwx izin hesapları"),
                        TopicCheckItem("lin_proc", "Process Yönetimi (ps, top, htop, kill, pkill)", "PID, background jobs, sinyaller (SIGKILL, SIGTERM)"),
                        TopicCheckItem("lin_grep_find", "Metin & Dosya Arama (grep, find, awk, sed)", "Boru hatları (pipes |) ve yönlendirmeler (>, >>)"),
                        TopicCheckItem("lin_ssh_curl", "Ağ Araçları (curl, wget, ssh, scp, netstat)", "Uzak sunucuya SSH ile bağlanma, API istekleri"),
                        TopicCheckItem("lin_sys_logs", "Servis & Log Yönetimi (systemctl, journalctl)", "Daemon servisleri başlatma, log inceleme"),
                        TopicCheckItem("lin_bash", "Bash Betikleme (Bash Scripting)", "Otomasyon betikleri, döngüler, environment değişkenleri")
                    )
                ),
                TopicSection(
                    title = "GELİŞTİRME ORTAMI (LAZYVIM & C++)",
                    emoji = "⌨️",
                    items = listOf(
                        TopicCheckItem("lin_lazyvim", "LazyVim / Neovim Editör Alışkanlığı", "Klavye kısayolları, dosya gezgini, LSP entegrasyonu"),
                        TopicCheckItem("lin_cpp_build", "C++ Terminal Derleme (g++, clang++, make, cmake)", "Derleme bayrakları, optimizasyon seviyeleri (-O2, -O3)")
                    )
                )
            )
        ),

        // ==========================================
        // 8. SİBER GÜVENLİK
        // ==========================================
        "sub_cyber_security" to SubItemRoadmap(
            subItemId = "sub_cyber_security",
            title = "Cyber Security",
            subtitle = "AppSec, Web & Ağ Güvenliği Genel Kültürü",
            emoji = "🛡️",
            targetLevel = "Hedef: İyi Bir Backend Geliştiricinin Güvenlik Kültürü",
            overview = "Siber güvenlik uzmanı olmak değil; iyi bir bilgisayar mühendisi olarak savunma pratiklerini, kriptografi temellerini ve OWASP risklerini sohbet tadında kavramak.",
            sections = listOf(
                TopicSection(
                    title = "KRİPTOGRAFİ & İLETİŞİM GÜVENLİĞİ",
                    emoji = "🔑",
                    items = listOf(
                        TopicCheckItem("sec_hash_enc", "Hashing vs Encryption (Şifreleme)", "Tek yönlü hash (SHA-256) vs iki yönlü şifreleme (AES)"),
                        TopicCheckItem("sec_tls", "HTTPS & TLS El Sıkışması", "Sertifikalar, asimetrik & simetrik şifreleme süreci"),
                        TopicCheckItem("sec_pw", "Güvenli Parola Saklama", "Salt, Pepper, BCrypt, PBKDF2 ve rainbow table savunması")
                    )
                ),
                TopicSection(
                    title = "WEB & API SALDIRILARI VE SAVUNMA",
                    emoji = "🛡️",
                    items = listOf(
                        TopicCheckItem("sec_sqli", "SQL Injection & Savunma", "Parameterized queries, ORM kullanımı, raw SQL tehlikesi"),
                        TopicCheckItem("sec_xss_csrf", "XSS & CSRF Saldırıları", "Input sanitization, Content-Security-Policy, SameSite çerezler"),
                        TopicCheckItem("sec_jwt_defense", "JWT Güvenlik Tuzakları", "Algoritma karışıklığı (None attack), secret anahtar gücü")
                    )
                )
            )
        ),

        // ==========================================
        // 9. SOFTWARE ENGINEERING FUNDAMENTALS
        // ==========================================
        "sub_swe_fundamentals" to SubItemRoadmap(
            subItemId = "sub_swe_fundamentals",
            title = "Software Engineering Fundamentals",
            subtitle = "SOLID, Clean Code & Tasarım Desenleri",
            emoji = "📐",
            targetLevel = "Hedef: Okunabilir, Bakımı Kolay, Profesyonel Kod",
            overview = "Kodun sadece çalışması yetmez; başkası (ve 6 ay sonraki kendin) okuduğunda anlaşılabilir ve değiştirilebilir olması gerekir.",
            sections = listOf(
                TopicSection(
                    title = "PRENSİPLER & TEMİZ KOD",
                    emoji = "🧼",
                    items = listOf(
                        TopicCheckItem("swe_solid", "SOLID Prensipleri Derinlemesine", "S-O-L-I-D kurallarının gerçek projede uygulanışı"),
                        TopicCheckItem("swe_cleancode", "Clean Code Prensipleri", "Anlamlı isimlendirme, küçük fonksiyonlar, yan etkisizlik"),
                        TopicCheckItem("swe_dry_kiss", "DRY, KISS & YAGNI", "Kod tekrarından kaçınma, gereksiz karmaşıklıktan uzak durma"),
                        TopicCheckItem("swe_refactoring", "Refactoring Teknikleri", "Code smells tespiti, güvenli adımlarla kodu iyileştirme")
                    )
                ),
                TopicSection(
                    title = "TASARIM DESENLERİ (DESIGN PATTERNS)",
                    emoji = "🧩",
                    items = listOf(
                        TopicCheckItem("swe_creational", "Creational Patterns", "Factory Method, Abstract Factory, Builder, Singleton"),
                        TopicCheckItem("swe_structural", "Structural Patterns", "Adapter, Decorator, Facade, Proxy"),
                        TopicCheckItem("swe_behavioral", "Behavioral Patterns", "Strategy, Observer, Command, Chain of Responsibility")
                    )
                )
            )
        ),

        // ==========================================
        // 10. İNGİLİZCE (B2+ AKTİF)
        // ==========================================
        "sub_english" to SubItemRoadmap(
            subItemId = "sub_english",
            title = "İngilizce (B2+)",
            subtitle = "Oxford 5000, Speaking, Listening & Writing",
            emoji = "🇬🇧",
            targetLevel = "Hedef: Aktif ve Akıcı B2+ Teknik İletişim",
            overview = "İngilizceyi sadece sınav geçmek için değil; teknik dokümanları anında okumak, yapay zeka ile speaking yapmak ve mesleki mülakatları rahatça yönetmek için aktif alışkanlık haline getirmek.",
            sections = listOf(
                TopicSection(
                    title = "KELİME & VOCABULARY (OXFORD 5000)",
                    emoji = "📖",
                    items = listOf(
                        TopicCheckItem("eng_oxford_a", "Oxford 5000: A1 & A2 Kelimeleri", "Kişisel kelime uygulamasında temel seviyeleri tamamlama"),
                        TopicCheckItem("eng_oxford_b1", "Oxford 5000: B1 Kelimeleri", "Orta seviye günlük ve akademik kelimeleri pekiştirme"),
                        TopicCheckItem("eng_oxford_b2", "Oxford 5000: B2 & B2+ Kelimeleri", "İleri seviye kelimeleri aktif konuşma/yazmada kullanma")
                    )
                ),
                TopicSection(
                    title = "SPEAKING & DİLEMAR (AKTİF KULLANIM)",
                    emoji = "🎙️",
                    items = listOf(
                        TopicCheckItem("eng_spk_ai", "ChatGPT / AI ile Düzenli Sesli Konuşma", "Haftada 3-4 seans serbest ve teknik sohbet"),
                        TopicCheckItem("eng_spk_tech", "Yazılım & Mimari Anlatma Pratiği", "Geliştirilen backend ve projeleri İngilizce anlatma"),
                        TopicCheckItem("eng_spk_interview", "İngilizce Teknik Mülakat Simülasyonu", "Kendini tanıtma, tecrübeleri ve projeleri sunma")
                    )
                ),
                TopicSection(
                    title = "LISTENING, READING & WRITING",
                    emoji = "🎧",
                    items = listOf(
                        TopicCheckItem("eng_list_tech", "Teknik Podcast & YouTube Konferansları", "GOTO Conferences, NDC, .NET Conf sunumları dinleme"),
                        TopicCheckItem("eng_read_docs", "Teknik Dokümantasyon & Kitap Okuma", "Microsoft Docs ve CS kitaplarını orijinal dilinde okuma"),
                        TopicCheckItem("eng_write_notes", "Medium Makaleleri & Teknik Notlar", "Medium yazılarını veya özetleri İngilizce kaleme alma")
                    )
                )
            )
        ),

        // ==========================================
        // 11. 2. YABANCI DİL (İSPANYOLCA)
        // ==========================================
        "sub_second_language" to SubItemRoadmap(
            subItemId = "sub_second_language",
            title = "2. Yabancı Dil: İspanyolca",
            subtitle = "Hobi, Kültür & Eğlence Dili",
            emoji = "🇪🇸",
            targetLevel = "Durum: 🔒 KİLİTLİ (İngilizce B2+ Sonrası)",
            overview = "İspanyolca şu anda acelesi olmayan keyifli bir hobi hedefi. İngilizce B2+ seviyesine yerleştikten sonra eğlenerek, stressiz şekilde temelleri atılacak.",
            sections = listOf(
                TopicSection(
                    title = "İSPANYOLCA TEMELLERİ (GELECEK HEDEFİ)",
                    emoji = "🇪🇸",
                    items = listOf(
                        TopicCheckItem("es_alphabet", "İspanyolca Alfabe & Fonetik Okuma", "Harf sesleri ve telaffuz kuralları"),
                        TopicCheckItem("es_greetings", "Günlük Selamlaşma & Temel İfadeler", "Hola, gracias, ¿cómo estás? ve tanışma kalıpları"),
                        TopicCheckItem("es_vocab_basic", "En Çok Kullanılan 500 Kelime", "Günlük nesneler, sayılar, günler ve temel fiiller"),
                        TopicCheckItem("es_grammar_intro", "Temel Gramer (Ser vs Estar, Şimdiki Zaman)", "Temel cümle kurma yapıları")
                    )
                )
            )
        ),

        // ==========================================
        // 12. PORTFÖY ÇIKTILARI (GITHUB, PLAY STORE, MEDIUM, LINKEDIN, PROJELER)
        // ==========================================
        "sub_github" to SubItemRoadmap(
            subItemId = "sub_github",
            title = "GitHub",
            subtitle = "Açık Kaynak Depoları & Katkılar",
            emoji = "🐙",
            targetLevel = "Kasım 2027 Hedefi: Düzenli, Yemyeşil ve Nitelikli",
            overview = "300 tane boş commit yerine; 15-20 tane gerçekten çalışan, README'si ve mimari çizimi olan nitelikli mühendislik depoları.",
            sections = listOf(
                TopicSection(
                    title = "GITHUB HEDEFLERİ",
                    emoji = "🟩",
                    items = listOf(
                        TopicCheckItem("gh_clean_repos", "Temiz & Açıklayıcı Depo İsimlendirmeleri", "Her projede amaca uygun isimlendirme"),
                        TopicCheckItem("gh_pro_readme", "Kapsamlı README & Mimari Şemaları", "Ekran görüntüleri, mimari diyagramlar ve çalıştırma adımları"),
                        TopicCheckItem("gh_commit_streak", "Düzenli Geliştirme & Yeşil Takvim", "Yazılım geliştirme disiplinini yansıtan düzenli commitler")
                    )
                )
            )
        ),
        "sub_play_store" to SubItemRoadmap(
            subItemId = "sub_play_store",
            title = "Play Store",
            subtitle = "Canlı Mobil Uygulamalar",
            emoji = "📱",
            targetLevel = "Hedef: Canlıda Çalışan 2-3 Native Android Uygulaması",
            overview = "Kullanıcıya dokunan, Google Play Store'da yayınlanmış ve üretim deneyimini kanıtlayan native Android uygulamaları.",
            sections = listOf(
                TopicSection(
                    title = "PLAY STORE ÇIKTILARI",
                    emoji = "🏪",
                    items = listOf(
                        TopicCheckItem("ps_winterarc", "Winter Arc Skill Tree Uygulaması", "Play Store'da öğrencilere yönelik yayın hazırlığı"),
                        TopicCheckItem("ps_vocab_app", "Oxford 5000 Kelime Takip Uygulaması", "Kişisel İngilizce kelime aracını canlıya alma"),
                        TopicCheckItem("ps_ai_mini_app", "AI Destekli Niş Bir Mobil Uygulama", "Küçük ama faydalı bir üretkenlik aracı")
                    )
                )
            )
        ),
        "sub_medium" to SubItemRoadmap(
            subItemId = "sub_medium",
            title = "Medium",
            subtitle = "Öğrendiklerimi Unutmama Sistemi",
            emoji = "✍️",
            targetLevel = "Hedef: Kişisel Teknik Hafıza & Bilgi Kütüphanesi",
            overview = "Amaç influencer olmak değil; öğrenilen her kritik mimariyi, JWT'yi, EF Core tracking mantığını veya Docker yapılandırmasını yazıya dökerek kalıcı kılmak.",
            sections = listOf(
                TopicSection(
                    title = "PLANLANAN TEKNİK YAZILAR",
                    emoji = "📝",
                    items = listOf(
                        TopicCheckItem("med_exc_handling", "ASP.NET Core'da Global Exception Handling Nasıl Yapılır?", "ProblemDetails ve IExceptionHandler kullanımı"),
                        TopicCheckItem("med_jwt_auth", "JWT ile Güvenli Authentication ve Refresh Token Akışı", "Adım adım token yaşam döngüsü"),
                        TopicCheckItem("med_clean_arch", "Neden Clean Architecture? Katmanlar Arası Sınırlar", "Domain ve Application katmanlarının izolasyonu"),
                        TopicCheckItem("med_ef_tracking", "EF Core Tracking Mantığı ve AsNoTracking Önemi", "Bellek tasarrufu ve performans farkları"),
                        TopicCheckItem("med_rabbitmq", "RabbitMQ ile Asenkron İletişim: Producer & Consumer", "Event-driven mimaride mesaj kuyruğu pratiği"),
                        TopicCheckItem("med_kv_cache", "LLM Çıkarımlarında KV Cache ve vAttention Nedir?", "Bitirme tezinden çıkan teorik özet")
                    )
                )
            )
        ),
        "sub_linkedin" to SubItemRoadmap(
            subItemId = "sub_linkedin",
            title = "LinkedIn",
            subtitle = "Profesyonel Ağ & Görünürlük",
            emoji = "💼",
            targetLevel = "Hedef: Mezuniyet Öncesi Güçlü Network & Profil",
            overview = "Tamamlanan projeleri, Medium yazılarını ve kazanılan teknik yetkinlikleri sektördeki mühendislerle ve liderlerle paylaşmak.",
            sections = listOf(
                TopicSection(
                    title = "PROFİL VE PAYLAŞIM STRATEJİSİ",
                    emoji = "🌐",
                    items = listOf(
                        TopicCheckItem("li_headline", "Net ve Profesyonel Başlık (Headline)", "Junior Backend Developer | Computer Engineering Student"),
                        TopicCheckItem("li_project_posts", "Proje Demo Videoları & GitHub Linkleri", "Tamamlanan projelerin kısa video gösterimleri"),
                        TopicCheckItem("li_medium_share", "Medium Makalelerinin Paylaşımı", "Yazılan teknik yazıların özetleriyle birlikte linklenmesi")
                    )
                )
            )
        ),
        "sub_projects" to SubItemRoadmap(
            subItemId = "sub_projects",
            title = "Projeler",
            subtitle = "Öne Çıkan Mühendislik Eserleri",
            emoji = "🏆",
            targetLevel = "Hedef: CV'de Fark Yaratan Sağlam Referanslar",
            overview = "Özgeçmişte ve teknik mülakatlarda en ince ayrıntısına kadar anlatabileceğin, kod kalitesiyle gurur duyduğun ana projeler.",
            sections = listOf(
                TopicSection(
                    title = "VİTRİN PROJELERİ",
                    emoji = "⭐",
                    items = listOf(
                        TopicCheckItem("sp_pharmacy", "Pharmacy Management System (Backend + DB)", "Staj tecrübesini aşan kurumsal backend"),
                        TopicCheckItem("sp_thesis", "LLM KV Cache Paging Optimization (C++ / OS)", "Sistem seviyesinde C++ ve yapay zeka çıkarım optimizasyonu"),
                        TopicCheckItem("sp_winterarc_app", "Winter Arc Engineering Skill Tree (Android Compose)", "Kendi kullandığın native mobil yol haritası uygulaması")
                    )
                )
            )
        )
    )
}
