# Adventure Time

A multi-player co-op adventure game that features map exploration, puzzles/quests, and a rich story plot. The game utilizes an MVC architecture - separating the data from the game logic - allowing 'hot-swappable' game content.

**Stack**: Java 21 (Temurin), Gradle 8.x, Spring Boot 3.5.x (server), H2 (dev), Postgres (prod), Flyway, Testcontainers, JUnit 5.

---

## Architecture Overview

```
:domain
  com.dpandev.domain
  model/           // Pure state: Player, Room, Item, Puzzle, Monster, etc.
  world/           // World definitions + loader
  command/         // Command objects, parser, handlers
  service/         // Movement, inventory, inspect/look, save API, etc.
  util/            // ID types, small helpers

:client
    com.dpandev.client
    controller/      // Command controllers
    io/              // Console IO
    runtime/         // Game loop, session
    persistence/     // H2 db implementation of SaveRepository
    view/            // Console view rendering
  resource/
    worldpacks/     // World content packs (JSON)

:server           # builds, but not yet implemented
  com.dpandev.server
  config/         // Spring configuration
  controller/     // REST & WebSocket controllers
  service/        // Save management, auth, user management
  repository/     // Postgres repositories
```

## Gameplay Scenario Screenshots
View [Gameplay Scenario Screenshots](docs/assets/scenario-screenshots/)
View [Example Worldpack Map](docs/assets/example-worldpack-map.txt)
View [Gameplay Demo Video](~~link~~)

---

## UML Diagram
[UML Diagram Link](https://drive.google.com/file/d/1-DTjbgjPbhBIcD-TMBt5lK5gtYeajm4L/view?usp=drive_link) (needs update for latest version)

---

## Modules
- `:domain` — Shared domain models and utilities.
- `:client` — Console client for single-player MVP.
- `:server` — Spring Boot app.

---

## Prereqs
- JDK 21 Temurin
- Gradle 8.14.3

---

## Quick Start

### For Players

Want to play the game? Start here:

**1. Run the game:**
```bash
./gradlew :client:run
```

**2. Learn the basics:**
- [Player Quickstart Guide](docs/players/quickstart.md) - Get started in 5 minutes
- [Commands Reference](docs/players/commands.md) - Complete list of all commands
- [FAQ](docs/players/faq.md) - Common questions and troubleshooting

**3. Type `help` in-game** for a quick command reference.

---

### For Development

**1. Setup your environment:**
- Generate the wrapper: `gradle wrapper --gradle-version 8.14.3`
- Build everything: `./gradlew clean build`

**2. Run the game:**
- Console client: `./gradlew :client:run`
- ~~Server: `./gradlew :server:bootRun`~~ (works, but not yet fully implemented)

**3. Read the docs:**
- [Developer Setup](docs/dev/dev-setup.md) - Development environment setup
- [Architecture](docs/dev/architecture.md) - System design and structure
- [API Documentation](docs/dev/api/) - Code reference
- [Testing Guide](docs/dev/testing.md) - How to test
- [Runbook](docs/dev/runbook.md) - Operations guide

**4. Create custom content:**
- [Content Packs Guide](docs/admin/content-packs.md) - Create worlds, items, monsters, puzzles
- Worldpacks location: [client/src/main/resources/worldpacks/](client/src/main/resources/worldpacks)

---

## Documentation

### Player Documentation
- [Quickstart Guide](docs/players/quickstart.md) - Get started playing in 5 minutes
- [Commands Reference](docs/players/commands.md) - Complete command list with examples
- [FAQ](docs/players/faq.md) - Frequently asked questions and troubleshooting

### Developer Documentation
- [Developer Setup](docs/dev/dev-setup.md) - Environment setup and installation
- ~~[Architecture Overview](docs/dev/architecture.md) - System design and structure~~ (to be updated)
- [API Documentation](docs/dev/api/) - Code reference for modules and classes
- [Testing Guide](docs/dev/testing.md) - Testing strategies and examples
- [Runbook](docs/dev/runbook.md) - Operations and deployment

### Admin Documentation
- [Content Packs Guide](docs/admin/content-packs.md) - Create custom worlds and content
- [Setup Guide](docs/admin/setup.md) - Administrative setup

---

## Additional info

- Create/modify worldpacks (items, puzzles, etc.) → [client/src/main/resources/worldpacks/](client/src/main/resources/worldpacks)
- Player saved games → [/saves/](saves)

---

## AI Disclaimer:

AI was utilized in the development of this application. AI was primarily used as a tool for determining the initial repo structure/setup plan (given the desired stack to use - java 21, gradle, spring, etc.), debugging, optimization, and aiding with documentation. The purpose of using AI in this project is to aid learning and development efforts - reducing the amount of time normally spent googling and searching/filtering for information on Google, StackOverflow, etc. A more detailed list of prompts used for this project (non-conclusive) can be provided upon request.

The following is a list of the tasks completed by (or completed with the help of) AI tools:
- Determining the optimal project repo structure for maximum versions compatability between frameworks, tools, and other requirements — as well as following conventional Java development standards. Requirements included: Java 21 (Temurin LTS), Gradle, Spring Boot, H2 (dev), Postgres (prod), and JUnit 5.
- Configuring Gradle project structure and debugging build errors related to setup.
- Generated a "less strict" checkstyle (in comparison to the [Google Java Style](https://checkstyle.sourceforge.io/google_style.html)). Found in config/checkstyle/...
- Generating documentation (docs are manually reviewed for accuracy)
- Auto-completing (tab) code snippets, methods, and javadoc comments (manually reviewed for accuracy; formatted automatically with spotless)
- Debugging code errors and exceptions
- Asking for "correct" or "best practice" alternatives to code snippets or approaches (class immutability, encapsulation, handling exceptions, etc.)
-

---
