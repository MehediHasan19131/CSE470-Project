# Sprint 3 — Medicine Reminder & Medicine History
**Member:** Rony Miah (24141084)

## Features
- Create / Edit / Deactivate / Delete Medicine Reminders
- List active reminders for the logged-in patient (Reminder Dashboard)
- Mark a scheduled dose as TAKEN / MISSED / SKIPPED (dose logging)
- Medicine History: all logged doses joined with reminder info, most recent first
- Validation: `endDate` must be after `startDate`; `frequencyPerDay` between 1 and 10

## Routes
| Method | Path | Description |
|--------|------|-------------|
| GET | `/reminders` | Reminder dashboard (active reminders) |
| GET | `/reminders/new` | Create form |
| POST | `/reminders/new` | Create reminder |
| GET | `/reminders/{id}/edit` | Edit form |
| POST | `/reminders/{id}/edit` | Update reminder |
| POST | `/reminders/{id}/deactivate` | Soft-delete / pause reminder |
| POST | `/reminders/{id}/delete` | Hard-delete reminder (+ its logs) |
| POST | `/reminders/{id}/log` | Log a dose (status=TAKEN/MISSED/SKIPPED, scheduledTime) |
| GET | `/reminders/history` | Medicine history table |

## Files
| File | Purpose |
|------|---------|
| `model/MedicineReminder.java` | Reminder entity (reminder times stored as comma-separated string) |
| `model/MedicineLog.java` | History log entity |
| `model/MedicineLogStatus.java` | Enum: TAKEN, MISSED, SKIPPED |
| `repository/MedicineReminderRepository.java` | DAO with hand-written JPQL queries |
| `repository/MedicineLogRepository.java` | DAO with hand-written JPQL queries |
| `service/sprint3/ReminderService.java` | CRUD, dose logging, validation |
| `controller/sprint3/ReminderController.java` | All reminder routes |
| `templates/sprint3/reminders/dashboard.html` | Reminder card dashboard |
| `templates/sprint3/reminders/form.html` | Create/Edit form |
| `templates/sprint3/reminders/history.html` | History table |
| `static/css/sprint3.css` | Card + badge styles |

## Design note
Reminder times are stored as a comma-separated list in a single column
(`reminder_times`), e.g. `08:00,14:00,20:00`. This keeps the module simple —
the form uses one text input with helper text and parsing is trivial. A child
`MedicineReminderTime` entity would be cleaner for production scheduling but
adds complexity not needed for this sprint.

## Dependencies (shared with main project)
- `model/User.java`, `model/UserRole.java`
- `repository/UserRepository.java`
- `service/CurrentUserService.java`
- `templates/fragments/dashboard-layout.html`
- `static/css/styles.css`
