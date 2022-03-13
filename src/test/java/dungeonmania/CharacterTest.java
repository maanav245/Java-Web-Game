package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.Modes.GameMode;
import dungeonmania.models.Modes.GameModeFactory;
import dungeonmania.models.MovingEntities.*;
import dungeonmania.models.MovingEntities.Character;
import dungeonmania.models.StaticEntities.*;

public class CharacterTest {

    /*
     * test that a small number of zombies spawn with armour
     */
    @Test
    public void testZombieSpawnArmour() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("enemiesBasic", "standard").getDungeonId();
        Dungeon dr = dc.getDungeon(id);
        GameModeFactory gameModeF = dr.getGameModeFactory();
        GameMode gameMode = (GameMode) gameModeF;

        Zombie z1 = new StandardZombie(UUID.randomUUID().toString(), new Position(0, 1));
        z1.spawnWithArmour(dr.getGameModeFactory().getArmourSeed());
        dr.addEntity(z1);
        assertEquals(0, z1.getInventory().getCount("armour"));

        Zombie z2 = new StandardZombie(UUID.randomUUID().toString(), new Position(0, 1));
        z2.spawnWithArmour(dr.getGameModeFactory().getArmourSeed());
        dr.addEntity(z2);
        assertEquals(1, z2.getInventory().getCount("armour"));
    }

    /*
     * test that some mercenaries spawn with armour
     */
    @Test
    public void testMercenariesSpawnArmour() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("enemiesBasic", "standard").getDungeonId();
        Dungeon dr = dc.getDungeon(id);

        Mercenary m1 = new StandardMercenary(UUID.randomUUID().toString(), new Position(0, 1));
        m1.spawnWithArmour(dr.getGameModeFactory().getArmourSeed());
        dr.addEntity(m1);
        assertEquals(1, m1.getInventory().getCount("armour"));

        Mercenary m2 = new StandardMercenary(UUID.randomUUID().toString(), new Position(0, 1));
        m2.spawnWithArmour(dr.getGameModeFactory().getArmourSeed());
        dr.addEntity(m2);
        assertEquals(1, m2.getInventory().getCount("armour"));
    }

    /*
     * test that rarely characters spawn with the onering
     */

    @Test
    public void testOneRingSpawn() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("enemiesBasic", "standard").getDungeonId();
        Dungeon dr = dc.getDungeon(id);

        Mercenary m1 = new StandardMercenary(UUID.randomUUID().toString(), new Position(0, 1));
        m1.spawnWithRareObject(dr.getGameModeFactory().getRareObjectSeed());
        dr.addEntity(m1);
        assertEquals(0, m1.getInventory().getCount("one_ring"));

        Zombie z1 = new StandardZombie(UUID.randomUUID().toString(), new Position(0, 1));
        z1.spawnWithRareObject(dr.getGameModeFactory().getRareObjectSeed());
        dr.addEntity(z1);
        assertEquals(0, z1.getInventory().getCount("one_ring"));

        Spider s1 = new StandardSpider(UUID.randomUUID().toString(), new Position(0, 1));
        s1.spawnWithRareObject(dr.getGameModeFactory().getRareObjectSeed());
        dr.addEntity(s1);
        assertEquals(1, s1.getInventory().getCount("one_ring"));
    }

    /*
     * test that when a standard game is started the enimies have standard stats
     */

    @Test
    public void testCharacterStandardStats() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("enemiesBasic", "standard").getDungeonId();
        dc.tick(null, Direction.DOWN);

        Dungeon dr1 = dc.getDungeon(id);
        List<Entity> zombies = TestHelpers.getEntityList(dr1, "zombie_toast");
        Zombie z = (Zombie) zombies.get(0);
        assertEquals(10, z.getHP());
        assertEquals(5, z.getAP());

        List<Entity> mercenaries = TestHelpers.getEntityList(dr1, "mercenary");
        Mercenary m = (Mercenary) mercenaries.get(0);
        assertEquals(30, m.getHP());
        assertEquals(10, m.getAP());

        List<Entity> spiders = TestHelpers.getEntityList(dr1, "spider");
        Spider s = (Spider) spiders.get(0);
        assertEquals(5, s.getHP());
        assertEquals(5, s.getAP());
    }

    /*
     * test that a zombie spawns every 10 tics out of a zombie toaster spawner and
     * is destroyed if the player has a sword
     */
    @Test
    public void testToasterSpawnsZombies() {
        DungeonManiaController dc = new DungeonManiaController();
        String dungeonId = dc.newGame("enemiesBasic", "standard").getDungeonId();
        Dungeon dungeon = dc.getDungeon(dungeonId);
        Player player = dungeon.getPlayer();
        player.setHP(8000);

        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        DungeonResponse dr2 = dc.tick(null, Direction.NONE);
        assertEquals("sword", dr2.getInventory().get(0).getType());
        String swordId = dr2.getInventory().get(0).getId();
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        for (int i = 0; i < 40; i++) {
            dc.tick(null, Direction.NONE);
        }
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        List<EntityResponse> zombies = TestHelpers.getEntityResponseList(dr3, "zombie_toast");
        // check that there is now more than 1 zombie
        assertTrue(zombies.size() > 1);

    }

    /*
     * test that a zombie spawns every 10 tics out of a zombie toaster spawner and
     * is destroyed if the player has a sword
     */
    @Test
    public void testToasterSpawnsZombiesHardMode() {
        DungeonManiaController dc = new DungeonManiaController();
        String dungeonId = dc.newGame("enemiesBasic", "hard").getDungeonId();
        Dungeon dungeon = dc.getDungeon(dungeonId);
        Player player = dungeon.getPlayer();
        player.setHP(9000);

        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        DungeonResponse dr2 = dc.tick(null, Direction.NONE);
        assertEquals("sword", dr2.getInventory().get(0).getType());
        String swordId = dr2.getInventory().get(0).getId();
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        for (int i = 0; i < 14; i++) {
            dc.tick(null, Direction.NONE);
        }
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        List<EntityResponse> zombies = TestHelpers.getEntityResponseList(dr3, "zombie_toast");
        // check that there is now more than 1 zombie
        assertTrue(zombies.size() > 0);

    }

    /*
     * test that a zombie spawns every 10 tics out of a zombie toaster spawner and
     * is destroyed if the player has a sword
     */
    @Test
    public void testToasterSpawnsZombiesPeacefulMode() {
        DungeonManiaController dc = new DungeonManiaController();
        String dungeonId = dc.newGame("enemiesBasic", "peaceful").getDungeonId();
        Dungeon dungeon = dc.getDungeon(dungeonId);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        DungeonResponse dr2 = dc.tick(null, Direction.NONE);
        assertEquals("sword", dr2.getInventory().get(0).getType());
        String swordId = dr2.getInventory().get(0).getId();
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        for (int i = 0; i < 20; i++) {
            dc.tick(null, Direction.NONE);
        }
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        dr3 = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dungeon.getDungeonId());

        List<EntityResponse> zombies = TestHelpers.getEntityResponseList(dr3, "zombie_toast");
        // check that there is now more than 1 zombie
        assertTrue(zombies.size() > 1);

    }

    /*
     * test that a zombie spawner can be destroyed
     */

    @Test
    public void DestroySpawnerTest() {
        DungeonManiaController dc = new DungeonManiaController();
        String dungeonId = dc.newGame("spawner", "standard").getDungeonId();
        Dungeon dungeon = dc.getDungeon(dungeonId);
        Player p = dungeon.getPlayer();
        p.setHP(9000);

        Sword s = new Sword("1", new Position(0, 0));
        p.addToInventory(s);

        DungeonResponse dr = dc.tick(null, Direction.NONE);
        EntityResponse spawner = TestHelpers.getEntityResponseList(dr, "zombie_toast_spawner").get(0);
        String spawnerId = spawner.getId();
        Entity Espawner = TestHelpers.getEntityList(dungeon, "zombie_toast_spawner").get(0);

        dc.tick(null, Direction.NONE);
        assertFalse(dungeon.getPlayer().getInventory().getWieldables().isEmpty());
        assertTrue(Espawner instanceof ZombieToasterSpawner);
        assertTrue(Position.isAdjacent(spawner.getPosition(), dungeon.getPlayer().getPosition()));

        dc.interact(spawnerId);
        DungeonResponse dr4 = dc.tick(null, Direction.NONE);
        dr4 = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr4 = dc.loadGame(dungeon.getDungeonId());
        List<EntityResponse> zombiespawner = TestHelpers.getEntityResponseList(dr4, "zombie_toast_spawner"); // check
                                                                                                             // that
        assertEquals(0, zombiespawner.size());
    }

    /*
     * test that a mercenary spawns at the entry location every 20 tics
     */
    @Test
    public void MercenarySpawnPeacefulTest() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("advanced-2", "peaceful");

        // twenty ticks to spawn the first extra mercenary/ assassin
        for (int i = 0; i < 20; i++) {
            dc.tick(null, Direction.LEFT);
        }
        DungeonResponse dr1 = dc.tick(null, Direction.LEFT);
        List<EntityResponse> mercenaries = TestHelpers.getEntityResponseList(dr1, "mercenary");
        List<EntityResponse> assassins = TestHelpers.getEntityResponseList(dr1, "assassin");

        assertEquals(mercenaries.size() + assassins.size(), 2);

        for (int i = 0; i < 10; i++) {
            dc.tick(null, Direction.LEFT);
        }
        DungeonResponse dr2 = dc.tick(null, Direction.LEFT);
        List<EntityResponse> mercenaries2 = TestHelpers.getEntityResponseList(dr2, "mercenary");
        List<EntityResponse> assassins2 = TestHelpers.getEntityResponseList(dr2, "assassin");
        assertEquals(mercenaries2.size() + assassins2.size(), 2);

        for (int i = 0; i < 10; i++) {
            dc.tick(null, Direction.LEFT);
        }
        DungeonResponse dr3 = dc.tick(null, Direction.LEFT);

        dr3 = dc.saveGame(dr3.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dr3.getDungeonId());
        List<EntityResponse> mercenaries3 = TestHelpers.getEntityResponseList(dr3, "mercenary");
        ;
        List<EntityResponse> assassins3 = TestHelpers.getEntityResponseList(dr3, "assassin");
        
        assertEquals(mercenaries3.size() + assassins3.size(), 3);
             
    }

    /*
     * test that spiders spawn at a random location on the map at the begining of
     * the game That number of spiders spawned is never greater than max
     */
    @Test
    public void SpiderSpawnTest() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("advanced-2", "standard").getDungeonId();
        Dungeon dun = dc.getDungeon(id);
        DungeonResponse dr1 = dc.tick(null, Direction.NONE);
        List<EntityResponse> spiders1 = TestHelpers.getEntityResponseList(dr1, "spider");
        Entity playerE = dun.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0);
        Player player = (Player) playerE;
        player.setHP(10000);

        assertEquals(1, spiders1.size());

        assertEquals(new Position(14, 7), spiders1.get(0).getPosition());

        for (int i = 0; i < 30; i++) {
            dc.tick(null, Direction.NONE);
        }
        List<Entity> spiders2 = TestHelpers.getEntityList(dun, "spider");
        assertEquals(new Position(6, 0), spiders2.get(1).getPosition());
        assertTrue(spiders2.size() <= 6);
    }

    /*
     * test that spiders spawn at a random location on the map at the begining of
     * the game That number of spiders spawned is never greater than max
     */
    @Test
    public void SpiderSpawnTestHardMode() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("advanced-2", "hard").getDungeonId();
        Dungeon dun = dc.getDungeon(id);
        Player player = dun.getPlayer();
        player.setHP(1000);
        DungeonResponse dr1 = dc.tick(null, Direction.NONE);
        List<EntityResponse> spiders1 = TestHelpers.getEntityResponseList(dr1, "spider");

        assertEquals(1, spiders1.size());

        assertEquals(new Position(14, 7), spiders1.get(0).getPosition());

        for (int i = 0; i < 30; i++) {
            dc.tick(null, Direction.NONE);
        }
        List<Entity> spiders2 = TestHelpers.getEntityList(dun, "spider");
        assertEquals(new Position(6, 0), spiders2.get(1).getPosition());
        assertTrue(spiders2.size() <= 6);
    }

    /*
     * test that spiders spawn at a random location on the map at the begining of
     * the game That number of spiders spawned is never greater than max
     */
    @Test
    public void SpiderSpawnTestPeacefulMode() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("advanced-2", "peaceful").getDungeonId();
        Dungeon dun = dc.getDungeon(id);
        DungeonResponse dr1 = dc.tick(null, Direction.NONE);
        List<EntityResponse> spiders1 = TestHelpers.getEntityResponseList(dr1, "spider");

        assertEquals(1, spiders1.size());

        assertEquals(new Position(14, 7), spiders1.get(0).getPosition());

        for (int i = 0; i < 30; i++) {
            dr1 = dc.tick(null, Direction.NONE);
        }
        dr1 = dc.saveGame(dun.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr1 = dc.loadGame(dun.getDungeonId());
        List<Entity> spiders2 = TestHelpers.getEntityList(dun, "spider");
        assertEquals(new Position(6, 0), spiders2.get(1).getPosition());
        assertTrue(spiders2.size() <= 6);
    }

    /**
     * Test checking if BattleEnabled set properly, to indicate correct concrete
     * classes used Standard Mode
     */
    @Test
    public void testCorrectConcreteClassesMadeForStandardGameModeBattleEnabled() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("enemiesBasic", "standard").getDungeonId();
        Dungeon dr1 = dc.getDungeon(id);
        // move left to avoid all enemies
        dc.tick(null, Direction.LEFT);
        List<Entity> spiders = TestHelpers.getEntityList(dr1, "spider");
        assertEquals(spiders.size(), 2);
        dr1 = dc.getDungeon(id);
        List<Entity> zombies = TestHelpers.getEntityList(dr1, "zombie_toast");
        List<Entity> mercenary = TestHelpers.getEntityList(dr1, "mercenary");
        assertEquals(zombies.size(), 1);
        zombies.forEach(z -> {
            Character cz = (Character) z;
            assertTrue(cz.isBattleEnabled());
        });
        spiders.forEach(z -> {
            Character cz = (Character) z;
            assertTrue(cz.isBattleEnabled());
        });
        assertEquals(mercenary.size(), 1);
        mercenary.forEach(z -> {
            Character cz = (Character) z;
            assertTrue(cz.isBattleEnabled());
        });
    }

    /**
     * Test checking if BattleEnabled set properly, to indicate correct concrete
     * classes used PeacefulMode
     */
    @Test
    public void testCorrectConcreteClassesMadeForPeacefulGameModeBattleNotEnabled() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("enemiesBasic", "peaceful").getDungeonId();
        Dungeon dr1 = dc.getDungeon(id);
        // moving right would kill 1 spider and 1 mercenary
        // but numbers of Characters exactly the same as StandardMode when triggering no
        // battles
        dc.tick(null, Direction.RIGHT);
        List<Entity> spiders = TestHelpers.getEntityList(dr1, "spider");
        assertEquals(spiders.size(), 2);
        dr1 = dc.getDungeon(id);
        List<Entity> zombies = TestHelpers.getEntityList(dr1, "zombie_toast");
        assertEquals(zombies.size(), 1);
        zombies.forEach(z -> {
            Character cz = (Character) z;
            assertFalse(cz.isBattleEnabled());
        });
        spiders.forEach(z -> {
            Character cz = (Character) z;
            assertFalse(cz.isBattleEnabled());
        });
        List<Entity> mercenary = TestHelpers.getEntityList(dr1, "mercenary");
        mercenary.forEach(z -> {
            Character cz = (Character) z;
            assertFalse(cz.isBattleEnabled());
        });
    }

    /**
     * Test checking if BattleEnabled set properly, to indicate correct concrete
     * classes used Hard Mode
     */
    @Test
    public void testCorrectConcreteClassesMadeForHardGameModeBattleEnabled() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("enemiesBasic", "hard").getDungeonId();
        Dungeon dr1 = dc.getDungeon(id);
        // move left to avoid all enemies
        dc.tick(null, Direction.LEFT);
        List<Entity> spiders = TestHelpers.getEntityList(dr1, "spider");
        assertEquals(spiders.size(), 2);
        dr1 = dc.getDungeon(id);
        List<Entity> zombies = TestHelpers.getEntityList(dr1, "zombie_toast");
        List<Entity> mercenary = TestHelpers.getEntityList(dr1, "mercenary");
        assertEquals(zombies.size(), 1);
        zombies.forEach(z -> {
            Character cz = (Character) z;
            assertTrue(cz.isBattleEnabled());
        });
        spiders.forEach(z -> {
            Character cz = (Character) z;
            assertTrue(cz.isBattleEnabled());
        });
        assertEquals(mercenary.size(), 1);
        mercenary.forEach(z -> {
            Character cz = (Character) z;
            assertTrue(cz.isBattleEnabled());
        });
    }
}
