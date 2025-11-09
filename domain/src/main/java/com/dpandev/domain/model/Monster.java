package com.dpandev.domain.model;

public class Monster extends Character {
  public Monster(String name, String description, int maxHealth, int baseAttack, int baseDefense) {
    super(name, maxHealth);
    this.baseAttack = baseAttack;
    this.baseDefense = baseDefense;
  }
}
