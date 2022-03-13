package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Player;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.exceptions.InvalidActionException;

public class MVPv1Tests {

    @Test
    public void testDungeons() {
        assertTrue(DungeonManiaController.dungeons().size() > 0);
        assertTrue(DungeonManiaController.dungeons().contains("maze"));
        System.out.println(DungeonManiaController.dungeons());
    }

    @Test
    // Start new game
    public void testNewGame() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon1 = dc.newGame("3x3maze", "standard");

        assertTrue(dungeon1.getDungeonId() != null);
        assertTrue(!dungeon1.getGoals().isEmpty());
        assertTrue(!dungeon1.getEntities().isEmpty());
    }

    @Test
    // checks new game exceptions
    public void testNewGameException() {
        DungeonManiaController dc = new DungeonManiaController();

        // not a difficulity level
        assertThrows(IllegalArgumentException.class, () -> {
            dc.newGame("3x3maze", "notADifficulity");
        });
        // not a dungeon
        assertThrows(IllegalArgumentException.class, () -> {
            dc.newGame("Dungeon", "standard");
        });
    }

    @Test
    // LoadGame(load game file name)
    public void testLoadSimpleGame() throws InterruptedException {
        // checks everything is where they should be
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon1 = dc.newGame("3x3maze", "standard");
        dc.saveGame(dungeon1.getDungeonId());
        // Thread.sleep(1000);
        DungeonResponse wallgame = dc.loadGame(dungeon1.getDungeonId());
        dc.saveGame(wallgame.getDungeonId());

        assertEquals(dungeon1.getGoals(), wallgame.getGoals());
        assertEquals(dungeon1.getDungeonName(), wallgame.getDungeonName());
        assertEquals(dungeon1.getDungeonId(), wallgame.getDungeonId());

        List<String> d1IDs = new ArrayList<>();
        List<String> wallIDs = new ArrayList<>();
        dungeon1.getEntities().forEach(e -> d1IDs.add(e.getId()));
        wallgame.getEntities().forEach(e -> wallIDs.add(e.getId()));
        TestHelpers.assertListAreEqualIgnoringOrder(dungeon1.getBuildables(), wallgame.getBuildables());
        TestHelpers.assertListAreEqualIgnoringOrder(d1IDs, wallIDs);
    }

    @Test
    // checks loadGame exceptions
    public void testLoadGameException() {
        DungeonManiaController dc = new DungeonManiaController();
        // not a valid id
        assertThrows(IllegalArgumentException.class, () -> {
            dc.loadGame("NotValid");
        });
    }

    @Test
    // Save Game (save game)
    public void testSaveGame() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon1 = dc.newGame("3x3maze", "standard");
        DungeonResponse wallgame = dc.saveGame(dungeon1.getDungeonId());

        assertEquals(dungeon1.getGoals(), wallgame.getGoals());
        TestHelpers.assertListAreEqualIgnoringOrder(dungeon1.getBuildables(), wallgame.getBuildables());
        assertEquals(dungeon1.getDungeonId(), wallgame.getDungeonId());
        assertEquals(dungeon1.getDungeonName(), wallgame.getDungeonName());

        List<String> d1IDs = new ArrayList<>();
        List<String> wallIDs = new ArrayList<>();
        dungeon1.getEntities().forEach(e -> d1IDs.add(e.getId()));
        wallgame.getEntities().forEach(e -> wallIDs.add(e.getId()));
        TestHelpers.assertListAreEqualIgnoringOrder(d1IDs, wallIDs);
    }

    @Test
    // Player spawns in dungeon
    public void playerSpawns() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon1 = dc.newGame("3x3maze", "standard");

        // check that when a new game is created a player is spawned and at the (0,0)
        // position
        List<EntityResponse> players = dungeon1.getEntities().stream()
                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList());

        // check there is only one player and they exist
        assertEquals(1, players.size());

        // TODO: Check this out
        EntityResponse player = players.get(0);
        EntityResponse expectedPlayer = new Player("character", new Position(0, 1)).createEntityResponse();
        assertEquals(player.getPosition(), expectedPlayer.getPosition());
    }

    @Test
    // player can move around
    public void playerCanMove() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("3x3maze", "standard");

        // Test that when moved UP position updates by (0, -1)
        DungeonResponse dr1 = dc.tick(null, Direction.DOWN);
        List<EntityResponse> entities = dr1.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList());
        EntityResponse player = entities.get(0);
        assertEquals(player.getPosition(), new Position(0, 2));

    }

    @Test
    // there is a wall, player cannot move through this wall, using custom 3x3maze
    // u
    public void playerCannotPassThroughWall() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("3x3maze", "standard");

        // Player is at (0, 1) and at (1, 1) is a wall
        DungeonResponse dr1 = dc.tick(null, Direction.RIGHT);
        List<EntityResponse> entities = dr1.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList());
        EntityResponse player = entities.get(0);
        assertEquals(player.getPosition(), new Position(0, 1));
    }

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
    public void playerCanPickUpItem() {
        // Check if item spawned
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("treasure", "standard");
        // Move player
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr = dc.tick(null, Direction.NONE);
        // Use sword to see if it entered the inventory
        assertTrue(dr.getInventory().get(0).getType().equals("treasure"));
    }

    @Test
    public void playerMovesWithInventoryItem() {
        // check the position of the item that has been picked up Position moves when
        // the player moves
        // Check if item spawned
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("treasure", "standard");
        // Move player
        dc.tick(null, Direction.RIGHT);
        DungeonResponse dr = dc.tick(null, Direction.LEFT);
        // Assume item is not detected in map
        List<EntityResponse> er = dr.getEntities();
        assertFalse(er.stream().anyMatch(item -> item.getType().equals("treasure")));
    }

}