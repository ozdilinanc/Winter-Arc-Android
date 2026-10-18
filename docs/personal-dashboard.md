# Kişisel kullanım düzeni

- Alanlarım: Okul, Yazılım, Ürettiklerim, Hobiler.
- Yazılım: Backend, Android, BTK kursları; teknik genel kültür ve DevOps ayrı Konu arşivinde.
- Ürettiklerim: GitHub proje defteri, uygulama listesi (Clawssary yayında), mevcut Medium içeriği.
- Hobiler: Kitaplar, boş başlayan kart numarası defteri, anime/manga, İngilizce, İspanyolca.
- Günlük: Sağlık rutinleri ve seçilebilir hobiler. Health Connect, saat verileri, su hedefi ve su hatırlatıcılarına ayrıntılardan erişilir. Bu ayrıntılarda eski rutinler ve dopamin ekranı gösterilmez.

MediumRoadmapSeed, kişisel kütüphane ve okul verileri değiştirilmez. Önceden kaydedilmiş konu durumları ve kişisel proje verileri silinmez. Yeni proje/uygulama/numara defterleri ayrı tercihlerde tutulur. Kitap okuma ve İngilizce günlük kayıtları eski anahtarları kullanır; su kaydı ve hedefi ortak kalır.

## BTK kontrolü — 19 Ekim 2026

Kaynak: https://kariyerrehberi.btkakademi.gov.tr/gelisim_programlari/yazilim-gelistirme.html

Resmî rehberde Web API, Kotlin, Android temel/ileri, tasarım desenleri, Docker, API testi ve yazılım testine giriş yer alıyor. Kurs bağlantıları BtkCourseCatalog.kt içinde. Kotlin ve ileri Android için resmî rehbere; PostgreSQL için resmî kataloğa bağlantı veriliyor. Doğrudan kurs bağlantısı bulunmayan kayıtlar kurs sayfası gibi sunulmuyor.

- Android temelleri: https://www.btkakademi.gov.tr/portal/tr/course/30370
- PostgreSQL katalog kaydı: https://www.btkakademi.gov.tr/portal/en/catalog?categoryId=3204
- Docker: https://www.btkakademi.gov.tr/portal/course/docker-temelleri-21907

Önceki listede eğitmen, süre, tüm sınavlarda 70 puan, tüm sertifikaların e-Devlet'e işlenmesi ve uzmanlık diploması hakkında genellenmiş iddialar vardı. Bunlar doğrulanmış kurs bilgisi sayılmadığı için kaldırıldı. Listede yer almayan eski eğitimlerin bulunmadığı sonucuna varılmıyor; yalnızca bu turda doğrulanan ve backend/mobil odağına uyanlar öne çıkarılıyor. Önceki ilerleme anahtarları silinmiyor.

## MyAnimeList

Mevcut bağlantı herkese açık listeleri load.json üzerinden tek yönlü içeri alıyor. OAuth oturumu veya hesaba yazma yok; arayüz bunu açıkça belirtir. Resmî hesap bağlantısını tamamlamak için kullanıcıya ait API uygulama kaydı / Client ID ve kayıtlı yönlendirme adresi gereklidir. Şifre veya client secret istenmez.

Resmî başvuru ve referanslar:
- https://myanimelist.net/apiconfig
- https://myanimelist.net/apiconfig/references/authorization
- https://myanimelist.net/apiconfig/references/api/v2

Bu turda resmî referans sayfaları tarayıcı aracıyla alınamadı. OAuth bağlantısı uygulanmadı; kullanıcı ozdilinanc13 hesabını paylaştı ve API kaydı oluşturmadığını belirtti. Kullanıcı adı varsayılan olarak hazır; OAuth için API uygulama kaydı bekleniyor. Kullanıcı adıyla mevcut içe aktarma korunur. Clawssary mağaza sayfası da tarayıcıda açılamadı; uygulama adı ve paket adresi kullanıcının beyanı ve mevcut Android entegrasyonundan alındı.
