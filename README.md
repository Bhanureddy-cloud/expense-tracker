# Expense Tracker

A full-stack expense tracking app. Users sign up, log in, and manage their own expenses with monthly filtering and category summaries.

## Features
- Signup and login with JWT authentication (BCrypt-hashed passwords)
- Create, read, update, delete expenses
- Each user can only access their own data
- Filter by month and summarize spending by category
- Input validation with clear error messages

## Tech Stack
- **Backend:** Java 17, Spring Boot, Spring Security, Spring Data JPA
- **Database:** PostgreSQL
- **Auth:** JWT (jjwt)
- **Build:** Maven
- **Frontend:** React (coming soon)

## API Endpoints
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/auth/signup | Create account | No |
| POST | /api/auth/login | Get JWT token | No |
| GET | /api/expenses | List expenses (optional `?month=2026-10`) | Yes |
| POST | /api/expenses | Add expense | Yes |
| PUT | /api/expenses/{id} | Update expense | Yes |
| DELETE | /api/expenses/{id} | Delete expense | Yes |
| GET | /api/expenses/summary?month=2026-10 | Totals by category | Yes |

## Run Locally
1. Install JDK 17+ and PostgreSQL, then create a database named `expense_tracker`.
2. Set environment variables:
```powershell
   $env:DB_PASSWORD="your_postgres_password"
   $env:JWT_SECRET="a-random-string-of-at-least-32-characters"
```
3. Start the app:
```powershell
   ./mvnw spring-boot:run
```
4. The API runs at `http://localhost:8080`.

## Security Notes
- Passwords are hashed with BCrypt and never stored in plain text.
- Secrets are read from environment variables, not committed to the repo.
- Ownership checks ensure users cannot read or edit other users' expenses.

## Roadmap
- [ ] Unit and integration tests
- [ ] React frontend with charts
- [ ] Docker and cloud deployment