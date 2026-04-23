# Spring Boot Clean Architecture - Demo Project

A modern web application built with **Java 21**, **Spring Boot 3**, and **PostgreSQL**, following strictly the principles of **Clean Architecture** (Hexagonal Architecture).

## 🚀 Overview

This project serves as a showcase for building scalable and maintainable applications by separating business logic from infrastructure concerns. It includes features for user authentication, profile management, and article interaction (comments, reactions).

### 🏗️ Architecture Layers

The system is organized into four main layers:

1.  **Domain**: Core business rules and entities (`com.example.demo.domain`). Independent of any frameworks.
2.  **Application**: Use cases and port interfaces (`com.example.demo.application`). Coordinates the flow of data.
3.  **Infrastructure**: Technical implementations (`com.example.demo.infrastructure`).
    -   **Web Adapters**: REST Controllers.
    -   **Persistence Adapters**: JPA/PostgreSQL implementations.
    -   **Security**: JWT-based stateless authentication.
4.  **Shared**: Common utilities and cross-cutting concerns.

For more details, see [ARCHITECTURE.md](ARCHITECTURE.md) and [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md).

## 🛠️ Technology Stack

-   **Backend**: Java 21, Spring Boot 3.5.x
-   **Security**: Spring Security + Custom JWT Implementation
-   **Database**: PostgreSQL 16
-   **Persistence**: Spring Data JPA
-   **Configuration**: Dotenv for environment variables
-   **Build Tool**: Maven

## ✨ Key Features

-   **Authentication**:
    -   Secure Registration & Login
    -   JWT Access Tokens & Refresh Tokens
    -   Custom HMAC-SHA256 Token Signing
-   **Profile Management**:
    -   View/Update current user profile
-   **Article System**:
    -   List and View detailed articles (with Server-side Pagination)
    -   Interactive Comments
    -   Multi-type Reactions (LIKE, LOVE, WOW, etc.)
-   **Crawling & RSS Ingestion**:
    -   Automated news crawling from RSS feeds
    -   Dynamic news source management

## 🚦 Getting Started

### Prerequisites

-   Java 21 JDK
-   Docker and Docker Compose (recommended for Database)
-   Maven (or use the provided `./mvnw`)

### Setup & Installation

1.  **Clone the repository**
2.  **Configure Environment Variables**:
    Create or update the `.env` file in the root directory (refer to `.env` file for required keys).
    ```env
    DB_PORT=5432
    DB_NAME=demo_db
    DB_USERNAME=postgres
    DB_PASSWORD=your_password
    JWT_SECRET=your-secure-secret-key
    ```
3.  **Start the Database**:
    Use Docker Compose to spin up the PostgreSQL instance.
    ```bash
    docker-compose up -d
    ```
4.  **Run the Application**:
    ```bash
    ./mvnw spring-boot:run
    ```

## 📖 API Documentation

The complete list of REST endpoints and their request/response schemas can be found in:
👉 **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)**
## 🧪 API Testing

The project includes a comprehensive integration test suite for the REST API.

-   **Test File**: `http/ARTICLE_SERVICE_test.http`
-   **Environment**: `http/http-client.env.json`

You can run these tests directly from **IntelliJ IDEA** using the built-in HTTP Client or via the **JetBrains HTTP Client CLI**.

---

## 📂 Project Structure

```text
src/main/java/com/example/demo
├── domain                 # Core Domain (Entities/Models)
├── application            # Business Logic
│   ├── port               # In/Out Interfaces
│   └── service            # Use Case Implementations
├── infrastructure         # External Concerns
│   ├── adapter            # Web & Persistence Adapters
│   ├── config             # Spring Configuration
│   └── security           # JWT & Security Logic
└── shared                 # Shared Resources
```

---
*Created by Antigravity AI*
