# SmartCare MVC Architecture

SmartCare follows a layered Model-View-Controller (MVC) architecture. Each class has one clear responsibility, and feature subfolders keep related classes easy to find without mixing layers.

## Request flow

```text
Browser or API client
        |
        v
Controller layer       Handles HTTP requests and selects an HTML view or JSON response
        |
        v
Service layer          Applies business rules and coordinates work
        |
        v
Repository layer       Reads and writes data through JPA or JDBC
        |
        v
Model and MySQL        Domain data and persistent storage

View layer             Thymeleaf templates rendered for browser requests
```

Spring Security runs before the controller layer and enforces authentication and role-based access.

## Source layout

```text
src/main/java/com/healthcare/platform/
├── HealthcarePlatformApplication.java     # application entry point and demo-data seeder
├── config/                                # Spring configuration and seeders
├── security/                              # authentication filters and JWT support
├── controller/                            # all web and REST controllers
│   ├── admin/ auth/ blog/ healthprofile/ review/
│   ├── appointment/ facility/
│   └── ... feature controllers
├── service/                               # all business services and schedulers
│   ├── admin/ auth/ blog/ healthprofile/ review/ appointment/
│   └── ... shared and feature services
├── repository/                            # all JPA and JDBC repositories
│   ├── auth/ blog/ healthprofile/ review/
│   └── ... JPA repositories
├── model/                                 # entities, JDBC domain models, and enums
│   ├── auth/ blog/ healthprofile/ review/
│   └── ... shared JPA entities
└── dto/                                   # API and form request/response classes
    ├── auth/ blog/ healthprofile/ review/
    └── ... shared DTOs

src/main/resources/
├── templates/                             # Thymeleaf view files
│   ├── fragments/                         # shared page layout components
│   └── ... feature pages
├── static/                                # CSS, images, and browser assets
└── *.sql                                  # database schema and seed scripts
```

## Layer responsibilities

| Layer | Responsibility | Examples |
|---|---|---|
| Model | Represents domain data and relationships. | `User`, `Appointment`, `model/blog/Post`, `model/review/Review` |
| View | Renders the user interface with Thymeleaf. | `templates/dashboard-patient.html`, `templates/blood-requests.html` |
| Controller | Receives HTTP requests, calls services, returns an HTML view or JSON. | `controller/BloodDonationController`, `controller/blog/BlogWebController` |
| Service | Contains business rules and workflows. | `BloodDonationService`, `service/review/ReviewService` |
| Repository | Provides data access through Spring Data JPA or `JdbcTemplate`. | `DonorRepository`, `repository/blog/BlogJdbcRepository` |
| DTO | Carries request and response data between the API and clients. | `LoginRequest`, `dto/blog/PostResponse` |

## MVC implementation notes

- Web controllers return named templates from `src/main/resources/templates`.
- API controllers return JSON DTOs and are named `*ApiController`.
- Business logic is kept out of controllers and belongs in the service layer.
- Most persistence uses Spring Data JPA. The authentication, blog, review, and health-profile modules use `JdbcTemplate` repositories where plain JDBC is required.
- Feature subfolders appear inside a layer only; for example, blog code is separated into `controller/blog`, `service/blog`, `repository/blog`, `model/blog`, and `dto/blog`.

## Documentation

- `SmartCare-Class-Diagram.pdf` is the current class diagram for the project.
- API endpoint guides and the demo guide are retained because they describe how to use and test the finished system.
