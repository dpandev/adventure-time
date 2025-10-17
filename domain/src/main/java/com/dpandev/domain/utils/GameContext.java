package com.dpandev.domain.utils;

import com.dpandev.domain.model.Player;
import com.dpandev.domain.world.World;
import java.util.Objects;

public final class GameContext {
  public final World world;
  public final Player player;

  public GameContext(World world, Player player) {
    this.world = Objects.requireNonNull(world, "world must not be null");
    this.player = Objects.requireNonNull(player, "player must not be null");
  }

  public World world() {
    return world;
  }

  public Player player() {
    return player;
  }
}
