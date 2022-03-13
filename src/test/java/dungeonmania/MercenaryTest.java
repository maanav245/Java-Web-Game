package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.lang.Math;
import java.util.List;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Movement.MoveTowards;
import dungeonmania.models.Movement.MoveWith;
import dungeonmania.models.MovingEntities.Character;

public class MercenaryTest {

    // TODO: For some reason they get instant killed?
    @Test
    public void mercenaryApproachPlayer() {
        // Initialise game (assumes spider spawns after first tick)
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse response = dc.newGame("treasureExitMercenary", "standard");
        dc.saveGame(response.getDungeonId());
        response = dc.tick(null, Direction.NONE);
        // Find zombie initial position
        Position mercSpawnPos = TestHelpers.getEntityResponseList(response, "mercenary").get(0).getPosition();
        // Test movement ? (Assume random movement doesn't return to spawn point)
        dc.saveGame(response.getDungeonId());
        response = dc.tick(null, Direction.NONE);
        assertFalse(TestHelpers.getEntityResponseList(response, "mercenary").get(0).getPosition().equals(mercSpawnPos));
        // Get new position of zombie and player
        Position mercPos = TestHelpers.getEntityResponseList(response, "mercenary").get(0).getPosition();
        Position playerPos = response.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0).getPosition();
        // Calculate initial distance
        double initialDistance = Math.sqrt(
                (Math.pow(playerPos.getX() - mercPos.getX(), 2) + (Math.pow(playerPos.getY() - mercPos.getY(), 2))));
        response = dc.tick(null, Direction.NONE);
        // Calculate new distance
        mercPos = TestHelpers.getEntityResponseList(response, "mercenary").get(0).getPosition();
        playerPos = response.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0).getPosition();
        double newDistance = Math.sqrt(
                (Math.pow(playerPos.getX() - mercPos.getX(), 2) + (Math.pow(playerPos.getY() - mercPos.getY(), 2))));
        // Test if distance is greater
        dc.saveGame(response.getDungeonId());
        assertTrue(newDistance < initialDistance);
    }

    // TODO: Write assumption that one gold coin is enough to bribe (for standard
    // difficulty)
    @Test
    public void mercenaryBecomesAlly() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("treasureExitMercenary", "standard");
        String mercID = TestHelpers.getEntityResponseList(dr1, "mercenary").get(0).getId();
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "treasure").size() == 1);
        // Get mercid
        String mercenaryID = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
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
        // Check if mercenary is still on the map
        dr = dc.saveGame(dr.getDungeonId());
        String MercenaryID = TestHelpers.getEntityResponseList(dr1, "mercenary").get(0).getId();
        assertTrue(TestHelpers.getEntityResponseList(dr, "mercenary").stream().anyMatch(e -> e.getId().equals(mercID)));
        // Check if they cannot be interacted with anymore
        assertFalse(TestHelpers.getEntityResponseList(dr, "mercenary").get(0).isInteractable());
        // Check ally integratoin
        Character mercenary = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(MercenaryID);
        assertFalse(mercenary.isBattleEnabled());
        List<Entity> allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertTrue(allies.contains(mercenary));
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
        mercenary = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(MercenaryID);
        assertFalse(mercenary.isBattleEnabled());
        allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertTrue(allies.contains(mercenary));
    }

    @Test
    public void mercenaryBecomesAllyWithSunstone() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("sunstoneExitMercenary", "standard");
        String mercID = TestHelpers.getEntityResponseList(dr1, "mercenary").get(0).getId();
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "treasure").size() == 0);
        // Get mercid
        String mercenaryID = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
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
        // Check if mercenary is still on the map
        dr = dc.saveGame(dr.getDungeonId());
        assertTrue(TestHelpers.getEntityResponseList(dr, "mercenary").stream().anyMatch(e -> e.getId().equals(mercID)));
        // Check if they cannot be interacted with anymore
        assertFalse(TestHelpers.getEntityResponseList(dr, "mercenary").get(0).isInteractable());
    }

    @Test
    public void MercenaryBecomesAllySceptreTenTicks() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("sceptreExitMercenary", "standard");
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "sceptre").size() == 1);
        // Get mercid
        String MercenaryID = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
        // Make into ally
        dr = dc.interact(MercenaryID);
        // See if coin is used up
        assertTrue(TestHelpers.getInventoryResponseList(dr, "sceptre").size() == 1);
        assertFalse(TestHelpers.getEntityResponseList(dr, "mercenary").get(0).isInteractable());
        // Go in circle
        int i = 0;
        while (i < 10) {
            dr = dc.tick(null, Direction.LEFT);
            i += 1;
        }
        // Check save state before 11th tick
        dr = dc.saveGame(dr.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }
        dr = dc.loadGame(dr.getDungeonId());
        // Check mercenaries isInteractable
        assertFalse(TestHelpers.getEntityResponseList(dr, "mercenary").get(0).isInteractable());
        // Check if they are still an ally anymore
        Character mercenary = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(MercenaryID);
        assertFalse(mercenary.isBattleEnabled());
        List<Entity> allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertTrue(allies.contains(mercenary));
        assertTrue(mercenary.getStrategy() instanceof MoveWith);
        // Check if mercenary isnt ally anymore on the 11th tick
        dr = dc.tick(null, Direction.LEFT);
        mercenary = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(MercenaryID);
        assertTrue(mercenary.isBattleEnabled());
        allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertFalse(allies.contains(mercenary));
        assertTrue(mercenary.getStrategy() instanceof MoveTowards);
        // Check if player still exists (health unaffected)
        assertTrue(TestHelpers.getEntityResponseList(dr, "player").size() == 1);
    }

    @Test
    public void MercenaryBecomesAllySceptreOneEnemy() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("sceptreExitOneMercenary", "standard");
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect septre
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.NONE);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "sceptre").size() == 1);
        // Get mercid
        String MercenaryID1 = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
        String MercenaryID2 = TestHelpers.getEntityResponseList(dr, "mercenary").get(1).getId();
        // Make into ally
        dr = dc.interact(MercenaryID1);
        dr = dc.interact(MercenaryID2);
        // Check if merc 1 is an ally
        Character mercenary = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(MercenaryID1);
        assertFalse(mercenary.isBattleEnabled());
        List<Entity> allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertTrue(allies.contains(mercenary));
        assertTrue(mercenary.getStrategy() instanceof MoveWith);
        // Check if mercenary isnt ally anymore.
        mercenary = (Character) dc.getDungeon(dr.getDungeonId()).getEntity(MercenaryID2);
        assertTrue(mercenary.isBattleEnabled());
        allies = dc.getDungeon(dr.getDungeonId()).getPlayer().getAllies();
        assertFalse(allies.contains(mercenary));
        assertTrue(mercenary.getStrategy() instanceof MoveTowards);
        // Check if player still exists (health unaffected)
        assertTrue(TestHelpers.getEntityResponseList(dr, "player").size() == 1);
    }

    @Test
    public void MercenaryBecomesAllyOneringError() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("oneringExitMercenary", "standard");
        String mercID = TestHelpers.getEntityResponseList(dr1, "mercenary").get(0).getId();
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "one_ring").size() == 1);
        // Get mercid
        String MercenaryID = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
        // Make into ally
        assertThrows(InvalidActionException.class, () -> dc.interact(MercenaryID));
    }

    @Test
    public void notCardinallyCloseToMercenary() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("treasureExitMercenary", "standard");
        String mercenaryId = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
        assertThrows(InvalidActionException.class, () -> dc.interact(mercenaryId));
    }

    @Test
    public void noTreasureMercenary() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("treasureExitMercenary", "standard");
        String mercenaryId = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
        dr = dc.tick(null, Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dc.interact(mercenaryId));
    }

    @Test
    public void mercenaryBecomesAllyAfterSave() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr1 = dc.newGame("treasureExitMercenary", "standard");
        String mercID = TestHelpers.getEntityResponseList(dr1, "mercenary").get(0).getId();
        Dungeon dungeon = dc.getDungeon(dr1.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        // Collect coin
        DungeonResponse dr = dc.tick(null, Direction.RIGHT);
        dr = dc.tick(null, Direction.LEFT);
        // Check inventory for coin
        assertTrue(TestHelpers.getInventoryResponseList(dr, "treasure").size() == 1);
        // Get mercid
        String mercenaryID = TestHelpers.getEntityResponseList(dr, "mercenary").get(0).getId();
        // Make into ally
        dr = dc.interact(mercenaryID);
        // Save game
        dc.saveGame(dr.getDungeonName());
        // Start new game
        dr = dc.loadGame(dr.getDungeonName());
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
        // Check if mercenary is still on the map
        dr = dc.saveGame(dr.getDungeonId());
        assertTrue(TestHelpers.getEntityResponseList(dr, "mercenary").stream().anyMatch(e -> e.getId().equals(mercID)));
        // Check if they cannot be interacted with anymore
        assertFalse(TestHelpers.getEntityResponseList(dr, "mercenary").get(0).isInteractable());
    }

    @Test
    public void testBoulderMovement() {
        // Start game
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("boulders", "standard");
        // Move player
        DungeonResponse response = dc.tick(null, Direction.UP);
        response = dc.saveGame(response.getDungeonId());
        response = dc.tick(null, Direction.RIGHT);
        response = dc.tick(null, Direction.UP);
        response = dc.tick(null, Direction.RIGHT);
        response = dc.tick(null, Direction.RIGHT);
        response = dc.tick(null, Direction.DOWN);
        response = dc.tick(null, Direction.LEFT);
        while (!dc.getDungeon(response.getDungeonId()).getEntities().stream().anyMatch(e -> e.getType().equals("mercenary"))) {
            response = dc.tick(null, Direction.NONE);
        }
        // Test position isnt changed with boulder blocking it
        String MercenaryID = TestHelpers.getEntityResponseList(response, "mercenary").get(0).getId();
        Position mercInitial = dc.getDungeon(response.getDungeonId()).getEntity(MercenaryID).getPosition();
        dc.tick(null, Direction.NONE);
        Position mercPost = dc.getDungeon(response.getDungeonId()).getEntity(MercenaryID).getPosition();
        assertTrue(mercInitial.equals(mercPost));
    }

}