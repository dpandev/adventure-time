package com.dpandev.domain.service;

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

    // list items in the room
    if (!room.getItemIds().isEmpty()) {
      sb.append("You see: ").append(String.join(", ", room.getItemIds())).append("\n");
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
    if (destRoomOpt.isPresent()) {
      Room destRoom = destRoomOpt.get();
      if (destRoom.getPuzzleId() != null) {
        CommandResult puzzleResult = interactionService.presentPuzzle(ctx, destRoom.getPuzzleId());
        if (puzzleResult != null) {
          return CommandResult.success("You move " + direction + ".\n\n" + puzzleResult.message());
        }
      }
    }

    return CommandResult.success("You move " + direction + ".");
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

  private boolean isCurrentRoomValid(GameContext ctx) {
    return ctx.world().getRoomById(ctx.player().getRoomId()).isPresent();
  }
}
