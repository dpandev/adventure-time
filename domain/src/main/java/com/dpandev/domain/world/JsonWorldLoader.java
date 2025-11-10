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
 * This was mostly generated with CGPT and Claude Sonnet 4.5 during debugging to fix load issue
 * quickly. TODO refactor into proper robust loader with validation, error reporting, tests, etc.
 * Tiny JSON world loader: simple I/O, lenient puzzle enums, minimal helpers.
 */
public final class JsonWorldLoader implements WorldLoader {

  private final String path;
  private final ObjectMapper mapper = new ObjectMapper();

  public JsonWorldLoader(String resourcePath) {
    this.path = Objects.requireNonNull(resourcePath, "resourcePath");
  }

  @Override
  public World load() {
    // Try to open the requested path, fall back to example.json if not found
    String pathToLoad = path;
    try (InputStream testStream = open(path)) {
      if (testStream == null) {
        pathToLoad = "worldpacks/example.json";
      }
    } catch (IOException e) {
      pathToLoad = "worldpacks/example.json";
    }

    JsonNode root = read(pathToLoad);

    // Required top-level fields
    String version = reqText(root, "version");
    String startRoomId = reqText(root, "startRoomId");

    // Items
    Map<String, Item> itemsById = new HashMap<>();
    JsonNode items = root.path("items");
    if (items.isArray()) {
      for (JsonNode n : items) {
        String id = reqText(n, "id");
        String name = reqText(n, "name");
        String description = optText(n, "description", "");
        Item.ItemType type =
            parseEnum(
                n,
                new String[] {"type", "itemType"},
                Item.ItemType.MISCELLANEOUS,
                Item.ItemType.class);
        Item.ArmorType armorType =
            parseEnum(n, new String[] {"armorType", "subtype"}, null, Item.ArmorType.class);
        Item.ConsumableType consumableType =
            parseEnum(
                n, new String[] {"consumableType", "subtype"}, null, Item.ConsumableType.class);
        int attackBonus = n.path("attackBonus").asInt(0);
        int defenseBonus = n.path("defenseBonus").asInt(0);
        int healthRestore = n.path("healthRestore").asInt(0);
        itemsById.put(
            id,
            Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .type(type)
                .consumableType(consumableType)
                .armorType(armorType)
                .attackBonus(attackBonus)
                .defenseBonus(defenseBonus)
                .healthRestore(healthRestore)
                .build());
      }
    }

    // Monsters
    Map<String, Monster> monstersById = new HashMap<>();
    JsonNode monsters = root.path("monsters");
    if (monsters.isArray()) {
      for (JsonNode n : monsters) {
        String id = reqText(n, "id");
        String name = reqText(n, "name");
        String description = optText(n, "description", "");
        int maxHealth = n.path("maxHealth").asInt(30);
        int baseAttack = n.path("baseAttack").asInt(5);
        int baseDefense = n.path("baseDefense").asInt(0);

        monstersById.put(id, new Monster(name, description, maxHealth, baseAttack, baseDefense));
      }
    }

    // Rooms
    Map<String, Room> roomsById = new HashMap<>();
    Map<String, Puzzle> puzzlesById = new HashMap<>();

    JsonNode rooms = root.path("rooms");
    if (rooms.isArray()) {
      for (JsonNode n : rooms) {
        String id = reqText(n, "id");
        String name = reqText(n, "name");
        String description = optText(n, "description", "");

        Map<String, String> exits = new LinkedHashMap<>();
        JsonNode ex = n.path("exits");
        if (ex.isObject()) {
          Iterator<String> it = ex.fieldNames();
          while (it.hasNext()) {
            String dir = it.next();
            String to = ex.path(dir).asText(null);
            if (to != null && !to.isBlank()) {
              exits.put(dir, to);
            }
          }
        }

        List<String> itemIds = new ArrayList<>();
        JsonNode itemIdsNode = n.path("itemIds");
        if (itemIdsNode.isArray()) {
          for (JsonNode e : itemIdsNode) {
            String v = e.asText(null);
            if (v != null && !v.isBlank()) {
              itemIds.add(v);
            }
          }
        }

        // Parse inline puzzles array in room
        String puzzleId = null;
        JsonNode puzzlesNode = n.path("puzzles");
        if (puzzlesNode.isArray() && puzzlesNode.size() > 0) {
          // Take first puzzle in the array (rooms have at most one puzzle)
          JsonNode puzzleNode = puzzlesNode.get(0);
          String pId = reqText(puzzleNode, "id");
          String pDescription = optText(puzzleNode, "description", "");
          String prompt = optText(puzzleNode, "prompt", pDescription);
          int maxAttempts = puzzleNode.path("maxAttempts").asInt(3);

          PuzzleType type =
              parseEnum(
                  puzzleNode,
                  new String[] {"type", "puzzleType"},
                  PuzzleType.RIDDLE,
                  PuzzleType.class);

          // Build solution map from answer field or solution object
          Map<String, Object> solution = new HashMap<>();
          if (puzzleNode.hasNonNull("answer")) {
            solution.put("answer", puzzleNode.get("answer").asText());
          } else if (puzzleNode.hasNonNull("solution")) {
            // convert the solution node to a Map<String, Object> to support complex solutions and
            // preserve data types
            solution =
                mapper.convertValue(
                    puzzleNode.path("solution"), new TypeReference<Map<String, Object>>() {});
          }

          // Use prompt as description if provided, otherwise use description
          String finalDescription = prompt.isEmpty() ? pDescription : prompt;

          puzzlesById.put(
              pId,
              new Puzzle(
                  pId, finalDescription, type, solution, Puzzle.PuzzlePhase.LOCKED, maxAttempts));
          puzzleId = pId;
        }

        // Also check for standalone puzzleId field (alternative format)
        if (puzzleId == null && n.hasNonNull("puzzleId")) {
          puzzleId = n.get("puzzleId").asText();
        }

        // Parse monster field
        String monsterId = null;
        if (n.hasNonNull("monsterId")) {
          monsterId = n.get("monsterId").asText();
        }

        roomsById.put(
            id,
            Room.builder()
                .id(id)
                .name(name)
                .description(description)
                .exits(exits)
                .itemIds(itemIds)
                .puzzleId(puzzleId)
                .monsterId(monsterId)
                .build());
      }
    }

    // Also parse top-level puzzles array if present (for backwards compatibility - old format was
    // to load all puzzles on top level of JSON, not inline in rooms)
    JsonNode topLevelPuzzles = root.path("puzzles");
    if (topLevelPuzzles.isArray()) {
      for (JsonNode n : topLevelPuzzles) {
        String id = reqText(n, "id");
        String description = optText(n, "description", "");
        int maxAttempts = n.path("maxAttempts").asInt(3);

        // lenient: accept type/puzzleType, ignore case/spaces/dashes, default RIDDLE
        PuzzleType type =
            parseEnum(n, new String[] {"type", "puzzleType"}, PuzzleType.RIDDLE, PuzzleType.class);

        // lenient: accept phase/puzzlePhase/status, default LOCKED
        Puzzle.PuzzlePhase phase =
            parseEnum(
                n,
                new String[] {"phase", "puzzlePhase", "status"},
                Puzzle.PuzzlePhase.LOCKED,
                Puzzle.PuzzlePhase.class);

        Map<String, Object> solution =
            mapper.convertValue(
                n.path("solution").isMissingNode() ? Map.<String, Object>of() : n.path("solution"),
                new TypeReference<Map<String, Object>>() {});

        puzzlesById.put(id, new Puzzle(id, description, type, solution, phase, maxAttempts));
      }
    }

    if (!roomsById.containsKey(startRoomId)) {
      throw new IllegalStateException("startRoomId '" + startRoomId + "' not found.");
    }

    return new World(version, roomsById, itemsById, puzzlesById, monstersById, startRoomId);
  }

  // ---------------------- minimal helpers ----------------------

  private JsonNode read(String p) {
    try (InputStream in = open(p)) {
      if (in == null) {
        throw new IOException("Not found: " + p + " (classpath or filesystem).");
      }
      return mapper.readTree(in);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read JSON '" + p + "'", e);
    } catch (RuntimeException e) {
      throw new IllegalStateException("Invalid world JSON '" + p + "': " + e.getMessage(), e);
    }
  }

  /** Try classpath (as-is, then with leading slash), then filesystem. */
  private InputStream open(String p) throws IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream in = (cl != null) ? cl.getResourceAsStream(stripSlash(p)) : null;
    if (in != null) {
      return in;
    }

    in = JsonWorldLoader.class.getResourceAsStream(ensureSlash(p));
    if (in != null) {
      return in;
    }

    Path file = Path.of(p);
    return Files.exists(file) ? Files.newInputStream(file) : null;
  }

  private static String reqText(JsonNode n, String field) {
    JsonNode v = n.get(field);
    if (v == null || !v.isTextual() || v.asText().isBlank()) {
      throw new IllegalArgumentException("Missing required field: " + field);
    }
    return v.asText();
  }

  private static String optText(JsonNode n, String field, String def) {
    JsonNode v = n.get(field);
    if (v == null || v.isNull()) {
      return def;
    }
    String s = v.asText();
    return (s == null || s.isBlank()) ? def : s;
  }

  private static String firstText(JsonNode n, String[] keys) {
    for (String k : keys) {
      if (n.hasNonNull(k)) {
        String v = n.get(k).asText();
        if (v != null && !v.isBlank()) {
          return v;
        }
      }
    }
    return null;
  }

  private static String stripSlash(String s) {
    return (s != null && s.startsWith("/")) ? s.substring(1) : s;
  }

  private static String ensureSlash(String s) {
    return (s != null && s.startsWith("/")) ? s : "/" + s;
  }

  /** Case-insensitive enum parse with aliases + defaults. */
  private static <E extends Enum<E>> E parseEnum(JsonNode n, String[] keys, E def, Class<E> cls) {
    String raw = firstText(n, keys);
    if (raw == null) {
      return def;
    }
    String norm = raw.trim().toUpperCase().replace(' ', '_').replace('-', '_');
    try {
      return Enum.valueOf(cls, norm);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Invalid " + cls.getSimpleName() + " '" + raw + "'");
    }
  }
}
