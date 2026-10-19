# Günlük ekranı

Günlük kullanım için üç bölüm: seçili hobiler, günlük rutinler ve su. Mevcut uygulamanın renk paleti ve Material bileşenleri kullanılır.

- Son yedi gün şeridi; eski haftalara geçiş ve Bugün’e dönüş. Su ve işaretlemeler seçilen gün için kaydedilir.
- Kitap ve İngilizce varsayılan olarak görünür. Kart numarası ve İspanyolca Düzenle ile eklenebilir. Clawssary açma düğmesi ayrı eylemdir.
- Sağlıklı beslenme, yürüyüş/koşu, egzersiz ve düzenli uyku satırlarına dokunarak tik koyulur veya kaldırılır.
- Su: 250 ml ekle/geri al, mevcut günlük hedef, hedef düzenleme ve 15.00 hatırlatması.

`winter_arc_daily_tracker` anahtarları korunur. Egzersiz için `health_exercise_<tarih>` anahtarı eklenmiştir. Hobiyi gizlemek eski kayıtlarını silmez. Ekran geri geldiğinde ve tarih değiştiğinde günlük bilgiler yenilenir.

Huawei bağlantısı kullanıcı isteğiyle kaldırıldı. Günlük ekranında sağlık verisi sorgusu, eşitleme kartı veya bağlantı kurulumu bulunmaz; uygulama Health Connect okuma izni bildirmez. Sayısal adım, kalori ve uyku süresi saatten takip edilir.

## Doğrulama

`DailyScreenTest`: geçmiş gün ile bugünün su/hobi kayıtlarını ayrı tutma, egzersiz tikini kaydetme/geri alma ve eski rutin kayıtlarını koruma, açık tema, küçük ekranda büyük yazıyla kontrollere ulaşma.

`SimplifiedNavigationTest`: ana gezinme ve hobi seçimi. `HealthRoutineRepositoryTest`: eski kayıtlarla uyum.

Test görüntüleri `app/build/previews/winterarc-daily*.png` altında üretilir; kişisel saat verisi içermez.
