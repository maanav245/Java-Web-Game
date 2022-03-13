package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class BoulderAndSwitchTests {

        @Test
        public void testBoulderMovementSimple() {
                // Start game
                DungeonManiaController dc = new DungeonManiaController();
                dc.newGame("boulders", "standard");
                // Move boulder
                DungeonResponse response = dc.tick(null, Direction.RIGHT);
                // Check if boulder moved
                response = dc.saveGame(response.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                response = dc.loadGame(response.getDungeonId());
                assertTrue(response.getEntities().stream()
                                .anyMatch(entity -> entity.getPosition().equals(new Position(4, 2))));
        }

        @Test
        public void testBoulderMovementBlockedByWall() {
                // Start game
                DungeonManiaController dc = new DungeonManiaController();
                dc.newGame("bouldersWall", "standard");
                // Move player
                DungeonResponse response = dc.tick(null, Direction.UP);
                response = dc.saveGame(response.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                response = dc.loadGame(response.getDungeonId());
                assertTrue(TestHelpers.getEntityResponseList(response, "boulder").stream()
                                .anyMatch(entity -> entity.getPosition().equals(new Position(0, 1))));
        }

        @Test
        public void testBoulderMovementSwitch() {
                // Start game
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse dr = dc.newGame("bouldersExit", "standard");
                String dungeonId = dr.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                currDungeon.getGameModeFactory().setMaxSpiders(0);

                assertTrue(TestHelpers.getEntityResponseList(dr, "switch").size() > 0);
                // Move boulder
                DungeonResponse response = dc.tick(null, Direction.RIGHT);
                response = dc.saveGame(response.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                response = dc.loadGame(response.getDungeonId());
                dc.saveGame(response.getDungeonId());
                assertTrue(TestHelpers.getEntityResponseList(response, "boulder").stream()
                                .anyMatch(entity -> entity.getPosition().equals(new Position(2, 1))));
                assertTrue(TestHelpers.getEntityResponseList(response, "switch").stream()
                                .anyMatch(entity -> entity.getPosition().equals(new Position(2, 1))));
        }

}