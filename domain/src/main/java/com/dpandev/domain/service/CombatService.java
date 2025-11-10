package com.dpandev.domain.service;

import com.dpandev.domain.utils.GameContext;

/** Service interface for combat-related actions in the game. */
public interface CombatService {

  /**
   * Initiate combat with a monster.
   *
   * @param ctx the game context
   * @param monsterName the name of the monster to attack
   * @return the result of initiating combat
   */
  CommandResult initiateCombat(GameContext ctx, String monsterName);

  /**
   * Player attacks the current monster in combat.
   *
   * @param ctx the game context
   * @return the result of the attack
   */
  CommandResult playerAttack(GameContext ctx);

  /**
   * Player chooses to ignore/flee from a monster encounter.
   *
   * @param ctx the game context
   * @param monsterName the name of the monster to ignore
   * @return the result of ignoring the monster
   */
  CommandResult ignoreMonster(GameContext ctx, String monsterName);

  /**
   * Handle player death and game over.
   *
   * @param ctx the game context
   * @return the result containing game over message
   */
  CommandResult handlePlayerDeath(GameContext ctx);
}
