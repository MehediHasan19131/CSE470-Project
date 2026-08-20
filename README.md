# One Care — SmartCare Healthcare Platform

**CSE470 Software Engineering — Integrated Healthcare Management & Telemedicine System**

One Care (branded **SmartCare** in the app) is a role-based healthcare platform that connects
patients with doctors, hospitals, pharmacies, diagnostic centres and ambulance teams — covering
appointments, telemedicine, an AI symptom checker, pharmacy ordering, medicine reminders, health
records, reviews, crowdfunding, online payment and more, all in one Spring Boot application.

## Features

Role-Based Access Control · User Profile Management · Health Profile · Role dashboards ·
Admin Dashboard (approve / block / remove any account) · Search Doctor by Specialty ·
Online Appointment Booking · Appointment Reminders · Telemedicine (video/audio consultation) ·
AI-Powered Symptom Checker · Pharmacy Service · Ambulance Booking (ride-sharing model) ·
Medicine Reminder & History · **Online Payment (bKash / Bank — sandbox)** · Health Articles & Blog ·
**FAQ** · Ratings & Reviews · Donation & Crowdfunding · **Map Integration (OpenStreetMap)**.

## Tech stack

- **Backend:** Java 17, Spring Boot 3.3.4 (Spring MVC, Spring Security, Spring Data JPA + plain JDBC)
- **Frontend:** Thymeleaf + Bootstrap 5, Leaflet + OpenStreetMap for maps
- **Database:** MySQL 8 (via Docker Compose)
- **Auth:** Session login + JWT, BCrypt password hashing

## Architecture (MVC)

The application follows a layered Model–View–Controller structure under
`src/main/java/com/healthcare/platform`:

| Layer | Package(s) | Responsibility |
|-------|------------|----------------|
| **Controller** | `controller`, `auth`, `admin`, `blog`, `review`, `healthprofile` | HTTP endpoints (web pages + REST APIs) |
| **Service** | `service`, and the service classes inside the feature packages | Business logic |
| **Repository** | `repository`, `auth`, `review`, `blog`, `healthprofile` | Data access (Spring Data JPA + hand-written JDBC) |
| **Model** | `model` | JPA entities & domain enums |
| **DTO** | `dto` | Request/response shapes |
| **View** | `src/main/resources/templates` | Thymeleaf + Bootstrap pages |
| **Config / Security** | `config`, `security` | Spring Security, JWT filter, seeders |

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the full package-by-package breakdown and the
class diagram in `docs/SmartCare-Class-Diagram.svg`.

## Roles & permissions

Registration is open to everyone. **Patients** are activated instantly; **provider** accounts
(Doctor, Hospital, Pharmacy, Diagnostic, Ambulance) stay pending until an **admin approves** them.

| Capability | Patient | Doctor | Hospital | Diagnostic | Pharmacy | Ambulance | Admin |
|-----------|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Book appointments / order medicine / book care & ambulance | ✔ | | | | | | |
| Telemedicine | ✔ | ✔ | | | | | |
| Manage own facility / catalogue | | | ✔ | ✔ | ✔ | ✔ | |
| Health Blog — post & comment | ✔ | ✔ | ✔ | ✔ | | | |
| Health Blog — moderate (delete any) | | | | | | | ✔ |
| Crowdfunding — view & donate | ✔ | ✔ | ✔ | ✔ | | | |
| Make payments | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ | |
| View payments | own | own | own | own | own | own | **all (audit)** |
| Approve / block / remove any account | | | | | | | ✔ |
| Review Contact-us / Report submissions | | | | | | | ✔ |

The **admin never acts as a consumer** — no booking, ordering, telemedicine, blog posting, or making
payments. Admin is an operator: user management, moderation, payment audit, and report review.

Every logged-in user has an overflow (three-dots) menu with **Notifications, FAQ, Contact us,
Report a problem**, and **Logout**; admins additionally get **Review reports** there.

**Payments run through the bKash / Bank-Card checkout everywhere money changes hands:** crowdfunding
donations, **medicine orders**, **hospital service & diagnostic test bookings** ("Book & Pay" on a
facility's page), and **ambulance fares** (from *My ambulance requests*). Each **pharmacy manages its
own medicine catalogue** (per-facility products), just as hospitals manage their services and
diagnostic centres their tests; the selling pharmacy is shown on each medicine. Paid facility bookings
appear to the provider under *Manage facility / tests* as **Incoming bookings**.

## Getting started

**Prerequisites:** JDK 17+, Maven 3.9+, and Docker (for MySQL).

```bash
# 1. Start MySQL
docker compose up -d

# 2. Run the application
mvn spring-boot:run
```

Then open **http://localhost:8080**. On first start the app creates the schema and seeds demo data.

> The AI Symptom Checker is optional — it talks to a locally hosted Ollama model. If Ollama is not
> running, the rest of the app works normally and the AI page shows a friendly "unavailable" notice.

## Demo accounts

All demo accounts use the password **`password123`**.

| Role | Email |
|------|-------|
| Admin | `admin@health.test` |
| Patient | `patient@health.test` |
| Doctor | `doctor@health.test` |
| Hospital | `hospital@health.test` |
| Pharmacy | `pharmacy@health.test` |
| Diagnostic | `diagnostic@health.test` |
| Ambulance | `ambulance@health.test` |

New users can also self-register at **/register**. Patients are activated instantly; provider
accounts (doctor, hospital, pharmacy, diagnostic, ambulance) require **admin approval** before they
can log in.

## Team — One Care

| Name | GitHub |
|------|--------|
| Mehedi Hasan | MehediHasan19131 |
| Rony Miah | Saiful101 |
| Nahian Mahmud | nahianmahmud-2k1 |
| Imtiaz Zaman Sami | imtiazzamansami-arch |
