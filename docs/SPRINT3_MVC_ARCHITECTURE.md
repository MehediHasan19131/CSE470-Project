# MVC Architecture — Member 1: Health Profile

This is my (Member 1's) personal submission for Sprint 3, built on top of the
team's shared SmartCare repository — which already has Sprint 1 (Authentication
& User Management) and Sprint 2 (Review & Rating System) merged in. My assigned
task: **Database: Medical History, Allergies · Backend: Add History, Update
History · Frontend: Health Profile Page.**

## What's new in this submission vs. what's borrowed unchanged

Everything under `src/main/java/com/healthcare/platform/healthprofile/`, plus
`health-schema.sql`, `health-data.sql`, and one new Thymeleaf template
(`health-profile.html`), is new this sprint.

Everything else in this zip — `auth/`, `admin/`, `config/`, `controller/`,
`dto/`, `model/`, `review/`, `service/`, the earlier templates, `pom.xml`,
`docker-compose.yml`, `HealthcarePlatformApplication.java` — is Sprint 1 +
Sprint 2 work, copied **unchanged except for five small, deliberate touch
points** (see below). It's included only so this project compiles and runs
standalone. In the actual group repo these files already exist from earlier
merges — don't overwrite them with this copy except for those five files.

## The five files this submission touches, and why

- **`config/SecurityConfig.java`** — one rule added: `.requestMatchers("/health-profile/**",
  "/api/health/**").hasRole("PATIENT")`, right next to the existing
  `/admin/**` → `hasRole("ADMIN")` rule. A health profile belongs to a
  patient, not a provider or admin account.
- **`auth/ProfileController.java`** — `addRoleDashboardData(...)` (already
  branching on role for the Sprint 2 review panels) now also adds a
  `historyCount`/`allergyCount` summary for `PATIENT`, so `/profile` can show
  a small "Health" card with a link into the full page - the same "quick
  link" pattern already used for the Admin panel link.
- **`templates/profile.html`** — one new panel, shown only for `PATIENT`,
  right above the existing "reviews you've written" panel: entry counts +
  an "Open health profile" button linking to `/health-profile`.
- **`admin/AdminUserService.java`** — `deleteUser(...)` now also calls
  `HealthProfileService.deleteAllForPatient(id)` before deleting the user
  row, for the exact same reason it already calls the review cleanup methods:
  `medical_history`/`allergies` both have a foreign key on `users.id`, so
  the delete would fail with a constraint violation otherwise.
- **`templates/admin-users.html`** — the delete-confirmation `confirm(...)`
  text now also mentions medical history/allergies, matching the cleanup
  `AdminUserService` now performs.

No other file was touched. In particular `AuthUserJdbcRepository` itself
is untouched - `HealthProfileService` never reads or writes it at all,
unlike `ReviewService` which reads it read-only to validate review targets.

## No ORM, anywhere in this submission

Same course rule Sprint 1's auth module and Sprint 2's review module follow,
applied to `medical_history` and `allergies`:

- **`MedicalHistoryEntry`** / **`Allergy`** (`com.healthcare.platform.healthprofile`) —
  plain classes, not `@Entity`
- **`HealthProfileJdbcRepository`** — every query is hand-written SQL
  (`JdbcTemplate`), every row mapped to `MedicalHistoryEntry`/`Allergy` by
  hand in a `RowMapper`
- **Table creation** — `src/main/resources/health-schema.sql`, run
  automatically by Spring Boot on startup via plain JDBC — no Hibernate
  auto-DDL
- **Demo data** — `src/main/resources/health-data.sql`, same mechanism

## Layers

```text
Browser / API Client
        |
Controller Layer   (healthprofile/HealthProfileWebController - the Health
                     Profile Page, healthprofile/HealthProfileApiController - JSON)
        |
Service Layer      (healthprofile/HealthProfileService - validation,
                     ownership checks: a patient can only touch their own records)
        |
Repository Layer   (healthprofile/HealthProfileJdbcRepository - plain JDBC)
        |
MySQL Database
```

## Why two tables (`medical_history` and `allergies`) instead of one

The task lists **Medical History** and **Allergies** as separate database
items, and — like Sprint 2's `reviews`/`ratings` split — they hold genuinely
different shapes of data:

- `medical_history` — a condition/diagnosis/procedure name, an optional date,
  and free-text notes.
- `allergies` — an allergen name, a required severity (`MILD`/`MODERATE`/
  `SEVERE`), and optional reaction notes.

Unlike `reviews`/`ratings`, neither table here is a derived aggregate of the
other - both are primary, directly-editable tables, each with their own
"Add"/"Update" flow, both surfaced together on the one Health Profile Page.

## Why "Add History" and "Update History" cover both tables

The task names the two backend items generically ("History") rather than
listing four items (add/update × medical history/allergies). This submission
reads that as "Add"/"Update" being the two operations a patient performs on
either kind of health record, not a name scoped to `medical_history` alone -
`HealthProfileService` exposes `addHistory`/`updateHistory` for the medical
history table and `addAllergy`/`updateAllergy` for the allergies table, and
both are wired into the same page and the same two HTTP ideas (`POST` to
create, `PUT`/a same-shape `POST` form to edit). Delete is a small extra
beyond what was assigned - a patient occasionally needs to remove a
mis-entered record - mirroring how Sprint 2 added a "browse providers" page
beyond its two assigned frontend items.

## One page, two panels, query-string edit mode

`GET /health-profile` renders both database pieces at once - a Medical
History panel and an Allergies panel, each with its own list + add/edit form
- the same way `review-target.html` combines Rating Display and Review Form
on one page.

Editing an existing entry doesn't route to a separate page (the way
`/admin/users/{id}/edit` does): instead `?editHistory={id}` or
`?editAllergy={id}` on the same URL pre-fills that panel's form in "edit"
mode instead of "add" mode. This keeps the feature on the single URL the
task names ("Health Profile Page", singular) rather than growing into an
admin-style list-page + form-page pair, while still reusing the exact same
form markup for both add and edit - the same trade-off Sprint 2's
`review-target.html` made for its one review-per-target form.

## Folder Responsibilities

```text
src/main/java/com/healthcare/platform/healthprofile
```
Everything new for Sprint 3: the `medical_history`/`allergies` model classes,
the JDBC repository, the service layer, and both controllers (page + JSON
API). Self-contained, plain JDBC, no ORM. DTOs for the JSON API live here
too (`MedicalHistoryCreateRequest`, `MedicalHistoryUpdateRequest`,
`MedicalHistoryResponse`, `AllergyCreateRequest`, `AllergyUpdateRequest`,
`AllergyResponse`, `ErrorResponse`) rather than the top-level `dto` package,
matching how Sprint 2 kept its review DTOs inside `review/`.

```text
src/main/resources/templates
```
One new file: `health-profile.html` (Medical History panel + Allergies
panel, each with a list and an add/edit form).
