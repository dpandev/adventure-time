package com.dpandev.domain.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Represents the saved data of a player's game state. */
public record SaveData(
    String worldVersion,
    UUID playerId,
    String playerName,
    String roomId,
    List<String> itemIds,
    Instant savedAt) {}
