package com.dpandev.domain.service;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import java.util.Optional;

public final class DefaultExplorationService implements ExplorationService {

  private final InteractionService interactionService;

  public DefaultExplorationService(InteractionService interactionService) {
    this.interactionService = interactionService;
  }

  @Override
  public CommandResult look(GameContext ctx) {
    var world = ctx.world();
    var player = ctx.player();

    // case where player's current room ID is invalid
    // just get the room object by ID and check if present
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("You seem to be in an unknown location.");
    }

    Room room = currentRoomOpt.get();
    // alt Room room = world.getRoomById(player.getRoomId()).get(); // safe due to isEmpty() check
    // above

    StringBuilder sb = new StringBuilder();
    sb.append("You are in ")
        .append(room.getName())
        .append(".\n")
        .append(room.getDescription())
        .append("\n");

    // list monsters in the room
    if (room.getMonsterId() != null) {
      world
          .findMonster(room.getMonsterId())
          .ifPresent(
              monster -> {
                if (monster.isAlive()) {
                  sb.append("There is a ")
                      .append(monster.getName())
                      .append(" here! (HP: ")
                      .append(monster.getCurrentHealth())
                      .append("/")
                      .append(monster.getMaxHealth())
                      .append(")\n");
                }
              });
    }

    // list items in the room by name
    if (!room.getItemIds().isEmpty()) {
      sb.append("You see: ");
      var itemNames =
          room.getItemIds().stream()
              .map(world::findItem)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(Item::getName)
              .toList();
      sb.append(String.join(", ", itemNames)).append("\n");
    } else {
      sb.append("There are no items here.\n");
    }

    // list exits
    if (!room.getExits().isEmpty()) {
      sb.append("Exits: ").append(String.join(", ", room.getExits().keySet())).append("\n");
    } else {
      // this should not happen at all - all rooms have at least one exit
      sb.append("There are no exits from this room.\n");
    }

    return CommandResult.success(sb.toString());
  }

  @Override
  public CommandResult move(GameContext ctx, String direction) {
    if (direction == null || direction.isBlank() || !isValidDirection(direction)) {
      return CommandResult.fail(
          "You must specify a valid direction to move. Try a direction like 'north' or 'n'.");
    }
    var world = ctx.world();
    var player = ctx.player();

    // validate current room
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown."); // this should not happen
    }

    Room currentRoom = currentRoomOpt.get();

    // Delegate puzzle reset to InteractionService
    interactionService.resetPuzzleOnExit(ctx);

    // validate exit in that direction
    if (!isValidExit(direction, currentRoom)) {
      return CommandResult.fail("You can't go " + direction + " from here.");
    }

    // verify the destination room exists
    String destRoomId = currentRoom.getExits().get(direction.toLowerCase());
    if (!isValidRoomId(destRoomId, ctx)) {
      return CommandResult.fail("There is no room in that direction. Try another direction.");
    }

    // move to new room
    player.setRoomId(destRoomId);

    // Check if destination room has a puzzle and delegate to InteractionService
    Optional<Room> destRoomOpt = world.getRoomById(destRoomId);
    if (destRoomOpt.isEmpty()) {
      return CommandResult.fail("Unable to enter the destination room.");
    }

    Room destRoom = destRoomOpt.get();
    String roomDescription = formatRoomEntry(destRoom);

    if (destRoom.getPuzzleId() != null) {
      CommandResult puzzleResult = interactionService.presentPuzzle(ctx, destRoom.getPuzzleId());
      if (puzzleResult != null) {
        return CommandResult.success(roomDescription + "\n\n" + puzzleResult.message());
      }
    }

    return CommandResult.success(roomDescription);
  }

  @Override
  public CommandResult describeCurrentRoom(GameContext ctx) {
    var world = ctx.world();
    var player = ctx.player();

    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("You seem to be in an unknown location.");
    }

    Room room = currentRoomOpt.get();
    String roomDescription = formatRoomEntry(room);
    return CommandResult.success(roomDescription);
  }

  /**
   * Format the room entry message showing name, description, and exits.
   *
   * @param room the room to format
   * @return formatted room description
   */
  private String formatRoomEntry(Room room) {
    StringBuilder sb = new StringBuilder();
    sb.append(room.getName()).append("\n");
    sb.append(room.getDescription()).append("\n");

    // show exits
    if (!room.getExits().isEmpty()) {
      sb.append("Exits: ").append(String.join(", ", room.getExits().keySet()));
    }

    return sb.toString();
  }

  @Override
  public CommandResult showStats(GameContext ctx) {
    var player = ctx.player();
    var world = ctx.world();

    StringBuilder sb = new StringBuilder();
    sb.append("=== ").append(player.getName()).append("'s Stats ===\n");
    sb.append("Health: ")
        .append(player.getCurrentHealth())
        .append("/")
        .append(player.getMaxHealth())
        .append("\n");

    // calculate total attack and defense including equipped items
    int totalAttack = player.getBaseAttack();
    int totalDefense = player.getBaseDefense();

    // add bonuses from equipped items
    for (var entry : player.getEquippedItems().entrySet()) {
      String itemId = entry.getValue();
      world
          .findItem(itemId)
          .ifPresent(
              item -> {
                // attack and defense are added via increaseBaseAttack/Defense methods when equipped
              });
    }

    sb.append("Attack: ").append(totalAttack);
    sb.append(" (Base: ").append(player.getBaseAttack()).append(")");
    sb.append("\n");

    sb.append("Defense: ").append(totalDefense);
    sb.append(" (Base: ").append(player.getBaseDefense()).append(")");
    sb.append("\n");

    // Show equipped items
    sb.append("\nEquipped Items:\n");
    var equippedItems = player.getEquippedItems();
    if (equippedItems.isEmpty()) {
      sb.append("  None\n");
    } else {
      for (var entry : equippedItems.entrySet()) {
        var slot = entry.getKey();
        var itemId = entry.getValue();
        world
            .findItem(itemId)
            .ifPresent(
                item -> {
                  sb.append("  ").append(slot).append(": ").append(item.getName());
                  if (item.getAttackBonus() > 0 || item.getDefenseBonus() > 0) {
                    sb.append(" (");
                    if (item.getAttackBonus() > 0) {
                      sb.append("+").append(item.getAttackBonus()).append(" ATK");
                    }
                    if (item.getDefenseBonus() > 0) {
                      if (item.getAttackBonus() > 0) {
                        sb.append(", ");
                      }
                      sb.append("+").append(item.getDefenseBonus()).append(" DEF");
                    }
                    sb.append(")");
                  }
                  sb.append("\n");
                });
      }
    }

    return CommandResult.success(sb.toString());
  }

  /* helpers */
  private boolean isValidDirection(String dir) {
    return switch (dir.toLowerCase()) {
      case "north", "n", "south", "s", "east", "e", "west", "w", "up", "u", "down", "d" -> true;
      default -> false;
    };
  }

  private boolean isValidRoomId(String roomId, GameContext ctx) {
    return ctx.world().getRoomById(roomId).isPresent();
  }

  private boolean isValidExit(String direction, Room room) {
    return room.getExits().containsKey(direction.toLowerCase());
  }
}
