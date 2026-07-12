# MVC Architecture

SmartCare follows a standard Spring Boot MVC structure.

## Layers

```text
Browser / API Client
        |
Controller Layer (web + api)
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

Application configuration, including Spring Security and demo data seeding.

```text
src/main/java/com/healthcare/platform/config/security
```

JWT authentication service and filter.

```text
src/main/java/com/healthcare/platform/controller/web
```

MVC page controllers. Handle browser requests and return Thymeleaf views.

```text
src/main/java/com/healthcare/platform/controller/api
```

REST API controllers. Handle JSON requests and return API responses.

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

Static assets such as CSS and images.

```text
sql/schema.sql
```

Database schema reference.

## Current Sprint Modules

- Authentication and role-based access
- Admin dashboard and settings APIs
- Patient, doctor, hospital, and pharmacy dashboards
- Doctor search and facility listings
- JWT-protected dashboard and admin APIs
