# Adventure Time

A multi-player co-op adventure game that features map exploration, puzzles/quests, and a rich story plot. The game utilizes an MVC architecture - separating the data from the game logic - allowing 'hot-swappable' game content.

**Stack**: Java 21 (Temurin), Gradle 8.x, Spring Boot 3.5.x (server), H2 (dev), Postgres (prod), Flyway, Testcontainers, JUnit 5.

---

## UML Diagram
[UML Diagram Link](https://drive.google.com/file/d/1-DTjbgjPbhBIcD-TMBt5lK5gtYeajm4L/view?usp=drive_link)

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

- Generate the wrapper: `gradle wrapper --gradle-version 8.14.3`
- Build everything: `./gradlew clean build`
- Run console client: `./gradlew :client:run`
- ~~Run server: `./gradlew :server:bootRun`~~ (works, but not yet implemented)

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
