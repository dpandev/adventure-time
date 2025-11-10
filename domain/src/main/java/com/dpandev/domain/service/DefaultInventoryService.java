package com.dpandev.domain.service;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Monster;
import com.dpandev.domain.model.Player;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DefaultInventoryService implements InventoryService {

  private Optional<Item> findItemInCurrentRoom(GameContext ctx, String userInput) {
    var player = ctx.player();
    var world = ctx.world();

    Optional<Room> roomOpt = world.findRoom(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return Optional.empty();
    }

    var room = roomOpt.get();

    // try to find by exact ID match first in the current room
    if (room.hasItem(userInput)) {
      return world.findItem(userInput);
    }

    // try to find by fuzzy name match (case-insensitive) ONLY in current room items
    return room.getItemIds().stream()
        .map(world::findItem)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(item -> item.getName().equalsIgnoreCase(userInput))
        .findFirst();
  }

  private Optional<Item> findItemInPlayerInventory(GameContext ctx, String userInput) {
    var player = ctx.player();
    var world = ctx.world();

    // try exact ID match first
    Optional<Item> byId =
        player.getInventoryItemIds().stream()
            .filter(id -> id.equals(userInput))
            .findFirst()
            .flatMap(world::findItem);

    if (byId.isPresent()) {
      return byId;
    }

    // try name match (case-insensitive)
    return player.getInventoryItemIds().stream()
        .map(world::findItem)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(item -> item.getName().equalsIgnoreCase(userInput))
        .findFirst();
  }

  private Optional<Item> findEquippedItem(GameContext ctx, String userInput) {
    var player = ctx.player();
    var world = ctx.world();

    // check all equipment slots
    for (Player.EquipmentSlot slot : Player.EquipmentSlot.values()) {
      String equippedId = player.getEquippedItem(slot);
      if (equippedId != null) {
        if (equippedId.equals(userInput)) {
          return world.findItem(equippedId);
        }
        Optional<Item> item = world.findItem(equippedId);
        if (item.isPresent() && item.get().getName().equalsIgnoreCase(userInput)) {
          return item;
        }
      }
    }
    return Optional.empty();
  }

  @Override
  public CommandResult inventory(GameContext ctx) {
    var player = ctx.player();
    var world = ctx.world();
    List<String> inv = player.getInventoryItemIds();
    var equippedItems = player.getEquippedItems();

    StringBuilder sb = new StringBuilder();

    // show equipped items
    if (!equippedItems.isEmpty()) {
      sb.append("=== EQUIPPED ===\n");
      for (var entry : equippedItems.entrySet()) {
        Optional<Item> itemOpt = world.findItem(entry.getValue());
        if (itemOpt.isPresent()) {
          Item item = itemOpt.get();
          sb.append("  [").append(entry.getKey()).append("] ").append(item.getName()).append("\n");
        }
      }
      sb.append("\n");
    }

    // show inventory items
    if (inv.isEmpty() && equippedItems.isEmpty()) {
      return CommandResult.success("You don't have any items yet.");
    } else if (inv.isEmpty()) {
      return CommandResult.success(sb.toString().trim());
    }

    sb.append("=== INVENTORY ===\n");
    for (String itemId : inv) {
      Optional<Item> itemOpt = world.findItem(itemId);
      if (itemOpt.isPresent()) {
        Item item = itemOpt.get();
        sb.append("  - ")
            .append(item.getName())
            .append(": ")
            .append(item.getDescription())
            .append("\n");
      } else {
        sb.append("  - Unknown item with ID: ").append(itemId).append("\n");
      }
    }
    return CommandResult.success(sb.toString().trim());
  }

  @Override
  public CommandResult pickup(GameContext ctx, String userInput) {
    if (userInput == null || userInput.isBlank()) {
      return CommandResult.fail("Pickup what?");
    }
    var player = ctx.player();
    var world = ctx.world();

    // validate current room before removing item from it.
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown."); // this should not happen
    }

    Optional<Item> itemOpt = findItemInCurrentRoom(ctx, userInput);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("There is no " + userInput + " here to pick up.");
    }

    // add to player inv then remove from Room
    Item item = itemOpt.get();
    player.addItemToInventory(item.getId());
    currentRoomOpt.get().removeItemFromRoom(item.getId());

    return CommandResult.success(
        item.getName()
            + " has been picked up from the room and successfully added to the player inventory.");
  }

  @Override
  public CommandResult drop(GameContext ctx, String userInput) {
    if (userInput == null || userInput.isBlank()) {
      return CommandResult.fail("Drop what?");
    }
    var world = ctx.world();
    var player = ctx.player();

    Optional<Room> roomOpt = world.findRoom(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("Your current location is unknown."); // this should not happen
    }

    // Check if item is in player's inventory
    Optional<Item> itemOpt = findItemInPlayerInventory(ctx, userInput);
    if (itemOpt.isEmpty()) {
      // Check if the item is equipped instead
      Optional<Item> equippedItemOpt = findEquippedItem(ctx, userInput);
      if (equippedItemOpt.isPresent()) {
        return CommandResult.fail(
            "You need to unequip the " + equippedItemOpt.get().getName() + " first.");
      }
      return CommandResult.fail("You don't have a " + userInput + " to drop.");
    }

    Item item = itemOpt.get();
    player.removeItemFromInventory(item.getId());
    roomOpt.get().addItemToRoom(item.getId());

    return CommandResult.success(
        item.getName()
            + " has been dropped successfully from the player inventory and placed in "
            + roomOpt.get().getName()
            + ".");
  }

  @Override
  public CommandResult use(GameContext ctx, String userInput) {
    if (userInput == null || userInput.isBlank()) {
      return CommandResult.fail("Use what?");
    }

    // First, try to find in player inventory
    Optional<Item> inventoryItemOpt = findItemInPlayerInventory(ctx, userInput);
    if (inventoryItemOpt.isPresent()) {
      Item item = inventoryItemOpt.get();
      // Handle inventory item usage (potion, key, etc.)
      return CommandResult.success("You use the " + item.getName() + ".");
    }

    // If not in inventory, check if it's a fixture in the current room(e.g.,lever)
    Optional<Item> roomItemOpt = findItemInCurrentRoom(ctx, userInput);
    if (roomItemOpt.isPresent()) {
      Item item = roomItemOpt.get();
      // Handle room fixture usage (lever, switch, etc.)
      return CommandResult.success(
          "You interact with the " + item.getName() + ". (Nothing special happens.)");
    }

    return CommandResult.fail("You don't see a " + userInput + " to use.");
  }

  @Override
  public CommandResult inspect(GameContext ctx, String userInput) {
    if (userInput == null || userInput.isBlank()) {
      return CommandResult.fail("Inspect what?");
    }

    var world = ctx.world();

    // try to find in player inventory
    Optional<Item> inventoryItemOpt = findItemInPlayerInventory(ctx, userInput);
    if (inventoryItemOpt.isPresent()) {
      Item item = inventoryItemOpt.get();
      return CommandResult.success(formatItemDescription(item));
    }

    // Check if the item is equipped
    Optional<Item> equippedItemOpt = findEquippedItem(ctx, userInput);
    if (equippedItemOpt.isPresent()) {
      Item item = equippedItemOpt.get();
      return CommandResult.success(formatItemDescription(item) + "\n(Currently equipped)");
    }

    // If not in inventory or equipped, check if in the current room
    Optional<Item> roomItemOpt = findItemInCurrentRoom(ctx, userInput);
    if (roomItemOpt.isPresent()) {
      Item item = roomItemOpt.get();
      return CommandResult.success(formatItemDescription(item));
    }

    // Check if it's a monster in the current room
    Optional<Room> currentRoomOpt = world.getRoomById(ctx.player().getRoomId());
    if (currentRoomOpt.isPresent()) {
      Room room = currentRoomOpt.get();
      if (room.getMonsterId() != null) {
        Optional<Monster> monsterOpt = world.findMonster(room.getMonsterId());
        if (monsterOpt.isPresent()) {
          Monster monster = monsterOpt.get();
          if (monster.getName().equalsIgnoreCase(userInput) && monster.isAlive()) {
            return CommandResult.success(formatMonsterDescription(monster));
          }
        }
      }
    }

    return CommandResult.fail("You don't see a " + userInput + " to inspect.");
  }

  /**
   * Format item description with bonuses.
   *
   * @param item the item to format
   * @return formatted item description
   */
  private String formatItemDescription(Item item) {
    StringBuilder sb = new StringBuilder();
    sb.append(item.getName()).append(": ").append(item.getDescription());

    // Show bonuses if the item has any
    List<String> bonuses = new ArrayList<>();
    if (item.getAttackBonus() > 0) {
      bonuses.add("+" + item.getAttackBonus() + " Attack");
    }
    if (item.getDefenseBonus() > 0) {
      bonuses.add("+" + item.getDefenseBonus() + " Defense");
    }
    if (item.getHealthRestore() > 0) {
      bonuses.add("Restores " + item.getHealthRestore() + " HP");
    }

    if (!bonuses.isEmpty()) {
      sb.append("\n[").append(String.join(", ", bonuses)).append("]");
    }

    return sb.toString();
  }

  /**
   * Format monster description with stats.
   *
   * @param monster the monster to format
   * @return formatted monster description
   */
  private String formatMonsterDescription(Monster monster) {
    StringBuilder sb = new StringBuilder();
    sb.append(monster.getName()).append(": ").append(monster.getDescription());
    sb.append("\n[HP: ")
        .append(monster.getCurrentHealth())
        .append("/")
        .append(monster.getMaxHealth());
    sb.append(", Attack: ").append(monster.getBaseAttack());
    sb.append(", Defense: ").append(monster.getBaseDefense());
    sb.append("]");
    return sb.toString();
  }

  @Override
  public CommandResult equip(GameContext ctx, String userInput) {
    if (userInput == null || userInput.isBlank()) {
      return CommandResult.fail("Equip what?");
    }

    var player = ctx.player();

    // Try to find the item in player's inventory
    Optional<Item> itemOpt = findItemInPlayerInventory(ctx, userInput);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("You don't have a " + userInput + " to equip.");
    }

    Item item = itemOpt.get();
    // determine the equipment slot based on item type
    Player.EquipmentSlot slot;
    if (item.getItemType() == Item.ItemType.WEAPON) {
      slot = Player.EquipmentSlot.WEAPON;
    } else if (item.getItemType() == Item.ItemType.ARMOR) {
      if (item.getArmorType() == null) {
        return CommandResult.fail(item.getName() + " cannot be equipped.");
      }
      slot =
          switch (item.getArmorType()) {
            case HELMET -> Player.EquipmentSlot.HELMET;
            case CHESTPLATE -> Player.EquipmentSlot.CHESTPLATE;
            case LEGGINGS -> Player.EquipmentSlot.LEGGINGS;
            case BOOTS -> Player.EquipmentSlot.BOOTS;
          };
    } else {
      return CommandResult.fail("You can only equip weapons or armor.");
    }

    // Unequip any existing item in that slot
    String previouslyEquippedItemId = player.getEquippedItem(slot);
    if (previouslyEquippedItemId != null) {
      // previous item goes back to inventory (swapped)
      Optional<Item> prevItemOpt = ctx.world().findItem(previouslyEquippedItemId);
      // apply stat change (remove bonuses from previous item)
      prevItemOpt.ifPresent(
          prev -> {
            player.decreaseBaseAttack(prev.getAttackBonus());
            player.decreaseBaseDefense(prev.getDefenseBonus());
          });
      // Add previous item back to inventory
      player.addItemToInventory(previouslyEquippedItemId);
    }

    // remove from inventory and equip
    player.removeItemFromInventory(item.getId());
    player.equipItem(slot, item.getId());

    // apply new item stat bonuses
    player.increaseBaseAttack(item.getAttackBonus());
    player.increaseBaseDefense(item.getDefenseBonus());

    return CommandResult.success("You have equipped the " + item.getName() + ".");
  }

  @Override
  public CommandResult unequip(GameContext ctx, String userInput) {
    if (userInput == null || userInput.isBlank()) {
      return CommandResult.fail("Unequip what?");
    }

    var player = ctx.player();

    // Find the equipped item by user input (ID or name)
    Optional<Item> itemOpt = findEquippedItem(ctx, userInput);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("You don't have a " + userInput + " equipped.");
    }

    Item item = itemOpt.get();

    // Find which slot has this item
    Player.EquipmentSlot slotToUnequip = null;
    for (Player.EquipmentSlot slot : Player.EquipmentSlot.values()) {
      if (player.hasEquippedItem(slot) && player.getEquippedItem(slot).equals(item.getId())) {
        slotToUnequip = slot;
        break;
      }
    }

    if (slotToUnequip == null) {
      return CommandResult.fail("Unable to unequip " + item.getName() + ".");
    }

    // Remove stat bonuses
    player.decreaseBaseAttack(item.getAttackBonus());
    player.decreaseBaseDefense(item.getDefenseBonus());

    // Unequip and add back to inventory
    player.unequipItem(slotToUnequip);
    player.addItemToInventory(item.getId());

    return CommandResult.success("You unequipped the " + item.getName() + ".");
  }

  @Override
  public CommandResult heal(GameContext ctx, String userInput) {
    if (userInput == null || userInput.isBlank()) {
      return CommandResult.fail("Heal with what?");
    }

    var player = ctx.player();

    Optional<Item> itemOpt = findItemInPlayerInventory(ctx, userInput);
    if (itemOpt.isEmpty()) {
      return CommandResult.fail("You don't have a " + userInput + " to heal with.");
    }
    Item item = itemOpt.get();
    if (item.getItemType() != Item.ItemType.CONSUMABLE
        || item.getConsumableType() != Item.ConsumableType.HEALTH_POTION) {
      return CommandResult.fail("You can only heal with health potions.");
    }

    // check if player is already at max health
    if (player.getCurrentHealth() >= player.getMaxHealth()) {
      return CommandResult.fail("You are already at full health.");
    }

    // apply healing
    int healthRestored = item.getHealthRestore();
    player.heal(item.getHealthRestore());

    // remove used item from inventory
    player.removeItemFromInventory(item.getId());

    return CommandResult.success(
        "You used the " + item.getName() + " and restored " + healthRestored + " health.");
  }
}
