# Setup & Deployment Guide

## Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| JDK | 17+ | `java -version` |
| Maven | 3.6+ | `mvn -version` |
| Node.js | 18+ | `node -v` |
| Docker | any | `docker -v` |

---

## Local Development

### 1. Start Infrastructure (MySQL + Redis)

```bash
docker-compose up -d
```

- MySQL: `localhost:3306` (user: `musicshare`, password: `musicshare123`, db: `musicshare`)
- Redis: `localhost:6379`
- phpMyAdmin: http://localhost:8080 (root / root123456)

### 2. Start Backend

```bash
cd backend
mvn spring-boot:run
```

- API: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html

### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

- App: http://localhost:5173

---

## Create Admin User

After starting the app, register a normal user then promote via SQL:

```sql
docker exec -it musicshare-mysql mysql -u musicshare -pmusicshare123 musicshare

-- Replace 1 with your user's ID
INSERT INTO user_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE name = 'ROLE_ADMIN';
```

---

## Running Tests

### Backend (JUnit + Mockito)

```bash
cd backend
mvn test
# 158 tests across 11 test classes
```

### Frontend (Vitest)

```bash
cd frontend
npm test              # run once
npm run test:watch    # watch mode
npm run test:coverage # with coverage report
```

### E2E (Playwright) — requires running app

```bash
cd frontend
npx playwright install chromium   # first time only
npm run test:e2e
npm run test:e2e:ui               # interactive UI mode
```

---

## Production Build

### Backend

```bash
cd backend
mvn clean package -DskipTests
java -jar target/musicshare-backend-*.jar --spring.profiles.active=prod
```

### Frontend

```bash
cd frontend
npm run build
# Output in frontend/dist/
```

Serve `dist/` with Nginx or any static host. Point API calls to your backend URL by setting `VITE_API_BASE_URL` in `.env.production`.

---

## Environment Variables

### Backend (`application.yml` overrides)

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/musicshare` | DB connection |
| `SPRING_DATASOURCE_USERNAME` | `musicshare` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | `musicshare123` | DB password |
| `SPRING_REDIS_HOST` | `localhost` | Redis host |
| `JWT_SECRET` | (see application.yml) | JWT signing key |
| `FILE_UPLOAD_PATH` | `./uploads` | Music file storage |

### Frontend (`.env.production`)

```env
VITE_API_BASE_URL=https://your-api-domain.com
```

---

## Docker Compose Services

```yaml
# docker-compose.yml includes:
musicshare-mysql   # MySQL 8.0  — port 3306
musicshare-redis   # Redis 7    — port 6379
musicshare-phpmyadmin  # phpMyAdmin — port 8080
```

```bash
docker-compose up -d        # start all
docker-compose down         # stop all
docker-compose logs -f      # tail logs
```

---

## Project Ports Summary

| Service | Port | URL |
|---------|------|-----|
| Frontend (dev) | 5173 | http://localhost:5173 |
| Backend API | 8081 | http://localhost:8081 |
| MySQL | 3306 | — |
| Redis | 6379 | — |
| phpMyAdmin | 8080 | http://localhost:8080 |
