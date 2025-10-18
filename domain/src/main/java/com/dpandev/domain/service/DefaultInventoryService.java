package com.dpandev.domain.service;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import java.util.List;
import java.util.Optional;

public final class DefaultInventoryService implements InventoryService {

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
    var player = ctx.player();
    var world = ctx.world();
    // validate itemId
    if (itemId == null || itemId.isBlank()) {
      return CommandResult.fail("You must specify an item to pick up.");
    }
    // get item name from world using itemId and then build a string for success message, check that
    // item not null
    Optional<Item> itemName = ctx.world().findItem(itemId);
    if (itemName.isEmpty()) {
      return CommandResult.fail(
          "Item with ID " + itemId + " does not exist."); // this should not happen
    }

    // add to player inv then remove from Room
    player.getInventoryItemIds().add(itemId);

    // validate current room
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown."); // this should not happen
    }
    var currentRoom = currentRoomOpt.get();
    currentRoom.getItemIds().remove(itemId);

    return CommandResult.success(
        itemName
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

    if (!player.getInventoryItemIds().remove(itemId)) {
      return CommandResult.fail("You don't have a " + itemId + ".");
    }
    roomOpt.get().getItemIds().add(itemId);
    return CommandResult.success(
        itemId
            + "has been dropped successfully from the player inventory and placed in "
            + roomOpt.get().getName()
            + ".");
  }
}
