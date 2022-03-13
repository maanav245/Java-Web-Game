package dungeonmania;

import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Dungeon;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.models.MovingEntities.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class newBuildableTest {

    @Test
    public void SunStoneAsTreasureBuildTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("items", "peaceful");
        // collect 2 wood + 1 treasure to build bow
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);

        dr2 = dc.saveGame(dr2.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr2 = dc.loadGame(dr2.getDungeonId());
        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "sun_stone").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr2, "treasure").size());
        assertEquals(3, TestHelpers.getInventoryResponseList(dr2, "wood").size());
        assertEquals(4, TestHelpers.getInventoryResponseList(dr2, "arrow").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr2, "key").size());

        // build item using treasure
        dc.build("shield");
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        dr3 = dc.saveGame(dr3.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dr3.getDungeonId());
        // check shield in inventory and ingredients are not
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "wood").size());
        assertEquals(4, TestHelpers.getInventoryResponseList(dr3, "arrow").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "shield").size());
        List<String> expectedList = new ArrayList<>();
        expectedList.add("bow");
        expectedList.add("shield");
        assertEquals(expectedList, dr2.getBuildables());
    }

    @Test
    public void buildSceptreWithWoodTest() {
        // test crafting with 1 wood + treasure + sunstone
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("items", "peaceful");
        // collect 2 wood + 1 treasure to build bow
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);

        dr2 = dc.saveGame(dr2.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr2 = dc.loadGame(dr2.getDungeonId());

        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "treasure").size());
        assertEquals(3, TestHelpers.getInventoryResponseList(dr2, "wood").size());
        assertEquals(4, TestHelpers.getInventoryResponseList(dr2, "arrow").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "key").size());

        // build item using treasure
        dc.build("sceptre");
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        dr3 = dc.saveGame(dr3.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dr3.getDungeonId());

        // check shield in inventory and ingredients are not
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "treasure").size());
        assertEquals(2, TestHelpers.getInventoryResponseList(dr3, "wood").size());
        assertEquals(4, TestHelpers.getInventoryResponseList(dr3, "arrow").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "sceptre").size());
        List<String> expectedList = new ArrayList<>();
        expectedList.add("bow");
        expectedList.add("shield");
        expectedList.add("sceptre");
        expectedList.add("midnight_armour");
        assertEquals(expectedList, dr2.getBuildables());
    }

    @Test
    public void buildSceptreWithWoodKeyTest() {
        // test crafting with 1 wood + key + sunstone
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("items", "peaceful");
        // collect 2 wood + 1 treasure to build bow
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);

        dr2 = dc.saveGame(dr2.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr2 = dc.loadGame(dr2.getDungeonId());

        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "sun_stone").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr2, "treasure").size());
        assertEquals(3, TestHelpers.getInventoryResponseList(dr2, "wood").size());
        assertEquals(4, TestHelpers.getInventoryResponseList(dr2, "arrow").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "key").size());
        // build item using key
        dc.build("sceptre");
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        dr3 = dc.saveGame(dr3.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dr3.getDungeonId());

        // check shield in inventory and ingredients are not
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "sun_stone").size());
        assertEquals(2, TestHelpers.getInventoryResponseList(dr3, "wood").size());
        assertEquals(4, TestHelpers.getInventoryResponseList(dr3, "arrow").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "sceptre").size());
        List<String> expectedList = new ArrayList<>();
        expectedList.add("bow");
        expectedList.add("shield");
        expectedList.add("sceptre");
        expectedList.add("midnight_armour");
        assertEquals(expectedList, dr2.getBuildables());
    }

    @Test
    public void buildSceptreWithArrowsTest() throws InterruptedException {
        // test crafting with 2 arrows + treasure + sunstone
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("items", "peaceful").getDungeonId();
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);

        dr2 = dc.saveGame(dr2.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr2 = dc.loadGame(dr2.getDungeonId());

        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "treasure").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr2, "wood").size());
        assertEquals(3, TestHelpers.getInventoryResponseList(dr2, "arrow").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "key").size());
        // build item using key
        dc.build("sceptre");
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);
        // check shield in inventory and ingredients are not
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "arrow").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "treasure").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "sceptre").size());
        List<String> expectedList = new ArrayList<>();
        expectedList.add("sceptre");
        assertEquals(expectedList, dr2.getBuildables());

        DungeonResponse dr4 = dc.saveGame(id);
        Thread.sleep(1000);
        DungeonResponse dr5 = dc.loadGame(id);
        assertEquals(0, TestHelpers.getInventoryResponseList(dr5, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr5, "arrow").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr5, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr5, "treasure").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr5, "sceptre").size());
    }

    @Test
    public void buildSceptreWithArrowsKeyTest() {
        // test crafting with 2 arrows + key + sunstone
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("items", "peaceful");
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);

        dr2 = dc.saveGame(dr2.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr2 = dc.loadGame(dr2.getDungeonId());

        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "sun_stone").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr2, "treasure").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr2, "wood").size());
        assertEquals(3, TestHelpers.getInventoryResponseList(dr2, "arrow").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "key").size());
        // build item using key
        dc.build("sceptre");
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        dr3 = dc.saveGame(dr3.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dr3.getDungeonId());

        // check shield in inventory and ingredients are not
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "arrow").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "key").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "sceptre").size());
        List<String> expectedList = new ArrayList<>();
        expectedList.add("sceptre");
        assertEquals(expectedList, dr2.getBuildables());
    }

    @Test
    public void buildMidnightArmourTest() throws InterruptedException {
        // test crafting with 1 armour + sunstone
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("items", "peaceful").getDungeonId();
        // collect 1 armour + sunstone to build midnight_armour
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);
        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "armour").size());
        dc.build("midnight_armour");
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);
        // check shield in inventory and ingredients are not
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "sun_stone").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr3, "armour").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr3, "midnight_armour").size());

        DungeonResponse dr4 = dc.saveGame(id);
        Thread.sleep(1000);
        DungeonResponse dr5 = dc.loadGame(id);
        assertEquals(0, TestHelpers.getInventoryResponseList(dr5, "sun_stone").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr5, "armour").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr5, "midnight_armour").size());
    }

    @Test
    public void MidnightArmourCantBuildWithZombiesTest() throws InterruptedException {
        // test crafting with 1 armour + sunstone
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("items", "standard").getDungeonId();
        Dungeon dr = dc.getDungeon(id);

        Zombie z1 = new StandardZombie(UUID.randomUUID().toString(), new Position(0, 1));
        Zombie z2 = new StandardZombie(UUID.randomUUID().toString(), new Position(0, 2));
        dr.addEntity(z1);
        dr.addEntity(z2);

        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);
        // check that ingredients are inventory
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr2, "armour").size());
        assertThrows(InvalidActionException.class, () -> dc.build("midnight_armour"));

        dc.saveGame(id);
        Thread.sleep(500);
        DungeonResponse dr5 = dc.loadGame(id);
        assertEquals(1, TestHelpers.getInventoryResponseList(dr5, "sun_stone").size());
        assertEquals(1, TestHelpers.getInventoryResponseList(dr5, "armour").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dr5, "midnight_armour").size());
    }

}
