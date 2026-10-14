# Huawei Sağlık adım ve uyku bağlantısı

Winter Arc, Health Connect üzerinden **yalnızca `com.huawei.health` tarafından yazılan** adım ve uyku kayıtlarını okur. Google Fit entegrasyonu, Health Sync köprüsü, Samsung Health, Android'in kendi adım sayacı ve elle girilmiş eski toplamlar bu sonuçlara dahil edilmez. Health Connect burada telefonun yerel veri paylaşım katmanıdır; Google Fit hesabı gerektirmez.

## Telefon kurulumu

1. Huawei Sağlık'ta saatinizi eşitleyin; adım ve uyku verisinin Huawei uygulamasında güncel olduğunu kontrol edin.
2. Huawei Sağlık > Ben > Gizlilik yönetimi > Veri paylaşımı ve yetkilendirme bölümünde Health Connect paylaşımını etkinleştirin. Menü isimleri sürüm/bölgeye göre değişebilir.
3. Health Connect uygulama izinlerinde Huawei Sağlık'a **adım ve uyku yazma**, Winter Arc'a **adım ve uyku okuma** izinlerini verin. Yeniden kurulumdan sonra izinleri tekrar kontrol edin.
4. Winter Arc'ın günlük takip ekranında eşitleme kartına dokunun ve **İzin ver ve yenile** düğmesini kullanın. Huawei Sağlık'tan Winter Arc'a geri dönüldüğünde de otomatik okuma yapılır.

Bir uygulamanın yüklü olması veya yazma izni bulunması, veri aktardığını kanıtlamaz. Health Connect'teki adım ve uyku kayıtlarının kaynak uygulaması **Huawei Sağlık** olmalıdır. Kaynak Health Sync veya Google Fit görünüyorsa bu kayıtlar özellikle kullanılmaz. Huawei uygulamasındaki toplamla fark varsa önce Huawei'nin Health Connect'e aktardığı kayıtları karşılaştırın.

Huawei'nin mevcut gizlilik açıklaması Android'de Health Connect'e fitness ve uyku paylaşımını tanımlar. Telefonunuzdaki sürüm/bölgede bu özellik bulunmuyorsa uygulama bunu tek başına açamaz. Health Connect kullanmadan doğrudan erişim için ayrı Huawei Health Kit/AppGallery Connect kurulumu ve ilgili okuma kapsamlarının Huawei tarafından onaylanması gerekir; bu projede doğrudan HMS bağlantısı yoktur.

## Hesaplama ve veri bulunamaması

- Adım: yerel saatle bugün 00:00 ile okuma anı arasında, Huawei kaynağına filtrelenmiş resmi aggregate API sonucu. Ham kayıt toplamıyla maksimum alınmaz ve geleceğe zaman tamponu eklenmez.
- Uyku: yerel tarihle **bugün tamamlanan** oturumlar (gece uykusu ve varsa şekerlemeler). Önceki güne ait uyku bugüne taşınmaz; henüz bitmemiş kayıtlar sayılmaz.
- Uyku aşamaları varsa uyunan/evresi bilinmeyen aralıklar birleştirilir, uyanık ve yatak dışı aralıklar çıkarılır. Aşama içermeyen oturumlarda oturum süresi kullanılır. Çakışan/tekrarlanan aralıklar bir kez sayılır ve saniyeler toplamdan sonra dakikaya çevrilir.
- Eksik veri, gerçek sıfır adım ile aynı kabul edilmez. İzin ve veri hataları ekranda gösterilir. Başarısız okumada mevcut doğrulanmış önbellek korunur ve eski okumanın gösterildiği belirtilir.
- Eski karma kaynak önbelleği ayrı tutulur; Huawei etiketi altında yeniden kullanılmaz. Alışkanlık hedefleri doğrulanmış Huawei değerlerinden hesaplanır.

## Doğrulama

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests com.example.HealthSyncManagerTest :app:assembleDebug --no-configuration-cache
```

Testler kaynak dışlama, eski veya gelecekte biten oturumlar, yerel uyanma tarihi, gece yarısı, yinelenen ve bölünmüş oturumlar, uyanıklık aşamaları, saniye yuvarlama ve önbellek izolasyonunu kapsar. Gerçek telefon doğrulamasında Huawei Sağlık ekranı ile Health Connect'teki Huawei kayıtları ayrıca karşılaştırılmalıdır.

## Kaynaklar

- [Huawei Sağlık veri paylaşımı açıklaması](https://privacy-dre.dbankcdn.com/agreement/scope/huawei/health/privacy-statement/hkgroup_b0_20260910_0/default/privacy-statement-en-us.htm)
- [Android: aggregate sorgularını veri kaynağına filtreleme](https://developer.android.com/health-and-fitness/health-connect/aggregate-data#filter-by-data-origin)
- [Android: kayıt okuma ve sayfalama](https://developer.android.com/health-and-fitness/health-connect/read-data)
