# KINEDICAL

KINEDICAL là hệ thống y tế mẫu được xây dựng bằng Spring Boot, MongoDB và FastAPI. Dự án bao gồm:

- Backend Core: xử lý API chính, quản lý người dùng, hồ sơ bệnh án, nội dung y khoa.
- Authorization: hợp lệ hoá đăng ký, đăng nhập JWT và phân quyền truy cập bệnh án theo vai trò.
- Recommendation AI: dịch vụ FastAPI sinh gợi ý bài viết dựa trên thuật toán TF-IDF, cosine similarity và hybrid scoring.
- Database: MongoDB lưu dữ liệu người dùng, hồ sơ bệnh án và nội dung y tế.
- Bảo mật nâng cao: JWT authentication, role-based access control và audit logging để hỗ trợ các kịch bản tương tự HIPAA.

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

## Bảo mật và tuân thủ

KINEDICAL được thiết kế hướng tới các nguyên tắc bảo mật dữ liệu y tế giống HIPAA:

- Authentication & Authorization
  - JWT token cho API bảo mật trạng thái không phiên.
  - Phân quyền theo vai trò (`PATIENT`, `DOCTOR`, `ADMIN`) để chỉ cho phép truy cập bệnh án và nội dung phù hợp.
- Mật khẩu an toàn
  - Mật khẩu không bao giờ lưu dưới dạng văn bản thuần.
  - Sử dụng hashing mạnh (BCrypt) trên backend trước khi lưu vào MongoDB.
- Dữ liệu nhạy cảm
  - Các trường nhạy cảm như mật khẩu không được serialize ra JSON trong phản hồi API.
  - Trong một môi trường production, nên bật HTTPS/TLS để mã hóa dữ liệu khi truyền qua mạng.
- Audit và truy vết
  - Ghi lại hành động quan trọng như đăng nhập, đăng ký, tạo/cập nhật/xóa bệnh án.
  - Audit log giúp kiểm tra truy cập và phát hiện hành vi không hợp lệ.
- Lưu trữ và sao lưu
  - Đối với hệ thống thực tế, dữ liệu y tế cần được lưu trữ với mã hóa khi nghỉ (encryption at rest), sao lưu định kỳ và kiểm soát truy cập cơ sở dữ liệu.

> Lưu ý: Đây là một phiên bản mẫu cho bài tập/demo. Để đáp ứng tiêu chuẩn HIPAA thực sự, cần bổ sung thêm bảo mật nâng cao như mã hóa dữ liệu trường riêng lẻ, quản lý khoá an toàn, kiểm toán đầy đủ và kiểm tra chính sách nội bộ.

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
- Mặc định backend tạo một tài khoản admin thử nghiệm: `admin@kinedical.vn` / `P@ssw0rd123`.

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
