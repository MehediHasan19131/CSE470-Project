# API Endpoints — Member 1: Authentication & User Management

Only endpoints belonging to this sprint task are listed here. (Other members'
endpoints - admin, dashboard, search, pharmacy - were removed from this submission;
see `docs/MVC_ARCHITECTURE.md`.)

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
