package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.List;
import java.lang.Math;


import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.MovingEntities.Character;

public class AssassinTest {

    // TODO: For some reason they get instant killed?
    @Test
    public void AssassinApproachPlayer() {
        // Initialise game (assumes spider spawns after first tick)
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse response = dc.newGame("treasureExitAssassin", "standard");
        dc.saveGame(response.getDungeonId());
        response = dc.tick(null, Direction.NONE);
        // Find zombie initial position
        Position mercSpawnPos = TestHelpers.getEntityResponseList(response, "assassin").get(0).getPosition();
        // Test movement ? (Assume random movement doesn't return to spawn point)
        dc.saveGame(response.getDungeonId());
        response = dc.tick(null, Direction.NONE);
        assertFalse(TestHelpers.getEntityResponseList(response, "assassin").get(0).getPosition().equals(mercSpawnPos));
        // Get new position of zombie and player
        Position assassinPos = TestHelpers.getEntityResponseList(response, "assassin").get(0).getPosition();
        Position playerPos = response.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0).getPosition();
        // Calculate initial distance
        double initialDistance = Math.sqrt((Math.pow(playerPos.getX() - assassinPos.getX(), 2)
                + (Math.pow(playerPos.getY() - assassinPos.getY(), 2))));
        response = dc.tick(null, Direction.NONE);
        // Calculate new distance
        response = dc.saveGame(response.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        response = dc.loadGame(response.getDungeonId());
        assassinPos = TestHelpers.getEntityResponseList(response, "assassin").get(0).getPosition();
        playerPos = response.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0).getPosition();
        double newDistance = Math.sqrt((Math.pow(playerPos.getX() - assassinPos.getX(), 2)
                + (Math.pow(playerPos.getY() - assassinPos.getY(), 2))));
        // Test if distance is greater

        assertTrue(newDistance < initialDistance);
    }

    // TODO: Write assumption that one gold coin is enough to bribe (for standard
    // difficulty)
    @Test
    public void AssassinBecomesAllyTreasure() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("treasureExitAssassin", "standard");
        String mercID = TestHelpers.getEntityResponseList(dr1, "assassin").get(0).getId();
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);

        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "treasure").size() == 1);
        // Get mercid
        String mercenaryID = TestHelpers.getEntityResponseList(dr, "assassin").get(0).getId();
        // Make into ally
        dr = dc.interact(mercenaryID);
        // See if coin is used up
        assertTrue(TestHelpers.getInventoryResponseList(dr, "treasure").size() == 0);
        // Go in circle
        dr = dc.tick(null, Direction.LEFT);
        dr = dc.tick(null, Direction.DOWN);
        dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.UP);
        dr = dc.tick(null, Direction.LEFT);
        // Check if player still exists (health unaffected)
        assertTrue(TestHelpers.getEntityResponseList(dr, "player").size() == 1);
        // Check if assassin is still on the map
        dr = dc.saveGame(dr.getDungeonId());
        String AssassinID = TestHelpers.getEntityResponseList(dr1, "assassin").get(0).getId();
        assertTrue(TestHelpers.getEntityResponseList(dr, "assassin").stream().anyMatch(e -> e.getId().equals(mercID)));
        // Check if they cannot be interacted with anymore
        assertFalse(TestHelpers.getEntityResponseList(dr, "assassin").get(0).isInteractable());
        // Check ally integratoin
        Character assassin = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(AssassinID);
        assertFalse(assassin.isBattleEnabled());
        List<Entity> allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertTrue(allies.contains(assassin));
        // Check after savegame
        // Check after save game
        dr = dc.saveGame(dr.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }
        dr = dc.loadGame(dr.getDungeonId());
        assassin = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(AssassinID);
        assertFalse(assassin.isInteractable());
        assertFalse(assassin.isBattleEnabled());
        allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertTrue(allies.contains(assassin));
    }

    @Test
    public void AssassinBecomesAllyOnering() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("oneringExitAssassin", "standard");
        String mercID = TestHelpers.getEntityResponseList(dr1, "assassin").get(0).getId();
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);

        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "one_ring").size() == 1);
        // Get mercid
        String AssassinID = TestHelpers.getEntityResponseList(dr, "assassin").get(0).getId();
        // Make into ally
        dr = dc.interact(AssassinID);
        // See if coin is used up
        assertTrue(TestHelpers.getInventoryResponseList(dr, "one_ring").size() == 0);
        // Go in circle
        dr = dc.tick(null, Direction.LEFT);
        dr = dc.tick(null, Direction.DOWN);
        dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.UP);
        dr = dc.tick(null, Direction.LEFT);
        // Check if player still exists (health unaffected)
        assertTrue(TestHelpers.getEntityResponseList(dr, "player").size() == 1);
        // Check if Assassin is still on the map
        dr = dc.saveGame(dr.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }
        dr = dc.loadGame(dr.getDungeonId());
        Character assassin = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(AssassinID);
        assertFalse(assassin.isBattleEnabled());
        List<Entity> allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertTrue(allies.contains(assassin));
    }

    @Test
    public void AssassinBecomesAllySunstone() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("sceptreExitAssassin", "standard");
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "sceptre").size() == 1);
        // Get mercid
        String AssassinID = TestHelpers.getEntityResponseList(dr, "assassin").get(0).getId();
        // Make into ally
        dr = dc.interact(AssassinID);
        // See if coin is used up
        assertTrue(TestHelpers.getInventoryResponseList(dr, "sceptre").size() == 1);
        assertFalse(TestHelpers.getEntityResponseList(dr, "assassin").get(0).isInteractable());
        // Go in circle
        int i = 0;
        while (i < 10) {
            dr = dc.tick(null, Direction.LEFT);
            i += 1;
        }
        // Check mercenaries isInteractable
        assertFalse(TestHelpers.getEntityResponseList(dr, "assassin").get(0).isInteractable());
        // Check if they are not ally anymore
        dr = dc.tick(null, Direction.LEFT);
        Character assassin = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(AssassinID);
        assertTrue(assassin.isBattleEnabled());
        List<Entity> allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertFalse(allies.contains(assassin));
        // Check if player still exists (health unaffected)
        // Check after save game
        dr = dc.saveGame(dr.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }
        dr = dc.loadGame(dr.getDungeonId());
        assassin = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(AssassinID);
        assertTrue(assassin.isBattleEnabled());
        allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertFalse(allies.contains(assassin));
        // Check if player still exists (health unaffected)
        assertTrue(TestHelpers.getEntityResponseList(dr, "player").size() == 1);
    }

    @Test
    public void AssassinBecomesAllySceptreTenTicks() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("sceptreExitAssassin", "standard");
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "sceptre").size() == 1);
        // Get mercid
        String assassinID = TestHelpers.getEntityResponseList(dr, "assassin").get(0).getId();
        // Make into ally
        dr = dc.interact(assassinID);
        // See if coin is used up
        assertTrue(TestHelpers.getInventoryResponseList(dr, "sceptre").size() == 1);
        assertFalse(TestHelpers.getEntityResponseList(dr, "assassin").get(0).isInteractable());
        // Go in circle
        int i = 0;
        while (i < 10) {
            dr = dc.tick(null, Direction.LEFT);
            i += 1;
        }
        // Check mercenaries isInteractable
        assertFalse(TestHelpers.getEntityResponseList(dr, "assassin").get(0).isInteractable());
        // Check if they are not ally anymore
        dr = dc.tick(null, Direction.LEFT);
        Character assassin = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(assassinID);
        assertTrue(assassin.isBattleEnabled());
        List<Entity> allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertFalse(allies.contains(assassin));
    }

    @Test
    public void notCardinallyCloseToAssassin() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("treasureExitAssassin", "standard");
        String AssassinId = TestHelpers.getEntityResponseList(dr, "assassin").get(0).getId();
        assertThrows(InvalidActionException.class, () -> dc.interact(AssassinId));
    }

    @Test
    public void noTreasureAndOneringAssassin() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("treasureExitAssassin", "standard");
        String AssassinId = TestHelpers.getEntityResponseList(dr, "assassin").get(0).getId();
        dr = dc.tick(null, Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dc.interact(AssassinId));
    }

    // TODO: Potion has Ended Mechanics

}