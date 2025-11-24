# 📤 Hướng dẫn Upload Project lên GitHub

## Cách 1: Upload qua GitHub Web (Đơn giản nhất - Khuyến nghị)

### Bước 1: Download Project từ Replit
1. Click vào menu **≡** (3 gạch ngang) góc trên bên trái
2. Chọn **Download as ZIP**
3. File ZIP sẽ được tải về máy bạn

### Bước 2: Tạo Repository mới trên GitHub
1. Vào https://github.com/new
2. Điền thông tin:
   - **Repository name**: `cpu-gpu-monitor` (hoặc tên bạn thích)
   - **Description**: "Android CPU/GPU monitoring app"
   - ☑️ Chọn **Public** (hoặc Private nếu muốn)
   - ❌ **KHÔNG** chọn "Initialize with README" (để trống)
3. Click **Create repository**

### Bước 3: Upload Files
1. Giải nén file ZIP đã tải về
2. Trên trang GitHub repo vừa tạo, click **uploading an existing file**
3. Kéo thả **TẤT CẢ** files và folders vào
   ```
   ✅ app/
   ✅ gradle/
   ✅ .github/
   ✅ gradlew
   ✅ build.gradle
   ✅ settings.gradle
   ✅ README.md
   ✅ SETUP.md
   ✅ ... (tất cả files)
   ```
4. Xuống dưới, điền:
   - **Commit message**: `Initial commit - Android CPU/GPU Monitor`
5. Click **Commit changes**

### Bước 4: GitHub Actions sẽ Tự Động Build
1. Vào tab **Actions** trên repo
2. Workflow "Build Android APK" sẽ tự động chạy
3. Đợi ~3-5 phút để build xong (icon ✅ màu xanh)
4. Click vào workflow run → Scroll xuống **Artifacts**
5. Download **app-debug.apk** hoặc **app-release.apk**

✅ **Xong!** Bạn có APK để cài trên điện thoại rồi!

---

## Cách 2: Upload qua Git Desktop (Nếu muốn dùng Git)

### Bước 1: Tải GitHub Desktop
- Windows/Mac: https://desktop.github.com/
- Cài đặt và đăng nhập GitHub

### Bước 2: Download Project từ Replit
- Download as ZIP như hướng dẫn ở Cách 1

### Bước 3: Tạo Repo trong GitHub Desktop
1. Giải nén ZIP vào thư mục bạn muốn
2. Mở GitHub Desktop
3. **File** → **Add Local Repository**
4. Chọn thư mục vừa giải nén
5. Click **Create Repository** nếu được hỏi
6. Click **Publish repository**
7. Điền tên và mô tả → **Publish**

✅ Done! Workflow sẽ tự động chạy trên GitHub Actions.

---

## Cách 3: Command Line (Cho người có kinh nghiệm Git)

```bash
# 1. Download và giải nén project từ Replit

# 2. Mở Terminal trong thư mục project
cd /path/to/cpu-gpu-monitor

# 3. Initialize git (nếu chưa có)
git init

# 4. Add files
git add .

# 5. Commit
git commit -m "Initial commit - Android CPU/GPU Monitor"

# 6. Thêm remote (thay YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/cpu-gpu-monitor.git

# 7. Push
git branch -M main
git push -u origin main
```

---

## 🎯 Tạo Release (Optional)

Sau khi code đã ở trên GitHub:

### Via GitHub Web:
1. Vào tab **Releases** → **Create a new release**
2. **Choose a tag**: Gõ `v1.0.0` → **Create new tag**
3. **Release title**: `v1.0.0 - Initial Release`
4. **Description**: Copy từ README hoặc tự viết
5. Click **Publish release**
6. APK sẽ tự động được attach vào release!

### Via Command Line:
```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions sẽ tự động build và upload APK vào release.

---

## ⚠️ Lưu ý

- ✅ Upload TẤT CẢ files/folders (trừ `.git` nếu có)
- ✅ Giữ nguyên cấu trúc thư mục
- ✅ Đừng quên folder `.github/workflows/` (quan trọng!)
- ⏰ Workflow build mất ~3-5 phút
- 📱 APK trong Artifacts có hiệu lực 90 ngày
- 🏷️ APK trong Releases lưu vĩnh viễn

---

## 🆘 Troubleshooting

**Workflow fail?**
- Check tab Actions → Click vào run bị fail → Đọc error log
- Thường do thiếu files hoặc cấu trúc sai

**Không thấy APK?**
- Đợi workflow chạy xong (icon ✅ xanh)
- Scroll xuống tới phần **Artifacts**

**Want to update code?**
- Upload files mới (GitHub sẽ merge)
- Hoặc edit trực tiếp trên GitHub web
- Workflow tự động chạy lại mỗi khi có thay đổi
