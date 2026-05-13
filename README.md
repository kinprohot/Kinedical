# KINEDICAL

KINEDICAL là hệ thống y tế mẫu được xây dựng bằng Spring Boot, MongoDB và FastAPI. Dự án bao gồm:

- Backend Core: xử lý API chính, quản lý người dùng, hồ sơ bệnh án, nội dung y khoa.
- Recommendation AI: dịch vụ FastAPI sinh gợi ý bài viết dựa trên thuật toán TF-IDF, cosine similarity và hybrid scoring.
- Database: MongoDB lưu dữ liệu người dùng, hồ sơ bệnh án và nội dung y tế.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data MongoDB
- SpringDoc OpenAPI (Swagger UI)
- Python 3.12
- FastAPI
- scikit-learn, numpy
- MongoDB
- Docker / docker-compose

## Tích hợp Swagger UI

### Spring Boot

Sau khi thêm dependency `springdoc-openapi-starter-webmvc-ui`, Spring Boot sẽ tự động sinh tài liệu OpenAPI.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### FastAPI

FastAPI tự động tạo tài liệu API tại:

- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

## Cài đặt bằng Docker

1. Build và khởi chạy tất cả dịch vụ:

```bash
docker compose up --build
```

2. Các service sẽ chạy:

- Spring Boot: `http://localhost:8080`
- FastAPI: `http://localhost:8000`
- MongoDB: `mongodb://localhost:27017`

3. Truy cập tài liệu API:

- Spring Boot Swagger UI: `http://localhost:8080/swagger-ui.html`
- FastAPI Swagger UI: `http://localhost:8000/docs`

## Cấu trúc thư mục cơ bản

```
New folder/
├── Dockerfile
├── Dockerfile.fastapi
├── docker-compose.yml
├── pom.xml
├── requirements.txt
├── README.md
├── recommend_api.py
├── test_recommend_api.py
├── src/
│   ├── main/
│   │   ├── java/com/kinedical/
│   │   │   ├── config/
│   │   │   │   └── WebClientConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── HealthContentController.java
│   │   │   │   └── MedicalRecordController.java
│   │   │   ├── dto/
│   │   │   │   └── MedicalRecordDto.java
│   │   │   ├── model/
│   │   │   │   ├── HealthContent.java
│   │   │   │   ├── MedicalRecord.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── HealthContentRepository.java
│   │   │   │   ├── MedicalRecordRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/
│   │   │   │   ├── HealthContentService.java
│   │   │   │   └── MedicalRecordService.java
│   │   │   └── KineMedicalApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/dashboard.html
│   └── test/
│       └── java/com/kinedical/
│           ├── controller/MedicalRecordControllerTest.java
│           └── service/MedicalRecordServiceTest.java
└──
```

## Ghi chú

- Nếu muốn mở rộng API recommendation, bạn có thể thêm các model embedding và hệ thống đánh giá dựa trên lịch sử đọc.
- Trong Docker, `docker-compose.yml` đã cấu hình network chung để Spring Boot và FastAPI có thể gọi nhau bằng tên dịch vụ.

## Hướng dẫn phát triển

### Build dự án Spring Boot

```bash
mvn clean package
```

Hoặc chỉ build ứng dụng khi thay đổi mã nguồn:

```bash
mvn spring-boot:run
```

### Chạy test Spring Boot

```bash
mvn test
```

### Chạy test FastAPI

1. Cài dependencies:

```bash
pip install -r requirements.txt
```

2. Chạy pytest:

```bash
pytest test_recommend_api.py
```

### Chạy dự án bằng Docker

```bash
docker compose up --build
```

### Deploy lên GitHub

1. Đảm bảo project đã có `Dockerfile`, `Dockerfile.fastapi`, `docker-compose.yml`.
2. Đẩy mã nguồn lên repository GitHub.
3. Thiết lập GitHub Actions (nếu cần) để build và deploy tự động.

Ví dụ workflow GitHub Actions có thể gồm các bước:

- Build image Spring Boot và FastAPI
- Chạy unit test Java
- Chạy pytest cho FastAPI
- Đẩy container lên registry hoặc deploy lên môi trường mục tiêu
