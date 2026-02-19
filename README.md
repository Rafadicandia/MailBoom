# 🚀 Mailboom - Email Marketing SaaS

**Mailboom** is a bulk email sending platform designed to be affordable, scalable, and professional. It allows small and medium-sized businesses to manage their audiences and send personalized campaigns backed by the Amazon Web Services (AWS) infrastructure.

## 🛠️ Technologies

This project uses a modern and robust stack focused on scalability and performance:

* **Backend:** Java 17+ with **Spring Boot 3**.
* **Frontend:** **Angular** (v16+) with Angular Material.
* **Database:** **PostgreSQL** (using JSONB types for contact flexibility).
* **Sending Infrastructure:** **Amazon SES (Simple Email Service)**.
* **Security:** Spring Security + **JWT (JSON Web Tokens)**.
* **Task Management:** Spring Events (MVP) / Ready for RabbitMQ (Scalability).

---

## 🏛️ Architecture and Folder Structure

We apply **Hexagonal Architecture (Ports and Adapters)** and **DDD (Domain-Driven Design)** principles to keep the business core isolated from technology.

### Backend (Java)

```text
src/main/java/com/mailboom
│
├── domain                  # Domain Layer (No external dependencies)
│   ├── model               # Entities (User, Campaign) and Value Objects
│   ├── ports               # Interfaces (UserRepository, EmailSender)
│   └── exceptions          # Business exceptions
│
├── application             # Application Layer (Orchestration)
│   ├── usecases            # Use cases (ExecuteCampaign, ImportContacts)
│   └── dto                 # Data Transfer Objects
│
├── infrastructure          # Infrastructure Layer (Implementations)
│   ├── adapters            # Port Implementations (AWS, Postgres)
│   ├── security            # JWT and Auth Configuration
│   └── config              # Spring Beans and general configuration
│
└── web                     # Entry Layer (REST API Controllers)
    └── controllers         # Endpoints for the frontend

```

---

## ✨ Features (MVP - 3 Weeks)

### 👤 User Management

* Registration and authentication via JWT.
* Subscription plan control (Sending limits by tiers).

### 👥 Contact Management

* Complete CRUD of contacts.
* **Bulk Upload:** Import batches of contacts via JSON/Batch.
* **Custom Fields:** Total flexibility via JSONB storage.

### 📧 Email Campaigns

* Creation of campaigns with subject and HTML content.
* **Smart Branding:** Sending on behalf of the client "via Mailboom".
* `Reply-To` configuration so the client receives direct replies.
* Bulk sending integrated with AWS SES.

### 📊 Admin Panel

* Global platform usage metrics.
* Reputation control and sending monitoring per user.

---

## 🚀 Installation and Requirements

### Prerequisites

* Java 17 or higher.
* Node.js & Angular CLI.
* PostgreSQL 14+.
* AWS account with SES access.

### Quick Setup

1. Clone the repository.
2. Configure environment variables in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mailboom
    username: ${DB_USER}
    password: ${DB_PASS}
aws:
  ses:
    access-key: ${AWS_KEY}
    secret-key: ${AWS_SECRET}

```


3. Run the backend: `./mvnw spring-boot:run`
4. Run the frontend: `ng serve`

---

## 📈 Scalability Roadmap

* [ ] Message queue integration (Amazon SQS / RabbitMQ).
* [ ] Event tracking (Open rate & Click rate) via Webhooks.
* [ ] Drag & Drop template editor.
