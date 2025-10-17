package com.dpandev.client.runtime;

import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;

public class CliAppRunner {
  private final ConsoleView view;
  private final CommandParser parser;

  public CliAppRunner(ConsoleView view, CommandParser parser) {
    this.view = view;
    this.parser = parser;
  }

  public void run() {
    view.println("Adventure Time — CLI");

    try {
      while (true) {
        view.printf("> ");
        String line = view.readLine();
        if (line == null || line.equalsIgnoreCase("quit")) {
          break;
        }
        var command = parser.parse(line);
        view.println("Parsed command: " + command);
      }
    } catch (Exception e) {
      view.println("Error: " + e.getMessage());
    }
  }
}
