# 🚀 HƯỚNG DẪN CHẠY DỰ ÁN NHANH

## Vấn đề hiện tại
❌ **Dự án cần Java 11+ nhưng hệ thống đang dùng Java 8**

## ✅ GIẢI PHÁP (Chọn 1 trong 2)

### Giải pháp 1: Mở bằng Android Studio ⭐ RECOMMENDED

1. **Mở Android Studio**
2. **Chọn File → Open**
3. **Chọn folder:** `C:\Users\ABC\AndroidStudioProjects\LNRead`
4. **Đợi Gradle sync** (có thể mất 1-2 phút lần đầu)
5. **Kết nối thiết bị Android** hoặc tạo emulator
6. **Nhấn nút RUN** ▶️ (hoặc `Shift + F10`)

Android Studio sẽ tự động sử dụng JDK đúng version và build app!

---

### Giải pháp 2: Cài Java 11

1. **Download JDK 11:** https://adoptium.net/temurin/releases/
2. **Cài đặt** JDK 11
3. **Set biến môi trường JAVA_HOME:**
   - Windows: System Properties → Environment Variables → JAVA_HOME
4. **Chạy lệnh:**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

---

## 📋 Yêu cầu

- ✅ Android Studio đã cài
- ✅ Android SDK installed
- ✅ Thiết bị Android hoặc Emulator
- ❌ Java 8 → Cần Java 11+ (nhưng Android Studio đã có sẵn!)

---

## 🔍 TÓM TẮT THAY ĐỔI

Dự án đã được refactor để áp dụng **Singleton Pattern**:

### ✨ Files mới:
- `DatabaseSingleton.java` - Quản lý database với Singleton

### 🔧 Files đã sửa:
- `RetrofitClient.java` - Thread-safe Singleton pattern
- `MainActivity.java` - Sử dụng DatabaseSingleton
- Tất cả Fragment files - Truy cập database/API qua singleton

### 📊 Kết quả:
- ✅ Thread-safe
- ✅ Hiệu năng tốt hơn (1 instance duy nhất)
- ✅ Code gọn gàng, dễ bảo trì
- ✅ Không còn dependency giữa Fragment và MainActivity

