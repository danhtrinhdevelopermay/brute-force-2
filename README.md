# CPU/GPU Monitor - Android App

Ứng dụng Android giám sát CPU usage %, nhiệt độ CPU/GPU và trạng thái thermal theo thời gian thực.

## Tính năng

✅ **Giám sát CPU**
- Hiển thị % sử dụng CPU theo thời gian thực
- Đo nhiệt độ CPU
- Cảnh báo màu sắc theo nhiệt độ

✅ **Giám sát GPU**
- Đo nhiệt độ GPU
- Hiển thị trạng thái nhiệt
- ⚠️ **Lưu ý**: GPU usage % không khả dụng (Android không cung cấp API chính thức)

✅ **Thermal API**
- Trạng thái thermal của hệ thống (Normal, Light, Moderate, Severe, Critical)
- Thermal headroom (Android 11+)
- Tự động điều chỉnh màu sắc theo mức độ nhiệt

✅ **Pin**
- Hiển thị nhiệt độ pin

## Yêu cầu

- **Android 10 (API 29)** trở lên
- Quyền đọc thermal zones (được cấp tự động)

## Build APK với GitHub Actions

### Cách 1: Tự động build khi push code

1. Push code lên GitHub repository của bạn
2. GitHub Actions sẽ tự động build APK
3. Vào tab **Actions** → chọn workflow run mới nhất
4. Tải APK từ **Artifacts**

### Cách 2: Build thủ công

1. Vào tab **Actions** trong GitHub repository
2. Chọn workflow **Build Android APK**
3. Click **Run workflow** → **Run workflow**
4. Đợi build hoàn tất
5. Tải APK từ **Artifacts**

### Cách 3: Release với Tag

Để tạo release tự động:

```bash
git tag v1.0.0
git push origin v1.0.0
```

APK sẽ được tự động upload vào GitHub Releases.

## Build Local (Yêu cầu Android SDK & Gradle)

```bash
# Nếu có Gradle wrapper (./gradlew)
./gradlew assembleDebug
./gradlew assembleRelease

# Hoặc dùng Gradle được cài đặt
gradle assembleDebug
gradle assembleRelease

# APK output:
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release-unsigned.apk
```

**Lưu ý**: 
- Project này được thiết kế để build trên **GitHub Actions** (không cần Gradle wrapper JAR)
- GitHub Actions tự động download Gradle 8.2 và build APK
- Để build local: Cài Gradle 8.2+ hoặc run `gradle wrapper` để generate wrapper JAR
- **Khuyến nghị**: Push lên GitHub và để Actions build APK tự động (dễ nhất)

## Cài đặt

1. Tải APK từ GitHub Actions hoặc Releases
2. Bật "Cài đặt từ nguồn không xác định" trong Settings
3. Mở file APK và cài đặt

## Cấu trúc Project

```
.
├── app/
│   ├── build.gradle
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/monitor/cpugpu/
│   │   │   ├── MainActivity.kt
│   │   │   └── ThermalMonitor.kt
│   │   └── res/
│   │       ├── layout/
│   │       │   └── activity_main.xml
│   │       └── values/
├── .github/workflows/
│   └── build-apk.yml
├── build.gradle
└── settings.gradle
```

## API được sử dụng

### PowerManager Thermal API (Android 10+)
```kotlin
val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
val thermalStatus = powerManager.currentThermalStatus
```

### Thermal Headroom API (Android 11+)
```kotlin
val headroom = powerManager.getThermalHeadroom(10) // 0.0 - 1.0
```

### System Thermal Zones
```kotlin
// Đọc từ /sys/class/thermal/thermal_zone*/temp
val temp = readThermalZone("/sys/class/thermal/thermal_zone0/temp")
```

## Màu sắc cảnh báo

| Nhiệt độ | Màu | Trạng thái |
|----------|-----|------------|
| < 50°C | 🟢 Xanh lá | Normal |
| 50-60°C | 🟢 Xanh nhạt | Light |
| 60-70°C | 🟡 Vàng | Moderate |
| 70-80°C | 🟠 Cam | Severe |
| > 80°C | 🔴 Đỏ | Critical |

## Screenshots

App sẽ hiển thị:
- Card CPU với % usage và nhiệt độ
- Card GPU với nhiệt độ
- Card Thermal Status với trạng thái hệ thống
- Màu sắc tự động thay đổi theo mức độ nhiệt

## Lưu ý

⚠️ **Thermal zones khác nhau trên mỗi thiết bị**
- `thermal_zone0` có thể là CPU trên Samsung nhưng là Battery trên Xiaomi
- App sẽ thử đọc nhiều zones và chọn giá trị hợp lý

⚠️ **SELinux restrictions**
- Một số thiết bị có thể chặn đọc `/sys/class/thermal/`
- Trong trường hợp đó, app sẽ hiển thị "N/A"

⚠️ **Permissions**
- App không cần permissions đặc biệt
- PowerManager Thermal API hoạt động mà không cần khai báo permissions

⚠️ **GPU Usage Limitation**
- Android **không cung cấp API chính thức** để đọc GPU usage %
- App chỉ có thể đo nhiệt độ GPU qua thermal zones
- Để có GPU usage %, cần root access hoặc vendor-specific APIs (không khả thi cho app thông thường)
- Các app monitor GPU trên Play Store thường chỉ ước lượng hoặc yêu cầu root

## License

MIT License - Free to use and modify

## Hỗ trợ

Nếu gặp vấn đề:
1. Kiểm tra Android version >= 10
2. Xem logs trong Android Studio Logcat
3. Kiểm tra GitHub Actions logs nếu build fail

## Đóng góp

Pull requests được chào đón! Để thay đổi lớn, hãy mở issue trước.
