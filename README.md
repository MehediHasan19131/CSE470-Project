# SmartCare: Integrated Healthcare Management and Telemedicine System - Sprint 1

This project uses:

- Frontend: Bootstrap and HTML with Thymeleaf templates
- Backend: Java
- Framework: Spring Boot
- Database: MySQL
- Architecture: MVC

## Sprint Features

- User login
- Role management
- Admin dashboard and settings
- Patient dashboard
- Doctor dashboard
- Hospital dashboard
- Pharmacy dashboard
- Diagnostic centre dashboard
- Ambulance dashboard
- Doctor search by speciality and location
- Hospital listing API
- Pharmacy listing API
- Admin APIs
- MySQL database models and seed data

## Project Structure

```text
.
├── docs
│   ├── API_ENDPOINTS.md
│   ├── GITHUB_PUSH_GUIDE.md
│   └── MVC_ARCHITECTURE.md
├── sql
│   ├── schema.sql
│   └── seed.sql
├── src/main/java/com/healthcare/platform
│   ├── config
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
├── src/main/resources
│   ├── static/css
│   ├── templates
│   └── application.properties
├── docker-compose.yml
├── pom.xml
└── README.md
```

More details:

- [MVC architecture](docs/MVC_ARCHITECTURE.md)
- [API endpoints](docs/API_ENDPOINTS.md)
- [GitHub push guide](docs/GITHUB_PUSH_GUIDE.md)

## macOS Setup

### 1. Install Java

Install Java 17 or newer.

If Homebrew is available:

```bash
brew install openjdk@17
```

If `brew` is not available, install Java from:

```text
https://adoptium.net/
```

After installation, check:

```bash
java -version
```

### 2. Install Maven

If Homebrew is available:

```bash
brew install maven
```

If `brew` is not available, install Maven from:

```text
https://maven.apache.org/download.cgi
```

Check:

```bash
mvn -version
```

### 3. Start MySQL

Recommended option with Docker:

```bash
docker compose up -d mysql
```

Database values:

```text
Database: healthcare_platform
Username: health_user
Password: health_password
Host: localhost
Port: 3306
```

The Spring app uses this connection:

```properties
DB_URL=jdbc:mysql://localhost:3306/healthcare_platform?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=health_user
DB_PASSWORD=health_password
```

If you do not set these environment variables, the app uses the same default values automatically.

### 4. Run the App

From the project folder:

```bash
mvn spring-boot:run
```

Open:

```text
http://127.0.0.1:8000
```

## Demo Login

The app seeds demo users automatically on startup.

| Role | Email | Password |
| --- | --- | --- |
| Admin | admin@health.test | password123 |
| Patient | patient@health.test | password123 |
| Doctor | doctor@health.test | password123 |
| Hospital | hospital@health.test | password123 |
| Pharmacy | pharmacy@health.test | password123 |
| Diagnostic Centre | diagnostic@health.test | password123 |
| Ambulance | ambulance@health.test | password123 |

## API Highlights

- `POST /api/auth/login`
- `GET /api/me`
- `GET /api/dashboard`
- `GET /api/doctors/search?speciality=Cardiology&location=Dhaka`
- `GET /api/hospitals`
- `GET /api/pharmacies`
- `GET /api/admin/users`
- `PATCH /api/admin/users/{userId}/role`
- `GET /api/admin/settings`
- `PUT /api/admin/settings`

## Stop the App

Press:

```text
Control + C
```

Stop MySQL Docker container:

```bash
docker compose down
```
# CSE470-Project
