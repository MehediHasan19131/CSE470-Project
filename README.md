# SmartCare — Member 2: Review & Rating System

This is the personal submission for **Sprint 2, Member 2 (Nahian Mahmud, 24241288)**:
Review & Rating System, built on top of the team's shared SmartCare repository
(which already has Member 1's Authentication & User Management merged in — see
`docs/SPRINT2_MVC_ARCHITECTURE.md` for exactly what's new here vs. what's borrowed
unchanged so this project can run standalone).

## Tech Stack

Same as the rest of the project:

- **Frontend:** Bootstrap, HTML, Thymeleaf templates
- **Backend:** Java
- **Framework:** Spring Boot
- **Database:** MySQL — accessed with **plain JDBC only, no ORM** (no Hibernate/JPA)
- **Architecture:** MVC

## My Sprint 2 task (as assigned)

**Database:** Reviews, Ratings
**Backend:** Create Review, Update Review
**Frontend:** Review Form, Rating Display

## What's implemented here

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

## Project Structure

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

## Setup

### Prerequisites

- Java 17 or newer
- Maven 3.8+
- Docker (recommended for MySQL) or a local MySQL 8 installation

### Database

**Option A — Docker (recommended):**
```bash
docker compose up -d mysql
```
This creates the `healthcare_platform` database and a `health_user` account automatically.

**Option B — local MySQL you already have installed:**
```sql
CREATE DATABASE IF NOT EXISTS healthcare_platform;
CREATE USER IF NOT EXISTS 'health_user'@'localhost' IDENTIFIED BY 'health_password';
GRANT ALL PRIVILEGES ON healthcare_platform.* TO 'health_user'@'localhost';
FLUSH PRIVILEGES;
```
Either way, **you do not need to run any `.sql` file by hand** — Spring Boot runs
`schema.sql` → `review-schema.sql` → `data.sql` → `review-data.sql`, in that
order, automatically on startup. No Hibernate is involved.

### Run

```bash
mvn spring-boot:run
```
Open **http://localhost:8000**

## Demo Login

| Role | Email | Password |
| --- | --- | --- |
| Patient | patient@health.test | password123 |
| Doctor | doctor@health.test | password123 |
| Hospital | hospital@health.test | password123 |
| Pharmacy | pharmacy@health.test | password123 |
| Diagnostic Centre | diagnostic@health.test | password123 |
| Ambulance | ambulance@health.test | password123 |

(Full list of demo accounts in Member 1's README — same 7 accounts, all seeded here too.)

## Try it out

1. Log in as `patient@health.test` (or any non-admin account).
2. Go to **http://localhost:8000/reviews** to browse providers — Dr. Arif Khan
   already has a demo review + rating.
3. Click a provider to open their review page (`/reviews/{id}`) — see the
   **Rating Display** (average + review list) and the **Review Form** below it.
4. Submit the form once to create your review, submit again with a different
   rating to see it update in place instead of creating a duplicate.

## API Highlights

- `POST /api/reviews` — create a review
- `PUT /api/reviews/{id}` — update your own review
- `GET /api/reviews/target/{targetId}` — all reviews for a provider
- `GET /api/ratings/{targetId}` — a provider's average rating + count

Full request/response shapes: [docs/SPRINT2_API_ENDPOINTS.md](docs/SPRINT2_API_ENDPOINTS.md)

## Stop

```text
Ctrl + C
```
```bash
docker compose down
```
