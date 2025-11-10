package com.dpandev.domain.world;

import com.dpandev.domain.model.Item;
import com.dpandev.domain.model.Monster;
import com.dpandev.domain.model.Puzzle;
import com.dpandev.domain.model.PuzzleType;
import com.dpandev.domain.model.Room;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JSON-based world loader that parses world definition files and creates World instances. Supports
 * loading from classpath resources or filesystem paths. Falls back to example.json if the specified
 * path is not found.
 */
public final class JsonWorldLoader implements WorldLoader {

  private static final String DEFAULT_WORLD_PACK = "worldpacks/example.json";
  private static final int DEFAULT_MAX_ATTEMPTS = 3; // for puzzles
  private static final int DEFAULT_MONSTER_HEALTH = 30;
  private static final int DEFAULT_MONSTER_ATTACK = 5;
  private static final int DEFAULT_MONSTER_DEFENSE = 0;
  private static final double DEFAULT_CRIT_THRESHOLD = 0.3; // for monsters damage

  private final String path;
  private final ObjectMapper mapper;

  /**
   * Creates a new JsonWorldLoader for the specified resource path.
   *
   * @param resourcePath path to the world JSON file (classpath or filesystem)
   * @throws NullPointerException if resourcePath is null
   */
  public JsonWorldLoader(String resourcePath) {
    this.path = Objects.requireNonNull(resourcePath, "resourcePath cannot be null");
    this.mapper = new ObjectMapper();
  }

  @Override
  public World load() {
    String pathToLoad = resolvePathWithFallback();
    JsonNode root = read(pathToLoad);

    String version = reqText(root, "version");
    String startRoomId = reqText(root, "startRoomId");

    Map<String, Item> itemsById = parseItems(root.path("items"));
    Map<String, Monster> monstersById = parseMonsters(root.path("monsters"));
    Map<String, Puzzle> puzzlesById = new HashMap<>();
    Map<String, Room> roomsById = parseRooms(root.path("rooms"), puzzlesById);

    validateStartRoom(startRoomId, roomsById);

    return new World(version, roomsById, itemsById, puzzlesById, monstersById, startRoomId);
  }

  /**
   * Resolves the world pack path, falling back to default if the specified path is not found.
   *
   * @return the path to load
   */
  private String resolvePathWithFallback() {
    try (InputStream testStream = open(path)) {
      if (testStream != null) {
        return path;
      }
    } catch (IOException e) {
      // Fall through to default
    }
    return DEFAULT_WORLD_PACK;
  }

  /**
   * Validates that the start room exists in the loaded rooms.
   *
   * @param startRoomId the ID of the start room
   * @param roomsById map of all loaded rooms
   * @throws IllegalStateException if start room is not found
   */
  private void validateStartRoom(String startRoomId, Map<String, Room> roomsById) {
    if (!roomsById.containsKey(startRoomId)) {
      throw new IllegalStateException(
          "Start room '" + startRoomId + "' not found in world definition");
    }
  }

  /**
   * Parses items from the JSON node.
   *
   * @param itemsNode the JSON array of items
   * @return map of item ID to Item
   */
  private Map<String, Item> parseItems(JsonNode itemsNode) {
    Map<String, Item> itemsById = new HashMap<>();
    if (!itemsNode.isArray()) {
      return itemsById;
    }

    for (JsonNode node : itemsNode) {
      Item item = parseItem(node);
      itemsById.put(item.getId(), item);
    }
    return itemsById;
  }

  /**
   * Parses a single item from JSON.
   *
   * @param node the item JSON node
   * @return the parsed Item
   */
  private Item parseItem(JsonNode node) {
    String id = reqText(node, "id");
    String name = reqText(node, "name");
    String description = optText(node, "description", "");
    Item.ItemType type =
        parseEnum(
            node,
            new String[] {"type", "itemType"},
            Item.ItemType.MISCELLANEOUS,
            Item.ItemType.class);
    Item.ArmorType armorType =
        parseEnum(node, new String[] {"armorType", "subtype"}, null, Item.ArmorType.class);
    Item.ConsumableType consumableType =
        parseEnum(
            node, new String[] {"consumableType", "subtype"}, null, Item.ConsumableType.class);

    return Item.builder()
        .id(id)
        .name(name)
        .description(description)
        .type(type)
        .consumableType(consumableType)
        .armorType(armorType)
        .attackBonus(node.path("attackBonus").asInt(0))
        .defenseBonus(node.path("defenseBonus").asInt(0))
        .healthRestore(node.path("healthRestore").asInt(0))
        .build();
  }

  /**
   * Parses monsters from the JSON node.
   *
   * @param monstersNode the JSON array of monsters
   * @return map of monster ID to Monster
   */
  private Map<String, Monster> parseMonsters(JsonNode monstersNode) {
    Map<String, Monster> monstersById = new HashMap<>();
    if (!monstersNode.isArray()) {
      return monstersById;
    }

    for (JsonNode node : monstersNode) {
      String id = reqText(node, "id");
      Monster monster = parseMonster(node);
      monstersById.put(id, monster);
    }
    return monstersById;
  }

  /**
   * Parses a single monster from JSON.
   *
   * @param node the monster JSON node
   * @return the parsed Monster
   */
  private Monster parseMonster(JsonNode node) {
    String name = reqText(node, "name");
    String description = optText(node, "description", "");
    int maxHealth = node.path("maxHealth").asInt(DEFAULT_MONSTER_HEALTH);
    int baseAttack = node.path("baseAttack").asInt(DEFAULT_MONSTER_ATTACK);
    int baseDefense = node.path("baseDefense").asInt(DEFAULT_MONSTER_DEFENSE);
    double criticalHitThreshold =
        node.path("criticalHitThreshold").asDouble(DEFAULT_CRIT_THRESHOLD);

    return new Monster(name, description, maxHealth, baseAttack, baseDefense, criticalHitThreshold);
  }

  /**
   * Parses rooms from the JSON node, including inline puzzles.
   *
   * @param roomsNode the JSON array of rooms
   * @param puzzlesById map to populate with parsed puzzles
   * @return map of room ID to Room
   */
  private Map<String, Room> parseRooms(JsonNode roomsNode, Map<String, Puzzle> puzzlesById) {
    Map<String, Room> roomsById = new HashMap<>();
    if (!roomsNode.isArray()) {
      return roomsById;
    }

    for (JsonNode node : roomsNode) {
      Room room = parseRoom(node, puzzlesById);
      roomsById.put(room.getId(), room);
    }
    return roomsById;
  }

  /**
   * Parses a single room from JSON.
   *
   * @param node the room JSON node
   * @param puzzlesById map to populate with inline puzzles
   * @return the parsed Room
   */
  private Room parseRoom(JsonNode node, Map<String, Puzzle> puzzlesById) {
    String id = reqText(node, "id");
    String name = reqText(node, "name");
    String description = optText(node, "description", "");
    Map<String, String> exits = parseExits(node.path("exits"));
    List<String> itemIds = parseStringList(node.path("itemIds"));
    String puzzleId = parseRoomPuzzle(node, puzzlesById);
    String monsterId = optText(node, "monsterId", null);

    return Room.builder()
        .id(id)
        .name(name)
        .description(description)
        .exits(exits)
        .itemIds(itemIds)
        .puzzleId(puzzleId)
        .monsterId(monsterId)
        .build();
  }

  /**
   * Parses room exits from JSON.
   *
   * @param exitsNode the exits JSON object
   * @return map of direction to room ID
   */
  private Map<String, String> parseExits(JsonNode exitsNode) {
    Map<String, String> exits = new LinkedHashMap<>();
    if (!exitsNode.isObject()) {
      return exits;
    }

    Iterator<String> it = exitsNode.fieldNames();
    while (it.hasNext()) {
      String dir = it.next();
      String to = exitsNode.path(dir).asText(null);
      if (to != null && !to.isBlank()) {
        exits.put(dir, to);
      }
    }
    return exits;
  }

  /**
   * Parses a JSON array into a list of strings.
   *
   * @param arrayNode the JSON array node
   * @return list of strings
   */
  private List<String> parseStringList(JsonNode arrayNode) {
    List<String> list = new ArrayList<>();
    if (!arrayNode.isArray()) {
      return list;
    }

    for (JsonNode element : arrayNode) {
      String value = element.asText(null);
      if (value != null && !value.isBlank()) {
        list.add(value);
      }
    }
    return list;
  }

  /**
   * Parses inline puzzle definition from a room, if present.
   *
   * @param roomNode the room JSON node
   * @param puzzlesById map to populate with the parsed puzzle
   * @return the puzzle ID, or null if no puzzle
   */
  private String parseRoomPuzzle(JsonNode roomNode, Map<String, Puzzle> puzzlesById) {
    JsonNode puzzlesNode = roomNode.path("puzzles");
    if (puzzlesNode.isArray() && puzzlesNode.size() > 0) {
      JsonNode puzzleNode = puzzlesNode.get(0);
      Puzzle puzzle = parseInlinePuzzle(puzzleNode);
      puzzlesById.put(puzzle.getId(), puzzle);
      return puzzle.getId();
    }

    return null;
  }

  /**
   * Parses an inline puzzle definition.
   *
   * @param node the puzzle JSON node
   * @return the parsed Puzzle
   */
  private Puzzle parseInlinePuzzle(JsonNode node) {
    String id = reqText(node, "id");
    String description = optText(node, "description", "");
    String prompt = optText(node, "prompt", description);
    int maxAttempts = node.path("maxAttempts").asInt(DEFAULT_MAX_ATTEMPTS);
    PuzzleType type =
        parseEnum(node, new String[] {"type", "puzzleType"}, PuzzleType.RIDDLE, PuzzleType.class);

    Map<String, Object> solution = parsePuzzleSolution(node);
    String finalDescription = prompt.isEmpty() ? description : prompt;
    String rewardItemId = optText(node, "rewardItemId", null);

    return new Puzzle(
        id, finalDescription, type, solution, Puzzle.PuzzlePhase.LOCKED, maxAttempts, rewardItemId);
  }

  /**
   * Parses puzzle solution from JSON, supporting both simple answer and complex solution formats.
   *
   * @param node the puzzle JSON node
   * @return map of solution data
   */
  private Map<String, Object> parsePuzzleSolution(JsonNode node) {
    Map<String, Object> solution = new HashMap<>();

    if (node.hasNonNull("answer")) {
      solution.put("answer", node.get("answer").asText());
    } else if (node.hasNonNull("solution")) {
      solution =
          mapper.convertValue(node.path("solution"), new TypeReference<Map<String, Object>>() {});
    }

    return solution;
  }

  /**
   * Reads and parses JSON from the specified path.
   *
   * @param resourcePath path to the JSON file
   * @return parsed JSON root node
   * @throws UncheckedIOException if reading fails
   * @throws IllegalStateException if JSON is invalid
   */
  private JsonNode read(String resourcePath) {
    try (InputStream in = open(resourcePath)) {
      if (in == null) {
        throw new IOException(
            "Resource not found: " + resourcePath + " (searched classpath and filesystem)");
      }
      return mapper.readTree(in);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read world JSON from '" + resourcePath + "'", e);
    } catch (RuntimeException e) {
      throw new IllegalStateException(
          "Invalid world JSON in '" + resourcePath + "': " + e.getMessage(), e);
    }
  }

  /**
   * Opens an input stream for the specified path. Tries classpath resources first, then filesystem.
   *
   * @param resourcePath path to open
   * @return input stream, or null if not found
   * @throws IOException if an I/O error occurs
   */
  private InputStream open(String resourcePath) throws IOException {
    // Try thread context class loader first
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream in = (cl != null) ? cl.getResourceAsStream(stripSlash(resourcePath)) : null;
    if (in != null) {
      return in;
    }

    // Try this class's class loader with leading slash
    in = JsonWorldLoader.class.getResourceAsStream(ensureSlash(resourcePath));
    if (in != null) {
      return in;
    }

    // Try filesystem as last resort
    Path file = Path.of(resourcePath);
    return Files.exists(file) ? Files.newInputStream(file) : null;
  }

  /**
   * Gets required text field from JSON node.
   *
   * @param node JSON node
   * @param field field name
   * @return field value
   * @throws IllegalArgumentException if field is missing or blank
   */
  private static String reqText(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || !value.isTextual() || value.asText().isBlank()) {
      throw new IllegalArgumentException(
          "Missing or empty required field '" + field + "' in: " + node);
    }
    return value.asText();
  }

  /**
   * Gets optional text field from JSON node with default value.
   *
   * @param node JSON node
   * @param field field name
   * @param defaultValue default if field is missing or blank
   * @return field value or default
   */
  private static String optText(JsonNode node, String field, String defaultValue) {
    JsonNode value = node.get(field);
    if (value == null || value.isNull()) {
      return defaultValue;
    }
    String text = value.asText();
    return (text == null || text.isBlank()) ? defaultValue : text;
  }

  /**
   * Gets the first non-blank text value from multiple possible field names.
   *
   * @param node JSON node
   * @param fieldNames array of field names to try in order
   * @return first non-blank value found, or null
   */
  private static String firstText(JsonNode node, String[] fieldNames) {
    for (String fieldName : fieldNames) {
      if (node.hasNonNull(fieldName)) {
        String value = node.get(fieldName).asText();
        if (value != null && !value.isBlank()) {
          return value;
        }
      }
    }
    return null;
  }

  /**
   * Parses enum value with lenient matching (case-insensitive, handles spaces and dashes).
   *
   * @param node JSON node
   * @param fieldNames array of possible field names
   * @param defaultValue default value if field not found
   * @param enumClass enum class type
   * @param <E> enum type
   * @return parsed enum value or default
   * @throws IllegalArgumentException if value is invalid for the enum
   */
  private static <E extends Enum<E>> E parseEnum(
      JsonNode node, String[] fieldNames, E defaultValue, Class<E> enumClass) {
    String rawValue = firstText(node, fieldNames);
    if (rawValue == null) {
      return defaultValue;
    }

    // Normalize: uppercase, replace spaces and dashes with underscores
    String normalized = rawValue.trim().toUpperCase().replace(' ', '_').replace('-', '_');

    try {
      return Enum.valueOf(enumClass, normalized);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid "
              + enumClass.getSimpleName()
              + " value: '"
              + rawValue
              + "'. Valid values: "
              + java.util.Arrays.toString(enumClass.getEnumConstants()),
          e);
    }
  }

  /**
   * Removes leading slash from path if present.
   *
   * @param path path string
   * @return path without leading slash
   */
  private static String stripSlash(String path) {
    return (path != null && path.startsWith("/")) ? path.substring(1) : path;
  }

  /**
   * Ensures path has leading slash.
   *
   * @param path path string
   * @return path with leading slash
   */
  private static String ensureSlash(String path) {
    return (path != null && path.startsWith("/")) ? path : "/" + path;
  }
}
