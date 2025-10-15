# Architecture Overview

Modules:
- `:domain`: shared models/services
- `:client`: CLI client
- `:server`: Spring Boot (REST + WS)

Data:
- H2 (dev) → Postgres (prod)
- Saves stored as JSON (jsonb in Postgres)

---

## Roadmap

The ideal plan; May not have time to implement all phases; Phase 1 is the bare minimum.

### Phase 1 — Local single-player MVP

**Goal:** Build core domain logic and persistence locally to have the game's mechanics working before adding networking.

```
:domain
  com.dpandev.domain
  model/           // Pure state: Player, Room, Item, Exit, Area, etc.
  world/           // World definitions + loader
  command/         // Command objects, parser, handlers
  service/         // Movement, inventory, inspect/look, save API, etc.
  util/            // ID types, small helpers

:client
  com.dpandev.client
  io/              // Console IO
  runtime/         // Game loop, session, wiring parser → handlers → services
  persistence/     // H2 db implementation of SaveRepository
```

### Phase 2 — Server + Auth + Persistent storage (multi-device)

**Goal:** Create a server-side REST API that supports registration, login, save/load, and fetching world/content packs. Move from embedded DB to Postgres.

### Phase 3 — Realtime presence & room chat (WebSocket)

**Goal:** Make the world shared: multiple players present in same room can see each other's presence and chat. Keep server authoritative (validate messages).

### Phase 4 — Collaborative puzzles & authoritative room state

**Goal:** Support shared puzzles which multiple players can collaborate on; room state is authoritative and persisted.

### Phase 5 — Content management, versioning, & swappable packs

**Goal:** Make world data (rooms, items, puzzles) pluggable so game can be updated content without breaking saves.

### Phase 6 — UX enhancements & player analytics

**Goal:** Improve user interface (maybe add React web client or JavaFX). Implement player analytics to track engagement for future improvements.

---
