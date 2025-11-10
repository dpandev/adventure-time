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
import com.dpandev.domain.service.ExplorationService;
import com.dpandev.domain.service.InteractionService;
import com.dpandev.domain.service.InventoryService;
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

    WorldLoader loader = new JsonWorldLoader("worldpacks/main.json");
    World world = loader.load();
    Player player = new Player("Player", world.getStartRoomId());
    GameContext ctx = new GameContext(world, player);

    // init services here
    InteractionService interactionService = new DefaultInteractionService();
    ExplorationService explorationService = new DefaultExplorationService(interactionService);
    InventoryService inventoryService = new DefaultInventoryService();
    CombatService combatService = new DefaultCombatService();
    var saveDirectory = Path.of("saves");
    SaveService saveService = new SaveService(new FileSaveRepository(saveDirectory));

    // init controllers here
    CommandController movementController = new MovementController(explorationService);
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
}
