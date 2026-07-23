# Sprint 2 — Additions

Same stack as Sprint 1: Java 17, Spring Boot 3.3, Spring Security (session login), Spring Data JPA,
MySQL, Thymeleaf + Bootstrap 5, and now **OpenStreetMap + Leaflet.js** for the ambulance map.

This folder is **not a standalone project** — it only contains the files that are new or changed for
Sprint 2. Copy/merge them into the main project (one level up) to activate Sprint 2.

Sprint goal delivered:

- ✅ Appointment Booking (+ reminders)
- ✅ Reviews (ratings for doctors, hospitals, pharmacies, ambulance providers)
- ✅ Medicine Ordering
- ✅ Ambulance Booking + live Map tracking — **Member 4: Mehedi Hasan (23301352)**

## How to merge

### 1. Brand-new files — copy as-is

Copy everything under `sprint2/src` into the main project's `src`, keeping the same relative paths.
None of these paths exist yet in Sprint 1, so there is nothing to overwrite:

```text
src/main/java/com/healthcare/platform/model/Ambulance.java
src/main/java/com/healthcare/platform/model/AmbulanceRequest.java
src/main/java/com/healthcare/platform/model/MedicineOrder.java
src/main/java/com/healthcare/platform/model/MedicineOrderItem.java

src/main/java/com/healthcare/platform/repository/AmbulanceRepository.java
src/main/java/com/healthcare/platform/repository/AmbulanceRequestRepository.java
src/main/java/com/healthcare/platform/repository/MedicineOrderRepository.java

src/main/java/com/healthcare/platform/dto/Ambulance*.java
src/main/java/com/healthcare/platform/dto/Appointment*.java
src/main/java/com/healthcare/platform/dto/Rating*.java
src/main/java/com/healthcare/platform/dto/Medicine*.java

src/main/java/com/healthcare/platform/service/AmbulanceService.java
src/main/java/com/healthcare/platform/service/AppointmentService.java
src/main/java/com/healthcare/platform/service/RatingService.java
src/main/java/com/healthcare/platform/service/MedicineOrderService.java

src/main/java/com/healthcare/platform/controller/AmbulanceApiController.java
src/main/java/com/healthcare/platform/controller/AppointmentApiController.java
src/main/java/com/healthcare/platform/controller/RatingApiController.java
src/main/java/com/healthcare/platform/controller/MedicineOrderApiController.java

src/main/resources/templates/dashboard-ambulance.html
src/main/resources/templates/ambulance-booking.html
src/main/resources/templates/appointment-booking.html
src/main/resources/templates/appointment-management.html
src/main/resources/templates/reviews.html
src/main/resources/templates/medicine-ordering.html
src/main/resources/templates/order-management.html
```

### 2. Files that REPLACE existing Sprint 1 files

These already exist in the main project. Overwrite them with the Sprint 2 version (each file has a
`MERGE NOTE` comment at the top explaining exactly what changed):

```text
src/main/java/com/healthcare/platform/controller/WebController.java
src/main/java/com/healthcare/platform/HealthcarePlatformApplication.java
src/main/resources/templates/dashboard-patient.html
src/main/resources/templates/dashboard-doctor.html
src/main/resources/templates/dashboard-pharmacy.html
```

No changes are needed to `SecurityConfig.java` — all new `/api/**` routes are already covered by the
existing `anyRequest().authenticated()` rule, and role checks are enforced inside each service
(patients book, doctors confirm, pharmacies fulfill, ambulance providers dispatch).

### 3. Database

`spring.jpa.hibernate.ddl-auto=update` is already set in `application.properties`, so Hibernate
creates the 4 new tables automatically on the next `mvn spring-boot:run` — no manual SQL required.
`sprint2/sql/sprint2_schema.sql` is included only for your ERD/report.

New tables: `ambulances`, `ambulance_requests`, `medicine_orders`, `medicine_order_items`.

### 4. Run it

```bash
mvn spring-boot:run
```

Log in with the same Sprint 1 demo accounts (password123):

| Role | Email | What to try |
| --- | --- | --- |
| Patient | patient@health.test | Book an appointment, order medicine, request an ambulance, leave a review |
| Doctor | doctor@health.test | Confirm/complete appointments, see reminders |
| Pharmacy | pharmacy@health.test | Confirm and dispatch medicine orders |
| Ambulance | ambulance@health.test | Accept requests, update GPS location, toggle vehicle availability |

The ambulance account is seeded with 3 demo vehicles (`DHAKA-AMB-101/102/103`) so the map has
something to show immediately.

## New API endpoints

```http
# Ambulance (Member 4)
GET    /api/ambulances?lat=&lng=&availableOnly=
GET    /api/ambulances/mine
PATCH  /api/ambulances/{id}/location
PATCH  /api/ambulances/{id}/availability
POST   /api/ambulance-requests
GET    /api/ambulance-requests/me
GET    /api/ambulance-requests/incoming
GET    /api/ambulance-requests/{id}
PATCH  /api/ambulance-requests/{id}/status
PATCH  /api/ambulance-requests/{id}/cancel

# Appointment booking + reminders
POST   /api/appointments
GET    /api/appointments/me
GET    /api/appointments/reminders
PATCH  /api/appointments/{id}/status

# Reviews
POST   /api/ratings
GET    /api/ratings/target/{userId}
GET    /api/ratings/me

# Medicine ordering
GET    /api/medicines
POST   /api/medicine-orders
GET    /api/medicine-orders/me
GET    /api/medicine-orders/pharmacy
PATCH  /api/medicine-orders/{id}/status
```

## New page routes

```text
/appointments/book      patient — search doctors and book
/appointments/manage    doctor  — confirm / complete / cancel
/ambulance/book         patient — Leaflet map booking + live tracking
/medicines/order        patient — pharmacy catalog + cart + checkout
/orders/manage          pharmacy — fulfill medicine orders
/reviews                any role — browse & submit ratings
```

## Ambulance + Map design notes (Member 4)

- **Database:** `Ambulance` (a vehicle owned by an AMBULANCE-role account) and `AmbulanceRequest`
  (a ride request with pickup/drop coordinates and a status lifecycle:
  `REQUESTED → ACCEPTED → EN_ROUTE → COMPLETED`, or `CANCELLED`).
- **Backend:** `AmbulanceService` auto-assigns the nearest available vehicle using the Haversine
  formula, estimates a fare from `baseFare + perKmRate × distance`, and exposes tracking/incoming/
  status-update endpoints for the request-ambulance and track-ambulance flows.
- **Frontend:** `ambulance-booking.html` uses **Leaflet.js** tiles from **OpenStreetMap**
  (`tile.openstreetmap.org`) with click-to-pin pickup/drop, `navigator.geolocation` for GPS,
  best-effort reverse geocoding via the free Nominatim API, a nearby-fleet list, and a live tracking
  panel that polls the request every 5 seconds until it's completed or cancelled.
  `dashboard-ambulance.html` is the driver-side counterpart: toggle vehicle availability, push GPS
  location, and accept/advance/complete requests.
