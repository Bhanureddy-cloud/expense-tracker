# Expense Tracker

Full-stack expense tracker with secure per-user data, monthly filtering, and category analytics.

**Live demo:** https://expense-tracker-eight-blue-76.vercel.app
*(The backend runs on a free tier and sleeps when idle, so the first request can take up to a minute.)*

**Demo account:** `demo@example.com` / `Demo@1234`

## Architecture

Browser (React on Vercel) → REST API (Spring Boot on Render) → PostgreSQL (Render)

## Features
- Signup and login with JWT authentication (BCrypt-hashed passwords)
- Create, read, update, delete expenses
- Each user can only access their own data
- Filter by month and see spending by category in a chart
- Input validation with clear error messages
- Unit tests (JUnit 5 + Mockito) for auth and ownership rules

## Tech Stack
- **Backend:** Java 17, Spring Boot, Spring Security, Spring Data JPA
- **Database:** PostgreSQL
- **Auth:** JWT (jjwt)
- **Frontend:** React (Vite), Recharts
- **Build and deploy:** Maven, Docker, Render, Vercel

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
1. Install JDK 17 and PostgreSQL, then create a database named `expense_tracker`.
2. Set environment variables:
```powershell
   $env:DB_PASSWORD="your_postgres_password"
   $env:JWT_SECRET="a-random-string-of-at-least-32-characters"
```
3. Start the backend: `./mvnw spring-boot:run` (runs at http://localhost:8080)
4. Start the frontend:
```powershell
   cd frontend
   npm install
   npm run dev
```
5. Open http://localhost:5173

## Security Notes
- Passwords are hashed with BCrypt and never stored in plain text.
- Secrets are read from environment variables, not committed to the repo.
- Ownership checks ensure users cannot read or edit other users' expenses.
- CORS is restricted to the frontend origin.

## Possible Improvements
- Refresh tokens or httpOnly cookies instead of `localStorage`
- Service layer, pagination, and Flyway database migrations
- Integration tests and CI with GitHub Actions