package com.dpandev.domain.model;

/**
 * Represents a monster character in the game with specific attributes such as name, description,
 * maximum health, base attack, and base defense.
 */
public class Monster extends Character {
  private final String description;

  /**
   * Constructs a new Monster with the specified attributes.
   *
   * @param name The name of the monster.
   * @param description A brief description of the monster.
   * @param maxHealth The maximum health of the monster.
   * @param baseAttack The base attack value of the monster.
   * @param baseDefense The base defense value of the monster.
   */
  public Monster(String name, String description, int maxHealth, int baseAttack, int baseDefense) {
    super(name, maxHealth);
    this.description = description;
    increaseBaseAttack(baseAttack);
    increaseBaseDefense(baseDefense);
  }

  /**
   * Gets the description of the monster.
   *
   * @return The monster's description.
   */
  public String getDescription() {
    return description;
  }
}
