package com.dpandev.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for the CommandToken record. */
class CommandTokenTest {

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("should create command token with all fields")
    void shouldCreateCommandTokenWithAllFields() {
      CommandToken token =
          new CommandToken(Verb.LOOK, "sword", List.of("steel", "sword"), "look steel sword");

      assertEquals(Verb.LOOK, token.verb());
      assertEquals("sword", token.target());
      assertEquals(List.of("steel", "sword"), token.args());
      assertEquals("look steel sword", token.raw());
    }

    @Test
    @DisplayName("should default null verb to UNKNOWN")
    void shouldDefaultNullVerbToUnknown() {
      CommandToken token = new CommandToken(null, "target", List.of(), "test");

      assertEquals(Verb.UNKNOWN, token.verb());
    }

    @Test
    @DisplayName("should default null target to null")
    void shouldDefaultNullTargetToNull() {
      CommandToken token = new CommandToken(Verb.LOOK, null, List.of(), "look");

      assertNull(token.target());
    }

    @Test
    @DisplayName("should default blank target to null")
    void shouldDefaultBlankTargetToNull() {
      CommandToken token = new CommandToken(Verb.LOOK, "   ", List.of(), "look");

      assertNull(token.target());
    }

    @Test
    @DisplayName("should default null args to empty list")
    void shouldDefaultNullArgsToEmptyList() {
      CommandToken token = new CommandToken(Verb.LOOK, null, null, "look");

      assertNotNull(token.args());
      assertTrue(token.args().isEmpty());
    }

    @Test
    @DisplayName("should default null raw to empty string")
    void shouldDefaultNullRawToEmptyString() {
      CommandToken token = new CommandToken(Verb.LOOK, null, List.of(), null);

      assertEquals("", token.raw());
    }

    @Test
    @DisplayName("should create immutable copy of args list")
    void shouldCreateImmutableCopyOfArgsList() {
      List<String> mutableArgs = new ArrayList<>();
      mutableArgs.add("arg1");
      mutableArgs.add("arg2");

      CommandToken token = new CommandToken(Verb.GO, "north", mutableArgs, "go north");

      // Modify original list
      mutableArgs.add("arg3");

      // Token should still have original args
      assertEquals(2, token.args().size());
      assertEquals(List.of("arg1", "arg2"), token.args());
    }
  }

  @Nested
  @DisplayName("hasTarget Tests")
  class HasTargetTests {

    @Test
    @DisplayName("should return true when target is non-null and non-blank")
    void shouldReturnTrueWhenTargetIsNonNullAndNonBlank() {
      CommandToken token = new CommandToken(Verb.PICKUP, "sword", List.of("sword"), "pickup sword");

      assertTrue(token.hasTarget());
    }

    @Test
    @DisplayName("should return false when target is null")
    void shouldReturnFalseWhenTargetIsNull() {
      CommandToken token = new CommandToken(Verb.LOOK, null, List.of(), "look");

      assertFalse(token.hasTarget());
    }

    @Test
    @DisplayName("should return false when target is blank")
    void shouldReturnFalseWhenTargetIsBlank() {
      CommandToken token = new CommandToken(Verb.LOOK, "   ", List.of(), "look");

      assertFalse(token.hasTarget());
    }

    @Test
    @DisplayName("should return false when target is empty string")
    void shouldReturnFalseWhenTargetIsEmptyString() {
      CommandToken token = new CommandToken(Verb.LOOK, "", List.of(), "look");

      assertFalse(token.hasTarget());
    }
  }

  @Nested
  @DisplayName("Field Access Tests")
  class FieldAccessTests {

    @Test
    @DisplayName("should access verb field")
    void shouldAccessVerbField() {
      CommandToken token = new CommandToken(Verb.ATTACK, "goblin", List.of(), "attack goblin");

      assertEquals(Verb.ATTACK, token.verb());
    }

    @Test
    @DisplayName("should access target field")
    void shouldAccessTargetField() {
      CommandToken token = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

      assertEquals("north", token.target());
    }

    @Test
    @DisplayName("should access args field")
    void shouldAccessArgsField() {
      List<String> args = List.of("steel", "sword");
      CommandToken token = new CommandToken(Verb.PICKUP, "steel sword", args, "pickup steel sword");

      assertEquals(args, token.args());
    }

    @Test
    @DisplayName("should access raw field")
    void shouldAccessRawField() {
      CommandToken token = new CommandToken(Verb.SOLVE, "echo", List.of("echo"), "solve echo");

      assertEquals("solve echo", token.raw());
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("should handle all null inputs")
    void shouldHandleAllNullInputs() {
      CommandToken token = new CommandToken(null, null, null, null);

      assertEquals(Verb.UNKNOWN, token.verb());
      assertNull(token.target());
      assertEquals(List.of(), token.args());
      assertEquals("", token.raw());
      assertFalse(token.hasTarget());
    }

    @Test
    @DisplayName("should handle empty args list")
    void shouldHandleEmptyArgsList() {
      CommandToken token = new CommandToken(Verb.HELP, null, List.of(), "help");

      assertEquals(0, token.args().size());
      assertTrue(token.args().isEmpty());
    }

    @Test
    @DisplayName("should handle multi-word target")
    void shouldHandleMultiWordTarget() {
      CommandToken token =
          new CommandToken(
              Verb.PICKUP, "steel sword", List.of("steel", "sword"), "pickup steel sword");

      assertEquals("steel sword", token.target());
      assertTrue(token.hasTarget());
    }

    @Test
    @DisplayName("should handle special characters in target")
    void shouldHandleSpecialCharactersInTarget() {
      CommandToken token = new CommandToken(Verb.SOLVE, "echo!", List.of("echo!"), "solve echo!");

      assertEquals("echo!", token.target());
      assertTrue(token.hasTarget());
    }

    @Test
    @DisplayName("should preserve whitespace in raw")
    void shouldPreserveWhitespaceInRaw() {
      CommandToken token =
          new CommandToken(Verb.SOLVE, "the moon", List.of(), "  solve   the moon  ");

      assertEquals("  solve   the moon  ", token.raw());
    }
  }

  @Nested
  @DisplayName("Common Usage Pattern Tests")
  class CommonUsagePatternTests {

    @Test
    @DisplayName("should handle simple command with no target")
    void shouldHandleSimpleCommandWithNoTarget() {
      CommandToken token = new CommandToken(Verb.LOOK, null, List.of(), "look");

      assertEquals(Verb.LOOK, token.verb());
      assertNull(token.target());
      assertFalse(token.hasTarget());
      assertTrue(token.args().isEmpty());
    }

    @Test
    @DisplayName("should handle command with single word target")
    void shouldHandleCommandWithSingleWordTarget() {
      CommandToken token = new CommandToken(Verb.PICKUP, "sword", List.of("sword"), "pickup sword");

      assertEquals(Verb.PICKUP, token.verb());
      assertEquals("sword", token.target());
      assertTrue(token.hasTarget());
      assertEquals(List.of("sword"), token.args());
    }

    @Test
    @DisplayName("should handle command with multi-word target")
    void shouldHandleCommandWithMultiWordTarget() {
      CommandToken token =
          new CommandToken(
              Verb.INSPECT, "steel helmet", List.of("steel", "helmet"), "inspect steel helmet");

      assertEquals(Verb.INSPECT, token.verb());
      assertEquals("steel helmet", token.target());
      assertTrue(token.hasTarget());
      assertEquals(List.of("steel", "helmet"), token.args());
    }

    @Test
    @DisplayName("should handle movement command")
    void shouldHandleMovementCommand() {
      CommandToken token = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

      assertEquals(Verb.GO, token.verb());
      assertEquals("north", token.target());
      assertTrue(token.hasTarget());
    }

    @Test
    @DisplayName("should handle system command")
    void shouldHandleSystemCommand() {
      CommandToken token = new CommandToken(Verb.QUIT, null, List.of(), "quit");

      assertEquals(Verb.QUIT, token.verb());
      assertNull(token.target());
      assertFalse(token.hasTarget());
    }

    @Test
    @DisplayName("should handle unknown command")
    void shouldHandleUnknownCommand() {
      CommandToken token = new CommandToken(Verb.UNKNOWN, null, List.of(), "gibberish");

      assertEquals(Verb.UNKNOWN, token.verb());
      assertEquals("gibberish", token.raw());
    }
  }

  @Nested
  @DisplayName("Immutability Tests")
  class ImmutabilityTests {

    @Test
    @DisplayName("should have immutable args list")
    void shouldHaveImmutableArgsList() {
      List<String> originalArgs = List.of("arg1", "arg2");
      CommandToken token = new CommandToken(Verb.SOLVE, "answer", originalArgs, "solve answer");

      List<String> tokenArgs = token.args();
      assertEquals(originalArgs, tokenArgs);
      // Attempting to modify would throw UnsupportedOperationException
      // This is guaranteed by List.copyOf()
    }
  }
}
