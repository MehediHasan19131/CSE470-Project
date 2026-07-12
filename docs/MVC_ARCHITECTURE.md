# MVC Architecture

SmartCare follows a standard Spring Boot MVC structure.

## Layers

```text
Browser / API Client
        |
Controller Layer
        |
Service Layer
        |
Repository Layer
        |
MySQL Database
```

## Folder Responsibilities

```text
src/main/java/com/healthcare/platform/config
```

Application configuration, including Spring Security.

```text
src/main/java/com/healthcare/platform/controller
```

MVC page controllers and REST API controllers. Controllers receive HTTP requests, call services, and return views or JSON responses.

```text
src/main/java/com/healthcare/platform/service
```

Business logic. Services prepare dashboard data, current user data, search results, and listing responses.

```text
src/main/java/com/healthcare/platform/repository
```

Spring Data JPA repositories. Repositories communicate with MySQL.

```text
src/main/java/com/healthcare/platform/model
```

JPA entity classes that represent database tables.

```text
src/main/java/com/healthcare/platform/dto
```

Request and response objects used by REST APIs.

```text
src/main/resources/templates
```

Thymeleaf HTML templates using Bootstrap.

```text
src/main/resources/static
```

Static assets such as CSS.

## One exception: `com.healthcare.platform.auth` (Member 1)

Everything above uses Spring Data JPA. The registration and profile pages are
implemented differently, on purpose: they use plain JDBC (`JdbcTemplate`) with
hand-written SQL and a manual `RowMapper`, and their own plain `AuthUser` class
instead of the `@Entity User` class. No `@Entity`, no Spring Data repository
interface, no Hibernate anywhere in that package.

Both paths read/write the same physical `users` table - that's fine, a table can
be queried more than one way. The `auth` package also adds JWT-based login
(`POST /api/auth/token`) as a second, stateless way to authenticate, alongside
the existing session/cookie login - neither replaces the other.

```text
src/main/java/com/healthcare/platform/auth
```
Self-contained: registration, the profile page, and JWT issuing/validation, all
without an ORM.

## Current Sprint Modules

- Authentication and role-based access
- Admin dashboard
- Patient dashboard
- Doctor dashboard
- Hospital dashboard
- Pharmacy dashboard
- Diagnostic centre dashboard
- Ambulance dashboard
- Doctor search
- Hospital listing
- Pharmacy listing
- Admin users and settings APIs
