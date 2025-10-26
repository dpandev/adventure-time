package com.dpandev.domain.service;

import com.dpandev.domain.utils.GameContext;

/** Service interface for interaction-related actions in the game. */
public interface InteractionService {
  /**
   * Solve a puzzle or interact with an object in the game.
   *
   * @param ctx the game context
   * @param target the target object or puzzle to interact with
   * @return the result of the interaction command
   */
  CommandResult solve(GameContext ctx, String target);

  /**
   * Present a puzzle to the player when entering a room.
   *
   * @param ctx the game context
   * @param puzzleId the ID of the puzzle to present
   * @return the result containing the puzzle prompt
   */
  CommandResult presentPuzzle(GameContext ctx, String puzzleId);

  /**
   * Check if the current room has an active puzzle that needs to be reset.
   *
   * @param ctx the game context
   */
  void resetPuzzleOnExit(GameContext ctx);
}
