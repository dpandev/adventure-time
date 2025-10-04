# Dev Setup

- JDK: Temurin 21
- Gradle: use `./gradlew`
- IntelliJ: Gradle JVM = JDK 21
- Common Tasks:
  - Build: `./gradlew clean build`
  - Client: `./gradlew :client:run`
  - Server: `./gradlew :server:bootRun`
  - Tests: `./gradlew test`
  - Lint: `./gradlew spotlessApply checkstyleMain checkstyleTest`

---

### Generate the wrapper
```gradle wrapper --gradle-version 8.14.3```

### Build everything
```./gradlew build```

### Run console client
```./gradlew :client:run```

### Run server
```./gradlew :server:bootRun```

### Run everything in same console
```./gradlew :server:bootRun :client:run --parallel```

### Remove dependencies and artifacts
```./gradlew clean build```

---

## Formatting, Checkstyle, and Documentation
Check style and formatting: ```./gradlew check```

Only Checkstyle: ```./gradlew checkstyleMain checkstyleTest```

Auto-format all Java files: ```./gradlew spotlessApply```

Automatically generate Javadoc into `docs/dev/api/javadoc/<module>`: ```./gradlew javadocAll```

---

## Troubleshooting

- If IntelliJ shows red imports for Spring after a build works on CLI: Gradle → Reload All Projects (it’s just sync/indexing).
- Keep `developmentOnly("org.springframework.boot:spring-boot-devtools")` (no need to declare the configuration — it’s provided by Boot).
- Using Gradle 8: use toolchain (v21) as is; don’t assign to `destinationDir` on Javadoc — use the configured block you already added with `setDestinationDir(...)`.
