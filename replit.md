# CPU/GPU Monitor - Android App

## Overview

This is an Android mobile application that monitors real-time CPU and GPU metrics including usage percentages, temperatures, thermal states, and battery temperature. The app targets Android 10+ (API 29+) and provides color-coded visual feedback based on thermal levels. The project is designed to be built primarily through GitHub Actions CI/CD rather than local development environments.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Mobile Platform Architecture
- **Target Platform**: Native Android application
- **Minimum SDK**: API 29 (Android 10)
- **Target SDK**: API 34
- **Build System**: Gradle 8.2+ (wrapper JAR excluded from repo)
- **Language**: Kotlin (AndroidX + Material Design)

**Rationale**: Android 10+ requirement ensures access to Thermal API for accurate thermal monitoring. API 34 targets modern Android versions for latest features and security updates.

### Build & Deployment Strategy
- **Primary Build Method**: GitHub Actions CI/CD pipeline
- **Local Development**: Optional, supported but not required
- **Binary Management**: gradle-wrapper.jar intentionally excluded from repository to reduce size
- **Artifact Distribution**: APK distributed via GitHub Actions Artifacts and GitHub Releases

**Rationale**: Cloud-based build pipeline eliminates need for local Android SDK setup, reduces repository bloat (60KB wrapper JAR excluded), and provides consistent build environment. Developers can contribute code without full Android development environment.

**Alternatives Considered**:
- Committing gradle-wrapper.jar: Rejected to keep repository lean
- Requiring local builds: Rejected to lower barrier to entry for contributors

**Pros**:
- No local Android SDK setup required for contributors
- Consistent build environment
- Automated APK generation on every push
- Smaller repository size

**Cons**:
- Developers wanting local builds must generate wrapper or install Gradle manually
- Build feedback requires pushing to GitHub (slower iteration for some workflows)

### Monitoring Capabilities

**CPU Monitoring**:
- Real-time CPU usage percentage tracking
- CPU temperature measurement via thermal zones
- Color-coded temperature warnings

**GPU Monitoring**:
- GPU temperature measurement
- Thermal status display
- **Limitation**: GPU usage percentage unavailable (Android platform limitation - no official API)

**System Thermal Monitoring**:
- Thermal API integration for system thermal states (Normal, Light, Moderate, Severe, Critical)
- Thermal headroom monitoring (Android 11+ devices)
- Dynamic UI color adjustment based on thermal level

**Battery Monitoring**:
- Battery temperature display

**Rationale**: Focus on metrics available through official Android APIs. GPU usage percentage explicitly noted as unavailable due to platform constraints rather than implementation gaps.

### Permissions Model
- Thermal zone read permissions granted automatically by system
- No special user permissions required

**Rationale**: Leverages Android's built-in thermal monitoring APIs that don't require explicit user permission grants, providing seamless user experience.

## External Dependencies

### Build Tools
- **Gradle 8.2+**: Build automation and dependency management
- **Android SDK API 34**: Android platform libraries and compilation tools
- **JDK 17+**: Java Development Kit for compilation

### CI/CD Infrastructure
- **GitHub Actions**: Automated build pipeline with Android SDK and Gradle 8.2 auto-download
- **android-actions/setup-android@v3**: Automatic Android SDK setup and license acceptance
- **GitHub Releases**: APK distribution via git tags (e.g., v1.0.0)
- **GitHub Artifacts**: Debug and Release APK storage for every workflow run

**Workflow Details**:
- JDK 17 (Temurin distribution)
- Android SDK auto-installed with platform-tools
- Gradle 8.2 downloaded from services.gradle.org (bypasses wrapper JAR requirement)
- Runs on ubuntu-latest runners

### Android Platform APIs
- **PowerManager.ThermalAPI** (API 29+): System thermal state monitoring (Normal through Emergency levels)
- **getThermalHeadroom()** (API 30+): Predictive thermal forecasting for workload management
- **Thermal Zones** (/sys/class/thermal/): Direct temperature sensor access for CPU/GPU
- **Runtime.exec()**: Shell command execution for reading thermal zone files
- **Battery Manager**: Battery temperature via /sys/class/power_supply/battery/temp

### Android Dependencies (from app/build.gradle)
- androidx.core:core-ktx:1.12.0
- androidx.appcompat:appcompat:1.6.1
- com.google.android.material:material:1.11.0
- androidx.constraintlayout:constraintlayout:2.1.4
- androidx.lifecycle:lifecycle-runtime-ktx:2.7.0

**Note**: No external third-party SDKs, analytics services, or cloud backends. Application is fully self-contained using only Android platform APIs and AndroidX libraries.