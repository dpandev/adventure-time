package com.dpandev.domain.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Monster;
import com.dpandev.domain.model.Puzzle;
import com.dpandev.domain.model.PuzzleType;
import com.dpandev.domain.model.Room;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for the World class. */
class WorldTest {

  private World world;
  private Map<String, Room> rooms;
  private Map<String, Item> items;
  private Map<String, Puzzle> puzzles;
  private Map<String, Monster> monsters;

  @BeforeEach
  void setUp() {
    // Create test rooms
    rooms = new HashMap<>();
    rooms.put(
        "room1",
        Room.builder()
            .id("room1")
            .name("Room 1")
            .description("First room")
            .exits(Map.of("north", "room2"))
            .itemIds(List.of("sword"))
            .build());
    rooms.put(
        "room2",
        Room.builder()
            .id("room2")
            .name("Room 2")
            .description("Second room")
            .exits(Map.of("south", "room1"))
            .build());

    // Create test items
    items = new HashMap<>();
    items.put(
        "sword",
        Item.builder()
            .id("sword")
            .name("Steel Sword")
            .description("A sharp blade")
            .type(Item.ItemType.WEAPON)
            .attackBonus(10)
            .build());
    items.put(
        "potion",
        Item.builder()
            .id("potion")
            .name("Health Potion")
            .description("Restores HP")
            .type(Item.ItemType.CONSUMABLE)
            .consumableType(Item.ConsumableType.HEALTH_POTION)
            .healthRestore(20)
            .build());

    // Create test puzzles
    puzzles = new HashMap<>();
    puzzles.put(
        "puzzle1",
        new Puzzle(
            "puzzle1",
            "What am I?",
            PuzzleType.RIDDLE,
            Map.of("answer", "echo"),
            Puzzle.PuzzlePhase.LOCKED,
            3,
            null));

    // Create test monsters
    monsters = new HashMap<>();
    monsters.put("goblin", new Monster("Goblin", "A small green creature", 30, 5, 2, 0.3));
    monsters.put("troll", new Monster("Ice Troll", "A large frosty beast", 50, 10, 5, 0.25));

    world = new World("1.0", rooms, items, puzzles, monsters, "room1");
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("should create world with all properties")
    void shouldCreateWorldWithAllProperties() {
      assertNotNull(world);
      assertEquals("1.0", world.getVersion());
      assertEquals("room1", world.getStartRoomId());
      assertEquals(2, world.getRooms().size());
      assertEquals(2, world.getItems().size());
      assertEquals(1, world.getPuzzles().size());
      assertEquals(2, world.getMonsters().size());
    }

    @Test
    @DisplayName("should create immutable copies of maps")
    void shouldCreateImmutableCopiesOfMaps() {
      // Modify original maps
      rooms.put("room3", Room.builder().id("room3").name("Room 3").description("Third").build());
      items.put("shield", Item.builder().id("shield").name("Shield").build());
      puzzles.put(
          "puzzle2",
          new Puzzle(
              "puzzle2", "Test", PuzzleType.RIDDLE, Map.of(), Puzzle.PuzzlePhase.LOCKED, 3, null));
      monsters.put("dragon", new Monster("Dragon", "Fire!", 100, 20, 10, 0.2));

      // World should still have original data
      assertEquals(2, world.getRooms().size());
      assertEquals(2, world.getItems().size());
      assertEquals(1, world.getPuzzles().size());
      assertEquals(2, world.getMonsters().size());
    }
  }

  @Nested
  @DisplayName("Room Finding Tests")
  class RoomFindingTests {

    @Test
    @DisplayName("should find room by ID")
    void shouldFindRoomById() {
      Optional<Room> room = world.getRoomById("room1");

      assertTrue(room.isPresent());
      assertEquals("room1", room.get().getId());
      assertEquals("Room 1", room.get().getName());
    }

    @Test
    @DisplayName("should return empty for non-existent room ID")
    void shouldReturnEmptyForNonExistentRoomId() {
      Optional<Room> room = world.getRoomById("nonexistent");

      assertFalse(room.isPresent());
    }

    @Test
    @DisplayName("should find room using findRoom")
    void shouldFindRoomUsingFindRoom() {
      Optional<Room> room = world.findRoom("room2");

      assertTrue(room.isPresent());
      assertEquals("room2", room.get().getId());
    }
  }

  @Nested
  @DisplayName("Item Finding Tests")
  class ItemFindingTests {

    @Test
    @DisplayName("should find item by ID")
    void shouldFindItemById() {
      Optional<Item> item = world.findItem("sword");

      assertTrue(item.isPresent());
      assertEquals("sword", item.get().getId());
      assertEquals("Steel Sword", item.get().getName());
    }

    @Test
    @DisplayName("should return empty for non-existent item ID")
    void shouldReturnEmptyForNonExistentItemId() {
      Optional<Item> item = world.findItem("nonexistent");

      assertFalse(item.isPresent());
    }

    @Test
    @DisplayName("should find item by name case-insensitive")
    void shouldFindItemByNameCaseInsensitive() {
      Optional<Item> item1 = world.findItemByName("steel sword");
      Optional<Item> item2 = world.findItemByName("STEEL SWORD");
      Optional<Item> item3 = world.findItemByName("Steel Sword");

      assertTrue(item1.isPresent());
      assertTrue(item2.isPresent());
      assertTrue(item3.isPresent());
      assertEquals("sword", item1.get().getId());
      assertEquals("sword", item2.get().getId());
      assertEquals("sword", item3.get().getId());
    }

    @Test
    @DisplayName("should return empty for non-existent item name")
    void shouldReturnEmptyForNonExistentItemName() {
      Optional<Item> item = world.findItemByName("Nonexistent Item");

      assertFalse(item.isPresent());
    }
  }

  @Nested
  @DisplayName("Puzzle Finding Tests")
  class PuzzleFindingTests {

    @Test
    @DisplayName("should find puzzle by ID")
    void shouldFindPuzzleById() {
      Optional<Puzzle> puzzle = world.findPuzzle("puzzle1");

      assertTrue(puzzle.isPresent());
      assertEquals("puzzle1", puzzle.get().getId());
    }

    @Test
    @DisplayName("should return empty for non-existent puzzle ID")
    void shouldReturnEmptyForNonExistentPuzzleId() {
      Optional<Puzzle> puzzle = world.findPuzzle("nonexistent");

      assertFalse(puzzle.isPresent());
    }
  }

  @Nested
  @DisplayName("Monster Finding Tests")
  class MonsterFindingTests {

    @Test
    @DisplayName("should find monster by ID")
    void shouldFindMonsterById() {
      Optional<Monster> monster = world.findMonster("goblin");

      assertTrue(monster.isPresent());
      assertEquals("Goblin", monster.get().getName());
    }

    @Test
    @DisplayName("should return empty for non-existent monster ID")
    void shouldReturnEmptyForNonExistentMonsterId() {
      Optional<Monster> monster = world.findMonster("nonexistent");

      assertFalse(monster.isPresent());
    }

    @Test
    @DisplayName("should find monster by name case-insensitive")
    void shouldFindMonsterByNameCaseInsensitive() {
      Optional<Monster> monster1 = world.findMonsterByName("goblin");
      Optional<Monster> monster2 = world.findMonsterByName("GOBLIN");
      Optional<Monster> monster3 = world.findMonsterByName("Goblin");

      assertTrue(monster1.isPresent());
      assertTrue(monster2.isPresent());
      assertTrue(monster3.isPresent());
      assertEquals("Goblin", monster1.get().getName());
      assertEquals("Goblin", monster2.get().getName());
      assertEquals("Goblin", monster3.get().getName());
    }

    @Test
    @DisplayName("should return empty for non-existent monster name")
    void shouldReturnEmptyForNonExistentMonsterName() {
      Optional<Monster> monster = world.findMonsterByName("Dragon");

      assertFalse(monster.isPresent());
    }
  }

  @Nested
  @DisplayName("Getter Tests")
  class GetterTests {

    @Test
    @DisplayName("should return correct version")
    void shouldReturnCorrectVersion() {
      assertEquals("1.0", world.getVersion());
    }

    @Test
    @DisplayName("should return correct start room ID")
    void shouldReturnCorrectStartRoomId() {
      assertEquals("room1", world.getStartRoomId());
    }

    @Test
    @DisplayName("should return immutable rooms map")
    void shouldReturnImmutableRoomsMap() {
      Map<String, Room> worldRooms = world.getRooms();

      assertEquals(2, worldRooms.size());
      assertTrue(worldRooms.containsKey("room1"));
      assertTrue(worldRooms.containsKey("room2"));
    }

    @Test
    @DisplayName("should return immutable items map")
    void shouldReturnImmutableItemsMap() {
      Map<String, Item> worldItems = world.getItems();

      assertEquals(2, worldItems.size());
      assertTrue(worldItems.containsKey("sword"));
      assertTrue(worldItems.containsKey("potion"));
    }

    @Test
    @DisplayName("should return immutable puzzles map")
    void shouldReturnImmutablePuzzlesMap() {
      Map<String, Puzzle> worldPuzzles = world.getPuzzles();

      assertEquals(1, worldPuzzles.size());
      assertTrue(worldPuzzles.containsKey("puzzle1"));
    }

    @Test
    @DisplayName("should return immutable monsters map")
    void shouldReturnImmutableMonstersMap() {
      Map<String, Monster> worldMonsters = world.getMonsters();

      assertEquals(2, worldMonsters.size());
      assertTrue(worldMonsters.containsKey("goblin"));
      assertTrue(worldMonsters.containsKey("troll"));
    }
  }
}
