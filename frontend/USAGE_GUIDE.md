# KINEDICAL Frontend - Hướng Dẫn Sử Dụng Chi Tiết

## 📖 Giới Thiệu

Frontend React của KINEDICAL là một dashboard hiện đại để xem các bài viết sức khỏe được gợi ý bằng AI. Ứng dụng có:

- ✅ Hiển thị danh sách bài viết sức khỏe
- ✅ Lọc theo danh mục
- ✅ Loading state và error handling
- ✅ Responsive design
- ✅ Animations mượt mà

## 🎬 Quick Start (3 Bước)

### Step 1: Cài đặt Dependencies

```bash
cd frontend
npm install
```

### Step 2: Chạy Backend (nếu chưa chạy)

```bash
# Trong terminal khác, ở thư mục gốc dự án
docker compose up
```

### Step 3: Chạy Frontend

```bash
npm start
```

Ứng dụng sẽ tự động mở tại: **http://localhost:3000**

## 📱 UI Overview

### Header

```
┌─────────────────────────────────────┐
│  🏥 Bảng điều khiển sức khỏe       │
│  Khám phá các bài viết được gợi ý   │
└─────────────────────────────────────┘
```

### Filter Section

```
Lọc theo danh mục:
[Tất cả] [Đã xuất bản] [Tim mạch] [Thần kinh] ...
```

### Content Cards Grid

```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  [Hình ảnh]  │  │  [Hình ảnh]  │  │  [Hình ảnh]  │
│   CARDIO     │  │  NEUROLOGY   │  │ ORTHOPEDICS  │
│   Tiêu đề    │  │   Tiêu đề    │  │   Tiêu đề    │
│   Tóm tắt    │  │   Tóm tắt    │  │   Tóm tắt    │
│  ✍️ 👁️ ❤️   │  │  ✍️ 👁️ ❤️   │  │  ✍️ 👁️ ❤️   │
│ [Đọc thêm]   │  │ [Đọc thêm]   │  │ [Đọc thêm]   │
└──────────────┘  └──────────────┘  └──────────────┘
```

## 🔍 Các Trạng Thái

### 1. Loading State

```
    ⭕ (spinner quay tròn)
Đang tải dữ liệu sức khỏe cho bạn...
```

### 2. Error State

```
⚠️
Oops! Có lỗi xảy ra
Lỗi kết nối hoặc máy chủ bị sập
[🔄 Thử lại]
```

### 3. Empty State

```
📭
Không có bài viết
Hiện tại không có bài viết trong danh mục này
```

### 4. Success State

```
Tìm thấy 12 bài viết sức khỏe
[Cards hiển thị dữ liệu]
```

## 🎯 Các Danh Mục

| Danh mục          | Mã            | Màu           |
| ----------------- | ------------- | ------------- |
| Tim mạch          | CARDIOLOGY    | 🔴 Đỏ         |
| Thần kinh         | NEUROLOGY     | 🟢 Xanh lá    |
| Xương khớp        | ORTHOPEDICS   | 🔵 Xanh dương |
| Da liễu           | DERMATOLOGY   | 🟡 Vàng       |
| Dinh dưỡng        | NUTRITION     | 🟠 Cam        |
| Sức khỏe tâm thần | MENTAL_HEALTH | 🩷 Hồng       |
| Thể dục           | FITNESS       | 🟦 Xanh       |

## 💻 Code Structure

### Component Hierarchy

```
App
└── Dashboard
    ├── Filter Buttons
    ├── Loading Spinner (if loading)
    ├── Error Alert (if error)
    ├── Empty Message (if no data)
    └── Content Grid
        ├── HealthContentCard
        ├── HealthContentCard
        └── HealthContentCard
```

### API Flow

```
Dashboard Component
       ↓
   fetchHealthContent()
       ↓
   healthContentAPI.getRecommendations()
       ↓
   axios.get('/api/health-contents')
       ↓
   Backend Spring Boot
```

## 🔧 Cấu Hình API

### File: `.env`

```env
# API URL của backend
REACT_APP_API_URL=http://localhost:8080/api

# Environment (development/production)
REACT_APP_ENV=development
```

### Thay đổi API URL

Nếu backend chạy trên port khác, sửa `.env`:

```env
REACT_APP_API_URL=http://localhost:9090/api
```

Rồi restart development server:

```bash
npm start
```

## 📊 API Response Example

Backend trả về JSON array:

```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "_id": "507f1f77bcf86cd799439011",
    "title": "Cách Giữ Gìn Sức Khỏe Tim Mạch",
    "content": "Bài viết chi tiết về cách bảo vệ trái tim...",
    "category": "CARDIOLOGY",
    "author": "Dr. Nguyễn Văn A",
    "publishDate": "2026-05-13T10:30:00Z",
    "imageUrl": "https://example.com/image.jpg",
    "stats": {
      "views": 1200,
      "likes": 350,
      "comments": 45
    }
  }
]
```

## 🎨 Styling Details

### Colors

```
Primary: #667eea (Purple)
Secondary: #764ba2 (Darker Purple)
Text Dark: #2c3e50
Text Light: #7f8c8d
Background: #ecf0f1
Error: #e74c3c
Success: #2ecc71
```

### Breakpoints

```
Desktop: > 768px
Tablet: 481px - 768px
Mobile: ≤ 480px
```

### Animations

```
✨ Hover: Card translateY(-10px)
✨ Loading: Spinner spin (1s)
✨ Card Fade: fadeIn (0.5s)
```

## 🐛 Xử Lý Lỗi

### Lỗi Phổ Biến

#### 1. "Cannot reach backend"

**Nguyên nhân**: Backend không chạy
**Giải pháp**:

```bash
docker compose up
```

#### 2. "CORS error"

**Nguyên nhân**: Backend chưa cấu hình CORS
**Giải pháp**: Kiểm tra Spring Boot CORS config

#### 3. "Port 3000 already in use"

**Giải pháp**:

```bash
PORT=3001 npm start
```

#### 4. "Module not found"

**Giải pháp**:

```bash
rm -rf node_modules package-lock.json
npm install
```

## 🚀 Production Deployment

### Build

```bash
npm run build
```

### Output

```
build/
├── index.html
├── static/
│   ├── css/
│   ├── js/
│   └── media/
└── ...
```

### Deploy (ví dụ Vercel)

```bash
npm install -g vercel
vercel
```

## 📝 Environment Variables

Tạo `.env.local` cho development:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_DEBUG=true
```

## 🔗 Liên Kết Hữu Ích

- [React Hooks](https://react.dev/reference/react)
- [Axios Documentation](https://axios-http.com/docs/intro)
- [CSS Responsive Design](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Responsive_Design)

## 💡 Tips & Tricks

### 1. Debug API Calls

```javascript
// Thêm vào console
console.log("Response:", data);
```

### 2. Local Storage

```javascript
// Lưu preferences
localStorage.setItem("filter", "CARDIOLOGY");
```

### 3. Responsive Testing

```
Chrome DevTools → F12 → Ctrl+Shift+M
```

### 4. Performance

```javascript
// useCallback để optimize
const fetchData = useCallback(() => {
  // ...
}, [dependency]);
```

## ✅ Checklist Trước Deploy

- [ ] Kiểm tra `.env` cấu hình đúng
- [ ] Test tất cả filters
- [ ] Test loading/error states
- [ ] Test responsive (mobile, tablet, desktop)
- [ ] Kiểm tra console có error không
- [ ] Run build: `npm run build`
- [ ] Kiểm tra build folder

## 📞 Support

Nếu gặp vấn đề:

1. Kiểm tra console browser (F12)
2. Kiểm tra network tab
3. Xem lại `.env` configuration
4. Kiểm tra backend logs

---

**Happy Coding! 🎉**
