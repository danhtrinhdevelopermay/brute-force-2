# Gradle Wrapper

## Missing gradle-wrapper.jar?

Đây là expected behavior. Project này được thiết kế để build trên **GitHub Actions** mà không cần commit gradle-wrapper.jar (60KB binary file).

## How it works

### On GitHub Actions
Workflow tự động download Gradle 8.2 distribution và build APK mà không cần wrapper JAR.

### Local Development (Optional)

Nếu bạn muốn build local, có 2 cách:

#### Option 1: Generate wrapper JAR
```bash
gradle wrapper --gradle-version 8.2
```

Lệnh này sẽ tạo file `gradle-wrapper.jar` trong thư mục này.

#### Option 2: Use system Gradle
```bash
gradle assembleDebug
gradle assembleRelease
```

Dùng Gradle được cài đặt trên hệ thống (cần Gradle 8.2+).

## Why no JAR in repo?

- Giảm repo size (tránh commit 60KB binary)
- GitHub Actions tự động download distribution
- Wrapper JAR không cần thiết cho CI/CD workflow
- Developer có thể generate local nếu cần

## Files present

✅ `gradle-wrapper.properties` - Wrapper configuration  
✅ `gradlew` (root) - Wrapper script for Unix/Linux/Mac  
❌ `gradle-wrapper.jar` - Not needed for GitHub Actions (can be generated locally)

## For Contributors

Nếu bạn cần build local:
1. Cài Gradle 8.2: https://gradle.org/install/
2. Hoặc run `gradle wrapper` để generate JAR
3. Hoặc push lên GitHub và let Actions build APK

Không cần commit gradle-wrapper.jar vào repo!
