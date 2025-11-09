package com.dpandev.domain.model;

import java.util.UUID;

/** Represents a character in the game with attributes such as name, health, attack, and defense. */
public abstract class Character {
  private final UUID id = UUID.randomUUID();
  private String name;
  private final int maxHealth;
  private int currentHealth;
  private int baseAttack = 0;
  private int baseDefense = 0;

  /**
   * Constructs a Character with the specified name and maximum health.
   *
   * @param name the name of the character
   * @param maxHealth the maximum health of the character
   */
  public Character(String name, int maxHealth) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.currentHealth = maxHealth;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public int getCurrentHealth() {
    return currentHealth;
  }

  public boolean isAlive() {
    return currentHealth > 0;
  }

  public void takeDamage(int damage) {
    currentHealth = Math.max(0, currentHealth - damage);
  }

  public void heal(int amount) {
    currentHealth = Math.min(maxHealth, currentHealth + amount);
  }

  public void increaseBaseAttack(int amount) {
    this.baseAttack += amount;
  }

  public void increaseBaseDefense(int amount) {
    this.baseDefense += amount;
  }

  public int getBaseAttack() {
    return baseAttack;
  }

  public int getBaseDefense() {
    return baseDefense;
  }

  public void decreaseBaseDefense(int amount) {
    this.baseDefense = Math.max(0, this.baseDefense - amount);
  }

  public void decreaseBaseAttack(int amount) {
    this.baseAttack = Math.max(0, this.baseAttack - amount);
  }
}
