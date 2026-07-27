# Sprint 2 — Appointment & Service Booking
**Member:** Rony Miah (24141084)

## Features
- Book Appointment (form + submit)
- Cancel Appointment (patient, doctor, or admin)
- Confirm Appointment (doctor or admin only)
- Appointment Status (pending → confirmed/cancelled)
- Appointment History (filtered by role)

## Routes
| Method | Path | Description |
|--------|------|-------------|
| GET | `/appointments` | Appointment history |
| GET | `/appointments/book` | Booking form |
| POST | `/appointments/book` | Submit booking |
| POST | `/appointments/{id}/cancel` | Cancel appointment |
| POST | `/appointments/{id}/confirm` | Confirm appointment |

## Files
| File | Purpose |
|------|---------|
| `controller/sprint2/AppointmentController.java` | All appointment routes |
| `service/sprint2/AppointmentService.java` | Business logic |
| `templates/sprint2/appointments/book.html` | Booking form UI |
| `templates/sprint2/appointments/history.html` | Appointment list UI |
| `static/css/sprint2.css` | Card and status styles |

## Dependencies (shared with main project)
- `model/Appointment.java`, `model/User.java`, `model/UserRole.java`
- `repository/AppointmentRepository.java`, `repository/UserRepository.java`
- `service/CurrentUserService.java`, `service/ListingService.java`
- `dto/ServiceListingResponse.java`
- `templates/fragments/dashboard-layout.html`
- `static/css/styles.css`
