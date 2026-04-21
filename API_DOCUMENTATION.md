# System API Documentation

This document provides a detailed overview of the available REST API endpoints in the system.

## Authentication Module
**Base URL:** `/api/auth`

### 1. Register New User
Creates a new user account.

- **URL:** `/api/auth/register`
- **Method:** `POST`
- **Authentication Required:** No
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "fullName": "John Doe"
  }
  ```
- **Response (200 OK):** `AuthResult`
  ```json
  {
    "userId": "uuid",
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "USER",
    "accessToken": "jwt_token",
    "refreshToken": "refresh_token",
    "tokenType": "Bearer",
    "accessTokenExpiresAt": "2026-04-21T12:02:20Z",
    "refreshTokenExpiresAt": "2026-04-28T12:02:20Z"
  }
  ```

### 2. Login
Authenticates a user and returns JWT tokens.

- **URL:** `/api/auth/login`
- **Method:** `POST`
- **Authentication Required:** No
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response (200 OK):** `AuthResult` (Same as Register)

### 3. Forgot Password
Initiates the password recovery process.

- **URL:** `/api/auth/forgot-password`
- **Method:** `POST`
- **Authentication Required:** No
- **Request Body:**
  ```json
  {
    "email": "user@example.com"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "email": "user@example.com",
    "resetToken": "reset_uuid",
    "expiresAt": "2026-04-21T13:02:20Z",
    "message": "Password reset email sent"
  }
  ```

### 4. Refresh Token
Renews the access token using a valid refresh token.

- **URL:** `/api/auth/refresh`
- **Method:** `POST`
- **Authentication Required:** No
- **Request Body:**
  ```json
  {
    "refreshToken": "valid_refresh_token"
  }
  ```
- **Response (200 OK):** `AuthResult` (Same as Login)

---

## Profile Module
**Base URL:** `/api/profile`

### 1. Get My Profile
Retrieves the profile of the currently authenticated user.

- **URL:** `/api/profile/me`
- **Method:** `GET`
- **Authentication Required:** Yes (Bearer Token)
- **Response (200 OK):**
  ```json
  {
    "id": "uuid",
    "email": "user@example.com",
    "fullName": "John Doe",
    "avatarUrl": "https://example.com/avatar.jpg",
    "bio": "Developer and tech enthusiast",
    "role": "USER"
  }
  ```

### 2. Update My Profile
Updates the profile information for the authenticated user.

- **URL:** `/api/profile/me`
- **Method:** `PUT`
- **Authentication Required:** Yes (Bearer Token)
- **Request Body:**
  ```json
  {
    "fullName": "John Updated",
    "avatarUrl": "https://new-url.com/avatar.jpg",
    "bio": "New Bio Content"
  }
  ```
- **Response (200 OK):** Updated `ProfileView`

---

## Articles Module
**Base URL:** `/api/articles`

### 1. List Articles
Retrieves a list of available articles.

- **URL:** `/api/articles`
- **Method:** `GET`
- **Authentication Required:** No
- **Response (200 OK):** `Array<ArticleListItemView>`
  ```json
  [
    {
      "id": "uuid",
      "slug": "article-slug",
      "title": "Article Title",
      "summary": "Brief summary of the article",
      "thumbnailUrl": "image_url",
      "category": "Technology",
      "tags": ["AI", "Tech"],
      "authorName": "Author Name",
      "publishedAt": "2026-04-20T10:00:00Z",
      "source": {
        "id": "source_id",
        "name": "Source Name",
        "slug": "source-slug",
        "type": "RSS",
        "homePageUrl": "...",
        "rssUrl": "...",
        "logoUrl": "..."
      }
    }
  ]
  ```

### 2. Get Article Detail
Retrieves full details of a specific article.

- **URL:** `/api/articles/{articleId}`
- **Method:** `GET`
- **Authentication Required:** Optional (provide Bearer token to see current user's reaction)
- **Response (200 OK):**
  ```json
  {
    "id": "uuid",
    "externalId": "ext_123",
    "slug": "article-slug",
    "title": "Full Article Title",
    "summary": "...",
    "content": "<p>HTML Content here...</p>",
    "thumbnailUrl": "...",
    "category": "...",
    "tags": ["..."],
    "authorName": "...",
    "canonicalUrl": "...",
    "sourceArticleUrl": "...",
    "publishedAt": "...",
    "crawledAt": "...",
    "source": { ... },
    "reactions": {
      "counts": { "LIKE": 10, "WOW": 2 },
      "currentUserReaction": "LIKE",
      "total": 12
    },
    "comments": [
      {
        "id": "comment_uuid",
        "userId": "user_uuid",
        "userDisplayName": "John Doe",
        "content": "Great article!",
        "createdAt": "2026-04-21T08:00:00Z"
      }
    ],
    "metadata": { "key": "value" }
  }
  ```

### 3. Add Comment
Adds a comment to an article.

- **URL:** `/api/articles/{articleId}/comments`
- **Method:** `POST`
- **Authentication Required:** Yes (Bearer Token)
- **Request Body:**
  ```json
  {
    "content": "Your comment message here"
  }
  ```
- **Response (200 OK):** The newly created `CommentView`

### 4. React to Article
Adds or updates a reaction to an article.

- **URL:** `/api/articles/{articleId}/reactions`
- **Method:** `POST`
- **Authentication Required:** Yes (Bearer Token)
- **Request Body:**
  ```json
  {
    "reactionType": "LIKE" 
  }
  ```
  *Allowed `reactionType` values:* `LIKE`, `DISLIKE`, `LOVE`, `WOW`, `SAD`
- **Response (200 OK):** Updated `ReactionSummaryView`
