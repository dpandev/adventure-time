package com.dpandev.domain.service;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import java.util.List;
import java.util.Optional;

public final class DefaultInventoryService implements InventoryService {

  private Optional<Item> findItemInCurrentRoom(GameContext ctx, String itemId) {
    var player = ctx.player();
    var world = ctx.world();

    Optional<Room> roomOpt = world.findRoom(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return Optional.empty();
    }

    var room = roomOpt.get();
    if (room.hasItem(itemId)) {
      return world.findItem(itemId);
    }

    return Optional.empty();
  }

  private Optional<Item> findItemInPlayerInventory(GameContext ctx, String itemId) {
    var player = ctx.player();
    var world = ctx.world();

    return player.getInventoryItemIds().stream()
        .filter(id -> id.equals(itemId))
        .findFirst()
        .flatMap(world::findItem);
  }

  @Override
  public CommandResult inventory(GameContext ctx) {
    var player = ctx.player();
    List<String> inv = player.getInventoryItemIds();

    if (inv.isEmpty()) {
      return CommandResult.success("You didn't pickup any items yet.");
    }

    StringBuilder sb = new StringBuilder();
    sb.append("You are carrying:\n");
    for (String itemId : inv) {
      Optional<Item> itemOpt = ctx.world().findItem(itemId);
      if (itemOpt.isPresent()) {
        Item item = itemOpt.get();
        sb.append("- ")
            .append(item.getName())
            .append(": ")
            .append(item.getDescription())
            .append("\n");
      } else {
        sb.append("- Unknown item with ID: ").append(itemId).append("\n");
      }
    }
    return CommandResult.success(sb.toString());
  }

  @Override
  public CommandResult pickup(GameContext ctx, String itemId) {
    if (itemId == null || itemId.isBlank()) {
      return CommandResult.fail("Pickup what?");
    }
    var player = ctx.player();
    var world = ctx.world();

    // validate current room before removing item from it.
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown."); // this should not happen
    }

    Optional<Item> itemOpt = findItemInCurrentRoom(ctx, itemId);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("There is no " + itemId + " here to pick up.");
    }

    // add to player inv then remove from Room
    Item item = itemOpt.get();
    player.addItemToInventory(item.getId());
    currentRoomOpt.get().removeItemFromRoom(item.getId());

    return CommandResult.success(
        itemId
            + " has been picked up from the room and successfully added to the player inventory.");
  }

  @Override
  public CommandResult drop(GameContext ctx, String itemId) {
    if (itemId == null || itemId.isBlank()) {
      return CommandResult.fail("Drop what?");
    }
    var world = ctx.world();
    var player = ctx.player();

    Optional<Room> roomOpt = world.findRoom(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown."); // this should not happen
    }

    // Check if item is in player's inventory
    Optional<Item> itemOpt = findItemInPlayerInventory(ctx, itemId);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("You don't have a " + itemId + " to drop.");
    }

    Item item = itemOpt.get();
    player.removeItemFromInventory(itemId);
    roomOpt.get().addItemToRoom(itemId);

    return CommandResult.success(
        itemId
            + " has been dropped successfully from the player inventory and placed in "
            + roomOpt.get().getName()
            + ".");
  }

  @Override
  public CommandResult use(GameContext ctx, String itemId) {
    if (itemId == null || itemId.isBlank()) {
      return CommandResult.fail("Use what?");
    }

    // First, try to find in player inventory
    Optional<Item> inventoryItemOpt = findItemInPlayerInventory(ctx, itemId);
    if (inventoryItemOpt.isPresent()) {
      Item item = inventoryItemOpt.get();
      // Handle inventory item usage (potion, key, etc.)
      return CommandResult.success("You use the " + item.getName() + ".");
    }

    // If not in inventory, check if it's a fixture in the current room
    Optional<Item> roomItemOpt = findItemInCurrentRoom(ctx, itemId);
    if (roomItemOpt.isPresent()) {
      Item item = roomItemOpt.get();
      // Handle room fixture usage (lever, switch, etc.)
      return CommandResult.success(
          "You interact with the " + item.getName() + ". (Nothing special happens.)");
    }

    return CommandResult.fail("You don't see a " + itemId + " to use.");
  }

  @Override
  public CommandResult inspect(GameContext ctx, String itemId) {
    if (itemId == null || itemId.isBlank()) {
      return CommandResult.fail("Inspect what?");
    }

    // First, try to find in player inventory
    Optional<Item> inventoryItemOpt = findItemInPlayerInventory(ctx, itemId);
    if (inventoryItemOpt.isPresent()) {
      Item item = inventoryItemOpt.get();
      return CommandResult.success(item.getName() + ": " + item.getDescription());
    }

    // If not in inventory, check if it's in the current room
    Optional<Item> roomItemOpt = findItemInCurrentRoom(ctx, itemId);
    if (roomItemOpt.isPresent()) {
      Item item = roomItemOpt.get();
      return CommandResult.success(item.getName() + ": " + item.getDescription());
    }

    return CommandResult.fail("You don't see a " + itemId + " to inspect.");
  }
}
