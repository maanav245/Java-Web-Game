package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TimeItemsTests {

   @Test
   public void testItemsSimple() {
      DungeonManiaController dc = new DungeonManiaController();
      DungeonResponse d1 = dc.newGame("timeSimple", "standard");
      dc.tick(null, Direction.LEFT);
      dc.tick(null, Direction.UP);
      DungeonResponse dungeon = dc.tick(null, Direction.RIGHT);

      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      dungeon = dc.rewind(1);
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "sword").size());
      assertEquals(6, TestHelpers.getInventoryResponseList(dungeon, "armour").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "treasure").size());
      assertEquals(3, TestHelpers.getInventoryResponseList(dungeon, "arrow").size());
      assertEquals(3, TestHelpers.getInventoryResponseList(dungeon, "wood").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "key").size());

   }

   @Test
   public void testBuildableSimple() {
      DungeonManiaController dc = new DungeonManiaController();
      DungeonResponse d1 = dc.newGame("timeSimple", "standard");
      dc.tick(null, Direction.LEFT);
      DungeonResponse dungeon = dc.tick(null, Direction.UP);

      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "sword").size());
      assertEquals(6, TestHelpers.getInventoryResponseList(dungeon, "armour").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "treasure").size());
      assertEquals(3, TestHelpers.getInventoryResponseList(dungeon, "arrow").size());
      assertEquals(3, TestHelpers.getInventoryResponseList(dungeon, "wood").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "key").size());
      dc.build("bow");
      dc.build("shield");
      dungeon = dc.tick(null, Direction.RIGHT);
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      dungeon = dc.rewind(1);
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "sword").size());
      assertEquals(6, TestHelpers.getInventoryResponseList(dungeon, "armour").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").size());
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "treasure").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "arrow").size());
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "wood").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "key").size());
   }

   public void testInteractSimple() {
      DungeonManiaController dc = new DungeonManiaController();
      DungeonResponse d1 = dc.newGame("timeSimple", "standard");
      dc.tick(null, Direction.LEFT);
      DungeonResponse dungeon = dc.tick(null, Direction.UP);

      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "sword").size());
      assertEquals(6, TestHelpers.getInventoryResponseList(dungeon, "armour").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "treasure").size());
      assertEquals(3, TestHelpers.getInventoryResponseList(dungeon, "arrow").size());
      assertEquals(3, TestHelpers.getInventoryResponseList(dungeon, "wood").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "key").size());
      dc.build("bow");
      dc.build("shield");
      dungeon = dc.tick(null, Direction.RIGHT);
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      dungeon = dc.rewind(1);
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "sword").size());
      assertEquals(6, TestHelpers.getInventoryResponseList(dungeon, "armour").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").size());
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "treasure").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "arrow").size());
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "wood").size());
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "key").size());
   }

}
