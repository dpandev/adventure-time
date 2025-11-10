package com.dpandev.client.runtime;

import com.dpandev.client.controller.FrontController;
import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;
import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.ExplorationService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.Verb;
import java.util.List;

public final class CliAppRunner {
  private final ConsoleView view;
  private final CommandParser parser;
  private final FrontController frontController;
  private final ExplorationService explorationService;
  private final GameContext ctx;

  public CliAppRunner(
      ConsoleView view,
      CommandParser parser,
      FrontController frontController,
      ExplorationService explorationService,
      GameContext ctx) {
    this.view = view;
    this.parser = parser;
    this.frontController = frontController;
    this.explorationService = explorationService;
    this.ctx = ctx;
  }

  public void run() {
    view.println("Welcome to Adventure Time");
    view.println("Type 'help' for commands, 'quit' to exit.");
    view.println("");

    // show initial room description
    CommandResult roomDesc = explorationService.describeCurrentRoom(ctx);
    if (roomDesc != null && !roomDesc.message().isBlank()) {
      view.println(roomDesc.message());
      view.println("");
    }

    while (true) {
      view.printf("> ");
      String line;
      try {
        line = view.readLine();
      } catch (Exception e) {
        view.println("Error reading input. Exiting.");
        return;
      }

      // if awaiting puzzle answer, treat input as puzzle solution and bypass normal command
      // parsing.
      // player cannot issue other commands until puzzle is resolved (or max attempts reached).
      if (ctx.isAwaitingPuzzleAnswer()) {
        CommandToken solveCmd = new CommandToken(Verb.SOLVE, line, List.of(), line);
        CommandResult result = frontController.handle(solveCmd, ctx);
        if (result != null && !result.message().isBlank()) {
          view.println(result.message());
        }
        continue;
      }

      CommandToken cmd = parser.parse(line);
      CommandResult result = frontController.handle(cmd, ctx);
      if (result != null && !result.message().isBlank()) {
        view.println(result.message());
      }

      // check if the command is for game exit/quit
      if (result != null && result.shouldExit()) {
        return;
      }
    }
  }
}
