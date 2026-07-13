# SmartCare — Healthcare Management System

**CSE470 Project | Sprint 1**

> A web-based integrated healthcare platform connecting patients, doctors, hospitals, diagnostic centres, pharmacies, and ambulance services.

---

## Sprint 1 — Hospital, Diagnostic Center & Pharmacy Module

**Member:** Rony Miah (24141084)

### Features

- **Facility Dashboard** — Browse all registered hospitals, diagnostic centres, and pharmacies in a card grid. Filter by facility type.
- **Search** — Search facilities by name, city/location, and type.
- **Facility Profile** — View detailed information, ratings, and reviews for each facility.
- **Role-based Dashboards** — Separate dashboard views for Admin, Patient, Doctor, Hospital, and Pharmacy roles.
- **Authentication** — Secure login with Spring Security form-based authentication.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 24, Spring Boot 3.3.4 |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT |
| Frontend | Thymeleaf, Bootstrap 5.3, Bootstrap Icons |
| Database | MySQL 8.4 (via Docker Compose) |
| Build | Maven |

---

## Quick Start

### Prerequisites

- Java 24+
- Docker Desktop (for MySQL)
- Maven

### 1. Start MySQL

```bash
docker compose up -d
```

### 2. Configure database

The app auto-creates tables and seeds demo data on first run.  
Default DB credentials: `root` / `root` (port 3306).

### 3. Build & run

```bash
mvn package -DskipTests
java -jar target/healthcare-platform-0.0.1-SNAPSHOT.jar
```

### 4. Open browser

```
http://localhost:8080/
```

---

## Demo Accounts

| Email | Password | Role |
|-------|----------|------|
| `admin@health.test` | `password123` | Admin |
| `patient@health.test` | `password123` | Patient |
| `doctor@health.test` | `password123` | Doctor |
| `hospital@health.test` | `password123` | Hospital |
| `pharmacy@health.test` | `password123` | Pharmacy |
| `diagnostic@health.test` | `password123` | Diagnostic Centre |
| `ambulance@health.test` | `password123` | Ambulance |

---

## Project Structure

```
src/main/java/com/healthcare/platform/
├── config/SecurityConfig.java         ← Spring Security + JWT config
├── controller/
│   ├── WebController.java             ← Login, logout, dashboard routing
│   └── sprint1/
│       └── FacilityController.java    ← Sprint 1: facility dashboard, search, profile
├── dto/
│   └── ServiceListingResponse.java    ← Unified listing response DTO
├── model/
│   ├── User.java / UserRole.java      ← Unified user with role enum
│   ├── Profile.java                   ← Address, city, service details
│   ├── Rating.java                    ← Reviews & scores
│   ├── Appointment.java               ← Appointment entity
│   ├── Medicine.java                  ← Pharmacy inventory
│   └── AppSetting.java                ← Key-value settings
├── repository/                        ← JPA repositories
├── security/
│   ├── JwtService.java                ← JWT token generation
│   └── JwtAuthenticationFilter.java   ← JWT auth filter
└── service/
    ├── CurrentUserService.java        ← Current authenticated user
    ├── DashboardService.java          ← Role-based dashboard data
    └── ListingService.java            ← Facility/doctor listings

src/main/resources/
├── templates/
│   ├── login.html                     ← Login page (branded)
│   ├── dashboard-*.html               ← Role-based dashboards
│   ├── fragments/dashboard-layout.html ← Shared layout
│   └── sprint1/facilities/            ← Sprint 1 views
│       ├── dashboard.html             ← Facility card grid
│       ├── search.html                ← Search form + results
│       └── profile.html               ← Facility detail page
├── static/
│   ├── css/styles.css                 ← Global styles
│   ├── css/sprint1.css                ← Sprint 1 styles
│   └── img/                           ← SVG brand assets
└── application.properties

database/
└── seed.sql                           ← Standalone SQL schema + seed data
```

---

## Sprint 1 Routes

| Route | Description |
|-------|-------------|
| `GET /` | Login page |
| `POST /login` | Form authentication |
| `GET /dashboard` | Role-based dashboard |
| `GET /facilities` | Facility dashboard (card grid) |
| `GET /facilities/search` | Search facilities |
| `GET /facilities/{type}/{id}` | Facility profile page |

**Type values:** `hospital`, `pharmacy`, `diagnostic` / `diagnostic-center`

---

## Roadmap

| Sprint | Module | Status |
|--------|--------|--------|
| Sprint 1 | Hospital, Diagnostic & Pharmacy (Member 3) | ✅ Complete |
| Sprint 2 | TBD | ⬜ Pending |
| Sprint 3 | TBD | ⬜ Pending |
| Sprint 4 | Dashboard shell, Admin APIs (Member 4) | ⬜ Pending |

---

## License

CSE470 course project — University of ...
