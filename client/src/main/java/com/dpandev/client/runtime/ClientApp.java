package com.dpandev.client.runtime;

import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;
import com.dpandev.domain.command.SimpleCommandParser;

public class ClientApp {
  public static void main(String[] args) {
    ConsoleView view = new ConsoleView();
    CommandParser parser = new SimpleCommandParser();

    // init world here and load it

    // init player here

    // init services here

    // init controllers here

    // init front controller here and pass controllers as map with verb categories as keys

    CliAppRunner runner = new CliAppRunner(view, parser);
    runner.run();
  }
}
