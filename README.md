# Task Management System

A REST API for team task tracking: projects, tasks, comments and file attachments. Built with Spring Boot, PostgreSQL and JWT authentication.

## Features

- **JWT authentication**: registration, login, stateless Spring Security filter chain and role-based access (`USER`, `MANAGER`, `ADMIN`)
- **Projects and tasks**: CRUD with status (`TODO → IN_PROGRESS → IN_REVIEW → DONE / CANCELLED`), priority, assignee, tags and due dates
- **Filtering and pagination**: tasks can be filtered by status, priority, project, assignee or text search, and support page/size/sort parameters
- **Comments** on tasks
- **File attachments**: multipart upload (up to 10 MB), download and delete
- **Asynchronous processing**: notifications (simulated, written to the log) and project reports run on a dedicated `@Async` thread pool
- **Scheduled job**: a daily `@Scheduled` cron job finds overdue tasks and sends deadline reminders to their assignees
- **OpenAPI / Swagger UI** with bearer-token support
- Centralised error handling (`@RestControllerAdvice`), Bean Validation and MapStruct DTO mapping
- **Docker Compose** setup with PostgreSQL and health checks, plus Spring Boot Actuator health endpoint

## Tech stack

Java 17 · Spring Boot 3.2 (Web, Data JPA, Security, Validation, Actuator) · PostgreSQL 15 · JJWT · MapStruct · Lombok · springdoc-openapi · JUnit 5 · Mockito · Docker

## Getting started

### Run with Docker Compose

```bash
git clone https://github.com/kazuhaa99/task-management-system.git
cd task-management-system
cp .env.example .env        # set DB_PASSWORD and JWT_SECRET
docker compose up --build
```

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health: <http://localhost:8080/actuator/health>

### Run locally

Requirements: JDK 17, Maven 3.9+ and a running PostgreSQL instance.

```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskdb
export DB_USERNAME=postgres
export DB_PASSWORD=<your password>
export JWT_SECRET=<random string, 32+ chars>
mvn spring-boot:run
```

### Configuration

All secrets come from environment variables (see [`.env.example`](.env.example)):

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/taskdb` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | **required** |
| `JWT_SECRET` | HMAC signing key, at least 32 characters | **required** |
| `JWT_EXPIRATION` | Token lifetime in ms | `86400000` (24 h) |

### Tests

```bash
mvn test
```

## API overview

All endpoints except `/api/auth/**` require the header `Authorization: Bearer <token>`.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user and get a token |
| `POST` | `/api/auth/login` | Log in and get a token |
| `GET / POST` | `/api/projects` | List (with `?search=`) / create projects |
| `GET / PUT / DELETE` | `/api/projects/{id}` | Get / update / delete a project |
| `GET / POST` | `/api/tasks` | List with filters (`status`, `priority`, `projectId`, `assigneeId`, `search`, paging) / create |
| `GET / PUT / DELETE` | `/api/tasks/{id}` | Get / update / delete a task |
| `GET` | `/api/tasks/project/{projectId}/report` | Generate a project report (async) |
| `GET / POST` | `/api/tasks/{taskId}/comments` | List / add comments |
| `DELETE` | `/api/tasks/{taskId}/comments/{commentId}` | Delete a comment |
| `GET / POST` | `/api/tasks/{taskId}/files` | List / upload attachments |
| `GET` | `/api/tasks/{taskId}/files/{attachmentId}/download` | Download an attachment |
| `DELETE` | `/api/tasks/{taskId}/files/{attachmentId}` | Delete an attachment |
| `GET` | `/api/users`, `/api/users/{id}`, `/api/users/username/{username}` | User lookup |
| `DELETE` | `/api/users/{id}` | Delete a user (admin only) |

Full request and response schemas are available in Swagger UI.

Example:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123","fullName":"Alice"}'
```

## Project structure

```
src/main/java/com/akenzhan/taskmanagement/
├── config/       # Security, async executor, OpenAPI
├── controller/   # REST controllers
├── dto/          # Request / response DTOs
├── entity/       # JPA entities: User, Project, Task, Comment, Tag, Attachment
├── enums/        # Role, TaskStatus, TaskPriority
├── exception/    # Custom exceptions + global handler
├── mapper/       # MapStruct mappers
├── repository/   # Spring Data JPA repositories
├── security/     # JWT utility, filter, UserDetailsService
└── service/      # Business logic, async notifications and reports, overdue-task job
```

## License

[MIT](LICENSE)
