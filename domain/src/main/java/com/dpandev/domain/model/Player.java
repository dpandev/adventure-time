package com.dpandev.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a player in the game with attributes such as ID, name, score, current room, inventory
 * items, and puzzles solved.
 */
public final class Player {
  private final UUID id = UUID.randomUUID();
  private String name;
  private int score;
  private String roomId;
  private final List<String> inventoryItems;
  private List<String> puzzlesSolved;

  /**
   * Constructs a new Player with the specified name.
   *
   * @param name The name of the player.
   */
  public Player(String name) {
    this.name = name;
    this.score = 0;
    this.inventoryItems = new ArrayList<String>();
  }

  /**
   * Gets the unique identifier of the player.
   *
   * @return The UUID of the player.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Gets the player's name.
   *
   * @return The name of the player.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the player's name.
   *
   * @param name The name to set for the player.
   */
  public void setName(String name) {
    this.name = name;
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
  public List<String> getInventoryItems() {
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
   */
  public void removeItemFromInventory(String itemId) {
    this.inventoryItems.remove(itemId);
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
}
