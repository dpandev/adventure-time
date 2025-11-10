package com.dpandev.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dpandev.domain.service.CombatService;
import com.dpandev.domain.service.CommandResult;
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

/** Unit tests for CombatController. */
@ExtendWith(MockitoExtension.class)
class CombatControllerTest {

  @Mock private CombatService combatService;
  @Mock private GameContext ctx;

  private CombatController controller;

  @BeforeEach
  void setUp() {
    controller = new CombatController(combatService);
  }

  @Nested
  @DisplayName("ATTACK Command Tests")
  class AttackCommandTests {

    @Test
    @DisplayName("should initiate combat when not in combat")
    void shouldInitiateCombatWhenNotInCombat() {
      CommandToken cmd =
          new CommandToken(Verb.ATTACK, "goblin", List.of("goblin"), "attack goblin");
      CommandResult expected = CommandResult.success("Combat started with goblin");
      when(ctx.isInCombat()).thenReturn(false);
      when(combatService.initiateCombat(ctx, "goblin")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(combatService).initiateCombat(ctx, "goblin");
    }

    @Test
    @DisplayName("should attack current monster when already in combat")
    void shouldAttackCurrentMonsterWhenInCombat() {
      CommandToken cmd = new CommandToken(Verb.ATTACK, null, List.of(), "attack");
      CommandResult expected = CommandResult.success("You attack the goblin");
      when(ctx.isInCombat()).thenReturn(true);
      when(combatService.playerAttack(ctx)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(combatService).playerAttack(ctx);
    }
  }

  @Nested
  @DisplayName("IGNORE Command Tests")
  class IgnoreCommandTests {

    @Test
    @DisplayName("should delegate IGNORE to combat service")
    void shouldDelegateIgnoreToCombatService() {
      CommandToken cmd =
          new CommandToken(Verb.IGNORE, "goblin", List.of("goblin"), "ignore goblin");
      CommandResult expected = CommandResult.success("You ignore the goblin");
      when(combatService.ignoreMonster(ctx, "goblin")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(combatService).ignoreMonster(ctx, "goblin");
    }
  }

  @Nested
  @DisplayName("Unsupported Command Tests")
  class UnsupportedCommandTests {

    @Test
    @DisplayName("should return fail for unsupported verb")
    void shouldReturnFailForUnsupportedVerb() {
      CommandToken cmd = new CommandToken(Verb.LOOK, null, List.of(), "look");

      CommandResult result = controller.handle(cmd, ctx);

      assertFalse(result.success());
      assertTrue(result.message().contains("Unsupported"));
      assertTrue(result.message().contains("LOOK"));
    }
  }
}
