package com.dpandev.domain.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple command parser that interprets user input into structured commands. Supports verb
 * synonyms, direction shortcuts, and argument extraction.
 */
public class SimpleCommandParser implements CommandParser {
  private final Map<String, Verb> verbs;
  private final Map<String, String> directionSynonyms;

  /** Construct a SimpleCommandParser with predefined verbs and direction synonyms. */
  public SimpleCommandParser() {
    this.verbs = buildVerbs();
    this.directionSynonyms = buildDirectionSynonyms();
  }

  /**
   * Parse a command line into a CommandToken. Handles normalization, verb resolution, direction
   * synonyms, and argument extraction.
   *
   * @param line - the input command line
   * @return the parsed CommandToken
   */
  @Override
  public CommandToken parse(String line) {
    final String raw = (line == null) ? "" : line;
    final String normalized = normalize(raw);
    if (normalized.isEmpty()) {
      return new CommandToken(Verb.UNKNOWN, null, List.of(), raw);
    }
    List<String> tokens = new ArrayList<>(List.of(normalized.split(" ")));
    // first token is the verb
    String head = tokens.getFirst();

    // single-token direction command like "n" or "north"
    String dir = directionSynonyms.get(head);
    if (dir != null && tokens.size() == 1) {
      return new CommandToken(Verb.GO, dir, List.of(), raw);
    }
    // resolve the verb
    Verb verb = resolveVerb(head);
    // fallback to GO if first token is a direction
    if (verb == Verb.UNKNOWN && dir != null) {
      verb = Verb.GO;
    }
    // remaining tokens are target and args
    String target = null;
    List<String> args = List.of();
    if (tokens.size() > 1 || (verb == Verb.GO && tokens.size() > 0)) {
      if (verb == Verb.GO) {
        target = directionSynonyms.getOrDefault(tokens.get(1), tokens.get(1));
        // if verb is GO and target is null, then controller should prompt user for direction
      } else if (verb == Verb.INSPECT) {
        // if verb is INSPECT and no target, check if second token is a number (e.g., "inspect 2
        // box")
        int targetIndex = 1;
        target = tokens.get(targetIndex);
        args =
            (tokens.size() > targetIndex + 1)
                ? tokens.subList(targetIndex + 1, tokens.size())
                : List.of();
        // else: no target provided (e.g., "inspect"), controller can prompt
      } else {
        target = tokens.get(1);
      }
      // additional args beyond the target, if any; else empty list
      args = (tokens.size() > 2) ? tokens.subList(2, tokens.size()) : List.of();
    }
    return new CommandToken(verb, target, List.copyOf(args), raw);
  }

  /**
   * Normalize input string: lowercase, trim, collapse internal whitespace.
   *
   * @param s - input string
   * @return normalized string
   */
  private static String normalize(String s) {
    return s.toLowerCase().trim().replaceAll("\\s+", " ");
  }

  /**
   * Build the primary verb map with synonyms.
   *
   * @return map of verb strings to Verb enum
   */
  private Map<String, Verb> buildVerbs() {
    Map<String, Verb> verbMap = new HashMap<>();
    verbMap.put("help", Verb.HELP);
    verbMap.put("?", Verb.HELP);
    verbMap.put("look", Verb.LOOK);
    verbMap.put("l", Verb.LOOK);
    verbMap.put("inspect", Verb.INSPECT);
    verbMap.put("examine", Verb.INSPECT);
    verbMap.put("go", Verb.GO);
    verbMap.put("move", Verb.GO);
    verbMap.put("pickup", Verb.PICKUP);
    verbMap.put("grab", Verb.PICKUP);
    verbMap.put("take", Verb.PICKUP);
    verbMap.put("drop", Verb.DROP);
    verbMap.put("discard", Verb.DROP);
    verbMap.put("use", Verb.USE);
    verbMap.put("activate", Verb.USE);
    verbMap.put("inventory", Verb.INVENTORY);
    verbMap.put("i", Verb.INVENTORY);
    verbMap.put("solve", Verb.SOLVE);
    verbMap.put("answer", Verb.SOLVE);
    verbMap.put("quit", Verb.QUIT);
    verbMap.put("exit", Verb.QUIT);
    verbMap.put("q", Verb.QUIT);
    verbMap.put("save", Verb.SAVE);
    verbMap.put("load", Verb.LOAD);
    return verbMap;
  }

  /**
   * Build the direction synonyms map.
   *
   * @return map of direction strings to canonical direction
   */
  private static Map<String, String> buildDirectionSynonyms() {
    Map<String, String> dirMap = new HashMap<>();
    dirMap.put("north", "north");
    dirMap.put("n", "north");
    dirMap.put("south", "west");
    dirMap.put("s", "south");
    dirMap.put("east", "west");
    dirMap.put("e", "east");
    dirMap.put("west", "west");
    dirMap.put("w", "west");
    return dirMap;
  }

  /**
   * Resolve the verb from the command head.
   *
   * @param head - the first word of the command
   * @return - the corresponding Verb, or UNKNOWN if not found
   */
  private Verb resolveVerb(String head) {
    Verb v = verbs.get(head);
    if (v != null) {
      return v;
    }
    // if the head looks like a direction ("n", "north"), treat as GO
    if (directionSynonyms.containsKey(head)) {
      return Verb.GO;
    }
    return Verb.UNKNOWN;
  }
}
