package com.example.data.model

enum class TopicProgressState(
    val key: String,
    val label: String,
    val shortLabel: String,
    val emoji: String,
    val weight: Float
) {
    NOT_STARTED("not_started", "Başlanmadı", "Başlanmadı", "⚪", 0f),
    THEORY("theory", "Teori Tamam", "Teori", "📘", 0.35f),
    PRACTICED("practiced", "Pratik Yapıldı", "Pratik", "🛠️", 0.70f),
    COMPLETED("completed", "Tamamlandı", "Tamamlandı", "✅", 1.0f);

    fun next(): TopicProgressState = when (this) {
        NOT_STARTED -> THEORY
        THEORY -> PRACTICED
        PRACTICED -> COMPLETED
        COMPLETED -> NOT_STARTED
    }

    companion object {
        fun fromKey(key: String?): TopicProgressState {
            if (key.isNullOrBlank()) return NOT_STARTED
            return entries.find { it.key.equals(key, ignoreCase = true) || it.name.equals(key, ignoreCase = true) } ?: NOT_STARTED
        }
    }
}

data class TopicCheckItem(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val practiceTask: String = ""
) {
    fun effectivePracticeTask(): String {
        if (practiceTask.isNotBlank()) return practiceTask
        return "$title konusunu pekiştirmek için mini bir sandbox projesi veya test senaryosu kodla."
    }
}

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
                        TopicCheckItem("cs_syntax", "C# Temel Sözdizimi (Syntax)", "Veri tipleri, değişkenler, operatörler ve kontrol yapıları", practiceTask = "Pattern matching (switch expression) ve record tipleri kullanarak immutable sipariş modeli ve indirim hesaplayıcı kodla."),
                        TopicCheckItem("cs_oop", "OOP (Nesne Yönelimli Programlama)", "Encapsulation, Inheritance, Polymorphism, Abstraction", practiceTask = "IPaymentProcessor arayüzü tanımlayıp CreditCardPayment ve CryptoPayment sınıfları üzerinden polymorphism ve dependency inversion uygula."),
                        TopicCheckItem("cs_collections", "Collections & Generic Yapılar", "List, Dictionary, HashSet, IEnumerable, ICollection", practiceTask = "100.000 kayıtlık bir veri kümesinde List.Contains vs HashSet.Contains arama sürelerini Stopwatch ile ölçüp raporla."),
                        TopicCheckItem("cs_generics", "Generics & Generic Constraints", "Type-safe kod yazımı, where T : class/struct", practiceTask = "IRepository<TEntity, TId> where TEntity : BaseEntity generic sözleşmesini ve generic GetAll/GetById metodlarını yaz."),
                        TopicCheckItem("cs_linq", "LINQ (Language Integrated Query)", "Select, Where, GroupBy, SelectMany, Any, All", practiceTask = "Müşteri ve sipariş listesi üzerinde GroupBy, SelectMany ve Sum kullanarak müşteri bazlı harcama raporu çıkaran LINQ sorgusu yaz."),
                        TopicCheckItem("cs_async", "Async / Await & Concurrency", "Task, Task<T>, ThreadPool, ConfigureAwait, deadlock önleme", practiceTask = "3 farklı web servisine paralel istek atan, CancellationTokenSource(3000) ile timeout korumalı koşan Task.WhenAll metodu yaz."),
                        TopicCheckItem("cs_delegates", "Delegates & Events", "Action, Func, Predicate, EventHandler pratikleri", practiceTask = "Action ve Func parametresi alan, verilen bir metodun çalışma süresini loglayan yüksek mertebeden ExecutionTimer sarmalayıcısı yaz."),
                        TopicCheckItem("cs_exceptions", "Exception Handling", "try-catch-finally, custom exception sınıfları", practiceTask = "NotFoundException ve ValidationException özel sınıflarını yaz; hata durumunda hassas StackTrace sızdırmayan akış oluştur."),
                        TopicCheckItem("cs_memory", "Memory & GC (Garbage Collector) Mantığı", "Stack vs Heap, Value vs Reference types, IDisposable", practiceTask = "IDisposable ve Dispose(bool) modelini kullanarak unmanaged dosya akışını (FileStream) güvenle serbest bırakan sınıf tasarla.")
                    )
                ),
                TopicSection(
                    title = "ASP.NET CORE",
                    emoji = "🌐",
                    items = listOf(
                        TopicCheckItem("asp_http_rest", "HTTP Protokolü & RESTful İlkeler", "HTTP metodları (GET, POST, PUT, DELETE), Request & Response anatomisi, URI yapısı", practiceTask = "REST kurallarına tam uyan CRUD endpoint'leri hazırla; POST isteğinde 201 Created ve Location header dön."),
                        TopicCheckItem("asp_status_codes", "HTTP Status Kodları & REST Semantiği", "2xx (200, 201, 204), 4xx (400, 401, 403, 404, 409, 422) ve 5xx (500, 502, 503, 504) anlamları ve doğru API dönüşleri", practiceTask = "Endpoint'lerde duruma göre doğru status kodlarını dönen (201 Created, 204 NoContent, 404 NotFound, 409 Conflict, 422 Unprocessable, 502 Bad Gateway) kapsamlı bir Controller veya Minimal API senaryosu yaz."),
                        TopicCheckItem("asp_api_design", "API Tasarımı & Endpoint Hijyeni", "Resource isimlendirme, URI versiyonlama, standart response formatları", practiceTask = "Tüm API yanıtlarını sarmalayan standart ApiResponse<T> (Data, Success, Errors) formatı ve /api/v1/ prefix yapısı kur."),
                        TopicCheckItem("asp_controllers", "Controllers & Minimal APIs", "ControllerBase, ActionResults, Endpoint routing", practiceTask = "Tek bir Program.cs dosyası içinde app.MapGroup(\"/api/products\") ile Minimal API endpoint'leri yaz."),
                        TopicCheckItem("asp_middleware", "Middleware Pipeline Mantığı", "Custom middleware yazımı, Request/Response akışı", practiceTask = "Gelen her isteğe X-Correlation-Id header'ı ekleyen ve response süresini loglayan custom middleware yaz."),
                        TopicCheckItem("asp_di", "Dependency Injection (DI)", "Transient, Scoped, Singleton yaşam döngüleri", practiceTask = "Transient, Scoped ve Singleton servisler enjekte et; tek bir HTTP isteği içinde GUID değerlerinin nasıl değiştiğini logla."),
                        TopicCheckItem("asp_config", "Configuration & AppSettings", "IOptions<T> pattern, environment değişkenleri", practiceTask = "appsettings.json'daki JwtSettings bölümünü IOptionsSnapshot<JwtSettings> ile strongly-typed olarak servise inject et."),
                        TopicCheckItem("asp_routing", "Routing & Model Binding", "Route constraints, FromRoute, FromQuery, FromBody", practiceTask = "Route constraint ({id:int:min(1)}) ve FromQuery/FromBody model bağlayıcılarını içeren gelişmiş arama endpoint'i aç."),
                        TopicCheckItem("asp_validation", "Validation (FluentValidation)", "Model validasyonu, custom validator sınıfları", practiceTask = "CreateUserRequest için FluentValidation ile şifre karmaşıklığı ve e-posta kontrolü yaz; geçersiz veride 400 Bad Request dön."),
                        TopicCheckItem("asp_serialization", "Serialization (System.Text.Json)", "JSON serializer options, converter'lar", practiceTask = "DateTime için custom JsonConverter yazarak tarihleri 'yyyy-MM-dd HH:mm' formatında serileştir."),
                        TopicCheckItem("asp_errors", "Global Error & Exception Handling", "ProblemDetails, IExceptionHandler, global middleware", practiceTask = "IExceptionHandler (ASP.NET 8) implemente ederek RFC 7807 ProblemDetails formatında hata dönen global handler yaz."),
                        TopicCheckItem("asp_logging", "Logging (Serilog / ILogger)", "Structured logging, log seviyeleri (Info, Warn, Error)", practiceTask = "Serilog'u yapılandırıp logları hem konsola hem de structured JSON formatında dosyaya yönlendir."),
                        TopicCheckItem("asp_swagger", "Swagger / OpenAPI Dokümantasyonu", "Swashbuckle, XML comments, API test arayüzü", practiceTask = "Swagger UI'a JWT Bearer auth desteği ekle ve XML comments (summary) ile dokümantasyonu zenginleştir."),
                        TopicCheckItem("asp_versioning", "API Sürümleme (Asp.Versioning)", "URL, Query veya Header tabanlı API versiyonlama (v1, v2) ve geriye uyumluluk", practiceTask = "Asp.Versioning.Http ile v1.0 ve v2.0 endpoint'leri aç; v2'de response modeline yeni bir alan ekleyip geriye uyumluluğu koru."),
                        TopicCheckItem("asp_signalr", "Real-Time İletişim (SignalR)", "Hub mimarisi, gruplar, WebSockets/SSE fallback ve anlık canlı bildirimler", practiceTask = "NotificationHub açarak istemcilerin gruplara katıldığı ve gruba anlık bildirim fırlatan canlı akış kodla.")
                    )
                ),
                TopicSection(
                    title = "DATABASE & EF CORE",
                    emoji = "🗄️",
                    items = listOf(
                        TopicCheckItem("db_sql", "SQL Temelleri & Sorgu Yetkinliği", "SELECT, JOIN, Aggregations, Subqueries", practiceTask = "PostgreSQL'de INNER JOIN, LEFT JOIN ve HAVING içeren 3 tabloluk analitik bir SQL sorgusu yaz ve çalıştır."),
                        TopicCheckItem("db_postgres", "PostgreSQL Yönetimi", "PostgreSQL veri tipleri, sequence'lar, psql kullanımı", practiceTask = "Docker üzerinde PostgreSQL container'ı ayağa kaldırıp psql veya DBeaver ile bağlan ve yeni database oluştur."),
                        TopicCheckItem("db_design", "Database Tasarımı & Modelleme", "Tablo ilişkileri (1-1, 1-N, N-N), Foreign Keys", practiceTask = "E-ticaret için User, Order, OrderItem, Product tablolarını foreign key ve cascade kurallarıyla modelle."),
                        TopicCheckItem("db_normalization", "Normalizasyon (1NF, 2NF, 3NF)", "Veri tekrarını önleme, tutarlı şema tasarımı", practiceTask = "Tek tabloda tutulan müşteri ve sipariş verilerini 3. Normal Form'a (3NF) ayrıştırarak ilişkisel şema çıkar."),
                        TopicCheckItem("db_indexes", "İndeksleme & Sorgu Optimizasyonu", "B-tree index, Composite index, EXPLAIN ANALYZE", practiceTask = "100.000 kayıt içeren tabloda filtre kolonuna Composite B-tree Index ekle; EXPLAIN ANALYZE ile sorgu süresi farkını gözlemle."),
                        TopicCheckItem("db_transactions", "Transactions & ACID Prensipleri", "Atomicity, Consistency, Isolation, Durability", practiceTask = "Banka havalesi senaryosunda iki bakiye güncellemesini BeginTransactionAsync ve CommitAsync ile atomik yap; hata olursa Rollback yap."),
                        TopicCheckItem("db_concurrency", "Concurrency & Isolation Seviyeleri", "Read Committed, Serializable, Optimistic/Pessimistic Locking", practiceTask = "EF Core'da [Timestamp] attribute ile Optimistic Concurrency kontrolü yap ve çakışma anında DbUpdateConcurrencyException yakala."),
                        TopicCheckItem("db_efcore", "Entity Framework Core (EF Core)", "DbContext, DbSet, Fluent API konfigürasyonu", practiceTask = "DbContext içinde Fluent API (modelBuilder) kullanarak 1-N ve N-N ilişkileri ve required alanları yapılandır."),
                        TopicCheckItem("db_migrations", "EF Core Migrations", "dotnet ef migrations add, database update pratikleri", practiceTask = "dotnet ef migrations add InitialCreate ve dotnet ef database update komutlarıyla şemayı PostgreSQL'e yansıt."),
                        TopicCheckItem("db_tracking", "Change Tracker & AsNoTracking", "Performans için AsNoTracking kullanımı, entity state", practiceTask = "Sadece okuma yapılan sorgularda AsNoTracking() kullanarak bellek ve işlemci tasarrufunu gözlemle."),
                        TopicCheckItem("db_linq_sql", "LINQ ➔ SQL Mantığı (Expression Trees)", "IQueryable vs IEnumerable, N+1 problemi & AsSplitQuery", practiceTask = "Include() ile alt koleksiyon çekerken oluşan N+1 sorgusunu veya Cartesian patlamayı AsSplitQuery() kullanarak çöz."),
                        TopicCheckItem("db_dapper", "Dapper & Hibrit Veri Erişimi", "Mikro-ORM kullanımı, yüksek performanslı SELECT sorguları, EF Core + Dapper hibrit mimari", practiceTask = "EF Core DbContext ile Dapper IDbConnection'ı aynı projede kullan; yoğun bir liste sorgusunu Dapper QueryAsync ile çek."),
                        TopicCheckItem("db_advanced_ef", "Global Query Filters & Interceptors", "Soft Delete filtreleri, Audit Trail (Created/Updated By/At) ve SaveChanges Interceptor", practiceTask = "Soft Delete için IsDeleted global query filter ekle; SaveChangesInterceptor ile CreatedAt ve UpdatedAt değerlerini otomatik doldur.")
                    )
                ),
                TopicSection(
                    title = "MİMARİ & YAZILIM TASARIMI",
                    emoji = "🏛️",
                    items = listOf(
                        TopicCheckItem("arch_solid", "SOLID Prensipleri", "Single Responsibility, Open-Closed, Liskov, Interface Segregation, Dependency Inversion", practiceTask = "Liskov Substitution ve Interface Segregation ihlali barındıran spagetti bir kod parçasını SOLID'e uygun şekilde refactor et."),
                        TopicCheckItem("arch_layered", "Katmanlı Mimari (N-Tier)", "Presentation, Business, Data Access katmanları", practiceTask = "Presentation, Service ve Data Access katmanlarını barındıran temiz bir N-Tier proje iskeleti kur."),
                        TopicCheckItem("arch_clean", "Clean Architecture", "Domain, Application, Infrastructure, Presentation ayrımı", practiceTask = "Domain, Application, Infrastructure ve WebApi projelerini kur; bağımlılık yönünün Domain merkezli olduğunu doğrula."),
                        TopicCheckItem("arch_onion", "Onion Architecture", "Core domain merkezli bağımlılık yönü (Inward Dependency)", practiceTask = "Domain modelini çekirdeğe alıp veritabanı bağımlılığını Infrastructure katmanına taşıyarak Inward Dependency kuralını sağla."),
                        TopicCheckItem("arch_cqrs_mediatr", "CQRS & MediatR", "Command ve Query ayrımı, IRequest<T>, IRequestHandler ve MediatR Pipeline Behaviors", practiceTask = "CreateProductCommand ve GetProductsQuery için MediatR handler ve FluentValidation pipeline behavior yaz."),
                        TopicCheckItem("arch_result_pattern", "Result Pattern (ErrorOr / FluentResults)", "İş mantığında throw Exception yerine Result<T> dönme, tip güvenli hata yönetimi", practiceTask = "ErrorOr kütüphanesiyle exception fırlatmak yerine ErrorOr<User> dön ve controller'da Match() ile tip güvenli karşıla."),
                        TopicCheckItem("arch_repo_service", "Repository & Service Pattern", "Generic repository, iş mantığının servislere izolasyonu", practiceTask = "Generic IRepository yerine entity'ye özel IOrderRepository yaz; iş mantığını OrderService içinde topla."),
                        TopicCheckItem("arch_dto", "DTO & AutoMapper/Mapster", "Entity - DTO dönüşümleri, domain nesnelerini dışarı açmama", practiceTask = "AutoMapper veya Mapster ile Entity -> DTO profil eşlemesi oluştur; hassas parola alanının dışarı sızmasını engelle."),
                        TopicCheckItem("arch_mapping", "Modern Mapping: Mapster / Mapperly", "AutoMapper yerine modern Source Generator (Mapperly) ve Mapster ile DTO projeksiyonları", practiceTask = "Source generator tabanlı Mapperly ile derleme zamanında type-safe DTO mapping sınıfı oluştur."),
                        TopicCheckItem("arch_patterns", "Design Patterns (GoF)", "Factory, Strategy, Singleton, Decorator, Mediator", practiceTask = "Ödeme sağlayıcıları (Stripe, Iyzico) için Strategy Pattern veya bildirim göndericileri için Factory Pattern uygula."),
                        TopicCheckItem("arch_domain", "Domain Mantığı & Zengin Modeller", "Anemic domain modelden kaçınma, iş kuralları", practiceTask = "Anemic modeldeki public setter'ları private yap; entity içine DeactivateUser() ve UpdateEmail() domain metodları ekle."),
                        TopicCheckItem("arch_testability", "Test Edilebilir Kod Tasarımı (Loosely Coupled)", "Dependency Inversion ile sıkı bağımlılıklardan (tight coupling) kaçınma ve test edilebilir mimari", practiceTask = "Sıkı bağımlı doğrudan 'new EmailService()' çağrısını IEmailService arayüzü ve constructor injection ile refactor et.")
                    )
                ),
                TopicSection(
                    title = "AUTH & GÜVENLİK",
                    emoji = "🔐",
                    items = listOf(
                        TopicCheckItem("sec_authn_authz", "Authentication vs Authorization", "Kimlik doğrulama ile yetkilendirme farkı", practiceTask = "Anonim erişime açık /login endpoint'i ile [Authorize] yetkisi gerektiren /profile endpoint'inin farkını doğrula."),
                        TopicCheckItem("sec_jwt", "JWT (JSON Web Token)", "Header, Payload, Signature, Access & Refresh Token döngüsü", practiceTask = "HMAC-SHA256 ile sign edilen Access Token (15 dk) ve Refresh Token (7 gün) üreten TokenService yaz."),
                        TopicCheckItem("sec_password", "Password Hashing (BCrypt / Argon2)", "Salt, hash maliyet faktörü, güvenli parola saklama", practiceTask = "BCrypt.Net kütüphanesiyle şifre hashleme ve şifre doğrulama (BCrypt.Verify) fonksiyonu yaz."),
                        TopicCheckItem("sec_roles_claims", "Roles & Claims-Based Authorization", "Policy tabanlı yetkilendirme, [Authorize(Policy = ...)]", practiceTask = "RequireClaim(\"department\", \"IT\") kuralı içeren özel bir Authorization Policy tanımla ve controller'a bağla."),
                        TopicCheckItem("sec_oauth", "OAuth2 & OpenID Connect Temelleri", "Token exchange, SSO ve üçüncü parti giriş mantığı", practiceTask = "Google ile Giriş (Google OAuth2) akışını .NET Authentication middleware ile yapılandır."),
                        TopicCheckItem("sec_cors_https", "CORS & HTTPS Güvenliği", "Allowed origins, headers, SSL/TLS sertifikaları", practiceTask = "Sadece belirlediğin frontend origin'ine (localhost:3000) izin veren CORS policy yaz ve HSTS'i aktif et."),
                        TopicCheckItem("sec_vulns", "Genel Güvenlik Açıkları (OWASP)", "SQL Injection, XSS, CSRF, Mass Assignment önleme", practiceTask = "Kullanıcı girdisini raw SQL sorgusuna birleştirmek yerine parameterized query kullanarak SQL Injection açığını kapat.")
                    )
                ),
                TopicSection(
                    title = "PRODUCTION-READY BACKEND",
                    emoji = "🚀",
                    items = listOf(
                        TopicCheckItem("prod_unit_testing", "Unit Testing & Mocking (xUnit + Moq)", "Arrange-Act-Assert (AAA) pattern, [Fact] ve [Theory] testleri, Moq ile bağımlılık izolasyonu", practiceTask = "Moq kullanarak UserService.Register() metodunda bağımlılıkları izole et; başarılı ve hatalı senaryoları [Fact] ve [Theory] ile doğrula."),
                        TopicCheckItem("prod_integration_testing", "Integration Testing (WebApplicationFactory)", "In-memory test server (WebApplicationFactory) ile endpoint'leri, route'ları ve DB pipeline'ını test etme", practiceTask = "WebApplicationFactory<Program> kullanarak /api/products endpoint'ine gerçek HTTP POST isteği atan integration testi yaz."),
                        TopicCheckItem("prod_logging", "Structured Logging & Serilog", "Kayıtların JSON formatında merkezi log sistemlerine hazır olması", practiceTask = "Serilog RequestLogging middleware ekleyerek her HTTP isteğinin method, path, status ve süresini JSON log olarak üret."),
                        TopicCheckItem("prod_bg_jobs", "Background Jobs (Hangfire / IHostedService)", "Zamanlanmış görevler, periyodik veri işleme", practiceTask = "BackgroundService (IHostedService) miras alarak periyodik olarak süresi dolmuş token'ları temizleyen worker yaz."),
                        TopicCheckItem("prod_caching", "Caching Stratejileri (Memory & Redis)", "In-memory cache, Distributed Redis cache, Cache Invalidation", practiceTask = "StackExchange.Redis kütüphanesiyle ürün listesini Redis'e kaydet (10 dk TTL); yeni ürün eklendiğinde cache'i temizle."),
                        TopicCheckItem("prod_resilience", "Dayanıklılık & Polly (Resilience)", "Retry Policy, Circuit Breaker, Timeout ve Microsoft.Extensions.Resilience entegrasyonu", practiceTask = "Microsoft.Extensions.Resilience ile dış HTTP servisine 3 tekrarlı exponential backoff Retry ve Circuit Breaker bağla."),
                        TopicCheckItem("prod_rate_limit", "Rate Limiting & Throttling", "API abuse ve DoS saldırılarına karşı istek sınırlama", practiceTask = "ASP.NET Core RateLimiter middleware ile IP başına dakikada maksimum 60 isteğe izin veren FixedWindow kuralı tanımla."),
                        TopicCheckItem("prod_notifications", "E-Posta & Bildirim Dağıtımı (MailKit & FCM)", "MailKit ile SMTP e-posta gönderimi, HTML şablonları (Razor/Fluid) ve Push bildirim servisi", practiceTask = "MailKit ile HTML formatında hoş geldin e-postası gönder; Firebase Admin SDK ile FCM push bildirimi fırlat."),
                        TopicCheckItem("prod_storage", "Dosya & Medya Yönetimi (S3 / MinIO)", "IFormFile işleme, S3 / MinIO uyumlu presigned URL yükleme akışı ve dosya doğrulama", practiceTask = "MinIO container'ı ayağa kaldırıp AWS SDK (S3Client) ile dosya yükleme ve süreli Presigned URL oluşturma akışı yaz."),
                        TopicCheckItem("prod_health", "Health Checks", "Veritabanı ve dış servis sağlık durum kontrolleri (/health)", practiceTask = "AspNetCore.HealthChecks kütüphanesiyle PostgreSQL ve Redis bağlantılarını kontrol eden /healthz endpoint'i ekle.")
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
                        TopicCheckItem("doc_fundamentals", "Docker Temelleri", "Container vs Sanal Makine, Docker daemon, Image vs Container", practiceTask = "Terminalden docker run, docker ps, docker stop ve docker logs komutlarıyla bir nginx container'ının yaşam döngüsünü yönet."),
                        TopicCheckItem("doc_dockerfile", "Dockerfile Yazımı (.NET için)", "Multi-stage build, minimal image boyutları (alpine/chiseled)", practiceTask = ".NET 8 Web API için SDK ve runtime katmanlarını ayıran multi-stage Dockerfile yaz ve image oluştur."),
                        TopicCheckItem("doc_compose", "Docker Compose", "Çoklu servis orkestrasyonu (.NET + Postgres + Redis + RabbitMQ)", practiceTask = "docker-compose.yml içinde Web API, PostgreSQL, Redis ve RabbitMQ servislerini tek komutla (docker compose up -d) ayağa kaldır."),
                        TopicCheckItem("doc_volumes", "Volumes & Kalıcı Depolama", "Named volumes, bind mounts, veritabanı verisi saklama", practiceTask = "PostgreSQL verisinin container silinse bile kaybolmaması için docker-compose'da named volume tanımla."),
                        TopicCheckItem("doc_networks", "Docker Networks", "Bridge network, container'lar arası DNS ve haberleşme", practiceTask = "İki farklı container'ı aynı Docker bridge network'üne bağla ve container adıyla ping atarak DNS çözümlemesini test et.")
                    )
                ),
                TopicSection(
                    title = "MESSAGING & QUEUES (RABBITMQ)",
                    emoji = "📨",
                    items = listOf(
                        TopicCheckItem("msg_concept", "Message Queue Mantığı & Neden?", "Asenkron işleme, servis ayrışması (decoupling), yük dengeleme", practiceTask = "Kullanıcı kayıt olduğunda hoş geldin e-postasını senkron bekletmek yerine kuyruğa atıp asenkron işleten mimariyi tasarla."),
                        TopicCheckItem("msg_rabbitmq", "RabbitMQ Çekirdek Kavramları", "Producer, Consumer, Exchange, Queue, Routing Key", practiceTask = "RabbitMQ yönetim paneline (localhost:15672) bağlan; exchange, queue ve binding tanımlayıp test mesajı gönder."),
                        TopicCheckItem("msg_exchanges", "Exchange Tipleri", "Direct, Topic, Fanout, Headers exchange modelleri", practiceTask = "Direct, Fanout ve Topic exchange modelleri için birer producer ve birden fazla consumer yazıp mesaj dağılımını izle."),
                        TopicCheckItem("msg_retry_dlq", "Retry & Dead Letter Queue (DLQ)", "Hatalı mesajların tekrar denenmesi ve DLQ'ya aktarımı", practiceTask = "3 kez hata alan bir mesajın x-dead-letter-exchange üzerinden dead-letter queue'ya aktarılışını simüle et."),
                        TopicCheckItem("msg_masstransit_outbox", "MassTransit & Transactional Outbox", ".NET için kurumsal mesajlaşma soyutlaması, Outbox pattern ile mesaj kaybını önleme", practiceTask = "MassTransit ile Outbox pattern kur; DbContext transaction'ı commit olduğunda RabbitMQ'ya event fırlatıldığını doğrula.")
                    )
                ),
                TopicSection(
                    title = "MICROSERVICES",
                    emoji = "🧩",
                    items = listOf(
                        TopicCheckItem("ms_monolith", "Monolith vs Modular Monolith vs Microservices", "Ne zaman hangisi seçilmeli? Dağıtık monolit tuzağı", practiceTask = "Modüler monolit yapısında Order ve User modüllerini birbirinden bağımsız internal paketler olarak tasarla."),
                        TopicCheckItem("ms_comm", "Servisler Arası Haberleşme", "Senkron (REST/gRPC) vs Asenkron (Event-Driven / Pub-Sub)", practiceTask = "Sipariş servisi ile Ödeme servisi arasında gRPC ile senkron, RabbitMQ ile asenkron event haberleşmesini karşılaştır."),
                        TopicCheckItem("ms_gateway", "API Gateway (Ocelot / YARP)", "Ters proxy, routing, merkezi kimlik denetimi", practiceTask = "YARP (Yet Another Reverse Proxy) kütüphanesini kullanarak mikroservislerin önüne tek bir giriş kapısı (API Gateway) kur."),
                        TopicCheckItem("ms_saga", "Dağıtık Transaction & Saga Pattern", "2PC yerine koreografi / orkestrasyon tabanlı tutarlılık", practiceTask = "Sipariş verme akışında Ödeme -> Stok Düşme -> Kargo adımları için hata durumunda ters işlem (compensating) işleten Saga kurgula.")
                    )
                ),
                TopicSection(
                    title = "KUBERNETES & MONITORING",
                    emoji = "☸️",
                    items = listOf(
                        TopicCheckItem("k8s_basics", "Kubernetes Kavramları", "Cluster, Node, Pod, Deployment, ReplicaSet", practiceTask = "kubectl get pods, kubectl get services ve kubectl describe pod komutlarıyla çalışan küme durumunu incele."),
                        TopicCheckItem("k8s_networking", "K8s Servisleri & Ingress", "ClusterIP, NodePort, LoadBalancer, Ingress Controller", practiceTask = "Deployment için bir ClusterIP ve dış dünyaya açmak için Ingress kuralı tanımlayan YAML manifest dosyası yaz."),
                        TopicCheckItem("k8s_probes", "K8s Probes (Liveness & Readiness)", "Pod trafik kontrolü, ASP.NET Core /health/live ve /health/ready entegrasyonu", practiceTask = "Pod manifestine livenessProbe ve readinessProbe HTTP get kontrollerini ekle; endpoint 500 dönünce pod'un restart olduğunu gör."),
                        TopicCheckItem("k8s_config_secrets", "ConfigMaps & Secrets", "Hassas şifreleri ve ayarları kod dışından pod'a environment variable olarak enjekte etme", practiceTask = "kubectl create secret generic ile veritabanı şifresi oluştur; pod manifestinde envFrom ile pod'a enjekte et."),
                        TopicCheckItem("k8s_lens", "Kubernetes Yönetimi & Lens", "K8s cluster'ını görsel arayüzle izleme ve pod logları", practiceTask = "Lens uygulamasını açıp local Kubernetes kümesine (k3s/minikube) bağlan; pod loglarını ve terminal oturumunu izle."),
                        TopicCheckItem("k8s_monitoring", "Prometheus & Grafana", "Metrik toplama, panolar, CPU/bellek ve istek sayısı takibi", practiceTask = "Prometheus ile uygulamanın /metrics endpoint'ini scrape et; Grafana'da CPU ve HTTP istek hızı panosu oluştur."),
                        TopicCheckItem("k8s_tracing", "Distributed Tracing & CorrelationId", "OpenTelemetry / Jaeger ile isteklerin servisler arası şelale (waterfall) takibi", practiceTask = "OpenTelemetry .NET SDK ile servislere ActivitySource ekle; Jaeger arayüzünde isteklerin şelale izlerini (trace) incele.")
                    )
                ),
                TopicSection(
                    title = "CLOUD & CI/CD",
                    emoji = "☁️",
                    items = listOf(
                        TopicCheckItem("cloud_azure", "Azure Temelleri", "App Service, Azure SQL, Blob Storage mantığı", practiceTask = "Azure Portal'da ücretsiz App Service ve Azure SQL kaynağı oluştur veya mimari şemasını incele."),
                        TopicCheckItem("cloud_keyvault", "Azure Key Vault & Managed Identity", "Connection string ve API anahtarlarının bulutta güvenli saklanması ve otomatik çekilmesi", practiceTask = "Azure Key Vault'a gizli bir connection string ekle; .NET uygulamasında Azure.Security.KeyVault.Secrets ile oku."),
                        TopicCheckItem("cloud_cicd", "CI/CD Pipeline (GitHub Actions)", "Otomatik test koşumu, build alma ve container push", practiceTask = ".github/workflows/ci.yml dosyası yazarak her commit atıldığında dotnet test ve docker build çalıştıran pipeline kur.")
                    )
                )
            )
        ),

        // ==========================================
        // 3. ANDROID (NATIVE - KOTLIN & XML)
        // ==========================================
        "sub_android" to SubItemRoadmap(
            subItemId = "sub_android",
            title = "Native Android (Kotlin & XML)",
            subtitle = "Kotlin, XML Layouts, ViewBinding, MVVM & Hilt",
            emoji = "🤖",
            targetLevel = "Hedef: Bağımsız Mobil Ürün Geliştirme (XML & Clean Architecture)",
            overview = "Klasik Android View sistemine (XML, ViewBinding, ConstraintLayout, Fragments) tam hakim, modern Jetpack bileşenleriyle (ViewModel, Room, Hilt, Coroutines/Flow) offline-first ve üretime hazır uygulamalar geliştirip Play Store'da yayınlayan bir Android mühendisi olmak.",
            sections = listOf(
                TopicSection(
                    title = "KOTLIN DİL YETKİNLİĞİ",
                    emoji = "💎",
                    items = listOf(
                        TopicCheckItem("kt_fundamentals", "Kotlin Temelleri & Null Safety", "val vs var, nullables (?), safe calls (?.), elvis operator (?:)", practiceTask = "Null olabilecek bir API yanıt modelini elvis operatörü (?:) ve safe call (?.) ile güvenli şekilde varsayılan değerlere map eden fonksiyon yaz."),
                        TopicCheckItem("kt_oop", "Kotlin OOP (Data Classes, Sealed Classes)", "data class, sealed interface/class ile UI State modelleme", practiceTask = "Bir ekranın durumunu temsil eden sealed interface UiState (Loading, Success<T>, Error(val msg: String)) modelini tasarla."),
                        TopicCheckItem("kt_coroutines", "Coroutines & Asenkron Programlama", "suspend fonksiyonlar, launch, async, Dispatchers.IO/Main", practiceTask = "withContext(Dispatchers.IO) ile veritabanı okuyup Dispatchers.Main üzerinde UI güncelleyen suspend fonksiyon yaz."),
                        TopicCheckItem("kt_flow", "Flow & StateFlow / SharedFlow", "Reaktif veri akışları, UI state yönetimi, repeatOnLifecycle", practiceTask = "MutableStateFlow ile counter veya arama filtresi tut; Fragment içinde viewLifecycleOwner.repeatOnLifecycle ile güvenle dinle."),
                        TopicCheckItem("kt_extensions", "Extension Functions & Scope Functions", "let, run, apply, also ve View extension'ları", practiceTask = "View sınıfına .visible(), .gone(), .invisibleIf(condition) extension fonksiyonları yazıp UI kodunu temizle.")
                    )
                ),
                TopicSection(
                    title = "XML & ANDROID VIEW SİSTEMİ",
                    emoji = "🎨",
                    items = listOf(
                        TopicCheckItem("xml_constraint", "ConstraintLayout & Gelişmiş Hizalama", "Guidelines, Barriers, Chains, dikey/yatay oranlar, flat hiyerarşi", practiceTask = "Guideline ve Barrier kullanarak dikey ve yatay ekran modlarına uyumlu, iç içe layout barındırmayan tek bir ConstraintLayout tasarımı yap."),
                        TopicCheckItem("xml_viewbinding", "ViewBinding & DataBinding", "findViewById yerine type-safe layout erişimi, binding adapter'lar", practiceTask = "Activity ve Fragment'ta ViewBinding entegrasyonu yap; binding nesnesini onDestroyView'da null yaparak bellek sızıntısını önle."),
                        TopicCheckItem("xml_recyclerview", "RecyclerView & ListAdapter + DiffUtil", "ViewHolder pattern, item animasyonları, çoklu view tipleri", practiceTask = "ListAdapter ve DiffUtil.ItemCallback kullanarak yüksek performanslı ve otomatik animasyonlu bir ürün listesi adapter'ı kodla."),
                        TopicCheckItem("xml_fragments", "Fragments & Yaşam Döngüsü", "FragmentContainerView, FragmentManager, Backstack yönetimi", practiceTask = "FragmentContainerView kullanarak iki fragment arası geçişi ve backstack'e ekleme akışını kodla."),
                        TopicCheckItem("xml_navigation", "Jetpack Navigation & Safe Args", "nav_graph.xml, NavHostFragment, tip güvenli argüman aktarımı", practiceTask = "nav_graph.xml içinde iki ekran bağla; Safe Args (Directions) ile tıklandığında detay ekranına Parcelable model aktar."),
                        TopicCheckItem("xml_material", "Material Components & CoordinatorLayout", "MaterialCardView, TextInputLayout, BottomNavigationView, CollapsingToolbar", practiceTask = "CoordinatorLayout ve AppBarLayout kullanarak yukarı kaydırdıkça küçülen CollapsingToolbarLayout ekranı tasarla."),
                        TopicCheckItem("xml_drawables", "Custom Drawables, Selector & Dark Mode", "VectorDrawables, shape, state selector, temalar ve gece modu", practiceTask = "Tıklama durumuna göre (pressed, focused, default) renk değiştiren selector XML drawable ve koyu tema (night) renk paleti hazırla.")
                    )
                ),
                TopicSection(
                    title = "MİMARİ & JETPACK (OFFLINE-FIRST)",
                    emoji = "🏛️",
                    items = listOf(
                        TopicCheckItem("and_viewmodel", "ViewModel & SavedStateHandle", "Ekran döndürmede veri koruma, process death kurtarma", practiceTask = "SavedStateHandle enjekte edilen bir ViewModel yazarak sistem tarafından process öldürüldüğünde bile son arama metnini kurtar."),
                        TopicCheckItem("and_clean_mvvm", "MVVM & Clean Architecture", "Data Layer (Repository), Domain Layer (UseCases), Presentation", practiceTask = "GetUsersUseCase ve UserRepository arayüzü tanımlayarak iş mantığını ViewModel'den bağımsız Domain katmanına izole et."),
                        TopicCheckItem("and_hilt", "Dependency Injection (Hilt)", "@AndroidEntryPoint, @HiltViewModel, Network/Database modülleri", practiceTask = "@HiltAndroidApp kurup AppModule içinde Retrofit ve Room bağımlılıklarını @Provides ve @Singleton ile inject et."),
                        TopicCheckItem("and_room", "Room Database (Offline-First)", "Entity, Dao, SQLite işlemleri, reaktif veri tabanı akışları (Flow)", practiceTask = "Room Entity ve DAO yaz; verileri Flow<List<Entity>> olarak dönüp repository üzerinden ViewModel'e ilet."),
                        TopicCheckItem("and_datastore", "Jetpack DataStore", "Preferences DataStore ile anahtar-değer saklama", practiceTask = "Kullanıcının karanlık mod tercihini veya Auth token'ını DataStore ile asenkron okuyup yazan SessionManager yaz.")
                    )
                ),
                TopicSection(
                    title = "NETWORKING & ARKA PLAN",
                    emoji = "🌐",
                    items = listOf(
                        TopicCheckItem("and_retrofit_okhttp", "Retrofit & OkHttp", "HTTP istekleri, Interceptor, BaseUrl, JSON serialization", practiceTask = "HttpLoggingInterceptor ve AuthInterceptor (Bearer token) içeren OkHttpClient ile Retrofit API servisi kur."),
                        TopicCheckItem("and_auth_token", "Auth & Refresh Token Akışı (Authenticator)", "OkHttp Authenticator ile 401 anında otomatik token yenileme", practiceTask = "401 Unauthorized alındığında otomatik olarak refresh token endpoint'ine gidip yeni access token alan OkHttp Authenticator yaz."),
                        TopicCheckItem("and_workmanager", "WorkManager & Arka Plan Senkronizasyonu", "CoroutineWorker, Constraints (Şarj, WiFi), periyodik işler", practiceTask = "Sadece Wi-Fi bağlıyken ve şarjdayken çalışan, günde bir kez offline verileri sunucuya senkronize eden PeriodicWorkRequest kur."),
                        TopicCheckItem("and_notifications", "Push Bildirimleri (FCM) & NotificationChannel", "NotificationChannel, NotificationCompat, Android 13+ izinleri", practiceTask = "Android 13+ POST_NOTIFICATIONS iznini kontrol eden ve yüksek öncelikli NotificationChannel üzerinden bildirim fırlatan servis yaz.")
                    )
                ),
                TopicSection(
                    title = "TEST & PLAY STORE YAYINI",
                    emoji = "🚀",
                    items = listOf(
                        TopicCheckItem("and_testing", "Unit Testing (JUnit & MockK)", "ViewModel testleri, Coroutine test dispatcher, Turbine", practiceTask = "MockK ve Turbine kütüphanelerini kullanarak ViewModel'in StateFlow çıktısına unit test yaz."),
                        TopicCheckItem("and_r8_proguard", "ProGuard & R8 Optimizasyonu", "minifyEnabled, shrinkResources, proguard-rules.pro", practiceTask = "Release build için R8 minification'ı aç; Retrofit ve Data class modellerinin silinmemesi için ProGuard kurallarını yaz."),
                        TopicCheckItem("and_release", "Play Store Yayın Süreci", "Keystore imzalama, App Bundle (.aab), Google Play Console", practiceTask = "Release Keystore dosyası oluştur; gradle signingConfigs yapılandırıp imzalı bir app-release.aab paketi üret.")
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
            subtitle = "Lansman, Vitrin & Paylaşma Serüveni",
            emoji = "🛠️",
            targetLevel = "Hedef: Fikirden Play Store & GitHub Vitrinine Uçtan Uca",
            overview = "Bir projeyi sadece kodlamak yetmez; mimarisini kurup, testlerini yazıp, Docker/AAB ile paketleyip, Play Store'a, GitHub'a, Medium ve LinkedIn'e çıkararak görünür kılma serüveni.",
            sections = listOf(
                TopicSection(
                    title = "MOBİL UYGULAMA LANSMAN SERÜVENİ (PLAY STORE)",
                    emoji = "📱",
                    items = listOf(
                        TopicCheckItem("mob_arch", "Temiz Mimari & Offline-First (MVVM + Room)", "Data, Domain ve UI ayrımı, Room DB ve Retrofit ile kesintisiz veri akışı", practiceTask = "Uygulamada Room DB ve Repository pattern kurarak ağ kopsa bile verilerin ekranda gösterildiği offline-first yapıyı test et."),
                        TopicCheckItem("mob_tests_quality", "Testler, Hata Ayıklama & Crashlytics", "Unit testler, Firebase Crashlytics entegrasyonu ve kararlılık testleri", practiceTask = "ViewModel için JUnit testi yaz ve Firebase Crashlytics ile simüle edilmiş bir hatanın dashboard'a düştüğünü doğrula."),
                        TopicCheckItem("mob_release_build", "Release Build, Keystore & ProGuard/R8", "İmzalı release keystore, kod küçültme (minifyEnabled) ve .aab üretimi", practiceTask = "Terminalden keytool ile release keystore oluştur, proguard-rules.pro yapılandır ve 'bundleRelease' ile AAB paketi derle."),
                        TopicCheckItem("mob_play_console", "Play Console Listelemesi & Mağaza Varlıkları", "512x512 ikon, 1024x500 özellik grafiği, ekran görüntüleri ve gizlilik politikası", practiceTask = "Uygulama için ekran görüntülerini mockup çerçevesine yerleştir, gizlilik politikası URL'i oluştur ve Play Console'a yükle."),
                        TopicCheckItem("mob_showcase", "GitHub Vitrini & LinkedIn Lansmanı", "Mimari diyagram, GIF/video demosu, Play Store rozeti ve LinkedIn gönderisi", practiceTask = "README'ye Play Store rozeti ve 10 saniyelik demo GIF'i ekle; LinkedIn'de 'Neler öğrendim?' odaklı video demo lansmanı paylaş.")
                    )
                ),
                TopicSection(
                    title = "BACKEND APİ VİTRİN SERÜVENİ (DOCKER & MEDİUM)",
                    emoji = "🌐",
                    items = listOf(
                        TopicCheckItem("back_arch_db", "Clean Architecture & PostgreSQL Modelleme", "Entities, Migrations, Seed Data ve Repository/Service katmanları", practiceTask = "Clean Architecture iskeletinde PostgreSQL migration'larını ve test için gerekli ilk seed verilerini hazırla."),
                        TopicCheckItem("back_tests", "Birim & Entegrasyon Testleri (xUnit + Moq)", "İş kuralları için xUnit+Moq ve WebApplicationFactory ile controller testleri", practiceTask = "Kritik bir iş kuralı için Moq ile izole unit test, bir endpoint için de in-memory integration testi yaz."),
                        TopicCheckItem("back_docker", "Dockerize Etme & Tek Komutla Çalıştırma", "Multi-stage Dockerfile ve 'docker compose up' ile tüm servisleri ayağa kaldırma", practiceTask = "Multi-stage Dockerfile yaz; docker-compose.yml ile API, PostgreSQL ve Redis'i tek komutla çalıştırılabilir hale getir."),
                        TopicCheckItem("back_docs", "Swagger / OpenAPI & cURL Örnekleri", "Zengin summary açıklamaları, request/response şemaları ve test istekleri", practiceTask = "Swagger UI'da tüm controller'lar için XML dökümantasyonu üret ve README için örnek cURL istekleri hazırla."),
                        TopicCheckItem("back_readme", "README Vitrini (Mermaid Mimari & ERD Şeması)", "Mermaid diyagramı, ilişkisel şema (ERD) ve canlı demo/kurulum rehberi", practiceTask = "GitHub README.md içine Mermaid ile mimari akış şeması ve veritabanı ilişkisel diyagramı (ERD) çiz."),
                        TopicCheckItem("back_medium", "Teknik Medium Makalesi Yayınlama", "Karşılaşılan mimari zorluklar, trade-off'lar ve performans çözümleri", practiceTask = "'Bu backend'i geliştirirken neden Clean Architecture ve Redis seçtim?' konulu derinlemesine bir Medium teknik yazısı kaleme al."),
                        TopicCheckItem("back_cv", "CV & Portföy Entegrasyonu (STAR Metodu)", "Situation, Task, Action, Result kalıbıyla projeyi CV ve LinkedIn'e ekleme", practiceTask = "Projeyi STAR formülüyle (Kullanılan teknolojiler, çözülen problem, ölçülebilir sonuç) 3 madde halinde CV'ne yerleştir.")
                    )
                ),
                TopicSection(
                    title = "SİSTEM & AKADEMİK PROJE SERÜVENİ (BİTİRME TEZİ / C++)",
                    emoji = "🔬",
                    items = listOf(
                        TopicCheckItem("sys_research", "Problem Tanımı & Çekirdek/Kernel İnceleme", "Darboğaz analizi, bellek/işlemci mimarisi ve literatür araştırması", practiceTask = "Optimizasyon problemini, bellek fragmentasyonunu veya kernel darboğazını özetleyen teknik ön rapor yaz."),
                        TopicCheckItem("sys_benchmarking", "Benchmark & Performans Metrikleri", "RAM tüketimi, latency, throughput ölçümleri ve grafikleştirme", practiceTask = "Sistemin optimizasyon öncesi ve sonrası bellek/hız metriklerini karşılaştıran benchmark testi çalıştır ve grafik çıkar."),
                        TopicCheckItem("sys_report", "Akademik Tez Raporu & GitHub Deposu", "Derleme talimatları, bağımlılıklar ve teknik tez dokümantasyonu", practiceTask = "GitHub deposuna detaylı build/run adımları ekle ve bitirme tezi raporunu PDF/Markdown formatında depoya ekle.")
                    )
                )
            )
        ),

        // ==========================================
        // 5. BTK AKADEMİ DİPLOMA & SERTİFİKA AVI
        // ==========================================
        "sub_btk_akademi" to SubItemRoadmap(
            subItemId = "sub_btk_akademi",
            title = "BTK Akademi Diploma Avı",
            subtitle = "e-Devlet & 1 Milyon İstihdam Onaylı Sertifikalar",
            emoji = "🎓",
            targetLevel = "Hedef: e-Devlet & LinkedIn İçin 20+ Resmi Sertifika & Uzmanlık Diploması",
            overview = "Platformdaki tüm eğitimler, sınavlar ve sertifikalar %100 ÜCRETSİZDİR. Videoları 1.5x hızda izleyip bitirme testinden 70+ aldığında barkodlu resmi sertifikan e-Devlet profiline işlenir.",
            sections = listOf(
                TopicSection(
                    title = "⚡ HIZLI DİPLOMA KASMALIK EĞİTİMLER (1-3 SAAT)",
                    emoji = "⚡",
                    items = listOf(
                        TopicCheckItem(
                            id = "btk_it_intro",
                            title = "Bilgi Teknolojilerine Giriş",
                            description = "Kariyer programlarının kilidini açan zorunlu ön koşul eğitimi (Temel bilgisayar mimarisi ve ağlar).",
                            practiceTask = "Videoları hızlandırarak tamamla, bölüm sonu sınavını geç ve 1. barkodlu e-Devlet sertifikanı cebe at."
                        ),
                        TopicCheckItem(
                            id = "btk_git_cert",
                            title = "Versiyon Kontrolleri: Git ve GitHub (Atıl Samancıoğlu)",
                            description = "Git komutları, branch stratejileri ve GitHub portfolyo iş akışları.",
                            practiceTask = "Atıl Hoca'nın anlatımıyla eğitimi bitir, testten 70+ alarak CV'ye resmi Git & GitHub sertifikasını ekle."
                        ),
                        TopicCheckItem(
                            id = "btk_sec_intro",
                            title = "Siber Güvenliğe Giriş & Güvenlik Farkındalığı",
                            description = "Temel siber tehditler, kimlik avı, şifreleme ve kurumsal ağ güvenliği ilkeleri.",
                            practiceTask = "Teorik videoları tamamlayıp tek oturuşta sınavı geçerek 'Siber Güvenlik Farkındalık' belgesini al."
                        ),
                        TopicCheckItem(
                            id = "btk_ai_prompt",
                            title = "Üretken Yapay Zeka & Prompt Mühendisliği",
                            description = "LLM modelleri, ChatGPT, Gemini ve yapay zeka araçlarıyla yazılımda üretkenlik.",
                            practiceTask = "Eğitimi ve bitirme quizini tamamlayarak modern AI okuryazarlığı sertifikanı LinkedIn profiline ekle."
                        ),
                        TopicCheckItem(
                            id = "btk_kvkk",
                            title = "Kişisel Verilerin Korunması Kanunu (KVKK) Farkındalığı",
                            description = "Yazılım sistemlerinde veri saklama, açık rıza ve kurumsal yasal uyumluluk.",
                            practiceTask = "İş mülakatlarında ve kurumsal firmalarda büyük artı puan sağlayan KVKK sertifikasını al."
                        )
                    )
                ),
                TopicSection(
                    title = "📱 NATIVE ANDROID & KOTLIN SERTİFİKALARI",
                    emoji = "🤖",
                    items = listOf(
                        TopicCheckItem(
                            id = "btk_kotlin_core",
                            title = "Kotlin Programlama Dili",
                            description = "Kotlin sözdizimi, OOP, Null Safety, lambda fonksiyonları ve Coroutines temelleri.",
                            practiceTask = "Dersleri bitirip final sınavından 70+ alarak resmi 'Kotlin Programlama' sertifikanı indir."
                        ),
                        TopicCheckItem(
                            id = "btk_android_basic",
                            title = "Kotlin ile Android Mobil Uygulama Geliştirme Temelleri (Atıl Samancıoğlu)",
                            description = "Geleneksel XML UI tasarımı, ConstraintLayout, Activity/Fragment yaşam döngüsü ve ViewBinding.",
                            practiceTask = "XML tabanlı mini projeleri yap, sınavı ver ve Temel Android sertifikanı portfolyona ekle."
                        ),
                        TopicCheckItem(
                            id = "btk_android_adv",
                            title = "Kotlin ile Android Mobil Uygulama Geliştirme İleri Seviye (Atıl Samancıoğlu)",
                            description = "Retrofit REST API entegrasyonu, Room Database ile yerel veri yönetimi, MVVM ve WorkManager.",
                            practiceTask = "İleri seviye eğitim projesini ve final sınavını geçerek İleri Android sertifikanı kazan."
                        )
                    )
                ),
                TopicSection(
                    title = "⚡ .NET / C# & BACKEND GELİŞTİRİCİ DİPLOMALARI",
                    emoji = "💻",
                    items = listOf(
                        TopicCheckItem(
                            id = "btk_csharp_oop",
                            title = "C# ile Nesne Yönelimli Programlama (Engin Demiroğ)",
                            description = "Class, Interface, Abstract Class, Polymorphism ve SOLID prensipleriyle kurumsal kodlama.",
                            practiceTask = "Engin Hoca'nın C# OOP atölyesini tamamla, testten geç ve C# OOP sertifikanı al."
                        ),
                        TopicCheckItem(
                            id = "btk_dotnet_webapi",
                            title = "ASP.NET Core Web API",
                            description = "REST standartları, HTTP durum kodları (200, 400, 404, 500), Controller, DI ve Swagger.",
                            practiceTask = "RESTful API mimari derslerini bitir, sınavını çöz ve resmi .NET Web API sertifikasını al."
                        ),
                        TopicCheckItem(
                            id = "btk_ef_core",
                            title = "Entity Framework Core ile Veri Erişimi (Engin Demiroğ)",
                            description = "Code-First, DbContext, Migrations, LINQ sorguları ve kurumsal Repository Pattern.",
                            practiceTask = "EF Core veri tabanı projesini ve bölüm sınavlarını geçerek ORM yetkinlik belgesini al."
                        ),
                        TopicCheckItem(
                            id = "btk_design_patterns",
                            title = "Yazılım Tasarım Desenleri (Design Patterns)",
                            description = "Singleton, Factory, Repository, Mediator, Observer ve mülakatlarda sorulan kalıplar.",
                            practiceTask = "Tasarım desenleri atölyesini tamamla, desen sorularını çöz ve resmi sertifikanı al."
                        ),
                        TopicCheckItem(
                            id = "btk_microservices",
                            title = "Mikroservis Mimarisine Giriş",
                            description = "Monolitik yapıdan mikroservislere geçiş, API Gateway ve servisler arası iletişim pratikleri.",
                            practiceTask = "Mikroservis mimari eğitimini tamamlayıp modern backend sertifikanı kazan."
                        )
                    )
                ),
                TopicSection(
                    title = "🗄️ VERİTABANI & DEVOPS / ALTYAPI SERTİFİKALARI",
                    emoji = "🐳",
                    items = listOf(
                        TopicCheckItem(
                            id = "btk_sql_postgres",
                            title = "PostgreSQL için SQL Dili (Temel Seviye)",
                            description = "İlişkisel veritabanı tasarımı, DDL/DML, JOIN'ler, indeksleme ve DBeaver/pgAdmin.",
                            practiceTask = "SQL sorgu pratiklerini ve veri tabanı tasarım sınavını geçerek resmi SQL sertifikasını al."
                        ),
                        TopicCheckItem(
                            id = "btk_docker_basics",
                            title = "Docker Temelleri",
                            description = "Container yapısı, Dockerfile yazımı, Docker Hub, port yönlendirme ve docker-compose.",
                            practiceTask = "Docker eğitim videolarını bitir, container sınavını geç ve DevOps sertifikanı al."
                        ),
                        TopicCheckItem(
                            id = "btk_azure_devops",
                            title = "Microsoft Azure DevOps Eğitimi",
                            description = "Kaynak denetimi, Git reposu, CI/CD derleme ve otomatik dağıtım süreçleri.",
                            practiceTask = "DevOps yaşam döngüsü eğitimini tamamla ve kurumsal dağıtım sertifikanı ekle."
                        )
                    )
                ),
                TopicSection(
                    title = "🧪 YAZILIM TESTİ & KALİTE DİPLOMALARI",
                    emoji = "🎯",
                    items = listOf(
                        TopicCheckItem(
                            id = "btk_api_testing",
                            title = "API ve API Testi (Postman)",
                            description = "REST API test etme, Postman ile istek koleksiyonları, ortam değişkenleri ve response doğrulama.",
                            practiceTask = "API test adımlarını ve Postman pratiklerini tamamlayarak API Testi sertifikanı al."
                        ),
                        TopicCheckItem(
                            id = "btk_software_test_intro",
                            title = "Yazılım Testine Giriş",
                            description = "Yazılım kalite güvencesi, test senaryoları, kara/beyaz kutu testleri ve Unit Test.",
                            practiceTask = "Yazılım test süreçleri sınavını geç ve CV'ye resmi yazılım test sertifikasını ekle."
                        ),
                        TopicCheckItem(
                            id = "btk_test_automation",
                            title = "Yazılım Test Otomasyonu",
                            description = "Test otomasyonu mimarisi, otomasyon kodlama pratikleri ve sürekli entegrasyonda test kültürü.",
                            practiceTask = "Test otomasyonu eğitimini bitir ve kalite güvence uzmanlığı belgeni indir."
                        )
                    )
                ),
                TopicSection(
                    title = "🛡️ SİBER GÜVENLİK & SAVUNMA SERTİFİKALARI",
                    emoji = "🛡️",
                    items = listOf(
                        TopicCheckItem(
                            id = "btk_net_fundamentals",
                            title = "Ağ Temelleri ve Ağ Güvenliği",
                            description = "TCP/IP, OSI modeli, DNS, DHCP, portlar, firewall ve paket analiz temelleri.",
                            practiceTask = "Ağ kavramlarını ve temel güvenlik protokollerini bitir, sınavı geçip resmi ağ güvenliği sertifikanı al."
                        ),
                        TopicCheckItem(
                            id = "btk_web_security",
                            title = "Web Uygulama Güvenliği & Sızma Tekniklerine Giriş",
                            description = "OWASP Top 10, SQL Injection, XSS, CSRF, kimlik doğrulama zafiyetleri ve güvenli kodlama.",
                            practiceTask = "Backend ve web zafiyet senaryolarını incele, web güvenliği sınavını vererek kritik güvenlik sertifikanı al."
                        ),
                        TopicCheckItem(
                            id = "btk_pentest_intro",
                            title = "Uygulamalı Sızma Testi Temelleri (Penetrasyon Testi)",
                            description = "Kali Linux ortamı, Nmap ile ağ/port keşfi ve zaafiyet tarama mantığı.",
                            practiceTask = "Temel penetrasyon testi eğitimini bitirip CV'ye uygulamalı güvenlik diplomasını ekle."
                        ),
                        TopicCheckItem(
                            id = "btk_malware_intro",
                            title = "Zararlı Yazılım (Malware) Temelleri & Analizi",
                            description = "Virüsler, truva atları, fidye yazılımları, davranış analiz yöntemleri ve sistem savunması.",
                            practiceTask = "Zararlı yazılım analizi derslerini tamamla, sınavı geç ve savunma güvenliği belgeni cebe at."
                        )
                    )
                ),
                TopicSection(
                    title = "🏆 BÜYÜK UZMANLIK PROGRAMLARI (KARİYER YOLU DİPLOMALARI)",
                    emoji = "👑",
                    items = listOf(
                        TopicCheckItem(
                            id = "btk_career_android",
                            title = "Mobil Uygulama Geliştiricisi (Android) Kariyer Yolu",
                            description = "Kotlin + Temel Android + İleri Android eğitimlerinin tamamını kapsayan büyük uzmanlık programı.",
                            practiceTask = "Kariyer yolundaki tüm dersleri bitir; profilinde resmi 'Android Geliştirici' uzmanlık diploması açılsın."
                        ),
                        TopicCheckItem(
                            id = "btk_career_backend",
                            title = "Web / .NET Backend Geliştiricisi Kariyer Yolu",
                            description = "C#, ASP.NET Core Web API, SQL ve mimari eğitimlerinin birleştiği ana uzmanlık programı.",
                            practiceTask = "Tüm backend paketini tamamlayıp 1 Milyon İstihdam havuzuna 'Backend Geliştiricisi' unvanını işlet."
                        ),
                        TopicCheckItem(
                            id = "btk_career_qa",
                            title = "Yazılım Test Uzmanı Gelişim Programı",
                            description = "Yazılım testi, API testi ve test otomasyonunu birleştiren kariyer gelişim yolu.",
                            practiceTask = "Tüm test eğitimlerini tamamlayarak resmi 'Yazılım Test Uzmanı' gelişim programı diplomasını al."
                        ),
                        TopicCheckItem(
                            id = "btk_career_cyber",
                            title = "Siber Güvenlik Analisti Gelişim Programı",
                            description = "Ağ temelleri, siber saldırı tespiti ve savunma süreçlerini kapsayan resmi büyük kariyer diploması.",
                            practiceTask = "Siber güvenlik yolundaki dersleri tamamlayarak e-Devlet ve 1 Milyon İstihdam'da 'Siber Güvenlik Analisti' unvanı kazan."
                        )
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
                ),
                TopicSection(
                    title = "TEST KÜLTÜRÜ & KALİTE",
                    emoji = "🧪",
                    items = listOf(
                        TopicCheckItem("swe_test_pyramid", "Test Piramidi & Test Türleri", "Unit Test, Integration Test ve E2E Test dengesi, piramit kuralı ve ROI dengesi", practiceTask = "Geliştirdiğin bir projede hangi mantıkların Unit Test, hangilerinin Integration Test ile kapsanması gerektiğini analiz et."),
                        TopicCheckItem("swe_unit_principles", "Unit Test & AAA Pattern", "Arrange-Act-Assert kalıbı, bağımsız/hızlı testler, Mock vs Stub ayrımı", practiceTask = "Temel bir hesaplama/iş mantığı fonksiyonu seçip AAA (Arrange-Act-Assert) pattern uygulayarak izole unit testini yaz.")
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
