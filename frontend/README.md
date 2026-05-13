# KINEDICAL Frontend - React Dashboard

Frontend React cho nền tảng KINEDICAL Healthcare Platform.

## 📋 Yêu cầu

- Node.js >= 14.0
- npm >= 6.0

## 🚀 Cài đặt và Chạy

### 1. Cài đặt dependencies

```bash
cd frontend
npm install
```

### 2. Cấu hình API

Tạo file `.env` từ `.env.example`:

```bash
cp .env.example .env
```

Sửa file `.env` (nếu cần):

```env
REACT_APP_API_URL=http://localhost:8080/api
```

### 3. Chạy development server

```bash
npm start
```

Ứng dụng sẽ mở tại `http://localhost:3000`

## 🏗️ Cấu trúc Thư mục

```
frontend/
├── public/              # Tài nguyên tĩnh
│   └── index.html       # HTML chính
├── src/
│   ├── components/      # React components
│   │   ├── Dashboard.js         # Component chính
│   │   └── HealthContentCard.js # Card component
│   ├── services/        # API services
│   │   └── api.js       # Axios API client
│   ├── styles/          # CSS files
│   │   ├── Dashboard.css         # Dashboard styling
│   │   └── HealthContentCard.css # Card styling
│   ├── App.js           # Root component
│   ├── App.css          # App styling
│   ├── index.js         # Entry point
│   └── index.css        # Global styling
├── package.json         # Dependencies
├── .env.example         # Environment template
└── README.md            # Documentation
```

## 🎯 Tính Năng

- ✅ Hiển thị danh sách bài viết sức khỏe
- ✅ Lọc theo danh mục (Tim mạch, Thần kinh, v.v.)
- ✅ Loading state với spinner
- ✅ Error handling thân thiện
- ✅ Responsive design (Mobile, Tablet, Desktop)
- ✅ Card hiển thị chi tiết bài viết (tiêu đề, tóm tắt, tác giả, ngày xuất bản, stats)
- ✅ UI đẹp mắt với gradient và animations

## 📝 Component Overview

### Dashboard Component

- Quản lý trạng thái (loading, error, data)
- Gọi API từ backend
- Hiển thị loading spinner trong khi chờ
- Xử lý error với nút Retry
- Lọc theo danh mục

### HealthContentCard Component

- Hiển thị thông tin bài viết
- Badge danh mục với màu sắc khác nhau
- Thống kê (views, likes, comments)
- Responsive image
- Hover effects

### API Service

- Axios client với base URL configuration
- Error handling centralized
- Các methods:
  - `getRecommendations()` - Lấy tất cả bài viết
  - `getContentById(id)` - Lấy bài viết theo ID
  - `getContentByCategory(category)` - Lọc theo danh mục
  - `getPublishedContent()` - Lấy bài đã xuất bản

## 🎨 Styling

- **Color Scheme**: Purple gradient (#667eea - #764ba2)
- **Typography**: Segoe UI, modern fonts
- **Effects**: Hover animations, transitions
- **Responsive**: Mobile-first design

## 🔄 API Integration

Frontend gọi API từ Spring Boot backend:

### Base URL:

```
http://localhost:8080/api
```

### Endpoints:

- `GET /api/health-contents` - Lấy tất cả bài viết
- `GET /api/health-contents/{id}` - Lấy bài viết theo ID
- `GET /api/health-contents?category={category}` - Lọc theo danh mục

### Response Format:

```json
[
  {
    "id": "string",
    "title": "string",
    "content": "string",
    "category": "CARDIOLOGY|NEUROLOGY|...",
    "author": "string",
    "publishDate": "2026-05-13T...",
    "imageUrl": "string",
    "stats": {
      "views": 0,
      "likes": 0,
      "comments": 0
    }
  }
]
```

## 🚢 Build for Production

```bash
npm run build
```

Tạo thư mục `build/` chứa ứng dụng production-ready.

## 📦 Dependencies

- **react**: UI framework
- **react-dom**: DOM rendering
- **axios**: HTTP client
- **react-scripts**: Build tools

## ⚙️ Environment Variables

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

## 🐛 Troubleshooting

### Lỗi CORS

Nếu gặp lỗi CORS, kiểm tra cấu hình backend Spring Boot.

### Lỗi kết nối API

- Kiểm tra backend đang chạy: `http://localhost:8080`
- Kiểm tra cấu hình `.env`
- Xem console browser cho chi tiết lỗi

### Port 3000 đã được sử dụng

```bash
PORT=3001 npm start
```

## 📚 Tài liệu Thêm

- [React Documentation](https://react.dev)
- [Axios Documentation](https://axios-http.com)
- [KINEDICAL Backend API](../README.md)

## 📄 License

MIT

## 👨‍💻 Developer

Phát triển cho KINEDICAL Healthcare Platform
