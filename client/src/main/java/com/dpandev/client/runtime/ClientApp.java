package com.dpandev.client.runtime;

import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;
import com.dpandev.domain.command.SimpleCommandParser;
import com.dpandev.domain.model.Player;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.world.JsonWorldLoader;
import com.dpandev.domain.world.World;
import com.dpandev.domain.world.WorldLoader;

public class ClientApp {
  public static void main(String[] args) {
    ConsoleView view = new ConsoleView();
    CommandParser parser = new SimpleCommandParser();

    WorldLoader loader = new JsonWorldLoader("worldpacks/main.json");
    World world = loader.load();
    Player player = new Player("Player", world.getStartRoomId());
    GameContext ctx = new GameContext(world, player);

    // init services here

    // init controllers here

    // init front controller here and pass controllers as map with verb categories as keys

    CliAppRunner runner = new CliAppRunner(view, parser);
    runner.run();
  }
}
