package com.dpandev.client.controller;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.ExplorationService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;

/** Controller to handle movement-related commands such as LOOK and GO. */
public final class MovementController implements CommandController {

  private final ExplorationService exploration;

  public MovementController(ExplorationService exploration) {
    this.exploration = exploration;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case LOOK -> exploration.look(ctx);
      case GO -> exploration.move(ctx, cmd.target());
      default -> CommandResult.fail("Unsupported verb for movement: " + cmd.verb());
    };
  }
}
