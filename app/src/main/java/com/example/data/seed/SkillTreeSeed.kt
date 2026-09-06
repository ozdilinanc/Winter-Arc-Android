package com.example.data.seed

import com.example.data.model.*

object SkillTreeSeed {

    val referenceBooks = listOf(
        ReferenceBook(
            title = "Operating Systems: Three Easy Pieces (OSTEP)",
            domain = "Operating Systems",
            whyItMatters = "Gold standard for virtualization, concurrency, and persistence.",
            keyTopics = listOf("Virtualization", "Processes & CPU Scheduling", "Paging & TLBs", "Locks & Semaphores", "File Systems & Crash Consistency")
        ),
        ReferenceBook(
            title = "Computer Systems: A Programmer's Perspective (CS:APP)",
            domain = "Computer Systems & Hardware",
            whyItMatters = "Bridges high-level software with machine execution, memory hierarchies, and system linking.",
            keyTopics = listOf("Machine-Level Representation of Programs", "Processor Architecture", "Memory Hierarchy & Caching", "Virtual Memory", "Dynamic Memory Allocation")
        ),
        ReferenceBook(
            title = "Computer Networks: A Top-Down Approach",
            domain = "Computer Networks",
            whyItMatters = "Comprehensive understanding of network layers from application protocols to packets.",
            keyTopics = listOf("Socket Programming", "TCP Flow & Congestion Control", "IP Routing Algorithms", "DNS & TLS Handshakes", "Reliable Data Transfer")
        ),
        ReferenceBook(
            title = "Designing Data-Intensive Applications (DDIA)",
            domain = "Data Systems & Distributed Storage",
            whyItMatters = "The definitive modern classic for data models, indexing, transactions, and distributed consensus.",
            keyTopics = listOf("Storage Engines (LSM-tree vs B-tree)", "Transactions & Isolation Levels", "Replication & Consensus", "Partitioning & Sharding", "Batch & Stream Processing")
        )
    )

    val knowledgeLoopSteps = listOf(
        KnowledgeLoopStage(
            stepNumber = 1,
            name = "LEARN",
            description = "Understand the core principles, read official documentation, books, or technical RFCs.",
            actionHint = "Focus on the 'why' and runtime mechanics before touching syntax."
        ),
        KnowledgeLoopStage(
            stepNumber = 2,
            name = "APPLY",
            description = "Implement small isolated proofs-of-concept, test scripts, or minimal benchmarks.",
            actionHint = "Write test suites to break the implementation and observe failure modes."
        ),
        KnowledgeLoopStage(
            stepNumber = 3,
            name = "WRITE NOTES",
            description = "Document personal mental models, gotchas, architecture diagrams, and edge cases.",
            actionHint = "Record personal insights, not copied reference text."
        ),
        KnowledgeLoopStage(
            stepNumber = 4,
            name = "BUILD",
            description = "Incorporate the skill into an end-to-end production-quality project with clean architecture.",
            actionHint = "Ensure proper error handling, logging, and dockerization."
        ),
        KnowledgeLoopStage(
            stepNumber = 5,
            name = "WRITE MEDIUM ARTICLE",
            description = "Write a clear, in-depth technical post explaining how and why you solved the engineering challenge.",
            actionHint = "Purpose: Technical memory and portfolio evidence, NOT becoming an influencer."
        ),
        KnowledgeLoopStage(
            stepNumber = 6,
            name = "REVIEW LATER",
            description = "Revisit code, refactor based on newer senior knowledge, and assess long-term retention.",
            actionHint = "Spaced repetition ensures lasting mastery beyond semester deadlines."
        )
    )

    val initialProjects = listOf(
        EngineeringProject(
            id = "proj_pharmacy",
            title = "Pharmacy Management & Dispensing System",
            description = "End-to-end full-stack backend system managing medication inventories, prescriptions, and order fulfillment.",
            category = "Backend / .NET",
            currentStage = ProjectWorkflowStage.IDEA,
            githubRepo = "github.com/student/pharmacy-backend-net",
            mediumArticleUrl = "",
            notes = "Targeting Clean Architecture, PostgreSQL with EF Core, and JWT authentication.",
            tags = listOf("ASP.NET Core", "Clean Architecture", "PostgreSQL", "EF Core")
        ),
        EngineeringProject(
            id = "proj_kv_cache",
            title = "Graduation Project: LLM KV Cache Optimization",
            description = "High-performance C++ research system optimizing transformer KV cache paging and v-attention memory fragmentation.",
            category = "Systems / Graduation",
            currentStage = ProjectWorkflowStage.IDEA,
            githubRepo = "github.com/student/kv-cache-v-attention-opt",
            mediumArticleUrl = "",
            notes = "Benchmarking non-contiguous memory allocation on Linux with Ollama/Llama kernels.",
            tags = listOf("C++", "KV Cache", "Paging", "Performance", "Linux")
        ),
        EngineeringProject(
            id = "proj_android_mobile",
            title = "Personal Engineering Roadmap & Skill Tree",
            description = "Native Android Jetpack Compose interactive knowledge map and engineering tracker for student progression.",
            category = "Android",
            currentStage = ProjectWorkflowStage.IDEA,
            githubRepo = "github.com/student/skilltree-android-compose",
            mediumArticleUrl = "medium.com/@student/building-a-mind-map-in-jetpack-compose",
            notes = "Offline-first with Room, dynamic radial graph layout, M3 technical styling.",
            tags = listOf("Jetpack Compose", "Kotlin", "Room", "M3")
        ),
        EngineeringProject(
            id = "proj_distributed_messaging",
            title = "Event-Driven Order Processing Microservice",
            description = "RabbitMQ asynchronous message consumer with dead-letter exchange, retry policies, and Docker Compose.",
            category = "Distributed Systems",
            currentStage = ProjectWorkflowStage.IDEA,
            githubRepo = "github.com/student/event-order-microservice",
            mediumArticleUrl = "",
            notes = "Demonstrates asynchronous decoupling and fault-tolerant background processing.",
            tags = listOf("RabbitMQ", "Docker", "Microservices", "Hangfire")
        )
    )

    fun getInitialSkills(): List<SkillNode> = rawSkills.map {
        it.copy(status = SkillStatus.NOT_STARTED, personalNotes = "")
    }

    private val rawSkills: List<SkillNode> = listOf(
        // ==========================================
        // 1. BACKEND / .NET (HIGHEST PRIORITY)
        // ==========================================
        // C#
        SkillNode(
            id = "net_cs_fundamentals",
            name = "Fundamentals",
            branchId = BranchId.BACKEND_DOTNET,
            category = "C#",
            description = "Type system, value vs reference types, structs, records, control flow, string manipulation, and operators.",
            prerequisites = listOf("Programming 101"),
            recommendedResources = listOf("Microsoft C# Documentation", "C# in a Nutshell"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG,
            personalNotes = "💡 Key Takeaway: Value types live on stack; reference types on managed heap.\n📚 Resource: https://learn.microsoft.com/dotnet/csharp/fundamentals/"
        ),
        SkillNode(
            id = "net_cs_oop",
            name = "OOP",
            branchId = BranchId.BACKEND_DOTNET,
            category = "C#",
            description = "Inheritance, encapsulation, polymorphism, abstract classes, interfaces, and composition over inheritance.",
            prerequisites = listOf("C# Fundamentals"),
            recommendedResources = listOf("C# Design Patterns", "Pluralsight C# Deep Dive"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_cs_generics",
            name = "Generics",
            branchId = BranchId.BACKEND_DOTNET,
            category = "C#",
            description = "Generic classes, interfaces, methods, type constraints (where T : class, new()), and covariance/contravariance.",
            prerequisites = listOf("C# OOP"),
            recommendedResources = listOf("MSDN Generics Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_cs_linq",
            name = "LINQ",
            branchId = BranchId.BACKEND_DOTNET,
            category = "C#",
            description = "Language Integrated Query: Select, Where, GroupBy, Join, deferred execution, IEnumerable vs IQueryable.",
            prerequisites = listOf("C# Generics"),
            recommendedResources = listOf("LINQPad Tutorials", "Official .NET LINQ Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "net_cs_async_await",
            name = "Async / Await",
            branchId = BranchId.BACKEND_DOTNET,
            category = "C#",
            description = "Task-based asynchronous pattern (TAP), SynchronizationContext, Task.WhenAll, cancellation tokens, thread pool starvation.",
            prerequisites = listOf("C# Fundamentals"),
            recommendedResources = listOf("Stephen Cleary: Async in C#", "MSDN Async Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_cs_exceptions",
            name = "Exceptions",
            branchId = BranchId.BACKEND_DOTNET,
            category = "C#",
            description = "Try-catch-finally, custom exception types, exception filters (when), stack trace preservation (throw vs throw ex).",
            prerequisites = listOf("C# Fundamentals"),
            recommendedResources = listOf("Framework Design Guidelines"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_cs_memory_gc",
            name = "Memory & Garbage Collector",
            branchId = BranchId.BACKEND_DOTNET,
            category = "C#",
            description = "Stack vs Heap, Generation 0/1/2, Large Object Heap (LOH), IDisposable pattern, Span<T>, Memory<T>, pinning.",
            prerequisites = listOf("C# Fundamentals", "OS Memory"),
            recommendedResources = listOf("Pro .NET Memory Management (Konrad Kokosa)"),
            priorityTag = "Highest Priority",
            status = SkillStatus.IN_PROGRESS
        ),

        // ASP.NET Core
        SkillNode(
            id = "net_asp_http",
            name = "HTTP",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "HTTP request/response lifecycle, verbs (GET, POST, PUT, DELETE, PATCH), status codes, headers, cookies.",
            prerequisites = listOf("Computer Networks"),
            recommendedResources = listOf("MDN Web Docs HTTP Guide", "RFC 9110"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_asp_rest",
            name = "REST",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "RESTful resource design, idempotency, HATEOAS, content negotiation, API versioning.",
            prerequisites = listOf("HTTP"),
            recommendedResources = listOf("RESTful Web APIs by Leonard Richardson"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_asp_controllers",
            name = "Controllers",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "ControllerBase, ActionResults, model binding from body/route/query, Minimal APIs vs Controller-based.",
            prerequisites = listOf("REST"),
            recommendedResources = listOf("Microsoft Learn ASP.NET Core"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED,
            personalNotes = "💡 Key Takeaway: Prefer ControllerBase over Controller for Web APIs to avoid Razor view engine overhead.\n⚠️ Gotcha: Async actions must return Task<IActionResult>."
        ),
        SkillNode(
            id = "net_asp_routing",
            name = "Routing",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "Attribute routing, route constraints, endpoint routing mechanism, URL generation.",
            prerequisites = listOf("Controllers"),
            recommendedResources = listOf("ASP.NET Core Routing In-Depth"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_asp_di",
            name = "Dependency Injection",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "Service lifetimes (Transient, Scoped, Singleton), captive dependencies, service provider scopes, Scrutor.",
            prerequisites = listOf("C# OOP"),
            recommendedResources = listOf("Dependency Injection in .NET by Mark Seemann"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "net_asp_middleware",
            name = "Middleware",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "Request pipeline pipeline, RequestDelegate, custom middleware creation, pipeline ordering and short-circuiting.",
            prerequisites = listOf("HTTP", "DI"),
            recommendedResources = listOf("Microsoft Docs: Middleware in ASP.NET Core"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_asp_validation",
            name = "Validation",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "FluentValidation, DataAnnotations, automatic ModelState validation, custom validators.",
            prerequisites = listOf("Controllers"),
            recommendedResources = listOf("FluentValidation Documentation"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_asp_serialization",
            name = "Serialization",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "System.Text.Json vs Newtonsoft.Json, JsonConverter, polymorphic serialization, case naming policies.",
            prerequisites = listOf("C# Fundamentals"),
            recommendedResources = listOf("System.Text.Json Deep Dive"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_asp_swagger",
            name = "Swagger / OpenAPI",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "Swashbuckle / NSwag, OpenAPI 3.0 specification, operation filters, XML documentation comments, JWT authorization in UI.",
            prerequisites = listOf("REST"),
            recommendedResources = listOf("OpenAPI Specification"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_asp_configuration",
            name = "Configuration",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "IConfiguration, appsettings.json, environment variables, Options pattern (IOptions, IOptionsSnapshot).",
            prerequisites = listOf("C# Fundamentals"),
            recommendedResources = listOf("Microsoft Configuration Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_asp_logging",
            name = "Logging",
            branchId = BranchId.BACKEND_DOTNET,
            category = "ASP.NET Core",
            description = "ILogger<T>, structured logging, Serilog, sinks (Console, File, Seq), log levels, correlation IDs.",
            prerequisites = listOf("Configuration"),
            recommendedResources = listOf("Serilog Official Wiki"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),

        // Database
        SkillNode(
            id = "net_db_postgresql",
            name = "PostgreSQL",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "PostgreSQL architecture, schemas, data types (JSONB, UUID, timestamps), psql CLI, pgAdmin.",
            prerequisites = listOf("Database Fundamentals"),
            recommendedResources = listOf("The Art of PostgreSQL", "PostgreSQL Official Docs"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_db_sql",
            name = "SQL",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "DML & DDL, SELECT, JOINs (INNER, LEFT, RIGHT, FULL), Subqueries, CTEs, Window Functions (ROW_NUMBER, RANK).",
            prerequisites = listOf("Relational Concepts"),
            recommendedResources = listOf("SQL Antipatterns Book"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "net_db_relations",
            name = "Relations",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "One-to-One, One-to-Many, Many-to-Many junction tables, Foreign Keys, Cascade deletes vs Restrict.",
            prerequisites = listOf("SQL"),
            recommendedResources = listOf("Database Design Manual"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_db_normalization",
            name = "Normalization",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "1NF, 2NF, 3NF, BCNF, denormalization trade-offs for read-heavy workloads.",
            prerequisites = listOf("Relations"),
            recommendedResources = listOf("Database System Concepts (Silberschatz)"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_db_indexes",
            name = "Indexes",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "B-Tree indexes, Hash indexes, Composite indexes, Clustered vs Non-Clustered, Partial indexes, Index selectivity.",
            prerequisites = listOf("SQL"),
            recommendedResources = listOf("Use The Index, Luke!"),
            priorityTag = "Highest Priority",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "net_db_query_opt",
            name = "Query Optimization",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "EXPLAIN / EXPLAIN ANALYZE, sequential scans vs index scans, query plan cost analysis, preventing N+1 problems.",
            prerequisites = listOf("Indexes"),
            recommendedResources = listOf("PostgreSQL Query Optimization Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "net_db_transactions",
            name = "Transactions",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "ACID properties (Atomicity, Consistency, Isolation, Durability), COMMIT, ROLLBACK, Savepoints.",
            prerequisites = listOf("SQL"),
            recommendedResources = listOf("DDIA Chapter 7: Transactions"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_db_isolation",
            name = "Isolation",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "Read Uncommitted, Read Committed, Repeatable Read, Serializable; Dirty reads, Non-repeatable reads, Phantom reads.",
            prerequisites = listOf("Transactions"),
            recommendedResources = listOf("PostgreSQL Transaction Isolation Documentation"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_db_concurrency",
            name = "Concurrency",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "Optimistic locking (RowVersion / timestamp), Pessimistic locking (SELECT FOR UPDATE), Deadlock prevention.",
            prerequisites = listOf("Isolation"),
            recommendedResources = listOf("Enterprise Integration Patterns"),
            priorityTag = "Highest Priority",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "net_db_efcore",
            name = "EF Core",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "DbContext, DbSet, Fluent API configuration, Entity relationships, Value Converters, Shadow properties.",
            prerequisites = listOf("C# OOP", "SQL"),
            recommendedResources = listOf("Entity Framework Core in Action"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "net_db_migrations",
            name = "Migrations",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "dotnet ef migrations add/update, migration scripts, idempotent deployment scripts, rollbacks.",
            prerequisites = listOf("EF Core"),
            recommendedResources = listOf("Microsoft EF Core Migrations Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_db_tracking",
            name = "Tracking",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "ChangeTracker, EntityState, AsNoTracking() for high performance reads, attach/detach entities.",
            prerequisites = listOf("EF Core"),
            recommendedResources = listOf("EF Core Performance Practices"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_db_linq_to_sql",
            name = "LINQ to SQL",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Database",
            description = "Expression trees, query translation to SQL, client evaluation vs server evaluation, raw SQL queries (FromSqlRaw).",
            prerequisites = listOf("LINQ", "EF Core"),
            recommendedResources = listOf("Under the Hood of EF Core Expression Trees"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),

        // Architecture
        SkillNode(
            id = "net_arch_solid",
            name = "SOLID",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion.",
            prerequisites = listOf("C# OOP"),
            recommendedResources = listOf("Clean Architecture by Robert C. Martin"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "net_arch_layered",
            name = "Layered Architecture",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Presentation, Business Logic (BLL), Data Access Layer (DAL); separation of concerns and unidirectional data flow.",
            prerequisites = listOf("SOLID"),
            recommendedResources = listOf("Software Architecture Patterns by Mark Richards"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_arch_clean",
            name = "Clean Architecture",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Domain, Application, Infrastructure, Presentation layers; Dependency Rule (inward pointing dependencies), MediatR/CQRS.",
            prerequisites = listOf("Layered Architecture"),
            recommendedResources = listOf("Milan Jovanović Clean Architecture Series", "Jason Taylor Clean Architecture Template"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_arch_onion",
            name = "Onion Architecture",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Domain model at the center, domain services, application services, outer infrastructure adapters.",
            prerequisites = listOf("Clean Architecture"),
            recommendedResources = listOf("Jeffrey Palermo: The Onion Architecture"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_arch_repository",
            name = "Repository Pattern",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Generic Repository, Unit of Work, encapsulating query logic, trade-offs with EF Core DbContext.",
            prerequisites = listOf("EF Core", "C# Generics"),
            recommendedResources = listOf("Martin Fowler: Repository Pattern"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "net_arch_service_layer",
            name = "Service Layer",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Orchestrating domain operations, transactional boundaries, mapping domain objects to DTOs.",
            prerequisites = listOf("Layered Architecture"),
            recommendedResources = listOf("Patterns of Enterprise Application Architecture"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_arch_dto",
            name = "DTO",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Data Transfer Objects, decoupling API contracts from database entities, Mapster, AutoMapper, Manual mapping.",
            prerequisites = listOf("Controllers"),
            recommendedResources = listOf("API Design Patterns"),
            priorityTag = "Highest Priority",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "net_arch_dep_inversion",
            name = "Dependency Inversion",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "High-level modules should not depend on low-level modules; both should depend on abstractions (interfaces).",
            prerequisites = listOf("SOLID"),
            recommendedResources = listOf("Clean Code by Uncle Bob"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_arch_design_patterns",
            name = "Design Patterns",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Factory, Strategy, Observer, Decorator, Adapter, Builder, Singleton in modern C#.",
            prerequisites = listOf("C# OOP"),
            recommendedResources = listOf("Refactoring Guru: Design Patterns", "GoF Design Patterns"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_arch_testability",
            name = "Testability",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Architecture",
            description = "Designing for tests: seam creation, decoupling side effects, avoiding static state, mocking interfaces.",
            prerequisites = listOf("SOLID"),
            recommendedResources = listOf("Working Effectively with Legacy Code"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),

        // Production Backend
        SkillNode(
            id = "net_prod_authn",
            name = "Authentication",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "Verifying identity, ClaimsPrincipal, Cookie authentication, Bearer authentication schemes.",
            prerequisites = listOf("ASP.NET Core Middleware"),
            recommendedResources = listOf("Microsoft Identity Documentation"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_prod_authz",
            name = "Authorization",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "Role-based access control (RBAC), Policy-based authorization, Requirements and AuthorizationHandlers.",
            prerequisites = listOf("Authentication"),
            recommendedResources = listOf("ASP.NET Core Policy-Based Auth Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_prod_jwt",
            name = "JWT",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "JSON Web Tokens: Header, Payload, Signature; Symmetric vs Asymmetric keys (RSA), Refresh token rotation.",
            prerequisites = listOf("Authentication"),
            recommendedResources = listOf("RFC 7519", "jwt.io Debugger"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_prod_password_hashing",
            name = "Password Hashing",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "Cryptographic salts, Argon2id, BCrypt, PBKDF2; timing attack resistance.",
            prerequisites = listOf("Security Fundamentals"),
            recommendedResources = listOf("OWASP Password Storage Cheat Sheet"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_prod_global_exceptions",
            name = "Global Exception Handling",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "IExceptionHandler (.NET 8+), UseExceptionHandler middleware, RFC 7807 ProblemDetails format.",
            prerequisites = listOf("ASP.NET Core Middleware"),
            recommendedResources = listOf(".NET 8 Exception Handling Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "net_prod_unit_testing",
            name = "Unit Testing",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "xUnit, NUnit, FluentAssertions, Moq / NSubstitute, AAA pattern (Arrange, Act, Assert).",
            prerequisites = listOf("Testability"),
            recommendedResources = listOf("Unit Testing Principles, Practices, and Patterns by Vladimir Khorikov"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_prod_integration_testing",
            name = "Integration Testing",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "WebApplicationFactory<Program>, Testcontainers with real PostgreSQL in Docker, Respawn database cleanup.",
            prerequisites = listOf("Unit Testing", "Docker"),
            recommendedResources = listOf("Testcontainers for .NET Documentation"),
            priorityTag = "Highest Priority",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "net_prod_background_jobs",
            name = "Background Jobs",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "IHostedService, BackgroundService, periodic timers, channel-based worker queues.",
            prerequisites = listOf("Async / Await"),
            recommendedResources = listOf("ASP.NET Core Background Tasks Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "net_prod_hangfire",
            name = "Hangfire",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "Persistent background processing: fire-and-forget, delayed jobs, recurring cron jobs, dashboard integration.",
            prerequisites = listOf("Background Jobs"),
            recommendedResources = listOf("Hangfire Official Documentation"),
            priorityTag = "Highest Priority",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "net_prod_caching",
            name = "Caching",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "IMemoryCache, IDistributedCache with Redis, Cache-Aside pattern, Cache invalidation strategies, OutputCache (.NET 8).",
            prerequisites = listOf("ASP.NET Core"),
            recommendedResources = listOf("Redis Developer Hub", "Microsoft Caching Guide"),
            priorityTag = "Highest Priority",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "net_prod_health_checks",
            name = "Health Checks",
            branchId = BranchId.BACKEND_DOTNET,
            category = "Production Backend",
            description = "ASP.NET Core HealthChecks for DB, Redis, RabbitMQ; liveness vs readiness probes for Kubernetes.",
            prerequisites = listOf("ASP.NET Core"),
            recommendedResources = listOf("AspNetCore.Diagnostics.HealthChecks GitHub"),
            priorityTag = "Highest Priority",
            status = SkillStatus.PRACTICED
        ),

        // ==========================================
        // 2. DISTRIBUTED SYSTEMS / DEVOPS
        // ==========================================
        // Containers
        SkillNode(
            id = "devops_docker",
            name = "Docker",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Containers",
            description = "Containerization concepts, namespaces, cgroups, images vs containers, Docker daemon.",
            prerequisites = listOf("Linux Fundamentals"),
            recommendedResources = listOf("Docker Deep Dive by Nigel Poulton"),
            priorityTag = "DevOps Core",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "devops_dockerfile",
            name = "Dockerfile",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Containers",
            description = "Multi-stage builds, layer caching, non-root user security, distroless images for .NET.",
            prerequisites = listOf("Docker"),
            recommendedResources = listOf("Best practices for writing Dockerfiles"),
            priorityTag = "DevOps Core",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "devops_docker_compose",
            name = "Docker Compose",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Containers",
            description = "Multi-container local environments (app + PostgreSQL + Redis + RabbitMQ), depends_on, healthchecks.",
            prerequisites = listOf("Docker"),
            recommendedResources = listOf("Docker Compose Specification"),
            priorityTag = "DevOps Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "devops_volumes",
            name = "Volumes",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Containers",
            description = "Named volumes, bind mounts, data persistence for databases, volume drivers.",
            prerequisites = listOf("Docker"),
            recommendedResources = listOf("Docker Storage Guide"),
            priorityTag = "DevOps Core",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "devops_networks",
            name = "Networks",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Containers",
            description = "Bridge networks, host networks, overlay networks, container DNS resolution.",
            prerequisites = listOf("Docker"),
            recommendedResources = listOf("Docker Networking Deep Dive"),
            priorityTag = "DevOps Core",
            status = SkillStatus.PRACTICED
        ),

        // Messaging
        SkillNode(
            id = "devops_mq_concepts",
            name = "Message Queue concepts",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Messaging",
            description = "Asynchronous decoupling, push vs pull, broker vs peer-to-peer, at-least-once vs exactly-once delivery.",
            prerequisites = listOf("Distributed Systems"),
            recommendedResources = listOf("Enterprise Integration Patterns"),
            priorityTag = "Messaging",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_rabbitmq",
            name = "RabbitMQ",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Messaging",
            description = "AMQP 0-9-1 protocol, RabbitMQ server setup, management UI, connection and channel pooling.",
            prerequisites = listOf("Message Queue concepts"),
            recommendedResources = listOf("RabbitMQ in Action"),
            priorityTag = "Messaging",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_producer_consumer",
            name = "Producer / Consumer",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Messaging",
            description = "Publishing messages, consuming with acknowledgement (ack/nack), prefetch count, competing consumers.",
            prerequisites = listOf("RabbitMQ"),
            recommendedResources = listOf("RabbitMQ Tutorials (C# / .NET)"),
            priorityTag = "Messaging",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_exchange_queue",
            name = "Exchange & Queue",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Messaging",
            description = "Exchange types (Direct, Fanout, Topic, Headers), bindings, queue durability, message TTL.",
            prerequisites = listOf("RabbitMQ"),
            recommendedResources = listOf("RabbitMQ Core Concepts Guide"),
            priorityTag = "Messaging",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_retry_dlq",
            name = "Retry & Dead Letter Queue",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Messaging",
            description = "Handling poisoned messages, exponential backoff, dead-letter exchanges (DLX), MassTransit sagas.",
            prerequisites = listOf("RabbitMQ"),
            recommendedResources = listOf("MassTransit Documentation"),
            priorityTag = "Messaging",
            status = SkillStatus.NOT_STARTED
        ),

        // Microservices
        SkillNode(
            id = "devops_monolith_modular",
            name = "Monolith & Modular Monolith",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Microservices",
            description = "Classic monolith trade-offs, modular monolith design, module boundaries, in-memory events.",
            prerequisites = listOf("Clean Architecture"),
            recommendedResources = listOf("Modular Monolith: A Primer (Kamil Grzybek)"),
            priorityTag = "Architecture",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "devops_microservices_core",
            name = "Microservices",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Microservices",
            description = "Independent deployability, decentralized data management, domain-driven design bounded contexts.",
            prerequisites = listOf("Monolith & Modular Monolith"),
            recommendedResources = listOf("Building Microservices by Sam Newman"),
            priorityTag = "Architecture",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_api_gateway",
            name = "API Gateway",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Microservices",
            description = "Reverse proxy, routing, rate limiting, token offloading, YARP (Yet Another Reverse Proxy) in .NET, Ocelot.",
            prerequisites = listOf("Microservices"),
            recommendedResources = listOf("YARP Documentation (Microsoft)"),
            priorityTag = "Architecture",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_event_driven",
            name = "Event-driven Architecture",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Microservices",
            description = "Domain events vs Integration events, eventual consistency, Outbox pattern, idempotent message consumers.",
            prerequisites = listOf("Messaging"),
            recommendedResources = listOf("Designing Event-Driven Systems (Ben Stopford)"),
            priorityTag = "Architecture",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_dist_tx",
            name = "Distributed Transactions",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Microservices",
            description = "Two-Phase Commit (2PC) limitations, Saga pattern (Orchestration vs Choreography), compensating transactions.",
            prerequisites = listOf("Event-driven Architecture"),
            recommendedResources = listOf("Microservices Patterns by Chris Richardson"),
            priorityTag = "Architecture",
            status = SkillStatus.NOT_STARTED
        ),

        // Kubernetes
        SkillNode(
            id = "devops_k8s_pods",
            name = "Pods",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Kubernetes",
            description = "Smallest deployable unit, container specs, init containers, resource limits/requests.",
            prerequisites = listOf("Docker"),
            recommendedResources = listOf("Kubernetes Up & Running (Kelsey Hightower)"),
            priorityTag = "Orchestration",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_k8s_deployments",
            name = "Deployments",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Kubernetes",
            description = "ReplicaSets, rolling updates, rollbacks, zero-downtime deployment strategies.",
            prerequisites = listOf("Pods"),
            recommendedResources = listOf("Official Kubernetes Documentation"),
            priorityTag = "Orchestration",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_k8s_services",
            name = "Services",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Kubernetes",
            description = "ClusterIP, NodePort, LoadBalancer, service discovery, kube-proxy, CoreDNS.",
            prerequisites = listOf("Pods"),
            recommendedResources = listOf("Kubernetes in Action"),
            priorityTag = "Orchestration",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_k8s_config_secrets",
            name = "ConfigMap & Secret",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Kubernetes",
            description = "Decoupling configuration from image, base64 secrets, volume mounts vs environment variables.",
            prerequisites = listOf("Deployments"),
            recommendedResources = listOf("Kubernetes Security Best Practices"),
            priorityTag = "Orchestration",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_k8s_ingress_lens",
            name = "Ingress & Lens",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Kubernetes",
            description = "Ingress controllers (NGINX Ingress), routing rules, TLS termination; Lens IDE for cluster management.",
            prerequisites = listOf("Services"),
            recommendedResources = listOf("Lens IDE for Kubernetes"),
            priorityTag = "Orchestration",
            status = SkillStatus.NOT_STARTED
        ),

        // Observability & Cloud
        SkillNode(
            id = "devops_prometheus_grafana",
            name = "Prometheus & Grafana",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Observability / Cloud",
            description = "Metric scraping, PromQL, Grafana dashboards, application latency (p95, p99), error rates.",
            prerequisites = listOf("Docker"),
            recommendedResources = listOf("Prometheus: Up & Running"),
            priorityTag = "Observability",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "devops_tracing",
            name = "Tracing & OpenTelemetry",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Observability / Cloud",
            description = "Distributed tracing across microservices, spans, traces, Jaeger, OpenTelemetry .NET SDK.",
            prerequisites = listOf("Prometheus & Grafana"),
            recommendedResources = listOf("OpenTelemetry Official Documentation"),
            priorityTag = "Observability",
            status = SkillStatus.NOT_STARTED
        ),
        SkillNode(
            id = "devops_cicd",
            name = "CI/CD",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Observability / Cloud",
            description = "Automated test pipelines, building docker containers, vulnerability scans, deployment pipelines.",
            prerequisites = listOf("Git / GitHub"),
            recommendedResources = listOf("Continuous Delivery by Jez Humble"),
            priorityTag = "DevOps Core",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "devops_azure",
            name = "Azure Fundamentals",
            branchId = BranchId.DISTRIBUTED_DEVOPS,
            category = "Observability / Cloud",
            description = "Azure App Services, Azure SQL, Azure Blob Storage, Azure Container Apps, AZ-900 foundation.",
            prerequisites = listOf("Cloud Basics"),
            recommendedResources = listOf("Microsoft Learn: Azure Fundamentals"),
            priorityTag = "Cloud",
            status = SkillStatus.NOT_STARTED
        ),

        // ==========================================
        // 3. ANDROID / MOBILE
        // ==========================================
        // Kotlin
        SkillNode(
            id = "and_kt_fundamentals",
            name = "Fundamentals",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Kotlin",
            description = "val vs var, basic types, type inference, smart casts, when expressions, ranges.",
            prerequisites = listOf("Java or C# Fundamentals"),
            recommendedResources = listOf("Kotlin in Action", "Kotlin Official Reference"),
            priorityTag = "Mobile Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_kt_oop",
            name = "OOP",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Kotlin",
            description = "Primary/secondary constructors, data classes, sealed classes/interfaces, object declarations, companion objects.",
            prerequisites = listOf("Kotlin Fundamentals"),
            recommendedResources = listOf("Kotlin Official Tour"),
            priorityTag = "Mobile Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_kt_collections",
            name = "Collections",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Kotlin",
            description = "Immutable vs mutable collections (List, Set, Map), collection transformations (map, filter, flatMap, associateBy).",
            prerequisites = listOf("Kotlin Fundamentals"),
            recommendedResources = listOf("Kotlin Standard Library Docs"),
            priorityTag = "Mobile Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_kt_coroutines",
            name = "Coroutines",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Kotlin",
            description = "Suspend functions, CoroutineScope, Dispatchers (Main, IO, Default), structured concurrency, Job cancellation.",
            prerequisites = listOf("Kotlin Fundamentals"),
            recommendedResources = listOf("Roman Elizarov Coroutines Articles", "Android Developers Coroutines Guide"),
            priorityTag = "Mobile Core",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "and_kt_flow",
            name = "Flow",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Kotlin",
            description = "Cold flows vs Hot flows (StateFlow, SharedFlow), flow operators (combine, debounce, distinctUntilChanged), collectAsStateWithLifecycle.",
            prerequisites = listOf("Coroutines"),
            recommendedResources = listOf("Reactive Streams in Kotlin Flow"),
            priorityTag = "Mobile Core",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "and_kt_null_safety",
            name = "Null Safety",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Kotlin",
            description = "Nullable types (String?), safe call (?.), Elvis operator (?:), non-null assertion (!!), platform types.",
            prerequisites = listOf("Kotlin Fundamentals"),
            recommendedResources = listOf("Kotlin Null Safety Guide"),
            priorityTag = "Mobile Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_kt_extension_functions",
            name = "Extension Functions",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Kotlin",
            description = "Extending classes without inheritance, infix functions, scope functions (let, run, apply, also, with).",
            prerequisites = listOf("Kotlin OOP"),
            recommendedResources = listOf("Effective Kotlin by Marcin Moskala"),
            priorityTag = "Mobile Core",
            status = SkillStatus.COMPLETED
        ),

        // Android
        SkillNode(
            id = "and_lifecycle",
            name = "Lifecycle",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Android",
            description = "Activity / Fragment lifecycle states, configuration changes, LifecycleOwner, repeatOnLifecycle.",
            prerequisites = listOf("Android Fundamentals"),
            recommendedResources = listOf("Android Developer Lifecycle Guide"),
            priorityTag = "Mobile Core",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "and_viewmodel",
            name = "ViewModel",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Android",
            description = "Surviving configuration changes, viewModelScope, exposing StateFlow, SavedStateHandle.",
            prerequisites = listOf("Lifecycle"),
            recommendedResources = listOf("Android Architecture Components ViewModel"),
            priorityTag = "Mobile Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_compose_jetpack",
            name = "Jetpack & Jetpack Compose",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Android",
            description = "Declarative UI, State hoisting, Recomposition, Layouts (Column, Row, Box, Scaffold), Canvas, Material 3.",
            prerequisites = listOf("Kotlin"),
            recommendedResources = listOf("Android Jetpack Compose Pathways"),
            priorityTag = "Mobile Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_retrofit_room",
            name = "Retrofit & Room",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Android",
            description = "REST API consuming with Retrofit & OkHttp, SQLite local persistence with Room, DAOs, Entities, and KSP.",
            prerequisites = listOf("Coroutines", "Flow"),
            recommendedResources = listOf("Modern Android Storage with Room"),
            priorityTag = "Mobile Core",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_firebase",
            name = "Firebase",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Android",
            description = "Firebase Authentication, Cloud Firestore, Cloud Messaging (FCM), Crashlytics, App Check.",
            prerequisites = listOf("Android Fundamentals"),
            recommendedResources = listOf("Firebase Android Documentation"),
            priorityTag = "Mobile Core",
            status = SkillStatus.PRACTICED
        ),

        // Architecture
        SkillNode(
            id = "and_arch_mvvm_clean",
            name = "MVVM & Clean Architecture",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Architecture",
            description = "Model-View-ViewModel, separation of Data / Domain / Presentation layers, UseCases.",
            prerequisites = listOf("ViewModel"),
            recommendedResources = listOf("Guide to app architecture (Android Developers)"),
            priorityTag = "Architecture",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "and_arch_hilt",
            name = "Hilt / Dependency Injection",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Architecture",
            description = "Compile-time dependency injection with Hilt/Dagger, @AndroidEntryPoint, @InstallIn, SingletonComponent.",
            prerequisites = listOf("DI Concepts"),
            recommendedResources = listOf("Dagger Hilt Official Guide"),
            priorityTag = "Architecture",
            status = SkillStatus.PRACTICED
        ),

        // Product workflow
        SkillNode(
            id = "and_workflow_pipeline",
            name = "Product Workflow",
            branchId = BranchId.ANDROID_MOBILE,
            category = "Product workflow",
            description = "Build app → GitHub repository → Clean README → Play Store release → Medium article showcase.",
            prerequisites = listOf("Android Jetpack Compose"),
            recommendedResources = listOf("Google Play Console Documentation"),
            priorityTag = "Workflow",
            status = SkillStatus.PRACTICED
        ),

        // ==========================================
        // 4. COMPUTER SCIENCE (FOUNDATIONAL)
        // ==========================================
        // Operating Systems
        SkillNode(
            id = "cs_os_process_thread",
            name = "Process & Thread",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Operating Systems",
            description = "Address spaces, PCB, thread execution contexts, context switching overhead, user vs kernel mode.",
            prerequisites = listOf("C / Systems Programming"),
            recommendedResources = listOf("OSTEP Chapters 4-6"),
            priorityTag = "Foundational CS",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "cs_os_scheduling",
            name = "Scheduling",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Operating Systems",
            description = "FIFO, SJF, Round Robin, Multi-Level Feedback Queue (MLFQ), CFS (Completely Fair Scheduler in Linux).",
            prerequisites = listOf("Process & Thread"),
            recommendedResources = listOf("OSTEP Chapters 7-10"),
            priorityTag = "Foundational CS",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "cs_os_virtual_memory",
            name = "Virtual Memory & Paging",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Operating Systems",
            description = "Address translation, page tables, multi-level paging, TLBs, page faults, swapping, memory fragmentation.",
            prerequisites = listOf("Computer Systems"),
            recommendedResources = listOf("OSTEP Chapters 18-22", "CS:APP Chapter 9"),
            priorityTag = "Foundational CS",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "cs_os_concurrency",
            name = "Concurrency & Synchronization",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Operating Systems",
            description = "Race conditions, critical sections, Mutex, Semaphores, Condition Variables, Deadlock conditions & prevention.",
            prerequisites = listOf("Process & Thread"),
            recommendedResources = listOf("OSTEP Chapters 26-32"),
            priorityTag = "Foundational CS",
            status = SkillStatus.COMPLETED
        ),

        // Computer Systems
        SkillNode(
            id = "cs_sys_cpu_assembly",
            name = "CPU & Assembly",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Computer Systems",
            description = "x86-64 register architecture, instruction pipeline, ALU, stack frames, calling conventions.",
            prerequisites = listOf("Logic Design"),
            recommendedResources = listOf("CS:APP Chapter 3"),
            priorityTag = "Foundational CS",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "cs_sys_cache",
            name = "Cache & Memory Hierarchy",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Computer Systems",
            description = "L1/L2/L3 caches, cache lines (64 bytes), temporal & spatial locality, cache hits/misses, false sharing.",
            prerequisites = listOf("CPU & Assembly"),
            recommendedResources = listOf("CS:APP Chapter 6: The Memory Hierarchy"),
            priorityTag = "Foundational CS",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "cs_sys_compilation_linking",
            name = "Compilation & Linking",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Computer Systems",
            description = "Preprocessing, compiling, assembling, static vs dynamic linking, ELF binary format, symbols.",
            prerequisites = listOf("C / C++"),
            recommendedResources = listOf("CS:APP Chapter 7: Linking"),
            priorityTag = "Foundational CS",
            status = SkillStatus.COMPLETED
        ),

        // Networks
        SkillNode(
            id = "cs_net_tcp_ip",
            name = "TCP/IP & Routing",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Networks",
            description = "OSI 7 layers vs TCP/IP model, IP addressing, subnetting, CIDR, packet routing, ARP.",
            prerequisites = listOf("CS Core"),
            recommendedResources = listOf("Computer Networks: A Top-Down Approach"),
            priorityTag = "Foundational CS",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "cs_net_tcp_udp",
            name = "TCP & UDP",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Networks",
            description = "3-way handshake, 4-way termination, flow control (sliding window), congestion control (AIMD), UDP.",
            prerequisites = listOf("TCP/IP"),
            recommendedResources = listOf("Computer Networks: Top-Down Ch 3"),
            priorityTag = "Foundational CS",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "cs_net_dns_tls",
            name = "DNS, HTTP, HTTPS & TLS",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Networks",
            description = "Hierarchical DNS resolution, TLS 1.3 handshake, asymmetric key exchange, symmetric encryption, certificates.",
            prerequisites = listOf("TCP"),
            recommendedResources = listOf("High Performance Browser Networking by Ilya Grigorik"),
            priorityTag = "Foundational CS",
            status = SkillStatus.COMPLETED
        ),

        // Data Systems
        SkillNode(
            id = "cs_data_storage_indexing",
            name = "Storage & Indexing",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Data Systems",
            description = "LSM-Trees vs B-Trees, Write-Ahead Logging (WAL), SSTables, Bloom filters, disk I/O characteristics.",
            prerequisites = listOf("Data Structures"),
            recommendedResources = listOf("DDIA Chapter 3: Storage and Retrieval"),
            priorityTag = "Foundational CS",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "cs_data_rep_part",
            name = "Replication, Partitioning & Consensus",
            branchId = BranchId.COMPUTER_SCIENCE,
            category = "Data Systems",
            description = "Leader-follower replication, split-brain, consistent hashing, CAP Theorem, Raft/Paxos consensus overview.",
            prerequisites = listOf("Storage & Indexing"),
            recommendedResources = listOf("DDIA Chapters 5-9"),
            priorityTag = "Foundational CS",
            status = SkillStatus.IN_PROGRESS
        ),

        // ==========================================
        // 5. GRADUATION PROJECT / SYSTEMS (SPECIALIZED RESEARCH)
        // Topic: LLM / KV Cache optimization
        // ==========================================
        SkillNode(
            id = "grad_cpp",
            name = "Modern C++ (C++20)",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "RAII, smart pointers (unique_ptr, shared_ptr), move semantics, rvalue references, templates, memory alignment.",
            prerequisites = listOf("Computer Systems"),
            recommendedResources = listOf("Effective Modern C++ (Scott Meyers)", "cppreference.com"),
            priorityTag = "Research Core",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "grad_memory_mgmt",
            name = "Memory Management & Paging",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "Custom memory allocators (pool allocator, bump allocator), mmap, posix_memalign, avoiding heap fragmentation.",
            prerequisites = listOf("Modern C++", "Virtual Memory"),
            recommendedResources = listOf("Game Engine Architecture Memory Systems"),
            priorityTag = "Research Core",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "grad_perf_profiling",
            name = "Performance & Profiling",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "Linux perf, flame graphs, Valgrind/Cachegrind, measuring cache misses and branch mispredictions.",
            prerequisites = listOf("Linux CLI"),
            recommendedResources = listOf("Brendan Gregg: Systems Performance"),
            priorityTag = "Research Core",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "grad_transformer_attention",
            name = "Transformer & Attention",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "Self-attention mechanism: Q, K, V matrix multiplications, sequence length scaling, memory bottleneck of inference.",
            prerequisites = listOf("Linear Algebra"),
            recommendedResources = listOf("Attention Is All You Need Paper", "The Illustrated Transformer (Jay Alammar)"),
            priorityTag = "Research Core",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "grad_kv_cache",
            name = "KV Cache Optimization",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "Key-Value caching during auto-regressive generation, eliminating redundant recalculation for past tokens.",
            prerequisites = listOf("Transformer & Attention"),
            recommendedResources = listOf("vLLM Paper: Efficient Memory Management for Large Language Models"),
            priorityTag = "Research Core",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "grad_v_attention",
            name = "PagedAttention / V-Attention",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "Virtual memory inspired non-contiguous physical memory blocks for KV caches, eliminating internal and external fragmentation.",
            prerequisites = listOf("KV Cache Optimization", "Virtual Memory"),
            recommendedResources = listOf("vLLM Architecture Documentation & Source Code"),
            priorityTag = "Research Core",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "grad_ollama_llama",
            name = "Ollama & Llama Inference Engines",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "llama.cpp architecture, tensor memory layout, quantization (GGUF), benchmarking prompt vs evaluation tokens/sec.",
            prerequisites = listOf("Modern C++"),
            recommendedResources = listOf("llama.cpp GitHub Repository"),
            priorityTag = "Research Core",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "grad_experiments_report",
            name = "Experiments & Technical Reporting",
            branchId = BranchId.GRADUATION_PROJECT,
            category = "Graduation Project: KV Cache",
            description = "Rigorous empirical methodology, statistical benchmarking, throughput/latency curves, IEEE format thesis report.",
            prerequisites = listOf("Performance & Profiling"),
            recommendedResources = listOf("Academic Technical Writing Guide"),
            priorityTag = "Research Core",
            status = SkillStatus.IN_PROGRESS
        ),

        // ==========================================
        // 6. ENGINEERING TOOLS
        // ==========================================
        // Git / GitHub
        SkillNode(
            id = "tools_git_branching",
            name = "Branching, Merge & Rebase",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Git / GitHub",
            description = "Feature branches, fast-forward merges, 3-way merges, interactive rebase (git rebase -i), conflict resolution.",
            prerequisites = listOf("Git Basics"),
            recommendedResources = listOf("Pro Git Book by Scott Chacon"),
            priorityTag = "Essential",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "tools_git_pr_review",
            name = "Pull Requests & Code Review",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Git / GitHub",
            description = "Small atomic commits, descriptive PR summaries, constructive technical reviews, semantic commit messages.",
            prerequisites = listOf("Branching"),
            recommendedResources = listOf("Google Engineering Practices: Code Review"),
            priorityTag = "Essential",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "tools_git_actions",
            name = "GitHub Actions",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Git / GitHub",
            description = "Workflows, triggers, matrix builds, caching dependencies, secrets management, automated unit tests on push.",
            prerequisites = listOf("Git / GitHub"),
            recommendedResources = listOf("GitHub Actions Official Documentation"),
            priorityTag = "Essential",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "tools_git_readme_releases",
            name = "Releases & Professional README",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Git / GitHub",
            description = "Semantic versioning (SemVer), release tags, release notes, badges, architecture diagrams in README.",
            prerequisites = listOf("Git / GitHub"),
            recommendedResources = listOf("Awesome README Guide"),
            priorityTag = "Essential",
            status = SkillStatus.STRONG
        ),

        // Linux / Terminal
        SkillNode(
            id = "tools_linux_core",
            name = "Filesystem & Permissions",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Linux / Terminal",
            description = "FHS (Filesystem Hierarchy Standard), chmod, chown, umask, POSIX permissions, symlinks, inodes.",
            prerequisites = listOf("Linux Basics"),
            recommendedResources = listOf("The Linux Command Line (William Shotts)"),
            priorityTag = "Essential",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "tools_linux_pipes_search",
            name = "Pipes, grep & find",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Linux / Terminal",
            description = "Standard streams (stdin, stdout, stderr), redirection, pipes, ripgrep (rg), sed, awk, xargs.",
            prerequisites = listOf("Filesystem & Permissions"),
            recommendedResources = listOf("Linux CLI Mastery"),
            priorityTag = "Essential",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "tools_linux_processes_sysctl",
            name = "Processes & systemctl",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Linux / Terminal",
            description = "ps, top, htop, kill signals (SIGTERM, SIGKILL), systemd units, journalctl for log inspection, environment variables.",
            prerequisites = listOf("Filesystem & Permissions"),
            recommendedResources = listOf("systemd Official Documentation"),
            priorityTag = "Essential",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "tools_linux_ssh_bash",
            name = "SSH, curl & Bash Scripting",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Linux / Terminal",
            description = "SSH key generation and config, remote port forwarding, curl HTTP debugging, automating setup with Bash scripts.",
            prerequisites = listOf("Linux Core"),
            recommendedResources = listOf("Bash Hackers Wiki"),
            priorityTag = "Essential",
            status = SkillStatus.STRONG
        ),

        // Development Environment
        SkillNode(
            id = "tools_env_lazyvim",
            name = "LazyVim & Terminal Workflow",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Development Environment",
            description = "Modal editing, Neovim configuration, LazyVim keybindings, Treesitter, Telescope, LSP integration, tmux.",
            prerequisites = listOf("Vim Basics"),
            recommendedResources = listOf("LazyVim.org Documentation"),
            priorityTag = "Workflow",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "tools_env_cpp_build",
            name = "C++ Workflow, Build & Debug",
            branchId = BranchId.ENGINEERING_TOOLS,
            category = "Development Environment",
            description = "CMake, Makefiles, Clang/GCC compiler flags (-O3, -g, -Wall), GDB/LLDB debugging, AddressSanitizer (ASan).",
            prerequisites = listOf("C++"),
            recommendedResources = listOf("Modern CMake by Henry Schreiner"),
            priorityTag = "Workflow",
            status = SkillStatus.PRACTICED
        ),

        // ==========================================
        // 7. CYBER SECURITY
        // ==========================================
        SkillNode(
            id = "sec_hashing_encryption",
            name = "Hashing & Encryption",
            branchId = BranchId.CYBER_SECURITY,
            category = "Cryptography",
            description = "One-way hashes (SHA-256) vs Symmetric encryption (AES-GCM) vs Asymmetric encryption (RSA, ECC).",
            prerequisites = listOf("Math & Logic"),
            recommendedResources = listOf("Serious Cryptography by Jean-Philippe Aumasson"),
            priorityTag = "Security",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "sec_https_tls",
            name = "HTTPS & TLS",
            branchId = BranchId.CYBER_SECURITY,
            category = "Network Security",
            description = "Public Key Infrastructure (PKI), Certificate Authorities (CA), Let's Encrypt, HSTS, Certificate pinning.",
            prerequisites = listOf("Computer Networks"),
            recommendedResources = listOf("Bulletproof TLS and PKI (Ivan Ristić)"),
            priorityTag = "Security",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "sec_auth_oauth_jwt",
            name = "Authentication, JWT & OAuth",
            branchId = BranchId.CYBER_SECURITY,
            category = "Identity & Access",
            description = "OAuth 2.0 Authorization Code Flow with PKCE, OpenID Connect (OIDC), JWT signing algorithms and validation.",
            prerequisites = listOf("ASP.NET Core Auth"),
            recommendedResources = listOf("OAuth 2.0 in Action"),
            priorityTag = "Security",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "sec_owasp_top10",
            name = "SQL Injection, XSS & CSRF",
            branchId = BranchId.CYBER_SECURITY,
            category = "Application Security",
            description = "Parameterized queries, output encoding, Content Security Policy (CSP), Anti-forgery tokens, OWASP Top 10.",
            prerequisites = listOf("SQL", "HTTP"),
            recommendedResources = listOf("OWASP Top 10 Documentation"),
            priorityTag = "Security",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "sec_api_network",
            name = "API Security & Rate Limiting",
            branchId = BranchId.CYBER_SECURITY,
            category = "Application Security",
            description = "CORS policies, Rate limiting algorithms (Token Bucket, Leaky Bucket), API secret rotation, DDoD mitigation.",
            prerequisites = listOf("ASP.NET Core"),
            recommendedResources = listOf("OWASP API Security Top 10"),
            priorityTag = "Security",
            status = SkillStatus.PRACTICED
        ),

        // ==========================================
        // 8. ENGLISH (GOAL: ACTIVE B2+)
        // ==========================================
        SkillNode(
            id = "eng_vocab_oxford",
            name = "Oxford 5000 & Active Vocab",
            branchId = BranchId.ENGLISH,
            category = "Vocabulary",
            description = "Targeting high-frequency academic and technical vocabulary; converting passive recognition to active production.",
            prerequisites = listOf("Intermediate English"),
            recommendedResources = listOf("Oxford 5000 Word List", "Anki Spaced Repetition"),
            priorityTag = "Active B2+",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "eng_habits_chatgpt_speaking",
            name = "Speaking with ChatGPT Voice",
            branchId = BranchId.ENGLISH,
            category = "Daily Habits",
            description = "15-20 min daily technical conversations in English about software architecture, trade-offs, and daily summaries.",
            prerequisites = listOf("Basic Speech"),
            recommendedResources = listOf("ChatGPT Voice Mode"),
            priorityTag = "Active B2+",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "eng_habits_tech_reading",
            name = "Technical Reading & RFCs",
            branchId = BranchId.ENGLISH,
            category = "Daily Habits",
            description = "Reading original English documentation (Microsoft Learn, RFCs, Hacker News, engineering blogs).",
            prerequisites = listOf("Vocabulary"),
            recommendedResources = listOf("High Scalability Blog", "ACM Queue"),
            priorityTag = "Active B2+",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "eng_habits_writing",
            name = "Technical Writing",
            branchId = BranchId.ENGLISH,
            category = "Daily Habits",
            description = "Writing GitHub READMEs, commit messages, code comments, and Medium technical articles in clear English.",
            prerequisites = listOf("Technical Reading"),
            recommendedResources = listOf("Google Technical Writing Courses"),
            priorityTag = "Active B2+",
            status = SkillStatus.PRACTICED
        ),

        // ==========================================
        // 9. PORTFOLIO / OUTPUT
        // ==========================================
        SkillNode(
            id = "port_pharmacy_proj",
            name = "Pharmacy Management Project",
            branchId = BranchId.PORTFOLIO_OUTPUT,
            category = "Target Portfolio",
            description = "Clean Architecture .NET 8 backend, PostgreSQL, EF Core, Unit/Integration tests, Docker Compose.",
            prerequisites = listOf(".NET Backend", "PostgreSQL", "Docker"),
            recommendedResources = listOf("Clean Architecture Template"),
            priorityTag = "Flagship Project",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "port_backend_projects",
            name = "2–3 Strong Backend Projects",
            branchId = BranchId.PORTFOLIO_OUTPUT,
            category = "Target Portfolio",
            description = "Microservices / Event-driven order processing, Redis caching layer, distributed background job system.",
            prerequisites = listOf("ASP.NET Core", "RabbitMQ"),
            recommendedResources = listOf("Enterprise Project Blueprints"),
            priorityTag = "Portfolio Core",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "port_android_apps",
            name = "Several Small Android Apps",
            branchId = BranchId.PORTFOLIO_OUTPUT,
            category = "Target Portfolio",
            description = "Interactive Jetpack Compose apps, Room offline-first persistence, Material 3 design, Google Play publication.",
            prerequisites = listOf("Android", "Kotlin"),
            recommendedResources = listOf("Android Showcase Apps"),
            priorityTag = "Portfolio Core",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "port_grad_systems_proj",
            name = "Graduation Systems Project",
            branchId = BranchId.PORTFOLIO_OUTPUT,
            category = "Target Portfolio",
            description = "LLM / KV Cache optimization in C++, non-contiguous paged memory evaluation, profiling flame graphs.",
            prerequisites = listOf("C++", "Virtual Memory", "Transformer Attention"),
            recommendedResources = listOf("Research Paper Drafts"),
            priorityTag = "Research Thesis",
            status = SkillStatus.IN_PROGRESS
        ),
        SkillNode(
            id = "port_medium_articles",
            name = "Technical Articles on Medium",
            branchId = BranchId.PORTFOLIO_OUTPUT,
            category = "Target Portfolio",
            description = "Deep dives into memory optimization, EF Core tracking performance, and Android Canvas rendering.",
            prerequisites = listOf("Completed Projects"),
            recommendedResources = listOf("Medium Publication Guides"),
            priorityTag = "Proof of Work",
            status = SkillStatus.PRACTICED
        ),

        // ==========================================
        // 10. KNOWLEDGE MANAGEMENT (LEARNING LOOP)
        // ==========================================
        SkillNode(
            id = "km_loop_learn",
            name = "1. LEARN (Principles & Docs)",
            branchId = BranchId.KNOWLEDGE_MANAGEMENT,
            category = "Learning Loop",
            description = "Understand the core runtime mechanics, reading official documentation and architectural RFCs.",
            prerequisites = listOf("Curiosity"),
            recommendedResources = listOf("Official Documentation"),
            priorityTag = "System Loop",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "km_loop_apply",
            name = "2. APPLY (Minimal POCs)",
            branchId = BranchId.KNOWLEDGE_MANAGEMENT,
            category = "Learning Loop",
            description = "Build isolated test harness or minimal reproduction repository to verify behavior and failure modes.",
            prerequisites = listOf("LEARN"),
            recommendedResources = listOf("GitHub Gists / Sandbox repos"),
            priorityTag = "System Loop",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "km_loop_notes",
            name = "3. WRITE NOTES (Personal Mental Models)",
            branchId = BranchId.KNOWLEDGE_MANAGEMENT,
            category = "Learning Loop",
            description = "Document internal architecture decisions, edge cases, gotchas, and performance caveats.",
            prerequisites = listOf("APPLY"),
            recommendedResources = listOf("Markdown Knowledge Base"),
            priorityTag = "System Loop",
            status = SkillStatus.STRONG
        ),
        SkillNode(
            id = "km_loop_build",
            name = "4. BUILD (End-to-End System)",
            branchId = BranchId.KNOWLEDGE_MANAGEMENT,
            category = "Learning Loop",
            description = "Incorporate the knowledge into a production-grade portfolio system with Docker, tests, and CI/CD.",
            prerequisites = listOf("WRITE NOTES"),
            recommendedResources = listOf("Clean Architecture Solutions"),
            priorityTag = "System Loop",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "km_loop_article",
            name = "5. WRITE MEDIUM ARTICLE (Technical Memory)",
            branchId = BranchId.KNOWLEDGE_MANAGEMENT,
            category = "Learning Loop",
            description = "Publish an engineering post. The goal is technical memory and portfolio evidence, NOT becoming an influencer.",
            prerequisites = listOf("BUILD"),
            recommendedResources = listOf("Technical Writing Guidelines"),
            priorityTag = "System Loop",
            status = SkillStatus.PRACTICED
        ),
        SkillNode(
            id = "km_loop_review",
            name = "6. REVIEW LATER (Spaced Refactoring)",
            branchId = BranchId.KNOWLEDGE_MANAGEMENT,
            category = "Learning Loop",
            description = "Periodically revisit existing codebase after learning new patterns to refactor and cement deep mastery.",
            prerequisites = listOf("WRITE MEDIUM ARTICLE"),
            recommendedResources = listOf("Spaced Repetition Schedule"),
            priorityTag = "System Loop",
            status = SkillStatus.IN_PROGRESS
        ),

        // ==========================================
        // 11. CERTIFICATES (OPTIONAL BYPRODUCT)
        // ==========================================
        SkillNode(
            id = "cert_btk_academy",
            name = "BTK Academy Courses",
            branchId = BranchId.CERTIFICATES,
            category = "Optional Side Branch",
            description = "Structured foundational courses on .NET and database management. Certificates are a byproduct, not the main goal.",
            prerequisites = listOf("Self-discipline"),
            recommendedResources = listOf("BTK Akademi Platform"),
            priorityTag = "Optional Byproduct",
            status = SkillStatus.COMPLETED
        ),
        SkillNode(
            id = "cert_azure_fundamentals",
            name = "Azure AZ-900 / Developer Certs",
            branchId = BranchId.CERTIFICATES,
            category = "Optional Side Branch",
            description = "Validation of cloud fundamentals. Valued as secondary proof alongside actual GitHub repositories.",
            prerequisites = listOf("Azure Fundamentals"),
            recommendedResources = listOf("Microsoft Certification Dashboard"),
            priorityTag = "Optional Byproduct",
            status = SkillStatus.NOT_STARTED
        ),

        // ==========================================
        // 12. SECOND LANGUAGE (OPTIONAL HOBBY BRANCH)
        // ==========================================
        SkillNode(
            id = "lang_german",
            name = "German (A1/A2)",
            branchId = BranchId.SECOND_LANGUAGE,
            category = "Optional Hobby Branch",
            description = "Casual hobby language learning. No urgency or timeline pressure.",
            prerequisites = listOf("Interest"),
            recommendedResources = listOf("Nicos Weg (Deutsche Welle)", "Duolingo / Anki"),
            priorityTag = "Optional Hobby",
            status = SkillStatus.NOT_STARTED
        ),
        SkillNode(
            id = "lang_spanish",
            name = "Spanish (A1/A2)",
            branchId = BranchId.SECOND_LANGUAGE,
            category = "Optional Hobby Branch",
            description = "Secondary elective language interest. No urgency.",
            prerequisites = listOf("Interest"),
            recommendedResources = listOf("Dreaming Spanish (Comprehensible Input)"),
            priorityTag = "Optional Hobby",
            status = SkillStatus.NOT_STARTED
        )
    )
}
