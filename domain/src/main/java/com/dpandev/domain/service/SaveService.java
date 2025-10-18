package com.dpandev.domain.service;

import com.dpandev.domain.spi.SaveRepository;
import com.dpandev.domain.utils.GameContext;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Service responsible for saving and loading game data. */
public final class SaveService {
  private final SaveRepository repo;

  /**
   * Constructs a SaveService with the given SaveRepository.
   *
   * @param repo the repository for saving and loading game data
   */
  public SaveService(SaveRepository repo) {
    this.repo = Objects.requireNonNull(repo); // need to ensure repo is not null
  }

  /**
   * Save the current game state for the player in the given context.
   *
   * @param ctx the game context containing world and player information
   */
  public void saveData(GameContext ctx) {
    var world = ctx.world();
    var player = ctx.player();

    SaveData data =
        new SaveData(
            world.getVersion(),
            player.getId(),
            player.getName(),
            player.getRoomId(),
            List.copyOf(player.getInventoryItemIds()),
            Instant.now());

    repo.upsert(data); // save or update the save data if exists
  }

  /**
   * Load the save data for a given player ID.
   *
   * @param playerId the UUID of the player
   * @return an Optional containing the SaveData if found, otherwise empty
   */
  public Optional<SaveData> load(UUID playerId) {
    return repo.findByPlayerId(playerId);
  }
}
