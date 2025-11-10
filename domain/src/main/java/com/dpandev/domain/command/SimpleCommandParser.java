package com.dpandev.domain.command;

import com.dpandev.domain.utils.CommandToken;
import com.dpandev.domain.utils.Verb;
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
  public CommandToken parse(final String line) {
    final String raw = (line == null) ? "" : line;
    final String normalized = normalize(raw);
    if (normalized.isEmpty()) {
      return new CommandToken(Verb.UNKNOWN, null, List.of(), raw);
    }

    final List<String> tokens = new ArrayList<>(List.of(normalized.split(" ")));
    final String head = tokens.getFirst();

    // single-token direction like "n" or "north"
    final String dirSyn = directionSynonyms.get(head);
    if (dirSyn != null && tokens.size() == 1) {
      return new CommandToken(Verb.GO, dirSyn, List.of(), raw);
    }

    // resolve verb; fallback to GO if first token is a direction word
    Verb verb = resolveVerb(head);
    if (verb == Verb.UNKNOWN && dirSyn != null) {
      verb = Verb.GO;
    }

    String target = null;
    List<String> args = List.of();

    if (verb == Verb.GO) {
      // need a second token for the direction; if missing, leave target null
      if (tokens.size() >= 2) {
        final String t = tokens.get(1);
        target = directionSynonyms.getOrDefault(t, t);
        args = List.copyOf(tokens.subList(1, tokens.size()));
      } else {
        // controller should prompt for a direction
        target = null;
        args = List.of();
      }
    } else if (verb == Verb.INSPECT
        || verb == Verb.PICKUP
        || verb == Verb.DROP
        || verb == Verb.USE
        || verb == Verb.EQUIP
        || verb == Verb.UNEQUIP
        || verb == Verb.HEAL
        || verb == Verb.ATTACK
        || verb == Verb.IGNORE) {
      // multi-word targets allowed ("pickup steel sword", "equip iron helmet", "attack ice troll")
      if (tokens.size() >= 2) {
        target = String.join(" ", tokens.subList(1, tokens.size()));
        args = List.copyOf(tokens.subList(1, tokens.size()));
      } else {
        // no target provided
        target = null;
        args = List.of();
      }
    } else {
      // other verbs: optional single target token
      if (tokens.size() >= 2) {
        target = tokens.get(1);
        args = (tokens.size() > 2) ? List.copyOf(tokens.subList(2, tokens.size())) : List.of();
      } else {
        target = null;
        args = List.of();
      }
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
    verbMap.put("map", Verb.MAP);
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
    verbMap.put("equip", Verb.EQUIP);
    verbMap.put("unequip", Verb.UNEQUIP);
    verbMap.put("heal", Verb.HEAL);
    verbMap.put("drink", Verb.HEAL);
    verbMap.put("consume", Verb.HEAL);
    verbMap.put("solve", Verb.SOLVE);
    verbMap.put("answer", Verb.SOLVE);
    verbMap.put("stats", Verb.STATS);
    verbMap.put("status", Verb.STATS);
    verbMap.put("health", Verb.STATS);
    verbMap.put("attack", Verb.ATTACK);
    verbMap.put("fight", Verb.ATTACK);
    verbMap.put("ignore", Verb.IGNORE);
    verbMap.put("flee", Verb.IGNORE);
    verbMap.put("run", Verb.IGNORE);
    verbMap.put("new", Verb.NEW_GAME);
    verbMap.put("newgame", Verb.NEW_GAME);
    verbMap.put("restart", Verb.NEW_GAME);
    verbMap.put("quit", Verb.QUIT);
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
    dirMap.put("south", "south");
    dirMap.put("s", "south");
    dirMap.put("west", "west");
    dirMap.put("w", "west");
    dirMap.put("east", "east");
    dirMap.put("e", "east");
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
