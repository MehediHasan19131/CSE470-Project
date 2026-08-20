# One Care / SmartCare — Demo Guide

A ready-to-follow script for demonstrating the project in about 5–7 minutes.

## Before you start

```bash
docker compose up -d      # start MySQL
mvn spring-boot:run       # start the app
```

Open **http://localhost:8080**. The first launch creates the schema and seeds demo data.

**Login (all demo accounts use password `password123`):**

| Role | Email |
|------|-------|
| Admin | `admin@health.test` |
| Patient | `patient@health.test` |
| Doctor | `doctor@health.test` |
| Hospital | `hospital@health.test` |
| Pharmacy | `pharmacy@health.test` |
| Diagnostic | `diagnostic@health.test` |
| Ambulance | `ambulance@health.test` |

## Suggested walkthrough

### 1. Landing page & registration (role-based)
- Show the landing page — note the footer (copyright, links) and the **role tiles** / **Create your account** button.
- Click **Doctor** → the register form opens with "Doctor" pre-selected. Register a new doctor
  (e.g. `newdoc@test.com`). You'll be told the account is **pending admin approval**.
- Register a **Patient** the same way → note it says you can **log in right away** (no approval).
- Try logging in as the new doctor → blocked with "pending admin approval".

### 2. Admin: approve, block, remove
- Log in as **admin@health.test**. From the dashboard, open the **Admin** panel → **Manage users**.
- Find the pending doctor → click **Approve**. Now that doctor can log in.
- Demonstrate control on any account: **Block** (they can no longer log in), **Unblock**, and **Delete**
  (with confirmation). Note admins can't block/delete themselves or the last admin.

### 3. Online payment (bKash / Bank) + donations
- Log in as **patient@health.test** → **Campaigns** → open a campaign → set an amount →
  **Continue to Payment**.
- On checkout, show the **bKash** tab (mobile number + PIN) and the **Bank / Card** tab.
- Pay → a **receipt** with a transaction id appears, the campaign total updates, and the payment
  shows under **Payments** (My payments). Emphasise: sandbox demo, no real charge.

### 4. Map integration
- Open **Find Care** (`/map`) → a Leaflet + OpenStreetMap map plots hospitals, doctors, pharmacies,
  diagnostic centres and ambulances from the database, colour-coded, with popups. (Also public at
  `/map` without login.)

### 5. Quick tour of the rest (as patient)
- **Doctors** → search by specialty; **Appointments** → book; **Telemedicine** → start a call;
  **Pharmacy** → order medicine; **Medicine Reminders**; **Health Profile** (medical history/allergies);
  **Reviews**; **Health Blog**; **AI Symptom Checker** (optional — needs a local Ollama model).
- **FAQ** (`/faq`) — public help page linked from the footer and nav.

## Talking points (architecture)
- **Layered MVC** Spring Boot app: Controller → Service → Repository → MySQL, with Thymeleaf + Bootstrap views.
- **Security:** role-based access (Spring Security), session + JWT login, BCrypt password hashing;
  provider accounts are gated behind admin approval.
- **Dual persistence:** most modules use Spring Data JPA; the auth, review, blog and health-profile
  modules use hand-written JDBC (course "no-ORM" requirement) — both against the same `users` table.

See `README.md` and `docs/ARCHITECTURE.md` for the full breakdown.
