package com.dpandev.domain.model;

import java.util.UUID;

public abstract class Character {
  final UUID id = UUID.randomUUID();
  final String name;
  final int maxHealth;
  int currentHealth;
  int baseAttack;
  int baseDefense;

  public Character(String name, int maxHealth) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.currentHealth = maxHealth;
    this.baseAttack = 0;
    this.baseDefense = 0;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
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
