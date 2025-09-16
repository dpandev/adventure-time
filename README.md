# Adventure Time

A multi-player co-op adventure game that features map exploration, puzzles/quests, and a rich story plot. The game utilizes an MVC architecture - separating the data from the game logic - allowing 'hot-swappable' game content.

**Stack**: Java 21 (Temurin), Gradle 8.x, Spring Boot 3.5.x (server), H2 (dev), Postgres (prod), Flyway, Testcontainers, JUnit 5.

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

TODO

---

## AI Disclaimer:

ChatGPTv5 was utilized in the development of this application. AI was primarily used as a tool for creating the project structure/implementation plan, debugging, optimization, and aiding with documentation. The purpose of using AI in this project is to aid learning efforts (reducing the amount of time normally spent googling and searching/filtering for information on Google, StackOverflow, etc. AI was **NOT** used for writing more than 20% of the Java code in this project. A complete list of prompts used for this project can be provided upon request.

The following is a list of the tasks completed by (or completed with the help of) AI tools:
- Determining the optimal project repo structure for maximum compatability between frameworks, tools, and other requirements — as well as following conventional Java development standards.
- Configuring Gradle project structure and debugging build errors related to setup.
- Generated a "less strict" checkstyle (in comparison to the [Google Java Style](https://checkstyle.sourceforge.io/google_style.html)).
- Generating documentation (docs are manually reviewed for accuracy)
-

---
