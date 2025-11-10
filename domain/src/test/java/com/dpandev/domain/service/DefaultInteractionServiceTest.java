package com.dpandev.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dpandev.domain.model.Player;
import com.dpandev.domain.model.Puzzle;
import com.dpandev.domain.model.PuzzleType;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.world.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for DefaultInteractionService. */
@ExtendWith(MockitoExtension.class)
class DefaultInteractionServiceTest {

  @Mock private World world;
  @Mock private Player player;
  @Mock private GameContext ctx;

  private DefaultInteractionService interactionService;

  @BeforeEach
  void setUp() {
    interactionService = new DefaultInteractionService();
    lenient().when(ctx.world()).thenReturn(world);
    lenient().when(ctx.player()).thenReturn(player);
    lenient().when(player.getPuzzlesSolved()).thenReturn(new ArrayList<>());
  }

  @Nested
  @DisplayName("Solve Puzzle Tests")
  class SolvePuzzleTests {

    private Room room;
    private Puzzle puzzle;
    private Map<String, Object> solution;

    @BeforeEach
    void setUp() {
      solution = new HashMap<>();
      solution.put("answer", "echo");

      puzzle =
          new Puzzle(
              "puzzle1",
              "I speak without a mouth",
              PuzzleType.RIDDLE,
              solution,
              Puzzle.PuzzlePhase.IN_PROGRESS,
              3,
              "healing_potion");

      room =
          Room.builder()
              .id("room1")
              .name("Test Room")
              .description("A test room")
              .puzzleId("puzzle1")
              .build();

      lenient().when(player.getRoomId()).thenReturn("room1");
      lenient().when(world.getRoomById("room1")).thenReturn(Optional.of(room));
      lenient().when(world.findPuzzle("puzzle1")).thenReturn(Optional.of(puzzle));
    }

    @Test
    @DisplayName("should solve puzzle with correct answer")
    void shouldSolvePuzzleWithCorrectAnswer() {
      when(world.findItem("healing_potion")).thenReturn(Optional.empty());

      CommandResult result = interactionService.solve(ctx, "echo");

      assertTrue(result.success());
      assertTrue(puzzle.isSolved());
      assertEquals(Puzzle.PuzzlePhase.SOLVED, puzzle.getPuzzlePhase());
      verify(ctx).setAwaitingPuzzleAnswer(false);
    }

    @Test
    @DisplayName("should solve puzzle with case insensitive answer")
    void shouldSolvePuzzleWithCaseInsensitiveAnswer() {
      when(world.findItem("healing_potion")).thenReturn(Optional.empty());

      CommandResult result = interactionService.solve(ctx, "ECHO");

      assertTrue(result.success());
      assertTrue(puzzle.isSolved());
    }

    @Test
    @DisplayName("should fail with wrong answer")
    void shouldFailWithWrongAnswer() {
      CommandResult result = interactionService.solve(ctx, "wrong");

      assertFalse(result.success());
      assertFalse(puzzle.isSolved());
      assertEquals(2, puzzle.getAttemptsLeft());
    }

    @Test
    @DisplayName("should lock out after max attempts")
    void shouldLockOutAfterMaxAttempts() {
      interactionService.solve(ctx, "wrong");
      interactionService.solve(ctx, "wrong");
      CommandResult result = interactionService.solve(ctx, "wrong");

      assertFalse(result.success());
      assertEquals(Puzzle.PuzzlePhase.LOCKED_OUT, puzzle.getPuzzlePhase());
      assertEquals(0, puzzle.getAttemptsLeft());
      verify(ctx, atLeastOnce()).setAwaitingPuzzleAnswer(false);
    }

    @Test
    @DisplayName("should fail if puzzle already solved")
    void shouldFailIfPuzzleAlreadySolved() {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.SOLVED);

      CommandResult result = interactionService.solve(ctx, "echo");

      assertFalse(result.success());
      assertTrue(result.message().contains("already solved"));
    }

    @Test
    @DisplayName("should fail if puzzle is locked")
    void shouldFailIfPuzzleIsLocked() {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.LOCKED);

      CommandResult result = interactionService.solve(ctx, "echo");

      assertFalse(result.success());
      assertTrue(result.message().contains("hasn't been presented"));
    }

    @Test
    @DisplayName("should fail if no answer provided")
    void shouldFailIfNoAnswerProvided() {
      CommandResult result = interactionService.solve(ctx, "");

      assertFalse(result.success());
      assertTrue(result.message().contains("provide a solution"));
    }

    @Test
    @DisplayName("should fail if room has no puzzle")
    void shouldFailIfRoomHasNoPuzzle() {
      Room emptyRoom =
          Room.builder()
              .id("room2")
              .name("Empty Room")
              .description("No puzzle here")
              .puzzleId(null)
              .build();

      when(player.getRoomId()).thenReturn("room2");
      when(world.getRoomById("room2")).thenReturn(Optional.of(emptyRoom));

      CommandResult result = interactionService.solve(ctx, "answer");

      assertFalse(result.success());
      assertTrue(result.message().contains("no puzzle"));
    }
  }

  @Nested
  @DisplayName("Present Puzzle Tests")
  class PresentPuzzleTests {

    private Puzzle puzzle;

    @BeforeEach
    void setUp() {
      Map<String, Object> solution = new HashMap<>();
      solution.put("answer", "echo");

      puzzle =
          new Puzzle(
              "puzzle1",
              "Test puzzle description",
              PuzzleType.RIDDLE,
              solution,
              Puzzle.PuzzlePhase.LOCKED,
              3,
              null);

      lenient().when(world.findPuzzle("puzzle1")).thenReturn(Optional.of(puzzle));
    }

    @Test
    @DisplayName("should present locked puzzle")
    void shouldPresentLockedPuzzle() {
      CommandResult result = interactionService.presentPuzzle(ctx, "puzzle1");

      assertTrue(result.success());
      assertEquals(Puzzle.PuzzlePhase.IN_PROGRESS, puzzle.getPuzzlePhase());
      assertTrue(result.message().contains("PUZZLE"));
      assertTrue(result.message().contains("Test puzzle description"));
      verify(ctx).setAwaitingPuzzleAnswer(true);
    }

    @Test
    @DisplayName("should reset and present locked out puzzle")
    void shouldResetAndPresentLockedOutPuzzle() {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.LOCKED_OUT);
      puzzle.decrementAttemptsLeft();

      CommandResult result = interactionService.presentPuzzle(ctx, "puzzle1");

      assertTrue(result.success());
      assertEquals(Puzzle.PuzzlePhase.IN_PROGRESS, puzzle.getPuzzlePhase());
      assertEquals(3, puzzle.getAttemptsLeft());
    }

    @Test
    @DisplayName("should not present already solved puzzle")
    void shouldNotPresentAlreadySolvedPuzzle() {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.SOLVED);

      CommandResult result = interactionService.presentPuzzle(ctx, "puzzle1");

      assertNull(result);
    }

    @Test
    @DisplayName("should fail if puzzle not found")
    void shouldFailIfPuzzleNotFound() {
      when(world.findPuzzle("nonexistent")).thenReturn(Optional.empty());

      CommandResult result = interactionService.presentPuzzle(ctx, "nonexistent");

      assertFalse(result.success());
    }
  }

  @Nested
  @DisplayName("Reset Puzzle Tests")
  class ResetPuzzleTests {

    private Room room;
    private Puzzle puzzle;

    @BeforeEach
    void setUp() {
      puzzle =
          new Puzzle(
              "puzzle1",
              "Test puzzle",
              PuzzleType.RIDDLE,
              new HashMap<>(),
              Puzzle.PuzzlePhase.IN_PROGRESS,
              3,
              null);

      room =
          Room.builder()
              .id("room1")
              .name("Test Room")
              .description("A test room")
              .puzzleId("puzzle1")
              .build();

      when(player.getRoomId()).thenReturn("room1");
      lenient().when(world.getRoomById("room1")).thenReturn(Optional.of(room));
      lenient().when(world.findPuzzle("puzzle1")).thenReturn(Optional.of(puzzle));
    }

    @Test
    @DisplayName("should reset puzzle in progress when exiting")
    void shouldResetPuzzleInProgressWhenExiting() {
      puzzle.decrementAttemptsLeft();

      interactionService.resetPuzzleOnExit(ctx);

      assertEquals(Puzzle.PuzzlePhase.LOCKED, puzzle.getPuzzlePhase());
      assertEquals(3, puzzle.getAttemptsLeft());
      verify(ctx).setAwaitingPuzzleAnswer(false);
    }

    @Test
    @DisplayName("should reset locked out puzzle when exiting")
    void shouldResetLockedOutPuzzleWhenExiting() {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.LOCKED_OUT);
      puzzle.decrementAttemptsLeft();
      puzzle.decrementAttemptsLeft();

      interactionService.resetPuzzleOnExit(ctx);

      assertEquals(Puzzle.PuzzlePhase.LOCKED, puzzle.getPuzzlePhase());
      assertEquals(3, puzzle.getAttemptsLeft());
    }

    @Test
    @DisplayName("should not reset solved puzzle when exiting")
    void shouldNotResetSolvedPuzzleWhenExiting() {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.SOLVED);

      interactionService.resetPuzzleOnExit(ctx);

      assertEquals(Puzzle.PuzzlePhase.SOLVED, puzzle.getPuzzlePhase());
    }

    @Test
    @DisplayName("should handle room without puzzle")
    void shouldHandleRoomWithoutPuzzle() {
      Room emptyRoom =
          Room.builder()
              .id("room2")
              .name("Empty Room")
              .description("No puzzle")
              .puzzleId(null)
              .build();

      when(player.getRoomId()).thenReturn("room2");
      when(world.getRoomById("room2")).thenReturn(Optional.of(emptyRoom));

      assertDoesNotThrow(() -> interactionService.resetPuzzleOnExit(ctx));
    }
  }
}
