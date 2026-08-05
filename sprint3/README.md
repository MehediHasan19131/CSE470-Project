# Sprint 3 — Additions (Telemedicine)

Same stack as Sprints 1–2: Java 17, Spring Boot 3.3, Spring Security (session login), Spring Data
JPA, MySQL, Thymeleaf + Bootstrap 5 — plus **Jitsi Meet** (free, no API key) for video calls.

This folder is **not standalone** — merge it on top of Sprint 1 + Sprint 2. It must be applied
*after* `sprint2/`, since several files here are Sprint 2's files with one more round of changes.

Sprint goal delivered — **Member 3: Telemedicine (Mehedi Hasan, 23301352)**:

- ✅ Database: Consultation Records
- ✅ Backend: Consultation APIs
- ✅ Frontend: Video Call Page + Consultation History
- ✅ Free tool: Jitsi Meet

## How to merge

### 1. Brand-new files — copy as-is

```text
src/main/java/com/healthcare/platform/model/Consultation.java
src/main/java/com/healthcare/platform/repository/ConsultationRepository.java
src/main/java/com/healthcare/platform/dto/ConsultationResponse.java
src/main/java/com/healthcare/platform/dto/ConsultationStartRequest.java
src/main/java/com/healthcare/platform/dto/ConsultationStatusUpdateRequest.java
src/main/java/com/healthcare/platform/dto/ConsultationNotesUpdateRequest.java
src/main/java/com/healthcare/platform/service/ConsultationService.java
src/main/java/com/healthcare/platform/controller/ConsultationApiController.java

src/main/resources/templates/video-call.html
src/main/resources/templates/consultation-history.html
```

### 2. Files that REPLACE the Sprint 2 version

Each has a `MERGE NOTE` comment at the top explaining exactly what changed on top of Sprint 2:

```text
src/main/java/com/healthcare/platform/controller/WebController.java
src/main/java/com/healthcare/platform/HealthcarePlatformApplication.java
src/main/resources/templates/dashboard-patient.html
src/main/resources/templates/dashboard-doctor.html
src/main/resources/templates/appointment-booking.html
src/main/resources/templates/appointment-management.html
```

No changes to `SecurityConfig.java` — the new `/api/consultations/**` and `/telemedicine/**`
routes are already covered by `anyRequest().authenticated()`, and access control (only the
patient/doctor on that appointment, only the doctor can write notes) is enforced in
`ConsultationService`.

### 3. Database

`spring.jpa.hibernate.ddl-auto=update` auto-creates the new `consultations` table on the next
`mvn spring-boot:run`. `sprint3/sql/sprint3_schema.sql` is included for your ERD/report.

### 4. Run it

```bash
mvn spring-boot:run
```

Demo flow with the existing Sprint 1 accounts (password123):

1. Log in as `patient@health.test` → **Book an appointment** → confirm the flow, or use the
   already-`confirmed` demo appointment.
2. Log in as `doctor@health.test` → **Manage appointments** → click **Video call** on the
   confirmed appointment → the Jitsi room opens.
3. Log in as `patient@health.test` again (a second browser/incognito window works well for a live
   demo) → **Video consultations** → **Join** → both sides land in the same Jitsi room.
4. End the call — the doctor's side shows a notes/prescription form; save it, then check
   **Consultation history** as the patient to see the record.

A demo `COMPLETED` consultation (with sample notes and a prescription) is seeded on the existing
confirmed appointment so **Consultation History** has something to show immediately.

## New API endpoints

```http
POST   /api/consultations                  { appointmentId }  → creates or re-opens the room
GET    /api/consultations/me                list mine (patient or doctor)
GET    /api/consultations/{id}
PATCH  /api/consultations/{id}/status       { status: IN_PROGRESS | COMPLETED | CANCELLED }
PATCH  /api/consultations/{id}/notes        { notes, prescription }  — doctor only
```

## New page routes

```text
/telemedicine/history          consultation list (patient or doctor view)
/telemedicine/call/{id}        Jitsi video call page
```

## Design notes

- **Database:** `Consultation` is 1:1 with a confirmed `Appointment` (unique `appointment_id`),
  carrying a unique `room_name` used as the Jitsi room, a status lifecycle
  (`SCHEDULED → IN_PROGRESS → COMPLETED`, or `CANCELLED`), and doctor-authored `notes` /
  `prescription` fields — the actual "consultation record."
- **Backend:** `ConsultationService.start()` is idempotent — calling it twice for the same
  appointment (patient joins, then doctor joins) returns the *same* room instead of creating a
  duplicate, so both parties always land in one call. Only the appointment's patient or doctor can
  access, join, or update a consultation; only the doctor can write notes/prescription.
- **Frontend:** `video-call.html` loads the **Jitsi Meet External API**
  (`https://meet.jit.si/external_api.js`, free, no signup) and embeds the call in an iframe sized
  to the panel. Joining/leaving the Jitsi call automatically syncs the consultation status via the
  API (`videoConferenceJoined` → `IN_PROGRESS`, `videoConferenceLeft`/`readyToClose` → `COMPLETED`).
  `consultation-history.html` lists past and upcoming calls with a **Join** button and, once
  completed, the doctor's notes and prescription inline.
