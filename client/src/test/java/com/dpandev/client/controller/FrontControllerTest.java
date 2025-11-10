package com.dpandev.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.Verb;
import com.dpandev.domain.utils.VerbCategory;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for FrontController. */
@ExtendWith(MockitoExtension.class)
class FrontControllerTest {

  @Mock private CommandController movementController;
  @Mock private CommandController inventoryController;
  @Mock private CommandController combatController;
  @Mock private CommandController interactionController;
  @Mock private CommandController systemController;
  @Mock private GameContext ctx;

  private FrontController frontController;
  private Map<VerbCategory, CommandController> controllersByCategory;

  @BeforeEach
  void setUp() {
    controllersByCategory = new EnumMap<>(VerbCategory.class);
    controllersByCategory.put(VerbCategory.MOVEMENT, movementController);
    controllersByCategory.put(VerbCategory.INVENTORY, inventoryController);
    controllersByCategory.put(VerbCategory.COMBAT, combatController);
    controllersByCategory.put(VerbCategory.INTERACTION, interactionController);

    frontController = new FrontController(controllersByCategory, systemController);
  }

  @Nested
  @DisplayName("Movement Command Routing Tests")
  class MovementCommandRoutingTests {

    @Test
    @DisplayName("should route LOOK to movement controller")
    void shouldRouteLookToMovementController() {
      CommandToken cmd = new CommandToken(Verb.LOOK, null, List.of(), "look");
      CommandResult expected = CommandResult.success("You see a room");
      when(movementController.supports(Verb.LOOK)).thenReturn(true);
      when(movementController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(movementController).supports(Verb.LOOK);
      verify(movementController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route GO to movement controller")
    void shouldRouteGoToMovementController() {
      CommandToken cmd = new CommandToken(Verb.GO, "north", List.of("north"), "go north");
      CommandResult expected = CommandResult.success("You move north");
      when(movementController.supports(Verb.GO)).thenReturn(true);
      when(movementController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(movementController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route MAP to movement controller")
    void shouldRouteMapToMovementController() {
      CommandToken cmd = new CommandToken(Verb.MAP, null, List.of(), "map");
      CommandResult expected = CommandResult.success("Map displayed");
      when(movementController.supports(Verb.MAP)).thenReturn(true);
      when(movementController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(movementController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route STATS to movement controller")
    void shouldRouteStatsToMovementController() {
      CommandToken cmd = new CommandToken(Verb.STATS, null, List.of(), "stats");
      CommandResult expected = CommandResult.success("HP: 100/100");
      when(movementController.supports(Verb.STATS)).thenReturn(true);
      when(movementController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(movementController).handle(cmd, ctx);
    }
  }

  @Nested
  @DisplayName("Inventory Command Routing Tests")
  class InventoryCommandRoutingTests {

    @Test
    @DisplayName("should route PICKUP to inventory controller")
    void shouldRoutePickupToInventoryController() {
      CommandToken cmd = new CommandToken(Verb.PICKUP, "sword", List.of("sword"), "pickup sword");
      CommandResult expected = CommandResult.success("Picked up sword");
      when(inventoryController.supports(Verb.PICKUP)).thenReturn(true);
      when(inventoryController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route INVENTORY to inventory controller")
    void shouldRouteInventoryToInventoryController() {
      CommandToken cmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");
      CommandResult expected = CommandResult.success("You are carrying: sword");
      when(inventoryController.supports(Verb.INVENTORY)).thenReturn(true);
      when(inventoryController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route EQUIP to inventory controller")
    void shouldRouteEquipToInventoryController() {
      CommandToken cmd = new CommandToken(Verb.EQUIP, "helmet", List.of("helmet"), "equip helmet");
      CommandResult expected = CommandResult.success("Equipped helmet");
      when(inventoryController.supports(Verb.EQUIP)).thenReturn(true);
      when(inventoryController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryController).handle(cmd, ctx);
    }
  }

  @Nested
  @DisplayName("Combat Command Routing Tests")
  class CombatCommandRoutingTests {

    @Test
    @DisplayName("should route ATTACK to combat controller")
    void shouldRouteAttackToCombatController() {
      CommandToken cmd =
          new CommandToken(Verb.ATTACK, "goblin", List.of("goblin"), "attack goblin");
      CommandResult expected = CommandResult.success("You attack the goblin");
      when(combatController.supports(Verb.ATTACK)).thenReturn(true);
      when(combatController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(combatController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route IGNORE to combat controller")
    void shouldRouteIgnoreToCombatController() {
      CommandToken cmd =
          new CommandToken(Verb.IGNORE, "goblin", List.of("goblin"), "ignore goblin");
      CommandResult expected = CommandResult.success("You ignore the goblin");
      when(combatController.supports(Verb.IGNORE)).thenReturn(true);
      when(combatController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(combatController).handle(cmd, ctx);
    }
  }

  @Nested
  @DisplayName("Interaction Command Routing Tests")
  class InteractionCommandRoutingTests {

    @Test
    @DisplayName("should route SOLVE to interaction controller")
    void shouldRouteSolveToInteractionController() {
      CommandToken cmd = new CommandToken(Verb.SOLVE, "echo", List.of("echo"), "solve echo");
      CommandResult expected = CommandResult.success("Correct!");
      when(interactionController.supports(Verb.SOLVE)).thenReturn(true);
      when(interactionController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(interactionController).handle(cmd, ctx);
    }
  }

  @Nested
  @DisplayName("System Command Routing Tests")
  class SystemCommandRoutingTests {

    @Test
    @DisplayName("should route HELP to system controller")
    void shouldRouteHelpToSystemController() {
      CommandToken cmd = new CommandToken(Verb.HELP, null, List.of(), "help");
      CommandResult expected = CommandResult.success("Help text");
      when(systemController.supports(Verb.HELP)).thenReturn(true);
      when(systemController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(systemController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route SAVE to system controller")
    void shouldRouteSaveToSystemController() {
      CommandToken cmd = new CommandToken(Verb.SAVE, null, List.of(), "save");
      CommandResult expected = CommandResult.success("Game saved");
      when(systemController.supports(Verb.SAVE)).thenReturn(true);
      when(systemController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(systemController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should route QUIT to system controller")
    void shouldRouteQuitToSystemController() {
      CommandToken cmd = new CommandToken(Verb.QUIT, null, List.of(), "quit");
      CommandResult expected = CommandResult.exit("Goodbye");
      when(systemController.supports(Verb.QUIT)).thenReturn(true);
      when(systemController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(systemController).handle(cmd, ctx);
    }
  }

  @Nested
  @DisplayName("Fallback and Error Handling Tests")
  class FallbackAndErrorHandlingTests {

    @Test
    @DisplayName(
        "should fallback to system controller when category controller does not support verb")
    void shouldFallbackToSystemControllerWhenCategoryControllerDoesNotSupport() {
      CommandToken cmd = new CommandToken(Verb.LOOK, null, List.of(), "look");
      CommandResult expected = CommandResult.success("System handled");
      when(movementController.supports(Verb.LOOK)).thenReturn(false);
      when(systemController.supports(Verb.LOOK)).thenReturn(true);
      when(systemController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = frontController.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(systemController).handle(cmd, ctx);
    }

    @Test
    @DisplayName("should return error when no controller supports the verb")
    void shouldReturnErrorWhenNoControllerSupportsVerb() {
      CommandToken cmd = new CommandToken(Verb.UNKNOWN, null, List.of(), "gibberish");
      when(systemController.supports(Verb.UNKNOWN)).thenReturn(false);

      CommandResult result = frontController.handle(cmd, ctx);

      assertFalse(result.success());
      assertTrue(result.message().contains("No controller found"));
      assertTrue(result.message().contains("UNKNOWN"));
    }

    @Test
    @DisplayName("should handle null command token")
    void shouldHandleNullCommandToken() {
      when(systemController.supports(Verb.UNKNOWN)).thenReturn(false);

      CommandResult result = frontController.handle(null, ctx);

      assertFalse(result.success());
      assertTrue(result.message().contains("No controller found"));
    }
  }

  @Nested
  @DisplayName("Controller Map Immutability Tests")
  class ControllerMapImmutabilityTests {

    @Test
    @DisplayName("should use defensive copy of controller map")
    void shouldUseDefensiveCopyOfControllerMap() {
      Map<VerbCategory, CommandController> originalMap = new EnumMap<>(VerbCategory.class);
      originalMap.put(VerbCategory.MOVEMENT, movementController);

      FrontController controller = new FrontController(originalMap, systemController);

      // Modify original map
      originalMap.clear();

      // FrontController should still work because it made a defensive copy
      CommandToken cmd = new CommandToken(Verb.LOOK, null, List.of(), "look");
      CommandResult expected = CommandResult.success("Still works");
      when(movementController.supports(Verb.LOOK)).thenReturn(true);
      when(movementController.handle(cmd, ctx)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
    }
  }
}
