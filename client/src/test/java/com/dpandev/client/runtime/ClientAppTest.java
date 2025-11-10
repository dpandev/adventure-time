package com.dpandev.client.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Comprehensive unit tests for ClientApp, focusing on worldpack argument parsing. */
class ClientAppTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void setUpStreams() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void restoreStreams() {
    System.setOut(originalOut);
  }

  // ============================================================================
  // Worldpack Argument Parsing Tests
  // ============================================================================

  @Test
  void testParseWorldpackArgNoArgs() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {});
    assertEquals("worldpacks/example.json", result);
  }

  @Test
  void testParseWorldpackArgNullArgs() throws Exception {
    String result = invokeParseWorldpackArg(null);
    assertEquals("worldpacks/example.json", result);
  }

  @Test
  void testParseWorldpackArgEmptyArray() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {});
    assertEquals("worldpacks/example.json", result);
  }

  @Test
  void testParseWorldpackArgWorldFlag() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=jurassic"});
    assertEquals("worldpacks/jurassic.json", result);
  }

  @Test
  void testParseWorldpackArgWorldFlagWithJson() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=custom.json"});
    assertEquals("custom.json", result);
  }

  @Test
  void testParseWorldpackArgWorldFlagWithPath() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=path/to/world.json"});
    assertEquals("path/to/world.json", result);
  }

  @Test
  void testParseWorldpackArgWorldpackFlag() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--worldpack=/full/path/custom.json"});
    assertEquals("/full/path/custom.json", result);
  }

  @Test
  void testParseWorldpackArgShortFlag() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"-w", "jurassic"});
    assertEquals("worldpacks/jurassic.json", result);
  }

  @Test
  void testParseWorldpackArgShortFlagWithJson() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"-w", "custom.json"});
    assertEquals("custom.json", result);
  }

  @Test
  void testParseWorldpackArgShortFlagWithPath() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"-w", "path/to/world.json"});
    assertEquals("path/to/world.json", result);
  }

  @Test
  void testParseWorldpackArgShortFlagOnly() throws Exception {
    // -w without following argument should fall back to default
    String result = invokeParseWorldpackArg(new String[] {"-w"});
    assertEquals("worldpacks/example.json", result);
  }

  @Test
  void testParseWorldpackArgMultipleArgs() throws Exception {
    // First valid worldpack arg should be used
    String result = invokeParseWorldpackArg(new String[] {"--world=jurassic", "--world=example"});
    assertEquals("worldpacks/jurassic.json", result);
  }

  @Test
  void testParseWorldpackArgMixedArgs() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--other-flag", "--world=jurassic"});
    assertEquals("worldpacks/jurassic.json", result);
  }

  @Test
  void testParseWorldpackArgUnrecognizedFlags() throws Exception {
    String result =
        invokeParseWorldpackArg(new String[] {"--unknown", "--random=value", "--other"});
    assertEquals("worldpacks/example.json", result);
  }

  @Test
  void testParseWorldpackArgEmptyWorldName() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world="});
    assertEquals("worldpacks/.json", result); // Edge case: empty name
  }

  @Test
  void testParseWorldpackArgWorldpackEmptyPath() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--worldpack="});
    assertEquals("", result); // Edge case: empty path
  }

  @Test
  void testParseWorldpackArgCaseSensitivity() throws Exception {
    // Should be case-sensitive for world names
    String result = invokeParseWorldpackArg(new String[] {"--world=Jurassic"});
    assertEquals("worldpacks/Jurassic.json", result);
  }

  @Test
  void testParseWorldpackArgSpecialCharacters() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=my-world_v2"});
    assertEquals("worldpacks/my-world_v2.json", result);
  }

  @Test
  void testParseWorldpackArgRelativePath() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=../other/world.json"});
    assertEquals("../other/world.json", result);
  }

  @Test
  void testParseWorldpackArgAbsolutePath() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=/Users/test/world.json"});
    assertEquals("/Users/test/world.json", result);
  }

  @Test
  void testParseWorldpackArgShortFlagInMiddle() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--other", "-w", "custom", "--flag"});
    assertEquals("worldpacks/custom.json", result);
  }

  @Test
  void testParseWorldpackArgShortFlagAtEnd() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--other", "-w"});
    assertEquals("worldpacks/example.json", result); // No value after -w
  }

  @Test
  void testParseWorldpackArgWorldpackPriority() throws Exception {
    // When both --world and --worldpack are present, first one wins
    String result =
        invokeParseWorldpackArg(new String[] {"--world=jurassic", "--worldpack=/full/path.json"});
    assertEquals("worldpacks/jurassic.json", result);
  }

  @Test
  void testParseWorldpackArgShortAndLongFlags() throws Exception {
    // First valid arg wins
    String result = invokeParseWorldpackArg(new String[] {"-w", "first", "--world=second"});
    assertEquals("worldpacks/first.json", result);
  }

  @Test
  void testParseWorldpackArgSpacesInPath() throws Exception {
    // Path with spaces (though not recommended)
    String result = invokeParseWorldpackArg(new String[] {"--worldpack=my world/pack.json"});
    assertEquals("my world/pack.json", result);
  }

  @Test
  void testParseWorldpackArgBackslashPath() throws Exception {
    // Windows-style path
    String result = invokeParseWorldpackArg(new String[] {"--world=C:\\worlds\\game.json"});
    assertEquals("C:\\worlds\\game.json", result);
  }

  @Test
  void testParseWorldpackArgMultipleJsonExtensions() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=world.json.json"});
    assertEquals("world.json.json", result);
  }

  @Test
  void testParseWorldpackArgNoJsonExtension() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=myworld"});
    assertEquals("worldpacks/myworld.json", result);
  }

  @Test
  void testParseWorldpackArgNumericWorldName() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=123"});
    assertEquals("worldpacks/123.json", result);
  }

  @Test
  void testParseWorldpackArgDotInWorldName() throws Exception {
    String result = invokeParseWorldpackArg(new String[] {"--world=world.v2"});
    assertEquals("worldpacks/world.v2.json", result);
  }

  @Test
  void testParseWorldpackArgShortFlagWithMultipleValues() throws Exception {
    // Only the immediate next value after -w should be used
    String result = invokeParseWorldpackArg(new String[] {"-w", "first", "second"});
    assertEquals("worldpacks/first.json", result);
  }

  // ============================================================================
  // Integration Tests - Full Worldpack Loading
  // ============================================================================

  @Test
  void testWorldpackActuallyLoadsFromArgs() throws Exception {
    // Test that passing --world=test actually loads the test worldpack
    String worldpack = invokeParseWorldpackArg(new String[] {"--world=test"});
    assertEquals("worldpacks/test.json", worldpack);

    // Verify the worldpack can be loaded (should be on test classpath)
    com.dpandev.domain.world.JsonWorldLoader loader =
        new com.dpandev.domain.world.JsonWorldLoader(worldpack);
    com.dpandev.domain.world.World world = loader.load();

    // Verify it's the correct world
    assertEquals("test_start", world.getStartRoomId());
    assertEquals("Test Start Room", world.getRooms().get("test_start").getName());
    assertEquals(1, world.getItems().size());
    assertEquals("Test Sword", world.getItems().get("test_sword").getName());
  }

  @Test
  void testDefaultWorldpackLoadsWhenNoArgs() throws Exception {
    // Test that no args loads the example worldpack
    String worldpack = invokeParseWorldpackArg(new String[] {});
    assertEquals("worldpacks/example.json", worldpack);

    // Verify the default worldpack can be loaded
    com.dpandev.domain.world.JsonWorldLoader loader =
        new com.dpandev.domain.world.JsonWorldLoader(worldpack);
    com.dpandev.domain.world.World world = loader.load();

    // Verify it's the example world
    assertEquals("entrance", world.getStartRoomId());
    assertEquals("Frozen Gate", world.getRooms().get("entrance").getName());
  }

  @Test
  void testWorldpackWithShortFlagLoads() throws Exception {
    // Test that -w test loads the test worldpack
    String worldpack = invokeParseWorldpackArg(new String[] {"-w", "test"});
    assertEquals("worldpacks/test.json", worldpack);

    // Verify the worldpack can be loaded
    com.dpandev.domain.world.JsonWorldLoader loader =
        new com.dpandev.domain.world.JsonWorldLoader(worldpack);
    com.dpandev.domain.world.World world = loader.load();

    assertEquals("test_start", world.getStartRoomId());
  }

  @Test
  void testNonExistentWorldpackFallsBackToExample() throws Exception {
    // Test that a non-existent worldpack falls back to example
    String worldpack = invokeParseWorldpackArg(new String[] {"--world=nonexistent"});
    assertEquals("worldpacks/nonexistent.json", worldpack);

    // When loaded, it should fall back to example.json
    com.dpandev.domain.world.JsonWorldLoader loader =
        new com.dpandev.domain.world.JsonWorldLoader(worldpack);
    com.dpandev.domain.world.World world = loader.load();

    // Verify it fell back to example world
    assertEquals("entrance", world.getStartRoomId());
  }

  @Test
  void testWorldpackArgumentParsedBeforeWorldLoad() throws Exception {
    // Verify the parsing happens correctly before JsonWorldLoader is instantiated
    String[] testArgs = new String[] {"--world=test"};
    String result = invokeParseWorldpackArg(testArgs);

    // The result should be ready to pass to JsonWorldLoader
    assertEquals("worldpacks/test.json", result);

    // And when we load it, it should work
    com.dpandev.domain.world.JsonWorldLoader loader =
        new com.dpandev.domain.world.JsonWorldLoader(result);
    com.dpandev.domain.world.World world = loader.load();

    // Verify the test world was loaded with all its entities
    assertEquals(1, world.getRooms().size());
    assertEquals(1, world.getItems().size());
    assertEquals(1, world.getMonsters().size());

    // Verify specific test entities
    assertEquals("Test Monster", world.getMonsters().get("test_monster").getName());
    assertEquals(10, world.getMonsters().get("test_monster").getMaxHealth());
  }

  @Test
  void testJurassicWorldpackLoadsFromArgs() throws Exception {
    // Test that passing --world=jurassic actually loads the jurassic worldpack
    String worldpack = invokeParseWorldpackArg(new String[] {"--world=jurassic"});
    assertEquals("worldpacks/jurassic.json", worldpack);

    // Verify the worldpack can be loaded (should be on test classpath)
    com.dpandev.domain.world.JsonWorldLoader loader =
        new com.dpandev.domain.world.JsonWorldLoader(worldpack);
    com.dpandev.domain.world.World world = loader.load();

    // Verify it's the jurassic world
    assertEquals("visitor_center", world.getStartRoomId());
    assertEquals("Visitor Center", world.getRooms().get("visitor_center").getName());

    // Verify some jurassic-specific items exist
    assertEquals("Dinosaur Egg", world.getItems().get("dino_egg").getName());
    assertEquals("Tranquilizer Gun", world.getItems().get("tranquilizer_gun").getName());

    // Verify dinosaur monsters
    assertEquals("Velociraptor", world.getMonsters().get("velociraptor").getName());
    assertEquals("Tyrannosaurus Rex", world.getMonsters().get("t_rex").getName());
  }

  @Test
  void testJurassicWorldpackWithShortFlag() throws Exception {
    // Test that -w jurassic loads the jurassic worldpack
    String worldpack = invokeParseWorldpackArg(new String[] {"-w", "jurassic"});
    assertEquals("worldpacks/jurassic.json", worldpack);

    // Verify the worldpack can be loaded
    com.dpandev.domain.world.JsonWorldLoader loader =
        new com.dpandev.domain.world.JsonWorldLoader(worldpack);
    com.dpandev.domain.world.World world = loader.load();

    // Verify it's the correct world
    assertEquals("visitor_center", world.getStartRoomId());

    // Verify we have multiple rooms, items, and monsters
    // (jurassic worldpack should be larger than test worldpack)
    assertTrue(world.getRooms().size() > 1);
    assertTrue(world.getItems().size() > 1);
    assertTrue(world.getMonsters().size() > 1);
  }

  // ============================================================================
  // Helper Methods
  // ============================================================================

  /**
   * Uses reflection to invoke the private parseWorldpackArg method in ClientApp.
   *
   * @param args the arguments to pass
   * @return the parsed worldpack path
   * @throws Exception if reflection fails
   */
  private String invokeParseWorldpackArg(String[] args) throws Exception {
    Method method = ClientApp.class.getDeclaredMethod("parseWorldpackArg", String[].class);
    method.setAccessible(true);
    return (String) method.invoke(null, (Object) args);
  }
}
