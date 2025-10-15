package com.dpandev.client.runtime;

import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;
import com.dpandev.domain.command.SimpleCommandParser;

/**
 * A simple command-line client application for the Adventure Time game. Reads user input from the
 * console and echoes it back until "quit" is entered.
 */
public class ClientApp {
  public static void main(String[] args) throws Exception {
    ConsoleView view = new ConsoleView();
    CommandParser parser = new SimpleCommandParser();

    CliAppRunner runner = new CliAppRunner(view, parser);
    runner.run();
  }
}
