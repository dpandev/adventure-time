package com.dpandev.domain.service;

import com.dpandev.domain.utils.GameContext;

/** Service interface for exploration-related actions in the game. */
public interface ExplorationService {

  /**
   * Look around in the current location.
   *
   * @param ctx the game context
   * @return the result of the look command
   */
  CommandResult look(GameContext ctx);

  /**
   * Move the player in the specified direction.
   *
   * @param ctx the game context
   * @param direction the direction to move (e.g., "north", "south", "east", "west")
   * @return the result of the move command
   */
  CommandResult move(GameContext ctx, String direction);
}
