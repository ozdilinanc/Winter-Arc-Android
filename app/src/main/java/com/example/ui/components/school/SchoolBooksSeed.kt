package com.example.ui.components.school

fun defaultTrackedBooks(): List<TrackedBook> {
    return listOf(
        // =========================================================================
        // 1. OPERATING SYSTEMS: THREE EASY PIECES (OSTEP)
        // =========================================================================
        TrackedBook(
            id = "book_ostep",
            title = "Operating Systems: Three Easy Pieces (OSTEP)",
            shortTitle = "OSTEP",
            authors = "Remzi H. Arpaci-Dusseau & Andrea C. Arpaci-Dusseau",
            authorOrDomain = "İşletim Sistemleri, Çekirdek & Sistem Programlama",
            whyItMatters = "İşletim sistemlerini Sanallaştırma, Eşzamanlılık ve Kalıcılık olarak üç kolay parçada C kodları ve simülatörlerle öğreten başyapıt.",
            totalPages = 640,
            currentPage = 145,
            coverEmoji = "💻",
            keyTopics = listOf("Virtualization", "Processes & Threads", "Paging & TLB", "Semaphores & Locks", "Crash Consistency & RAID"),
            status = BookReadingStatus.READING,
            personalNotes = "CPU sanallaştırmasında MLFQ ve bellek tarafında çok seviyeli sayfalama bölümleri kritik önemde.",
            sections = listOf(
                BookSection(
                    id = "ostep_sec_intro",
                    title = "Giriş & Temel İlkeler",
                    emoji = "📖",
                    chapters = listOf(
                        BookChapterItem("ostep_ch1", "Bölüm 1", "Kitap Üzerine Diyalog", "İşletim sistemlerinin varlık sebebi ve temel felsefe"),
                        BookChapterItem("ostep_ch2", "Bölüm 2", "İşletim Sistemlerine Giriş", "CPU/Bellek sanallaştırma, Concurrency, Kalıcılık ve Tasarım hedefleri")
                    )
                ),
                BookSection(
                    id = "ostep_sec_cpu",
                    title = "I. Sanallaştırma: CPU (Virtualization)",
                    emoji = "⚡",
                    chapters = listOf(
                        BookChapterItem("ostep_ch4", "Bölüm 4", "Süreç (Process) Soyutlaması", "Süreç durumları (Running, Ready, Blocked), PCB ve veri yapıları"),
                        BookChapterItem("ostep_ch5", "Bölüm 5", "Process API: fork, exec, wait", "UNIX süreç oluşturma, yürütme ve bekleme sistem çağrıları"),
                        BookChapterItem("ostep_ch6", "Bölüm 6", "Sınırlı Doğrudan Yürütme (LDE)", "User vs Kernel modu, Trap tabloları ve Context Switch mekanizması"),
                        BookChapterItem("ostep_ch7", "Bölüm 7", "CPU Çizelgeleme Temelleri", "FIFO, SJF, STCF, Round Robin ve Yanıt Süresi metriği"),
                        BookChapterItem("ostep_ch8", "Bölüm 8", "Çok Seviyeli Geri Besleme Kuyruğu (MLFQ)", "Öncelik kuralları, Priority Boost ve CPU açlığını önleme"),
                        BookChapterItem("ostep_ch9", "Bölüm 9", "Orantılı Paylaşım (Lottery)", "Bilet mekanizması, deterministik olmayan çizelgeleme, Stride scheduling"),
                        BookChapterItem("ostep_ch10", "Bölüm 10", "Çok İşlemcili Çizelgeleme", "Cache Affinity (Önbellek benzeşimi), SQMS ve MQMS mimarisi")
                    )
                ),
                BookSection(
                    id = "ostep_sec_mem",
                    title = "I. Sanallaştırma: Bellek (Memory)",
                    emoji = "🧠",
                    chapters = listOf(
                        BookChapterItem("ostep_ch13", "Bölüm 13", "Adres Uzayı Soyutlaması", "Fiziksel bellekten sanal adres uzayına geçiş ve izolasyon"),
                        BookChapterItem("ostep_ch14", "Bölüm 14", "Bellek API: malloc ve free", "Heap tahsisi, yaygın bellek sızıntıları ve geçersiz işaretçiler"),
                        BookChapterItem("ostep_ch15", "Bölüm 15", "Adres Çevirisi Mekanizmaları", "Dinamik donanımsal yer değiştirme, Base ve Bounds yazmaçları"),
                        BookChapterItem("ostep_ch16", "Bölüm 16", "Bölütleme (Segmentation)", "Segment kayıtları, yığın ve veri bölütleri, paylaşım desteği"),
                        BookChapterItem("ostep_ch17", "Bölüm 17", "Boş Alan Yönetimi (Free-Space)", "Parçalanma (Fragmentation), First-fit, Best-fit ve Buddy Allocator"),
                        BookChapterItem("ostep_ch18", "Bölüm 18", "Sayfalama (Paging) Temelleri", "Sayfa tabloları, sayfa çerçeveleri (frames) ve adres parçalama"),
                        BookChapterItem("ostep_ch19", "Bölüm 19", "Hızlı Çeviri: TLB", "Translation Lookaside Buffer mimarisi, Hit/Miss ve donanım desteği"),
                        BookChapterItem("ostep_ch20", "Bölüm 20", "Küçük Sayfa Tabloları", "Çok seviyeli sayfa tabloları, Inverted page tables ve bellek tasarrufu"),
                        BookChapterItem("ostep_ch21", "Bölüm 21", "Fiziksel Belleğin Ötesi: Mekanizmalar", "Swap alanı, Present bit, Sayfa Hatası (Page Fault) işleme döngüsü"),
                        BookChapterItem("ostep_ch22", "Bölüm 22", "Fiziksel Belleğin Ötesi: Politikalar", "Sayfa değiştirme algoritmaları: FIFO, Optimal, LRU, Clock algoritması ve Thrashing"),
                        BookChapterItem("ostep_ch23", "Bölüm 23", "VAX/VMS Sanal Bellek Mimarisi", "Klasik bir işletim sisteminde sanal bellek pratikleri")
                    )
                ),
                BookSection(
                    id = "ostep_sec_concurrency",
                    title = "II. Eşzamanlılık (Concurrency)",
                    emoji = "🔄",
                    chapters = listOf(
                        BookChapterItem("ostep_ch26", "Bölüm 26", "Concurrency Giriş: Thread'ler", "Thread oluşturma, paylaşılan bellek, kontrolsüz çizelgeleme ve Race Conditions"),
                        BookChapterItem("ostep_ch27", "Bölüm 27", "Thread API Pratikleri", "pthread_create, pthread_join, kilitler ve koşul değişkenleri"),
                        BookChapterItem("ostep_ch28", "Bölüm 28", "Kilitler (Locks)", "Test-and-Set, Compare-and-Swap, Spinlock, Yield ve Futex mekanizmaları"),
                        BookChapterItem("ostep_ch29", "Bölüm 29", "Kilit Tabanlı Veri Yapıları", "Eşzamanlı sayaçlar, bağlı listeler, kuyruklar ve hash tabloları"),
                        BookChapterItem("ostep_ch30", "Bölüm 30", "Koşul Değişkenleri (Condition Variables)", "Üretici/Tüketici (Bounded Buffer) problemi ve Mesa semantiği"),
                        BookChapterItem("ostep_ch31", "Bölüm 31", "Semaforlar (Semaphores)", "İkili semaforlar, Okuyucu-Yazıcı kilitleri, Yemek Yiyen Filozoflar"),
                        BookChapterItem("ostep_ch32", "Bölüm 32", "Eşzamanlılık Hataları & Deadlock", "Deadlock koşulları, dairesel bekleme kırma, non-deadlock senkronizasyon hataları"),
                        BookChapterItem("ostep_ch33", "Bölüm 33", "Olay Tabanlı Eşzamanlılık (Event-based)", "Event loop mimarisi, select(), epoll, asenkron G/Ç ve kilitsiz tasarım")
                    )
                ),
                BookSection(
                    id = "ostep_sec_persistence",
                    title = "III. Kalıcılık & Depolama (Persistence)",
                    emoji = "💾",
                    chapters = listOf(
                        BookChapterItem("ostep_ch36", "Bölüm 36", "G/Ç Aygıtları (I/O Devices)", "Sistem mimarisi, Kesmeler (Interrupts), DMA ve Aygıt Sürücüleri"),
                        BookChapterItem("ostep_ch37", "Bölüm 37", "Sabit Disk Sürücüleri (HDD)", "Geometri, arama süresi, dönme gecikmesi ve disk çizelgeleme (SSTF, SCAN)"),
                        BookChapterItem("ostep_ch38", "Bölüm 38", "RAID Dizi Mimarisi", "RAID 0 (Striping), RAID 1 (Mirroring), RAID 4/5 (Eşlik/Parite hesaplama)"),
                        BookChapterItem("ostep_ch39", "Bölüm 39", "Dosyalar ve Dizinler", "Dosya tanımlayıcıları, dosya sistemi API (open, read, write, fsync, links)"),
                        BookChapterItem("ostep_ch40", "Bölüm 40", "Dosya Sistemi Uygulaması", "Inode yapısı, dizin organizasyonu, boş alan yönetimi ve blok haritası"),
                        BookChapterItem("ostep_ch41", "Bölüm 41", "Hızlı Dosya Sistemi (FFS)", "Disk duyarlılığı, Silindir Grupları (Cylinder Groups) ve yerellik"),
                        BookChapterItem("ostep_ch42", "Bölüm 42", "Çökme Tutarlılığı: FSCK ve Journaling", "FSCK sorunları, Write-Ahead Logging (Journaling) ve metadata tutarlılığı"),
                        BookChapterItem("ostep_ch43", "Bölüm 43", "Log-Structured File Systems (LFS)", "Sıralı yazma optimizasyonu, Inode haritası ve Çöp Toplama (Cleaning)"),
                        BookChapterItem("ostep_ch44", "Bölüm 44", "Veri Bütünlüğü ve Koruma", "Sektör hataları, Checksum yöntemleri, Bit Rot ve Scrubbing")
                    )
                ),
                BookSection(
                    id = "ostep_sec_dist",
                    title = "Dağıtık Sistemler Eki",
                    emoji = "🌐",
                    chapters = listOf(
                        BookChapterItem("ostep_ch47", "Bölüm 47", "Dağıtık Sistem Temelleri & RPC", "Güvenilmez iletişim katmanları, RPC mimarisi ve hata modelleri"),
                        BookChapterItem("ostep_ch48", "Bölüm 48", "Sun Network File System (NFS)", "Durumsuz (Stateless) protokol tasarımı, İdempotent operasyonlar ve önbellek tutarlılığı"),
                        BookChapterItem("ostep_ch49", "Bölüm 49", "Andrew File System (AFS)", "İstemci taraflı kalıcı önbellekleme ve ölçeklenebilir dağıtık dosya erişimi")
                    )
                )
            )
        ),

        // =========================================================================
        // 2. DESIGNING DATA-INTENSIVE APPLICATIONS (DDIA)
        // =========================================================================
        TrackedBook(
            id = "book_ddia",
            title = "Designing Data-Intensive Applications",
            shortTitle = "DDIA",
            authors = "Martin Kleppmann (University of Cambridge)",
            authorOrDomain = "Dağıtık Sistemler, Büyük Veri & Veritabanı Mimarisi",
            whyItMatters = "Dağıtık veri sistemleri, depolama motorları (LSM vs B-Tree), mutabakat protokolleri ve replikasyon mekanizmalarının başucu eseri.",
            totalPages = 616,
            currentPage = 210,
            coverEmoji = "🗄️",
            keyTopics = listOf("LSM-Trees vs B-Trees", "Replication & Partitioning", "ACID & Transactions", "Consistency & Consensus", "Batch & Stream Processing"),
            status = BookReadingStatus.READING,
            personalNotes = "Bölüm 5-7 arası replikasyon ve izolasyon seviyeleri backend mimarisi için altın değerinde.",
            sections = listOf(
                BookSection(
                    id = "ddia_sec_part1",
                    title = "Part I: Veri Sistemlerinin Temelleri",
                    emoji = "🧱",
                    chapters = listOf(
                        BookChapterItem("ddia_ch1", "Bölüm 1", "Güvenilir, Ölçeklenebilir ve Sürdürülebilir Uygulamalar", "Reliability, Scalability, P99 Gecikmeler, Operability ve Evolvability"),
                        BookChapterItem("ddia_ch2", "Bölüm 2", "Veri Modelleri ve Sorgu Dilleri", "İlişkisel vs Doküman modelleri, NoSQL, Ağ ve Grafik Modelleri (Cypher, SPARQL, Datalog)"),
                        BookChapterItem("ddia_ch3", "Bölüm 3", "Depolama ve Erişim Motorları", "Hash indeksleri, SSTables & LSM-Trees, B-Trees, OLTP vs OLAP, Sütun Odaklı Depolama"),
                        BookChapterItem("ddia_ch4", "Bölüm 4", "Kodlama ve Evrim (Encoding & Evolution)", "JSON, XML, Protobuf, Thrift, Avro, Şema uyumluluğu, REST, RPC ve Mesaj Akışı")
                    )
                ),
                BookSection(
                    id = "ddia_sec_part2",
                    title = "Part II: Dağıtık Veri (Distributed Data)",
                    emoji = "🌐",
                    chapters = listOf(
                        BookChapterItem("ddia_ch5", "Bölüm 5", "Replikasyon (Replication)", "Leader-Follower, Senkron/Asenkron, Replikasyon gecikmesi, Multi-Leader, Leaderless & Quorums"),
                        BookChapterItem("ddia_ch6", "Bölüm 6", "Bölümleme (Partitioning / Sharding)", "Key Range ve Hash of Key bölümleme, İkincil indeksler, Sıcak noktalar, Yeniden dengeleme"),
                        BookChapterItem("ddia_ch7", "Bölüm 7", "İşlemler (Transactions & ACID)", "ACID ilkeleri, Zayıf izolasyon seviyeleri, Snapshot Isolation, Write Skew, 2PL, SSI"),
                        BookChapterItem("ddia_ch8", "Bölüm 8", "Dağıtık Sistemlerin Zorlukları", "Ağ bölünmeleri, Güvenilmez saatler (NTP skew), Süreç duraklamaları, Bizans hataları"),
                        BookChapterItem("ddia_ch9", "Bölüm 9", "Tutarlılık ve Mutabakat (Consensus)", "Linearizability, Total Order Broadcast, 2-Phase Commit (2PC), Paxos & Raft")
                    )
                ),
                BookSection(
                    id = "ddia_sec_part3",
                    title = "Part III: Türetilmiş Veri (Derived Data)",
                    emoji = "⚡",
                    chapters = listOf(
                        BookChapterItem("ddia_ch10", "Bölüm 10", "Toplu İşleme (Batch Processing)", "Unix araçları felsefesi, Dağıtık dosya sistemleri, MapReduce yürütme ve Join stratejileri"),
                        BookChapterItem("ddia_ch11", "Bölüm 11", "Akış İşleme (Stream Processing)", "Mesajlaşma sistemleri, Partitioned Logs (Kafka), CDC, Event Sourcing, Akış birleştirmeleri ve Pencereleme"),
                        BookChapterItem("ddia_ch12", "Bölüm 12", "Veri Sistemlerinin Geleceği", "Veritabanlarının ayrıştırılması (Unbundling), Uçtan uca doğruluk ve veri bütünlüğü")
                    )
                )
            )
        ),

        // =========================================================================
        // 3. COMPUTER SYSTEMS: A PROGRAMMER'S PERSPECTIVE (CS:APP)
        // =========================================================================
        TrackedBook(
            id = "book_csapp",
            title = "Computer Systems: A Programmer's Perspective (CS:APP)",
            shortTitle = "CS:APP",
            authors = "Randal E. Bryant & David R. O'Hallaron (Carnegie Mellon)",
            authorOrDomain = "Sistem Mimarisi, x86-64 & Donanım-Yazılım Köprüsü",
            whyItMatters = "C kodunun assembly'ye dönüşümünü, bellek hiyerarşisini, sanal belleği ve linker mekanizmasını yazılımcı bakış açısıyla öğreten dünya standardı.",
            totalPages = 1100,
            currentPage = 80,
            coverEmoji = "⚙️",
            keyTopics = listOf("Machine-Level Code", "Processor Architecture", "Memory Hierarchy", "Linking & Shared Libs", "Virtual Memory"),
            status = BookReadingStatus.READING,
            personalNotes = "Bölüm 3 (Assembly) ve Bölüm 6 (Cache Memories) yazılımın gerçek çalışma mantığını anlamak için mutlaka bitmeli.",
            sections = listOf(
                BookSection(
                    id = "csapp_sec_intro",
                    title = "Giriş: Bilgisayar Sistemlerine Bakış",
                    emoji = "🖥️",
                    chapters = listOf(
                        BookChapterItem("csapp_ch1", "Bölüm 1", "Bir Bilgisayar Sisteminde Tur", "Bitler, Derleme aşamaları, Donanım organizasyonu, Önbellekler ve İşletim sistemi soyutlamaları")
                    )
                ),
                BookSection(
                    id = "csapp_sec_part1",
                    title = "Part I: Program Yapısı ve Yürütme",
                    emoji = "⚙️",
                    chapters = listOf(
                        BookChapterItem("csapp_ch2", "Bölüm 2", "Bilgiyi Temsil Etme ve İşleme", "Hexadecimal, Two's Complement tamsayılar, Taşmalar, IEEE 754 Kayan Nokta aritmetiği"),
                        BookChapterItem("csapp_ch3", "Bölüm 3", "C Kodunun Makine Düzeyinde Gösterimi", "x86-64 Assembly, Yazmaçlar, Kontrol yapıları, Döngüler, Stack Frame, Buffer Overflow saldırıları"),
                        BookChapterItem("csapp_ch4", "Bölüm 4", "İşlemci Mimarisi", "Y86-64 komut kümesi, Boru Hattı (Pipelining) ilkeleri ve Tehlikeler (Hazards)"),
                        BookChapterItem("csapp_ch5", "Bölüm 5", "Program Performansını Optimize Etme", "Döngü açma (Loop unrolling), Bellek erişim optimizasyonu, Dal tahmini, Profiling & Amdahl Yasası"),
                        BookChapterItem("csapp_ch6", "Bölüm 6", "Bellek Hiyerarşisi", "SRAM/DRAM, Referans Yerelliği (Locality), Önbellek organizasyonu, Doğrudan ve Küme eşlemeli Cache, Cache-Friendly Kod")
                    )
                ),
                BookSection(
                    id = "csapp_sec_part2",
                    title = "Part II: Sistem Üzerinde Program Çalıştırma",
                    emoji = "🚀",
                    chapters = listOf(
                        BookChapterItem("csapp_ch7", "Bölüm 7", "Bağlama (Linking)", "Statik ve dinamik bağlama, ELF dosya formatı, Sembol çözümleme, Statik ve Paylaşılan kütüphaneler (.so/.dll)"),
                        BookChapterItem("csapp_ch8", "Bölüm 8", "İstisnai Kontrol Akışı", "Kesmeler, Tuzaklar (Traps), Süreçler, Context Switch, fork/execve, Sinyaller"),
                        BookChapterItem("csapp_ch9", "Bölüm 9", "Program Yürütme Süresini Ölçme", "Süreç zamanlayıcıları, Döngü sayaçları ve deneysel protokol"),
                        BookChapterItem("csapp_ch10", "Bölüm 10", "Sanal Bellek (Virtual Memory)", "Adres çevirisi, Sayfa tabloları, TLB, Çok seviyeli sayfalar, mmap, malloc/free dinamik bellek tahsisi, Çöp Toplama")
                    )
                ),
                BookSection(
                    id = "csapp_sec_part3",
                    title = "Part III: Programlar Arası İletişim",
                    emoji = "🔗",
                    chapters = listOf(
                        BookChapterItem("csapp_ch11", "Bölüm 11", "Thread'ler ile Eşzamanlı Programlama", "POSIX Thread'ler, Paylaşılan bellek, Semaforlar ile senkronizasyon, Mutex, Deadlock"),
                        BookChapterItem("csapp_ch12", "Bölüm 12", "Ağ Programlama", "İstemci-Sunucu mimarisi, IP adresleme, Sockets API, Eşzamanlı sunucular ve Tiny Web Server mimarisi")
                    )
                )
            )
        ),

        // =========================================================================
        // 4. COMPUTER NETWORKING: A TOP-DOWN APPROACH
        // =========================================================================
        TrackedBook(
            id = "book_networks",
            title = "Computer Networking: A Top-Down Approach",
            shortTitle = "Computer Networks",
            authors = "James F. Kurose & Keith W. Ross",
            authorOrDomain = "Bilgisayar Ağları, İnternet Protokolleri & Dağıtık İletişim",
            whyItMatters = "Ağ mimarisini ve protokolleri en tepeden, yani doğrudan uygulama katmanından (HTTP/DNS) fiziksel katmana doğru öğreten modern ağ klasiği.",
            totalPages = 860,
            currentPage = 60,
            coverEmoji = "🌐",
            keyTopics = listOf("Socket Programming", "TCP Flow & Congestion", "IP Routing & BGP", "Link Layer & Ethernet", "Network Security & TLS"),
            status = BookReadingStatus.READING,
            personalNotes = "Transport katmanındaki TCP tıkanıklık kontrolü (AIMD) ve uygulama katmanındaki soket programlama pratikleri backend mühendisliğinin temeli.",
            sections = listOf(
                BookSection(
                    id = "net_sec_ch1",
                    title = "Bölüm 1: Bilgisayar Ağları ve İnternet",
                    emoji = "🌐",
                    chapters = listOf(
                        BookChapterItem("net_1_1", "1.1", "İnternetin Yapısı ve Protokol Tanımı", "Ağ uçları, ISP'ler ve protokol kavramının temel tanımı"),
                        BookChapterItem("net_1_2", "1.2", "Ağ Kenarı ve Fiziksel Medya", "Erişim ağları, DSL, Fiber, Kablo ve fiziksel iletim medyası"),
                        BookChapterItem("net_1_3", "1.3", "Ağ Çekirdeği: Paket vs Devre Anahtarlama", "Paket anahtarlama, yönlendirme, istatistiksel çoğullama"),
                        BookChapterItem("net_1_4", "1.4", "Gecikme, Kayıp ve Verim", "Kuyruk, iletim ve yayılma gecikmesi, Paket kaybı, Throughput"),
                        BookChapterItem("net_1_5", "1.5", "Protokol Katmanları & 5 Katmanlı Model", "Kapsülleme (Encapsulation), Katmanlı mimari avantajları"),
                        BookChapterItem("net_1_6", "1.6", "Ağ Güvenliği ve İnternet Tarihi", "Ağ saldırı türleri, paket koklama, IP sahteciliği")
                    )
                ),
                BookSection(
                    id = "net_sec_ch2",
                    title = "Bölüm 2: Uygulama Katmanı (Application)",
                    emoji = "🖥️",
                    chapters = listOf(
                        BookChapterItem("net_2_1", "2.1", "Ağ Uygulaması İlkeleri & Mimariler", "İstemci-Sunucu vs P2P, Taşıma katmanı gereksinimleri"),
                        BookChapterItem("net_2_2", "2.2", "Web ve HTTP Protokolü", "HTTP/1.1 vs HTTP/2, Kalıcı bağlantılar, Çerezler, Web Önbellekleme (Proxy)"),
                        BookChapterItem("net_2_3", "2.3", "Dosya Transferi (FTP) ve E-Posta", "SMTP, IMAP, POP3 protokolleri ve mesaj formatları"),
                        BookChapterItem("net_2_4", "2.4", "DNS: İnternetin Dizin Hizmeti", "DNS hiyerarşisi, Kök, TLD ve Yetkili sunucular, Kayıt türleri (A, AAAA, CNAME, MX)"),
                        BookChapterItem("net_2_5", "2.5", "Eşler Arası (P2P) ve Dağıtık Hash Tabloları", "BitTorrent dosya dağıtımı ve DHT (Distributed Hash Tables)"),
                        BookChapterItem("net_2_6", "2.6", "Soket Programlama (Socket Programming)", "UDP ve TCP soketleri ile istemci-sunucu uygulaması geliştirme")
                    )
                ),
                BookSection(
                    id = "net_sec_ch3",
                    title = "Bölüm 3: İletim Katmanı (Transport)",
                    emoji = "🚚",
                    chapters = listOf(
                        BookChapterItem("net_3_1", "3.1", "İletim Katmanı Hizmetleri & Çoğullama", "Multiplexing / Demultiplexing, Port numaraları"),
                        BookChapterItem("net_3_2", "3.2", "Bağlantısız İletim: UDP", "UDP segment başlığı, Checksum hesaplama ve kullanım alanları"),
                        BookChapterItem("net_3_3", "3.3", "Güvenilir Veri İletimi İlkeleri", "Stop-and-Wait, Pipelined protokoller: Go-Back-N (GBN) ve Selective Repeat (SR)"),
                        BookChapterItem("net_3_4", "3.4", "TCP Mimarisi ve Bağlantı Yönetimi", "TCP Segment yapısı, Sıra ve Onay numaraları, 3-Way Handshake, RTT tahmini"),
                        BookChapterItem("net_3_5", "3.5", "TCP Akış Denetimi (Flow Control)", "Alıcı penceresi (rwnd) ve tampon taşması koruması"),
                        BookChapterItem("net_3_6", "3.6", "Tıkanıklık Denetimi (Congestion Control)", "AIMD, Yavaş Başlama (Slow Start), Hızlı Yeniden İletim ve TCP Reno/Cubic")
                    )
                ),
                BookSection(
                    id = "net_sec_ch4",
                    title = "Bölüm 4: Ağ Katmanı (Network)",
                    emoji = "🗺️",
                    chapters = listOf(
                        BookChapterItem("net_4_1", "4.1", "Yönlendirme (Routing) vs İletme (Forwarding)", "Veri düzlemi ve denetim düzlemi ayrımı"),
                        BookChapterItem("net_4_2", "4.2", "Router Donanım Mimarisi", "Giriş portları, anahtarlama kumaşı, çıkış portları ve kuyruk yönetimi"),
                        BookChapterItem("net_4_3", "4.3", "İnternet Protokolü (IP) & Adresleme", "IPv4 datagramı, Alt ağlar (Subnetting), CIDR, NAT ve IPv6'ya geçiş"),
                        BookChapterItem("net_4_4", "4.4", "ICMP Protokolü", "Ping, Traceroute ve hata bildirimi mekanizmaları"),
                        BookChapterItem("net_4_5", "4.5", "Yönlendirme Algoritmaları", "Link-State (Dijkstra) ve Distance-Vector (Bellman-Ford) algoritmaları"),
                        BookChapterItem("net_4_6", "4.6", "Otonom Sistemler Arası Yönlendirme", "Intra-AS: OSPF ve RIP, Inter-AS: BGP (Border Gateway Protocol)")
                    )
                ),
                BookSection(
                    id = "net_sec_ch5",
                    title = "Bölüm 5: Bağlantı Katmanı ve Yerel Ağlar (Link Layer)",
                    emoji = "🔌",
                    chapters = listOf(
                        BookChapterItem("net_5_1", "5.1", "Bağlantı Katmanı Hizmetleri ve Hata Tespiti", "Parite bitleri, Checksum, CRC (Cyclic Redundancy Check)"),
                        BookChapterItem("net_5_2", "5.2", "Çoklu Erişim Protokolleri", "Kanal bölme, Rastgele erişim (CSMA/CD), Sıra alma protokolleri"),
                        BookChapterItem("net_5_3", "5.3", "Bağlantı Katmanı Adresleme & ARP", "MAC adresleri ve ARP (Address Resolution Protocol)"),
                        BookChapterItem("net_5_4", "5.4", "Ethernet ve Anahtarlar (Switches)", "Ethernet çerçevesi, Switch filtreleme ve kendi kendine öğrenme (Self-learning)"),
                        BookChapterItem("net_5_5", "5.5", "VLAN'lar ve Veri Merkezi Ağları", "Sanal LAN'lar, MPLS ve modern bulut veri merkezi mimarileri"),
                        BookChapterItem("net_5_6", "5.6", "Bir Web İsteğinin Uçtan Uca Anatomisi", "DHCP, DNS, ARP, TCP ve HTTP protokollerinin zincirleme çalışması")
                    )
                ),
                BookSection(
                    id = "net_sec_ch6",
                    title = "Bölüm 6: Kablosuz ve Mobil Ağlar",
                    emoji = "📶",
                    chapters = listOf(
                        BookChapterItem("net_6_1", "6.1", "Kablosuz Bağlantı Özellikleri & CDMA", "Kablosuz sinyal bozulması, gizli terminal problemi, CDMA"),
                        BookChapterItem("net_6_2", "6.2", "WiFi: 802.11 Kablosuz LAN'lar", "802.11 Mimarisi, CSMA/CA protokolü, Çerçeve yapısı"),
                        BookChapterItem("net_6_3", "6.3", "Hücresel Ağlar: 4G LTE ve 5G", "Hücresel mimari, baz istasyonları ve EPC çekirdek ağı"),
                        BookChapterItem("net_6_4", "6.4", "Mobilite Yönetimi & Mobile IP", "Gezici kullanıcı yönlendirme, Home/Foreign agent, GSM Handoff")
                    )
                ),
                BookSection(
                    id = "net_sec_ch7",
                    title = "Bölüm 7: Çoklu Ortam Ağları (Multimedia)",
                    emoji = "🎬",
                    chapters = listOf(
                        BookChapterItem("net_7_1", "7.1", "Video Akışı ve DASH", "Dinamik uyarlamalı akış (DASH) ve İçerik Dağıtım Ağları (CDN)"),
                        BookChapterItem("net_7_2", "7.2", "VoIP ve Gerçek Zamanlı İletişim", "Gecikme titreşimi (Jitter) giderme, Kayıp paket telafisi"),
                        BookChapterItem("net_7_3", "7.3", "RTP ve SIP Protokolleri", "Real-time Transport Protocol ve Session Initiation Protocol"),
                        BookChapterItem("net_7_4", "7.4", "Ağ Hizmet Kalitesi (QoS)", "Trafik şekillendirme, Token bucket ve DiffServ mimarisi")
                    )
                ),
                BookSection(
                    id = "net_sec_ch8",
                    title = "Bölüm 8: Ağ Güvenliği (Network Security)",
                    emoji = "🔒",
                    chapters = listOf(
                        BookChapterItem("net_8_1", "8.1", "Kriptografi İlkeleri", "Simetrik (AES) ve Asimetrik (RSA) şifreleme mekanizmaları"),
                        BookChapterItem("net_8_2", "8.2", "Mesaj Bütünlüğü ve Dijital İmzalar", "Kriptografik özetler (SHA-256), MAC ve Dijital sertifikalar"),
                        BookChapterItem("net_8_3", "8.3", "Güvenli Soket Katmanı: SSL / TLS", "TLS el sıkışması, simetrik anahtar türetme ve TCP şifreleme"),
                        BookChapterItem("net_8_4", "8.4", "Ağ Katmanı Güvenliği: IPsec ve VPN", "AH ve ESP protokolleri, Güvenlik Birlikleri (SA) ve Tünelleme"),
                        BookChapterItem("net_8_5", "8.5", "Güvenlik Duvarları (Firewalls) ve IDS/IPS", "Paket filtreleme, Durum bilgili denetim (Stateful inspection)")
                    )
                ),
                BookSection(
                    id = "net_sec_ch9",
                    title = "Bölüm 9: Ağ Yönetimi (Network Management)",
                    emoji = "📊",
                    chapters = listOf(
                        BookChapterItem("net_9_1", "9.1", "Ağ Yönetimi Altyapısı ve SNMP", "SNMP protokol mimarisi, MIB değişkenleri ve SMI yapısı")
                    )
                )
            )
        )
    )
}
