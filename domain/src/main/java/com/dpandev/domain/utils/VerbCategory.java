package com.dpandev.domain.utils;

/** Categories for different types of verbs/commands in the game. */
public enum VerbCategory {
  MOVEMENT,
  INTERACTION,
  INVENTORY,
  COMBAT,
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
      case GO, LOOK, MAP, STATS -> MOVEMENT;
      case SOLVE -> INTERACTION;
      case ATTACK, IGNORE -> COMBAT;
      case INSPECT, PICKUP, DROP, USE, INVENTORY, EQUIP, UNEQUIP, HEAL -> INVENTORY;
      case HELP, SAVE, LOAD, QUIT, NEW_GAME, UNKNOWN -> SYSTEM;
    };
  }
}
