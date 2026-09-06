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
        // 10. VERİ YAPILARI & ALGORİTMALAR (BIG-O)
        // ==========================================
        "sub_data_structures_bigo" to SubItemRoadmap(
            subItemId = "sub_data_structures_bigo",
            title = "Veri Yapıları & Algoritmalar",
            subtitle = "Big-O, Karmaşıklık & Temel Veri Tipleri",
            emoji = "⚡",
            targetLevel = "Hedef: Kodun Zaman ve Bellek Maliyetini Öngören Bir Mühendis Olmak",
            overview = "Kodu sadece çalıştırmak değil; veri boyutları büyüdüğünde CPU ve RAM tüketimini optimize etmek, doğru veri yapısını doğru senaryoda seçmek.",
            sections = listOf(
                TopicSection(
                    title = "KARMAŞIKLIK ANALİZİ (BIG-O)",
                    emoji = "⏱️",
                    items = listOf(
                        TopicCheckItem(
                            id = "algo_bigo_basics",
                            title = "Big-O, Big-Omega & Big-Theta Kavramları",
                            description = "En kötü durum (Worst-case), ortalama durum, constant O(1), logaritmik O(log n), doğrusal O(n) ve karesel O(n²) farkı.",
                            practiceTask = "Yazdığın veya incelediğin 3 farklı döngülü algoritmanın zaman karmaşıklığını (Big-O) kağıt üzerinde hesapla."
                        ),
                        TopicCheckItem(
                            id = "algo_space_complexity",
                            title = "Zaman vs Alan (Space) Karmaşıklığı",
                            description = "Bellek tahsisi, recursion çağrı yığını (call stack) maliyeti, auxiliary space ve in-place algoritmalar.",
                            practiceTask = "Özyinelemeli (recursive) bir fonksiyonun bellek tüketimini call stack derinliği üzerinden analiz et."
                        )
                    )
                ),
                TopicSection(
                    title = "TEMEL & İLERİ VERİ YAPILARI",
                    emoji = "📦",
                    items = listOf(
                        TopicCheckItem(
                            id = "algo_array_linkedlist",
                            title = "Array (Dizi) vs LinkedList (Bağlı Liste)",
                            description = "Bellek yerleşimi (cache locality), rastgele erişim (O(1)) vs araya eleman ekleme/silme maliyeti.",
                            practiceTask = "Dizi ve Bağlı Liste yapılarının bellek erişim farklarını ve hangi senaryoda hangisinin seçilmesi gerektiğini kıyasla."
                        ),
                        TopicCheckItem(
                            id = "algo_hashtable",
                            title = "Hash Table (Hash Haritası) & Çarpışma Çözümü",
                            description = "Hash fonksiyonu, Chaining vs Open Addressing, Load Factor ve O(1) ortalama erişim mekanizması.",
                            practiceTask = "Hash Table'da iki anahtarın aynı indise düşmesi (collision) durumunda chaining ve open addressing nasıl çalışır açıkla."
                        ),
                        TopicCheckItem(
                            id = "algo_stack_queue",
                            title = "Stack (Yığın) & Queue (Kuyruk) Yapıları",
                            description = "LIFO vs FIFO mantığı, parantez dengeleme, geri alma (undo) geçmişi, Circular Queue ve çift uçlu Deque.",
                            practiceTask = "Stack kullanarak parantezlerin dengeli olup olmadığını (örn. '{[()]}') kontrol eden basit bir fonksiyon kurgula."
                        ),
                        TopicCheckItem(
                            id = "algo_trees_heaps",
                            title = "Ağaçlar (BST) & Heap (Öncelikli Kuyruk)",
                            description = "Binary Search Tree kuralları, ağaç dengelenmesi, Min/Max Heap ve PriorityQueue kullanım alanları.",
                            practiceTask = "Bir BST üzerinde küçükten büyüğe sıralı dolaşımın (In-order traversal) mantığını çıkar."
                        )
                    )
                ),
                TopicSection(
                    title = "KLASİK ALGORİTMALAR & PROBLEM ÇÖZME",
                    emoji = "🧠",
                    items = listOf(
                        TopicCheckItem(
                            id = "algo_binary_search",
                            title = "İkili Arama (Binary Search)",
                            description = "Sıralı dizilerde O(log n) karmaşıklıkla böl ve fethet (Divide and Conquer) tekniği.",
                            practiceTask = "Sıralı bir dizide aranan elemanın indisini döndüren iterative Binary Search algoritmasını yaz."
                        ),
                        TopicCheckItem(
                            id = "algo_sort_comparison",
                            title = "Sıralama Algoritmaları: QuickSort vs MergeSort",
                            description = "O(n log n) sıralama algoritmaları, Worst-case senaryoları, bellek maliyeti ve kararlılık (stability).",
                            practiceTask = "QuickSort'un pivot seçimine bağlı kötü durumunu ve MergeSort'un ek bellek ihtiyacını karşılaştır."
                        ),
                        TopicCheckItem(
                            id = "algo_dfs_bfs",
                            title = "Graf ve Ağaç Gezinmesi: DFS vs BFS",
                            description = "Derinlik öncelikli arama (Stack/Recursion) ve Genişlik öncelikli arama (Queue/En kısa yol).",
                            practiceTask = "BFS'nin en kısa yol bulmada neden kullanıldığını ve DFS ile arasındaki mantık farkını özetle."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // 11. SİSTEM TASARIMI & MİMARİ MANTIĞI
        // ==========================================
        "sub_system_design" to SubItemRoadmap(
            subItemId = "sub_system_design",
            title = "Sistem Tasarımı Temelleri",
            subtitle = "Ölçeklenebilirlik, Caching, CAP & Dayanıklılık",
            emoji = "🏗️",
            targetLevel = "Hedef: Milyonlarca Kullanıcıya Hizmet Veren Sistemlerin Mimarisini Kavramak",
            overview = "Tek sunucudan yatayda büyüyen devasa sistemlere geçiş; yük dağıtımı, önbellekleme stratejileri, mesajlaşma kuyrukları ve veri tutarlılığı modelleri.",
            sections = listOf(
                TopicSection(
                    title = "ÖLÇEKLENEBİLİRLİK & YÜK DENGELEME",
                    emoji = "⚖️",
                    items = listOf(
                        TopicCheckItem(
                            id = "sys_vert_horiz",
                            title = "Dikey (Scale-Up) vs Yatay (Scale-Out) Büyüme",
                            description = "CPU/RAM yükseltme sınırları, stateless servis mimarisi ve cluster yapılandırması.",
                            practiceTask = "Bir API servisinin yatayda büyüyebilmesi için session state'in sunucuda tutulmamasının önemini analiz et."
                        ),
                        TopicCheckItem(
                            id = "sys_load_balancing",
                            title = "Load Balancer Stratejileri",
                            description = "Round Robin, Least Connections, IP Hash algoritmaları, L4 vs L7 dengeleme ve Health Check kontrolleri.",
                            practiceTask = "L4 (TCP seviyesi) ile L7 (HTTP seviyesi) yük dengeleyicilerin farkını ve kullanım yerlerini kıyasla."
                        ),
                        TopicCheckItem(
                            id = "sys_rate_limiting",
                            title = "Rate Limiting & Throttling",
                            description = "Token Bucket ve Leaky Bucket algoritmaları, DDoS/Spam savunması, 429 Too Many Requests cevabı.",
                            practiceTask = "Token Bucket algoritmasının ani trafik patlamalarına (burst) nasıl izin verdiğini incele."
                        )
                    )
                ),
                TopicSection(
                    title = "ÖNBELLEK (CACHE) & VERİTABANI ÖLÇEKLENDİRME",
                    emoji = "⚡",
                    items = listOf(
                        TopicCheckItem(
                            id = "sys_cache_patterns",
                            title = "Caching Kalıpları: Cache-Aside vs Write-Through",
                            description = "Cache-Aside okuma, Write-Back/Write-Through yazma kalıpları, TTL (Time-to-Live) ve Cache Invalidation zorluğu.",
                            practiceTask = "En popüler kalıp olan Cache-Aside stratejisinin akış diyagramını zihninde canlandır."
                        ),
                        TopicCheckItem(
                            id = "sys_db_scaling",
                            title = "Veritabanı Büyütme: Replication & Sharding",
                            description = "Master-Replica (Yazma/Okuma ayrımı), dikey bölme ve yatay parçalama (Sharding), Partition Key seçimi.",
                            practiceTask = "Kullanıcı ID'sine göre veri tabanını sharding yaparken partition key seçiminin önemini açıkla."
                        ),
                        TopicCheckItem(
                            id = "sys_cap_theorem",
                            title = "CAP Teoremi & PACELC",
                            description = "Consistency (Tutarlılık), Availability (Erişilebilirlik), Partition Tolerance (Ağ Bölünmesi) ve uzlaşmalar.",
                            practiceTask = "Ağ kesintisi anında (Partition) bir sistemin neden hem %100 Consistency hem %100 Availability sağlayamayacağını kavra."
                        )
                    )
                ),
                TopicSection(
                    title = "DAYANIKLILIK & ASENKRON İLETİŞİM",
                    emoji = "🛡️",
                    items = listOf(
                        TopicCheckItem(
                            id = "sys_event_driven",
                            title = "Mesaj Kuyrukları & Event-Driven Mimari",
                            description = "RabbitMQ, Kafka, Producer-Consumer kalıbı, Publish/Subscribe, kuyrukta mesaj birikmesi ve Decoupling.",
                            practiceTask = "Sipariş oluşturma işleminde e-posta gönderiminin asenkron mesaj kuyruğuyla ayrıştırılmasının faydalarını listele."
                        ),
                        TopicCheckItem(
                            id = "sys_resilience",
                            title = "Dayanıklılık Kalıpları: Circuit Breaker & Retry",
                            description = "Çöken servise istek göndermeyi kesen devre kesici (Circuit Breaker), Exponential Backoff ve Jitter.",
                            practiceTask = "Bir dış servisin çökmesi durumunda Circuit Breaker'ın sistemin kilitlenmesini nasıl engellediğini açıkla."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // 12. BİLGİSAYAR AĞLARI & WEB PROTOKOLLERİ
        // ==========================================
        "sub_networking_web" to SubItemRoadmap(
            subItemId = "sub_networking_web",
            title = "Bilgisayar Ağları & Web",
            subtitle = "DNS, TCP/IP, HTTP/1-2-3 & İletişim",
            emoji = "🌐",
            targetLevel = "Hedef: Web ve Mobil İsteklerinin Fiziksel & Mantıksal Yolculuğuna Hakim Olmak",
            overview = "Tarayıcıya veya mobil uygulamaya istek atıldığında arkada çalışan DNS, TCP/IP, TLS el sıkışması, HTTP evrimi ve gerçek zamanlı haberleşme protokolleri.",
            sections = listOf(
                TopicSection(
                    title = "İNTERNETİN TEMELLERİ & TAŞIMA KATMANI",
                    emoji = "🌐",
                    items = listOf(
                        TopicCheckItem(
                            id = "net_dns_journey",
                            title = "google.com İstek Yolculuğu & DNS Çözümleme",
                            description = "Tarayıcı cache, OS hosts, Recursive Resolver, Root, TLD (.com) ve Authoritative DNS sorgusu.",
                            practiceTask = "Terminalde `dig google.com` veya `nslookup` çalıştırarak DNS sorgu çözümleme adımlarını gözlemle."
                        ),
                        TopicCheckItem(
                            id = "net_tcp_udp",
                            title = "TCP vs UDP & 3-Way Handshake",
                            description = "SYN - SYN/ACK - ACK el sıkışması, paket sıralaması, güvenilirlik vs UDP'nin düşük gecikmesi.",
                            practiceTask = "Video akışı / online oyunların neden UDP, finansal / web isteklerinin neden TCP kullandığını kıyasla."
                        ),
                        TopicCheckItem(
                            id = "net_tls_handshake",
                            title = "TLS/SSL El Sıkışması & HTTPS Güvenliği",
                            description = "Sertifika doğrulama, Asimetrik anahtar ile ortak oturum anahtarı üretimi ve Simetrik şifrelemeye geçiş.",
                            practiceTask = "HTTPS bağlantısında asimetrik şifrelemenin sadece el sıkışmada kullanılma sebebini (performans) kavra."
                        )
                    )
                ),
                TopicSection(
                    title = "HTTP PROTOKOLLERİ & GERÇEK ZAMANLI İLETİŞİM",
                    emoji = "⚡",
                    items = listOf(
                        TopicCheckItem(
                            id = "net_http_evolution",
                            title = "HTTP/1.1 vs HTTP/2 vs HTTP/3 (QUIC)",
                            description = "Keep-Alive, Multiplexing (tek bağlantıda çoklu istek), Head-of-line blocking ve UDP tabanlı QUIC.",
                            practiceTask = "HTTP/2'deki multiplexing özelliğinin HTTP/1.1'deki istek kuyruğu tıkanmasını nasıl çözdüğünü incele."
                        ),
                        TopicCheckItem(
                            id = "net_realtime",
                            title = "Gerçek Zamanlı İletişim: WebSocket vs SSE vs Polling",
                            description = "Tam çift yönlü (Full-Duplex) socket bağlantısı, sunucudan tek yönlü akış (SSE) ve Long Polling maliyeti.",
                            practiceTask = "Bir sohbet uygulamasında neden WebSocket, borsa/haber akışında neden SSE tercih edildiğini karşılaştır."
                        ),
                        TopicCheckItem(
                            id = "net_cors_preflight",
                            title = "CORS Politikası & Preflight (OPTIONS)",
                            description = "Same-Origin Policy, Origin, Access-Control-Allow-Origin başlıkları ve OPTIONS preflight kontrolü.",
                            practiceTask = "Frontend uygulamasının farklı porttaki backend API'sine istek atarken CORS hatası alma senaryosunu incele."
                        )
                    )
                ),
                TopicSection(
                    title = "DAĞITIM VE ALTYAPI AĞLARI",
                    emoji = "🚀",
                    items = listOf(
                        TopicCheckItem(
                            id = "net_cdn_edge",
                            title = "CDN & Edge Caching Mantığı",
                            description = "Statik dosyaların (resim, JS, CSS) kullanıcıya en yakın PoP (Point of Presence) noktasından sunulması.",
                            practiceTask = "CDN kullanımının sunucu yükünü azaltma ve TTFB (Time to First Byte) süresini düşürme etkisini açıkla."
                        ),
                        TopicCheckItem(
                            id = "net_reverse_proxy",
                            title = "Reverse Proxy (Nginx) & SSL Termination",
                            description = "İstemci ile backend arasında ters vekil sunucu, SSL sonlandırma, IP gizleme ve statik dosya sunumu.",
                            practiceTask = "Bir backend API'sinin önüne Nginx koymanın güvenlik ve performans avantajlarını özetle."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // 13. YAPAY ZEKA & LLM OKURYAZARLIĞI
        // ==========================================
        "sub_ai_llm" to SubItemRoadmap(
            subItemId = "sub_ai_llm",
            title = "Yapay Zeka & LLM Okuryazarlığı",
            subtitle = "Büyük Dil Modelleri, RAG & AI Ajanları",
            emoji = "🤖",
            targetLevel = "Hedef: Modern Yazılım Süreçlerinde Yapay Zekayı Anlayan & Yöneten Mühendis",
            overview = "Yapay zekayı sadece kullanıcı olarak tüketmek değil; token mantığı, vektör veritabanları, semantik arama ve AI Agent'ların çalışma mekaniğini kavramak.",
            sections = listOf(
                TopicSection(
                    title = "LLM VE ÜRETKEN YAPAY ZEKA ÇEKİRDEĞİ",
                    emoji = "🧠",
                    items = listOf(
                        TopicCheckItem(
                            id = "ai_llm_internals",
                            title = "LLM Çalışma Mantığı: Token, Context & Transformer",
                            description = "Olasılıksal sonraki kelime tahmini, token hesabı, context window sınırları ve halüsinasyonun temel sebebi.",
                            practiceTask = "Bir cümlenin kaç token ettiğini analiz et ve context window aşımında modelin eski bilgileri neden unuttuğunu kavra."
                        ),
                        TopicCheckItem(
                            id = "ai_temperature",
                            title = "Parametreler: Temperature, Top-P & Determinism",
                            description = "Yaratıcılık vs tutarlılık ayarları, kodlama için düşük temperature (0.0 - 0.2), hikaye için yüksek temperature.",
                            practiceTask = "Kod üretimi ve JSON çıktı alırken neden temperature değerinin 0'a yakın tutulması gerektiğini açıkla."
                        ),
                        TopicCheckItem(
                            id = "ai_prompt_eng",
                            title = "Prompt Mühendisliği & Düşünce Zinciri (CoT)",
                            description = "Sistem rolü tanımlama, adım adım düşünme (Chain-of-Thought), Zero-Shot vs Few-Shot prompt teknikleri.",
                            practiceTask = "Karmaşık bir algoritma problemini LLM'e çözerken adım adım düşünmesini sağlayan bir Few-Shot prompt hazırla."
                        )
                    )
                ),
                TopicSection(
                    title = "RAG & VEKTÖR VERİTABANLARI",
                    emoji = "📚",
                    items = listOf(
                        TopicCheckItem(
                            id = "ai_embeddings",
                            title = "Vektörler (Embeddings) & Semantik Arama",
                            description = "Metinlerin anlam uzayında çok boyutlu sayılara dönüştürülmesi, kosinüs benzerliği (Cosine Similarity).",
                            practiceTask = "'Elma' ve 'Armut' kelimelerinin vektör uzayında neden 'Araba' kelimesine göre birbirine daha yakın olduğunu açıkla."
                        ),
                        TopicCheckItem(
                            id = "ai_rag_architecture",
                            title = "RAG (Retrieval-Augmented Generation) Mimarisi",
                            description = "Kendi dokümanlarını parçalama (chunking), vektör veritabanında arama ve bulunan bağlamla modeli besleme.",
                            practiceTask = "RAG mimarisinin modeli sıfırdan eğitmeden (fine-tuning) güncel şirket verileriyle konuşturma avantajını özetle."
                        )
                    )
                ),
                TopicSection(
                    title = "AI AGENT'LAR & GELİŞTİRİCİ VERİMLİLİĞİ",
                    emoji = "🛠️",
                    items = listOf(
                        TopicCheckItem(
                            id = "ai_function_calling",
                            title = "Function Calling & Tool Use Mantığı",
                            description = "LLM'in dış dünyayla konuşması: JSON formatında fonksiyon parametresi üretip hava durumu, DB veya API sorgulaması.",
                            practiceTask = "Bir yapay zekanın veri tabanına doğrudan bağlanmak yerine function calling ile nasıl sorgu yaptırdığını kavra."
                        ),
                        TopicCheckItem(
                            id = "ai_coding_tools",
                            title = "AI Destekli Kodlama & Agentic Yazılım Kültürü",
                            description = "Cursor, Copilot, Antigravity gibi araçlarla kod tamamlama, birim test üretme, dokümantasyon ve refactoring.",
                            practiceTask = "Geliştirdiğin bir fonksiyon için yapay zekaya sınır durumları (edge cases) kapsayan birim test senaryoları yazdır."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // KİŞİSEL GELİŞİM 1. WINTER ARC & SAĞLIK PROTOKOLÜ
        // ==========================================
        "sub_winter_arc_discipline" to SubItemRoadmap(
            subItemId = "sub_winter_arc_discipline",
            title = "Winter Arc & Sağlık Protokolü",
            subtitle = "Dopamin Detoksu, Uyku, Su, Beslenme, Spor & Tenis",
            emoji = "⚡",
            targetLevel = "Hedef: Zihinsel Berraklık, Yüksek Enerji & Sarsılmaz Disiplin",
            overview = "Dopamin detoksu ile dikkat dağınıklığını sıfırlamak, kesintisiz uyku ve bol su ile zihinsel berraklık kazanmak; ağırlık, tenis ve yürüyüş ile bedensel gücü korumak.",
            sections = listOf(
                TopicSection(
                    title = "DOPAMİN DETOKSU & ZİHİNSEL DİSİPLİN",
                    emoji = "🧠",
                    items = listOf(
                        TopicCheckItem(
                            id = "dop_no_shorts",
                            title = "Shorts, Reels & TikTok Blokajı",
                            description = "Beyni yoran, dikkat süresini (attention span) düşüren ve dopamin reseptörlerini körleştiren sonsuz dikey video akışlarını sıfırlamak.",
                            practiceTask = "Sosyal medya uygulamalarındaki bildirimleri kapat; gün içinde refleks olarak açmamak için ekran süresi sınırlarını aktifleştir."
                        ),
                        TopicCheckItem(
                            id = "dop_mindful_screen",
                            title = "Bilinçli Ekran Süresi & Bildirim Temizliği",
                            description = "Telefona sadece belirli amaçlar (iletişim, iş, öğrenme) için bakmak; gereksiz tüm uygulama bildirimlerini sessize almak.",
                            practiceTask = "Gereksiz tüm alışveriş ve eğlence bildirimlerini kapat; ana ekranı sadece temel araçlar kalacak şekilde sadeleştir."
                        ),
                        TopicCheckItem(
                            id = "dop_streak_tracker",
                            title = "Bozdum / Bozmadım Günlük Streak Takibi",
                            description = "Her günü berrak bir bilinçle bitirip seriyi büyütmek; anlık dürtü krizlerinde 10 dakika erteleme kuralını uygulamak.",
                            practiceTask = "Dopamin krizine girdiğinde telefon yerine 1 bardak soğuk su içip 10 derin nefes al."
                        ),
                        TopicCheckItem(
                            id = "dop_boredom_peace",
                            title = "Sıkılmayı Kabullenme & Zihinsel Reset",
                            description = "Boşta kalınan anlarda (otobüste, sırada, mola anında) hemen cebe sarılmak yerine zihnin kendi düşünceleriyle kalmasına izin vermek.",
                            practiceTask = "Günde 15 dakika hiçbir ekrana bakmadan, sadece sessizlikte otur veya kısa bir yürüyüş yap."
                        )
                    )
                ),
                TopicSection(
                    title = "UYKU KALİTESİ & SİRKADİYEN RİTİM",
                    emoji = "🛌",
                    items = listOf(
                        TopicCheckItem(
                            id = "slp_fixed_schedule",
                            title = "Sabit Uyku & Uyanış Saati",
                            description = "Biyolojik saati (sirkadiyen ritim) oturtmak için hafta sonları dahil benzer saatlerde yatıp uyanmak.",
                            practiceTask = "Hedeflenen uyanış saatini belirle ve 7 gün boyunca alarm çaldığı anda yatağı terk et."
                        ),
                        TopicCheckItem(
                            id = "slp_huawei_health",
                            title = "Huawei Sağlık ile Uyku Skoru Takibi",
                            description = "Akıllı saat verileriyle derin uyku yüzdesi, REM süresi ve gece uyanıklıklarını düzenli analiz etmek.",
                            practiceTask = "Huawei Sağlık uygulamasındaki haftalık uyku skoru ortalamasını 80+ puan üzerinde tutmayı hedefle."
                        ),
                        TopicCheckItem(
                            id = "slp_no_blue_light",
                            title = "Yatmadan 1 Saat Önce Mavi Işık Yasağı",
                            description = "Melatonin üretimini korumak için yatmadan önce telefon ve bilgisayarı bırakıp kitap okumaya veya gevşemeye geçmek.",
                            practiceTask = "Yatmadan 45 dakika önce telefonunu şarja takıp uzaklaştır ve bir kitaptan 15-20 sayfa oku."
                        ),
                        TopicCheckItem(
                            id = "slp_morning_sunlight",
                            title = "Sabah İlk Güneş Işığı & Doğal Uyanış",
                            description = "Uyanıştan sonraki ilk 30 dakikada doğrudan doğal ışık alarak kortizol ritmini ve günün enerji seviyesini sağlıklı başlatmak.",
                            practiceTask = "Uyandıktan sonra balkona veya pencere önüne çıkıp 5-10 dakika doğal gün ışığı al."
                        )
                    )
                ),
                TopicSection(
                    title = "HİDRASYON & ZİHİNSEL BERRAKLIK BESLENMESİ",
                    emoji = "💧",
                    items = listOf(
                        TopicCheckItem(
                            id = "hyd_daily_water",
                            title = "Günlük Minimum 2.5 - 3.0 Litre Su",
                            description = "Zihinsel yorgunluk, odak kaybı ve baş ağrısını önleyen en temel fizyolojik alışkanlık.",
                            practiceTask = "Çalışma masanda sürekli 1 litrelik matara bulundur; öğleye kadar 1.5L, akşama kadar 3L'yi tamamla."
                        ),
                        TopicCheckItem(
                            id = "nut_clean_energy",
                            title = "Temiz Beslenme & Şeker/Fast-Food Kısıtlaması",
                            description = "Ağır karbonhidrat ve işlenmiş şeker tüketiminin yarattığı öğleden sonraki zihinsel sis (brain fog) ve uyku hissini önlemek.",
                            practiceTask = "Haftalık beslenmende şekerli içecekleri sıfırla, öğünlerde kaliteli protein ve yeşillik oranını artır."
                        ),
                        TopicCheckItem(
                            id = "nut_caffeine_timing",
                            title = "Stratejik Kafein Zamanlaması",
                            description = "Uyandıktan sonraki ilk 60-90 dakika adenozin birikimini bekleyip ardından ilk kahveyi içmek; saat 15:00'ten sonra kafeini kesmek.",
                            practiceTask = "İlk kahveni sabah uyandıktan en az 1 saat sonra iç ve saat 15:30'dan sonra yalnızca bitki çayı veya su tüket."
                        )
                    )
                ),
                TopicSection(
                    title = "BEDENSEL GÜÇ: SPOR, TENİS & YÜRÜYÜŞ",
                    emoji = "🎾",
                    items = listOf(
                        TopicCheckItem(
                            id = "fit_gym_strength",
                            title = "Düzenli Ağırlık / Fitness Antrenmanı",
                            description = "Haftada 3-4 gün kas kütlesi, güç, testosteron ve fiziksel dayanıklılık için planlı ağırlık antrenmanı.",
                            practiceTask = "Antrenman günlerinde hareket formlarına ve aşamalı yüklemeye (progressive overload) odaklan."
                        ),
                        TopicCheckItem(
                            id = "fit_tennis_matches",
                            title = "Tenis Seansları (Kardiyo & Çeviklik)",
                            description = "Yüksek el-göz koordinasyonu, ayak hareketliliği, patlayıcı güç ve maç adrenalini sağlayan tenis antrenmanları.",
                            practiceTask = "Haftalık spor takvimine en az 1-2 tenis seansı ekle; servis ve vole pratiklerine odaklan."
                        ),
                        TopicCheckItem(
                            id = "fit_daily_steps",
                            title = "Günlük Yürüyüş & Temiz Hava (7.000 - 10.000 Adım)",
                            description = "Pasif kalori yakımı, düşünceleri toparlama ve mental yenilenme sağlayan günlük tempolu yürüyüş.",
                            practiceTask = "Akşam yemeğinden sonra veya çalışma arasında 25-30 dakikalık açık hava yürüyüşü yap."
                        ),
                        TopicCheckItem(
                            id = "fit_posture_mobility",
                            title = "Masa Başı Postür & Omurga Mobilitesi",
                            description = "Uzun kodlama saatlerinin omurgaya ve boyna yüklediği baskıyı kaldıran göğüs açma, sırt ve kalça esnetmeleri.",
                            practiceTask = "Günde iki kez 5 dakikalık kapı eşiği göğüs esnetmesi ve kalça fleksör mobilitesi uygula."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // KİŞİSEL GELİŞİM 2. KİTAP DÜNYASI
        // ==========================================
        "sub_reading_books" to SubItemRoadmap(
            subItemId = "sub_reading_books",
            title = "Kitap Dünyası",
            subtitle = "Tarih, Kişisel Gelişim, Felsefe & Edebiyat",
            emoji = "📚",
            targetLevel = "Hedef: Yılda 20+ Nitelikli Eser, Günlük 20-30 Sayfa Rutini",
            overview = "Tarih perspektifi kazandıran kurucu eserler, zihinsel disiplini artıran kişisel gelişim kitapları, Stoa felsefesi ve dünya edebiyatı romanları.",
            sections = listOf(
                TopicSection(
                    title = "TARİH & BİYOGRAFİ KİTAPLARI",
                    emoji = "🏛️",
                    items = listOf(
                        TopicCheckItem(
                            id = "bk_hist_sapiens",
                            title = "Yuval Noah Harari - Sapiens & Homo Deus",
                            description = "İnsan türünün bilişsel, tarım ve bilimsel devrimlerini ve türümüzün geleceğini anlatan başyapıt.",
                            practiceTask = "Kitaptan aldığın en çarpıcı 3 kavrayışı (bilişsel devrim, ortak mitler vb.) kendi cümlelerinle not et."
                        ),
                        TopicCheckItem(
                            id = "bk_hist_guns_germs",
                            title = "Jared Diamond - Tüfek, Mikrop ve Çelik",
                            description = "Coğrafyanın, iklimin ve doğal kaynakların insan toplumlarının kaderini ve eşitsizliği nasıl belirlediğinin analizi.",
                            practiceTask = "Tarihsel gelişimde coğrafi avantajların medeniyetler üzerindeki etkisini özetleyen bir sayfa not çıkar."
                        ),
                        TopicCheckItem(
                            id = "bk_hist_turkish_ottoman",
                            title = "Halil İnalcık & İlber Ortaylı Seçkisi",
                            description = "Osmanlı Devleti'nin teşkilat yapısı, kurumları, yükselişi ve Cumhuriyet'in kurucu fikir temelleri.",
                            practiceTask = "Klasik Dönem Osmanlı kurumları veya Cumhuriyet devrimleri üzerine bir bölümü derinlemesine oku."
                        ),
                        TopicCheckItem(
                            id = "bk_hist_biography_leaders",
                            title = "Lider, Bilim & Vizyoner Biyografileri",
                            description = "Atatürk (Lord Kinross/Şevket Süreyya), Steve Jobs (Walter Isaacson), Leonardo da Vinci gibi çağı değiştiren liderlerin hayatları.",
                            practiceTask = "Biyografideki liderin zorluklarla başa çıkma ve odaklanma yöntemlerini kendi hayatına uyarla."
                        )
                    )
                ),
                TopicSection(
                    title = "KİŞİSEL GELİŞİM, PSİKOLOJİ & ALIŞKANLIKLAR",
                    emoji = "🌱",
                    items = listOf(
                        TopicCheckItem(
                            id = "bk_dev_atomic_habits",
                            title = "James Clear - Atomik Alışkanlıklar",
                            description = "Küçük değişimlerin bileşik getirisi, 4 adımlı alışkanlık döngüsü (İpucu, İstek, Tepki, Ödül) ve sistem tasarımı.",
                            practiceTask = "Winter Arc için 1 iyi alışkanlığı kolaylaştır (2 dakika kuralı) ve 1 kötü alışkanlığı zorlaştır."
                        ),
                        TopicCheckItem(
                            id = "bk_dev_mindset_dweck",
                            title = "Carol Dweck - Mindset (Gelişim Zihniyeti)",
                            description = "Yeteneklerin doğuştan sabit olmadığını, çaba ve doğru stratejilerle beynin geliştiğini gösteren büyüme zihniyeti.",
                            practiceTask = "Zorlandığın bir yazılım veya spor konusunda 'Henüz yapamıyorum' dilini benimse."
                        ),
                        TopicCheckItem(
                            id = "bk_dev_mans_search",
                            title = "Viktor Frankl - İnsanın Anlam Arayışı",
                            description = "Logoterapi kurucusu Frankl'ın toplama kamplarından çıkardığı varoluşsal anlam ve içsel özgürlük felsefesi.",
                            practiceTask = "Kendi temel yaşam değerlerini ve seni motive eden 3 ana hedefi yazılı hale getir."
                        ),
                        TopicCheckItem(
                            id = "bk_dev_influence_cialdini",
                            title = "Robert Cialdini - İknanın Psikolojisi",
                            description = "Karşılıklılık, sosyal kanıt, otorite, tutarlılık ve kıtlık gibi insan davranışlarını yönlendiren psikolojik mekanizmalar.",
                            practiceTask = "Günlük hayatta maruz kaldığın pazarlama ve ikna taktiklerini bu 6 prensibe göre gözlemle."
                        )
                    )
                ),
                TopicSection(
                    title = "STOA FELSEFESİ & ZİHİNSEL DİAYANIKLILIK",
                    emoji = "🗿",
                    items = listOf(
                        TopicCheckItem(
                            id = "bk_phil_meditations",
                            title = "Marcus Aurelius - Kendime Düşünceler",
                            description = "Roma İmparatoru'nun savaş meydanlarında kendine yazdığı özyönetim, ego kontrolü ve kontrol çemberi notları.",
                            practiceTask = "Bugün seni sinirlendiren bir olayda kontrol edemediğin kısmı bırakıp sadece kendi tepkine odaklan."
                        ),
                        TopicCheckItem(
                            id = "bk_phil_seneca_shortness",
                            title = "Seneca - Yaşamın Kısalığı Üzerine & Ahlak Mektupları",
                            description = "Hayatın kısa olmadığını, çoğunu boş işlerle israf ettiğimizi anlatan zamansız bir zaman yönetimi başyapıtı.",
                            practiceTask = "Günün 24 saatini nasıl geçirdiğini dürüstçe analiz et ve en çok zaman yiyen 1 unsuru buda."
                        ),
                        TopicCheckItem(
                            id = "bk_phil_epictetus",
                            title = "Epiktetos - Düşünceler ve Sohbetler (Enchiridion)",
                            description = "Eski bir köle olan Epiktetos'un içsel özgürlük, olaylara bakış açımız ve kontrol edilebilir alan öğretisi.",
                            practiceTask = "'Bizi üzen şeyler olaylar değil, olaylar hakkındaki düşüncelerimizdir' prensibini bir olayda test et."
                        )
                    )
                ),
                TopicSection(
                    title = "ROMAN & DÜNYA EDEBİYATI KLASİKLERİ",
                    emoji = "📖",
                    items = listOf(
                        TopicCheckItem(
                            id = "bk_lit_dostoyevski",
                            title = "Dostoyevski - Suç ve Ceza / Karamazov Kardeşler",
                            description = "Raskolnikov'un vicdan muhasebesi, insan psikolojisinin derin dehlizleri ve felsefi ahlak sorgulamaları.",
                            practiceTask = "Karakterlerin ahlaki ikilemleri ve iç monologları üzerine odaklanarak oku."
                        ),
                        TopicCheckItem(
                            id = "bk_lit_orwell_distopia",
                            title = "George Orwell - 1984 & Hayvan Çiftliği",
                            description = "Totalitarizm, zihin kontrolü (doublethink), dil manipülasyonu (newspeak) ve bireysel özgürlük temaları.",
                            practiceTask = "Kavramların ve dilin düşünce biçimini nasıl şekillendirdiğini gözlemle."
                        ),
                        TopicCheckItem(
                            id = "bk_lit_camus_stranger",
                            title = "Albert Camus - Yabancı & Veba / Sisifos Söyleni",
                            description = "Absürdizm felsefesi, toplumsal normlar karşısında yabancılaşma ve anlamsızlığa karşı varoluşsal direnç.",
                            practiceTask = "Meursault karakterinin topluma ve hayata bakışını absürt felsefesiyle karşılaştır."
                        ),
                        TopicCheckItem(
                            id = "bk_lit_favorites_reading",
                            title = "Kişisel Roman Seçkisi & Çağdaş Başyapıtlar",
                            description = "Kişisel ilgi alanına göre seçilen dünya klasikleri, bilim kurgu (Dune, Vakıf) veya sürükleyici kurgu eserleri.",
                            practiceTask = "Yatmadan önce en az 20 sayfa akıcı roman okuma alışkanlığını kesintisiz sürdür."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // KİŞİSEL GELİŞİM 3. KART NUMARALARI & İLLÜZYON
        // ==========================================
        "sub_card_sleights" to SubItemRoadmap(
            subItemId = "sub_card_sleights",
            title = "Kart Numaraları & İllüzyon",
            subtitle = "Sleight of Hand Mekanikleri & Performans",
            emoji = "🃏",
            targetLevel = "Hedef: Pürüzsüz Sleight of Hand, Kart Kontrolü & Sahne Büyüsü",
            overview = "Parmak hassasiyeti, deste hakimiyeti, klasik sleight of hand teknikleri, misdirection (dikkat yönetimi) ve seyirci karşısında akıcı sunum.",
            sections = listOf(
                TopicSection(
                    title = "TEMEL TUTUŞLAR & DESTE HAKİMİYETİ",
                    emoji = "🖐️",
                    items = listOf(
                        TopicCheckItem(
                            id = "crd_grips",
                            title = "Mechanic's Grip & Biddle Grip Ustalığı",
                            description = "Desteyi tutarken parmakların rahat ve gerilimsiz olması; doğal bir görünüm sergileme.",
                            practiceTask = "Ayna karşısında deste tutuşunun gergin değil, tamamen gündelik ve doğal göründüğünü doğrula."
                        ),
                        TopicCheckItem(
                            id = "crd_pinky_break",
                            title = "Pinky Break & Thumb Break Kontrolü",
                            description = "Küçük parmakla fark edilmeden aralık tutma; önden ve yandan bakış açılarında boşluğu gizleme.",
                            practiceTask = "Kartların arasında serçe parmağınla tuttuğun break'i destenin ön tarafından görünmez kıl."
                        ),
                        TopicCheckItem(
                            id = "crd_riffle_shuffle",
                            title = "Pürüzsüz Riffle Shuffle, Waterfall & Faro Temelleri",
                            description = "Desteyi profesyonel bir akıcılıkla karıştırma, yay ve şelale (waterfall) kapanışı yapma.",
                            practiceTask = "Kartları bükmeden ve zorlamadan 10 kez art arda pürüzsüz waterfall ile karıştır."
                        )
                    )
                ),
                TopicSection(
                    title = "SLEIGHT OF HAND & KONTROL MEKANİKLERİ",
                    emoji = "✨",
                    items = listOf(
                        TopicCheckItem(
                            id = "crd_double_lift",
                            title = "Doğal & Kusursuz Double Lift",
                            description = "İki kartı tek bir kartmış gibi kusursuzca çevirme — kart sihirbazlığının en kritik temel taşı.",
                            practiceTask = "Double lift yaparken kartları tek kart çeviriyormuş gibi gevşek ve hızlıca çevirip masaya bırak."
                        ),
                        TopicCheckItem(
                            id = "crd_pass_control",
                            title = "Classic Pass & Charlier Cut",
                            description = "Seyircinin destenin ortasına koyduğu kartı sessizce ve fark ettirmeden en üste getirme manevrası.",
                            practiceTask = "Tek elle Charlier Cut'ı her iki elinle de takılmadan yapabilecek seviyeye getir."
                        ),
                        TopicCheckItem(
                            id = "crd_elmsley_count",
                            title = "Elmsley Count & Jordan Count",
                            description = "4 kart sayarken 3. kartı gizleyip başka bir kartı iki kez sayma tekniği.",
                            practiceTask = "4 kartlık bir pakette Elmsley count yaparak ters dönmüş kartı hiç göstermeden saymayı başar."
                        ),
                        TopicCheckItem(
                            id = "crd_palming_skills",
                            title = "Top Palm & Bottom Palm Teknikleri",
                            description = "Seyirciye hissettirmeden destenin en üstündeki veya altındaki kartı avuç içine alma.",
                            practiceTask = "Desteyi masaya koyarken üst kartı sağ avucuna al ve elini doğal bir pozisyonda masada dinlendir."
                        ),
                        TopicCheckItem(
                            id = "crd_false_cuts",
                            title = "False Cuts & False Shuffles (Sahte Kesmeler)",
                            description = "Desteyi defalarca kesiyormuş gibi gösterip tüm deste sırasını koruyan görsel manevralar.",
                            practiceTask = "Seyirci önünde desteyi üç parçaya bölüp sahte kesme yaparak en üst kartın değişmediğini göster."
                        )
                    )
                ),
                TopicSection(
                    title = "KLASİK NUMARALAR & REPERTUAR",
                    emoji = "🎩",
                    items = listOf(
                        TopicCheckItem(
                            id = "crd_ambitious_card",
                            title = "Ambitious Card Routine (Tırmanan Kart)",
                            description = "İmzalanan kartın destenin neresine konulursa konulsun tekrar tekrar en üste çıkması rutini.",
                            practiceTask = "3 aşamalı (Double lift, tilt move ve son final) kesintisiz bir Ambitious Card rutini sergile."
                        ),
                        TopicCheckItem(
                            id = "crd_triumph_effect",
                            title = "Triumph (Ters Yüz Karışan Kartlar)",
                            description = "Kartların yarısı ters yarısı düz şekilde karıştırıldıktan sonra sihirli bir şıklatmayla seçilen kart hariç tüm destenin düzelmesi.",
                            practiceTask = "Triumph'taki sahte karıştırma açısını ayna karşısında prova et ve temizliğini test et."
                        ),
                        TopicCheckItem(
                            id = "crd_sandwich_routine",
                            title = "Sandwich Effect & Card to Pocket",
                            description = "İki asın arasına seçilen kartın ışınlanması veya seyircinin cebinden/cüzdanından çıkması.",
                            practiceTask = "İki siyah papazın arasına seyircinin seçtiği kartı görsel olarak yakalama numarasını tamamla."
                        )
                    )
                ),
                TopicSection(
                    title = "MİSDİRECTİON, ANLATI (PATTER) & PERFORMANS",
                    emoji = "🎭",
                    items = listOf(
                        TopicCheckItem(
                            id = "crd_misdirection_rules",
                            title = "Göz Teması & Misdirection (Büyük/Küçük Hareket)",
                            description = "Seyircinin gözü senin gözündeyken ellerin hareket etmesi prensibi; gerilim ve rahatlama anları.",
                            practiceTask = "Seyirciye soru sorup gözlerinin içine baktığın anda deste üzerinde gizli hareketi yap."
                        ),
                        TopicCheckItem(
                            id = "crd_patter_storytelling",
                            title = "Patter (Sahne Konuşması) & Gizem Yaratma",
                            description = "Kart numarasını mekanik bir 'buldum' şovundan çıkarıp izleyiciyi büyüleyen bir hikayeye dönüştürmek.",
                            practiceTask = "Seçtiğin klasik bir numaraya kumarbazlar veya dedektif temalı 1 dakikalık akıcı bir hikaye yaz."
                        ),
                        TopicCheckItem(
                            id = "crd_reset_improv",
                            title = "Hata Kurtarma & Doğaçlama Becerisi",
                            description = "Bir kart düştüğünde veya numara ters gittiğinde paniklemeden durumu başka bir güçlü finale bağlama.",
                            practiceTask = "Planlanan hile başarısız olursa kullanabileceğin yedek bir 'kart bulma' manevrası belirle."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // KİŞİSEL GELİŞİM 4. ANİME & MANHWA TAKİBİ
        // ==========================================
        "sub_anime_manhwa" to SubItemRoadmap(
            subItemId = "sub_anime_manhwa",
            title = "Anime & Manhwa Takibi",
            subtitle = "Kült Yapımlar, Popüler Seriler & Başyapıtlar",
            emoji = "⚔️",
            targetLevel = "Hedef: Başyapıt Eserleri Tamamlama & Düzenli Takip",
            overview = "Hikaye anlatımı, karakter gelişimi, sanatsal çizim kalitesi ve kurgusuyla öne çıkan anime ve manhwa kültürünü sistemli takip etmek.",
            sections = listOf(
                TopicSection(
                    title = "BAŞYAPIT & KÜLT ANİME SERİLERİ",
                    emoji = "🍿",
                    items = listOf(
                        TopicCheckItem(
                            id = "ani_aot",
                            title = "Attack on Titan (Shingeki no Kyojin)",
                            description = "Muazzam kurgu, öngörülemez ters köşeler, felsefi özgürlük teması ve politik savaş gerilimi.",
                            practiceTask = "Eren Yeager'ın karakter evrimini ve serinin determinizm/özgürlük felsefesini analiz et."
                        ),
                        TopicCheckItem(
                            id = "ani_vinland",
                            title = "Vinland Saga (Season 1 & 2)",
                            description = "Viking çağından insan olmanın anlamına, intikam hırsından barış ve gerçek savaşçı olmanın erdemine uzanan devasa dönüşüm.",
                            practiceTask = "Thorfinn'in 'Benim hiç düşmanım yok' felsefesinin arka planını değerlendir."
                        ),
                        TopicCheckItem(
                            id = "ani_hxh",
                            title = "Hunter x Hunter (2011)",
                            description = "Yorknew Şehri ve Chimera Ant arklarıyla shounen türünün kurallarını baştan yazan stratejik dövüş ve derinlik zirvesi.",
                            practiceTask = "Meruem ve Komugi ilişkisindeki insanlık ve canavarlık kavramlarının yer değişimini gözlemle."
                        ),
                        TopicCheckItem(
                            id = "ani_frieren",
                            title = "Sousou no Frieren (Beyond Journey's End)",
                            description = "Zaman algısı, fanilik, hatıralar ve bir elfin insanları anlama yolculuğu üzerine dingin bir başyapıt.",
                            practiceTask = "Frieren'in zaman algısı ile insanların kısa ömrü arasındaki melankoliyi hisset."
                        ),
                        TopicCheckItem(
                            id = "ani_monster_deathnote",
                            title = "Monster & Death Note (Psikolojik Gerilim)",
                            description = "Johan Liebert'in nihilizmi, Dr. Tenma'nın ahlaki mücadelesi ve L vs Light zeka savaşları.",
                            practiceTask = "Kötülüğün doğası ve adaletin göreceliği üzerine kurgulanmış zihinsel çatışmaları takip et."
                        )
                    )
                ),
                TopicSection(
                    title = "GÜNCEL & POPÜLER AKSİYON ANİMELERİ",
                    emoji = "🔥",
                    items = listOf(
                        TopicCheckItem(
                            id = "ani_jjk",
                            title = "Jujutsu Kaisen (Shibuya Incident & Movie)",
                            description = "MAPPA'nın üst düzey koreografisi, akıcı aksiyon sahneleri ve karanlık lanetler evreni.",
                            practiceTask = "Gojo Satoru mühürlenmesi ve Shibuya arkındaki çoklu karakter bakış açılarını izle."
                        ),
                        TopicCheckItem(
                            id = "ani_bleach_tybw",
                            title = "Bleach: Thousand-Year Blood War",
                            description = "Quincy istilası, efsanevi Bankai açılışları ve Tite Kubo'nun modern sinematik animasyon şöleni.",
                            practiceTask = "Gotei 13 kaptanlarının yeni Bankai açılışlarını ve Yamamoto vs Yhwach savaşını izle."
                        ),
                        TopicCheckItem(
                            id = "ani_cyberpunk",
                            title = "Cyberpunk: Edgerunners",
                            description = "Studio Trigger'ın Night City'de geçen 10 bölümlük görsel, müzikal ve duygusal patlaması.",
                            practiceTask = "David Martinez'in siberpsikoz sınırındaki yükseliş ve trajik düşüş temposunu incele."
                        ),
                        TopicCheckItem(
                            id = "ani_chainsaw_man",
                            title = "Chainsaw Man",
                            description = "Fujimoto'nun sinematik yönetmenlik anlayışı, çiğ gerçeklik, kaotik dövüşler ve karanlık mizah.",
                            practiceTask = "Denji'nin basit arzuları ile şeytan avcılarının varoluşsal tehlikeleri arasındaki zıtlığı gözlemle."
                        )
                    )
                ),
                TopicSection(
                    title = "KÜLT & BAŞYAPIT MANHWA SERİLERİ",
                    emoji = "📖",
                    items = listOf(
                        TopicCheckItem(
                            id = "man_solo_leveling",
                            title = "Solo Leveling (I Alone Level Up) - Tamamlandı",
                            description = "Manhwa sektörünü küresel bir fenomene dönüştüren Redice Studio çizimleri ve Sung Jin-woo'nun Gölge Hükümdarı oluşu.",
                            practiceTask = "Zayıf E-seviye bir avcının mutlak monark seviyesine yükselişindeki panelleme dinamiklerini incele."
                        ),
                        TopicCheckItem(
                            id = "man_orv",
                            title = "Omniscient Reader's Viewpoint (ORV)",
                            description = "Yıkılan dünyayı romanın tek okuyucusu Kim Dokja olarak hayatta tutma mücadelesi; olağanüstü senaryo ve meta-kurgu.",
                            practiceTask = "Okuyucu ile hikaye kahramanı (Yoo Joonghyuk) arasındaki kader bağını takip et."
                        ),
                        TopicCheckItem(
                            id = "man_tbate",
                            title = "The Beginning After the End (TBATE)",
                            description = "Kral Grey'in büyü dünyasında Arthur Leywin olarak reenkarne oluşu, Dicathen-Alacrya savaşı ve asura güçleri.",
                            practiceTask = "Büyü çekirdeği geliştirme aşamalarını ve Arthur'un savaş zekasını oku."
                        ),
                        TopicCheckItem(
                            id = "man_tower_of_god",
                            title = "Tower of God (Sin-ui Tap)",
                            description = "SIU'nun devasa kurgusu, kule testleri, kuralsızlar (irregulars), Zahard ve 10 büyük aile entrikaları.",
                            practiceTask = "Kulenin katlarındaki benzersiz kuralları ve Baam'ın güç uyanışlarını takip et."
                        ),
                        TopicCheckItem(
                            id = "man_lookism_ptj",
                            title = "Lookism & Viral Hit (PTJ Evreni)",
                            description = "Kore sokak dövüşleri, 4 büyük çete, görünüş ayrımcılığı ve dövüş sanatı tekniklerinin gerçekçi aktarımı.",
                            practiceTask = "Farklı dövüş disiplinlerinin (boks, muay thai, jiu-jitsu) çizimlerdeki koreografisini gözlemle."
                        )
                    )
                ),
                TopicSection(
                    title = "MURIM & DÖVÜŞ SANATLARI MANHWALARI",
                    emoji = "🥋",
                    items = listOf(
                        TopicCheckItem(
                            id = "man_legend_northern",
                            title = "Legend of the Northern Blade",
                            description = "Murim dünyasının en stilistik, siyah-beyaz mürekkep etkili akıcı kılıç koreografileri ve Jin Mu-won'un intikamı.",
                            practiceTask = "Dövüş panellerindeki hareket hissini ve sessiz kılıç sanatının görselliğini incele."
                        ),
                        TopicCheckItem(
                            id = "man_mount_hua",
                            title = "Return of the Blossoming Blade (Mount Hua)",
                            description = "Kılıç Azizi Chung Myung'un 100 yıl sonra yeniden doğup yıkılmış Mount Hua tarikatını mizah ve güçle şahlandırması.",
                            practiceTask = "Erik çiçeği kılıç tekniğinin estetik panellerini ve Chung Myung'un liderlik tarzını oku."
                        ),
                        TopicCheckItem(
                            id = "man_nano_machine",
                            title = "Nano Machine",
                            description = "Geleneksel Murim dövüş sanatlarıyla geleceğin yapay zeka nano teknolojisini birleştiren acımasız güç tırmanışı.",
                            practiceTask = "Nano makinenin dövüş tekniklerini analiz edip vücuda yükleme mekaniğini takip et."
                        ),
                        TopicCheckItem(
                            id = "man_murim_login",
                            title = "Murim Login & SSS-Class Suicide Hunter",
                            description = "Modern avcı dünyası ile geleneksel Murim arasında geçiş yapan özgün konseptler ve derin karakter arkları.",
                            practiceTask = "Oyunlaştırma (gamification) ile kadim qi sanatlarının sentezini keyifle oku."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // 14. İNGİLİZCE (B2+ AKTİF)
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
            subtitle = "Kelime Uygulaması Eşliğinde Stressiz Hobi",
            emoji = "🇪🇸",
            targetLevel = "Hedef: Kendi Kelime Uygulaması ile Stressiz Hobi (A1-A2)",
            overview = "Kendi geliştirdiğin kelime uygulamasına İspanyolca destesi ekleyerek; acele etmeden, tamamen hobi ve genel kültür odaklı temel kelime ve kalıp dağarcığı oluşturmak.",
            sections = listOf(
                TopicSection(
                    title = "İSPANYOLCA TEMELLERİ & KELİME DAĞARCIĞI",
                    emoji = "🇪🇸",
                    items = listOf(
                        TopicCheckItem(
                            id = "es_alphabet",
                            title = "İspanyolca Alfabe & Fonetik Okuma",
                            description = "Harf sesleri, çift 'll', 'ñ', 'rr' yuvarlama ve doğal telaffuz kuralları.",
                            practiceTask = "İspanyolca temel sesletim kurallarını ve kelime vurgularını dinleyerek sesli tekrar et."
                        ),
                        TopicCheckItem(
                            id = "es_greetings",
                            title = "Günlük Selamlaşma & Temel İfadeler",
                            description = "Hola, buenos días, gracias, ¿cómo estás?, por favor gibi temel nezaket ve tanışma kalıpları.",
                            practiceTask = "Temel tanışma diyaloğunu kendi kelime uygulamana ekle ve telaffuz pratiği yap."
                        ),
                        TopicCheckItem(
                            id = "es_vocab_app",
                            title = "Kelime Uygulamasında Temel 500 Kelime",
                            description = "Kendi kelime uygulamana en sık kullanılan 500 İspanyolca kelimeyi (sayılar, günler, temel fiiller, sıfatlar) yükleyip aralıklı tekrar (spaced repetition) yapmak.",
                            practiceTask = "Günde 5 yeni İspanyolca kelimeyi uygulamanda tekrar et."
                        ),
                        TopicCheckItem(
                            id = "es_grammar_intro",
                            title = "Temel Gramer (Ser vs Estar & Şimdiki Zaman)",
                            description = "Kalıcı durumlar (Ser) ile geçici durumlar (Estar) ayrımı ve basit fiil çekimleri.",
                            practiceTask = "Kendini, nereden olduğunu ve şu an nasıl hissettiğini Ser ve Estar kullanarak 3 cümleyle ifade et."
                        )
                    )
                ),
                TopicSection(
                    title = "KÜLTÜR, MÜZİK & DİNLEME PRATİĞİ",
                    emoji = "🎸",
                    items = listOf(
                        TopicCheckItem(
                            id = "es_music_lyrics",
                            title = "İspanyolca Şarkı Sözü İncelemeleri",
                            description = "Akılda kalıcı şarkıların sözlerini çevirerek günlük deyimler ve argo kalıplar kazanma.",
                            practiceTask = "Beğendiğin bir İspanyolca şarkının sözlerini Türkçe ve İngilizce karşılıklarıyla analiz et."
                        ),
                        TopicCheckItem(
                            id = "es_mini_stories",
                            title = "Kısa Kolay Hikayeler (Short Stories in Spanish)",
                            description = "A1-A2 seviyesi derecelendirilmiş hikayelerle bağlam içinde kelime pekiştirme.",
                            practiceTask = "1 sayfalık kısa bir hikayeyi sesli oku ve bilmediğin 3 kelimeyi uygulamana kaydet."
                        )
                    )
                )
            )
        ),

        // ==========================================
        // 12. PORTFÖY ÇIKTILARI (GITHUB, PLAY STORE, MEDIUM)
        // ==========================================
        "sub_github" to SubItemRoadmap(
            subItemId = "sub_github",
            title = "GitHub",
            subtitle = "Depoları Doldurma, Vitrin & Açık Kaynak",
            emoji = "🐙",
            targetLevel = "Hedef: Yemyeşil Katkı Takvimi & Nitelikli Depo Mimarisi",
            overview = "300 tane rastgele boş commit yerine; mimari şemalı, kapsamlı README'li, test edilmiş ve gerçekten çalışan 15-20 tane mühendislik deposu oluşturmak ve açık kaynak katkısı sağlamak.",
            sections = listOf(
                TopicSection(
                    title = "PROFİL VİTRİNİ & REPO HİJYENİ",
                    emoji = "🟩",
                    items = listOf(
                        TopicCheckItem("gh_profile_readme", "Özel GitHub Profil README'si", "Teknoloji rozetleri, GitHub streak stats kartı, hedefler ve sosyal linkler"),
                        TopicCheckItem("gh_pinned_repos", "En Güçlü 6 Depoyu Sabitleme (Pinned Repos)", "Backend, Android, CS Algoritmaları ve Otomasyon projelerini öne çıkarma"),
                        TopicCheckItem("gh_pro_readme", "Profesyonel README Standardı & Mimari Şema", "Mermaid / Excalidraw akış diyagramları, ekran görüntüleri, API endpoint tablosu ve cURL örnekleri"),
                        TopicCheckItem("gh_repo_hygiene", "Repo Hijyeni & Standartlar", "Kusursuz .gitignore temizliği, lisans seçimi (MIT), issue template ve PR şablonları")
                    )
                ),
                TopicSection(
                    title = "DEPOLARI DOLDURMA STRATEJİSİ",
                    emoji = "📁",
                    items = listOf(
                        TopicCheckItem("gh_repo_backend_clean", "Kurumsal .NET Clean Architecture API Deposu", "Domain, Application, Infrastructure, Persistence katmanları ve CQRS"),
                        TopicCheckItem("gh_repo_android_compose", "Modern Native Android Jetpack Compose Depoları", "Winter Arc Skill Tree, kelime takipçisi ve modern UI araçları"),
                        TopicCheckItem("gh_repo_cs_algorithms", "CS, Veri Yapıları & Algoritmalar Deposu", "C# / C++ / Python ile çözülmüş veri yapıları, leetcode ve karmaşıklık analizleri"),
                        TopicCheckItem("gh_repo_cli_automation", "Linux CLI & Bash Otomasyon Scriptleri Deposu", "Developer üretkenliğini artıran pratik shell ve python scriptleri"),
                        TopicCheckItem("gh_repo_thesis_ai", "Bitirme Tezi & LLM Bellek Optimizasyon Deposu", "KV Cache, vAttention veya sistem düzeyinde C++ optimizasyon kodu")
                    )
                ),
                TopicSection(
                    title = "DİSİPLİN, CI/CD & KATKILAR",
                    emoji = "⚡",
                    items = listOf(
                        TopicCheckItem("gh_commit_streak", "Düzenli Yeşil Takvim & Conventional Commits", "feat:, fix:, refactor:, docs: standartlarıyla yazılım geliştirme disiplini"),
                        TopicCheckItem("gh_actions_cicd", "GitHub Actions CI/CD Pipeline Entegrasyonu", "Her push ve PR'da otomatik build, lint ve unit test çalıştıran workflow"),
                        TopicCheckItem("gh_open_source_pr", "Açık Kaynak Projelere Katkı (Open Source PR)", "Popüler bir kütüphaneye dokümantasyon, typo veya bugfix pull request'i gönderme")
                    )
                )
            )
        ),
        "sub_play_store" to SubItemRoadmap(
            subItemId = "sub_play_store",
            title = "Play Store",
            subtitle = "Canlı Native Mobil Uygulamalar",
            emoji = "📱",
            targetLevel = "Hedef: Canlıda Çalışan 2-3 Native Android Uygulaması",
            overview = "Sadece yerel cihazda çalışan kod yerine; Google Play Console yayın sürecini, test aşamalarını, mağaza optimizasyonunu ve sürüm güncellemelerini uçtan uca deneyimlemek.",
            sections = listOf(
                TopicSection(
                    title = "GOOGLE PLAY CONSOLE & YAYIN HAZIRLIĞI",
                    emoji = "🛠️",
                    items = listOf(
                        TopicCheckItem("ps_console_account", "Google Play Developer Hesabı & Kimlik Doğrulama", "Geliştirici hesabı açılışı, D-U-N-S / kimlik onay süreçleri"),
                        TopicCheckItem("ps_signing_keystore", "Production Keystore & Güvenli İmzalama", "Upload keystore oluşturma, gradle.properties ile key şifreleme ve alias yönetimi"),
                        TopicCheckItem("ps_aab_bundle", "Android App Bundle (.aab) & R8 ProGuard Optimizasyonu", "Release build konfigürasyonu, kod küçültme ve obfuscation testleri"),
                        TopicCheckItem("ps_store_assets", "Mağaza Varlıkları & Grafik Tasarımları", "512x512 uygulama ikonu, 1024x500 Feature Graphic ve cihaz mockup ekran görüntüleri"),
                        TopicCheckItem("ps_privacy_policy", "Gizlilik Politikası (Privacy Policy) & Veri Güvenliği", "GitHub Pages üzerinde gizlilik politikası barındırma ve Play Console Data Safety formu")
                    )
                ),
                TopicSection(
                    title = "CANLIYA ALINACAK UYGULAMALAR",
                    emoji = "🚀",
                    items = listOf(
                        TopicCheckItem("ps_app_winterarc", "Winter Arc Skill Tree Uygulaması", "Öğrencilere & yazılımcılara yönelik native Compose yol haritası ve alışkanlık takipçisi"),
                        TopicCheckItem("ps_app_vocab", "Oxford 5000 / Kelime Ezberleme & Quiz Uygulaması", "Kişisel İngilizce/İspanyolca kelime aracı ve aralıklı tekrar algoritması"),
                        TopicCheckItem("ps_app_utility", "Günlük Hayatı Kolaylaştıran Niş Mini Araç", "Dopamin takipçisi, odaklanma sayacı veya hobi amaçlı hafif bir araç")
                    )
                ),
                TopicSection(
                    title = "TEST, YAYIN & SÜRÜM YÖNETİMİ",
                    emoji = "📈",
                    items = listOf(
                        TopicCheckItem("ps_closed_testing", "20 Test Kullanıcısı ile 14 Günlük Kapalı Test", "Google'ın yeni geliştirici şartı olan kapalı test döngüsünü tamamlama"),
                        TopicCheckItem("ps_production_release", "İlk Canlı Sürümü Yayınlama (Production Release)", "İnceleme (Review) sürecini geçerek uygulamayı dünya çapında mağazaya sunma"),
                        TopicCheckItem("ps_post_launch_update", "Sürüm Güncellemesi & Yama Çıkma", "Version code / name artırımı ve kullanıcı geri bildirimleriyle hızlı güncelleme"),
                        TopicCheckItem("ps_crash_monitoring", "Android Vitals & Crash Analizi", "ANR ve çökme oranlarını %0 seviyesinde tutma, kullanıcı puanlarını yönetme")
                    )
                )
            )
        ),
        "sub_medium" to SubItemRoadmap(
            subItemId = "sub_medium",
            title = "Medium",
            subtitle = "Öğrendiklerimi Unutmama & Teknik Hafıza",
            emoji = "✍️",
            targetLevel = "Hedef: Kişisel Teknik Hafıza & Dijital Kütüphane",
            overview = "Amaç takipçi kasmak veya influencer olmak değil; öğrenilen ufak tefek her teknik detayı, mimari kararı, hata çözümünü ve Linux/Android püf noktasını yazıya dökerek kalıcı kılmak.",
            sections = listOf(
                TopicSection(
                    title = "TEKNİK YAZARLIK & FEYNMAN STRATEJİSİ",
                    emoji = "🧠",
                    items = listOf(
                        TopicCheckItem("med_mindset", "Kendime Notlar & Feynman Metodu", "Öğrendiğin konuyu en sade haliyle anlatarak kafada tam netleştirme"),
                        TopicCheckItem("med_format_template", "Yazı İskeleti Standartı", "Problem Tanımı -> Karşılaşılan Zorluklar -> Kod Çözümü -> Alınan Dersler"),
                        TopicCheckItem("med_gist_diagram", "Kod Parçacıkları (GitHub Gist) & Mimari Şemalar", "Görsel ve kod bloklarıyla okuması keyifli temiz içerik üretimi")
                    )
                ),
                TopicSection(
                    title = ".NET, BACKEND & VERİTABANI YAZILARI",
                    emoji = "⚙️",
                    items = listOf(
                        TopicCheckItem("med_exc_handling", "ASP.NET Core'da Global Exception Handling & ProblemDetails", "IExceptionHandler ile temiz hata yönetimi ve RFC 7807 uyumluluğu"),
                        TopicCheckItem("med_jwt_auth", "JWT ile Güvenli Authentication ve Refresh Token Döngüsü", "Token süresi dolunca sessiz yenileme mekanizması ve güvenlik ipuçları"),
                        TopicCheckItem("med_clean_arch", "Neden Clean Architecture? Domain Katmanını Bağımsız Tutmak", "Katmanlar arası sınırların korunması ve bağımlılıkların yönetimi"),
                        TopicCheckItem("med_ef_tracking", "EF Core Tracking Mantığı: AsNoTracking Neden Hayat Kurtarır?", "Read-only sorgularda bellek optimizasyonu ve performans karşılaştırması"),
                        TopicCheckItem("med_rabbitmq_event", "RabbitMQ ile Asenkron İletişim: Producer & Consumer", "Mesaj kuyrukları, event-driven yapı ve dead-letter queue mantığı"),
                        TopicCheckItem("med_sql_indexing", "Veritabanlarında B-Tree İndeksleme Mantığı ve Sorgu Hızı", "Index scan vs index seek farkı ve execution plan okuma")
                    )
                ),
                TopicSection(
                    title = "ANDROID, LINUX & ÖZEL MİMARİ TRİKLERİ",
                    emoji = "🐧",
                    items = listOf(
                        TopicCheckItem("med_compose_state", "Jetpack Compose'da Recomposition Mantığı & State Hoisting", "Gereksiz recomposition'ları önleme ve derivedStateOf püf noktaları"),
                        TopicCheckItem("med_linux_bash_tips", "Hayat Kurtaran Linux CLI & Bash Kısayolları", "Terminalde hızlanmak için pratik komutlar, alias'lar ve piping trikleri"),
                        TopicCheckItem("med_git_rescue", "Git'te Hata Yapınca Ne Yapılır? Reflog & Kurtarma Rehberi", "Yanlış commit veya rebase sonrası kodu kurtarma adımları"),
                        TopicCheckItem("med_thesis_kvcache", "LLM Çıkarımlarında KV Cache ve Bellek Optimizasyonunun Mantığı", "Bitirme tezinden çıkan sistem seviyesinde teorik özet"),
                        TopicCheckItem("med_debug_stories", "Haftanın Bug'ı: İlginç Bir Hatayı Çözme Günlüğü", "Geliştirme esnasında saatler alan bir bug'ın analizi ve çözümü")
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
