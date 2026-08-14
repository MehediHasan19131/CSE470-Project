# API Endpoints — Member 2: Review & Rating System

Only endpoints belonging to this sprint task are listed here. (Auth endpoints —
login, register, JWT, profile — are Member 1's; see their docs in the group repo.)

All endpoints below require being logged in (session cookie or `Authorization: Bearer`
JWT — either works, same as the rest of the app) — enforced by the existing
`anyRequest().authenticated()` rule in `SecurityConfig`, unchanged this sprint.

## Create a review

```http
POST /api/reviews
```
```json
{ "targetId": 3, "rating": 5, "comment": "Excellent, thorough, and easy to talk to." }
```
`targetId` must belong to a provider (doctor, hospital, pharmacy, diagnostic
centre, or ambulance service) — not a patient, admin, or yourself. `rating`
must be 1–5. `comment` is optional, max 1000 characters.

Returns `201` with the created review, `400` if the target/rating is invalid
or you've already reviewed this target (update it instead — see below), or
`404` if the target doesn't exist.

## Update a review

```http
PUT /api/reviews/{id}
```
```json
{ "rating": 4, "comment": "Updated my thoughts after a follow-up visit." }
```
`{id}` is the review's own id (not the target's). Only the original reviewer
can update it. Returns `200` with the updated review, `403` if it's not yours,
`404` if it doesn't exist, or `400` if the rating is invalid.

## Read a provider's reviews and rating

```http
GET /api/reviews/target/{targetId}
GET /api/ratings/{targetId}
```

**GET /api/reviews/target/{targetId}** — every review for that provider, most
recent first:
```json
[
  {
    "id": 1,
    "reviewerId": 2,
    "reviewerName": "Nadia Rahman",
    "targetId": 3,
    "rating": 5,
    "comment": "Excellent, thorough, and easy to talk to.",
    "createdAt": "2026-07-19T10:15:00",
    "updatedAt": "2026-07-19T10:15:00"
  }
]
```

**GET /api/ratings/{targetId}** — the aggregate:
```json
{ "targetId": 3, "averageRating": 4.8, "totalReviews": 12 }
```
A provider with no reviews yet returns `averageRating: 0.0, totalReviews: 0`
rather than a `404`.

## Server-rendered pages

| Method | Path              | Access          | Purpose                                                    |
|--------|-------------------|-----------------|--------------------------------------------------------------|
| GET    | /reviews          | any logged-in user | Browse providers with their current rating (testing convenience, see README) |
| GET    | /reviews/{targetId} | any logged-in user | Rating Display + Review Form for one provider             |
| POST   | /reviews/{targetId} | any logged-in user | Submits the form — creates or updates your review automatically |
