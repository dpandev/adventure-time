package com.dpandev.domain.utils;

import com.dpandev.domain.model.Player;
import com.dpandev.domain.world.World;
import java.util.Objects;

/** A context object encapsulating the game world and the current player. */
public final class GameContext {
  public final World world;
  public final Player player;
  // Flag indicating if the game is awaiting a puzzle answer from the player
  private boolean awaitingPuzzleAnswer = false;

  /**
   * Constructs a GameContext with the specified world and player.
   *
   * @param world the game world
   * @param player the current player
   * @throws NullPointerException if either world or player is null
   */
  public GameContext(World world, Player player) {
    this.world = Objects.requireNonNull(world, "world must not be null");
    this.player = Objects.requireNonNull(player, "player must not be null");
  }

  /**
   * Returns the game world.
   *
   * @return the game world
   */
  public World world() {
    return world;
  }

  /**
   * Returns the current player.
   *
   * @return the current player
   */
  public Player player() {
    return player;
  }

  /**
   * Check if the game is awaiting a puzzle answer from the player.
   *
   * @return true if awaiting puzzle answer, false otherwise
   */
  public boolean isAwaitingPuzzleAnswer() {
    return awaitingPuzzleAnswer;
  }

  /**
   * Set whether the game is awaiting a puzzle answer.
   *
   * @param awaiting true if awaiting answer, false otherwise
   */
  public void setAwaitingPuzzleAnswer(boolean awaiting) {
    this.awaitingPuzzleAnswer = awaiting;
  }
}
