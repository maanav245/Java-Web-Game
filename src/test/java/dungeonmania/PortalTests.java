package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class PortalTests {
    @Test
    public void testPortalLoads() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("portals", "hard");

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        List<EntityResponse> portals = TestHelpers.getEntityResponseList(dungeon, "portal_blue");

        assertEquals(2, portals.size());
    }

    @Test
    public void testPortalSaves() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon1 = dc.newGame("portals", "standard");
        dungeon1 = dc.saveGame(dungeon1.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon1 = dc.loadGame(dungeon1.getDungeonId());
        assertEquals(2, TestHelpers.getEntityResponseList(dungeon1, "portal_blue").size());
    }

    @Test
    public void testSimplePortal() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("portals", "standard");

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 0));

        dungeon = dc.tick(null, Direction.RIGHT);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(5, 0));

        dungeon = dc.tick(null, Direction.LEFT);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 0));

        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.DOWN);

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(4, 1));

        dungeon = dc.tick(null, Direction.UP);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(1, -1));
    }

    @Test
    public void testTwoPortals() throws InterruptedException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("portals2", "standard");

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 0));

        dungeon = dc.tick(null, Direction.RIGHT);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(5, 0));

        dc.saveGame(dungeon.getDungeonId());
        // Thread.sleep(1000);
        dc.loadGame(dungeon.getDungeonId());

        dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.LEFT);
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(2, 3));

        // double portal
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.UP);

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());
        playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(1, -1));
    }
}
