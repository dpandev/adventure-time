package com.dpandev.domain.service;

import com.dpandev.domain.model.Puzzle;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import java.util.Map;
import java.util.Optional;

public class DefaultInteractionService implements InteractionService {

  @Override
  public CommandResult solve(GameContext ctx, String target) {
    var world = ctx.world();
    var player = ctx.player();

    // Get current room
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("You seem to be in an unknown location."); // should not happen
    }

    Room room = currentRoomOpt.get();

    // Check if room has a puzzle
    if (room.getPuzzleId() == null) {
      return CommandResult.fail("There is no puzzle to solve here.");
    }

    Optional<Puzzle> puzzleOpt = world.findPuzzle(room.getPuzzleId());
    if (puzzleOpt.isEmpty()) {
      return CommandResult.fail("There is no puzzle to solve here.");
    }

    Puzzle puzzle = puzzleOpt.get();

    // Check puzzle state
    if (puzzle.getPuzzlePhase() == Puzzle.PuzzlePhase.SOLVED) {
      ctx.setAwaitingPuzzleAnswer(false);
      return CommandResult.fail("The puzzle is already solved.");
    }

    if (puzzle.getPuzzlePhase() == Puzzle.PuzzlePhase.LOCKED) {
      return CommandResult.fail("The puzzle hasn't been presented yet. Try moving into the room.");
    }

    if (puzzle.getPuzzlePhase() == Puzzle.PuzzlePhase.LOCKED_OUT) {
      return CommandResult.fail(
          "You've failed this puzzle. Leave the room and return to try again.");
    }

    // Check if player provided an answer
    if (target == null || target.isBlank()) {
      return CommandResult.fail("You need to provide a solution to solve the puzzle.");
    }

    // Check the answer against the solution
    Map<String, Object> solution = puzzle.getSolution();
    String correctAnswer =
        solution.get("answer") != null
            ? solution.get("answer").toString().toLowerCase().trim()
            : null;

    if (correctAnswer == null) { // Solution not configured properly
      return CommandResult.fail("This puzzle has no solution configured.");
    }

    String playerAnswer = target.toLowerCase().trim();

    // Check if answer is correct
    if (playerAnswer.equals(correctAnswer)) {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.SOLVED);
      ctx.setAwaitingPuzzleAnswer(false);

      if (!player.getPuzzlesSolved().contains(puzzle.getId())) {
        player.getPuzzlesSolved().add(puzzle.getId());
      }

      StringBuilder successMessage = new StringBuilder();
      successMessage.append("Correct! You have solved the puzzle.\n");

      // Check for reward item
      String rewardItemId = puzzle.getRewardItemId();
      if (rewardItemId != null && !rewardItemId.isBlank()) {
        var itemOpt = world.findItem(rewardItemId);
        if (itemOpt.isPresent()) {
          player.addItemToInventory(rewardItemId);
          successMessage.append("You received: ").append(itemOpt.get().getName()).append("\n");
        }
      }

      successMessage.append("The way forward is now clear.");

      return CommandResult.success(successMessage.toString());
    } else {
      // wrong answer - decrement attempts
      int attemptsLeft = puzzle.decrementAttemptsLeft();

      if (attemptsLeft <= 0) {
        puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.LOCKED_OUT);
        ctx.setAwaitingPuzzleAnswer(false);
        return CommandResult.fail(
            "Wrong answer! You have failed the puzzle.\n"
                + "Leave the room and return to try again.");
      } else {
        // keep awaiting puzzle answer flag set for retry
        return CommandResult.fail(
            "Wrong answer! Try again.\n" + "Attempts remaining: " + attemptsLeft);
      }
    }
  }

  @Override
  public CommandResult presentPuzzle(GameContext ctx, String puzzleId) {
    var world = ctx.world();

    Optional<Puzzle> puzzleOpt = world.findPuzzle(puzzleId);
    if (puzzleOpt.isEmpty()) {
      return CommandResult.fail("There is no puzzle to solve here.");
    }

    Puzzle puzzle = puzzleOpt.get();

    // if puzzle is locked or locked out (failed before), present it
    if (puzzle.getPuzzlePhase() == Puzzle.PuzzlePhase.LOCKED
        || puzzle.getPuzzlePhase() == Puzzle.PuzzlePhase.LOCKED_OUT) {
      puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.IN_PROGRESS);
      puzzle.resetAttemptsLeft();
      ctx.setAwaitingPuzzleAnswer(true);
      return CommandResult.success(
          "=== PUZZLE ===\n"
              + puzzle.getDescription()
              + "\n"
              + "Attempts remaining: "
              + puzzle.getAttemptsLeft()
              + "\n"
              + "Enter your answer:");
    }

    // puzzle already solved
    return null;
  }

  @Override
  public void resetPuzzleOnExit(GameContext ctx) {
    var world = ctx.world();
    var player = ctx.player();

    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return;
    }

    Room currentRoom = currentRoomOpt.get();

    // Check if current room has an unsolved puzzle
    if (currentRoom.getPuzzleId() != null) {
      Optional<Puzzle> puzzleOpt = world.findPuzzle(currentRoom.getPuzzleId());
      if (puzzleOpt.isPresent()) {
        Puzzle puzzle = puzzleOpt.get();
        // If puzzle is in progress and player tries to leave, reset it
        if (puzzle.getPuzzlePhase() == Puzzle.PuzzlePhase.IN_PROGRESS
            || puzzle.getPuzzlePhase() == Puzzle.PuzzlePhase.LOCKED_OUT) {
          puzzle.resetAttemptsLeft();
          puzzle.setPuzzlePhase(Puzzle.PuzzlePhase.LOCKED);
          ctx.setAwaitingPuzzleAnswer(false);
        }
      }
    }
  }
}
