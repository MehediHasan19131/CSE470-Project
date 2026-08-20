# One Care / SmartCare — MVC Architecture

This document describes how the codebase is organised. The project follows a layered
**Model–View–Controller** architecture on top of Spring Boot.

## Request flow

```
Browser / API client
        │
        ▼
  Controller layer      ← Spring MVC (@Controller / @RestController)
        │                  validates input, calls services, picks the view
        ▼
  Service layer         ← business rules (@Service)
        │
        ▼
  Repository layer      ← data access (Spring Data JPA + hand-written JDBC)
        │
        ▼
   MySQL database

  View layer            ← Thymeleaf + Bootstrap templates rendered by the controllers
```

Security is enforced by a Spring Security filter chain (`config/SecurityConfig`) that runs before
any controller, plus a JWT filter for stateless API access.

## Source layout

```
src/main/java/com/healthcare/platform/
├── HealthcarePlatformApplication.java   # entry point + demo-data seeder
├── config/                              # SecurityConfig, seeders
├── security/                            # JWT filter & token service
├── controller/                          # web + REST controllers (incl. sprint1/, sprint2/)
├── service/                             # business logic (incl. sprint2/)
├── repository/                          # Spring Data JPA repositories
├── model/                               # JPA entities + enums
├── dto/                                 # request/response records
├── auth/                                # authentication & registration module (JDBC)
├── admin/                               # admin user-management module
├── blog/                                # health blog module
├── review/                              # ratings & reviews module
└── healthprofile/                       # medical history & allergies module

src/main/resources/
├── templates/                           # Thymeleaf views (+ fragments/, sprint1/, sprint2/)
├── static/css, static/img               # Bootstrap theme + assets
└── *.sql                                # schema/seed scripts run at startup
```

## Layers in detail

### Model (`model/`)
JPA entities mapped to database tables: `User`, `Profile`, `Appointment`, `Medicine`, `Order`,
`Campaign`, `Donation`, **`Payment`**, `Notification`, `Consultation`, `Ambulance`,
`BedAvailability`, `TestOffer`, `MedicineReminder`, and the `UserRole` enum, among others.

### View (`templates/`)
Server-rendered Thymeleaf pages styled with Bootstrap. Shared chrome lives in
`templates/fragments/` — `dashboard-layout.html` (head, role-aware navbar, page headings) and
`site-footer.html` (the public footer). Role dashboards are `dashboard-<role>.html`.

### Controller (`controller/` + feature packages)
Each controller is thin: it reads the request, delegates to a service, and returns a view name or
JSON. Web controllers render Thymeleaf; `*ApiController` classes return JSON for the REST API.

### Service (`service/` + feature packages)
All business rules live here — appointment booking, dashboards, payments, donations, ambulance
dispatch, medicine reminders, the AI symptom checker, etc.

### Repository (`repository/` + feature packages)
Spring Data JPA interfaces for most modules. The **auth**, **review**, **blog** and
**health-profile** modules deliberately use hand-written JDBC (`JdbcTemplate`) instead of JPA, to
satisfy the course's "no ORM for my module" requirement — both styles coexist against the same
MySQL database.

## Feature modules

| Module | Package(s) | Notes |
|--------|-----------|-------|
| Auth & registration | `auth`, `config`, `security` | Session + JWT login, BCrypt, role-based approval |
| Admin | `admin` | Approve / block / unblock / edit / delete any account |
| Doctors & patients | `controller`, `service`, `model` (sprint1) | Search by specialty, records |
| Appointments | `controller/sprint2`, `service/sprint2` | Booking + reminders |
| Telemedicine | `controller`, `service` | Video/audio consultation |
| AI symptom checker | `service` (`AiChatService`, `OllamaClient`) | Optional local Ollama model |
| Pharmacy | `controller`, `service` | Store, orders |
| Ambulance | `controller`, `service` | Ride-sharing style booking |
| Medicine reminders | `controller`, `service`, `model` | Reminders + dose history |
| **Online payment** | `controller/PaymentController`, `service/PaymentService`, `model/Payment` | **bKash / Bank sandbox checkout + ledger** |
| Blog | `blog` | Health articles & comments (JDBC) |
| Reviews | `review` | Ratings for providers (JDBC) |
| Donations | `controller`, `service`, `model` | Crowdfunding campaigns |
| Health profile | `healthprofile` | Medical history & allergies (JDBC) |
| **Map** | `controller/MapController`, `service/MapService` | **Leaflet + OpenStreetMap provider map** |
| **FAQ** | `controller/PublicController` | **Public help page** |

## Cross-cutting notes

- **Two user representations, one `users` table:** the JPA `User` entity (used by most modules and
  Hibernate DDL) and the plain `AuthUser` (used by the JDBC auth module) both map to `users`.
- **Sandbox payments:** `PaymentService` and `DonationService` never contact a real gateway — every
  payment is recorded as `SUCCESS` with a generated transaction id (`app_settings.payment_gateway =
  sandbox`).
- **Seeding:** `HealthcarePlatformApplication` seeds demo users, providers, campaigns and facility
  data on startup if the tables are empty.
