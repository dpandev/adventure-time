package com.dpandev.domain.utils;

/**
 * Represents the result of executing a command in the Adventure Time game.
 *
 * @param success indicates if the command was executed successfully
 * @param message provides feedback or information about the command execution
 */
public record CommandResult(boolean success, String message) {
  public static CommandResult ok(String message) {
    return new CommandResult(true, message);
  }

  public static CommandResult fail(String message) {
    return new CommandResult(false, message);
  }
}
