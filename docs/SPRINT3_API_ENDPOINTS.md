# API Endpoints — Member 1: Health Profile

Only endpoints belonging to this sprint task are listed here. (Auth endpoints —
login, register, JWT, profile — and the Review & Rating endpoints are documented
in the group repo's earlier sprint docs.)

All endpoints below require being logged in **as a patient** — enforced by
`.requestMatchers("/health-profile/**", "/api/health/**").hasRole("PATIENT")`
in `SecurityConfig` (session cookie or `Authorization: Bearer` JWT both work,
same as the rest of the app). A patient can only ever read or write their own
records — every write is checked against the logged-in user's id, never a
`patientId` supplied by the caller.

## Add a medical history entry ("Add History")

```http
POST /api/health/history
```
```json
{ "condition": "Type 2 Diabetes", "diagnosedOn": "2022-03-14", "notes": "Managed with metformin and diet." }
```
`condition` is required, max 150 characters. `diagnosedOn` is optional (ISO
date). `notes` is optional, max 1000 characters.

Returns `201` with the created entry, or `400` if `condition` is blank.

## Update a medical history entry ("Update History")

```http
PUT /api/health/history/{id}
```
```json
{ "condition": "Type 2 Diabetes", "diagnosedOn": "2022-03-14", "notes": "Updated after latest A1C review." }
```
`{id}` is the entry's own id. Only the owning patient can update it. Returns
`200` with the updated entry, `403` if it's not yours, `404` if it doesn't
exist, or `400` if `condition` is blank.

## Read / delete medical history

```http
GET /api/health/history
DELETE /api/health/history/{id}
```
**GET** returns every history entry for the logged-in patient, most recently
diagnosed first (undated entries last, then most recently added):
```json
[
  {
    "id": 1,
    "patientId": 2,
    "condition": "Type 2 Diabetes",
    "diagnosedOn": "2022-03-14",
    "notes": "Managed with metformin and diet.",
    "createdAt": "2026-08-01T09:12:00",
    "updatedAt": "2026-08-01T09:12:00"
  }
]
```
**DELETE** removes an entry you own. Returns `204`, `403` if it's not yours,
or `404` if it doesn't exist. (Delete isn't one of the two assigned backend
items - see README for why it's included anyway.)

## Allergies — same shape, its own table

```http
GET    /api/health/allergies
POST   /api/health/allergies
PUT    /api/health/allergies/{id}
DELETE /api/health/allergies/{id}
```
```json
{ "allergen": "Penicillin", "severity": "SEVERE", "reaction": "Hives and difficulty breathing." }
```
`allergen` is required, max 150 characters. `severity` is required - one of
`MILD`, `MODERATE`, `SEVERE`. `reaction` is optional, max 500 characters.
Same status codes as the medical history endpoints above.

## Server-rendered pages

| Method | Path                              | Access  | Purpose                                                        |
|--------|------------------------------------|---------|------------------------------------------------------------------|
| GET    | /health-profile                    | patient | The Health Profile Page - Medical History panel + Allergies panel |
| GET    | /health-profile?editHistory={id}   | patient | Same page, with the history form pre-filled for editing that entry |
| GET    | /health-profile?editAllergy={id}   | patient | Same page, with the allergy form pre-filled for editing that entry |
| POST   | /health-profile/history            | patient | Submits the "add" history form                                  |
| POST   | /health-profile/history/{id}       | patient | Submits the "edit" history form                                 |
| POST   | /health-profile/history/{id}/delete | patient | Deletes a history entry                                        |
| POST   | /health-profile/allergies          | patient | Submits the "add" allergy form                                  |
| POST   | /health-profile/allergies/{id}     | patient | Submits the "edit" allergy form                                 |
| POST   | /health-profile/allergies/{id}/delete | patient | Deletes an allergy                                            |
