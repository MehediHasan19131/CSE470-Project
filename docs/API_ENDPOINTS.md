# API Endpoints

## Authentication

```http
POST /api/auth/login
GET /api/me
```

## Dashboard

```http
GET /api/dashboard
```

## Search and Listings

```http
GET /api/doctors/search?speciality=Cardiology&location=Dhaka
GET /api/hospitals
GET /api/pharmacies
```

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

## Admin

```http
GET /api/admin/users
PATCH /api/admin/users/{userId}/role
GET /api/admin/settings
PUT /api/admin/settings
```

Admin APIs require an admin login session.
