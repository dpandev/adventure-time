package com.dpandev.domain.world;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Monster;
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
  private final Map<String, Monster> monstersById;

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
   * @param monsters A map of monsters in the world, where the key is the monster ID and the value
   *     is
   * @param startRoomId The ID of the starting room in the world.
   */
  public World(
      String version,
      Map<String, Room> rooms,
      Map<String, Item> items,
      Map<String, Puzzle> puzzles,
      Map<String, Monster> monsters,
      String startRoomId) {
    this.version = version;
    this.roomsById = Map.copyOf(rooms);
    this.itemsById = Map.copyOf(items);
    this.puzzlesById = Map.copyOf(puzzles);
    this.monstersById = Map.copyOf(monsters);
    this.startRoomId = startRoomId;
  }

  /**
   * Gets the version of the world.
   *
   * @return The version of the world.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Gets the items in the world.
   *
   * @return A map of items in the world.
   */
  public Map<String, Item> getItems() {
    return itemsById;
  }

  /**
   * Gets the rooms in the world.
   *
   * @return A map of rooms in the world.
   */
  public Map<String, Room> getRooms() {
    return roomsById;
  }

  /**
   * Gets the puzzles in the world.
   *
   * @return A map of puzzles in the world.
   */
  public Map<String, Puzzle> getPuzzles() {
    return puzzlesById;
  }

  /**
   * Gets the monsters in the world.
   *
   * @return A map of monsters in the world.
   */
  public Map<String, Monster> getMonsters() {
    return monstersById;
  }

  /**
   * Gets the ID of the starting room.
   *
   * @return The ID of the starting room.
   */
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

  /**
   * Finds an item by its name (case-insensitive).
   *
   * @param itemName The name of the item to find.
   * @return An Optional containing the Item if found, or empty if not found.
   */
  public Optional<Item> findItemByName(String itemName) {
    return itemsById.values().stream()
        .filter(item -> item.getName().equalsIgnoreCase(itemName))
        .findFirst();
  }

  /**
   * Finds a monster by its ID.
   *
   * @param monsterId The ID of the monster to find.
   * @return An Optional containing the Monster if found, or empty if not found.
   */
  public Optional<Monster> findMonster(String monsterId) {
    return Optional.ofNullable(monstersById.get(monsterId));
  }

  /**
   * Finds a monster by its name (case-insensitive).
   *
   * @param monsterName The name of the monster to find.
   * @return An Optional containing the Monster if found, or empty if not found.
   */
  public Optional<Monster> findMonsterByName(String monsterName) {
    return monstersById.values().stream()
        .filter(monster -> monster.getName().equalsIgnoreCase(monsterName))
        .findFirst();
  }
}
