package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Player;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MovementTest {

    @Test
    public void testSpiderCircle() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("enemiesBasic", "standard");

        Position spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(1, 2));

        dungeon = dc.tick(null, Direction.LEFT);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(1, 1));

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(2, 1));

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(2, 2));

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(2, 3));

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(1, 3));

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(0, 3));

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(0, 2));

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(0, 1));

        dungeon = dc.tick(null, Direction.NONE);
        spiderPosition = TestHelpers.getEntityResponseList(dungeon, "spider").get(0).getPosition();
        assertEquals(spiderPosition, new Position(1, 1));

    }

    @Test
    public void testSpiderBoulders() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("spiderBoulders", "standard");

        List<EntityResponse> spiders = TestHelpers.getEntityResponseList(dungeon, "spider");
        String id1 = "", id2 = "", id3 = "";

        for (EntityResponse spider : spiders) {
            if (spider.getPosition().getX() == 3) {
                if (spider.getPosition().getY() == 1) {
                    id1 = spider.getId();
                } else {
                    id2 = spider.getId();
                }
            } else {
                id3 = spider.getId();
            }
        }

        /*
         * Spider1 gets trapped after moving up spider2 should go anticlockwise spider3
         * should get trapped at start
         */

        Position spider1 = TestHelpers.getEntityFromID(dungeon, id1).getPosition();
        Position spider2 = TestHelpers.getEntityFromID(dungeon, id2).getPosition();
        Position spider3 = TestHelpers.getEntityFromID(dungeon, id3).getPosition();
        assertEquals(spider1, new Position(3, 1));
        assertEquals(spider2, new Position(3, 4));
        assertEquals(spider3, new Position(1, 3));

        dungeon = dc.tick(null, Direction.NONE);
        spider1 = TestHelpers.getEntityFromID(dungeon, id1).getPosition();
        spider2 = TestHelpers.getEntityFromID(dungeon, id2).getPosition();
        spider3 = TestHelpers.getEntityFromID(dungeon, id3).getPosition();
        assertEquals(spider1, new Position(3, 0));
        assertEquals(spider2, new Position(3, 5));
        assertEquals(spider3, new Position(1, 3));

        dungeon = dc.tick(null, Direction.NONE);
        spider1 = TestHelpers.getEntityFromID(dungeon, id1).getPosition();
        spider2 = TestHelpers.getEntityFromID(dungeon, id2).getPosition();
        spider3 = TestHelpers.getEntityFromID(dungeon, id3).getPosition();
        assertEquals(spider1, new Position(3, 0));
        assertEquals(spider2, new Position(4, 5));
        assertEquals(spider3, new Position(1, 3));

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        dungeon = dc.tick(null, Direction.NONE);
        spider1 = TestHelpers.getEntityFromID(dungeon, id1).getPosition();
        spider2 = TestHelpers.getEntityFromID(dungeon, id2).getPosition();
        spider3 = TestHelpers.getEntityFromID(dungeon, id3).getPosition();
        assertEquals(spider1, new Position(3, 0));
        assertEquals(spider2, new Position(4, 4));
        assertEquals(spider3, new Position(1, 3));

        dungeon = dc.tick(null, Direction.NONE);
        spider2 = TestHelpers.getEntityFromID(dungeon, id2).getPosition();
        assertEquals(spider2, new Position(4, 3));

        dungeon = dc.tick(null, Direction.NONE);
        spider2 = TestHelpers.getEntityFromID(dungeon, id2).getPosition();
        assertEquals(spider2, new Position(4, 4));

    }

    @Test
    public void testMoveTowardsAboveAndLeft() {

        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("advanced", "standard");

        Position mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(3, 5));

        dungeon = dc.tick(null, Direction.RIGHT);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(2, 5));

        dc.saveGame("test");

        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(3, 1));

        dungeon = dc.tick(null, Direction.RIGHT);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(4, 1));

        dungeon = dc.tick(null, Direction.DOWN);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(5, 1));

        for (int i = 0; i < 7; i++)
            dungeon = dc.tick(null, Direction.RIGHT);
        dungeon = dc.tick(null, Direction.RIGHT);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(13, 1));

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        for (int i = 0; i < 3; i++)
            dungeon = dc.tick(null, Direction.DOWN);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(16, 1));

        for (int i = 0; i < 5; i++)
            dungeon = dc.tick(null, Direction.DOWN);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(16, 6));

        dc.tick(null, Direction.LEFT);
        dungeon = dc.tick(null, Direction.RIGHT);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(15, 7));

        dungeon = dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.DOWN);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(15, 10));
    }

    @Test
    public void testMoveTowardsCircleAround() throws InterruptedException {

        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("advanced", "standard");
        String dungeonId = dungeon.getDungeonId();
        Dungeon dun = dc.getDungeon(dungeonId);
        Player player = dun.getPlayer();
        player.setHP(8000);

        Position mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(3, 5));

        for (int i = 0; i < 10; i++)
            dc.tick(null, Direction.RIGHT);
        for (int i = 0; i < 12; i++)
            dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        for (int i = 0; i < 5; i++)
            dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.UP);

        dc.saveGame(dungeon.getDungeonId());
        Thread.sleep(1000);
        dc.loadGame(dungeon.getDungeonId());

        for (int i = 0; i < 2; i++)
            dc.tick(null, Direction.LEFT);
        for (int i = 0; i < 3; i++)
            dc.tick(null, Direction.UP);
        for (int i = 0; i < 2; i++)
            dc.tick(null, Direction.LEFT);
        for (int i = 0; i < 8; i++)
            dc.tick(null, Direction.UP);

        dungeon = dc.tick(null, Direction.UP);
        mercenary = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getPosition();
        assertEquals(mercenary, new Position(2, 3));

    }

    @Test
    public void testMoveAway() throws InterruptedException, IOException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("advanced", "standard");
        String mercenaryId = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getId();
        dungeon = dc.tick(null, Direction.RIGHT);
        ItemResponse invince = TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").get(0);
        dungeon = dc.tick(invince.getId(), Direction.NONE);
        for (int i = 0; i < 8; i++)
            dungeon = dc.tick(null, Direction.DOWN);

        dc.saveGame(dungeon.getDungeonId());
        Thread.sleep(1000);
        dc.loadGame(dungeon.getDungeonId());

        for (int i = 0; i < 5; i++)
            dc.tick(null, Direction.RIGHT);
        for (int i = 0; i < 4; i++)
            dc.tick(null, Direction.UP);
        dungeon = dc.tick(null, Direction.UP);
        Position mercenary = TestHelpers.getEntityFromID(dungeon, mercenaryId).getPosition();
        assertEquals(mercenary, new Position(2, 10));
    }

}
