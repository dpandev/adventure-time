package com.dpandev.domain.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Monster;
import com.dpandev.domain.model.Puzzle;
import com.dpandev.domain.model.PuzzleType;
import com.dpandev.domain.model.Room;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Comprehensive unit tests for JsonWorldLoader. */
class JsonWorldLoaderTest {

  @TempDir Path tempDir;

  // ============================================================================
  // Valid World Loading Tests
  // ============================================================================

  @Test
  void testLoadMinimalValidWorld() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Starting Room",
              "description": "A simple room",
              "exits": {},
              "itemIds": []
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("minimal.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    assertNotNull(world);
    assertEquals("1.0", world.getVersion());
    assertEquals("room1", world.getStartRoomId());
    assertEquals(1, world.getRooms().size());
    assertTrue(world.getItems().isEmpty());
    assertTrue(world.getMonsters().isEmpty());
  }

  @Test
  void testLoadWorldWithAllEntityTypes() throws IOException {
    String json =
        """
        {
          "version": "2.0",
          "startRoomId": "entrance",
          "items": [
            {
              "id": "sword",
              "name": "Steel Sword",
              "description": "A sharp blade",
              "type": "WEAPON",
              "attackBonus": 10
            },
            {
              "id": "potion",
              "name": "Health Potion",
              "type": "CONSUMABLE",
              "consumableType": "HEALTH_POTION",
              "healthRestore": 20
            },
            {
              "id": "helmet",
              "name": "Iron Helmet",
              "type": "ARMOR",
              "armorType": "HELMET",
              "defenseBonus": 5
            }
          ],
          "monsters": [
            {
              "id": "goblin",
              "name": "Goblin",
              "description": "A small green creature",
              "maxHealth": 25,
              "baseAttack": 5,
              "baseDefense": 2,
              "criticalHitThreshold": 0.2
            }
          ],
          "rooms": [
            {
              "id": "entrance",
              "name": "Entrance Hall",
              "description": "The entrance to the dungeon",
              "exits": { "north": "treasure_room" },
              "itemIds": ["sword", "potion"],
              "monsterId": "goblin",
              "puzzles": [
                {
                  "id": "door_puzzle",
                  "description": "A locked door blocks your path",
                  "type": "RIDDLE",
                  "answer": "friend",
                  "maxAttempts": 3,
                  "rewardItemId": "helmet"
                }
              ]
            },
            {
              "id": "treasure_room",
              "name": "Treasure Room",
              "description": "Gold and jewels everywhere",
              "exits": { "south": "entrance" },
              "itemIds": ["helmet"]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("full.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    assertEquals("2.0", world.getVersion());
    assertEquals("entrance", world.getStartRoomId());

    // Verify items
    assertEquals(3, world.getItems().size());
    Item sword = world.getItems().get("sword");
    assertNotNull(sword);
    assertEquals("Steel Sword", sword.getName());
    assertEquals(Item.ItemType.WEAPON, sword.getItemType());
    assertEquals(10, sword.getAttackBonus());

    Item potion = world.getItems().get("potion");
    assertEquals(Item.ItemType.CONSUMABLE, potion.getItemType());
    assertEquals(Item.ConsumableType.HEALTH_POTION, potion.getConsumableType());
    assertEquals(20, potion.getHealthRestore());

    Item helmet = world.getItems().get("helmet");
    assertEquals(Item.ItemType.ARMOR, helmet.getItemType());
    assertEquals(Item.ArmorType.HELMET, helmet.getArmorType());
    assertEquals(5, helmet.getDefenseBonus());

    // Verify monsters
    assertEquals(1, world.getMonsters().size());
    Monster goblin = world.getMonsters().get("goblin");
    assertNotNull(goblin);
    assertEquals("Goblin", goblin.getName());
    assertEquals(25, goblin.getMaxHealth());
    assertEquals(5, goblin.getBaseAttack());
    assertEquals(2, goblin.getBaseDefense());
    assertEquals(0.2, goblin.getCriticalHitThreshold(), 0.001);

    // Verify rooms
    assertEquals(2, world.getRooms().size());
    Room entrance = world.getRooms().get("entrance");
    assertNotNull(entrance);
    assertEquals("Entrance Hall", entrance.getName());
    assertEquals(1, entrance.getExits().size());
    assertEquals("treasure_room", entrance.getExits().get("north"));
    assertEquals(2, entrance.getItemIds().size());
    assertTrue(entrance.getItemIds().contains("sword"));
    assertTrue(entrance.getItemIds().contains("potion"));
    assertEquals("goblin", entrance.getMonsterId());
    assertNotNull(entrance.getPuzzleId());

    // Verify puzzle
    assertEquals(1, world.getPuzzles().size());
    Puzzle puzzle = world.getPuzzles().get("door_puzzle");
    assertNotNull(puzzle);
    assertEquals("A locked door blocks your path", puzzle.getDescription());
    assertEquals(PuzzleType.RIDDLE, puzzle.getPuzzleType());
    assertEquals("friend", puzzle.getSolution().get("answer"));
    assertEquals(3, puzzle.getMaxAttempts());
    assertEquals("helmet", puzzle.getRewardItemId());
  }

  // ============================================================================
  // Item Parsing Tests
  // ============================================================================

  @Test
  void testParseItemWithDefaults() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "misc_item",
              "name": "Random Thing"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("defaults.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Item item = world.getItems().get("misc_item");
    assertNotNull(item);
    assertEquals("Random Thing", item.getName());
    assertEquals(Item.ItemType.MISCELLANEOUS, item.getItemType());
    assertEquals("", item.getDescription());
    assertEquals(0, item.getAttackBonus());
    assertEquals(0, item.getDefenseBonus());
    assertEquals(0, item.getHealthRestore());
  }

  @Test
  void testParseItemWithLenientEnumValues() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "armor1",
              "name": "Chest Armor",
              "type": "armor",
              "armorType": "chestplate"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("lenient.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Item item = world.getItems().get("armor1");
    assertNotNull(item);
    assertEquals(Item.ItemType.ARMOR, item.getItemType());
    assertEquals(Item.ArmorType.CHESTPLATE, item.getArmorType());
  }

  // ============================================================================
  // Monster Parsing Tests
  // ============================================================================

  @Test
  void testParseMonsterWithDefaults() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [
            {
              "id": "slime",
              "name": "Slime"
            }
          ],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("monster_defaults.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Monster monster = world.getMonsters().get("slime");
    assertNotNull(monster);
    assertEquals("Slime", monster.getName());
    assertEquals("", monster.getDescription());
    assertEquals(30, monster.getMaxHealth()); // DEFAULT_MONSTER_HEALTH
    assertEquals(5, monster.getBaseAttack()); // DEFAULT_MONSTER_ATTACK
    assertEquals(0, monster.getBaseDefense()); // DEFAULT_MONSTER_DEFENSE
    assertEquals(0.3, monster.getCriticalHitThreshold(), 0.001); // DEFAULT_CRIT_THRESHOLD
  }

  @Test
  void testParseMonsterWithCustomValues() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [
            {
              "id": "dragon",
              "name": "Ancient Dragon",
              "description": "A fearsome beast",
              "maxHealth": 200,
              "baseAttack": 50,
              "baseDefense": 20,
              "criticalHitThreshold": 0.15
            }
          ],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("monster_custom.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Monster monster = world.getMonsters().get("dragon");
    assertNotNull(monster);
    assertEquals("Ancient Dragon", monster.getName());
    assertEquals("A fearsome beast", monster.getDescription());
    assertEquals(200, monster.getMaxHealth());
    assertEquals(50, monster.getBaseAttack());
    assertEquals(20, monster.getBaseDefense());
    assertEquals(0.15, monster.getCriticalHitThreshold(), 0.001);
  }

  // ============================================================================
  // Room Parsing Tests
  // ============================================================================

  @Test
  void testParseRoomWithMultipleExits() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "hub",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "hub",
              "name": "Central Hub",
              "description": "Paths lead in all directions",
              "exits": {
                "north": "north_room",
                "south": "south_room",
                "east": "east_room",
                "west": "west_room"
              }
            },
            {
              "id": "north_room",
              "name": "North Room",
              "exits": { "south": "hub" }
            },
            {
              "id": "south_room",
              "name": "South Room",
              "exits": { "north": "hub" }
            },
            {
              "id": "east_room",
              "name": "East Room",
              "exits": { "west": "hub" }
            },
            {
              "id": "west_room",
              "name": "West Room",
              "exits": { "east": "hub" }
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("exits.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Room hub = world.getRooms().get("hub");
    assertNotNull(hub);
    assertEquals(4, hub.getExits().size());
    assertEquals("north_room", hub.getExits().get("north"));
    assertEquals("south_room", hub.getExits().get("south"));
    assertEquals("east_room", hub.getExits().get("east"));
    assertEquals("west_room", hub.getExits().get("west"));
  }

  @Test
  void testParseRoomWithEmptyAndBlankExits() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {
                "north": "room2",
                "south": "",
                "east": "   ",
                "west": null
              }
            },
            {
              "id": "room2",
              "name": "Room 2",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("blank_exits.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Room room = world.getRooms().get("room1");
    assertEquals(1, room.getExits().size()); // Only "north" should be kept
    assertEquals("room2", room.getExits().get("north"));
  }

  // ============================================================================
  // Puzzle Parsing Tests
  // ============================================================================

  @Test
  void testParsePuzzleWithSimpleAnswer() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Puzzle Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "riddle1",
                  "description": "What has keys but no locks?",
                  "type": "RIDDLE",
                  "answer": "piano",
                  "maxAttempts": 5
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("puzzle_simple.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Puzzle puzzle = world.getPuzzles().get("riddle1");
    assertNotNull(puzzle);
    assertEquals("What has keys but no locks?", puzzle.getDescription());
    assertEquals(PuzzleType.RIDDLE, puzzle.getPuzzleType());
    assertEquals("piano", puzzle.getSolution().get("answer"));
    assertEquals(5, puzzle.getMaxAttempts());
    assertEquals(Puzzle.PuzzlePhase.LOCKED, puzzle.getPuzzlePhase());
  }

  @Test
  void testParsePuzzleWithComplexSolution() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Puzzle Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "combo_lock",
                  "description": "Enter the combination",
                  "type": "LOGIC",
                  "solution": {
                    "code": "1234",
                    "sequence": ["red", "blue", "green"]
                  },
                  "maxAttempts": 3
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("puzzle_complex.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Puzzle puzzle = world.getPuzzles().get("combo_lock");
    assertNotNull(puzzle);
    assertEquals(PuzzleType.LOGIC, puzzle.getPuzzleType());
    Map<String, Object> solution = puzzle.getSolution();
    assertEquals("1234", solution.get("code"));
    assertNotNull(solution.get("sequence"));
  }

  @Test
  void testParsePuzzleWithDefaults() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Puzzle Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "simple",
                  "description": "A puzzle",
                  "answer": "test"
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("puzzle_defaults.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Puzzle puzzle = world.getPuzzles().get("simple");
    assertNotNull(puzzle);
    assertEquals(PuzzleType.RIDDLE, puzzle.getPuzzleType()); // Default type
    assertEquals(3, puzzle.getMaxAttempts()); // Default max attempts
  }

  // ============================================================================
  // Error Handling Tests
  // ============================================================================

  @Test
  void testLoadNonExistentFileThrowsException() {
    // When both the specified path and default fallback don't exist, should throw
    JsonWorldLoader loader = new JsonWorldLoader("completely/nonexistent/path.json");
    assertThrows(UncheckedIOException.class, () -> loader.load());
  }

  @Test
  void testLoadInvalidJsonThrowsException() throws IOException {
    String invalidJson = "{ invalid json content !!!";

    Path worldFile = tempDir.resolve("invalid.json");
    Files.writeString(worldFile, invalidJson);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(UncheckedIOException.class, () -> loader.load());
  }

  @Test
  void testMissingVersionThrowsException() throws IOException {
    String json =
        """
        {
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_version.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testMissingStartRoomIdThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_start.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testStartRoomNotFoundThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "nonexistent",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("bad_start.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> loader.load());
    assertTrue(exception.getMessage().contains("Start room"));
    assertTrue(exception.getMessage().contains("nonexistent"));
  }

  @Test
  void testMissingItemIdThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "name": "No ID Item"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_item_id.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testMissingItemNameThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "item1"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_item_name.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testMissingRoomIdThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "name": "Room Without ID",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_room_id.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testInvalidItemTypeThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "item1",
              "name": "Bad Item",
              "type": "INVALID_TYPE"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("bad_item_type.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> loader.load());
    assertTrue(exception.getMessage().contains("Invalid"));
    assertTrue(exception.getMessage().contains("ItemType"));
  }

  @Test
  void testInvalidPuzzleTypeThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "puzzle1",
                  "description": "A puzzle",
                  "type": "INVALID_PUZZLE_TYPE",
                  "answer": "test"
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("bad_puzzle_type.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> loader.load());
    assertTrue(exception.getMessage().contains("Invalid"));
    assertTrue(exception.getMessage().contains("PuzzleType"));
  }

  // ============================================================================
  // Edge Cases
  // ============================================================================

  @Test
  void testNullResourcePathThrowsException() {
    assertThrows(NullPointerException.class, () -> new JsonWorldLoader(null));
  }

  @Test
  void testEmptyRoomsArrayThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": []
        }
        """;

    Path worldFile = tempDir.resolve("empty_rooms.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> loader.load());
    assertTrue(exception.getMessage().contains("Start room"));
  }

  @Test
  void testMissingRoomsArrayThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": []
        }
        """;

    Path worldFile = tempDir.resolve("no_rooms.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> loader.load());
    assertTrue(exception.getMessage().contains("Start room"));
  }

  @Test
  void testEmptyItemIdsInRoom() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "itemIds": ["", "  ", null]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("empty_item_ids.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Room room = world.getRooms().get("room1");
    assertTrue(room.getItemIds().isEmpty()); // Blank/null values filtered out
  }

  @Test
  void testRoomWithNoPuzzlesArray() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_puzzles.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Room room = world.getRooms().get("room1");
    assertNull(room.getPuzzleId());
  }

  @Test
  void testRoomWithEmptyPuzzlesArray() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": []
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("empty_puzzles.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Room room = world.getRooms().get("room1");
    assertNull(room.getPuzzleId());
  }

  @Test
  void testMultiplePuzzlesOnlyFirstIsUsed() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "puzzle1",
                  "description": "First puzzle",
                  "answer": "first"
                },
                {
                  "id": "puzzle2",
                  "description": "Second puzzle",
                  "answer": "second"
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("multiple_puzzles.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Room room = world.getRooms().get("room1");
    assertEquals("puzzle1", room.getPuzzleId()); // Only first puzzle used
    assertEquals(1, world.getPuzzles().size()); // Only first puzzle loaded
    assertNotNull(world.getPuzzles().get("puzzle1"));
    assertNull(world.getPuzzles().get("puzzle2"));
  }

  @Test
  void testBlankVersionThrowsException() throws IOException {
    String json =
        """
        {
          "version": "  ",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("blank_version.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  // ============================================================================
  // Additional Coverage Tests
  // ============================================================================

  @Test
  void testMissingMonsterNameThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [
            {
              "id": "monster1",
              "description": "A nameless monster"
            }
          ],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_monster_name.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testMissingRoomNameThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "description": "A room without a name",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_room_name.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testMissingPuzzleIdThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": [
                {
                  "description": "A puzzle without id",
                  "answer": "test"
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("no_puzzle_id.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testAlternativeFieldNames() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "item1",
              "name": "Test Item",
              "itemType": "WEAPON",
              "attackBonus": 5
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("alt_fields.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Item item = world.getItems().get("item1");
    assertEquals(Item.ItemType.WEAPON, item.getItemType());
  }

  @Test
  void testPuzzlePromptOverridesDescription() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "puzzle1",
                  "description": "Original description",
                  "prompt": "Custom prompt",
                  "answer": "test"
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("puzzle_prompt.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Puzzle puzzle = world.getPuzzles().get("puzzle1");
    assertEquals("Custom prompt", puzzle.getDescription());
  }

  @Test
  void testPuzzleWithNoSolution() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "puzzle1",
                  "description": "Puzzle with no solution"
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("puzzle_no_solution.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Puzzle puzzle = world.getPuzzles().get("puzzle1");
    assertNotNull(puzzle);
    assertTrue(puzzle.getSolution().isEmpty());
  }

  @Test
  void testNonArrayItemsNode() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": "not-an-array",
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("non_array_items.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    assertTrue(world.getItems().isEmpty());
  }

  @Test
  void testNonArrayMonstersNode() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": {"invalid": "object"},
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("non_array_monsters.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    assertTrue(world.getMonsters().isEmpty());
  }

  @Test
  void testNonObjectExitsNode() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": ["north", "south"]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("non_object_exits.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Room room = world.getRooms().get("room1");
    assertTrue(room.getExits().isEmpty());
  }

  @Test
  void testInvalidArmorTypeThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "item1",
              "name": "Bad Armor",
              "type": "ARMOR",
              "armorType": "INVALID_ARMOR_TYPE"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("bad_armor_type.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> loader.load());
    assertTrue(exception.getMessage().contains("Invalid"));
    assertTrue(exception.getMessage().contains("ArmorType"));
  }

  @Test
  void testInvalidConsumableTypeThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "item1",
              "name": "Bad Consumable",
              "type": "CONSUMABLE",
              "consumableType": "INVALID_CONSUMABLE"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("bad_consumable_type.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> loader.load());
    assertTrue(exception.getMessage().contains("Invalid"));
    assertTrue(exception.getMessage().contains("ConsumableType"));
  }

  @Test
  void testPuzzleTypeFieldAlternativeName() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "puzzle1",
                  "description": "Test",
                  "puzzleType": "MATH",
                  "answer": "42"
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("puzzle_type_alt.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Puzzle puzzle = world.getPuzzles().get("puzzle1");
    assertEquals(PuzzleType.MATH, puzzle.getPuzzleType());
  }

  @Test
  void testMonsterWithNegativeStats() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [
            {
              "id": "weak",
              "name": "Weak Monster",
              "maxHealth": -10,
              "baseAttack": -5,
              "baseDefense": -3
            }
          ],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("negative_stats.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Monster monster = world.getMonsters().get("weak");
    assertEquals(-10, monster.getMaxHealth()); // Accepts negative values
    assertEquals(-5, monster.getBaseAttack());
    assertEquals(-3, monster.getBaseDefense());
    // criticalHitThreshold will use default since not specified
    assertEquals(0.3, monster.getCriticalHitThreshold(), 0.001);
  }

  @Test
  void testItemWithNegativeStats() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "cursed",
              "name": "Cursed Item",
              "attackBonus": -10,
              "defenseBonus": -5,
              "healthRestore": -20
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("cursed_item.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Item item = world.getItems().get("cursed");
    assertEquals(-10, item.getAttackBonus());
    assertEquals(-5, item.getDefenseBonus());
    assertEquals(-20, item.getHealthRestore());
  }

  @Test
  void testBlankStartRoomIdThrowsException() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "   ",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("blank_start_room.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    assertThrows(IllegalArgumentException.class, () -> loader.load());
  }

  @Test
  void testDuplicateItemIds() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [
            {
              "id": "item1",
              "name": "First Item"
            },
            {
              "id": "item1",
              "name": "Second Item"
            }
          ],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {}
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("duplicate_items.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    // Last item with same ID wins
    assertEquals(1, world.getItems().size());
    assertEquals("Second Item", world.getItems().get("item1").getName());
  }

  @Test
  void testDuplicateRoomIds() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "First Room"
            },
            {
              "id": "room1",
              "name": "Second Room"
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("duplicate_rooms.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    // Last room with same ID wins
    assertEquals(1, world.getRooms().size());
    assertEquals("Second Room", world.getRooms().get("room1").getName());
  }

  @Test
  void testPuzzleWithZeroMaxAttempts() throws IOException {
    String json =
        """
        {
          "version": "1.0",
          "startRoomId": "room1",
          "items": [],
          "monsters": [],
          "rooms": [
            {
              "id": "room1",
              "name": "Room",
              "exits": {},
              "puzzles": [
                {
                  "id": "puzzle1",
                  "description": "Impossible puzzle",
                  "answer": "test",
                  "maxAttempts": 0
                }
              ]
            }
          ]
        }
        """;

    Path worldFile = tempDir.resolve("zero_attempts.json");
    Files.writeString(worldFile, json);

    JsonWorldLoader loader = new JsonWorldLoader(worldFile.toString());
    World world = loader.load();

    Puzzle puzzle = world.getPuzzles().get("puzzle1");
    assertEquals(0, puzzle.getMaxAttempts());
  }
}
