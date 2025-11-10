package com.dpandev.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for the Player class. */
class PlayerTest {

  private Player player;

  @BeforeEach
  void setUp() {
    player = new Player("TestPlayer", "room1");
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("should create player with name and room")
    void shouldCreatePlayerWithNameAndRoom() {
      assertEquals("TestPlayer", player.getName());
      assertEquals("room1", player.getRoomId());
    }

    @Test
    @DisplayName("should initialize with default stats")
    void shouldInitializeWithDefaultStats() {
      assertEquals(100, player.getCurrentHealth());
      assertEquals(100, player.getMaxHealth());
      assertEquals(10, player.getBaseAttack());
      assertEquals(0, player.getBaseDefense());
    }

    @Test
    @DisplayName("should generate unique ID")
    void shouldGenerateUniqueId() {
      Player player2 = new Player("Player2", "room2");

      assertNotNull(player.getId());
      assertNotNull(player2.getId());
      assertFalse(player.getId().equals(player2.getId()));
    }

    @Test
    @DisplayName("should initialize empty inventory and equipment")
    void shouldInitializeEmptyInventoryAndEquipment() {
      assertTrue(player.getInventoryItemIds().isEmpty());
      assertTrue(player.getEquippedItems().isEmpty());
      assertTrue(player.getPuzzlesSolved().isEmpty());
    }
  }

  @Nested
  @DisplayName("Health Management Tests")
  class HealthManagementTests {

    @Test
    @DisplayName("should take damage correctly")
    void shouldTakeDamageCorrectly() {
      player.takeDamage(30);

      assertEquals(70, player.getCurrentHealth());
    }

    @Test
    @DisplayName("should not go below zero health")
    void shouldNotGoBelowZeroHealth() {
      player.takeDamage(150);

      assertEquals(0, player.getCurrentHealth());
    }

    @Test
    @DisplayName("should heal correctly")
    void shouldHealCorrectly() {
      player.takeDamage(40);
      player.heal(20);

      assertEquals(80, player.getCurrentHealth());
    }

    @Test
    @DisplayName("should not heal above max health")
    void shouldNotHealAboveMaxHealth() {
      player.heal(50);

      assertEquals(100, player.getCurrentHealth());
    }

    @Test
    @DisplayName("should check if alive")
    void shouldCheckIfAlive() {
      assertTrue(player.isAlive());

      player.takeDamage(100);

      assertFalse(player.isAlive());
    }
  }

  @Nested
  @DisplayName("Inventory Tests")
  class InventoryTests {

    @Test
    @DisplayName("should add item to inventory")
    void shouldAddItemToInventory() {
      player.addItemToInventory("sword");

      assertTrue(player.getInventoryItemIds().contains("sword"));
      assertEquals(1, player.getInventoryItemIds().size());
    }

    @Test
    @DisplayName("should remove item from inventory")
    void shouldRemoveItemFromInventory() {
      player.addItemToInventory("sword");
      player.removeItemFromInventory("sword");

      assertFalse(player.getInventoryItemIds().contains("sword"));
      assertTrue(player.getInventoryItemIds().isEmpty());
    }

    @Test
    @DisplayName("should handle multiple items")
    void shouldHandleMultipleItems() {
      player.addItemToInventory("sword");
      player.addItemToInventory("shield");
      player.addItemToInventory("potion");

      assertEquals(3, player.getInventoryItemIds().size());
    }
  }

  @Nested
  @DisplayName("Equipment Tests")
  class EquipmentTests {

    @Test
    @DisplayName("should equip item in slot")
    void shouldEquipItemInSlot() {
      String previousItem = player.equipItem(Player.EquipmentSlot.WEAPON, "sword");

      assertNull(previousItem);
      assertEquals("sword", player.getEquippedItem(Player.EquipmentSlot.WEAPON));
      assertTrue(player.hasEquippedItem(Player.EquipmentSlot.WEAPON));
    }

    @Test
    @DisplayName("should unequip item from slot")
    void shouldUnequipItemFromSlot() {
      player.equipItem(Player.EquipmentSlot.WEAPON, "sword");
      String unequippedItem = player.unequipItem(Player.EquipmentSlot.WEAPON);

      assertEquals("sword", unequippedItem);
      assertFalse(player.hasEquippedItem(Player.EquipmentSlot.WEAPON));
      assertNull(player.getEquippedItem(Player.EquipmentSlot.WEAPON));
    }

    @Test
    @DisplayName("should replace equipped item")
    void shouldReplaceEquippedItem() {
      player.equipItem(Player.EquipmentSlot.WEAPON, "sword");
      String previousItem = player.equipItem(Player.EquipmentSlot.WEAPON, "axe");

      assertEquals("sword", previousItem);
      assertEquals("axe", player.getEquippedItem(Player.EquipmentSlot.WEAPON));
    }

    @Test
    @DisplayName("should get all equipped items")
    void shouldGetAllEquippedItems() {
      player.equipItem(Player.EquipmentSlot.WEAPON, "sword");
      player.equipItem(Player.EquipmentSlot.HELMET, "helmet");

      assertEquals(2, player.getEquippedItems().size());
      assertEquals("sword", player.getEquippedItems().get(Player.EquipmentSlot.WEAPON));
      assertEquals("helmet", player.getEquippedItems().get(Player.EquipmentSlot.HELMET));
    }
  }

  @Nested
  @DisplayName("Stats Modification Tests")
  class StatsModificationTests {

    @Test
    @DisplayName("should increase attack")
    void shouldIncreaseAttack() {
      player.increaseBaseAttack(15);

      assertEquals(25, player.getBaseAttack());
    }

    @Test
    @DisplayName("should decrease attack")
    void shouldDecreaseAttack() {
      player.increaseBaseAttack(15);
      player.decreaseBaseAttack(5);

      assertEquals(20, player.getBaseAttack());
    }

    @Test
    @DisplayName("should increase defense")
    void shouldIncreaseDefense() {
      player.increaseBaseDefense(10);

      assertEquals(10, player.getBaseDefense());
    }

    @Test
    @DisplayName("should decrease defense")
    void shouldDecreaseDefense() {
      player.increaseBaseDefense(15);
      player.decreaseBaseDefense(5);

      assertEquals(10, player.getBaseDefense());
    }
  }

  @Nested
  @DisplayName("Room and Puzzle Tests")
  class RoomAndPuzzleTests {

    @Test
    @DisplayName("should change room")
    void shouldChangeRoom() {
      player.setRoomId("room2");

      assertEquals("room2", player.getRoomId());
    }

    @Test
    @DisplayName("should track solved puzzles")
    void shouldTrackSolvedPuzzles() {
      player.getPuzzlesSolved().add("puzzle1");
      player.getPuzzlesSolved().add("puzzle2");

      assertEquals(2, player.getPuzzlesSolved().size());
      assertTrue(player.getPuzzlesSolved().contains("puzzle1"));
      assertTrue(player.getPuzzlesSolved().contains("puzzle2"));
    }
  }
}
