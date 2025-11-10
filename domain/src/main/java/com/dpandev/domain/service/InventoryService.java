package com.dpandev.domain.service;

import com.dpandev.domain.utils.GameContext;

/** Service interface for inventory-related actions in the game. */
public interface InventoryService {

  /**
   * Display the player's inventory.
   *
   * @param ctx the game context
   * @return the result of the inventory command
   */
  CommandResult inventory(GameContext ctx);

  /**
   * Pick up an item and add it to the player's inventory.
   *
   * @param ctx the game context
   * @param userInput the name or ID of the item to pick up
   * @return the result of the pickup command
   */
  CommandResult pickup(GameContext ctx, String userInput);

  /**
   * Drop an item from the player's inventory.
   *
   * @param ctx the game context
   * @param userInput the name or ID of the item to drop
   * @return the result of the drop command
   */
  CommandResult drop(GameContext ctx, String userInput);

  /**
   * Use an item from the player's inventory.
   *
   * @param ctx the game context
   * @param userInput the name or ID of the item to use
   * @return the result of the use command
   */
  CommandResult use(GameContext ctx, String userInput);

  /**
   * Inspect an item from the player's inventory.
   *
   * @param ctx the game context
   * @param userInput the name or ID of the item to inspect
   * @return the result of the inspect command
   */
  CommandResult inspect(GameContext ctx, String userInput);

  /**
   * Equip an item from the player's inventory.
   *
   * @param ctx the game context
   * @param userInput the name or ID of the item to equip
   * @return the result of the equip command
   */
  CommandResult equip(GameContext ctx, String userInput);

  /**
   * Unequip an item from the player's equipped slots.
   *
   * @param ctx the game context
   * @param userInput the name or ID of the item to unequip
   * @return the result of the unequip command
   */
  CommandResult unequip(GameContext ctx, String userInput);

  /**
   * Heal using a consumable item from the player's inventory.
   *
   * @param ctx the game context
   * @param userInput the name or ID of the item to heal with
   * @return the result of the heal command
   */
  CommandResult heal(GameContext ctx, String userInput);
}
