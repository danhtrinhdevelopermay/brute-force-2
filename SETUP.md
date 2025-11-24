# Setup Instructions

## Build APK qua GitHub Actions (Khuyến nghị)

Đây là cách dễ nhất để build APK mà không cần setup Android SDK local:

### Bước 1: Tạo GitHub Repository

```bash
git init
git add .
git commit -m "Initial Android CPU/GPU Monitor project"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/cpu-gpu-monitor.git
git push -u origin main
```

### Bước 2: GitHub Actions sẽ tự động build

- Mỗi lần push code, workflow sẽ tự động chạy
- Vào tab **Actions** trong repo để xem tiến trình
- Download APK từ **Artifacts** sau khi build xong

### Bước 3: Tạo Release với Tag (Optional)

```bash
git tag v1.0.0
git push origin v1.0.0
```

APK sẽ được tự động publish vào **Releases**.

---

## Build Local với Android SDK

### Yêu cầu

- JDK 17 hoặc cao hơn
- Android SDK (API 34)
- Gradle 8.2+

### Cài đặt

1. **Cài Android SDK**
   ```bash
   # Download Android Command Line Tools
   wget https://dl.google.com/android/repository/commandlinetools-linux-latest.zip
   unzip commandlinetools-linux-latest.zip -d ~/Android/Sdk
   
   # Install SDK
   ~/Android/Sdk/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   ```

2. **Generate Gradle Wrapper** (một lần duy nhất)
   ```bash
   gradle wrapper --gradle-version 8.2
   ```

3. **Build APK**
   ```bash
   ./gradlew assembleDebug
   # Output: app/build/outputs/apk/debug/app-debug.apk
   ```

---

## Troubleshooting

### "gradle-wrapper.jar not found"

**GitHub Actions**: Wrapper được tự động download bởi `setup-gradle@v4` action

**Local build**: Chạy một trong các lệnh sau:
```bash
# Option 1: Generate wrapper
gradle wrapper --gradle-version 8.2

# Option 2: Download wrapper JAR manually
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://github.com/gradle/gradle/raw/v8.2.1/gradle/wrapper/gradle-wrapper.jar
```

### "Android SDK not found"

Set ANDROID_HOME environment variable:
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

---

## Workflow Details

GitHub Actions workflow (`.github/workflows/build-apk.yml`) sử dụng:

- **JDK 17** (Temurin distribution)
- **gradle/actions/setup-gradle@v4** - Latest official Gradle action
- **Automatic wrapper download** - Không cần commit gradle-wrapper.jar
- **Artifact upload** - Debug và Release APKs
- **Tag-based releases** - Auto-publish khi push tag `v*`

---

## Recommended Workflow cho Developer

1. Code trên Replit hoặc local editor
2. Push lên GitHub
3. GitHub Actions build APK tự động
4. Download APK từ Actions artifacts
5. Test trên Android device
6. Lặp lại cho các updates tiếp theo

**Lợi ích**: Không cần setup Android SDK phức tạp trên máy local!
