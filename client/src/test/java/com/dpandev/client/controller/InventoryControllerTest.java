package com.dpandev.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dpandev.domain.service.CommandResult;
import com.dpandev.domain.service.InventoryService;
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

/** Unit tests for InventoryController. */
@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

  @Mock private InventoryService inventoryService;
  @Mock private GameContext ctx;

  private InventoryController controller;

  @BeforeEach
  void setUp() {
    controller = new InventoryController(inventoryService);
  }

  @Nested
  @DisplayName("PICKUP Command Tests")
  class PickupCommandTests {

    @Test
    @DisplayName("should delegate PICKUP to inventory service")
    void shouldDelegatePickupToInventoryService() {
      CommandToken cmd = new CommandToken(Verb.PICKUP, "sword", List.of("sword"), "pickup sword");
      CommandResult expected = CommandResult.success("Picked up sword");
      when(inventoryService.pickup(ctx, "sword")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).pickup(ctx, "sword");
    }
  }

  @Nested
  @DisplayName("DROP Command Tests")
  class DropCommandTests {

    @Test
    @DisplayName("should delegate DROP to inventory service")
    void shouldDelegateDropToInventoryService() {
      CommandToken cmd = new CommandToken(Verb.DROP, "sword", List.of("sword"), "drop sword");
      CommandResult expected = CommandResult.success("Dropped sword");
      when(inventoryService.drop(ctx, "sword")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).drop(ctx, "sword");
    }
  }

  @Nested
  @DisplayName("INVENTORY Command Tests")
  class InventoryCommandTests {

    @Test
    @DisplayName("should delegate INVENTORY to inventory service")
    void shouldDelegateInventoryToInventoryService() {
      CommandToken cmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");
      CommandResult expected = CommandResult.success("You are carrying: sword");
      when(inventoryService.inventory(ctx)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).inventory(ctx);
    }
  }

  @Nested
  @DisplayName("USE Command Tests")
  class UseCommandTests {

    @Test
    @DisplayName("should delegate USE to inventory service")
    void shouldDelegateUseToInventoryService() {
      CommandToken cmd = new CommandToken(Verb.USE, "key", List.of("key"), "use key");
      CommandResult expected = CommandResult.success("Used key");
      when(inventoryService.use(ctx, "key")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).use(ctx, "key");
    }
  }

  @Nested
  @DisplayName("INSPECT Command Tests")
  class InspectCommandTests {

    @Test
    @DisplayName("should delegate INSPECT to inventory service")
    void shouldDelegateInspectToInventoryService() {
      CommandToken cmd = new CommandToken(Verb.INSPECT, "sword", List.of("sword"), "inspect sword");
      CommandResult expected = CommandResult.success("A sharp sword");
      when(inventoryService.inspect(ctx, "sword")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).inspect(ctx, "sword");
    }
  }

  @Nested
  @DisplayName("EQUIP Command Tests")
  class EquipCommandTests {

    @Test
    @DisplayName("should delegate EQUIP to inventory service")
    void shouldDelegateEquipToInventoryService() {
      CommandToken cmd = new CommandToken(Verb.EQUIP, "helmet", List.of("helmet"), "equip helmet");
      CommandResult expected = CommandResult.success("Equipped helmet");
      when(inventoryService.equip(ctx, "helmet")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).equip(ctx, "helmet");
    }
  }

  @Nested
  @DisplayName("UNEQUIP Command Tests")
  class UnequipCommandTests {

    @Test
    @DisplayName("should delegate UNEQUIP to inventory service")
    void shouldDelegateUnequipToInventoryService() {
      CommandToken cmd =
          new CommandToken(Verb.UNEQUIP, "helmet", List.of("helmet"), "unequip helmet");
      CommandResult expected = CommandResult.success("Unequipped helmet");
      when(inventoryService.unequip(ctx, "helmet")).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).unequip(ctx, "helmet");
    }
  }

  @Nested
  @DisplayName("HEAL Command Tests")
  class HealCommandTests {

    @Test
    @DisplayName("should delegate HEAL to inventory service")
    void shouldDelegateHealToInventoryService() {
      CommandToken cmd = new CommandToken(Verb.HEAL, null, List.of(), "heal");
      CommandResult expected = CommandResult.success("Healed 20 HP");
      when(inventoryService.heal(ctx, null)).thenReturn(expected);

      CommandResult result = controller.handle(cmd, ctx);

      assertEquals(expected, result);
      verify(inventoryService).heal(ctx, null);
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
