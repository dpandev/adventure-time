package com.dpandev.domain.model;

import java.util.List;
import java.util.Map;

/** Represents a room in the game. */
public final class Room {
  private final String id;
  private final String name;
  private final String description;
  private final Map<String, String> exits;
  private final List<String> itemIds;
  private String puzzleId;

  /**
   * Constructor for Room.
   *
   * @param id The unique identifier for the room.
   * @param name The name of the room.
   * @param description A description of the room.
   * @param exits A map of exits from the room, where the key is the direction and the value is the
   *     ID of the connected room.
   * @param itemIds A list of item IDs present in the room.
   */
  public Room(
      String id, String name, String description, Map<String, String> exits, List<String> itemIds) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.exits = exits;
    this.itemIds = itemIds;
  }

  public Room(
      String id,
      String name,
      String description,
      Map<String, String> exits,
      List<String> itemIds,
      String puzzleId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.exits = exits;
    this.itemIds = itemIds;
    this.puzzleId = puzzleId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Map<String, String> getExits() {
    return exits;
  }

  public String getPuzzleId() {
    return puzzleId;
  }

  public List<String> getItemIds() {
    return itemIds;
  }
}
