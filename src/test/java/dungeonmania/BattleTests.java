package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.Battle.Battle;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.Goals.GoalComponent;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.StaticEntities.Wieldable;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class BattleTests {

        @Test
        public void testUnitTestStartBattlePlayerWinsReducesEntity() throws InterruptedException {

                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();

                // check that the goal is NOT exit only
                // System.out.println(exitgame.getGoals());
                assertFalse(exitgame.getGoals().equals("exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // mercenrary was on the RIGHT of player, so player moves DOWN to avoid movement
                // observer triggering battle
                // battle will be simulated without being on same position using static
                // Note: once Character observer is implemented, a battle may be triggered by
                // the mercenary's Character movement strategy itself

                DungeonResponse dungeon = dc.tick(null, Direction.DOWN);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());

                Dungeon currDungeon = dc.getDungeon(dungeonId);

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);

                Player player = (Player) playerE;
                DoBattle mercenary = (DoBattle) mercenaryE;
                // assert a loser is returned
                Entity losingEntity = Battle.startBattle(player, (DoBattle) mercenary);
                assertTrue(losingEntity != null);
                // Mercenary is loser as single battle, no armour, player has high HP
                assertTrue(losingEntity instanceof Mercenary);

        }

        @Test
        public void testIntegrationWithGameObserverEnemyDies() throws InterruptedException {
                // exactly the same scenario as above except rely on Observer to start battle
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Thread.sleep(600);

                // assertTrue(DungeonManiaController.dungeons().contains("enemiesExit"));
                // check game is created with name "3x3maze"

                // assertTrue(dc.allGames().contains(exitgame.getDungeonId()));

                // check that the goal is NOT exit only
                // System.out.println(exitgame.getGoals());
                assertFalse(exitgame.getGoals().equals("exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // mercenrary was on the RIGHT of player, so player moves RIGHT first "tick" to
                // start battle
                DungeonResponse dungeon = dc.tick(null, Direction.RIGHT);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());

                Dungeon currDungeon = dc.getDungeon(dungeonId);
                List<Entity> mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList());

                // assert that the current Dungeon no longer has the losing Entity
                assertTrue(mercenaryE.size() == 0);

        }

        @Test
        public void testIntegrationWithGameObserverPlayerDies() throws InterruptedException {
                // exactly the same scenario as above except rely on Observer to start battle
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                // Thread.sleep(600);
                // assertTrue(DungeonManiaController.dungeons().contains("enemiesExit"));

                // check that the goal is NOT exit only
                // System.out.println(exitgame.getGoals());
                assertFalse(exitgame.getGoals().equals("exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // mercenrary was on the RIGHT of player, so player moves RIGHT first "tick" to
                // start battle
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                DoBattle mercenary = (DoBattle) mercenaryE;
                // set mercenary HP high to ensure player loses
                mercenary.setHP(1000);

                // player moves RIGHT to trigger battle
                dc.tick(null, Direction.RIGHT);

                List<Entity> playerE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());

                // assert that the current Dungeon no longer has the losing Entity
                assertTrue(playerE.size() == 0);

        }

        @Test
        public void testEnemiesGoalCompleteWhenCharactersAllVanquished() {
                // If you stop returning any goals (i.e. empty string) it'll say the game has
                // been completed
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("mercBasic", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currGame = dc.getDungeon(dungeonId);
                currGame.getGameModeFactory().setMaxSpiders(0);

                // assertTrue(DungeonManiaController.dungeons().contains("mercBasic"));

                // spider was on the RIGHT of player, so first "tick" should have
                // Character movement resulting in a battle
                dc.tick(null, Direction.RIGHT);

                // since single Spider HP is low, Player should win & enemies Goal should be
                // complete

                GoalComponent goal = currGame.getGoals();
                // startBattle & removing the losingEntity, the mercenary
                assertTrue(dc.isDungeonComplete(dungeonId));
        }

        @Test
        public void testPlayerLosesBattle() {
                // 'enemiesBasic' has one of every enemy; should eventually lose
                // If you no longer give an entity object for a player to the frontend it'll say
                // the game has been lost
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();

                List<Entity> PlayerE = dc.getDungeon(dungeonId).getEntities().stream()
                                .filter(ent -> ent.getType() == "player").collect(Collectors.toList());

                Entity mercenaryE = dc.getDungeon(dungeonId).getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);

                Player player = (Player) PlayerE.get(0);
                DoBattle mercenary = (DoBattle) mercenaryE;
                // set mercenary HP to something crazy high to ensure player will lose
                mercenary.setHP(100000);

                // simulate a battle
                Entity losingEntity = Battle.startBattle(player, (DoBattle) mercenary);

                // assert Player no longer an entity in Dungeon because HP of player has reached
                // 0, meaning frontend will consider Game Lost
                assertTrue(losingEntity instanceof Player);

        }

        @Test
        public void testGameLostPlayerHPZero() {
                // 'enemiesBasic' has one of every enemy; should eventually lose
                // If you no longer give an entity object for a player to the frontend it'll say
                // the game has been lost
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();

                List<Entity> PlayerE = dc.getDungeon(dungeonId).getEntities().stream()
                                .filter(ent -> ent.getType() == "player").collect(Collectors.toList());

                Entity mercenaryE = dc.getDungeon(dungeonId).getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);

                Player player = (Player) PlayerE.get(0);
                DoBattle mercenary = (DoBattle) mercenaryE;
                // set mercenary HP to something crazy high to ensure player will lose
                mercenary.setHP(100000);

                // simulate a battle
                Entity losingEntity = Battle.startBattle(player, (DoBattle) mercenary);

                // assert Player no longer an entity in Dungeon because HP of player has reached
                // 0, meaning frontend will consider Game Lost
                assertTrue(losingEntity instanceof Player);
                assertTrue(player.getHP() == 0);

        }

        @Test
        public void testPlayerDies30MercenarySpawn() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("hydraAssassinAndurilSword", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                for (int i = 0; currDungeon.isPlayerRemovedGameLost() == false && i < 604; i++) {
                }
                {
                        dc.tick(null, Direction.NONE);
                }

                assertEquals(TestHelpers.getEntityList(currDungeon, "player").size(), 0);
        }

}
