package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MVPv1GoalComponentTests {
        // base test working with single leaf 'exit' goal from MVPv1Tests
        @Test
        // Ends game on entering exit
        public void goThroughExit() throws InterruptedException {
                // controller, new game
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("3x3maze", "peaceful");

                // check that the goal is exit only
                assertTrue(exitgame.getGoals().equals(":exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // player moves DOWN, RIGHT, to arrive at exit
                dc.tick(null, Direction.DOWN);

                // get player and move towards exit
                entities = dc.tick(null, Direction.RIGHT).getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());
                assertTrue(entities.size() == 1);
                EntityResponse player = entities.get(0);

                // check player is in same position as exit
                assertEquals(exit.getPosition(), player.getPosition());

                // check if game's goal conditions have been completed
                assertTrue(dc.isDungeonComplete(exitgame.getDungeonId()));
        }

        @Test
        // added "treasure" leaf goal as well as "exit"
        // player must go "RIGHT", then "DOWN" to fulfill both conditions
        public void testAndTreasureGoThroughExitNotComplete() throws InterruptedException {
                // controller, new game
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("treasureExit", "standard");
                Thread.sleep(500);

                // check that the goal is NOT exit only
                // System.out.println(exitgame.getGoals());
                assertFalse(exitgame.getGoals().equals(":exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // player moves DOWN, RIGHT, to arrive at exit
                dc.tick(null, Direction.DOWN);

                // get player and move towards exit
                entities = dc.tick(null, Direction.RIGHT).getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());
                assertTrue(entities.size() == 1);
                EntityResponse player = entities.get(0);

                // check player is in same position as exit
                assertEquals(exit.getPosition(), player.getPosition());

                // check if game's goal conditions have been completed
                assertFalse(dc.isDungeonComplete(exitgame.getDungeonId()));
        }

        @Test
        // added "treasure" leaf goal as well as "exit"
        // player must go "RIGHT", then "DOWN" to fulfill both conditions
        public void testAndTreasureGoThroughExitComplete() throws InterruptedException {
                // controller, new game
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("treasureExit", "standard");
                Thread.sleep(500);

                // check that the goal is NOT exit only
                // System.out.println(exitgame.getGoals());
                assertFalse(exitgame.getGoals().equals(":exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // player moves RIGHT, DOWN, to arrive at exit
                dc.tick(null, Direction.RIGHT);

                // get player and move towards exit
                entities = dc.tick(null, Direction.DOWN).getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());
                assertTrue(entities.size() == 1);
                EntityResponse player = entities.get(0);

                // check player is in same position as exit
                assertEquals(exit.getPosition(), player.getPosition());

                // check if game's goal conditions have been completed
                assertTrue(dc.isDungeonComplete(exitgame.getDungeonId()));
        }

        @Test
        // added "boulder" leaf goal as well as "exit"
        // player must go "RIGHT", then "DOWN" to fulfill both conditions
        public void testAndBouldersGoThroughExitComplete() throws InterruptedException {
                // controller, new game
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("bouldersExit", "standard");
                Thread.sleep(500);

                // check that the goal is NOT exit only
                // System.out.println(exitgame.getGoals());
                assertFalse(exitgame.getGoals().equals(":exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // player moves RIGHT to push the boulder onto the floor switch, and then DOWN,
                // to arrive at exit
                dc.tick(null, Direction.RIGHT);

                // get player and move towards exit
                entities = dc.tick(null, Direction.DOWN).getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());
                assertTrue(entities.size() == 1);
                EntityResponse player = entities.get(0);

                // check player is in same position as exit
                assertEquals(exit.getPosition(), player.getPosition());

                // check if game's goal conditions have been completed
                assertTrue(dc.isDungeonComplete(exitgame.getDungeonId()));
        }

        @Test
        // added "enemy" leaf goal as well as "exit"
        // player must go "DOWN", then "RIGHT" to fulfil exit, but enemies not all dealt
        // will so game will not complete
        public void testAndEnemiesGoThroughExitNotComplete() throws InterruptedException {
                // controller, new game
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                Dungeon dungeon = dc.getDungeon(exitgame.getDungeonId());
                dungeon.getGameModeFactory().setMaxSpiders(1);

                // check that the goal is NOT exit only
                // System.out.println(exitgame.getGoals());
                assertFalse(exitgame.getGoals().equals("exit"));

                // checks there is an entity of type "exit" in expected position, x=1, y=2
                List<EntityResponse> entities = exitgame.getEntities().stream()
                                .filter(entity -> entity.getType().equals("exit")).collect(Collectors.toList());
                assertEquals(entities.size(), 1);
                EntityResponse exit = entities.get(0);
                assertEquals(exit.getPosition(), new Position(1, 2));

                // player moves DOWN, then RIGHT, to arrive at exit
                // but mercenrary was on the RIGHT of player not killed yet
                dc.tick(null, Direction.DOWN);

                // get player and move towards exit
                entities = dc.tick(null, Direction.RIGHT).getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());
                assertTrue(entities.size() == 1);
                EntityResponse player = entities.get(0);

                // check player is in same position as exit
                assertEquals(exit.getPosition(), player.getPosition());

                // check if game's goal conditions have been completed
                assertFalse(dc.isDungeonComplete(exitgame.getDungeonId()));
        }
}
