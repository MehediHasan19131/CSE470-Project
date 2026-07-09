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

## Admin

```http
GET /api/admin/users
PATCH /api/admin/users/{userId}/role
GET /api/admin/settings
PUT /api/admin/settings
```

Admin APIs require an admin login session.
