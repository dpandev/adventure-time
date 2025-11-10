package com.dpandev.client.controller;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.ExplorationService;
import com.dpandev.domain.service.MapService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;

/** Controller to handle movement-related commands such as LOOK, GO, and MAP. */
public final class MovementController implements CommandController {

  private final ExplorationService exploration;
  private final MapService mapService;

  public MovementController(ExplorationService exploration, MapService mapService) {
    this.exploration = exploration;
    this.mapService = mapService;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case LOOK -> exploration.look(ctx);
      case GO -> exploration.move(ctx, cmd.target());
      case MAP -> mapService.showMap(ctx);
      case STATS -> exploration.showStats(ctx);
      default -> CommandResult.fail("Unsupported verb for movement: " + cmd.verb());
    };
  }
}
