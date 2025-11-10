package com.dpandev.domain.model;

/**
 * Represents a monster character in the game with specific attributes such as name, description,
 * maximum health, base attack, and base defense.
 */
public class Monster extends Character {
  private final String description;
  private final double criticalHitThreshold; // Threshold for double damage (0.0 to 1.0)

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
    this(name, description, maxHealth, baseAttack, baseDefense, 0.3); // Default 30% chance
  }

  /**
   * Constructs a new Monster with the specified attributes including critical hit threshold.
   *
   * @param name The name of the monster.
   * @param description A brief description of the monster.
   * @param maxHealth The maximum health of the monster.
   * @param baseAttack The base attack value of the monster.
   * @param baseDefense The base defense value of the monster.
   * @param criticalHitThreshold The threshold for critical hits (0.0 to 1.0).
   */
  public Monster(
      String name,
      String description,
      int maxHealth,
      int baseAttack,
      int baseDefense,
      double criticalHitThreshold) {
    super(name, maxHealth);
    this.description = description;
    this.criticalHitThreshold = Math.max(0.0, Math.min(1.0, criticalHitThreshold));
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

  /**
   * Gets the critical hit threshold for this monster.
   *
   * @return The critical hit threshold (0.0 to 1.0).
   */
  public double getCriticalHitThreshold() {
    return criticalHitThreshold;
  }
}
