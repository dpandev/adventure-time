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
    if (itemId == null || itemId.isBlank()) {
      return CommandResult.fail("Pickup what?");
    }
    var player = ctx.player();
    var world = ctx.world();

    // user will provide name, so find item by name
    Optional<Item> itemOpt = world.findItem(itemId);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("There is no " + itemId + " here to pick up.");
    }

    // add to player inv then remove from Room
    player.getInventoryItemIds().add(itemOpt.get().getId());

    // validate current room before removing item from it.
    // TODO will need to move this check logic elsewhere later
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown."); // this should not happen
    }
    // get the current Room object and remove the item from it
    var currentRoom = currentRoomOpt.get();
    currentRoom.getItemIds().remove(itemOpt.get().getId());

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

    Optional<Item> itemOpt = world.findItem(itemId);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("There is no " + itemId + " to drop.");
    }

    if (!player.getInventoryItemIds().remove(itemId)) {
      return CommandResult.fail("You don't have a " + itemId + ".");
    }
    roomOpt.get().getItemIds().add(itemId);
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
    var player = ctx.player();
    var world = ctx.world();
    Optional<Item> itemOpt =
        player.getInventoryItemIds().stream()
            .filter(id -> id.equals(itemId))
            .findFirst()
            .flatMap(world::findItem);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("There is no " + itemId + " to use.");
    }

    if (!player.getInventoryItemIds().contains(itemId)) {
      return CommandResult.fail("You don't have a " + itemId + ".");
    }

    return CommandResult.success("You use the " + itemId + ". (Nothing special happens.)");
  }

  @Override
  public CommandResult inspect(GameContext ctx, String itemId) {
    if (itemId == null || itemId.isBlank()) {
      return CommandResult.fail("Inspect what?");
    }

    var player = ctx.player();
    var world = ctx.world();
    Optional<Item> itemOpt = world.findItem(itemId);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("There is no " + itemId + " to inspect.");
    }

    if (!player.getInventoryItemIds().contains(itemId)) {
      return CommandResult.fail("You don't have a " + itemId + ".");
    }

    return CommandResult.success(itemId + ": " + itemOpt.get().getDescription());
  }
}
