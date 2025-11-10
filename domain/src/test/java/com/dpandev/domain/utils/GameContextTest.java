package com.dpandev.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dpandev.domain.model.Player;
import com.dpandev.domain.world.World;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for the GameContext class. */
class GameContextTest {

  private World world;
  private Player player;
  private GameContext context;

  @BeforeEach
  void setUp() {
    world = new World("1.0", Map.of(), Map.of(), Map.of(), Map.of(), "room1");
    player = new Player("TestPlayer", "room1");
    context = new GameContext(world, player);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("should create context with world and player")
    void shouldCreateContextWithWorldAndPlayer() {
      assertNotNull(context);
      assertEquals(world, context.world());
      assertEquals(player, context.player());
    }

    @Test
    @DisplayName("should initialize with default state")
    void shouldInitializeWithDefaultState() {
      assertFalse(context.isAwaitingPuzzleAnswer());
      assertFalse(context.isInCombat());
      assertNull(context.getCombatMonsterId());
    }

    @Test
    @DisplayName("should throw NullPointerException if world is null")
    void shouldThrowNullPointerExceptionIfWorldIsNull() {
      assertThrows(NullPointerException.class, () -> new GameContext(null, player));
    }

    @Test
    @DisplayName("should throw NullPointerException if player is null")
    void shouldThrowNullPointerExceptionIfPlayerIsNull() {
      assertThrows(NullPointerException.class, () -> new GameContext(world, null));
    }
  }

  @Nested
  @DisplayName("Puzzle Answer State Tests")
  class PuzzleAnswerStateTests {

    @Test
    @DisplayName("should set and get awaiting puzzle answer state")
    void shouldSetAndGetAwaitingPuzzleAnswerState() {
      context.setAwaitingPuzzleAnswer(true);

      assertTrue(context.isAwaitingPuzzleAnswer());
    }

    @Test
    @DisplayName("should toggle awaiting puzzle answer state")
    void shouldToggleAwaitingPuzzleAnswerState() {
      context.setAwaitingPuzzleAnswer(true);
      assertTrue(context.isAwaitingPuzzleAnswer());

      context.setAwaitingPuzzleAnswer(false);
      assertFalse(context.isAwaitingPuzzleAnswer());
    }
  }

  @Nested
  @DisplayName("Combat State Tests")
  class CombatStateTests {

    @Test
    @DisplayName("should set and get combat state")
    void shouldSetAndGetCombatState() {
      context.setInCombat(true);

      assertTrue(context.isInCombat());
    }

    @Test
    @DisplayName("should set and get combat monster ID")
    void shouldSetAndGetCombatMonsterId() {
      context.setCombatMonsterId("goblin");

      assertEquals("goblin", context.getCombatMonsterId());
    }

    @Test
    @DisplayName("should start combat with monster")
    void shouldStartCombatWithMonster() {
      context.startCombat("troll");

      assertTrue(context.isInCombat());
      assertEquals("troll", context.getCombatMonsterId());
    }

    @Test
    @DisplayName("should end combat and clear state")
    void shouldEndCombatAndClearState() {
      context.startCombat("goblin");
      context.endCombat();

      assertFalse(context.isInCombat());
      assertNull(context.getCombatMonsterId());
    }

    @Test
    @DisplayName("should replace combat monster when starting new combat")
    void shouldReplaceCombatMonsterWhenStartingNewCombat() {
      context.startCombat("goblin");
      assertEquals("goblin", context.getCombatMonsterId());

      context.startCombat("troll");
      assertEquals("troll", context.getCombatMonsterId());
      assertTrue(context.isInCombat());
    }
  }

  @Nested
  @DisplayName("Reset Game Tests")
  class ResetGameTests {

    @Test
    @DisplayName("should reset game with new world and player")
    void shouldResetGameWithNewWorldAndPlayer() {
      World newWorld = new World("2.0", Map.of(), Map.of(), Map.of(), Map.of(), "entrance");

      context.resetGame(newWorld, "NewPlayer");

      assertEquals(newWorld, context.world());
      assertEquals("NewPlayer", context.player().getName());
      assertEquals("entrance", context.player().getRoomId());
    }

    @Test
    @DisplayName("should clear all state flags when resetting game")
    void shouldClearAllStateFlagsWhenResettingGame() {
      // Set some state
      context.setAwaitingPuzzleAnswer(true);
      context.startCombat("goblin");

      World newWorld = new World("2.0", Map.of(), Map.of(), Map.of(), Map.of(), "entrance");

      context.resetGame(newWorld, "NewPlayer");

      assertFalse(context.isAwaitingPuzzleAnswer());
      assertFalse(context.isInCombat());
      assertNull(context.getCombatMonsterId());
    }

    @Test
    @DisplayName("should throw NullPointerException if new world is null")
    void shouldThrowNullPointerExceptionIfNewWorldIsNull() {
      assertThrows(NullPointerException.class, () -> context.resetGame(null, "NewPlayer"));
    }

    @Test
    @DisplayName("should create player at new world start room")
    void shouldCreatePlayerAtNewWorldStartRoom() {
      World newWorld = new World("3.0", Map.of(), Map.of(), Map.of(), Map.of(), "custom_start");

      context.resetGame(newWorld, "PlayerName");

      assertEquals("custom_start", context.player().getRoomId());
    }
  }

  @Nested
  @DisplayName("State Management Integration Tests")
  class StateManagementIntegrationTests {

    @Test
    @DisplayName("should handle simultaneous puzzle and combat states")
    void shouldHandleSimultaneousPuzzleAndCombatStates() {
      context.setAwaitingPuzzleAnswer(true);
      context.startCombat("goblin");

      assertTrue(context.isAwaitingPuzzleAnswer());
      assertTrue(context.isInCombat());
      assertEquals("goblin", context.getCombatMonsterId());
    }

    @Test
    @DisplayName("should maintain player and world references through state changes")
    void shouldMaintainPlayerAndWorldReferencesThroughStateChanges() {
      context.setAwaitingPuzzleAnswer(true);
      context.startCombat("goblin");
      context.setAwaitingPuzzleAnswer(false);
      context.endCombat();

      assertEquals(world, context.world());
      assertEquals(player, context.player());
    }
  }

  @Nested
  @DisplayName("Accessor Tests")
  class AccessorTests {

    @Test
    @DisplayName("should return world reference")
    void shouldReturnWorldReference() {
      World contextWorld = context.world();

      assertEquals(world, contextWorld);
      assertEquals("1.0", contextWorld.getVersion());
    }

    @Test
    @DisplayName("should return player reference")
    void shouldReturnPlayerReference() {
      Player contextPlayer = context.player();

      assertEquals(player, contextPlayer);
      assertEquals("TestPlayer", contextPlayer.getName());
    }
  }
}
