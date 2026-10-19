# Günlük ekranı

Günlük kullanım için dört bölüm: saat özeti, seçili hobiler, su, günlük rutinler. Büyük tanıtım alanı, puan veya seri baskısı yok. Mevcut uygulamanın renk paleti ve Material bileşenleri kullanılır; ek görsel servisine bağımlı değildir.

- Son yedi gün şeridi; eski haftalara geçiş ve Bugün’e dönüş. Su ve işaretlemeler seçilen gün için kaydedilir.
- Huawei Sağlık kartında adım, aktif kalori, egzersiz oturum süresi ve uyku. Kayıt yoksa `—`; gerçek sıfırdan ayrı gösterilir. Bağlantı ayarları kart içindeki düğmeden açılır.
- Kitap ve İngilizce varsayılan olarak görünür. Kart numarası ve İspanyolca Düzenle ile eklenebilir. Clawssary açma düğmesi ayrı eylemdir.
- Su: 250 ml ekle/geri al, mevcut günlük hedef, hedef düzenleme ve mevcut 15.00 hatırlatması.
- Beslenme, yürüyüş/koşu ve uyku düzeni kişisel işaretlemelerdir. Saat verileri bu kayıtları kendiliğinden değiştirmez.

`winter_arc_daily_tracker` anahtarları korunur. Hobiyi gizlemek eski kayıtlarını silmez. Ekran geri geldiğinde ve tarih değiştiğinde günlük bilgiler yenilenir. Sağlık verisi ekran açılışında, gün seçiminde, uygulamaya dönüşte ve kullanıcı isteğiyle okunur; sürekli arka plan sorgusu yoktur.

## Doğrulama

`DailyScreenTest`: geçmiş gün ile bugünün su/hobi kayıtlarını ayrı tutma, eksik sağlık verisini sıfır göstermeme, önbellek etiketi ve açık tema, küçük ekranda büyük yazıyla kontrollere ulaşma.

`SimplifiedNavigationTest`: ana gezinme ve hobi seçimi. `HealthRoutineRepositoryTest`: eski kayıtlarla uyum. `HealthSyncManagerTest`: kaynak, tarih, süre, kısmi veri ve önbellek hesaplamaları.

Test görüntüleri `app/build/previews/winterarc-daily*.png` altında üretilir. Açık tema görüntüsündeki sayılar test verisidir; kullanıcıya ait gerçek saat verisi değildir. Huawei erişiminin cihazda doğrulanan sınırları için [bağlantı notlarına](huawei-health-sync.md) bakın.
