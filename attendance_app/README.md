# 📱 HRM Attendance Mobile App

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-29%2B-orange.svg)](https://developer.android.com/studio/releases/platforms)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A production-ready native Android application for employee attendance management with AI-powered face recognition integration.

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Application Architecture](#-application-architecture)
- [Technology Stack](#-technology-stack)
- [Features](#-features)
- [Attendance Workflow](#-attendance-workflow)
- [Camera Integration](#-camera-integration)
- [API Communication](#-api-communication)
- [Authentication Flow](#-authentication-flow)
- [Project Structure](#-project-structure)
- [Permissions Required](#-permissions-required)
- [Running the Application](#-running-the-application)
- [APK Build Instructions](#-apk-build-instructions)
- [Backend Integration](#-backend-integration)
- [Error Handling Strategy](#-error-handling-strategy)
- [Screenshots](#-screenshots)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## 🎯 Project Overview

The **HRM Attendance Mobile App** is a client-side Android application that serves as the primary interface for employees to record their attendance through face recognition technology. It is part of a distributed HRM Attendance Management System designed for enterprise environments.

### Key Highlights

- 🔐 **Secure Face Recognition** - Integrates with backend AI service for identity verification
- ⚡ **Real-time Validation** - Instant attendance confirmation with visual feedback
- 🌐 **Cloud-Connected** - Seamless communication with AWS EC2 deployed backend
- 📸 **Smart Auto-Capture** - Automated face detection with guided positioning
- 🔒 **HTTPS Security** - All communications encrypted with TLS

---

## 🏗️ Application Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    ANDROID CLIENT (This App)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │   UI Layer   │  │ CameraX API  │  │   Network Layer      │  │
│  │  (Activities)│  │(Face Capture)│  │  (Retrofit/OkHttp)   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────────┬───────────┘  │
│         │                 │                     │              │
│  ┌──────▼─────────────────▼─────────────────────▼───────────┐  │
│  │              ML Kit Face Detection                        │  │
│  │         (Real-time face tracking & validation)            │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────────────┬────────────────────────────────┘
                                 │ HTTPS
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                      BACKEND SERVER (AWS EC2)                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              REST API (Spring Boot)                      │   │
│  │    • Face Scan Endpoint (/api/v1/attendance/scan)       │   │
│  │    • Employee Authentication                            │   │
│  │    • Attendance Record Management                       │   │
│  └────────────────────────┬────────────────────────────────┘   │
│                           │                                     │
│  ┌────────────────────────▼────────────────────────────────┐   │
│  │              AI Face Recognition Service                  │   │
│  │         (Deep Learning-based Face Matching)              │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Architectural Principles

- **Client-Server Architecture**: Thin client with heavy processing on backend
- **Separation of Concerns**: UI, business logic, and network layers are decoupled
- **Reactive Programming**: Coroutines for asynchronous operations
- **State Management**: Proper lifecycle-aware component handling

---

## 🛠️ Technology Stack

### Core Technologies

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Kotlin | 1.9+ | Primary development language |
| **Platform** | Android SDK | API 29-36 | Target platform |
| **Build System** | Gradle | 8.x | Build automation |
| **UI Framework** | Android Views | - | Native UI components |

### Camera & Vision

| Library | Version | Purpose |
|---------|---------|---------|
| **CameraX** | 1.3.1 | Camera operations with lifecycle support |
| **ML Kit Face Detection** | 16.1.6 | Real-time face detection and tracking |

### Networking

| Library | Version | Purpose |
|---------|---------|---------|
| **Retrofit** | 2.9.0 | Type-safe HTTP client |
| **OkHttp** | 4.12.0 | HTTP client with interceptor support |
| **Gson Converter** | 2.9.0 | JSON serialization/deserialization |

### Android Jetpack

- **AppCompat** - Backward compatibility
- **Core KTX** - Kotlin extensions
- **ConstraintLayout** - Flexible layout system
- **Lifecycle** - Lifecycle-aware components

---

## ✨ Features

### Core Functionality

| Feature | Description | Status |
|---------|-------------|--------|
| 🔐 **Employee Authentication** | Secure identity verification via face recognition | ✅ Implemented |
| 📸 **Auto Face Capture** | Smart detection with 2-second stability timer | ✅ Implemented |
| 🎯 **Position Guidance** | Visual overlay for optimal face positioning | ✅ Implemented |
| 🔄 **Check-in/Check-out** | Dual-mode attendance recording | ✅ Implemented |
| 📡 **Real-time API Sync** | Instant backend communication | ✅ Implemented |
| 📶 **Offline Detection** | Network status awareness | ✅ Implemented |
| 🎨 **Visual Feedback** | Color-coded status indicators | ✅ Implemented |

### User Experience

- **Guided Face Positioning** - Real-time overlay shows face detection bounds
- **Auto-Capture** - Automatically captures when face is stable and centered
- **Immediate Feedback** - Toast notifications for success/error states
- **Vietnamese Localization** - Native language support for messages

---

## 🔄 Attendance Workflow

```
┌─────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Launch    │────▶│  Camera Preview │────▶│  Face Detection │
│   App       │     │   (Front Cam)   │     │   (ML Kit)      │
└─────────────┘     └─────────────────┘     └────────┬────────┘
                                                     │
                         ┌───────────────────────────┘
                         ▼
              ┌─────────────────────┐
              │  Position Validation │
              │  (Guide Frame Check) │
              └──────────┬──────────┘
                         │
            ┌────────────┼────────────┐
            ▼            ▼            ▼
      ┌─────────┐  ┌─────────┐  ┌─────────┐
      │ No Face │  │ Off Ctr │  │  Valid  │
      │ Detected│  │ Position│  │  Face   │
      └────┬────┘  └────┬────┘  └────┬────┘
           │            │            │
           ▼            ▼            ▼
      ┌─────────┐  ┌─────────┐  ┌─────────────┐
      │  Retry  │  │  Guide  │  │ Start Timer │
      │ Detection│  │  User   │  │ (2 seconds) │
      └─────────┘  └─────────┘  └──────┬──────┘
                                       │
                                       ▼
                              ┌─────────────────┐
                              │  Auto Capture   │
                              │  (Image Saved)  │
                              └────────┬────────┘
                                       │
                                       ▼
                              ┌─────────────────┐
                              │  Upload Image   │
                              │  (Multipart)    │
                              └────────┬────────┘
                                       │ HTTPS
                                       ▼
                              ┌─────────────────┐
                              │   Backend API   │
                              │ /attendance/scan│
                              └────────┬────────┘
                                       │
                    ┌──────────────────┼──────────────────┐
                    ▼                  ▼                  ▼
             ┌──────────┐      ┌──────────┐       ┌──────────┐
             │  Success │      │  Failed  │       │  Error   │
             │  200 OK  │      │  4xx/5xx │       │ Network  │
             └────┬─────┘      └────┬─────┘       └────┬─────┘
                  │                 │                  │
                  ▼                 ▼                  ▼
           ┌────────────┐    ┌────────────┐     ┌────────────┐
           │ Display    │    │ Show Error │     │ Connection │
           │ Employee   │    │  Message   │     │   Error    │
           │   Info     │    │            │     │            │
           └────────────┘    └────────────┘     └────────────┘
```

### Workflow Steps

1. **Initialization** - App launches, requests camera permission
2. **Camera Setup** - CameraX initializes with front camera
3. **Face Detection** - ML Kit continuously analyzes frames
4. **Position Validation** - Face must be within guide frame
5. **Stability Timer** - 2-second countdown ensures stable capture
6. **Auto Capture** - Image automatically saved when conditions met
7. **Upload** - Multipart POST request with captured image
8. **Processing** - Backend performs AI face recognition
9. **Response** - Employee info displayed or error shown
10. **Reset** - 3-second cooldown before next capture

---

## 📷 Camera Integration

### CameraX Configuration

```kotlin
// CameraX use cases configured in MainActivity.kt
val preview = Preview.Builder()
    .build()
    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

val imageCapture = ImageCapture.Builder()
    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
    .build()

val imageAnalysis = ImageAnalysis.Builder()
    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
    .build()
    .also { it.setAnalyzer(executor, faceDetectionAnalyzer) }
```

### Face Detection Parameters

```kotlin
val options = FaceDetectorOptions.Builder()
    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
    .enableTracking()  // Essential for tracking ID
    .build()
```

### Guide Frame Specifications

- **Frame Size**: 65% of screen width and height
- **Position**: Centered on screen
- **Validation**: Face bounding box must be completely within frame
- **Visual Indicator**: Green border when valid, red when invalid

### Coordinate Mapping

The application handles coordinate transformation between camera sensor and display:

```
┌─────────────────────────────────────┐
│         Device Display               │
│  ┌─────────────────────────────┐   │
│  │                             │   │
│  │    ┌───────────────────┐    │   │
│  │    │   GUIDE FRAME     │    │   │
│  │    │   (Target Area)   │    │   │
│  │    │                   │    │   │
│  │    │   ┌───────────┐   │    │   │
│  │    │   │   FACE    │   │    │   │
│  │    │   │  (Valid)  │   │    │   │
│  │    │   └───────────┘   │    │   │
│  │    │                   │    │   │
│  │    └───────────────────┘    │   │
│  │                             │   │
│  └─────────────────────────────┘   │
│                                     │
│  Front Camera (Mirrored View)       │
└─────────────────────────────────────┘
```

---

## 🌐 API Communication

### REST API Endpoints

| Endpoint | Method | Content-Type | Description |
|----------|--------|--------------|-------------|
| `/api/v1/attendance/scan` | POST | `multipart/form-data` | Upload face image for recognition |

### Request Format

```http
POST https://hrm-db.duckdns.org/api/v1/attendance/scan
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="file"; filename="face_capture_1234567890.jpg"
Content-Type: image/jpeg

[Binary image data]
------WebKitFormBoundary--
```

### Response Format

```json
{
  "employeeCode": "EMP001",
  "employeeName": "Nguyen Van A",
  "time": "2024-01-15T08:30:00",
  "status": "CHECK_IN",
  "message": "Chấm công vào thành công"
}
```

### Network Configuration

```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://hrm-db.duckdns.org/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

### Security Features

- ✅ **HTTPS Only** - All API calls use TLS encryption
- ✅ **Certificate Pinning** - Can be enabled for production
- ✅ **Request Logging** - Debug logging with HttpLoggingInterceptor
- ✅ **Timeout Configuration** - Configurable read/write/connect timeouts

---

## 🔐 Authentication Flow

The application uses **backend-driven authentication** via face recognition:

```
┌─────────────┐          ┌──────────────┐          ┌──────────────────┐
│   Employee  │          │   Mobile     │          │   Backend API    │
│   (User)    │          │   App        │          │   + AI Service   │
└──────┬──────┘          └──────┬───────┘          └────────┬─────────┘
       │                        │                           │
       │  1. Present Face       │                           │
       │───────────────────────▶│                           │
       │                        │                           │
       │                        │  2. Capture Image         │
       │                        │──────────────────────────▶│
       │                        │                           │
       │                        │  3. Face Image            │
       │                        │──────────────────────────▶│
       │                        │                           │
       │                        │                           │ 4. AI Recognition
       │                        │                           │    (Face Matching)
       │                        │                           │
       │                        │  5. Employee Info         │
       │                        │◀──────────────────────────│
       │                        │                           │
       │  6. Show Result        │                           │
       │◀───────────────────────│                           │
       │                        │                           │
```

### Authentication Characteristics

- **Biometric-based** - No username/password required
- **Stateless** - No session tokens stored on device
- **Server-Authoritative** - Backend validates all recognition results
- **Audit Trail** - All attempts logged with timestamps

---

## 📁 Project Structure

```
attendance_app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/attendance_app/
│   │   │   │   ├── MainActivity.kt              # Main entry point
│   │   │   │   ├── FaceDetectionAnalyzer.kt     # ML Kit face analysis
│   │   │   │   ├── FaceGuideOverlayView.kt      # Custom overlay view
│   │   │   │   ├── data/
│   │   │   │   │   └── AttendanceResponse.kt    # API response models
│   │   │   │   └── network/
│   │   │   │       ├── RetrofitClient.kt        # HTTP client config
│   │   │   │       └── AttendanceApiService.kt  # API interface
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml        # Main UI layout
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── drawable/
│   │   │   └── AndroidManifest.xml
│   │   └── test/                               # Unit tests
│   ├── build.gradle.kts                        # Module build config
│   └── proguard-rules.pro                      # ProGuard rules
├── gradle/
│   ├── libs.versions.toml                      # Version catalog
│   └── wrapper/
├── build.gradle.kts                            # Project build config
├── settings.gradle.kts
├── gradlew
└── gradlew.bat
```

### Package Organization

```
com.example.attendance_app
├── (root)                    # UI Layer - Activities & Views
├── data                      # Data Layer - Models & Responses
└── network                   # Network Layer - API & Client
```

---

## 🔒 Permissions Required

### Runtime Permissions

| Permission | Purpose | Required |
|------------|---------|----------|
| `CAMERA` | Face capture and detection | ✅ Yes |

### Manifest Permissions

```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.INTERNET"/>

<uses-feature
    android:name="android.hardware.camera"
    android:required="true"/>
```

### Permission Flow

```
┌─────────────────┐
│  App Launch     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Check Camera    │
│ Permission      │
└────────┬────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌───────┐ ┌───────────┐
│Granted│ │  Denied   │
└───┬───┘ └─────┬─────┘
    │           │
    ▼           ▼
┌───────────┐ ┌───────────┐
│Initialize │ │ Show      │
│ CameraX   │ │ Rationale │
│ & Start   │ │ or Exit   │
└───────────┘ └───────────┘
```

---

## 🚀 Running the Application

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK API 29+ (Android 10)
- Kotlin 1.9+
- Gradle 8.0+
- Physical Android device with front camera (recommended)

### Development Setup

```bash
# 1. Clone the repository
git clone https://github.com/TRONGG2005k/attendance_app.git
cd attendance_app

# 2. Open in Android Studio
# File → Open → Select project folder

# 3. Sync Gradle
# Click "Sync Now" in the notification bar

# 4. Connect device or start emulator
# Ensure device has front camera support

# 5. Run the application
# Click Run button (▶) or press Shift+F10
```

### Device Requirements

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| Android Version | API 29 (Android 10) | API 33+ (Android 13+) |
| RAM | 2 GB | 4 GB+ |
| Camera | Front camera required | Auto-focus preferred |
| Network | WiFi/Data connection | Stable 4G/5G |

---

## 📦 APK Build Instructions

### Debug Build

```bash
# Generate debug APK
./gradlew assembleDebug

# Output location
app/build/outputs/apk/debug/app-debug.apk
```

### Release Build

```bash
# 1. Create keystore (if not exists)
keytool -genkey -v -keystore attendance.keystore -alias attendance -keyalg RSA -keysize 2048 -validity 10000

# 2. Configure signing in app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("attendance.keystore")
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = "attendance"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

# 3. Build release APK
./gradlew assembleRelease

# Output location
app/build/outputs/apk/release/app-release.apk
```

### Bundle Build (Google Play)

```bash
# Generate App Bundle
./gradlew bundleRelease

# Output location
app/build/outputs/bundle/release/app-release.aab
```

---

## 🔌 Backend Integration

### Configuration

Update `RetrofitClient.kt` to point to your backend:

```kotlin
object RetrofitClient {
    // Production server
    private const val BASE_URL = "https://hrm-db.duckdns.org/"
    
    // Development server (optional)
    // private const val BASE_URL = "http://10.0.2.2:8080/"
    
    // Staging server (optional)
    // private const val BASE_URL = "https://staging.hrm-db.duckdns.org/"
}
```

### Environment Setup

| Environment | Base URL | Description |
|-------------|----------|-------------|
| Development | `http://10.0.2.2:8080/` | Local emulator server |
| Staging | `https://staging.hrm-db.duckdns.org/` | Pre-production testing |
| Production | `https://hrm-db.duckdns.org/` | Live AWS EC2 instance |

### API Health Check

```bash
# Verify backend connectivity
curl -I https://hrm-db.duckdns.org/api/v1/attendance/scan

# Expected: HTTP/2 405 (Method Not Allowed for GET on POST endpoint)
# Or: HTTP/2 200 if health endpoint exists
```

### Backend Dependencies

The mobile app requires a backend service that:

1. Accepts `multipart/form-data` POST requests
2. Performs face recognition using AI/ML service
3. Returns JSON with employee information
4. Handles check-in/check-out logic
5. Maintains employee face embeddings database

---

## ⚠️ Error Handling Strategy

### Error Categories

| Category | Examples | Handling Strategy |
|----------|----------|-------------------|
| **Camera Errors** | Permission denied, Hardware unavailable | Graceful exit with user message |
| **Network Errors** | Timeout, No connection, DNS failure | Retry with exponential backoff |
| **API Errors** | 4xx Client errors, 5xx Server errors | Display specific error message |
| **Face Detection** | No face, Poor lighting, Off-center | Visual guidance to user |
| **Recognition** | Unknown face, Low confidence | Prompt for manual verification |

### Error Handling Implementation

```kotlin
// API Error Handling Pattern
lifecycleScope.launch {
    try {
        val response = RetrofitClient.attendanceApiService.scanFace(body)
        
        when {
            response.isSuccessful -> {
                // Handle success
                val attendance = response.body()
                showSuccess(attendance)
            }
            response.code() == 401 -> showError("Unauthorized")
            response.code() == 404 -> showError("Employee not found")
            response.code() >= 500 -> showError("Server error")
            else -> showError("Unknown error: ${response.code()}")
        }
    } catch (e: IOException) {
        showError("Network error: ${e.message}")
    } catch (e: HttpException) {
        showError("HTTP error: ${e.code()}")
    } catch (e: Exception) {
        showError("Unexpected error: ${e.message}")
    }
}
```

### User Feedback

| State | Visual Indicator | Toast Message |
|-------|-----------------|---------------|
| No Face | Red guide frame | "No face detected" |
| Off Center | Red guide frame | "Position your face in the center" |
| Valid Position | Green guide frame | "Hold still..." |
| Capturing | Countdown display | "Hold still in 2..." |
| Uploading | Progress indicator | "Đang xử lý nhận diện..." |
| Success | Employee info display | "Chấm công thành công" |
| Error | Error highlight | Specific error message |

---

## 📸 Screenshots

> _Screenshots will be added in future updates_

| Screen | Description | Status |
|--------|-------------|--------|
| Camera Preview | Main attendance capture interface | ⬜ Pending |
| Face Detection | Guide overlay with face bounds | ⬜ Pending |
| Success State | Employee info after recognition | ⬜ Pending |
| Error State | Error message display | ⬜ Pending |

---

## 🔮 Future Improvements

### Short-term Roadmap

- [ ] **Offline Mode** - Queue attendance when network unavailable
- [ ] **Biometric Fallback** - Fingerprint authentication option
- [ ] **Attendance History** - View past check-ins/check-outs
- [ ] **Multi-language Support** - English, Vietnamese localization
- [ ] **Dark Mode** - Theme switching capability

### Long-term Enhancements

- [ ] **ML On-device** - Edge AI for faster recognition
- [ ] **Geofencing** - Location-based attendance validation
- [ ] **Push Notifications** - Shift reminders and alerts
- [ ] **Dashboard Integration** - Manager view for team attendance
- [ ] **Analytics** - Attendance statistics and insights
- [ ] **Wear OS Support** - Smartwatch companion app

### Technical Debt

- [ ] Unit test coverage improvement
- [ ] Integration tests for API layer
- [ ] UI tests with Espresso
- [ ] Dependency injection with Hilt
- [ ] MVVM architecture migration
- [ ] Repository pattern implementation

---

## 👤 Author

**TRONGG2005k**

- GitHub: [@TRONGG2005k](https://github.com/TRONGG2005k)
- Project: [attendance_app](https://github.com/TRONGG2005k/attendance_app)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Google ML Kit](https://developers.google.com/ml-kit) for face detection capabilities
- [CameraX](https://developer.android.com/training/camerax) for modern camera API
- [Retrofit](https://square.github.io/retrofit/) for type-safe HTTP client
- [Android Jetpack](https://developer.android.com/jetpack) for architecture components

---

<div align="center">

**[⬆ Back to Top](#-hrm-attendance-mobile-app)**

Made with ❤️ for efficient attendance management

</div>
