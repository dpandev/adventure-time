package com.dpandev.domain.utils;

import com.dpandev.domain.model.Player;
import com.dpandev.domain.world.World;

public record GameContext(World world, Player player) {

  public GameContext {
    if (world == null) {
      throw new IllegalArgumentException("World cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
  }
}
