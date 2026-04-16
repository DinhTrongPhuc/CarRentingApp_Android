# 🚗 CarRentingApp - Ứng dụng Thuê Xe Android

Ứng dụng thuê xe hai chiều (chủ xe & người thuê) xây dựng bằng **Android Java + Firebase**.

---

## 📋 Tính năng

| Module | Chức năng |
|--------|-----------|
| Auth | Đăng ký / Đăng nhập / Phân quyền |
| Trang chủ | Xem 20 xe mới nhất, infinite scroll, pull-to-refresh |
| Tìm kiếm | Lọc theo địa điểm, loại xe, giá |
| Chi tiết xe | Slider ảnh, Google Maps, liên hệ chủ xe |
| Đăng xe | Upload ảnh, validate đầy đủ (dành cho chủ xe) |
| Quản lý xe | Sửa, xóa xe; xem đơn thuê |
| Xe đã lưu | Lưu / bỏ lưu xe, cập nhật realtime |
| Đặt xe | Chọn ngày, kiểm tra trùng lịch, tính tổng tiền |
| Thanh toán | MoMo / ZaloPay / Thẻ ngân hàng (mock) |
| Lịch sử | Xem lịch sử đặt xe |

---

## 🛠 Tech Stack

- **Language**: Java
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34
- **Firebase**: Authentication, Firestore
- **Image Hosting**: Cloudinary (Thay thế Firebase Storage)
- **Image Loading**: Glide 4.16
- **Maps**: Google Maps SDK
- **UI**: Material Design Components

---

## 🚀 Hướng dẫn thiết lập

### Bước 1: Tạo Firebase Project

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Nhấn **"Add project"** → đặt tên `CarRentingApp`
3. Bỏ chọn Google Analytics (tuỳ chọn) → **Create project**

### Bước 2: Thêm Android App vào Firebase

1. Trong Firebase Console → nhấn icon Android (**"Add app"**)
2. **Package name**: `com.example.carrentingapp`
3. Nhấn **Register app**
4. Tải file `google-services.json`
5. **Copy file vào thư mục**: `app/google-services.json` (thay thế file template có sẵn)

### Bước 3: Bật các Firebase Services

#### Authentication
```
Firebase Console → Authentication → Get started
→ Sign-in method → Email/Password → Enable → Save
```

#### Firestore Database
```
Firebase Console → Firestore Database → Create database
→ Start in test mode (hoặc production mode + upload rules)
→ Chọn region: asia-southeast1 (Singapore)
```

#### Cloudinary (Thay thế Firebase Storage)
1. Tạo tài khoản tại [Cloudinary.com](https://cloudinary.com/) (Free)
2. Truy cập **Dashboard** để lấy: `Cloud Name`, `API Key`, `API Secret`
3. Mở file `app/src/main/java/com/example/carrentingapp/utils/Constants.java`:
   - Điền các giá trị vào `CLOUD_NAME`, `API_KEY`, `API_SECRET`

### Bước 4: Upload Firebase Security Rules

**Firestore Rules** - Copy nội dung file `firebase/firestore.rules` vào:
```
Firebase Console → Firestore → Rules → Paste → Publish
```

**Firestore Rules** - Copy nội dung file `firebase/firestore.rules` vào:
```
Firebase Console → Firestore → Rules → Paste → Publish
```

> [!NOTE]
> Không cần thiết lập Storage Rules vì chúng ta đã chuyển sang dùng Cloudinary.

### Bước 5: Lấy Google Maps API Key

1. Vào [Google Cloud Console](https://console.cloud.google.com/)
2. Chọn project Firebase của bạn (cùng project)
3. APIs & Services → Enable APIs → tìm **"Maps SDK for Android"** → Enable
4. APIs & Services → Credentials → **Create Credentials** → API Key -> select API restrictions chọn "Maps SDK for Android" -> create
5. Mở file `AndroidManifest.xml`, tìm dòng:
   ```xml
   android:value="YOUR_GOOGLE_MAPS_API_KEY"
   ```
   Thay `YOUR_GOOGLE_MAPS_API_KEY` bằng key vừa tạo

### Bước 6: Tạo Firestore Indexes

Vào **Firestore → Indexes → Composite** và tạo các index sau:

| Collection | Fields | Order |
|------------|--------|-------|
| cars | available ASC, createdAt DESC | |
| cars | ownerId ASC, createdAt DESC | |
| cars | available ASC, type ASC, createdAt DESC | |
| bookings | renterId ASC, createdAt DESC | |
| bookings | ownerId ASC, createdAt DESC | |
| bookings | carId ASC, status ASC | |
| saved_cars | userId ASC | |

> 💡 Hoặc chạy app, khi gặp lỗi Firestore sẽ log URL để tạo index tự động.

### Bước 7: Mở project trong Android Studio

1. Mở **Android Studio** → **Open** → chọn thư mục `CarRentingApp`
2. Đợi Gradle sync hoàn tất (~2-5 phút lần đầu)
3. Nếu gặp lỗi Gradle: **File → Invalidate Caches → Restart**

### Bước 8: Chạy ứng dụng

```
Run → Run 'app' (Shift+F10)
Chọn emulator hoặc thiết bị thật
```

---

## 📁 Cấu trúc Project

```
CarRentingApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/carrentingapp/
│   │   │   ├── activities/          # 9 màn hình chính
│   │   │   │   ├── SplashActivity.java
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── CarDetailActivity.java
│   │   │   │   ├── PostCarActivity.java
│   │   │   │   ├── EditCarActivity.java
│   │   │   │   ├── BookingActivity.java
│   │   │   │   └── PaymentActivity.java
│   │   │   ├── fragments/           # 5 tab fragment
│   │   │   │   ├── HomeFragment.java
│   │   │   │   ├── SearchFragment.java
│   │   │   │   ├── SavedFragment.java
│   │   │   │   ├── ManageCarsFragment.java
│   │   │   │   └── ProfileFragment.java
│   │   │   ├── adapters/            # RecyclerView adapters
│   │   │   │   ├── CarAdapter.java
│   │   │   │   ├── BookingAdapter.java
│   │   │   │   ├── ImageSliderAdapter.java
│   │   │   │   ├── ManageCarAdapter.java
│   │   │   │   └── SelectedImageAdapter.java
│   │   │   ├── models/              # Data models (POJO)
│   │   │   │   ├── User.java
│   │   │   │   ├── Car.java
│   │   │   │   ├── Booking.java
│   │   │   │   └── SavedCar.java
│   │   │   ├── repositories/        # Firebase data layer
│   │   │   │   ├── AuthRepository.java
│   │   │   │   ├── CarRepository.java
│   │   │   │   ├── BookingRepository.java
│   │   │   │   ├── SavedCarRepository.java
│   │   │   │   └── StorageRepository.java
│   │   │   └── utils/               # Helper classes
│   │   │       ├── Constants.java
│   │   │       ├── FormatUtils.java
│   │   │       ├── ValidationUtils.java
│   │   │       ├── NetworkUtils.java
│   │   │       └── SessionManager.java
│   │   ├── res/
│   │   │   ├── layout/              # 20 file XML layout
│   │   │   ├── drawable/            # Icons & backgrounds
│   │   │   ├── values/              # strings, colors, themes, dimens
│   │   │   └── menu/                # Bottom navigation menu
│   │   └── AndroidManifest.xml
│   ├── build.gradle                 # App dependencies
│   └── google-services.json         # Firebase config (cần thay)
├── firebase/
│   ├── firestore.rules
│   └── storage.rules
└── build.gradle                     # Root build config
```

---

## 🔐 Phân quyền người dùng

| Tính năng | Chủ xe (owner) | Người thuê (renter) |
|-----------|:--------------:|:-------------------:|
| Xem xe | ✅ | ✅ |
| Tìm kiếm | ✅ | ✅ |
| Lưu xe | ✅ | ✅ |
| Đặt xe | ❌ | ✅ |
| Đăng xe | ✅ | ❌ |
| Quản lý xe của mình | ✅ | ❌ |
| Xem đơn thuê | ✅ | ✅ |

---

## 📊 Cấu trúc Firestore Database

```
users/{uid}
  - uid, fullName, email, phone, avatarUrl, role, createdAt

cars/{carId}
  - id, name, brand, type, licensePlate, location, description
  - ownerId, ownerName, ownerPhone
  - pricePerDay, latitude, longitude
  - imageUrls[], condition, available, createdAt

bookings/{bookingId}
  - id, carId, carName, carImageUrl
  - renterId, renterName, renterPhone, ownerId
  - startDate, endDate, totalDays, pricePerDay, totalPrice
  - status, paymentMethod, paymentStatus, createdAt

saved_cars/{docId}
  - id, carId, userId, savedAt
```

---

## ⚠️ Lỗi thường gặp & Cách xử lý

### Gradle sync failed
```
File → Invalidate Caches and Restart
hoặc: ./gradlew clean build
```

### "No google-services.json found"
→ Đảm bảo file `google-services.json` thật nằm đúng tại `app/google-services.json`

### Lỗi Upload ảnh (Cloudinary)
- Kiểm tra lại `API Key` và `API Secret` trong file `Constants.java`.
- Đảm bảo thiết bị có kết nối Internet.
- Kiểm tra logcat với tag `Cloudinary` để xem chi tiết lỗi.

### Maps không hiển thị
→ Kiểm tra API Key trong AndroidManifest.xml
→ Bật Maps SDK for Android trong Google Cloud Console
→ Kiểm tra billing đã được bật chưa (Maps yêu cầu billing)

### "FAILED_PRECONDITION: The query requires an index"
→ Click vào link trong Logcat để tự động tạo index trên Firebase Console

---

## 🧪 Tài khoản test

Sau khi chạy app, tạo 2 tài khoản:
- **Chủ xe**: Đăng ký với role "Chủ xe" → đăng xe, quản lý
- **Người thuê**: Đăng ký với role "Người thuê" → tìm kiếm, đặt xe

---

## 📱 Yêu cầu hệ thống

- Android Studio **Hedgehog** (2023.1.1) trở lên
- JDK 17+
- Android SDK 34
- Thiết bị/Emulator Android 8.0 (API 26) trở lên
- Kết nối Internet

---

## 📝 FR Coverage

| FR | Trạng thái | File |
|----|-----------|------|
| FR1.1-1.6 | ✅ | LoginActivity, RegisterActivity |
| FR2.1-2.7 | ✅ | PostCarActivity |
| FR3.1-3.5 | ✅ | SearchFragment |
| FR4.1-4.5 | ✅ | HomeFragment |
| FR5.1-5.7 | ✅ | CarDetailActivity |
| FR6.1-6.6 | ✅ | ManageCarsFragment, EditCarActivity |
| FR7.1-7.5 | ✅ | CarDetailActivity, SavedFragment |
| FR8.1-8.8 | ✅ | BookingActivity, PaymentActivity |
