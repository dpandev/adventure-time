package com.dpandev.client.controller;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.InteractionService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;

/** Controller to handle interaction-related commands such as SOLVE. */
public class InteractionController implements CommandController {

  private final InteractionService interaction;

  /**
   * Constructs an InteractionController with the given InteractionService.
   *
   * @param interaction the interaction service
   */
  public InteractionController(InteractionService interaction) {
    this.interaction = interaction;
  }

  /**
   * Handles interaction commands based on the verb in the CommandToken.
   *
   * @param cmd the command token containing the verb and target
   * @param ctx the current game context
   * @return the result of the command execution
   */
  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case SOLVE -> interaction.solve(ctx, cmd.target());
      // more interaction commands will be added here
      default -> CommandResult.fail("Unsupported verb for interaction: " + cmd.verb());
    };
  }
}
