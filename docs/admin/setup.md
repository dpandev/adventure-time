# Server Setup (Dev)

- JDK 21, Gradle wrapper
- Postgres 15+ (or Testcontainers)
- Run: `./gradlew :server:bootRun` (dev profile)
- Env vars: `DB_URL`, `DB_USER`, `DB_PASS`
- Migrations: Flyway runs on startup
