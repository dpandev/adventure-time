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

### Remove dependencies and artifacts
```./gradlew clean build```

---

## Formatting and Checkstyle
Check style and formatting: ```./gradlew check```

Only Checkstyle: ```./gradlew checkstyleMain checkstyleTest```

Auto-format all Java files: ```./gradlew spotlessApply```

