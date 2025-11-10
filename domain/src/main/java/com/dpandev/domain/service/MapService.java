package com.dpandev.domain.service;

import com.dpandev.domain.utils.GameContext;

/** Service interface for map-related actions in the game. */
public interface MapService {
  /**
   * Display a map showing the current room and adjacent rooms with their connections.
   *
   * @param ctx the game context
   * @return the result of the map command
   */
  CommandResult showMap(GameContext ctx);
}
