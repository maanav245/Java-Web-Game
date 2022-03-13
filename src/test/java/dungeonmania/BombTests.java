package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.*;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class BombTests {
        @Test
        public void testBombItemPickedUpByPlayer() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("bombTest", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                // player moves UP to get bomb
                DungeonResponse dungeon = dc.tick(null, Direction.UP);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());
                // check bomb has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> bombE = player.getInventory().getIfContains("bomb");
                // check that the player has a bomb in inventory
                assertTrue(bombE.size() == 1);
        }

        @Test
        public void testBombCanBePlacedNearSwitchByPlayer() {
                // tick("bomb", direction);
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("bombTest", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                // player moves UP to get bomb
                dc.tick(null, Direction.UP);
                // check bomb has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> bombE = player.getInventory().getIfContains("bomb");
                // check that the player has a bomb in inventory
                assertTrue(bombE.size() == 1);
                String bombId = bombE.get(0).getId();
                // move player in position to place bomb cardinally adjacent to switch
                dc.tick(null, Direction.UP);
                DungeonResponse dungeon = dc.tick(null, Direction.RIGHT);
                // place bomb

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());

                dc.tick(bombE.get(0).getId(), Direction.RIGHT);
                // check bomb is now within map
                List<Entity> bombEMap = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("bomb")).collect(Collectors.toList());
                assertEquals(1, bombEMap.size());
                Position placedBombPos = bombEMap.get(0).getPosition();
                assertTrue(placedBombPos.equals(new Position(2, 0)));
                List<Entity> switchE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("switch")).collect(Collectors.toList());
                // checks switch is adjacent
                assertEquals(1, switchE.size());
                assertTrue(switchE.get(0).getPosition().getAdjacentPositions().contains(placedBombPos));
        }

        @Test
        public void testBombExplodesAndKillsAllNearbyCharactersExceptPlayer() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("bombTest", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                currDungeon.getGameModeFactory().setMaxSpiders(1);
                // player moves UP to get bomb
                dc.tick(null, Direction.UP);
                // check bomb has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> bombE = player.getInventory().getIfContains("bomb");
                // check spider on map
                List<Entity> spiderE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("spider")).collect(Collectors.toList());
                assertEquals(spiderE.size(), 1);
                // check that the player has a bomb in inventory
                assertTrue(bombE.size() == 1);
                String bombId = bombE.get(0).getId();
                // move player in position to place bomb cardinally adjacent to switch
                dc.tick(null, Direction.UP);
                dc.tick(null, Direction.RIGHT);
                // place bomb
                dc.tick(bombId, Direction.RIGHT);
                // check bomb is now within map
                List<Entity> bombEMap = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("bomb")).collect(Collectors.toList());
                assertEquals(bombEMap.size(), 1);
                Position placedBombPos = bombEMap.get(0).getPosition();
                assertTrue(placedBombPos.equals(new Position(2, 0)));
                Entity switchE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("switch"))
                                .collect(Collectors.toList()).get(0);
                // checks switch is adjacent
                switchE.getPosition().getAdjacentPositions().contains(placedBombPos);
                System.out.println(switchE.getPosition().getAdjacentPositions());
                // move player in position to trigger explosion by pushing boulder on switch
                dc.tick(null, Direction.LEFT);
                dc.tick(null, Direction.LEFT);
                dc.tick(null, Direction.DOWN);
                // This last movement pushes boulder onto switch to cause explosion
                exitgame = dc.tick(null, Direction.RIGHT);

                exitgame = dc.saveGame(exitgame.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                exitgame = dc.loadGame(exitgame.getDungeonId());
                // check nearby spider is no longer on map
                List<EntityResponse> spiderER = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("spider")).collect(Collectors.toList());
                // TODO: rewrite so id is unique to spider initially
                assertEquals(spiderER.size(), 0);
        }

}
