package com.dpandev.domain.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.dpandev.domain.model.Player;
import com.dpandev.domain.model.Room;
import com.dpandev.domain.utils.GameContext;
import com.dpandev.domain.world.World;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for DefaultMapService. */
@ExtendWith(MockitoExtension.class)
class DefaultMapServiceTest {

  @Mock private World world;
  @Mock private Player player;
  @Mock private GameContext ctx;

  private DefaultMapService mapService;

  @BeforeEach
  void setUp() {
    mapService = new DefaultMapService();
    when(ctx.world()).thenReturn(world);
    when(ctx.player()).thenReturn(player);
  }

  @Nested
  @DisplayName("Show Map Tests")
  class ShowMapTests {

    @Test
    @DisplayName("should show map with all cardinal directions")
    void shouldShowMapWithAllCardinalDirections() {
      Room currentRoom =
          Room.builder()
              .id("center")
              .name("Center Room")
              .description("The center")
              .exits(
                  Map.of(
                      "north", "north_room",
                      "south", "south_room",
                      "east", "east_room",
                      "west", "west_room"))
              .build();

      Room northRoom =
          Room.builder().id("north_room").name("North Room").description("To the north").build();

      Room southRoom =
          Room.builder().id("south_room").name("South Room").description("To the south").build();

      Room eastRoom =
          Room.builder().id("east_room").name("East Room").description("To the east").build();

      Room westRoom =
          Room.builder().id("west_room").name("West Room").description("To the west").build();

      when(player.getRoomId()).thenReturn("center");
      when(world.getRoomById("center")).thenReturn(Optional.of(currentRoom));
      when(world.getRoomById("north_room")).thenReturn(Optional.of(northRoom));
      when(world.getRoomById("south_room")).thenReturn(Optional.of(southRoom));
      when(world.getRoomById("east_room")).thenReturn(Optional.of(eastRoom));
      when(world.getRoomById("west_room")).thenReturn(Optional.of(westRoom));

      CommandResult result = mapService.showMap(ctx);

      assertTrue(result.success());
      String map = result.message();
      assertTrue(map.contains("AREA MAP"));
      assertTrue(map.contains("Center Room"));
      assertTrue(map.contains("North Room"));
      assertTrue(map.contains("South Room"));
      assertTrue(map.contains("East Room"));
      assertTrue(map.contains("West Room"));
      assertTrue(map.contains("[ Center Room ]")); // Current location highlighted
      assertTrue(map.contains("NORTH"));
      assertTrue(map.contains("SOUTH"));
      assertTrue(map.contains("Available exits: east, north, south, west"));
    }

    @Test
    @DisplayName("should show map with up and down")
    void shouldShowMapWithUpAndDown() {
      Room currentRoom =
          Room.builder()
              .id("current")
              .name("Current Level")
              .description("Current")
              .exits(Map.of("up", "upper_room", "down", "lower_room"))
              .build();

      Room upperRoom =
          Room.builder().id("upper_room").name("Upper Level").description("Above").build();

      Room lowerRoom =
          Room.builder().id("lower_room").name("Lower Level").description("Below").build();

      when(player.getRoomId()).thenReturn("current");
      when(world.getRoomById("current")).thenReturn(Optional.of(currentRoom));
      when(world.getRoomById("upper_room")).thenReturn(Optional.of(upperRoom));
      when(world.getRoomById("lower_room")).thenReturn(Optional.of(lowerRoom));

      CommandResult result = mapService.showMap(ctx);

      assertTrue(result.success());
      String map = result.message();
      assertTrue(map.contains("Upper Level"));
      assertTrue(map.contains("Lower Level"));
      assertTrue(map.contains("UP"));
      assertTrue(map.contains("DOWN"));
      assertTrue(map.contains("Available exits: down, up"));
    }

    @Test
    @DisplayName("should show map with no exits")
    void shouldShowMapWithNoExits() {
      Room isolatedRoom =
          Room.builder()
              .id("isolated")
              .name("Isolated Room")
              .description("No way out")
              .exits(Map.of())
              .build();

      when(player.getRoomId()).thenReturn("isolated");
      when(world.getRoomById("isolated")).thenReturn(Optional.of(isolatedRoom));

      CommandResult result = mapService.showMap(ctx);

      assertTrue(result.success());
      String map = result.message();
      assertTrue(map.contains("[ Isolated Room ]"));
      assertTrue(map.contains("Available exits: None"));
    }

    @Test
    @DisplayName("should show map with single exit")
    void shouldShowMapWithSingleExit() {
      Room currentRoom =
          Room.builder()
              .id("current")
              .name("Current Room")
              .description("Current")
              .exits(Map.of("north", "next_room"))
              .build();

      Room nextRoom = Room.builder().id("next_room").name("Next Room").description("Next").build();

      when(player.getRoomId()).thenReturn("current");
      when(world.getRoomById("current")).thenReturn(Optional.of(currentRoom));
      when(world.getRoomById("next_room")).thenReturn(Optional.of(nextRoom));

      CommandResult result = mapService.showMap(ctx);

      assertTrue(result.success());
      String map = result.message();
      assertTrue(map.contains("Next Room"));
      assertTrue(map.contains("Available exits: north"));
    }

    @Test
    @DisplayName("should handle long room names")
    void shouldHandleLongRoomNames() {
      Room currentRoom =
          Room.builder()
              .id("current")
              .name("This is a Very Long Room Name That Should Be Truncated")
              .description("Current")
              .exits(Map.of("north", "north_room"))
              .build();

      Room northRoom =
          Room.builder()
              .id("north_room")
              .name("Another Extremely Long Room Name That Needs Truncation")
              .description("North")
              .build();

      when(player.getRoomId()).thenReturn("current");
      when(world.getRoomById("current")).thenReturn(Optional.of(currentRoom));
      when(world.getRoomById("north_room")).thenReturn(Optional.of(northRoom));

      CommandResult result = mapService.showMap(ctx);

      assertTrue(result.success());
      String map = result.message();
      assertTrue(map.contains("...")); // Truncation indicator
    }

    @Test
    @DisplayName("should fail when current room not found")
    void shouldFailWhenCurrentRoomNotFound() {
      when(player.getRoomId()).thenReturn("unknown");
      when(world.getRoomById("unknown")).thenReturn(Optional.empty());

      CommandResult result = mapService.showMap(ctx);

      assertFalse(result.success());
      assertTrue(result.message().contains("unknown"));
    }

    @Test
    @DisplayName("should handle missing adjacent rooms gracefully")
    void shouldHandleMissingAdjacentRoomsGracefully() {
      Room currentRoom =
          Room.builder()
              .id("current")
              .name("Current Room")
              .description("Current")
              .exits(Map.of("north", "missing_room"))
              .build();

      when(player.getRoomId()).thenReturn("current");
      when(world.getRoomById("current")).thenReturn(Optional.of(currentRoom));
      when(world.getRoomById("missing_room")).thenReturn(Optional.empty());

      CommandResult result = mapService.showMap(ctx);

      assertTrue(result.success());
      // Map should still display, just without the missing room
      String map = result.message();
      assertTrue(map.contains("[ Current Room ]"));
    }
  }
}
