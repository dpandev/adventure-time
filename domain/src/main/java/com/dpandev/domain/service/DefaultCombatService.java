package com.dpandev.domain.service;

import com.dpandev.domain.model.Monster;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import java.util.Optional;
import java.util.Random;

/** Default implementation of CombatService for turn-based combat. */
public final class DefaultCombatService implements CombatService {

  private final Random random;

  public DefaultCombatService() {
    this.random = new Random();
  }

  // Constructor for testing with seeded random
  public DefaultCombatService(Random random) {
    this.random = random;
  }

  @Override
  public CommandResult initiateCombat(GameContext ctx, String monsterName) {
    if (monsterName == null || monsterName.isBlank()) {
      return CommandResult.fail("Attack what? Specify a monster name.");
    }

    var world = ctx.world();
    var player = ctx.player();

    // Check if already in combat
    if (ctx.isInCombat()) {
      return CommandResult.fail("You are already in combat!");
    }

    Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("You seem to be in an unknown location."); // should not happen
    }

    Room room = roomOpt.get();
    if (room.getMonsterId() == null) {
      return CommandResult.fail("There is no monster here to attack.");
    }

    Optional<Monster> monsterOpt = world.findMonster(room.getMonsterId());
    if (monsterOpt.isEmpty()) {
      return CommandResult.fail("The monster has disappeared.");
    }

    Monster monster = monsterOpt.get();

    // Check if the monster name matches
    if (!monster.getName().equalsIgnoreCase(monsterName)) {
      return CommandResult.fail(
          "There is no " + monsterName + " here. Did you mean " + monster.getName() + "?");
    }

    // Check if monster is already dead
    if (!monster.isAlive()) {
      return CommandResult.fail("The " + monster.getName() + " is already dead.");
    }

    // Combat starts
    ctx.startCombat(room.getMonsterId());
    StringBuilder sb = new StringBuilder();
    sb.append("You engage in combat with the ").append(monster.getName()).append("!\n");
    sb.append("=== COMBAT STARTED ===\n");
    sb.append(getCombatStatus(ctx));
    sb.append("\nWhat will you do? (attack, heal, inventory, equip, unequip, stats)");

    return CommandResult.success(sb.toString());
  }

  @Override
  public CommandResult playerAttack(GameContext ctx) {
    if (!ctx.isInCombat()) {
      return CommandResult.fail("You are not in combat.");
    }

    var world = ctx.world();
    var player = ctx.player();

    Optional<Monster> monsterOpt = world.findMonster(ctx.getCombatMonsterId());
    if (monsterOpt.isEmpty() || !monsterOpt.get().isAlive()) {
      ctx.endCombat();
      return CommandResult.fail("The monster has disappeared.");
    }

    Monster monster = monsterOpt.get();

    StringBuilder sb = new StringBuilder();

    // Player turn -attack
    int playerDamage = calculatePlayerDamage(ctx);
    int actualDamage = Math.max(0, playerDamage - monster.getBaseDefense());
    monster.takeDamage(actualDamage);

    sb.append("You attack the ")
        .append(monster.getName())
        .append(" for ")
        .append(actualDamage)
        .append(" damage!\n");

    if (!monster.isAlive()) {
      sb.append("\n*** You have defeated the ").append(monster.getName()).append("! ***\n");
      sb.append("Victory! You may continue your adventure.\n");

      // remove dead monster
      Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
      roomOpt.ifPresent(
          room -> {
            // monster id remains but is dead
          });

      ctx.endCombat();
      return CommandResult.success(sb.toString());
    }

    // Monster turn - attack
    sb.append("\n");
    int monsterDamage = calculateMonsterDamage(monster);
    int actualPlayerDamage = Math.max(0, monsterDamage - player.getBaseDefense());
    player.takeDamage(actualPlayerDamage);

    sb.append("The ")
        .append(monster.getName())
        .append(" attacks you for ")
        .append(actualPlayerDamage)
        .append(" damage!\n");
    // Check if player is dead
    if (!player.isAlive()) {
      sb.append("\n");
      sb.append(handlePlayerDeath(ctx).message());
      return CommandResult.fail(sb.toString());
    }

    // Combat continues
    sb.append("\n").append(getCombatStatus(ctx));
    sb.append("\nWhat will you do? (attack, heal, inventory, equip, unequip, stats)");

    return CommandResult.success(sb.toString());
  }

  @Override
  public CommandResult ignoreMonster(GameContext ctx, String monsterName) {
    if (ctx.isInCombat()) {
      return CommandResult.fail("You cannot flee from combat once it has started!");
    }

    var world = ctx.world();
    var player = ctx.player();

    Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("You seem to be in an unknown location.");
    }

    Room room = roomOpt.get();
    if (room.getMonsterId() == null) {
      return CommandResult.fail("There is no monster here to ignore.");
    }

    Optional<Monster> monsterOpt = world.findMonster(room.getMonsterId());
    if (monsterOpt.isEmpty()) {
      return CommandResult.fail("The monster has disappeared.");
    }

    Monster monster = monsterOpt.get();

    // Check if the monster name matches
    if (monsterName != null
        && !monsterName.isBlank()
        && !monster.getName().equalsIgnoreCase(monsterName)) {
      return CommandResult.fail(
          "There is no " + monsterName + " here. Did you mean " + monster.getName() + "?");
    }

    // Kill monster -make it disappear by setting health to 0
    monster.takeDamage(monster.getCurrentHealth());

    StringBuilder sb = new StringBuilder();
    sb.append("You choose to ignore the ")
        .append(monster.getName())
        .append(" and it disappears forever.\n");

    return CommandResult.success(sb.toString());
  }

  @Override
  public CommandResult handlePlayerDeath(GameContext ctx) {
    ctx.endCombat();

    StringBuilder sb = new StringBuilder();
    sb.append("\n╔════════════════════════════╗\n");
    sb.append("║       GAME OVER!           ║\n");
    sb.append("╚════════════════════════════╝\n");
    sb.append("\nYou have fallen in battle.\n\n");
    sb.append("Options:\n");
    sb.append("  'new' - Start a new game\n");
    sb.append("  'load' - Load your last saved game\n");
    sb.append("  'quit' - Exit the game\n");

    return CommandResult.fail(sb.toString());
  }

  /**
   * Calculate the damage the player deals based on base attack and equipped items.
   *
   * @param ctx the game context
   * @return the total damage
   */
  private int calculatePlayerDamage(GameContext ctx) {
    var player = ctx.player();
    var world = ctx.world();

    int totalDamage = player.getBaseAttack();

    // Add attack bonuses from equipped items
    for (String itemId : player.getEquippedItems().values()) {
      world
          .findItem(itemId)
          .ifPresent(
              item -> {
                // Attack bonus is already added to base attack when equipped
              });
    }

    return totalDamage;
  }

  /**
   * Calculate the damage the monster deals, with chance for critical hit (double damage).
   *
   * @param monster the monster attacking
   * @return the damage dealt
   */
  private int calculateMonsterDamage(Monster monster) {
    double roll = random.nextDouble(); // random num between 0.0 and 1.0
    int baseDamage = monster.getBaseAttack();

    // If roll is below threshold, deal double damage
    if (roll < monster.getCriticalHitThreshold()) {
      return baseDamage * 2;
    }

    return baseDamage;
  }

  /**
   * Get the current combat status showing player and monster health.
   *
   * @param ctx the game context
   * @return the combat status string
   */
  private String getCombatStatus(GameContext ctx) {
    var player = ctx.player();
    var world = ctx.world();

    StringBuilder sb = new StringBuilder();

    Optional<Monster> monsterOpt = world.findMonster(ctx.getCombatMonsterId());
    if (monsterOpt.isEmpty()) {
      return "Monster not found.";
    }

    Monster monster = monsterOpt.get();

    sb.append("─────────────────────────────\n");
    sb.append("Player HP: ")
        .append(player.getCurrentHealth())
        .append("/")
        .append(player.getMaxHealth())
        .append("\n");
    sb.append(monster.getName())
        .append(" HP: ")
        .append(monster.getCurrentHealth())
        .append("/")
        .append(monster.getMaxHealth())
        .append("\n");
    sb.append("─────────────────────────────");

    return sb.toString();
  }
}
