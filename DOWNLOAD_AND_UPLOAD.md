# 🚀 Hướng dẫn Upload Project lên GitHub (Bỏ qua Git Error)

## ⚠️ Vấn đề
Replit Git service đang báo lỗi:
```
UNKNOWN_NOT_GIT
Unknown error from the Git service. This is probably a bug in the app.
```

**Giải pháp:** BỎ QUA hoàn toàn Git trong Replit, download thủ công và upload lên GitHub.

---

## 📥 BƯỚC 1: Download Project

### Cách A: Download qua Replit Menu (Dễ nhất)
1. Click icon **≡** (ba gạch ngang) góc trên trái
2. Chọn **"Download as ZIP"**
3. Lưu file về máy

### Cách B: Download file có sẵn
Tôi đã tạo sẵn archive file cho bạn:

**File:** `cpu-gpu-monitor-project.tar.gz` (271 KB)

**Cách download:**
1. Vào tab **Files** bên trái Replit
2. Tìm file `cpu-gpu-monitor-project.tar.gz` ở root folder
3. Click chuột phải → **Download**

**Cách giải nén:**
- **Windows:** Dùng 7-Zip hoặc WinRAR
- **Mac:** Double-click file (hoặc dùng Archive Utility)
- **Linux:** `tar -xzf cpu-gpu-monitor-project.tar.gz`

---

## 🌐 BƯỚC 2: Upload lên GitHub (Qua Web UI)

### 1. Tạo Repository mới
1. Vào https://github.com/new
2. Điền thông tin:
   - **Repository name:** `cpu-gpu-monitor`
   - **Description:** "Android CPU/GPU Monitor - Real-time thermal monitoring"
   - **Visibility:** ☑️ Public (hoặc Private)
   - **❌ QUAN TRỌNG:** KHÔNG chọn "Add a README file"
   - **❌ QUAN TRỌNG:** KHÔNG chọn ".gitignore" hay "license"
3. Click **Create repository**

### 2. Upload Files
Sau khi tạo repo, GitHub sẽ hiện trang trống. Làm theo:

1. Click link **"uploading an existing file"** (màu xanh)
2. Giải nén file đã download
3. Mở folder `cpu-gpu-monitor` (hoặc folder sau khi giải nén)
4. **Chọn TẤT CẢ files và folders:**
   ```
   ✅ app/
   ✅ gradle/
   ✅ .github/
   ✅ gradlew
   ✅ gradlew.bat
   ✅ build.gradle
   ✅ settings.gradle
   ✅ gradle.properties
   ✅ README.md
   ✅ SETUP.md
   ✅ replit.md
   ✅ ... (tất cả files)
   ```
5. **Kéo thả** tất cả vào khung GitHub (hoặc click "choose your files")
6. Đợi upload xong (~30 giây)
7. Xuống dưới, điền **Commit message:**
   ```
   Initial commit - Android CPU/GPU Monitor
   ```
8. Click **Commit changes**

---

## ⚙️ BƯỚC 3: GitHub Actions Tự Động Build APK

### 1. Chờ Workflow chạy
1. Vào tab **Actions** trên repo (thanh menu phía trên)
2. Bạn sẽ thấy workflow **"Build Android APK"** đang chạy (icon ⏳ màu vàng)
3. Click vào workflow run để xem tiến trình
4. Đợi ~3-5 phút cho đến khi thấy icon ✅ màu xanh

### 2. Download APK
Sau khi workflow hoàn thành:
1. Scroll xuống trang workflow run
2. Tìm phần **Artifacts** (cuối trang)
3. Bạn sẽ thấy 2 files:
   - **app-debug** - Click để download APK debug (khuyến nghị)
   - **app-release** - APK release (unsigned)
4. Giải nén ZIP và lấy file `.apk`

---

## 📱 BƯỚC 4: Cài APK lên Android

1. Chuyển file APK vào điện thoại (USB, Bluetooth, email...)
2. Trên điện thoại:
   - Vào **Settings** → **Security** → Bật **"Install from unknown sources"**
   - Hoặc **Settings** → **Apps** → Bật cho app quản lý files
3. Mở file APK và cài đặt
4. Mở app **CPU/GPU Monitor**
5. Xem CPU/GPU temperature realtime!

---

## 🏷️ BONUS: Tạo Release (Optional)

Để APK lưu vĩnh viễn (không bị xóa sau 90 ngày như Artifacts):

### Via GitHub Web:
1. Vào tab **Releases** → Click **"Create a new release"**
2. **Choose a tag:** Gõ `v1.0.0` → Click **"Create new tag: v1.0.0"**
3. **Release title:** `v1.0.0 - First Release`
4. **Description:** (Optional - mô tả tính năng)
5. Click **Publish release**
6. GitHub Actions sẽ tự động build và attach APK vào release!

---

## ✅ Checklist

- [ ] Download project từ Replit (ZIP hoặc tar.gz)
- [ ] Tạo GitHub repository mới (KHÔNG add README)
- [ ] Upload TẤT CẢ files lên GitHub
- [ ] Đợi workflow "Build Android APK" chạy xong
- [ ] Download APK từ Artifacts
- [ ] Cài APK lên điện thoại Android
- [ ] Test app xem có hoạt động không

---

## ⚠️ Troubleshooting

### Workflow bị fail?
1. Click vào workflow run bị fail
2. Click vào step bị lỗi (icon ❌ đỏ)
3. Đọc error log
4. Thường do thiếu files hoặc sai cấu trúc thư mục

### Không thấy folder .github khi upload?
- Windows/Mac mặc định ẩn folders bắt đầu bằng "."
- **Windows:** File Explorer → View → Show hidden files
- **Mac:** Finder → Cmd+Shift+. (toggle hidden files)
- **Hoặc:** Upload từng file/folder riêng lẻ

### APK không cài được?
- Đảm bảo đã bật "Install from unknown sources"
- Thử app-debug.apk thay vì app-release
- Check phone có Android 10+ không (yêu cầu tối thiểu)

### Workflow không tự động chạy?
- Check tab Actions có enabled không
- Nếu disabled, click **"I understand my workflows, go ahead and enable them"**

---

## 📞 Need Help?

Nếu vẫn gặp vấn đề:
1. Check README.md và SETUP.md trong project
2. Đọc error log trong GitHub Actions
3. Hỏi lại tôi với screenshot cụ thể bước nào bị lỗi

---

**🎯 Mục tiêu cuối cùng:** Có file APK để cài lên điện thoại Android và monitor CPU/GPU realtime!
