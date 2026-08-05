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

---

## Sprint 2 — Appointment & Service Booking
**Member:** Rony Miah (24141084)

### Features
- Book Appointment (form + submit)
- Cancel Appointment (patient, doctor, or admin)
- Confirm Appointment (doctor or admin only)
- Appointment Status (pending → confirmed/cancelled)
- Appointment History (filtered by role)

### Routes
| Method | Path | Description |
|--------|------|-------------|
| GET | `/appointments` | Appointment history |
| GET | `/appointments/book` | Booking form |
| POST | `/appointments/book` | Submit booking |
| POST | `/appointments/{id}/cancel` | Cancel appointment |
| POST | `/appointments/{id}/confirm` | Confirm appointment |

### Files
| File | Purpose |
|------|---------|
| `controller/sprint2/AppointmentController.java` | All appointment routes |
| `service/sprint2/AppointmentService.java` | Business logic |
| `templates/sprint2/appointments/book.html` | Booking form UI |
| `templates/sprint2/appointments/history.html` | Appointment list UI |
| `static/css/sprint2.css` | Card and status styles |

### Dependencies (shared with main project)
- `model/Appointment.java`, `model/User.java`, `model/UserRole.java`
- `repository/AppointmentRepository.java`, `repository/UserRepository.java`
- `service/CurrentUserService.java`, `service/ListingService.java`
- `dto/ServiceListingResponse.java`
- `templates/fragments/dashboard-layout.html`
- `static/css/styles.css`

---

## Sprint 2 — Review & Rating System
**Member:** Nahian Mahmud

### Task

**Database:** Reviews, Ratings
**Backend:** Create Review, Update Review
**Frontend:** Review Form, Rating Display

### What's implemented

- `reviews` table + `ratings` table (plain SQL, `src/main/resources/review-schema.sql`)
  - `reviews` — one row per (reviewer, target) pair: a 1–5 star rating + optional
    written comment. A reviewer can only have one review per provider — to
    change their mind they **update** that row, they don't create a second one.
  - `ratings` — one row per provider: a running average + review count, kept in
    sync with `reviews` every time a review is created or updated. This is what
    the "Rating Display" page reads from, instead of re-aggregating the whole
    `reviews` table on every request.
- **Create Review** — `POST /api/reviews`, and via the on-page form at `/reviews/{targetId}`
- **Update Review** — `PUT /api/reviews/{id}`, and via the same on-page form
  (it detects you already have a review for that provider and updates it instead)
- **Review Form** — `/reviews/{targetId}`, a star-rating + comment form (pure
  CSS star picker, no JS). Pre-fills with your existing review if you have one.
- **Rating Display** — the same page shows the provider's average rating (stars
  + number) and the full list of individual reviews.
- **Browse providers** — `/reviews`, a directory of every doctor/hospital/
  pharmacy/diagnostic centre/ambulance service with their current rating,
  linking into each one's review page. This wasn't one of the two assigned
  frontend items — it exists only so there's a way to reach a review page
  without typing a raw id into the URL, since the provider search/listing
  feature is a different member's task. Feel free to drop it once that page exists.
- 3 demo reviews seeded automatically on startup via `review-data.sql`, kept
  consistent with the `ratings` table so the pages have something to show
  immediately.

### Project structure

```text
.
├── docs
│   ├── SPRINT2_API_ENDPOINTS.md
│   ├── (your Sprint 1 docs stay here too)
│   └── SPRINT2_MVC_ARCHITECTURE.md
├── sql                        (reference copies - see src/main/resources for the real ones)
│   ├── schema.sql / seed.sql          (Member 1 - users, borrowed unchanged)
│   └── review-schema.sql / review-seed.sql   (Member 2 - reviews, ratings)
├── src/main/java/com/healthcare/platform
│   ├── review                 (Member 2: everything new this sprint - all JDBC, no ORM)
│   ├── auth, config, controller, dto, model, service   (Member 1 - borrowed unchanged)
├── src/main/resources
│   ├── schema.sql / data.sql              (Member 1 - users)
│   ├── review-schema.sql / review-data.sql (Member 2 - reviews, ratings)
│   ├── static/css, static/img
│   ├── templates
│   └── application.properties  (2 lines added - see below)
├── docker-compose.yml
├── pom.xml
└── README.md
```

More details: [MVC architecture](docs/SPRINT2_MVC_ARCHITECTURE.md) ·
[API endpoints](docs/SPRINT2_API_ENDPOINTS.md) — for pushing to GitHub, see the `docs/GITHUB_PUSH_GUIDE.md` already in this repo from Sprint 1, same steps apply.

### API highlights

- `POST /api/reviews` — create a review
- `PUT /api/reviews/{id}` — update your own review
- `GET /api/reviews/target/{targetId}` — all reviews for a provider
- `GET /api/ratings/{targetId}` — a provider's average rating + count

Full request/response shapes: [docs/SPRINT2_API_ENDPOINTS.md](docs/SPRINT2_API_ENDPOINTS.md)

---

## Sprint 2 — Ambulance Module
**Member:** Mehedi Hasan

Ambulance booking and dispatch: providers register vehicles and toggle
availability/location, patients request the nearest available ambulance and
track the request through its lifecycle.

This member also built appointment, medicine-order and rating implementations
during Sprint 2. They duplicated the modules Rony, Sami and Nahian delivered, so
after integration the more complete implementation of each was kept and these
duplicates were removed; the code remains in git history on `sprint2-Mehedi`.

### Page routes

| Method | Path | Access | Purpose |
|--------|------|--------|---------|
| GET | `/ambulance/book` | any logged-in user | Ambulance booking page with map |
| GET | `/dashboard` | AMBULANCE role | Ambulance provider dashboard |

### API endpoints

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/ambulances` | List ambulances |
| GET | `/api/ambulances/mine` | Vehicles owned by the logged-in provider |
| POST | `/api/ambulance-requests` | Book an ambulance |
| GET | `/api/ambulance-requests/me` | Requests made by the logged-in patient |
| GET | `/api/ambulance-requests/incoming` | Requests addressed to the provider |
| GET | `/api/ambulance-requests/{id}` | One request |
