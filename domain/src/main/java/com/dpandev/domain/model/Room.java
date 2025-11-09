package com.dpandev.domain.model;

import java.util.ArrayList;
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
  private String monsterId;

  public Room(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.exits = Map.copyOf(builder.exits != null ? builder.exits : Map.of());
    this.itemIds = new ArrayList<>(builder.itemIds != null ? builder.itemIds : List.of());
    this.puzzleId = builder.puzzleId;
    this.monsterId = builder.monsterId;
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

  public void addItemToRoom(String itemId) {
    this.itemIds.add(itemId);
  }

  public boolean hasItem(String itemId) {
    return this.itemIds.contains(itemId);
  }

  public void removeItemFromRoom(String itemId) {
    this.itemIds.remove(itemId);
  }

  public String getMonsterId() {
    return monsterId;
  }

  /** Builder pattern for creating Room instances */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class for Room */
  public static class Builder {
    private String id;
    private String name;
    private String description;
    private Map<String, String> exits;
    private List<String> itemIds;
    private String puzzleId;
    private String monsterId;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder exits(Map<String, String> exits) {
      this.exits = exits;
      return this;
    }

    public Builder itemIds(List<String> itemIds) {
      this.itemIds = itemIds;
      return this;
    }

    public Builder puzzleId(String puzzleId) {
      this.puzzleId = puzzleId;
      return this;
    }

    public Builder monsterId(String monsterId) {
      this.monsterId = monsterId;
      return this;
    }

    public Room build() {
      return new Room(this);
    }
  }
}
