# Huawei Sağlık bağlantısı — arşiv

> **19 Ekim 2026:** Kullanıcının isteğiyle Huawei entegrasyonundan vazgeçildi. Günlük ekranda yürüyüş/koşu, egzersiz, uyku ve beslenme elle işaretlenir. Health Connect izinleri ve günlük eşitleme akışı kaldırıldı. Geliştirici hesabı açılması veya dosya indirilmesi gerekmiyor. Güncel kullanım: [Günlük ekranı](daily-screen.md).
>
> Aşağıdaki metin önceki bağlantı denemesinin tarihsel notlarıdır; mevcut uygulama için kurulum talimatı değildir. Eski okuyucu kodu günlük ekrana bağlı değildir; cihaz tanısı testi kaldırılmıştır.

Winter Arc, Health Connect üzerinden **yalnızca `com.huawei.health` tarafından yazılan** adım, uyku, aktif kalori ve egzersiz oturumu kayıtlarını okur. Google Fit entegrasyonu, Health Sync köprüsü, Samsung Health, Android'in kendi adım sayacı ve elle girilmiş eski toplamlar bu sonuçlara dahil edilmez. Health Connect burada telefonun yerel veri paylaşım katmanıdır; Google Fit hesabı gerektirmez.

## Telefon kurulumu

1. Huawei Sağlık'ta saatinizi eşitleyin; adım ve uyku verisinin Huawei uygulamasında güncel olduğunu kontrol edin.
2. Huawei Sağlık > Ben > Gizlilik yönetimi > Veri paylaşımı ve yetkilendirme bölümünde Health Connect paylaşımını etkinleştirin. Menü isimleri sürüm/bölgeye göre değişebilir.
3. Health Connect uygulama izinlerinde Huawei Sağlık'a **desteklediği veri türlerini yazma**, Winter Arc'a **adım, uyku, aktif kalori ve egzersiz okuma** izinlerini verin. Yeniden kurulumdan sonra izinleri tekrar kontrol edin.
4. Winter Arc'ın Günlük ekranında Huawei Sağlık kartının bağlantı düğmesine dokunun ve **İzin ver ve kontrol et** düğmesini kullanın. Huawei Sağlık'tan Winter Arc'a geri dönüldüğünde de otomatik okuma yapılır.

Bir uygulamanın yüklü olması veya yazma izni bulunması, veri aktardığını kanıtlamaz. Health Connect'teki adım ve uyku kayıtlarının kaynak uygulaması **Huawei Sağlık** olmalıdır. Kaynak Health Sync veya Google Fit görünüyorsa bu kayıtlar özellikle kullanılmaz. Huawei uygulamasındaki toplamla fark varsa önce Huawei'nin Health Connect'e aktardığı kayıtları karşılaştırın.

Telefonunuzdaki Huawei Sağlık sürümünde/bölgesinde Health Connect paylaşımı bulunmuyorsa uygulama bunu tek başına açamaz. Android yazma izinleri, Huawei uygulamasındaki aktarımın etkin olduğunu kanıtlamaz. Health Connect kullanmadan doğrudan erişim için ayrı Huawei Health Kit/AppGallery Connect kurulumu ve ilgili okuma kapsamlarının Huawei tarafından onaylanması gerekir; bu projede doğrudan HMS bağlantısı yoktur.

## Mevcut cihazda doğrulanan engel — 14 Ekim 2026

Android 16 ve Huawei Sağlık 17.0.7.310 ile yapılan kontrolde Huawei Sağlık'ın adım/uyku yazma izinleri ve Winter Arc'ın okuma izinleri açıktı. Buna rağmen Huawei kaynaklı bugünkü kayıtlar okunamadı. Huawei Sağlık'ın **Veri paylaşımı ve yetkilendirme** ekranında Health Connect bağlantısı görünmüyordu. Dolayısıyla yalnızca Android izinlerini açmak aktarımı başlatmadı.

Health Sync kullanılmayacak ve Huawei geliştirici kaydı/Health Kit onayı henüz yok. Bu durumda otomatik veri aktarımı **henüz çalışır durumda değildir**. Kaynak filtreleme, hesaplama, hata görünürlüğü ve önbellek düzeltmeleri uygulanmıştır; bunlar eksik Huawei veri erişiminin yerini tutmaz.

## Mevcut cihazda yeniden doğrulama — 19 Ekim 2026

- Huawei Sağlık: **17.0.8.300**.
- Winter Arc: dört Health Connect okuma izni de verilmiş durumda.
- Huawei Sağlık: `WRITE_STEPS`, `WRITE_SLEEP`, `WRITE_ACTIVE_CALORIES_BURNED` izinleri verilmiş. Bu sürüm **`WRITE_EXERCISE` bildirmiyor**.
- Salt okunur cihaz testinde 13–19 Ekim aralığındaki yedi günün hiçbirinde Huawei kaynaklı dört veri türü bulunamadı. Gece yarısı sonrası boş bir günü aktarım hatasıyla karıştırmamak için tamamlanmış günler de kontrol edildi. `inspectDailyHuaweiMetrics`: `steps=false; sleep=false; activeCalories=false; exercise=false; granted=4/4`.
- Kullanıcı Huawei Developer/Health Kit uygulama kaydı oluşturmadığını doğruladı. Doğrudan HMS erişimi bu nedenle henüz uygulanıp doğrulanabilir durumda değil. Health Connect okuyucusunun tamamlanmış olması, gerçek Huawei aktarımının çalıştığı anlamına gelmez.

### Doğrudan Huawei Health Kit için gereken kayıt

1. Huawei Developer/AppGallery Connect'te bir Android uygulaması oluşturun. Paket adı: `com.aistudio.skilltree.cegraph`.
2. Bu projenin `debug.keystore` imzasına ait SHA-256 parmak izini ekleyin:

   ```text
   19:69:58:0A:16:F1:50:19:44:24:FD:3A:54:DA:0C:03:C6:84:82:7E:7A:FC:E6:B6:F2:56:37:74:7D:68:99:63
   ```

   19 Ekim 2026 APK’sından `apksigner verify --print-certs` ile doğrulandı; eski kurulumun imzası farklıydı. Bu yalnızca mevcut debug imzasıdır. Release sürümünde kullanılan imzanın parmak izi ayrıca kaydedilmelidir.
3. Account Kit ve Health Kit hizmetlerine başvurun; **adım**, **uyku**, **kalori** ve **aktivite/egzersiz özeti okuma** kapsamları için erişim isteyin. Sadece adım onayı, uyku erişimini sağlamaz. Gerekli geliştirici hesap türü ve veri kapsamı uygunluğunu Huawei konsolunda doğrulayın.
4. App ID ve onaylı kapsamlar hazır olduğunda Account Kit kullanıcı giriş/yetkilendirme akışı ve Health Kit üzerinden Huawei Sağlık veri okuma uygulanabilir. Kullanıcı kendi Huawei hesabıyla onay vermelidir; hesap parolası kaynak koda eklenmez.

## Hesaplama ve veri bulunamaması

- Adım: seçilen günün yerel 00:00 ile gün sonu/okuma anı arasında, Huawei kaynağına filtrelenmiş resmi aggregate API sonucu. Ham kayıt toplamıyla maksimum alınmaz ve geleceğe zaman tamponu eklenmez.
- Uyku: yerel tarihle **seçilen gün tamamlanan** oturumlar (gece uykusu ve varsa şekerlemeler). Önceki güne ait uyku bugüne taşınmaz; henüz bitmemiş kayıtlar sayılmaz.
- Uyku aşamaları varsa uyunan/evresi bilinmeyen aralıklar birleştirilir, uyanık ve yatak dışı aralıklar çıkarılır. Aşama içermeyen oturumlarda oturum süresi kullanılır. Çakışan/tekrarlanan aralıklar bir kez sayılır ve saniyeler toplamdan sonra dakikaya çevrilir.
- Aktif kalori: `ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL`, Huawei kaynağına filtreli, kcal. Dinlenme/bazal kalori ve adımlardan türetilmiş tahmin eklenmez.
- Egzersiz: sayfalı `ExerciseSessionRecord` okuması; tamamlanmış Huawei oturumları. Süre seçilen yerel güne kırpılır, yinelenen/çakışan aralıklar bir kez sayılır. Gösterilen süre molalar dahil oturum süresidir; aktif hareket süresi olduğu iddia edilmez. Adım sayısından egzersiz yapıldığı çıkarılmaz.
- Geçmiş günler de doğrudan okunabilir; Health Connect’in geçmiş okuma sınırı geçerlidir. Arka plan veya sınırsız geçmiş erişim izni istenmez.
- Eksik veri, gerçek sıfır adım ile aynı kabul edilmez. İzin ve veri hataları ekranda gösterilir. Başarısız okumada mevcut doğrulanmış önbellek korunur ve eski okumanın gösterildiği belirtilir. Kısmi okumada da diğer alanlar silinmez; ilgili alanların önceki okumadan kaldığı ayrıca gösterilir.
- Eski karma kaynak önbelleği ayrı tutulur; Huawei etiketi altında yeniden kullanılmaz. Alışkanlık hedefleri doğrulanmış Huawei değerlerinden hesaplanır.

## Doğrulama

```sh
./gradlew :app:testDebugUnitTest --tests com.example.HealthSyncManagerTest --tests com.example.DailyScreenTest :app:assembleDebug :app:assembleDebugAndroidTest
adb shell am instrument -w -e class com.example.HuaweiStepsDiagnosticTest#inspectDailyHuaweiMetrics com.aistudio.skilltree.cegraph.test/androidx.test.runner.AndroidJUnitRunner
```

Cihaz testi yalnızca okur; Health Connect veya Huawei Sağlık’a kayıt yazmaz.

Testler kaynak dışlama, eski veya gelecekte biten oturumlar, yerel uyanma tarihi, gece yarısı, yinelenen ve bölünmüş oturumlar, uyanıklık aşamaları, saniye yuvarlama ve önbellek izolasyonunu kapsar. Gerçek telefon doğrulamasında Huawei Sağlık ekranı ile Health Connect'teki Huawei kayıtları ayrıca karşılaştırılmalıdır.

## Kaynaklar

- [Android: sağlık veri türleri ve izinleri](https://developer.android.com/health-and-fitness/health-connect/data-types)
- [Android: aggregate sorgularını veri kaynağına filtreleme](https://developer.android.com/health-and-fitness/health-connect/aggregate-data#filter-by-data-origin)
- [Android: kayıt okuma ve sayfalama](https://developer.android.com/health-and-fitness/health-connect/read-data)
- [Huawei: Health Kit başvurusu, uygulama kurulumu ve kullanıcı yetkilendirmesi](https://developer.huawei.com/consumer/en/codelab/MyHealth/)
