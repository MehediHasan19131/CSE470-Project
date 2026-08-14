# MVC Architecture — Member 2: Review & Rating System

This is my (Member 2's) personal submission for Sprint 2, built on top of the
team's shared SmartCare repository — which already has Member 1's Sprint 1
work (Authentication & User Management) merged in. My assigned task:
**Database: Reviews, Ratings · Backend: Create Review, Update Review ·
Frontend: Review Form, Rating Display.**

## What's new in this submission vs. what's borrowed unchanged

Everything under `src/main/java/com/healthcare/platform/review/`, plus
`review-schema.sql`, `review-data.sql`, and two new Thymeleaf templates
(`review-target.html`, `reviews-directory.html`), is new this sprint.

Everything else in this zip — `auth/`, `config/`, `controller/`, `dto/`,
`model/`, `service/`, `login.html`/`register.html`/`profile.html`/
`logged-out.html`, `pom.xml`, `docker-compose.yml`, `HealthcarePlatformApplication.java` —
is Member 1's Sprint 1 work, copied **unchanged**. It's included only so this
project compiles and runs standalone (I need a working login to know who's
posting a review). In the actual group repo these files already exist from
Member 1's merge — don't overwrite them with this copy, they're identical.

The only shared file this submission *touches* is `application.properties`,
and only to add two lines so Spring Boot also picks up `review-schema.sql` /
`review-data.sql` on startup (see below). `SecurityConfig.java` needed **no**
changes — the existing `anyRequest().authenticated()` rule already covers
every new route this sprint adds.

## No ORM, anywhere in this submission

Same course rule Member 1's auth module follows, applied to `reviews` and `ratings`:

- **`Review`** / **`RatingSummary`** / **`ProviderSummary`** (`com.healthcare.platform.review`) —
  plain classes, not `@Entity`
- **`ReviewJdbcRepository`** — every query is hand-written SQL (`JdbcTemplate`),
  every row mapped to `Review`/`RatingSummary`/`ProviderSummary` by hand in a `RowMapper`
- **Table creation** — `src/main/resources/review-schema.sql`, run automatically
  by Spring Boot on startup via plain JDBC — no Hibernate auto-DDL
- **Demo data** — `src/main/resources/review-data.sql`, same mechanism

## Layers

```text
Browser / API Client
        |
Controller Layer   (review/ReviewWebController - pages,
                     review/ReviewApiController - JSON)
        |
Service Layer      (review/ReviewService - validation, keeps `ratings` in sync)
        |
Repository Layer   (review/ReviewJdbcRepository - plain JDBC)
        |
MySQL Database
```

`ReviewService` also reads (never writes) `AuthUserJdbcRepository` — to check
a review's target actually exists and is a provider role, not another patient
or an admin — the same way `CurrentUserService` reads it to resolve the
logged-in user. No changes were made to `AuthUserJdbcRepository` itself.

## Why two tables (`reviews` and `ratings`) instead of one

The task lists **Reviews** and **Ratings** as separate database items, and
they do serve different purposes here:

- `reviews` is the source of truth — one row per (reviewer, target), holding
  the star rating *and* the written comment. This is what "Create Review" and
  "Update Review" write to, and what the review list on the Rating Display
  page reads from.
- `ratings` is a small, cheap-to-read aggregate — one row per provider with
  just the average and count. `ReviewJdbcRepository.refreshRatingSummary(...)`
  recomputes it from `reviews` after every create/update, so the Rating
  Display page (and the provider directory) never has to run an `AVG()`/`COUNT()`
  over the whole `reviews` table on every page load.

## Why "Create Review" and "Update Review" are separate endpoints

A reviewer can only have one review per provider (`UNIQUE (reviewer_id, target_id)`
on `reviews`). `POST /api/reviews` creates a new one and fails if you already
have one for that target; `PUT /api/reviews/{id}` edits your existing one. The
on-page form at `/reviews/{targetId}` calls whichever one is appropriate
automatically, so from a user's point of view it's just "submit the form" —
create vs. update is an implementation detail, matching the task's separate
backend line items underneath it.

## Two ways to reach the same page

1. **`/reviews/{targetId}`** — server-rendered (Thymeleaf), what a person uses
   in a browser. Shows the Rating Display and Review Form together, the way
   `profile.html` shows an "Account" panel and a "Status" panel together.
2. **`POST /api/reviews`, `PUT /api/reviews/{id}`, `GET /api/reviews/target/{id}`,
   `GET /api/ratings/{id}`** — JSON, for API/mobile clients, the same dual
   pattern Member 1 used for session login vs. JWT login.

## Folder Responsibilities

```text
src/main/java/com/healthcare/platform/review
```
Everything new for Sprint 2: the `reviews`/`ratings` model classes, the
JDBC repository, the service layer, and both controllers (page + JSON API).
Self-contained, plain JDBC, no ORM. DTOs for the JSON API live here too
(`ReviewCreateRequest`, `ReviewUpdateRequest`, `ReviewResponse`,
`RatingSummaryResponse`, `ErrorResponse`) rather than the top-level `dto`
package, since they're only used by this feature.

```text
src/main/resources/templates
```
Two new files: `review-target.html` (Review Form + Rating Display) and
`reviews-directory.html` (browse providers — a testing convenience, not one
of the two assigned frontend items; see README).
