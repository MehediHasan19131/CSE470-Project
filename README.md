# SmartCare: Integrated Healthcare Management and Telemedicine System

SmartCare is a full-stack healthcare platform that connects patients, doctors, hospitals, pharmacies, diagnostic centres, and ambulance services through role-based dashboards, telemedicine, and AI-assisted care tools.

![SmartCare Logo](src/main/resources/static/img/smartcare-logo.svg)

## Tech Stack

- **Frontend:** Bootstrap, HTML, Thymeleaf templates
- **Backend:** Java
- **Framework:** Spring Boot
- **Database:** MySQL
- **Architecture:** MVC

## Features

1. **Role-Based Access Control (RBAC)** — Secure access for Admin, Patient, Doctor, Hospital, Pharmacy, Diagnostic Centre, and Ambulance roles
2. **User Profile Management** — Create, update, and manage user accounts and personal details
3. **Health Profile** — Store medical history, allergies, vitals, and health records per patient
4. **Dashboard for Different User Roles** — Tailored dashboards for each stakeholder
5. **Admin Dashboard** — Centralized user management, settings, and platform oversight
6. **Search Doctor by Specialty** — Find doctors by speciality and location
7. **Online Appointment Booking** — Schedule consultations with available doctors and hospitals
8. **Appointment Reminder** — Automated reminders for upcoming appointments
9. **Telemedicine (Video/Audio Consultation)** — Remote consultations via video or audio
10. **AI-Powered Symptom Checker** — Intelligent symptom analysis and care guidance
11. **Pharmacy Service** — Browse pharmacies, medicines, and prescription fulfilment
12. **Ambulance Booking (Ride-Sharing Model)** — Request and track ambulance services on demand
13. **Medicine Reminder & Medicine History** — Track medication schedules and past prescriptions
14. **Online Payment** — Secure payment processing for appointments, medicines, and services
15. **Health Articles & Blogs** — Educational health content and wellness articles
16. **FAQ (Frequently Asked Questions)** — Self-service answers to common platform questions
17. **Ratings & Reviews** — Rate and review doctors, hospitals, ambulance, and pharmacy services
18. **Donation & Crowdfunding** — Support medical causes and patient fundraising campaigns
19. **Map Integration** — Location-based discovery of hospitals, pharmacies, and ambulance services
20. **Healthcare Service Enhancement (Future Scope)** — Planned expansions including lab integration, insurance, and wearable device sync

## Sprint 1 (Implemented)

- User login and authentication
- Role management
- Admin dashboard and settings
- Patient, Doctor, Hospital, Pharmacy, Diagnostic Centre, and Ambulance dashboards
- Doctor search by speciality and location
- Hospital and pharmacy listing APIs
- Admin APIs for users and settings
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
│   ├── static/img
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

## Setup

### Prerequisites

- Java 17 or newer
- Maven 3.8+
- Docker (recommended for MySQL) or a local MySQL 8 installation

---

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

### 3. Install Docker (optional, for MySQL)

```bash
brew install --cask docker
```

Open Docker Desktop and wait until it is running.

---

## Windows Setup

### 1. Install Java

1. Download **Eclipse Temurin JDK 17** (or newer) from [Adoptium](https://adoptium.net/).
2. Run the installer and enable **Set JAVA_HOME variable** and **Add to PATH**.
3. Open **Command Prompt** or **PowerShell** and verify:

```cmd
java -version
```

### 2. Install Maven

1. Download the binary zip from [Apache Maven](https://maven.apache.org/download.cgi).
2. Extract it to a folder such as `C:\Program Files\Apache\maven`.
3. Add Maven's `bin` folder to your system **PATH** (e.g. `C:\Program Files\Apache\maven\bin`).
4. Optionally set `MAVEN_HOME` to the Maven install directory.
5. Open a new terminal and verify:

```cmd
mvn -version
```

### 3. Install Docker Desktop (optional, for MySQL)

1. Download [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/).
2. Install and start Docker Desktop.
3. Ensure WSL 2 is enabled if prompted during setup.

### 4. Install Git (optional)

Download from [git-scm.com](https://git-scm.com/download/win) if you need to clone or push the repository.

---

## Database Setup (macOS & Windows)

### Start MySQL with Docker (recommended)

From the project root:

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

### Using local MySQL without Docker

1. Install MySQL 8 from [mysql.com](https://dev.mysql.com/downloads/mysql/) or via your package manager.
2. Create the database and user:

```sql
CREATE DATABASE healthcare_platform;
CREATE USER 'health_user'@'localhost' IDENTIFIED BY 'health_password';
GRANT ALL PRIVILEGES ON healthcare_platform.* TO 'health_user'@'localhost';
FLUSH PRIVILEGES;
```

3. Run the schema and seed scripts from the `sql/` folder if needed.

---

## Run the App

From the project folder:

```bash
mvn spring-boot:run
```

Open in your browser:

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
Ctrl + C
```

Stop MySQL Docker container:

```bash
docker compose down
```
