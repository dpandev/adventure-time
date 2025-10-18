package com.dpandev.client.controller;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.utils.Verb;

/**
 * Interface for handling commands in the Adventure Time game. Implementations should specify which
 * verbs they support and how to handle them.
 */
public interface CommandController {

  /**
   * Checks if this controller supports handling the given verb.
   *
   * @param verb the verb to check
   * @return true if the verb is supported, false otherwise
   */
  default boolean supports(Verb verb) {
    return true;
  }

  /**
   * Handles the given command within the provided game context.
   *
   * @param cmd the command token to handle
   * @param ctx the current game context
   * @return the result of handling the command
   */
  CommandResult handle(CommandToken cmd, GameContext ctx);
}
