
# 🚀 Mailboom - SaaS de Email Marketing

**Mailboom** es una plataforma de envío masivo de correos electrónicos diseñada para ser económica, escalable y profesional. Permite a pequeñas y medianas empresas gestionar sus audiencias y enviar campañas personalizadas con el respaldo de la infraestructura de Amazon Web Services (AWS).

## 🛠️ Tecnologías

Este proyecto utiliza un stack moderno y robusto enfocado en la escalabilidad y el rendimiento:

* **Backend:** Java 17+ con **Spring Boot 3**.
* **Frontend:** **Angular** (v16+) con Angular Material.
* **Base de Datos:** **PostgreSQL** (utilizando tipos JSONB para flexibilidad de contactos).
* **Infraestructura de Envío:** **Amazon SES (Simple Email Service)**.
* **Seguridad:** Spring Security + **JWT (JSON Web Tokens)**.
* **Gestión de Tareas:** Spring Events (MVP) / Preparado para RabbitMQ (Escalabilidad).

---

## 🏛️ Arquitectura y Estructura de Carpetas

Aplicamos **Arquitectura Hexagonal (Puertos y Adaptadores)** y principios de **DDD (Domain-Driven Design)** para mantener el núcleo del negocio aislado de la tecnología.

### Backend (Java)

```text
src/main/java/com/mailboom
│
├── domain                  # Capa de Dominio (Sin dependencias externas)
│   ├── model               # Entidades (User, Campaign) y Value Objects
│   ├── ports               # Interfaces (UserRepository, EmailSender)
│   └── exceptions          # Excepciones de negocio
│
├── application             # Capa de Aplicación (Orquestación)
│   ├── usecases            # Casos de uso (ExecuteCampaign, ImportContacts)
│   └── dto                 # Objetos de transferencia de datos
│
├── infrastructure          # Capa de Infraestructura (Implementaciones)
│   ├── adapters            # Implementación de Puertos (AWS, Postgres)
│   ├── security            # Configuración de JWT y Auth
│   └── config              # Beans de Spring y configuración general
│
└── web                     # Capa de Entrada (Controladores API Rest)
    └── controllers         # Endpoints para el frontend

```

---

## ✨ Funcionalidades (MVP - 3 Semanas)

### 👤 Gestión de Usuarios

* Registro y autenticación mediante JWT.
* Control de planes de suscripción (Límites de envío por franjas).

### 👥 Gestión de Contactos

* CRUD completo de contactos.
* **Carga Masiva:** Importación de lotes de contactos mediante JSON/Batch.
* **Campos Personalizados:** Flexibilidad total mediante almacenamiento JSONB.

### 📧 Campañas de Email

* Creación de campañas con asunto y contenido HTML.
* **Branding Inteligente:** Envíos en nombre del cliente "a través de Mailboom".
* Configuración de `Reply-To` para que el cliente reciba respuestas directas.
* Envío masivo integrado con AWS SES.

### 📊 Panel de Administración

* Métricas globales de uso de la plataforma.
* Control de reputación y monitoreo de envíos por usuario.

---

## 🚀 Instalación y Requisitos

### Requisitos Previos

* Java 17 o superior.
* Node.js & Angular CLI.
* PostgreSQL 14+.
* Cuenta de AWS con acceso a SES.

### Configuración Rápida

1. Clonar el repositorio.
2. Configurar las variables de entorno en `application.yml`:
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


3. Ejecutar el backend: `./mvnw spring-boot:run`
4. Ejecutar el frontend: `ng serve`

---

## 📈 Roadmap de Escalabilidad

* [ ] Integración de colas de mensajería (Amazon SQS / RabbitMQ).
* [ ] Tracking de eventos (Open rate & Click rate) mediante Webhooks.
* [ ] Editor de plantillas Drag & Drop.

---