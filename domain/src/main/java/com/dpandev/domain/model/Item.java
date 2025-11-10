package com.dpandev.domain.model;

/** Represents an item in the game. */
public final class Item {
  private final String id;
  private final String name;
  private final String description;
  private final ItemType itemType;
  private final ArmorType armorType;
  private final ConsumableType consumableType;
  private final int attackBonus;
  private final int defenseBonus;
  private final int healthRestore;

  /** Enum representing different types of items. */
  public enum ItemType {
    WEAPON,
    ARMOR,
    CONSUMABLE,
    QUEST,
    MISCELLANEOUS
  }

  /** Subtypes for armor items. */
  public enum ArmorType {
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS
  }

  /** Subtypes for consumable items. */
  public enum ConsumableType {
    HEALTH_POTION,
    STRENGTH_BOOST,
    DEFENSE_BOOST,
    INVISIBILITY_POTION,
    INVINCIBILITY_POTION
  }

  /** Private constructor to enforce the use of the Builder pattern. */
  private Item(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.itemType = builder.type;
    this.armorType = builder.armorType;
    this.consumableType = builder.consumableType;
    this.attackBonus = builder.attackBonus;
    this.defenseBonus = builder.defenseBonus;
    this.healthRestore = builder.healthRestore;
  }

  /** Returns a new Builder instance for constructing Item objects. */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class for constructing Item instances. */
  public static class Builder {
    private String id;
    private String name;
    private String description;
    private ItemType type = ItemType.MISCELLANEOUS;
    private ArmorType armorType = null;
    private ConsumableType consumableType = null;
    private int attackBonus = 0;
    private int defenseBonus = 0;
    private int healthRestore = 0;

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

    public Builder type(ItemType type) {
      this.type = type;
      return this;
    }

    public Builder armorType(ArmorType armorType) {
      this.armorType = armorType;
      return this;
    }

    public Builder consumableType(ConsumableType consumableType) {
      this.consumableType = consumableType;
      return this;
    }

    public Builder attackBonus(int attackBonus) {
      this.attackBonus = attackBonus;
      return this;
    }

    public Builder defenseBonus(int defenseBonus) {
      this.defenseBonus = defenseBonus;
      return this;
    }

    public Builder healthRestore(int healthRestore) {
      this.healthRestore = healthRestore;
      return this;
    }

    /** Builds and returns the Item instance. */
    public Item build() {
      return new Item(this);
    }
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

  /**
   * Get the type of the item.
   *
   * @return the type of the item.
   */
  public ItemType getItemType() {
    return itemType;
  }

  /**
   * Get the armor type of the item, if applicable.
   *
   * @return the armor type of the item.
   */
  public ArmorType getArmorType() {
    return armorType;
  }

  /**
   * Get the consumable type of the item, if applicable.
   *
   * @return the consumable type of the item.
   */
  public ConsumableType getConsumableType() {
    return consumableType;
  }

  /**
   * Get the attack bonus provided by the item.
   *
   * @return the attack bonus.
   */
  public int getAttackBonus() {
    return attackBonus;
  }

  /**
   * Get the defense bonus provided by the item.
   *
   * @return the defense bonus.
   */
  public int getDefenseBonus() {
    return defenseBonus;
  }

  /**
   * Get the health restoration value of the item.
   *
   * @return the health restoration value.
   */
  public int getHealthRestore() {
    return healthRestore;
  }
}
