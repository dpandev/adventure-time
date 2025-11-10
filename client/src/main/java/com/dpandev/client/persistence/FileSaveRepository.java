package com.dpandev.client.persistence;

import com.dpandev.domain.service.SaveData;
import com.dpandev.domain.spi.SaveRepository;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * A file-based implementation of the SaveRepository interface for managing player save data. Save
 * data is stored in individual properties files within a specified base directory.
 */
public final class FileSaveRepository implements SaveRepository {
  private final Path baseDirectory;

  /**
   * Constructs a FileSaveRepository with the specified base directory for save files.
   *
   * @param baseDirectory the base directory where save files will be stored
   */
  public FileSaveRepository(Path baseDirectory) {
    this.baseDirectory = baseDirectory;
    try {
      Files.createDirectories(baseDirectory);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create base directory for saves: " + baseDirectory, e);
    }
  }

  /**
   * Constructs the file path for a player's save data based on their UUID.
   *
   * @param playerId the player's UUID
   * @return the Path to the player's save file
   */
  private Path fileFor(UUID playerId) {
    return baseDirectory.resolve(playerId.toString() + ".properties");
  }

  /**
   * Finds the save data for a player by their ID.
   *
   * @param id the player's UUID
   * @return an Optional containing the SaveData if found, or empty if not found
   */
  @Override
  public Optional<SaveData> findByPlayerId(UUID id) {
    Path file = fileFor(id);
    if (Files.notExists(file)) {
      return Optional.empty();
    }
    Properties p = new Properties();
    try (Reader r = Files.newBufferedReader(file)) {
      p.load(r);
    } catch (Exception e) {
      //      throw new RuntimeException("Failed to read save file for player ID: " + id, e);
      // TODO keep the app running and log the error instead of throwing
      return Optional.empty();
    }

    try {
      String worldVersion = p.getProperty("worldVersion");
      UUID uuid = UUID.fromString(p.getProperty("playerId"));
      String playerName = p.getProperty("playerName");
      String roomId = p.getProperty("roomId");
      List<String> itemIds = parseItemIds(p.getProperty("itemIds"));
      Map<String, String> equippedItems = parseEquippedItems(p.getProperty("equippedItems"));

      // Parse player stats with defaults for backward compatibility
      int score = Integer.parseInt(p.getProperty("score", "0"));
      int currentHealth = Integer.parseInt(p.getProperty("currentHealth", "100"));
      int maxHealth = Integer.parseInt(p.getProperty("maxHealth", "100"));
      int baseAttack = Integer.parseInt(p.getProperty("baseAttack", "10"));
      int baseDefense = Integer.parseInt(p.getProperty("baseDefense", "0"));

      List<String> puzzlesSolved = parseItemIds(p.getProperty("puzzlesSolved"));
      List<String> roomsVisited = parseItemIds(p.getProperty("roomsVisited"));

      Instant savedAt = Instant.parse(p.getProperty("savedAt"));

      return Optional.of(
          new SaveData(
              worldVersion,
              uuid,
              playerName,
              roomId,
              itemIds,
              equippedItems,
              score,
              currentHealth,
              maxHealth,
              baseAttack,
              baseDefense,
              puzzlesSolved,
              roomsVisited,
              savedAt));
    } catch (Exception e) {
      //      throw new RuntimeException("Failed to parse save data for player ID: " + id, e);
      return Optional.empty();
    }
  }

  /**
   * Inserts or updates the save data for a player.
   *
   * @param data the save data to upsert
   */
  @Override
  public void upsert(SaveData data) {
    Properties p = new Properties();
    p.setProperty("worldVersion", data.worldVersion());
    p.setProperty("playerId", data.playerId().toString());
    p.setProperty("playerName", data.playerName());
    p.setProperty("roomId", data.roomId());
    p.setProperty("itemIds", String.join(",", data.itemIds()));

    // Save equipped items as "SLOT:itemId" pairs
    StringBuilder equippedBuilder = new StringBuilder();
    data.equippedItems()
        .forEach(
            (slot, itemId) -> {
              if (equippedBuilder.length() > 0) {
                equippedBuilder.append(",");
              }
              equippedBuilder.append(slot).append(":").append(itemId);
            });
    p.setProperty("equippedItems", equippedBuilder.toString());

    p.setProperty("score", String.valueOf(data.score()));
    p.setProperty("currentHealth", String.valueOf(data.currentHealth()));
    p.setProperty("maxHealth", String.valueOf(data.maxHealth()));
    p.setProperty("baseAttack", String.valueOf(data.baseAttack()));
    p.setProperty("baseDefense", String.valueOf(data.baseDefense()));
    p.setProperty("puzzlesSolved", String.join(",", data.puzzlesSolved()));
    p.setProperty("roomsVisited", String.join(",", data.roomsVisited()));
    p.setProperty("savedAt", data.savedAt().toString());

    Path file = fileFor(data.playerId());
    try (var writer = Files.newBufferedWriter(file)) {
      p.store(writer, "AdventureTime_save_for_" + data.playerId());
    } catch (IOException e) {
      throw new UncheckedIOException(
          "Failed to write save file for player ID: " + data.playerId(), e);
      // unchecked so no need to force handling it
    }
  }

  /**
   * Parses a comma-separated string of item IDs into a list.
   *
   * @param itemIdsStr the comma-separated string of item IDs
   * @return a list of item IDs
   */
  private static List<String> parseItemIds(String itemIdsStr) {
    if (itemIdsStr == null || itemIdsStr.isBlank()) {
      return List.of();
    }
    String[] parts = itemIdsStr.split(",");
    return List.of(parts);
  }

  /**
   * Parses equipped items from "SLOT:itemId,SLOT:itemId" format.
   *
   * @param equippedStr the equipped items string
   * @return a map of slot name to item ID
   */
  private static Map<String, String> parseEquippedItems(String equippedStr) {
    if (equippedStr == null || equippedStr.isBlank()) {
      return Map.of();
    }

    Map<String, String> result = new java.util.HashMap<>();
    String[] pairs = equippedStr.split(",");
    for (String pair : pairs) {
      String[] parts = pair.split(":");
      if (parts.length == 2) {
        result.put(parts[0], parts[1]);
      }
    }
    return result;
  }
}
