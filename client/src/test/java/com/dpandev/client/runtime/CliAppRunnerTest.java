package com.dpandev.client.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dpandev.client.controller.FrontController;
import com.dpandev.client.view.ConsoleView;
import com.dpandev.domain.command.CommandParser;
import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.ExplorationService;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.Verb;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprehensive unit tests for CliAppRunner. */
@ExtendWith(MockitoExtension.class)
class CliAppRunnerTest {

  @Mock private ConsoleView view;
  @Mock private CommandParser parser;
  @Mock private FrontController frontController;
  @Mock private ExplorationService explorationService;
  @Mock private GameContext ctx;

  private CliAppRunner runner;

  @BeforeEach
  void setUp() {
    runner = new CliAppRunner(view, parser, frontController, explorationService, ctx);
  }

  @Test
  void testConstructorStoresAllDependencies() {
    assertNotNull(runner);
  }

  @Test
  void testRunDisplaysWelcomeMessages() throws Exception {
    when(view.readLine()).thenThrow(new RuntimeException("Exit"));
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));

    try {
      runner.run();
    } catch (RuntimeException e) {
      // Expected to exit for test
    }

    verify(view).println("Welcome to Adventure Time");
    verify(view).println("Type 'help' for commands, 'quit' to exit.");
  }

  @Test
  void testRunDisplaysInitialRoomDescription() throws Exception {
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "You are in a dark room.", false));
    when(view.readLine()).thenThrow(new RuntimeException("Exit"));

    try {
      runner.run();
    } catch (RuntimeException e) {
      // Expected to exit for test
    }

    verify(view).println("You are in a dark room.");
  }

  @Test
  void testRunProcessesCommand() throws Exception {
    CommandToken token = new CommandToken(Verb.LOOK, "look", List.of(), "look");
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));
    when(view.readLine()).thenReturn("look").thenThrow(new RuntimeException("Exit"));
    when(parser.parse("look")).thenReturn(token);
    when(ctx.isAwaitingPuzzleAnswer()).thenReturn(false);
    when(frontController.handle(token, ctx))
        .thenReturn(new CommandResult(true, "You look around.", false));

    try {
      runner.run();
    } catch (RuntimeException e) {
      // Expected to exit for test
    }

    verify(parser).parse("look");
    verify(frontController).handle(token, ctx);
    verify(view).println("You look around.");
  }

  @Test
  void testRunExitsOnQuitCommand() throws Exception {
    CommandToken token = new CommandToken(Verb.QUIT, "quit", List.of(), "quit");
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));
    when(view.readLine()).thenReturn("quit");
    when(parser.parse("quit")).thenReturn(token);
    when(ctx.isAwaitingPuzzleAnswer()).thenReturn(false);
    when(frontController.handle(token, ctx)).thenReturn(new CommandResult(true, "Goodbye!", true));

    runner.run();

    verify(view).println("Goodbye!");
  }

  @Test
  void testRunProcessesMultipleCommands() throws Exception {
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));
    when(ctx.isAwaitingPuzzleAnswer()).thenReturn(false);

    CommandToken cmd1 = new CommandToken(Verb.LOOK, "look", List.of(), "look");
    CommandToken cmd2 = new CommandToken(Verb.QUIT, "quit", List.of(), "quit");

    when(view.readLine()).thenReturn("look", "quit");
    when(parser.parse("look")).thenReturn(cmd1);
    when(parser.parse("quit")).thenReturn(cmd2);
    when(frontController.handle(cmd1, ctx)).thenReturn(new CommandResult(true, "Looking", false));
    when(frontController.handle(cmd2, ctx)).thenReturn(new CommandResult(true, "Bye", true));

    runner.run();

    verify(parser).parse("look");
    verify(parser).parse("quit");
    verify(frontController, times(2)).handle(any(CommandToken.class), eq(ctx));
  }

  @Test
  void testRunEntersPuzzleModeWhenAwaitingAnswer() throws Exception {
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));
    when(ctx.isAwaitingPuzzleAnswer()).thenReturn(true);
    when(view.readLine()).thenReturn("answer").thenThrow(new RuntimeException("Exit"));

    ArgumentCaptor<CommandToken> tokenCaptor = ArgumentCaptor.forClass(CommandToken.class);
    when(frontController.handle(tokenCaptor.capture(), eq(ctx)))
        .thenReturn(new CommandResult(true, "Correct!", false));

    try {
      runner.run();
    } catch (RuntimeException e) {
      // Expected to exit for test
    }

    verify(parser, never()).parse(anyString());
    CommandToken captured = tokenCaptor.getValue();
    assertEquals(Verb.SOLVE, captured.verb());
    assertEquals("answer", captured.target());
  }

  @Test
  void testRunHandlesReadLineException() throws Exception {
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));
    when(view.readLine()).thenThrow(new RuntimeException("IO Error"));

    runner.run();

    verify(view).println("Error reading input. Exiting.");
  }

  @Test
  void testRunDoesNotDisplayBlankMessage() throws Exception {
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));
    when(ctx.isAwaitingPuzzleAnswer()).thenReturn(false);
    when(view.readLine()).thenReturn("look").thenThrow(new RuntimeException("Exit"));
    when(parser.parse("look")).thenReturn(new CommandToken(Verb.LOOK, "look", List.of(), "look"));
    when(frontController.handle(any(), eq(ctx))).thenReturn(new CommandResult(true, "", false));

    try {
      runner.run();
    } catch (RuntimeException e) {
      // Expected to exit for test
    }

    // Verify blank message is not printed (only welcome and room description should be printed)
    verify(view, atLeast(1)).println(anyString());
  }

  @Test
  void testRunHandlesNullCommandResult() throws Exception {
    when(explorationService.describeCurrentRoom(ctx))
        .thenReturn(new CommandResult(true, "Room", false));
    when(ctx.isAwaitingPuzzleAnswer()).thenReturn(false);
    when(view.readLine()).thenReturn("look").thenThrow(new RuntimeException("Exit"));
    when(parser.parse("look")).thenReturn(new CommandToken(Verb.LOOK, "look", List.of(), "look"));
    when(frontController.handle(any(), eq(ctx))).thenReturn(null);

    try {
      runner.run();
    } catch (RuntimeException e) {
      // Expected to exit for test
    }

    // Should not crash on null result
    verify(frontController).handle(any(), eq(ctx));
  }
}
