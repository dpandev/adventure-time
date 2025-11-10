package com.dpandev.client.controller;

import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.SaveService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;

/** Controller to handle system commands like help, save, load, and quit. */
public class SystemController implements CommandController {
  private final SaveService save;
  private final ConsoleView view;

  /** Constructs a SystemController with the given SaveService and ConsoleView. */
  public SystemController(SaveService save, ConsoleView view) {
    this.save = save;
    this.view = view;
  }

  /** Handles system commands and returns the result. */
  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case HELP ->
          CommandResult.success(
              "Available commands: \nlook, go <dir>, inventory, pickup <item>, drop <item>, use <item>, save, quit");
      case SAVE -> {
        save.saveData(ctx);
        yield CommandResult.success("Game saved successfully.");
      }
      case LOAD -> {
        var playerId = ctx.player().getId();
        var loadedCtx = save.load(playerId); // no multiple saves per player, just one
        if (loadedCtx.isEmpty()) {
          yield CommandResult.fail("No saved game found for player ID: " + playerId);
        }
        yield save.applySave(ctx, loadedCtx.get()); // this also returns CommandResult
      }
      case QUIT -> {
        save.saveData(ctx);
        yield CommandResult.exit("Game saved. Goodbye!");
      }
      default -> CommandResult.fail("Unknown command: " + cmd.verb());
    };
  }
}
