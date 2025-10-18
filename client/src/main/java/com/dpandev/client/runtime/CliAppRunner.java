package com.dpandev.client.runtime;

import com.dpandev.client.controller.FrontController;
import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;
import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.SaveService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.Verb;

public final class CliAppRunner {
  private final ConsoleView view;
  private final CommandParser parser;
  private final FrontController frontController;
  private final SaveService saveService;
  private final GameContext ctx;

  public CliAppRunner(
      ConsoleView view,
      CommandParser parser,
      FrontController frontController,
      SaveService saveService,
      GameContext ctx) {
    this.view = view;
    this.parser = parser;
    this.frontController = frontController;
    this.saveService = saveService;
    this.ctx = ctx;
  }

  public void run() {
    view.println("Welcome to Adventure Time");
    view.println("Type 'help' for commands, 'quit' to exit.");

    while (true) {
      view.printf("> ");
      String line;
      try {
        line = view.readLine();
      } catch (Exception e) {
        saveAndExit("Error reading input. Exiting.");
        return;
      }

      CommandToken cmd = parser.parse(line);
      CommandResult result = frontController.handle(cmd, ctx);
      if (result != null && !result.message().isBlank()) {
        view.println(result.message());
      }

      if (cmd.verb() == Verb.QUIT) {
        saveAndExit("Game saved.");
        return;
      }
    }
  }

  private void saveAndExit(String message) {
    try {
      saveService.saveData(ctx);
    } catch (Exception e) {
      view.println(e.getMessage());
    }
    view.println(message);
  }
}
