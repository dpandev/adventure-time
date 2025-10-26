package com.dpandev.domain.model;

import java.util.Map;

/** Represents a puzzle in the game */
public class Puzzle {
  private final String id;
  private final String description;
  private final PuzzleType puzzleType;
  private final int maxAttempts;
  private int attemptsLeft;

  /** Different phases a puzzle can be in */
  public enum PuzzlePhase {
    LOCKED,
    IN_PROGRESS,
    SOLVED,
    LOCKED_OUT,
  }

  private PuzzlePhase puzzlePhase;
  private final Map<String, Object> solution;

  /**
   * Constructor for Puzzle
   *
   * @param id Unique identifier for the puzzle
   * @param description Description of the puzzle
   * @param puzzleType Type of the puzzle (e.g., RIDDLE, CODE, PHYSICAL)
   * @param solution Solution to the puzzle
   * @param puzzlePhase Current phase of the puzzle
   */
  public Puzzle(
      String id,
      String description,
      PuzzleType puzzleType,
      Map<String, Object> solution,
      PuzzlePhase puzzlePhase,
      int maxAttempts) {
    this.id = id;
    this.description = description;
    this.puzzleType = puzzleType;
    this.solution = solution;
    this.puzzlePhase = puzzlePhase;
    this.maxAttempts = maxAttempts;
    this.attemptsLeft = maxAttempts;
  }

  /** Getters and Setters */
  public String getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public boolean isSolved() {
    return this.puzzlePhase == PuzzlePhase.SOLVED;
  }

  public PuzzlePhase getPuzzlePhase() {
    return puzzlePhase;
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public int getAttemptsLeft() {
    return attemptsLeft;
  }

  public int decrementAttemptsLeft() {
    if (attemptsLeft > 0) {
      attemptsLeft--;
    }
    return attemptsLeft;
  }

  public void resetAttemptsLeft() {
    this.attemptsLeft = this.maxAttempts;
  }

  public void setPuzzlePhase(PuzzlePhase puzzlePhase) {
    this.puzzlePhase = puzzlePhase;
  }

  public PuzzleType getPuzzleType() {
    return puzzleType;
  }

  public Map<String, Object> getSolution() {
    return solution;
  }

  public void setSolution(Map<String, Object> solution) {
    this.solution.putAll(solution);
  }
}
