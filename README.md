# Hubstaff Workforce Management

A full-stack workforce management application for employee, attendance, scheduling, and Hubstaff-related operations.

## Project structure

```text
hubstaff-workforce-management/
├── hubstaff-backend/            # Spring Boot REST API
└── hubstaff-angular-frontend/   # Angular web application
```

## Technology

- Backend: Java 21, Spring Boot, Spring Data JPA, MariaDB
- Frontend: Angular

## Prerequisites

- Java 21
- Maven (or the Maven Wrapper, if added later)
- Node.js and npm
- MariaDB

## Run locally

### Backend

1. Create a MariaDB database named `hubstaff`.
2. Configure your local database connection in `hubstaff-backend/src/main/resources/application.properties`.
3. Start the API:

   ```powershell
   cd hubstaff-backend
   mvn spring-boot:run
   ```

   The API starts on `http://localhost:8080`.

   Check that it is reachable at `GET http://localhost:8080/api/status`.

### Frontend

1. Install dependencies:

   ```powershell
   cd hubstaff-angular-frontend
   npm install
   ```

2. Start the development server:

   ```powershell
   npm start
   ```

   Open the local URL displayed by Angular, normally `http://localhost:4200`.

## Git workflow between computers

Before changing code, get the latest version:

```powershell
git pull
```

When your work is ready to save remotely:

```powershell
git add .
git commit -m "Describe your change"
git push
```

Never commit passwords, API keys, `.env` files, build folders, or `node_modules`.
