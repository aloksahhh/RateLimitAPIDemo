# 🚦 Distributed Rate Limiting API using Spring Boot, Redis & Bucket4j

A backend application that implements **distributed API rate limiting** using Spring Boot, Redis, and Bucket4j. It protects REST APIs from excessive requests by applying rate limits based on both user ID and client IP address.

The project uses the **Token Bucket Algorithm** to control incoming requests and Redis to store rate-limit bucket states.

## 🎯 Project Purpose

**This is a demo and learning-oriented project developed to understand, implement, and test the concepts of API Rate Limiting using Spring Boot, Redis, and Bucket4j.**

The primary objective is to gain practical knowledge of how rate limiting works in backend applications and how the Token Bucket Algorithm can be implemented using Bucket4j.

Through this project, the following concepts are explored:

- Understanding the fundamentals of API Rate Limiting.
- Learning how the Token Bucket Algorithm controls incoming requests.
- Implementing rate limiting using the Bucket4j library.
- Integrating Redis for storing distributed rate-limit bucket states.
- Applying request limits based on user IDs and IP addresses.
- Testing API behavior when request limits are exceeded.
- Understanding how Spring Boot Filters intercept and control HTTP requests.

**Note:** This project is intended for educational purposes, experimentation, and concept validation. It is not a production-ready implementation. Additional security, authentication, concurrency handling, monitoring, and error-handling improvements would be required before production use.

## 🚀 Features

- **User-Based Rate Limiting:** Limits API requests based on the user ID provided through the `X-User-Id` HTTP header.
- **IP-Based Rate Limiting:** Applies an independent request limit based on the client's IP address.
- **Distributed Rate Limiting:** Uses Redis to maintain shared bucket state.
- **Token Bucket Algorithm:** Implements request control using Bucket4j.
- **Automatic Token Refill:** Tokens are gradually replenished over a one-minute period.
- **HTTP 429 Response:** Rejects requests when the configured rate limit is exceeded.
- **Spring Boot Filter:** Intercepts incoming HTTP requests before they reach the controller.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Web MVC | REST API development |
| Spring Data Redis | Redis integration |
| Redis | Distributed state storage |
| Bucket4j 8.14.0 | Rate-limiting implementation |
| Lettuce | Redis client |
| Maven | Dependency management |

## 🏗️ Project Architecture

```text
Client / Postman
       |
       | HTTP Request
       | X-User-Id Header
       v
RateLimitFilter
       |
       v
RateLimiterService
       |
       |----------------------|
       v                      v
  User Bucket              IP Bucket
       |                      |
       |----------------------|
                  |
                  v
          Bucket4j ProxyManager
                  |
                  v
                Redis
                  |
          Rate Limit Decision
                  |
          |---------------|
          v               v
       Allowed         Rejected
          |               |
          v               v
     Controller        HTTP 429
          |
          v
     HTTP 200
```

## 📂 Project Structure

```text
RateLimitDemoApplication/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/RateLimitDemoApplication/
│   │   │   │
│   │   │   ├── RateLimitDemoApplication.java
│   │   │   ├── RedisTest.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   └── Bucket4jConfig.java
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   └── TestController.java
│   │   │   │
│   │   │   ├── filter/
│   │   │   │   └── RateLimitFilter.java
│   │   │   │
│   │   │   └── service/
│   │   │       └── RateLimiterService.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
├── mvnw
└── README.md
```

## ⚙️ Rate Limiting Configuration

The application currently applies the following configuration:

| Parameter | Configuration |
|---|---|
| Algorithm | Token Bucket |
| User request limit | 10 requests per minute |
| IP request limit | 10 requests per minute |
| Token consumption | 1 token per request |
| Token refill | Gradually over 1 minute |
| Storage | Redis |
| Rejected request status | HTTP 429 |

**Note:** User and IP limits are independent. A request must successfully consume one token from both buckets to reach the controller.

## 🔄 How It Works

1. A client sends an HTTP request to the Spring Boot application.
2. `RateLimitFilter` intercepts the incoming request.
3. The filter extracts the user ID from the `X-User-Id` header.
4. The client's IP address is identified.
5. `RateLimiterService` retrieves the user and IP rate-limit buckets.
6. Bucket4j checks whether both buckets have available tokens.
7. If both checks succeed, the request proceeds to the controller.
8. If either check fails, the application returns HTTP 429.

## 🧠 Understanding the Token Bucket Algorithm

The Token Bucket Algorithm is a rate-limiting technique in which requests consume tokens from a bucket.

- A bucket has a predefined token capacity.
- Every incoming request consumes one token.
- Requests are rejected when insufficient tokens are available.
- Tokens are gradually refilled over time.
- Once tokens become available, new requests can be accepted.

This approach allows controlled request bursts while maintaining an average request rate.

## 💻 Installation and Setup

### Prerequisites

Make sure you have installed:

- Java JDK 21
- Redis
- Maven (optional, because Maven Wrapper is included)
- IntelliJ IDEA or another Java IDE
- Postman or curl

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/RateLimitDemoApplication.git

cd RateLimitDemoApplication
```

### 2. Start Redis

If you are using macOS with Homebrew:

```bash
brew install redis
```

Start Redis:

```bash
brew services start redis
```

Verify Redis connectivity:

```bash
redis-cli ping
```

Expected output:

```text
PONG
```

Redis runs on the default port `6379`.

### 3. Run the Spring Boot Application

Navigate to the project directory:

```bash
cd RateLimitDemoApplication
```

Give Maven Wrapper execution permission if required:

```bash
chmod +x mvnw
```

Start the application:

```bash
./mvnw spring-boot:run
```

The backend server starts at:

```text
http://localhost:8080
```

## 🧪 API Testing

### Test Endpoint

**HTTP Method:** GET

**Endpoint:**

```http
http://localhost:8080/api/test
```

**Request Header:**

```http
X-User-Id: user101
```

### Successful Response

**HTTP Status:** `200 OK`

```text
Request Successful rate limit not exceed
```

### Rate Limit Exceeded

**HTTP Status:** `429 Too Many Requests`

```text
Too many request.please try again later.
```

### Test Using cURL

Send a single request:

```bash
curl -i \
-H "X-User-Id: user101" \
http://localhost:8080/api/test
```

Send multiple requests:

```bash
for i in {1..15}
do
  echo "Request $i"

  curl -s -o /dev/null \
  -w "%{http_code}\n" \
  -H "X-User-Id: user101" \
  http://localhost:8080/api/test
done
```

With fresh buckets and no previous requests, the first 10 requests should normally return `200`, and subsequent requests should return `429`.

## 🔍 Redis Inspection

Check Redis connectivity:

```bash
redis-cli ping
```

View rate-limit keys:

```bash
redis-cli keys "rate-limit:*"
```

The application uses keys similar to:

```text
rate-limit:user:user101
rate-limit:ip:127.0.0.1
```

Bucket4j stores serialized bucket state in Redis, so the values are not necessarily human-readable.

## 📚 Key Concepts Demonstrated

This project demonstrates practical backend engineering concepts:

- Spring Boot Dependency Injection
- Servlet Filters and Request Interception
- REST API Development
- Redis Integration
- Distributed Rate Limiting
- Token Bucket Algorithm
- Bucket4j Configuration
- Lettuce Redis Client
- Maven Dependency Management
- HTTP Status Code Handling

## 🔮 Future Improvements

- Integrate Spring Security and JWT authentication.
- Implement configurable rate limits for different APIs.
- Improve atomic handling of user and IP bucket consumption.
- Add custom JSON responses for HTTP 429 errors.
- Implement `Retry-After` response headers.
- Add Redis bucket expiration and cleanup.
- Add Docker and Docker Compose support.
- Implement centralized logging and monitoring.
- Add unit and integration tests.
- Support multiple Spring Boot instances.

## 👨‍💻 Author

**Alok Kumar Sah**

---

⭐ If you find this project useful, consider giving it a star!
