# Tài liệu Phân tích Dự án (Project Analysis)

Dự án này được xây dựng theo kiến trúc **Clean Architecture** (hay còn gọi là Hexagonal Architecture) với mục tiêu tách biệt logic nghiệp vụ khỏi các chi tiết hạ tầng (database, framework, external services).

## 1. Cấu trúc thư mục và Công dụng của các File

Dự án được chia thành các Layer chính như sau:

### Layer 1: Domain (Cốt lõi nghiệp vụ)
Nằm tại `com.example.demo.domain`. Đây là tầng trung tâm, không phụ thuộc vào bất kỳ framework hay thư viện bên ngoài nào.
- `domain.model`: Chứa các Entity chính như `UserAccount`, `Article`, `Comment`, `RefreshToken`. Các class này định nghĩa dữ liệu và các quy tắc nghiệp vụ cơ bản.
- `domain.service`: (Nếu có) Chứa các logic nghiệp vụ phức tạp liên quan đến nhiều Entity.

### Layer 2: Application (Luồng xử lý nghiệp vụ)
Nằm tại `com.example.demo.application`. Tầng này điều phối các hoạt động của ứng dụng.
- `application.port.in`: Các Interface (Use Cases) định nghĩa những gì ứng dụng có thể làm (ví dụ: `AuthUseCase`, `ArticleUseCase`).
- `application.port.out`: Các Interface định nghĩa những gì ứng dụng cần từ bên ngoài (ví dụ: `UserRepository`, `AccessTokenProvider`).
- `application.service`: Triển khai thực tế của các Use Case. Ví dụ: `AuthService.java` chứa logic đăng ký, đăng nhập, và cấp phát token.

### Layer 3: Infrastructure (Chi tiết triển khai)
Nằm tại `com.example.demo.infrastructure`. Chứa mã nguồn cụ thể cho các công nghệ sử dụng.
- `infrastructure.adapter.in.web`: REST Controllers nhận HTTP Request và gọi xuống tầng Application (ví dụ: `AuthController`, `ArticleController`).
- `infrastructure.adapter.out.persistence`: Triển khai các Repository (truy cập database). Có cả bản **InMemory** (để test nhanh) và bản **JPA** (PostgreSQL).
- `infrastructure.security`: Chứa logic về bảo mật, JWT.
- `infrastructure.config`: Cấu hình Spring Boot (Bean, Security Filter Chain, Data Initializer).

### Layer 4: Shared (Dùng chung)
- `shared.exception`: Các ngoại lệ tùy chỉnh như `NotFoundException`, `UnauthorizedException`.

---

## 2. Luồng hoạt động tổng quát (Generic Flow)

**Request Flow:**
`Client` -> `Controller (In-Adapter)` -> `UseCase (Port-In Interface)` -> `Service (Application Implementation)` -> `Port-Out (Interface)` -> `Persistence/External Adapter (Out-Adapter)` -> `Database/Service`.

Điều này đảm bảo rằng logic nghiệp vụ (`Service`) chỉ biết về các Interface, giúp dễ dàng thay đổi Database (ví dụ từ InMemory sang PostgreSQL) mà không ảnh hưởng đến code xử lý chính.

---

## 3. Chi tiết luồng Security và JWT

Hệ thống sử dụng **Stateless Authentication** dựa trên **JWT (JSON Web Token)**.

### Thành phần chính:
1. `SecurityConfig.java`: Cấu hình Spring Security, tắt CSRF, đặt Session là STATELESS và đăng ký `JwtAuthenticationFilter`.
2. `JwtTokenService.java`: Chịu trách nhiệm tạo (issue) và kiểm tra (parse/validate) token. Nó tự triển khai việc ký HMAC-SHA256 thủ công.
3. `JwtAuthenticationFilter.java`: Một Filter chặn mọi Request để kiểm tra Header `Authorization`.

### Luồng Đăng nhập (Authentication):
1. **Request**: User gửi email/password tới `/api/auth/login`.
2. **Controller**: `AuthController` nhận dữ liệu và gọi `AuthService.login()`.
3. **Service**: `AuthService` kiểm tra user trong DB, so khớp mật khẩu (đã hash).
4. **Token Issuance**: Nếu hợp lệ, `AuthService` gọi `JwtTokenService` để tạo 2 tokens:
   - **Access Token**: Thời hạn ngắn (15 phút), chứa thông tin user (id, email, role).
   - **Refresh Token**: Thời hạn dài (7 ngày), lưu vào DB để cấp lại Access Token khi hết hạn.
5. **Response**: Trả về token cho Client.

### Luồng Xác thực các Request bảo mật (Authorization):
1. **Interceptor**: Mỗi khi các API yêu cầu bảo mật được gọi, `JwtAuthenticationFilter` sẽ chạy.
2. **Extraction**: Filter lấy token từ Header `Authorization: Bearer <token>`.
3. **Validation** (trong `JwtTokenService`):
   - Giải mã Header và Payload.
   - Ký lại (sign) `header.payload` bằng Secret Key và so sánh với chữ ký trong token.
   - Kiểm tra thời gian hết hạn (`exp`).
4. **Context Setting**: Nếu hợp lệ, Filter tạo một `AuthenticatedUserPrincipal` và đưa vào `SecurityContextHolder` của Spring.
5. **Access**: Các Controller/Service sau đó có thể lấy thông tin user hiện tại từ `SecurityContextHolder`.

---

## 4. Tổng kết các file quan trọng

| File | Chức năng |
| :--- | :--- |
| `SecurityConfig.java` | "Cổng bảo vệ" chính, định nghĩa API nào công khai, API nào cần login. |
| `JwtAuthenticationFilter.java` | "Người gác cổng", kiểm tra thẻ (token) của mỗi request. |
| `JwtTokenService.java` | "Máy in và kiểm định thẻ", tạo và verify JWT. |
| `AuthService.java` | Chứa logic nghiệp vụ về đăng ký, đăng nhập và quản lý token. |
| `UserAccount.java` | Entity đại diện cho người dùng trong hệ thống. |
| `JpaUserRepository.java` | Lớp giao tiếp với Database để tìm kiếm/lưu trữ người dùng. |
