package com.dpandev.client.runtime;

import com.dpandev.client.controller.CombatController;
import com.dpandev.client.controller.CommandController;
import com.dpandev.client.controller.FrontController;
import com.dpandev.client.controller.InteractionController;
import com.dpandev.client.controller.InventoryController;
import com.dpandev.client.controller.MovementController;
import com.dpandev.client.controller.SystemController;
import com.dpandev.client.persistence.FileSaveRepository;
import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;
import com.dpandev.domain.command.SimpleCommandParser;
import com.dpandev.domain.model.Player;
import com.dpandev.domain.service.CombatService;
import com.dpandev.domain.service.DefaultCombatService;
import com.dpandev.domain.service.DefaultExplorationService;
import com.dpandev.domain.service.DefaultInteractionService;
import com.dpandev.domain.service.DefaultInventoryService;
import com.dpandev.domain.service.DefaultMapService;
import com.dpandev.domain.service.ExplorationService;
import com.dpandev.domain.service.InteractionService;
import com.dpandev.domain.service.InventoryService;
import com.dpandev.domain.service.MapService;
import com.dpandev.domain.service.SaveService;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.VerbCategory;
import com.dpandev.domain.world.JsonWorldLoader;
import com.dpandev.domain.world.World;
import com.dpandev.domain.world.WorldLoader;
import java.nio.file.Path;
import java.util.Map;

/** Main client application entry point. */
public final class ClientApp {
  public static void main(String[] args) {
    ConsoleView view = new ConsoleView();
    CommandParser parser = new SimpleCommandParser();

    // Parse command-line arguments for worldpack selection
    String worldpack = parseWorldpackArg(args);

    WorldLoader loader = new JsonWorldLoader(worldpack);
    World world = loader.load();
    Player player = new Player("Player", world.getStartRoomId());
    GameContext ctx = new GameContext(world, player);

    // init services here
    InteractionService interactionService = new DefaultInteractionService();
    ExplorationService explorationService = new DefaultExplorationService(interactionService);
    InventoryService inventoryService = new DefaultInventoryService();
    CombatService combatService = new DefaultCombatService();
    MapService mapService = new DefaultMapService();

    // Use system property for saves directory, or default to "saves" in current working directory
    // This allows configuration via -Dsaves.dir=/path/to/saves if needed
    String savesPath = System.getProperty("saves.dir", "saves");
    var saveDirectory = Path.of(savesPath).toAbsolutePath();
    SaveService saveService = new SaveService(new FileSaveRepository(saveDirectory));

    // init controllers here
    CommandController movementController = new MovementController(explorationService, mapService);
    CommandController inventoryController = new InventoryController(inventoryService);
    CommandController interactionController = new InteractionController(interactionService);
    CommandController combatController = new CombatController(combatService);
    CommandController systemController = new SystemController(saveService, view, loader);

    // init front controller here and pass controllers as map with verb categories as keys
    FrontController frontController =
        new FrontController(
            Map.of(
                VerbCategory.MOVEMENT, movementController,
                VerbCategory.INVENTORY, inventoryController,
                VerbCategory.INTERACTION, interactionController,
                VerbCategory.COMBAT, combatController,
                VerbCategory.SYSTEM, systemController),
            systemController // fallback to system controller
            );

    CliAppRunner runner = new CliAppRunner(view, parser, frontController, explorationService, ctx);
    runner.run();
  }

  /**
   * Parses command-line arguments to determine which worldpack to load.
   *
   * <p>Supports the following formats:
   *
   * <ul>
   *   <li>--world=jurassic → loads worldpacks/jurassic.json
   *   <li>--world=example → loads worldpacks/example.json
   *   <li>--worldpack=path/to/pack.json → loads exact path
   *   <li>No args → defaults to worldpacks/example.json
   * </ul>
   *
   * @param args command-line arguments
   * @return path to worldpack JSON file
   */
  private static String parseWorldpackArg(String[] args) {
    String defaultPack = "worldpacks/example.json";

    if (args == null || args.length == 0) {
      return defaultPack;
    }

    for (String arg : args) {
      // Support --world=NAME format
      if (arg.startsWith("--world=")) {
        String worldName = arg.substring("--world=".length());
        // If it already has .json extension or contains path separator, use as-is
        if (worldName.endsWith(".json") || worldName.contains("/")) {
          return worldName;
        }
        // Otherwise, assume it's a worldpack name
        return "worldpacks/" + worldName + ".json";
      }

      // Support --worldpack=PATH format for full paths
      if (arg.startsWith("--worldpack=")) {
        return arg.substring("--worldpack=".length());
      }

      // Support -w NAME shorthand
      if (arg.equals("-w") && args.length > 1) {
        // Find the next argument
        for (int i = 0; i < args.length - 1; i++) {
          if (args[i].equals("-w")) {
            String worldName = args[i + 1];
            if (worldName.endsWith(".json") || worldName.contains("/")) {
              return worldName;
            }
            return "worldpacks/" + worldName + ".json";
          }
        }
      }
    }

    return defaultPack;
  }
}
