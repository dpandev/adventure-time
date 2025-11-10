# Testing Guide

Comprehensive guide for writing and running tests in the Adventure Time project.

---

## Testing Stack

- **JUnit Jupiter 6.0.1** - Modern testing framework for Java
- **Mockito Core 5.20.0** - Mocking framework for unit tests
- **Mockito JUnit Jupiter 5.20.0** - Mockito integration with JUnit 5 (provides `@ExtendWith(MockitoExtension.class)`)
- **JaCoCo 0.8.12** - Code coverage reporting
- **Testcontainers 1.21.3** - Integration testing with Docker (server module only)

---

## Test Structure

Tests are organized following the standard Maven/Gradle structure:

```
module/
├── src/
│   ├── main/java/           # Production code
│   └── test/java/           # Test code (mirrors main structure)
│       └── com/dpandev/
│           ├── model/       # Model tests
│           ├── service/     # Service tests
│           ├── controller/  # Controller tests
│           └── utils/       # Utility tests
```

---

## Best Practices

### 1. Test Organization

Use **nested test classes** with `@Nested` to group related tests:

```java
class PuzzleTest {

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    // Tests for constructor behavior
  }

  @Nested
  @DisplayName("Attempts Management Tests")
  class AttemptsManagementTests {
    // Tests for attempts logic
  }
}
```

### 2. Use Descriptive Test Names

Use `@DisplayName` for readable test descriptions:

```java
@Test
@DisplayName("should solve puzzle with correct answer")
void shouldSolvePuzzleWithCorrectAnswer() {
  // test implementation
}
```

### 3. Follow AAA Pattern

Structure tests using **Arrange-Act-Assert**:

```java
@Test
void shouldDecrementAttemptsLeft() {
  // Arrange
  Puzzle puzzle = new Puzzle(/*...*/);

  // Act
  int remaining = puzzle.decrementAttemptsLeft();

  // Assert
  assertEquals(2, remaining);
  assertEquals(2, puzzle.getAttemptsLeft());
}
```

### 4. Test Class Types

#### Model/Entity Tests
- Test constructors
- Test getters/setters
- Test business logic methods
- Test edge cases
- No mocking needed (pure unit tests)

**Example:**
```java
@Test
@DisplayName("should create puzzle with all fields")
void shouldCreatePuzzleWithAllFields() {
  Puzzle puzzle = new Puzzle(
    "puzzle1", "desc", PuzzleType.RIDDLE,
    solution, PuzzlePhase.LOCKED, 3, "reward"
  );

  assertEquals("puzzle1", puzzle.getId());
  assertEquals("reward", puzzle.getRewardItemId());
}
```

#### Service Tests
- Use `@ExtendWith(MockitoExtension.class)`
- Mock dependencies with `@Mock`
- Test success paths
- Test error paths
- Test edge cases
- Verify method calls with `verify()`

**Example:**
```java
@ExtendWith(MockitoExtension.class)
class DefaultInteractionServiceTest {

  @Mock private World world;
  @Mock private Player player;
  @Mock private GameContext ctx;

  private DefaultInteractionService service;

  @BeforeEach
  void setUp() {
    service = new DefaultInteractionService();
    when(ctx.world()).thenReturn(world);
    when(ctx.player()).thenReturn(player);
  }

  @Test
  @DisplayName("should solve puzzle with correct answer")
  void shouldSolvePuzzleWithCorrectAnswer() {
    // Arrange
    when(player.getRoomId()).thenReturn("room1");
    when(world.getRoomById("room1")).thenReturn(Optional.of(room));
    when(world.findPuzzle("puzzle1")).thenReturn(Optional.of(puzzle));

    // Act
    CommandResult result = service.solve(ctx, "echo");

    // Assert
    assertTrue(result.success());
    assertTrue(puzzle.isSolved());
    verify(ctx).setAwaitingPuzzleAnswer(false);
  }
}
```

#### Controller Tests
- Mock services
- Test routing logic
- Test command handling
- Minimal business logic

### 5. Coverage Goals

Aim for high coverage on critical paths:

- **Models**: 80%+ coverage
- **Services**: 85%+ coverage (critical business logic)
- **Controllers**: 70%+ coverage (mostly routing)
- **Utils**: 90%+ coverage

---

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests for Specific Module
```bash
./gradlew :domain:test
./gradlew :client:test
./gradlew :server:test
```

### Run Specific Test Class
```bash
./gradlew :domain:test --tests PuzzleTest
./gradlew :domain:test --tests DefaultInteractionServiceTest
```

### Run Specific Test Method
```bash
./gradlew :domain:test --tests "PuzzleTest.shouldDecrementAttemptsLeft"
```

### Run Tests with Coverage Report
```bash
./gradlew test jacocoTestReport
```

Coverage reports are generated at:
- `domain/build/reports/jacoco/test/html/index.html`
- `client/build/reports/jacoco/test/html/index.html`

### View Coverage in Browser
```bash
open domain/build/reports/jacoco/test/html/index.html
```

---

## Writing New Tests

### Step 1: Create Test Class

Match the structure of the class being tested:

**Production Code:** `domain/src/main/java/com/dpandev/domain/model/Puzzle.java`
**Test Code:** `domain/src/test/java/com/dpandev/domain/model/PuzzleTest.java`

### Step 2: Set Up Test Structure

```java
package com.dpandev.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MyClassTest {

  private MyClass myClass;

  @BeforeEach
  void setUp() {
    // Initialize test objects
    myClass = new MyClass();
  }

  @Test
  @DisplayName("should do something")
  void shouldDoSomething() {
    // Arrange
    // Act
    // Assert
  }
}
```

### Step 3: Add Test Cases

For each public method, test:
- Happy path (expected behavior)
- Edge cases (boundary conditions)
- Error cases (invalid inputs)
- State changes (if applicable)

### Step 4: Run and Verify

```bash
./gradlew :domain:test --tests MyClassTest
```

---

## Common Assertions

```java
// Equality
assertEquals(expected, actual);
assertNotEquals(unexpected, actual);

// Nullability
assertNull(value);
assertNotNull(value);

// Boolean
assertTrue(condition);
assertFalse(condition);

// Exceptions
assertThrows(Exception.class, () -> method());
assertDoesNotThrow(() -> method());

// Collections
assertIterableEquals(expectedList, actualList);
```

---

## Common Mockito Patterns

### Stubbing Method Returns
```java
when(mock.method()).thenReturn(value);
when(mock.method(arg)).thenReturn(value);
when(mock.method(anyString())).thenReturn(value);
```

### Lenient Stubbing (for Nested Tests)
When using nested test classes with shared setUp, use lenient stubbing to avoid UnnecessaryStubbingException:
```java
@Nested
class MyNestedTests {
  @BeforeEach
  void setUp() {
    // Use lenient() for mocks that might not be used in all tests
    lenient().when(mock.method()).thenReturn(value);
  }
}
```

### Stubbing with Optional
```java
when(world.findPuzzle("id")).thenReturn(Optional.of(puzzle));
when(world.findPuzzle("missing")).thenReturn(Optional.empty());
```

### Verify Method Calls
```java
verify(mock).method();
verify(mock, times(2)).method();
verify(mock, never()).method();
verify(mock, atLeastOnce()).method();
```

### Argument Captors
```java
ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
verify(mock).method(captor.capture());
assertEquals("expected", captor.getValue());
```

---

## Test Examples

### Example 1: Model Test (PuzzleTest.java)

```java
@Test
@DisplayName("should decrement attempts left")
void shouldDecrementAttemptsLeft() {
  // Arrange
  Puzzle puzzle = new Puzzle(
    "puzzle1", "desc", PuzzleType.RIDDLE,
    solution, PuzzlePhase.IN_PROGRESS, 3, null
  );

  // Act
  int remaining = puzzle.decrementAttemptsLeft();

  // Assert
  assertEquals(2, remaining);
  assertEquals(2, puzzle.getAttemptsLeft());
}
```

### Example 2: Service Test (DefaultInteractionServiceTest.java)

```java
@Test
@DisplayName("should fail with wrong answer")
void shouldFailWithWrongAnswer() {
  // Arrange
  when(player.getRoomId()).thenReturn("room1");
  when(world.getRoomById("room1")).thenReturn(Optional.of(room));
  when(world.findPuzzle("puzzle1")).thenReturn(Optional.of(puzzle));

  // Act
  CommandResult result = interactionService.solve(ctx, "wrong");

  // Assert
  assertFalse(result.success());
  assertFalse(puzzle.isSolved());
  assertEquals(2, puzzle.getAttemptsLeft());
}
```

### Example 3: Multiple Test Cases

```java
@Nested
@DisplayName("Puzzle Phase Tests")
class PuzzlePhaseTests {

  private Puzzle puzzle;

  @BeforeEach
  void setUp() {
    puzzle = new Puzzle(/* ... */);
  }

  @Test
  @DisplayName("should change puzzle phase")
  void shouldChangePuzzlePhase() {
    puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.IN_PROGRESS);
    assertEquals(Puzzle.PuzzlePhase.IN_PROGRESS, puzzle.getPuzzlePhase());
  }

  @Test
  @DisplayName("should identify solved puzzle")
  void shouldIdentifySolvedPuzzle() {
    puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.SOLVED);
    assertTrue(puzzle.isSolved());
  }
}
```

---

## Continuous Integration

Tests run automatically on build:

```bash
./gradlew build  # Runs tests + other checks
```

To skip tests during build (not recommended):
```bash
./gradlew build -x test
```

---

## Troubleshooting

### Tests Not Running
- Check test class ends with `Test` (e.g., `PuzzleTest.java`)
- Check test methods are annotated with `@Test`
- Check test is in `src/test/java` directory

### Mocking Not Working
- Add `@ExtendWith(MockitoExtension.class)` to test class
- Annotate dependencies with `@Mock`
- Use `when().thenReturn()` for stubbing

### Coverage Report Not Generated
- Run `./gradlew test jacocoTestReport`
- Check `build/reports/jacoco/test/html/index.html`

---

## Additional Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

---

## Next Steps

1. Review existing test examples in `domain/src/test/java/`
2. Write tests for untested classes
3. Run coverage report to identify gaps
4. Aim for 80%+ coverage on critical business logic
5. Add tests for new features before implementing them (TDD)

