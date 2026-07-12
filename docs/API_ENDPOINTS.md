# API Endpoints

## Authentication

```http
POST /api/auth/login
GET /api/me
```

### New (Member 1: Auth & User Management)

```http
POST /api/auth/register
POST /api/auth/token
```

**POST /api/auth/register** - create an account. Body:
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

**POST /api/auth/token** - stateless JWT login for API/mobile clients (the existing
`POST /api/auth/login` above still works exactly as before and creates a browser session
instead - use whichever fits your client). Body:
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

### Server-rendered pages (new)

| Method | Path       | Access          | Purpose                                  |
|--------|-----------|-----------------|-------------------------------------------|
| GET    | /register  | public          | Registration form                         |
| POST   | /register  | public          | Creates the account, redirects to /       |
| GET/POST | /profile | any logged-in user | View / edit your own account details |

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

## Admin

```http
GET /api/admin/users
PATCH /api/admin/users/{userId}/role
GET /api/admin/settings
PUT /api/admin/settings
```

Admin APIs require an admin login session.
