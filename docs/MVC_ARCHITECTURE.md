# MVC Architecture — Member 1: Authentication & User Management

This is my (Member 1's) personal submission, extracted from the team's shared
SmartCare repository. Everything below belongs to my assigned Sprint 1 task:
**Database: Users, Roles · Backend: Registration, Login, JWT Authentication,
Role-based Access · Frontend: Login Page, Registration Page, Profile Page.**

## What was removed from the full team repo, and why

The shared repo also contains other members' Sprint 1 work: role-specific
dashboards, an admin panel, doctor/hospital/pharmacy search and listings,
appointments, ratings, medicines, and a separate "health profile" (medical
history/vitals) feature. None of that is mine, so it isn't part of this
submission:

- `AdminApiController`, `DashboardApiController`, `SearchApiController`,
  `PharmacyApiController` and their DTOs
- `Appointment`, `Medicine`, `Profile` (medical profile), `Rating`, `TestOffer`,
  `HospitalService`, `HospitalAvailability`, `HospitalDoctorAvailability`,
  `AppSetting` models, repositories, and services
- The six `dashboard-*.html` templates and the shared dashboard-layout fragment

## No ORM, anywhere in this submission

The original shared repo used Spring Data JPA/Hibernate (an `@Entity User` class,
`UserRepository extends JpaRepository`, `spring.jpa.hibernate.ddl-auto=update`).
Per my course's rules, ORM isn't allowed, so this submission replaces all of that:

- **`AuthUser`** (`com.healthcare.platform.auth`) - a plain class, not an `@Entity`
- **`AuthUserJdbcRepository`** - every query is hand-written SQL (`JdbcTemplate`),
  every row is mapped to `AuthUser` by hand in a `RowMapper`
- **Table creation** - `src/main/resources/schema.sql`, run automatically by
  Spring Boot on startup via plain JDBC (`spring.sql.init.mode=always`) - no
  Hibernate auto-DDL involved
- **Demo data** - `src/main/resources/data.sql`, same mechanism

`UserRole` is a plain Java enum (no annotations) and is shared by everything above.

## Layers

```text
Browser / API Client
        |
Controller Layer   (auth/RegistrationController, auth/ProfileController,
                     auth/TokenAuthController, controller/WebController,
                     controller/AuthApiController)
        |
Service Layer      (auth/RegistrationService, auth/JwtService,
                     service/CurrentUserService)
        |
Repository Layer   (auth/AuthUserJdbcRepository - plain JDBC)
        |
MySQL Database
```

## Two ways to authenticate (both included, neither breaks the other)

1. **Session/cookie login** - `POST /login` (browser form) or `POST /api/auth/login`
   (JSON). Spring Security keeps you logged in via a session cookie (`JSESSIONID`).
2. **JWT login** - `POST /api/auth/token`. Stateless: you get a token back and send
   it yourself on every later request as `Authorization: Bearer <token>`.
   `JwtAuthFilter` checks for that header on every request; if it's missing, nothing
   happens and the session-based flow above still works exactly as before.

## Folder Responsibilities

```text
src/main/java/com/healthcare/platform/auth
```
Everything new for Sprint 1: registration, the profile page, and JWT
issuing/validation. Self-contained, plain JDBC, no ORM.

```text
src/main/java/com/healthcare/platform/config
```
Spring Security setup: which URLs are public, which need a login, which need a
specific role, and how a login is verified.

```text
src/main/java/com/healthcare/platform/controller
```
The login page (`WebController`) and the session-based login API
(`AuthApiController`).

```text
src/main/java/com/healthcare/platform/service
```
`CurrentUserService` - looks up the logged-in user's full record.

```text
src/main/java/com/healthcare/platform/model
```
Just `UserRole`, a plain enum.

```text
src/main/java/com/healthcare/platform/dto
```
Request/response shapes for the auth API.

```text
src/main/resources/templates
```
Thymeleaf + Bootstrap: `login.html`, `register.html`, `profile.html`, `logged-out.html`.
