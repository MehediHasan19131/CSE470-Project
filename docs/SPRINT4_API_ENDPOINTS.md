# API Endpoints — Health Blog & Community (Sprint 4)

Only endpoints belonging to this sprint task are listed here. (Auth, Review &
Rating, and Health Profile endpoints are documented in the earlier sprint
docs.)

All endpoints below just require being logged in - **any role**. Unlike
Sprint 3's Health Profile (patient-only), this sprint doesn't restrict who
can post or comment - see README for that call. Writes still check
ownership: editing or deleting a post/comment requires being its original
author, never something the caller can override.

## Create a post ("Create Post")

```http
POST /api/blog/posts
```
```json
{ "title": "5 Everyday Habits That Actually Protect Your Heart", "content": "Cardiovascular disease is still..." }
```
`title` is required, max 200 characters. `content` is required, max 8000
characters. Returns `201` with the created post, or `400` if either is blank.

## Read posts

```http
GET /api/blog/posts
GET /api/blog/posts/{id}
```
`GET /api/blog/posts` returns every post, most recent first - the Blog Feed:
```json
[
  {
    "id": 1,
    "authorId": 3,
    "authorName": "Dr. Fabiha Fyroz Ursa",
    "authorRole": "DOCTOR",
    "title": "5 Everyday Habits That Actually Protect Your Heart",
    "content": "Cardiovascular disease is still...",
    "commentCount": 2,
    "createdAt": "2026-07-20T09:15:00",
    "updatedAt": "2026-07-20T09:15:00"
  }
]
```
`GET /api/blog/posts/{id}` returns one post in the same shape, or `404` if it doesn't exist.

## Update / delete a post (extras beyond "Create Post" — see README)

```http
PUT    /api/blog/posts/{id}
DELETE /api/blog/posts/{id}
```
`PUT` takes the same body as create. Both return `403` if you're not the
author, `404` if the post doesn't exist. `DELETE` also removes every comment
on the post and returns `204`.

## Comments ("Comment System")

```http
GET  /api/blog/posts/{postId}/comments
POST /api/blog/posts/{postId}/comments
```
```json
{ "content": "This is really helpful, thank you!" }
```
`content` is required, max 1000 characters. `POST` returns `201` with the
created comment, or `404` if the post doesn't exist. `GET` returns every
comment on that post, oldest first:
```json
[
  {
    "id": 5,
    "postId": 1,
    "authorId": 2,
    "authorName": "Nahian Mahmud",
    "authorRole": "PATIENT",
    "content": "This is really helpful, thank you doctor!",
    "createdAt": "2026-07-20T11:02:00"
  }
]
```

## Delete a comment (extra beyond "Comment System" as literally assigned — see README)

```http
DELETE /api/blog/comments/{id}
```
Returns `204`, `403` if it's not yours, or `404` if it doesn't exist.

## Server-rendered pages

| Method | Path                                          | Purpose                                                              |
|--------|-------------------------------------------------|-----------------------------------------------------------------------|
| GET    | /blog                                           | Blog Feed - every post + the "Create Post" composer                   |
| GET    | /blog?editPost={id}                             | Same page, composer pre-filled to edit that post (only if it's yours) |
| POST   | /blog/posts                                     | Submits the "publish" form                                            |
| POST   | /blog/posts/{id}                                | Submits the "edit" form                                                |
| POST   | /blog/posts/{id}/delete                         | Deletes a post (and its comments)                                     |
| GET    | /blog/posts/{id}                                | Post Details - full post + the Comment System                         |
| POST   | /blog/posts/{id}/comments                       | Submits the "add comment" form                                        |
| POST   | /blog/posts/{postId}/comments/{commentId}/delete | Deletes a comment                                                    |
