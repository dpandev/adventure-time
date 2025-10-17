package com.dpandev.client.controller;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.Verb;
import com.dpandev.domain.utils.VerbCategory;
import java.util.EnumMap;
import java.util.Map;

public final class FrontController {
  private final Map<VerbCategory, CommandController> controllersByCategory;
  private final CommandController systemController;

  public FrontController(
      Map<VerbCategory, CommandController> controllersByCategory,
      CommandController systemController) {
    // make defensive copy of map for immutability
    // uses EnumMap for efficiency with enum keys - VerbCategory is the enum key type
    this.controllersByCategory = new EnumMap<>(VerbCategory.class);
    this.controllersByCategory.putAll(controllersByCategory);
    this.systemController = systemController;
  }

  CommandResult handle(CommandToken cmd, GameContext ctx) {
    final Verb verb = (cmd == null) ? Verb.UNKNOWN : cmd.verb();
    // route to appropriate controller based on verb category
    VerbCategory vc = VerbCategory.of(verb);
    CommandController controller = controllersByCategory.get(vc);

    if (controller != null && controller.supports(verb)) {
      return controller.handle(cmd, ctx);
    } else if (systemController != null && systemController.supports(verb)) {
      return systemController.handle(cmd, ctx);
    } else {
      return CommandResult.fail("No controller found for verb: " + verb);
    }
  }
}
