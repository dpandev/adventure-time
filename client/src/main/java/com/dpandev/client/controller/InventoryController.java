package com.dpandev.client.controller;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.InventoryService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;

/** Controller to handle inventory-related commands. */
public final class InventoryController implements CommandController {
  private final InventoryService inventoryService;

  /** Constructor for InventoryController. */
  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  /** Handles inventory-related commands such as PICKUP, DROP, INVENTORY, and USE. */
  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case PICKUP -> inventoryService.pickup(ctx, cmd.target());
      case DROP -> inventoryService.drop(ctx, cmd.target());
      case INVENTORY -> inventoryService.inventory(ctx);
      case USE -> inventoryService.use(ctx, cmd.target());
      case INSPECT -> inventoryService.inspect(ctx, cmd.target());
      case EQUIP -> inventoryService.equip(ctx, cmd.target());
      case UNEQUIP -> inventoryService.unequip(ctx, cmd.target());
      case HEAL -> inventoryService.heal(ctx, cmd.target());
      default -> CommandResult.fail("Unsupported verb for inventory: " + cmd.verb());
    };
  }
}
