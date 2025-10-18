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
   * @param itemName the Name of the item to pick up
   * @return the result of the pickup command
   */
  CommandResult pickup(GameContext ctx, String itemName);

  /**
   * Drop an item from the player's inventory.
   *
   * @param ctx the game context
   * @param itemId the ID of the item to drop
   * @return the result of the drop command
   */
  CommandResult drop(GameContext ctx, String itemId);

  /**
   * Use an item from the player's inventory.
   *
   * @param ctx the game context
   * @param itemName the ID of the item to use
   * @return the result of the use command
   */
  CommandResult use(GameContext ctx, String itemName);
}
