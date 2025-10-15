package com.dpandev.domain.command;

public interface CommandParser {
  CommandToken parse(String line);
}
