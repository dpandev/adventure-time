package com.dpandev.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.InteractionService;
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

/** Unit tests for InteractionController. */
@ExtendWith(MockitoExtension.class)
class InteractionControllerTest {

  @Mock private InteractionService interactionService;
  @Mock private GameContext ctx;

  private InteractionController controller;

  @BeforeEach
  void setUp() {
    controller = new InteractionController(interactionService);
  }

  @Nested
  @DisplayName("SOLVE Command Tests")
  class SolveCommandTests {

    @Test
    @DisplayName("should delegate SOLVE to interaction service")
    void shouldDelegateSolveToInteractionService() {
      CommandToken cmd = new CommandToken(Verb.SOLVE, "echo", List.of("echo"), "solve echo");
      CommandResult expected = CommandResult.success("Correct!");
      when(interactionService.solve(ctx, "echo")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(interactionService).solve(ctx, "echo");
    }

    @Test
    @DisplayName("should handle SOLVE with multi-word answer")
    void shouldHandleSolveWithMultiWordAnswer() {
      CommandToken cmd =
          new CommandToken(Verb.SOLVE, "the moon", List.of("the", "moon"), "solve the moon");
      CommandResult expected = CommandResult.success("Correct!");
      when(interactionService.solve(ctx, "the moon")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(interactionService).solve(ctx, "the moon");
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
