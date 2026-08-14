# API Endpoints

Sprint 1 endpoints across the merged branches. Authentication & user management
(Member 1) is documented first, followed by the Doctor & Patient module.
See `docs/MVC_ARCHITECTURE.md` for the layering.

## Session-based login (browser)

```http
POST /api/auth/login
GET /api/me
```

**POST /api/auth/login** - authenticates and creates a browser session (cookie).
```json
{ "email": "admin@health.test", "password": "password123" }
```

**GET /api/me** - returns the currently logged-in user (requires an active session
or a valid `Authorization: Bearer` header).

## Registration

```http
POST /api/auth/register
```
```json
{
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "password": "secret123",
  "phone": "01711111111",
  "role": "PATIENT"
}
```
Valid `role` values: `PATIENT`, `DOCTOR`, `HOSPITAL`, `PHARMACY`, `DIAGNOSTIC`, `AMBULANCE`
(not `ADMIN` - admin accounts are seeded only). Returns `201` with the created user, or
`400` if the email is already taken.

## JWT login (stateless, for API/mobile clients)

```http
POST /api/auth/token
```
```json
{ "email": "jane@example.com", "password": "secret123" }
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "role": "PATIENT",
  "fullName": "Jane Doe",
  "expiresInMs": 86400000
}
```
Send it on later requests as `Authorization: Bearer <token>`.

## Server-rendered pages

| Method | Path       | Access          | Purpose                                  |
|--------|-----------|-----------------|--------------------------------------------|
| GET    | /          | public          | Login page                                 |
| POST   | /login     | public          | Processes login (Spring Security formLogin)|
| GET    | /register  | public          | Registration form                          |
| POST   | /register  | public          | Creates the account, redirects to /        |
| GET/POST | /profile | any logged-in user | View / edit your own account details   |
| GET    | /logged-out | public         | Shown after logout                         |

## Doctor & Patient Module (Sprint 1 - Imtiaz Zaman Sami, 23101551)

Doctors and patients are stored the same way every other role is: a `User` row
(role = DOCTOR / PATIENT) plus a linked `Profile` row for the role-specific fields
(specialization, qualification, experience, fee for doctors; date of birth, gender,
blood group for patients).

```http
GET    /api/doctors                        # list all doctors
GET    /api/doctors/{id}                   # get one doctor
POST   /api/doctors                        # create a doctor            (ADMIN)
PUT    /api/doctors/{id}                   # update a doctor            (ADMIN)
DELETE /api/doctors/{id}                   # delete a doctor            (ADMIN)
GET    /api/doctors/specialty/{specialty}  # search doctor by specialty

GET    /api/patients                       # list all patients          (ADMIN, DOCTOR)
GET    /api/patients/{id}                  # get one patient            (ADMIN, DOCTOR)
POST   /api/patients                       # create a patient           (ADMIN)
PUT    /api/patients/{id}                  # update a patient           (ADMIN, PATIENT)
DELETE /api/patients/{id}                  # delete a patient           (ADMIN)
```

Frontend pages (Thymeleaf + Bootstrap, same layout as the rest of the app):

```http
GET /doctors            # Doctor List
GET /doctors/{id}       # Doctor Profile
GET /doctors/search     # Search Interface (search doctor by specialty)
```

Example — create a doctor:

```json
POST /api/doctors
Content-Type: application/json

{
  "fullName": "Dr. Test Doctor",
  "email": "test.doctor@health.test",
  "phone": "01700000099",
  "password": "password123",
  "specialization": "Cardiology",
  "licenseNumber": "DOC-2001",
  "qualification": "MBBS",
  "experienceYears": 6,
  "city": "Dhaka",
  "address": "Road 20, Dhaka",
  "bio": "A sample doctor.",
  "consultationFee": 900
}
```

Example — create a patient:

```json
POST /api/patients
Content-Type: application/json

{
  "fullName": "Test Patient",
  "email": "test.patient@health.test",
  "phone": "01800000099",
  "password": "password123",
  "dateOfBirth": "1999-05-10",
  "gender": "Male",
  "bloodGroup": "A+",
  "city": "Dhaka",
  "address": "Road 21, Dhaka",
  "bio": null
}
```
