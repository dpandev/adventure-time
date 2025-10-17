package com.dpandev.domain.command;

import com.dpandev.domain.utils.CommandToken;

/** Interface for parsing command lines into structured CommandToken objects. */
public interface CommandParser {
  /**
   * Parse a command line into a CommandToken.
   *
   * @param line - the input command line
   * @return the parsed CommandToken
   */
  CommandToken parse(String line);
}
