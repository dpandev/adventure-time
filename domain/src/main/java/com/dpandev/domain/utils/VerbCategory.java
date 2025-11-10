package com.dpandev.domain.utils;

/** Categories for different types of verbs/commands in the game. */
public enum VerbCategory {
  MOVEMENT,
  INTERACTION,
  INVENTORY,
  SYSTEM;

  /**
   * Get the VerbCategory for a given Verb.
   *
   * @param v the verb
   * @return the corresponding VerbCategory
   */
  public static VerbCategory of(Verb v) {
    if (v == null) {
      return SYSTEM;
    }
    return switch (v) {
      case GO, LOOK, STATS -> MOVEMENT;
      case SOLVE, ATTACK, IGNORE -> INTERACTION;
      case INSPECT, PICKUP, DROP, USE, INVENTORY, EQUIP, UNEQUIP, HEAL -> INVENTORY;
      case HELP, SAVE, LOAD, QUIT, UNKNOWN -> SYSTEM;
    };
  }
}
