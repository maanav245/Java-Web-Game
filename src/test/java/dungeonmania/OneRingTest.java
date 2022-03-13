package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.*;
import dungeonmania.models.Battle.Battle;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.StaticEntities.TheOneRing;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class OneRingTest {
        @Test
        public void testIfPlayerHasOneRingAndDiesRespawnsInsteadOfGameEnd() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("oneRingTest", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);

                // player moves UP to get one_ring
                dc.tick(null, Direction.UP);
                // check one_ring has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> one_ringE = player.getInventory().getIfContains("one_ring");
                // check that the player has a one_ring in inventory
                player.setNewRandomPositionSeed(500);
                assertTrue(one_ringE.size() == 1);

                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                player = (Player) playerE;
                DoBattle mercenary = (DoBattle) mercenaryE;
                // player loses battle if mercenary is 45 normally
                mercenary.setHP(45);

                // since mercenary is not an ally, player moving closer to player to trigger p
                dc.tick(null, Direction.RIGHT);
                dc.tick(null, Direction.DOWN);

                DungeonResponse dungeon = dc.tick(null, Direction.DOWN);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) { // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());

                List<Entity> playerL = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());
                // check player is still in the Dungeon position, after loss when ordinarily be
                // removed
                assertEquals(playerL.size(), 1);

                // test that the one_ring has been removed from the player's inventory
                player = (Player) playerL.get(0);
                assertTrue(player.getInventory().getIfContains("one_ring").size() == 0);

        }

}
