# Architecture Overview

Modules:
- `:domain`: shared models/services
- `:client`: CLI client
- `:server`: Spring Boot (REST + WS)

Data:
- H2 (dev) → Postgres (prod)
- Saves stored as JSON (jsonb in Postgres)
