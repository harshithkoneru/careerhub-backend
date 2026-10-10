# CareerHub Backend

Java 17 / Spring Boot 4.0.3 REST API for the CareerHub Vite frontend. API responses use `{ "success": true, "message": "...", "data": ... }`; DTO property names follow the UI fields (`fullName`, `company`, `role`, `applicationDate`, `resumeVersion`, `interviewLink`, etc.). For the shortest startup instructions, see the repository [README](../README.md).

## Prerequisites and run

- JDK 17 and Maven 3.9+
- MySQL 8.0+, create the database with `mysql -u root -p < db\setup.sql` from this directory
- Configure secrets/settings below in the process environment, then run `mvn spring-boot:run` from `backend`.
- Backend listens on `http://localhost:8081` by default; Swagger UI is `/swagger-ui.html`; health check is `/actuator/health`. Port 8080 is occupied by the Oracle TNS Listener on this computer. Change `SERVER_PORT` if needed.
- Ensure `JAVA_HOME` points to JDK 17 before invoking Maven (the build targets Java 17).

PowerShell setup (choose your own values; never put secrets in source control):

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/careerhub?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME='careerhub_app'
$env:DB_PASSWORD='<your-local-mysql-password>'
$env:APP_JWT_SECRET='<random-secret-at-least-32-characters>'
mvn spring-boot:run
```

Windows Command Prompt setup (replace the example password values with your own; the MySQL password must match the account created in `..\README.md`):

```cmd
cd /d C:\CareerHub\backend
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "DB_URL=jdbc:mysql://localhost:3306/careerhub?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
set "DB_USERNAME=careerhub_app"
set "DB_PASSWORD=your-own-mysql-password"
set "APP_JWT_SECRET=replace-with-a-random-secret-at-least-32-characters-long"
mvn spring-boot:run
```

The environment variables above apply only to the current Command Prompt window. The database setup script creates the database, not a MySQL login; create `careerhub_app` and grant it access as shown in `..\README.md`. Never commit real credentials or secrets.

Settings: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DDL_AUTO` (default `update`; production: `validate`), `SERVER_PORT` (default `8081`), `APP_JWT_SECRET` (required, 32+ characters), `JWT_EXPIRATION_MS`, `UPLOAD_DIRECTORY`, `UPLOAD_MAX_BYTES`, `UPLOAD_MAX_SIZE`. CORS allows `http://localhost:5173`.

Optional dev accounts are seeded only when email **and** password environment variables are explicitly provided. Passwords must be at least 12 characters:
`APP_SEED_ADMIN_EMAIL`, `APP_SEED_ADMIN_PASSWORD`, optionally `APP_SEED_ADMIN_NAME`; `APP_SEED_USER_EMAIL`, `APP_SEED_USER_PASSWORD`, optionally `APP_SEED_USER_NAME`. The seeder never creates fixed default credentials. Keep uploads outside the web root and back them up separately from MySQL.

## API quick reference

All routes except register/login, Swagger/OpenAPI, and health require `Authorization: Bearer <token>`. Register and login return `{token, tokenType, user:{id,fullName,email,role,...}}`. Roles are `USER` and `ADMIN`; `/api/admin/**` requires ADMIN.

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Register / obtain stateless JWT |
| GET, PUT | `/api/profile`, `/api/users/profile` | Get/update own profile |
| GET, POST | `/api/applications` | Paginated search/list (`status`, `search` or `q`, `jobRole`, `page`, `size`, `sort`, `direction`) / create |
| GET, PUT, DELETE | `/api/applications/{id}` | Read/update/delete own application |
| GET, POST | `/api/interviews` | List (`filter=upcoming|completed`) / create |
| GET | `/api/interviews/upcoming`, `/api/interviews/completed` | Upcoming/completed interviews |
| GET, PUT, DELETE | `/api/interviews/{id}` | Read/update/delete own interview |
| GET, POST | `/api/resumes`, `/api/resume` | List / multipart upload (`file`, PDF only, 5 MiB default) |
| PUT, GET, DELETE | `/api/resumes/{id}/default`, `/api/resumes/{id}/download`, `/api/resumes/{id}` | Set default / download / delete |
| GET | `/api/dashboard`, `/api/dashboard/statistics` | Summary stats, charts, recent applications, upcoming interviews |
| GET | `/api/career/analysis`, `/role-performance`, `/rejection-analysis`, `/recommendations`, `/health`, `/health-score` | Rules-based career insights |
| GET | `/api/admin/stats`, `/api/admin/statistics`, `/users`, `/users/trends`, `/popular-roles`, `/applications/status` | Admin-only totals and datasets |
| DELETE | `/api/admin/users/{id}` | Admin-only user deletion (ADMIN accounts protected) |

Application statuses are `Applied`, `Shortlisted`, `Interview`, `Selected`, `Rejected` in view/request DTOs (case-insensitive accepted). Valid progression: Applied → Shortlisted/Rejected → Interview/Rejected → Selected/Rejected; Selected and Rejected are terminal. Rejection reasons include `Unknown`, `Skills Mismatch`, `Resume Screening`, `Technical Interview`, `Experience`, `Other`. Duplicate company + role pairs per user are rejected. Interview status: `Upcoming`, `Completed`, `Cancelled`; optional `applicationId` must belong to the current user and interview date cannot predate its application.

Errors preserve the response envelope with `success:false`, an actionable `message`, and `data:null`; validation is HTTP 400, duplicate conflicts 409, missing records 404, ownership violations 403, invalid login 401.

## Example requests and test data

Register:

```json
{"fullName":"Alex Candidate","email":"alex@example.test","password":"choose-a-long-password","college":"Northstar University","degree":"BSc Computer Science","graduationYear":2027}
```

Create application:

```json
{"company":"Example Labs","role":"Frontend Developer","jobType":"Full Time","location":"Remote","applicationDate":"2026-10-08","jobUrl":"https://jobs.example.test/123","salary":"Negotiable","requiredSkills":["React","JavaScript"],"relevantSkills":["React","CSS"],"resumeVersion":"Frontend-2026.pdf","status":"Applied","rejectionReason":"Unknown","notes":"Applied from careers page"}
```

Create interview:

```json
{"applicationId":"<owned-application-uuid>","company":"Example Labs","role":"Frontend Developer","date":"2026-10-20","time":"10:30","round":"Technical","mode":"Online","interviewLink":"https://meet.example.test/room","status":"Upcoming","notes":"Review component design"}
```

Import the adjacent `postman/CareerHub.postman_collection.json` to run these endpoints; set its `baseUrl` and `token` variables after login. The collection includes registration/login, application, interview, analytics, resume and admin requests. Never commit real credentials or a real bearer token.

## Build and tests

```powershell
mvn test
mvn package
```

Tests use H2 (no MySQL needed): authentication/password hashing, duplicate prevention, status progression, ownership isolation and empty-state career analysis. MySQL-backed run/deployment still requires the MySQL service and environment settings above.
