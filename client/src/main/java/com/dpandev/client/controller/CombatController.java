package com.dpandev.client.controller;

import com.dpandev.domain.service.CombatService;
import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;

/** Controller to handle combat-related commands. */
public final class CombatController implements CommandController {

  private final CombatService combatService;

  public CombatController(CombatService combatService) {
    this.combatService = combatService;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case ATTACK -> {
        // ff in combat attack the current monster
        if (ctx.isInCombat()) {
          yield combatService.playerAttack(ctx);
        }
        // initiate combat with specified monster
        yield combatService.initiateCombat(ctx, cmd.target());
      }
      case IGNORE -> combatService.ignoreMonster(ctx, cmd.target());
      default -> CommandResult.fail("Unsupported combat command: " + cmd.verb());
    };
  }
}
