package com.dpandev.domain.world;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Puzzle;
import com.dpandev.domain.model.Room;
import java.util.Map;
import java.util.Optional;

/** Represents the entire game world, containing rooms, items, and puzzles. */
public final class World {
  private final String version;
  private final Map<String, Room> roomsById;
  private final Map<String, Item> itemsById;
  private final Map<String, Puzzle> puzzlesById;
  private final String startRoomId;

  /**
   * Constructor for World.
   *
   * @param version The version of the world.
   * @param rooms A map of rooms in the world, where the key is the room ID and the value is the
   *     Room object.
   * @param items A map of items in the world, where the key is the item ID and the value is the
   *     Item object.
   * @param puzzles A map of puzzles in the world, where the key is the puzzle ID and the value is
   *     the Puzzle object.
   * @param startRoomId The ID of the starting room in the world.
   */
  public World(
      String version,
      Map<String, Room> rooms,
      Map<String, Item> items,
      Map<String, Puzzle> puzzles,
      String startRoomId) {
    this.version = version;
    this.roomsById = rooms;
    this.itemsById = items;
    this.puzzlesById = puzzles;
    this.startRoomId = startRoomId;
  }

  public String getVersion() {
    return version;
  }

  public Map<String, Item> getItems() {
    return itemsById;
  }

  public Map<String, Room> getRooms() {
    return roomsById;
  }

  public Map<String, Puzzle> getPuzzles() {
    return puzzlesById;
  }

  public String getStartRoomId() {
    return startRoomId;
  }

  /**
   * Retrieves a room by its ID.
   *
   * @param roomId The ID of the room to retrieve.
   * @return An Optional containing the Room if found, or empty if not found.
   */
  public Optional<Room> getRoomById(String roomId) {
    return Optional.ofNullable(roomsById.get(roomId));
  }

  /**
   * Finds a puzzle by its ID.
   *
   * @param puzzleId The ID of the puzzle to find.
   * @return An Optional containing the Puzzle if found, or empty if not found.
   */
  public Optional<Puzzle> findPuzzle(String puzzleId) {
    return Optional.ofNullable(puzzlesById.get(puzzleId));
  }

  /**
   * Finds a room by its ID.
   *
   * @param roomId The ID of the room to find.
   * @return An Optional containing the Room if found, or empty if not found.
   */
  public Optional<Room> findRoom(String roomId) {
    return Optional.ofNullable(roomsById.get(roomId));
  }

  /**
   * Finds an item by its ID.
   *
   * @param itemId The ID of the item to find.
   * @return An Optional containing the Item if found, or empty if not found.
   */
  public Optional<Item> findItem(String itemId) {
    return Optional.ofNullable(itemsById.get(itemId));
  }
}
