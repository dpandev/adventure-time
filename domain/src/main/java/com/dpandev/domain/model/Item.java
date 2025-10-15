package com.dpandev.domain.model;

/** Represents an item in the game. */
public final class Item {
  private final String id;
  private final String name;
  private final String description;

  /**
   * Constructor for the Item class.
   *
   * @param id the unique identifier of the item.
   * @param name the name of the item.
   * @param description the description of the item.
   */
  public Item(String id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  /**
   * Get the unique identifier of the item.
   *
   * @return the unique identifier of the item.
   */
  public String getId() {
    return id;
  }

  /**
   * Get the name of the item.
   *
   * @return the name of the item.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the description of the item.
   *
   * @return the description of the item.
   */
  public String getDescription() {
    return description;
  }
}
