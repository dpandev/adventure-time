package com.dpandev.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for the Puzzle class. */
class PuzzleTest {

  private Map<String, Object> solution;

  @BeforeEach
  void setUp() {
    solution = new HashMap<>();
    solution.put("answer", "test answer");
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("should create puzzle with all fields")
    void shouldCreatePuzzleWithAllFields() {
      Puzzle puzzle =
          new Puzzle(
              "puzzle1",
              "Test puzzle",
              PuzzleType.RIDDLE,
              solution,
              Puzzle.PuzzlePhase.LOCKED,
              3,
              "reward_item");

      assertEquals("puzzle1", puzzle.getId());
      assertEquals("Test puzzle", puzzle.getDescription());
      assertEquals(PuzzleType.RIDDLE, puzzle.getPuzzleType());
      assertEquals(Puzzle.PuzzlePhase.LOCKED, puzzle.getPuzzlePhase());
      assertEquals(3, puzzle.getMaxAttempts());
      assertEquals(3, puzzle.getAttemptsLeft());
      assertEquals("reward_item", puzzle.getRewardItemId());
      assertNotNull(puzzle.getSolution());
    }

    @Test
    @DisplayName("should create puzzle without reward item")
    void shouldCreatePuzzleWithoutRewardItem() {
      Puzzle puzzle =
          new Puzzle(
              "puzzle1",
              "Test puzzle",
              PuzzleType.RIDDLE,
              solution,
              Puzzle.PuzzlePhase.LOCKED,
              3,
              null);

      assertNull(puzzle.getRewardItemId());
    }
  }

  @Nested
  @DisplayName("Attempts Management Tests")
  class AttemptsManagementTests {

    private Puzzle puzzle;

    @BeforeEach
    void setUp() {
      puzzle =
          new Puzzle(
              "puzzle1",
              "Test puzzle",
              PuzzleType.RIDDLE,
              solution,
              Puzzle.PuzzlePhase.IN_PROGRESS,
              3,
              null);
    }

    @Test
    @DisplayName("should decrement attempts left")
    void shouldDecrementAttemptsLeft() {
      assertEquals(3, puzzle.getAttemptsLeft());

      int remaining = puzzle.decrementAttemptsLeft();

      assertEquals(2, remaining);
      assertEquals(2, puzzle.getAttemptsLeft());
    }

    @Test
    @DisplayName("should not go below zero attempts")
    void shouldNotGoBelowZeroAttempts() {
      puzzle.decrementAttemptsLeft();
      puzzle.decrementAttemptsLeft();
      puzzle.decrementAttemptsLeft();
      assertEquals(0, puzzle.getAttemptsLeft());

      int remaining = puzzle.decrementAttemptsLeft();

      assertEquals(0, remaining);
      assertEquals(0, puzzle.getAttemptsLeft());
    }

    @Test
    @DisplayName("should reset attempts to max")
    void shouldResetAttemptsToMax() {
      puzzle.decrementAttemptsLeft();
      puzzle.decrementAttemptsLeft();
      assertEquals(1, puzzle.getAttemptsLeft());

      puzzle.resetAttemptsLeft();

      assertEquals(3, puzzle.getAttemptsLeft());
    }
  }

  @Nested
  @DisplayName("Puzzle Phase Tests")
  class PuzzlePhaseTests {

    private Puzzle puzzle;

    @BeforeEach
    void setUp() {
      puzzle =
          new Puzzle(
              "puzzle1",
              "Test puzzle",
              PuzzleType.RIDDLE,
              solution,
              Puzzle.PuzzlePhase.LOCKED,
              3,
              null);
    }

    @Test
    @DisplayName("should change puzzle phase")
    void shouldChangePuzzlePhase() {
      assertEquals(Puzzle.PuzzlePhase.LOCKED, puzzle.getPuzzlePhase());

      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.IN_PROGRESS);

      assertEquals(Puzzle.PuzzlePhase.IN_PROGRESS, puzzle.getPuzzlePhase());
    }

    @Test
    @DisplayName("should identify solved puzzle")
    void shouldIdentifySolvedPuzzle() {
      assertFalse(puzzle.isSolved());

      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.SOLVED);

      assertTrue(puzzle.isSolved());
    }

    @Test
    @DisplayName("should identify locked out puzzle")
    void shouldIdentifyLockedOutPuzzle() {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.LOCKED_OUT);

      assertEquals(Puzzle.PuzzlePhase.LOCKED_OUT, puzzle.getPuzzlePhase());
      assertFalse(puzzle.isSolved());
    }
  }

  @Nested
  @DisplayName("Solution Tests")
  class SolutionTests {

    private Puzzle puzzle;

    @BeforeEach
    void setUp() {
      puzzle =
          new Puzzle(
              "puzzle1",
              "Test puzzle",
              PuzzleType.RIDDLE,
              solution,
              Puzzle.PuzzlePhase.LOCKED,
              3,
              null);
    }

    @Test
    @DisplayName("should get solution")
    void shouldGetSolution() {
      Map<String, Object> puzzleSolution = puzzle.getSolution();

      assertNotNull(puzzleSolution);
      assertEquals("test answer", puzzleSolution.get("answer"));
    }

    @Test
    @DisplayName("should update solution")
    void shouldUpdateSolution() {
      Map<String, Object> newSolution = new HashMap<>();
      newSolution.put("hint", "test hint");

      puzzle.setSolution(newSolution);

      Map<String, Object> puzzleSolution = puzzle.getSolution();
      assertEquals("test answer", puzzleSolution.get("answer")); // old value still there
      assertEquals("test hint", puzzleSolution.get("hint")); // new value added
    }
  }
}
