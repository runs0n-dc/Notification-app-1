# 📬 Event Notification System (ENS)

A Java Spring Boot–based **Event Notification System** that accepts incoming events (EMAIL, SMS, PUSH), queues them via RabbitMQ, processes them asynchronously, and posts a callback response to the provided `callbackUrl`.

---

## ✅ Features

- Accepts events via REST endpoint: `POST /api/events`
- Validates inputs using Jakarta Bean Validation
- Sends events to dedicated RabbitMQ queues: `email.queue`, `sms.queue`, `push.queue`
- Processes events asynchronously with simulated delays
- Randomly fails 10% of events to simulate error scenarios
- Sends callback to the provided URL with status (`Completed` / `Failed`)
- Graceful shutdown support

---

## 📦 Docker Compose Setup

To run the system locally using Docker:

### Prerequisites

- Docker & Docker Compose installed

### Run the System

```bash
docker compose up --build
```

This starts:

Spring Boot application on port 8080

RabbitMQ broker with default UI at http://localhost:15672 (user/pass: guest/guest)
```
POST http://localhost:8080/api/events
```
Accepted eventType values:
1.EMAIL
2.SMS
3.PUSH
