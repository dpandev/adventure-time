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

  /**
   * Get a description of the current room (name, description, and exits). This is used when
   * entering a room or starting the game.
   *
   * @param ctx the game context
   * @return the result containing the room description
   */
  CommandResult describeCurrentRoom(GameContext ctx);

  /**
   * Show the player's current stats including health, attack, and defense.
   *
   * @param ctx the game context
   * @return the result containing the player stats
   */
  CommandResult showStats(GameContext ctx);
}
