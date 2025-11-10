package com.dpandev.domain.service;

import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Default implementation of MapService that displays a visual map of nearby rooms. */
public class DefaultMapService implements MapService {

  @Override
  public CommandResult showMap(GameContext ctx) {
    var world = ctx.world();
    var player = ctx.player();

    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown.");
    }

    Room currentRoom = currentRoomOpt.get();
    StringBuilder map = new StringBuilder();

    map.append("\n╔════════════════════════════════════════════╗\n");
    map.append("║                  AREA MAP                  ║\n");
    map.append("╠════════════════════════════════════════════╣\n\n");

    // Get adjacent rooms
    Map<String, String> exits = currentRoom.getExits();
    Map<String, Room> adjacentRooms = new HashMap<>();

    for (Map.Entry<String, String> exit : exits.entrySet()) {
      String direction = exit.getKey();
      String roomId = exit.getValue();
      world.getRoomById(roomId).ifPresent(room -> adjacentRooms.put(direction, room));
    }

    // Build the map visualization
    String north = adjacentRooms.containsKey("north") ? adjacentRooms.get("north").getName() : null;
    String south = adjacentRooms.containsKey("south") ? adjacentRooms.get("south").getName() : null;
    String east = adjacentRooms.containsKey("east") ? adjacentRooms.get("east").getName() : null;
    String west = adjacentRooms.containsKey("west") ? adjacentRooms.get("west").getName() : null;
    String up = adjacentRooms.containsKey("up") ? adjacentRooms.get("up").getName() : null;
    String down = adjacentRooms.containsKey("down") ? adjacentRooms.get("down").getName() : null;

    // Display UP if exists
    if (up != null) {
      map.append("                    ↑ UP\n");
      map.append("              ").append(centerText(up, 20)).append("\n\n");
    }

    // Display NORTH if exists
    if (north != null) {
      map.append("                    ↑ NORTH\n");
      map.append("              ").append(centerText(north, 20)).append("\n");
      map.append("                    |\n");
    }

    // Display WEST - CURRENT - EAST
    StringBuilder middleLine = new StringBuilder();

    if (west != null) {
      middleLine.append(String.format("%-15s ← ", truncate(west, 15)));
    } else {
      middleLine.append("                  ");
    }

    middleLine.append(String.format("[ %s ]", truncate(currentRoom.getName(), 15)));

    if (east != null) {
      middleLine.append(String.format(" → %-15s", truncate(east, 15)));
    }

    map.append(middleLine).append("\n");

    // Display SOUTH if exists
    if (south != null) {
      map.append("                    |\n");
      map.append("              ").append(centerText(south, 20)).append("\n");
      map.append("                    ↓ SOUTH\n");
    }

    // Display DOWN if exists
    if (down != null) {
      map.append("\n");
      map.append("              ").append(centerText(down, 20)).append("\n");
      map.append("                    ↓ DOWN\n");
    }

    map.append("\n╚════════════════════════════════════════════╝\n");

    // Add legend
    map.append("\n  [ ] = Current Location\n");
    map.append("  Available exits: ");

    if (exits.isEmpty()) {
      map.append("None");
    } else {
      List<String> exitList = new ArrayList<>(exits.keySet());
      Collections.sort(exitList);
      map.append(String.join(", ", exitList));
    }

    return CommandResult.success(map.toString());
  }

  /**
   * Center text within a given width.
   *
   * @param text the text to center
   * @param width the total width
   * @return centered text
   */
  private String centerText(String text, int width) {
    text = truncate(text, width);
    int padding = (width - text.length()) / 2;
    return " ".repeat(Math.max(0, padding)) + text;
  }

  /**
   * Truncate text to fit within a given width.
   *
   * @param text the text to truncate
   * @param maxWidth the maximum width
   * @return truncated text
   */
  private String truncate(String text, int maxWidth) {
    if (text.length() <= maxWidth) {
      return text;
    }
    return text.substring(0, maxWidth - 3) + "...";
  }
}
