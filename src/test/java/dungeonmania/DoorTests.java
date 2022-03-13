package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Entity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class DoorTests {
    @Test
    public void testDoorAndKeySpawn() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        List<EntityResponse> doors = TestHelpers.getEntityResponseList(dungeon, "door");
        List<EntityResponse> keys = TestHelpers.getEntityResponseList(dungeon, "key");

        assertEquals(2, doors.size());
        assertEquals(2, keys.size());
    }

    @Test
    public void testSimplePlayerUnlocksDoor() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 0));

        // check key is picked up
        dungeon = dc.tick(null, Direction.DOWN);
        List<EntityResponse> keys = TestHelpers.getEntityResponseList(dungeon, "key");
        assertEquals(1, keys.size());
        List<ItemResponse> keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(1, keysInventory.size());

        // test player can stand on and move through door
        dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(2, 1));
        dungeon = dc.tick(null, Direction.RIGHT);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(3, 1));
    }

    @Test
    public void testPlayerNoKey() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);
        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(1, 1));
    }

    @Test
    public void testPlayerWrongKey() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(1, 3));
    }

    @Test
    public void testDoorRemainsOpen() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.LEFT);
        dungeon = dc.tick(null, Direction.LEFT);

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 1));

    }

    @Test
    public void testPlayerCanOnlyPickUpOneKey() throws InterruptedException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        // move to collect both keys
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);

        dungeon = dc.tick(null, Direction.DOWN);

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(4, 3));

        List<EntityResponse> keys = TestHelpers.getEntityResponseList(dungeon, "key");
        assertEquals(1, keys.size());
        List<ItemResponse> keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(1, keysInventory.size());
    }

    @Test
    public void testKeyDisapearsOnceUsed() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 0));

        // check key is picked up
        dungeon = dc.tick(null, Direction.DOWN);
        List<ItemResponse> keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(1, keysInventory.size());

        // test key is removed from Inventory
        dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(2, 1));
        keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(0, keysInventory.size());
        dc.saveGame(dungeon.getDungeonId());
    }

    @Test
    public void testTwoDoors() throws InterruptedException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        dungeon = dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.RIGHT);
        List<ItemResponse> keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(1, keysInventory.size());
        dungeon = dc.saveGame(dungeon.getDungeonId());
        // Thread.sleep(500);
        dungeon = dc.loadGame(dungeon.getDungeonId());
        keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(1, keysInventory.size());

        dungeon = dc.tick(null, Direction.RIGHT);
        dungeon = dc.saveGame(dungeon.getDungeonId());
        // Thread.sleep(500);
        dungeon = dc.loadGame(dungeon.getDungeonId());
        keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(0, keysInventory.size());

        dungeon = dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.DOWN);

        dungeon = dc.tick(null, Direction.LEFT);
        dungeon = dc.tick(null, Direction.LEFT);
        dungeon = dc.tick(null, Direction.LEFT);

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // Thread.sleep(500);
        dungeon = dc.loadGame(dungeon.getDungeonId());

        dungeon = dc.tick(null, Direction.LEFT);

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 3));
        keysInventory = TestHelpers.getInventoryResponseList(dungeon, "key");
        assertEquals(0, keysInventory.size());

    }

    @Test
    public void testTwoDoorsSunStone() throws InterruptedException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("doorsSimple", "standard");

        dungeon = dc.tick(null, Direction.UP);
        dungeon = dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.DOWN);
        List<ItemResponse> sunstoneInventory = TestHelpers.getInventoryResponseList(dungeon, "sun_stone");
        assertEquals(1, sunstoneInventory.size());
        dungeon = dc.saveGame(dungeon.getDungeonId());
        // Thread.sleep(500);
        dungeon = dc.loadGame(dungeon.getDungeonId());
        sunstoneInventory = TestHelpers.getInventoryResponseList(dungeon, "sun_stone");
        assertEquals(1, sunstoneInventory.size());

        dungeon = dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.DOWN);

        dungeon = dc.tick(null, Direction.LEFT);
        dungeon = dc.tick(null, Direction.LEFT);
        dungeon = dc.tick(null, Direction.LEFT);

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 3));
        sunstoneInventory = TestHelpers.getInventoryResponseList(dungeon, "sun_stone");
        assertEquals(1, sunstoneInventory.size());
    }

}
