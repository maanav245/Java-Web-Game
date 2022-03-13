package dungeonmania;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Inventory;
import dungeonmania.models.StaticEntities.Arrows;
import dungeonmania.models.StaticEntities.Treasure;
import dungeonmania.models.StaticEntities.Wood;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.exceptions.InvalidActionException;

public class BuildableTest {

    @Test
    void buildThrowIllegalArgumentExceptionTest() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("items", "peaceful");
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        // IllegalArgumentException: if build is called on an item not shield or bow
        assertThrows(IllegalArgumentException.class, () -> dc.build("sword"));
    }

    @Test
    void buildInvalidActionExceptionTest() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("items", "peaceful");
        dc.tick(null, Direction.NONE);
        assertThrows(InvalidActionException.class, () -> dc.build("bow"));
        assertThrows(InvalidActionException.class, () -> dc.build("shield"));
        assertThrows(InvalidActionException.class, () -> dc.build("sceptre"));
        assertThrows(InvalidActionException.class, () -> dc.build("midnight_armour"));
    }

    @Test
    public void buildBowTest() throws InvalidActionException, IllegalArgumentException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon1 = dc.newGame("items", "peaceful");
        // collect 1 wood + 3 arrows to build bow
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dungeon1 = dc.tick(null, Direction.RIGHT);
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        // check that ingredients are inventory
        assertEquals(3, TestHelpers.getInventoryResponseList(dr, "wood").size());
        assertEquals(3, TestHelpers.getInventoryResponseList(dr, "arrow").size());
        // build item
        dc.build("bow");
        dungeon1 = dc.saveGame(dr.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon1 = dc.loadGame(dr.getDungeonId());

        dungeon1 = dc.tick(null, Direction.NONE);

        // check bow in inventory and ingredients are not
        assertEquals(2, TestHelpers.getInventoryResponseList(dungeon1, "wood").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dungeon1, "arrow").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dungeon1, "bow").size());
    }

    @Test
    void buildShieldTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("items", "peaceful");
        // collect 2 wood + 1 treasure to build shield
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);
        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "treasure").size());
        assertEquals(3, TestHelpers.getInventoryResponseList(dr2, "wood").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "key").size());

        // build item using treasure
        dc.build("shield");
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);
        // check bow in inventory and ingredients are not

        dr3 = dc.saveGame(dr.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dr.getDungeonId());

        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "wood").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "treasure").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "shield").size());
        List<String> expectedList = new ArrayList<>();
        expectedList.add("bow");
        expectedList.add("shield");
        expectedList.add("midnight_armour");
        assertEquals(expectedList, dr2.getBuildables());
    }

    @Test
    void buildShieldKeyTest() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("items", "peaceful");
        // collect 2 wood + 1 treasure to build bow
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        // check that ingredients are inventory
        assertEquals(3, TestHelpers.getInventoryResponseList(dr, "wood").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr, "treasure").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr, "key").size());

        // build item using treasure
        dc.build("shield");
        DungeonResponse dr2 = dc.tick(null, Direction.NONE);
        // check bow in inventory and ingredients are not
        dr2 = dc.saveGame(dr.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr2 = dc.loadGame(dr.getDungeonId());

        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "wood").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr2, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "shield").size());

        List<String> expectedList = new ArrayList<>();
        expectedList.add("bow");
        expectedList.add("shield");
        expectedList.add("midnight_armour");
        assertEquals(expectedList, dr.getBuildables());
    }

    @Test
    void buildablesListTest() {
        int x = 0;
        int y = 0;
        Inventory inventory = new Inventory();

        Entity w1 = new Wood("1", new Position(x, y));
        Entity w2 = new Wood("2", new Position(x, y));
        Entity a1 = new Arrows("3", new Position(x, y));
        Entity a2 = new Arrows("4", new Position(x, y));
        Entity a3 = new Arrows("5", new Position(x, y));
        Entity t1 = new Treasure("6", new Position(x, y));

        inventory.addToInventory(w1);
        inventory.addToInventory(w2);
        inventory.addToInventory(a1);
        inventory.addToInventory(a2);
        inventory.addToInventory(a3);
        inventory.addToInventory(t1);

        List<String> expectedList = new ArrayList<>();
        expectedList.add("bow");
        expectedList.add("shield");
        expectedList.add("midnight_armour");
        assertEquals(expectedList, inventory.getBuildables());
    }

}
