package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SwampTileTests {
    @Test
    public void testPlayerNotEffectedbySwampTile() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("swapTile", "standard");

        dungeon = dc.tick(null, Direction.DOWN);
        dungeon = dc.tick(null, Direction.DOWN);

        Position playerPosition = TestHelpers.getPlayerPosition(dungeon);
        assertEquals(playerPosition, new Position(0, 2));
    }

    @Test
    public void MecrenaryBlockedBySwampTile() throws InterruptedException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("swapTile", "standard");

        List<EntityResponse> mercs = TestHelpers.getEntityResponseList(dungeon, "mercenary");
        String id1 = "";
        String id2 = "";

        for (EntityResponse merc : mercs) {
            if (merc.getPosition().getX() == 3 && merc.getPosition().getY() == 0) {
                id1 = merc.getId();
            }
            if (merc.getPosition().getX() == 0 && merc.getPosition().getY() == 2) {
                id2 = merc.getId();
            }
        }

        dungeon = dc.tick(null, Direction.NONE);
        dungeon = dc.tick(null, Direction.NONE);
        dungeon = dc.tick(null, Direction.NONE);

        assertEquals(TestHelpers.getEntityFromID(dungeon, id1).getPosition(), new Position(1, 1));
        assertEquals(TestHelpers.getEntityFromID(dungeon, id2).getPosition(), new Position(1, 0));

        dungeon = dc.tick(null, Direction.NONE);
        assertEquals(TestHelpers.getEntityFromID(dungeon, id1).getPosition(), new Position(1, 0));
        assertEquals(TestHelpers.getEntityFromID(dungeon, id2).getPosition(), new Position(1, 0));

        dungeon = dc.tick(null, Direction.NONE);

        dungeon = dc.saveGame("swamptile_test");
        Thread.sleep(500);
        dungeon = dc.loadGame("swamptile_test");

        dungeon = dc.tick(null, Direction.NONE);

        List<EntityResponse> mercenaries = TestHelpers.getEntityResponseList(dungeon, "mercenary");
        List<EntityResponse> player = TestHelpers.getEntityResponseList(dungeon, "player");
        assertEquals(1, mercenaries.size());
        assertEquals(0, player.size());
    }

}
