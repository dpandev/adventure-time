package com.dpandev.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game with attributes such as name, score, current room, inventory
 * items, puzzles solved, and rooms visited.
 */
public final class Player extends Character {
  private int score = 0;
  private String roomId;
  private final List<String> inventoryItems;
  private List<String> puzzlesSolved;
  private final List<String> roomsVisited;

  /**
   * Constructs a new Player with the specified name.
   *
   * @param name The name of the player.
   */
  public Player(String name, String startingRoomId) {
    super(name, 100);
    this.roomId = startingRoomId;
    this.inventoryItems = new ArrayList<String>();
    this.puzzlesSolved = new ArrayList<String>();
    this.roomsVisited = new ArrayList<String>();
    increaseBaseAttack(10);
    increaseBaseDefense(0);
  }

  /**
   * Gets the current score of the player.
   *
   * @return The player's score.
   */
  public int getScore() {
    return score;
  }

  /**
   * Increases the player's score by a specified amount.
   *
   * @param amount The amount to increase the score by.
   */
  public void increaseScore(int amount) {
    this.score += amount;
  }

  /**
   * Decreases the player's score by a specified amount.
   *
   * @param amount The amount to decrease the score by.
   */
  public void decreaseScore(int amount) {
    this.score -= amount;
  }

  /**
   * Gets the current room ID for the player.
   *
   * @return The ID of the current room.
   */
  public String getRoomId() {
    return roomId;
  }

  /**
   * Sets the current room ID for the player.
   *
   * @param roomId The ID of the room to set as the current room.
   */
  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  /**
   * Gets the list of item IDs in the player's inventory.
   *
   * @return A list of item IDs.
   */
  public List<String> getInventoryItemIds() {
    return inventoryItems;
  }

  /**
   * Adds an item to the player's inventory.
   *
   * @param itemId The ID of the item to add.
   */
  public void addItemToInventory(String itemId) {
    this.inventoryItems.add(itemId);
  }

  /**
   * Removes an item from the player's inventory.
   *
   * @param itemId The ID of the item to remove.
   * @return true if the item was removed, false otherwise.
   */
  public boolean removeItemFromInventory(String itemId) {
    return this.inventoryItems.remove(itemId);
  }

  /**
   * Checks if the player has a specific item in their inventory.
   *
   * @param itemId The ID of the item to check.
   * @return true if the item is in the inventory, false otherwise.
   */
  public boolean hasItemInInventory(String itemId) {
    return this.inventoryItems.contains(itemId);
  }

  /**
   * Gets the list of puzzles solved by the player.
   *
   * @return A list of puzzle IDs that the player has solved.
   */
  public List<String> getPuzzlesSolved() {
    return puzzlesSolved;
  }

  /**
   * Sets the list of puzzles solved by the player.
   *
   * @param puzzlesSolved A list of puzzle IDs that the player has solved.
   */
  public void setPuzzlesSolved(List<String> puzzlesSolved) {
    this.puzzlesSolved = puzzlesSolved;
  }

  /**
   * Gets the list of rooms visited by the player.
   *
   * @return A list of room IDs that the player has visited.
   */
  public List<String> getRoomsVisited() {
    return roomsVisited;
  }

  /**
   * Adds a room to the list of rooms visited by the player.
   *
   * @param roomId The ID of the room to add.
   */
  public void addRoomToRoomsVisited(String roomId) {
    this.roomsVisited.add(roomId);
  }
}
