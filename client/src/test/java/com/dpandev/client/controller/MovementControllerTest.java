package com.dpandev.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.ExplorationService;
import com.dpandev.domain.service.MapService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.Verb;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for MovementController. */
@ExtendWith(MockitoExtension.class)
class MovementControllerTest {

  @Mock private ExplorationService explorationService;
  @Mock private MapService mapService;
  @Mock private GameContext ctx;

  private MovementController controller;

  @BeforeEach
  void setUp() {
    controller = new MovementController(explorationService, mapService);
  }

  @Nested
  @DisplayName("LOOK Command Tests")
  class LookCommandTests {

    @Test
    @DisplayName("should delegate LOOK to exploration service")
    void shouldDelegateLookToExplorationService() {
      CommandToken cmd = new CommandToken(Verb.LOOK, null, List.of(), "look");
      CommandResult expected = CommandResult.success("You see a room");
      when(explorationService.look(ctx)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(explorationService).look(ctx);
    }
  }

  @Nested
  @DisplayName("GO Command Tests")
  class GoCommandTests {

    @Test
    @DisplayName("should delegate GO to exploration service with target")
    void shouldDelegateGoToExplorationService() {
      CommandToken cmd = new CommandToken(Verb.GO, "north", List.of("north"), "go north");
      CommandResult expected = CommandResult.success("You move north");
      when(explorationService.move(ctx, "north")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(explorationService).move(ctx, "north");
    }

    @Test
    @DisplayName("should handle GO without target")
    void shouldHandleGoWithoutTarget() {
      CommandToken cmd = new CommandToken(Verb.GO, null, List.of(), "go");
      CommandResult expected = CommandResult.fail("Where?");
      when(explorationService.move(ctx, null)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(explorationService).move(ctx, null);
    }
  }

  @Nested
  @DisplayName("MAP Command Tests")
  class MapCommandTests {

    @Test
    @DisplayName("should delegate MAP to map service")
    void shouldDelegateMapToMapService() {
      CommandToken cmd = new CommandToken(Verb.MAP, null, List.of(), "map");
      CommandResult expected = CommandResult.success("Map display");
      when(mapService.showMap(ctx)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(mapService).showMap(ctx);
    }
  }

  @Nested
  @DisplayName("STATS Command Tests")
  class StatsCommandTests {

    @Test
    @DisplayName("should delegate STATS to exploration service")
    void shouldDelegateStatsToExplorationService() {
      CommandToken cmd = new CommandToken(Verb.STATS, null, List.of(), "stats");
      CommandResult expected = CommandResult.success("HP: 100/100");
      when(explorationService.showStats(ctx)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(explorationService).showStats(ctx);
    }
  }

  @Nested
  @DisplayName("Unsupported Command Tests")
  class UnsupportedCommandTests {

    @Test
    @DisplayName("should return fail for unsupported verb")
    void shouldReturnFailForUnsupportedVerb() {
      CommandToken cmd = new CommandToken(Verb.ATTACK, "goblin", List.of(), "attack goblin");

      CommandResult result = controller.handle(cmd, ctx);

      assertFalse(result.success());
      assertTrue(result.message().contains("Unsupported verb"));
      assertTrue(result.message().contains("ATTACK"));
    }
  }
}
